package gov.nysenate.sage.boe.StreetFiles;

import gov.nysenate.sage.boe.BOEAddressRange;
import gov.nysenate.sage.boe.StreetFileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.commons.dbutils.QueryRunner;

public class Nassau extends StreetFileHandler {
	public int currentLine;
    public HashMap<String, String> townMap;

    public Nassau(int county, File street_file) throws Exception {
        super(county, street_file);

        townMap = new HashMap<String, String>();
        townMap.put("1", "-GLEN");
        townMap.put("2", "HEMPST");
        townMap.put("3", "-LONG");
        townMap.put("4", "N HEMP");
        townMap.put("5", "OYSTER");
    }

    @Override
    public void save(DataSource db) throws Exception {
    	logger.info("Starting Nassau");
    	currentLine = 0;
    	QueryRunner runner = new QueryRunner(db);
        BufferedReader br = new BufferedReader(new FileReader(street_file));

        String line;
        String[] parts;
        br.readLine(); // Skip the header
        runner.update("BEGIN");
        while( (line = br.readLine()) != null) {
            parts = line.replace("\"", "").split(",");

            BOEAddressRange range = new BOEAddressRange();
            range.street = (parts[3]+" "+parts[1]+" "+parts[2]).trim();
            if (!parts[1].isEmpty())
            	range.zip5 = Integer.parseInt(parts[8]);
            range.bldgLoNum = Integer.parseInt(parts[4]);
            range.bldgHiNum = Integer.parseInt(parts[5]);
            range.bldgParity = getParity(parts[6]);
            range.town = parts[7];
            if ((parts.length > 15) && (!parts[15].isEmpty()))
            	range.townCode = getTownCode(parts[15]); // TW
            if ((parts.length > 14) && (!parts[14].isEmpty()))
            	range.electionCode = Integer.parseInt(parts[14].substring(3)); 	// ED
            range.assemblyCode = Integer.parseInt(parts[11].substring(3)); 		// AD
            range.clegCode = Integer.parseInt(parts[12].substring(3));                            // LD
            range.congressionalCode = Integer.parseInt(parts[9].substring(3)); 	// CD
            range.senateCode = Integer.parseInt(parts[10].substring(3)); 		// SD
            range.countyCode = this.county_code;
            range.state = "NY";

            if ((parts.length > 17) && (!parts[17].isEmpty()))
            	range.villCode = parts[17].substring(3); 	// VI
            if ((parts.length > 16) && (!parts[16].isEmpty()))
            	range.fireCode = parts[16].substring(3); 	// FR

            save_record(range, db);

            if(++currentLine % 5000 == 0) {
            	runner.update("COMMIT");
            	runner.update("BEGIN");
            }
        }
        runner.update("COMMIT");
        br.close();
        logger.info("Done with Nassau");
    }

    public String getTownCode(String town) {
        String abbrev = town.substring(4);
	    if (townMap.containsKey(abbrev)) {
	        return townMap.get(abbrev);
	    } else {
	        logger.error("Line "+currentLine+": Town "+town+" not found in the town code map.");
	        return "";
	    }
    }

    public String getParity(String parity) {
        if(parity.equals("B"))
            return "ALL";
        else if (parity.equals("E"))
            return "EVENS";
        else if (parity.equals("O"))
            return "ODDS";
        else {
            logger.error("Line "+currentLine+": Invalid parity input "+parity);
        	return "ALL";
        }
    }
}