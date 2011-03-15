package gov.nysenate.sage.BulkProcessing;

import generated.geoserver.json.GeoResult;
import gov.nysenate.sage.connectors.BingConnect;
import gov.nysenate.sage.connectors.GeoServerConnect;
import gov.nysenate.sage.connectors.GoogleConnect;
import gov.nysenate.sage.connectors.YahooConnect;
import gov.nysenate.sage.model.Point;
import gov.nysenate.sage.model.BulkProcessing.*;
import gov.nysenate.sage.model.abstracts.AbstractGeocoder;
import gov.nysenate.sage.util.Connect;
import gov.nysenate.sage.util.DelimitedFileExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeSet;



public class Processor {
	
	public class BulkGeocoder {
		public AbstractGeocoder geocoder;
		public int max;
		public int cur;
		
		public BulkGeocoder(AbstractGeocoder geocoder, int max, int cur) {
			this.geocoder = geocoder;
			this.max = max;
			this.cur = cur;
		}
	}
	
	public class GeoFilenameFilter implements FilenameFilter {
		String filter;
		public GeoFilenameFilter(String filter) {
			this.filter = filter;
		}
		public boolean accept(File dir, String name) {
			return name.startsWith(filter);
		}
	}

	public enum Geocoder {
		Google, Yahoo, Bing;
	}
	
	/*final String ROOT_DIRECTORY = "C:\\workspace\\.metadata\\.plugins\\org.eclipse.wst.server.core\\tmp0\\wtpwebapps\\";
	/*final String ROOT_DIRECTORY = "/Library/tomcat/webapps/";*/
	final String FILE_SEPERATOR = System.getProperty("file.separator");

	final String ROOT_DIRECTORY = "/opt/apache-tomcat-6.0.26/webapps/";
	final String WORK_DIRECTORY = ROOT_DIRECTORY + "upload" + FILE_SEPERATOR;
	final String DEST_DIRECTORY = ROOT_DIRECTORY + "complete" + FILE_SEPERATOR;
	final String LOCK_FILE = ".lock";
	
	/* currently set to # of milliseconds in a day */
	final long MS_BUFFER = 86400000L;
	
	final int MAX_GOOGLE_REQUESTS = 8000;
	final int MAX_YAHOO_REQUESTS = 41000;
	final int MAX_BING_REQUESTS = 41000;
	final int MIN_REQUESTS = 100;
	
	static String ASSEMBLY = "assembly";
	static String CONGRESSIONAL = "congressional";
	static String COUNTY = "county";
	static String ELECTION = "election";
	static String SENATE = "senate";
		
	BulkRequest AVAIL_REQUESTS;
	BulkRequest CUR_REQUESTS;
	
	HashMap<String,String> countyLookupMap;
	
	public static void main(String[] args) throws IOException, SecurityException, NoSuchMethodException {
		Processor p = new Processor();
		p.processFiles();
	}
	
	
	
	public void processFiles() throws IOException {
		//create lock file to stop processes from running in parallel
		if(!createLock()) {
			System.err.println("Process already running or error creating lock file");
			System.exit(0);
		}
		
		//will store requests made during processing
		CUR_REQUESTS = new BulkRequest();
		//total requests made in previous iterations
		AVAIL_REQUESTS = initilaizeRequests();
		//NYSS uses their own county codes, not FIPS
		loadCountyLookupMap();
		
		//job processes ordered from oldest to newest
		TreeSet<JobProcess> jobProcesses = JobProcess.getJobProcesses();
		
		Connect c = new Connect();
		
		for(JobProcess jp:jobProcesses) {
			System.out.println("Current job: " + jp.getContact() + " with file: " + jp.getFileName());
			AbstractGeocoder geocoder = getGeocoder(jp);
			
			int segment = jp.getSegment();
			
			File readFile = new File(
					WORK_DIRECTORY + jp.getFileName() + (segment == -1 ? "":"-raw-" + (segment)));
			File writeFile = new File(
					WORK_DIRECTORY + jp.getFileName() + (segment == -1 ? "-work-1":"-work-" + (segment + 1)));
			System.out.println("creating new work file: " + writeFile.getAbsolutePath());
			writeFile.createNewFile();
			
			BufferedReader br = new BufferedReader(new FileReader(readFile));
			BufferedWriter bw = new BufferedWriter(new FileWriter(writeFile));
			
			String header = br.readLine();
			DelimitedFileExtractor dfe = new DelimitedFileExtractor("\t", header, Boe3rdTsv.class);
			bw.write(header + "\n");
			
			String in = null;
			while((in  = br.readLine()) != null) {
				geocoder = checkGeocoder(geocoder, jp);
				
				if(geocoder == null) {
					System.err.println("Geocoding unavailable");
					break;
				} else {
					try {
						Boe3rdTsv record = (Boe3rdTsv)dfe.processTuple(in);
						fillRequest(geocoder.doParsing(record.getAddress()), record);
						//write results
						bw.write(record + "\n");
					}
					catch (Exception e) {
						Mailer.mailError(e, jp);
						geocoder = null;
						e.printStackTrace();
						break;
					}
				}
			}
			bw.close();
			
			//if file completely processed
			if(in == null) {
				File workDir = new File(WORK_DIRECTORY);
				//merge work files in upload directory
				mergeFiles(DEST_DIRECTORY + jp.getFileName(), header,
						workDir.list(new GeoFilenameFilter(jp.getFileName() + "-work-")));
				
				Mailer.mailAdminComplete(jp);
				Mailer.mailUserComplete(jp);

				c.deleteObjectById(JobProcess.class, "filename", jp.getFileName());
				
				br.close();
				//delete associated files
				deleteFiles(WORK_DIRECTORY, workDir.list(new GeoFilenameFilter(jp.getFileName())));
			}
			//if file partially processed save work and raw files as segments
			//for later processing
			else {
				if(segment == -1)
					segment = 1;
				else segment += 1;
				
				//create raw file of unprocessed data
				File newRawFile = new File(WORK_DIRECTORY + jp.getFileName() + "-raw-" + segment);
				newRawFile.createNewFile();
				
				BufferedWriter rawBw = new BufferedWriter(new FileWriter(newRawFile));
				rawBw.write(header + "\n" + in + "\n");
				
				int lineCount = 0;
				while((in  = br.readLine()) != null) {
					lineCount++;
					rawBw.write(in + "\n");
				}
				rawBw.close();
				
				//delete and recreate process with new line count and segment #
				c.deleteObjectById(JobProcess.class, "filename", jp.getFileName());
				jp.setSegment(segment);
				jp.setLineCount(lineCount);
				c.persist(jp);
				
				br.close();
			}
			
			System.out.println("\n\n");
			if(geocoder == null) {
				break;
			}
			
		}
		//write requests made during processing
		c.persist(CUR_REQUESTS);
		
		c.close();
		
		if(!deleteLock()) {
			System.err.println("Lock file does not exist or could not be deleted");
		}
		
	}
	
	public void fillRequest(Point p, BulkInterface bi) throws IOException {
		GeoResult gr = null;
		
		GeoServerConnect gsCon = new GeoServerConnect();
		gr = gsCon.fromGeoserver(gsCon.new WFS_REQUEST(COUNTY), p);
		//converts FIPS county code from geoserver to NYSS county code
		bi.setCounty(countyLookupMap.get(
				replaceLeading(gr.getFeatures().iterator().next().getProperties().getCOUNTYFP(),"0")));
		
		gr = gsCon.fromGeoserver(gsCon.new WFS_REQUEST(ELECTION), p);
		bi.setED(gr.getFeatures().iterator().next().getProperties().getED());
		
		gr = gsCon.fromGeoserver(gsCon.new WFS_REQUEST(ASSEMBLY), p);
		bi.setAD(gr.getFeatures().iterator().next().getProperties().getNAMELSAD().replace("Assembly District ",""));
		
		gr = gsCon.fromGeoserver(gsCon.new WFS_REQUEST(CONGRESSIONAL), p);
		bi.setCD(gr.getFeatures().iterator().next().getProperties().getNAMELSAD().replace("Congressional District ", ""));
		
		gr = gsCon.fromGeoserver(gsCon.new WFS_REQUEST(SENATE), p);
		bi.setSD(gr.getFeatures().iterator().next().getProperties().getNAMELSAD().replace("State Senate District ",""));
	}
	

	
	public void mergeFiles(String destination, String header, String... files) throws IOException {
		File outFile = new File(destination);
		BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
		bw.write(header + "\n");
		
		System.out.print("Merging ");
		for(String fileName:files) {
			File workFile = new File(WORK_DIRECTORY + fileName);
			BufferedReader br = new BufferedReader(new FileReader(workFile));
			br.readLine(); //header
			
			String in = null;
			while((in = br.readLine()) != null) {
				bw.write(in + "\n");
			}
			br.close();
			
			System.out.print(workFile.getName() + ", ");
		}
		bw.close();
		System.out.println("into " + destination);
	}
	
	public void deleteFiles(String directory, String... files) {
		System.out.print("deleting ");
		for(String fileName:files) {
			File file = new File(directory + fileName);
			file.delete();
			System.out.print(file.getName() + ", ");
		}
		System.out.println();
	}
	
	public AbstractGeocoder checkGeocoder(AbstractGeocoder geocoder, JobProcess jp) {
		if(geocoder instanceof GoogleConnect) {
			if(AVAIL_REQUESTS.getGRequests() > MAX_GOOGLE_REQUESTS - MIN_REQUESTS)
				return getGeocoder(jp);
			else {
				AVAIL_REQUESTS.incrementGRequest(1);
				CUR_REQUESTS.incrementGRequest(1);
			}
		}
		else if(geocoder instanceof YahooConnect) {
			if(AVAIL_REQUESTS.getYRequests() > MAX_YAHOO_REQUESTS - MIN_REQUESTS)
				return getGeocoder(jp);
			else {
				AVAIL_REQUESTS.incrementYRequest(1);
				CUR_REQUESTS.incrementYRequest(1);
			}
		}
		else if(geocoder instanceof BingConnect) {
			if(AVAIL_REQUESTS.getBRequests() > MAX_BING_REQUESTS - MIN_REQUESTS)
				return getGeocoder(jp);
			else {
				AVAIL_REQUESTS.incrementBRequest(1);
				CUR_REQUESTS.incrementBRequest(1);
			}
		}
		return geocoder;
	}
	
	public AbstractGeocoder getGeocoder(JobProcess jp) {		
		int lc = jp.getLineCount();
		int g = MAX_GOOGLE_REQUESTS - AVAIL_REQUESTS.getGRequests();
		int b = MAX_BING_REQUESTS - AVAIL_REQUESTS.getBRequests();
		int y = MAX_YAHOO_REQUESTS - AVAIL_REQUESTS.getYRequests();
		
		int cur = 0;
		int max = cur = g;
		Geocoder gr = Geocoder.Google;

		if(b > lc && ((b - lc > 0) && (b - lc < cur - lc))) {
			cur = b;
			gr = Geocoder.Bing;
		}
		else {
			if(b > max && lc > max) {
				max = cur = b;
				gr = Geocoder.Bing;
			}
		}
		
		if(y > lc && ((y - lc > 0) && (y - lc < cur - lc))) {
			cur = y;
			gr = Geocoder.Yahoo;
		}
		else {
			if(y > max && lc > max) {
				max = cur = y;
				gr = Geocoder.Yahoo;
			}
		}
		
		if(cur > MIN_REQUESTS) {
			switch (gr) {
				case Google: 
					return new GoogleConnect();
				case Yahoo: 
					return new YahooConnect();
				case Bing: 
					return new BingConnect();
			}
		}	
		return null;
	}
	
	/**
	 * this function determines the amount of requests that can currently be made
	 * with our geocoders
	 * @return number of available requests
	 */
	public BulkRequest initilaizeRequests() {
		Connect connect = new Connect();
		
		long timeStamp = new Date().getTime() - MS_BUFFER;
		
		int gRequests = 0;
		int yRequests = 0;
		int bRequests = 0;
		
		TreeSet<BulkRequest> bulkRequests = BulkRequest.getBulkRequests();
				
		for(BulkRequest br:bulkRequests) {
			if(br.getRequestTime() < timeStamp) {
				//request is older than a day, remove from history
				connect.deleteObjectById(BulkRequest.class, "requesttime", Long.toString(br.getRequestTime()));
			}
			else {
				gRequests += br.getGRequests();
				yRequests += br.getYRequests();
				bRequests += br.getBRequests();
			}
		}
		connect.close();
		
		return new BulkRequest(gRequests, yRequests, bRequests);
	}
	
	public boolean createLock() throws IOException {
		File lock = new File(WORK_DIRECTORY + LOCK_FILE);
		if(lock.exists()) {
			return false;
		}
		return lock.createNewFile();
	}
	
	public boolean deleteLock() {
		File lock = new File(WORK_DIRECTORY + LOCK_FILE);
		if(lock.exists() && lock.delete()) {
			return true;
		}
		return false;
	}
	
	public String replaceLeading(String str, String leading) {
		if(str.startsWith(leading)) {
			return replaceLeading(str.replaceFirst(leading, ""), leading);
		}
		else {
			return str;
		}
	}
	
	public void loadCountyLookupMap() throws IOException {		
		BufferedReader br = new BufferedReader(new FileReader(new File("")));
		countyLookupMap = new HashMap<String,String>();
		
		String in = null;
		
		while((in = br.readLine()) != null) {
			//tuple[0] = NYSS county code
			//tuple[1] = county name
			//tuple[2] = fips county code
			String[] tuple = in.split(":");
			
			countyLookupMap.put(tuple[2], tuple[0]);
		}
		br.close();
	}
	
}
