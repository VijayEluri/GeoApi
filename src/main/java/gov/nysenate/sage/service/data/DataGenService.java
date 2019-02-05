package gov.nysenate.sage.service.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.corba.se.impl.orbutil.closure.Constant;
import gov.nysenate.sage.client.response.base.ApiError;
import gov.nysenate.sage.config.Environment;
import gov.nysenate.sage.controller.admin.DataGenController;
import gov.nysenate.sage.dao.data.SqlDataGenDao;
import gov.nysenate.sage.dao.model.assembly.SqlAssemblyDao;
import gov.nysenate.sage.dao.model.congressional.SqlCongressionalDao;
import gov.nysenate.sage.dao.model.senate.SqlSenateDao;
import gov.nysenate.sage.dao.provider.district.SqlDistrictShapefileDao;
import gov.nysenate.sage.model.district.Assembly;
import gov.nysenate.sage.model.district.Congressional;
import gov.nysenate.sage.model.geo.Geocode;
import gov.nysenate.sage.scripts.streetfinder.County;
import gov.nysenate.sage.scripts.streetfinder.TownCode;
import gov.nysenate.sage.util.AssemblyScraper;
import gov.nysenate.sage.util.CongressScraper;
import gov.nysenate.sage.util.ImageUtil;
import gov.nysenate.sage.util.controller.ConstantUtil;
import gov.nysenate.services.NYSenateClientService;
import gov.nysenate.services.NYSenateJSONClient;
import gov.nysenate.services.model.Office;
import gov.nysenate.services.model.Senator;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.io.IOUtils;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static gov.nysenate.sage.model.result.ResultStatus.*;

@Service
public class DataGenService {

    private Logger logger = LoggerFactory.getLogger(DataGenController.class);
    private SqlDistrictShapefileDao sqlDistrictShapefileDao;
    private SqlAssemblyDao sqlAssemblyDao;
    private SqlCongressionalDao sqlCongressionalDao;
    private SqlSenateDao sqlSenateDao;
    private SqlDataGenDao sqlDataGenDao;
    private Environment env;

    @Autowired
    public DataGenService(SqlSenateDao sqlSenateDao, SqlDistrictShapefileDao sqlDistrictShapefileDao,
                          SqlAssemblyDao sqlAssemblyDao, SqlCongressionalDao sqlCongressionalDao,
                          Environment env, SqlDataGenDao sqlDataGenDao) {
        this.sqlSenateDao = sqlSenateDao;
        this.sqlDistrictShapefileDao = sqlDistrictShapefileDao;
        this.sqlAssemblyDao = sqlAssemblyDao;
        this.sqlCongressionalDao = sqlCongressionalDao;
        this.sqlDataGenDao = sqlDataGenDao;
        this.env = env;
    }

    public boolean ensureTownCodeFile() {
        try {
            File towns = new File(ConstantUtil.TOWN_FILE_DIRECTORY);
            if (towns.exists()) {
                logger.info("Town code file already exists");
                return true;
            }

            towns.createNewFile();
            List<TownCode> townCodes = sqlDataGenDao.getTownCodes();

            FileWriter fileWriter = new FileWriter(ConstantUtil.TOWN_FILE_DIRECTORY);
            PrintWriter outputWriter = new PrintWriter(fileWriter);

            int count = 0;
            for (TownCode townCode : townCodes) {
                count++;
                outputWriter.println(townCode.toString());
            }

            logger.info("Wrote " + count + " town codes to file");
            fileWriter.close();
            outputWriter.close();
            return true;

        } catch (IOException ex) {
            logger.error("Error creating town code file", ex);
            return false;
        }
    }

    public boolean ensureCountyCodeFile() {
        try {
            File senateCounties = new File(ConstantUtil.COUNTY_FILE_DIRECTORY);
            if (senateCounties.exists()) {
                logger.info("Senate county code file already exists");
                return true;
            }

            senateCounties.createNewFile();
            List<County> counties = sqlDataGenDao.getCountyCodes();

            FileWriter fileWriter = new FileWriter(ConstantUtil.COUNTY_FILE_DIRECTORY);
            PrintWriter outputWriter = new PrintWriter(fileWriter);

            int count = 0;
            for (County county : counties) {
                count++;
                outputWriter.println(county.toString());
            }

            logger.info("Wrote " + count + " Senate county codes to file");
            fileWriter.close();
            outputWriter.close();
            return true;

        }  catch (IOException ex) {
            logger.error("Error creating town code file", ex);
            return false;
        }
    }

    public Object generateSenatorImages(String path, int height) {
        Object apiResponse = new ApiError(this.getClass(), API_REQUEST_INVALID );

        try {
            Collection<Senator> senators = sqlSenateDao.getSenators();
            for (Senator senator : senators) {
                String filePath =  path + senator.getShortName() + ".png";
                File file = new File(filePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                String baseImageDir = "http://www.nysenate.gov/files/profile-pictures/";
                String url = baseImageDir + senator.getImageUrl()
                        .replace(baseImageDir, "").replace(" ", "%20");
                ImageUtil.saveResizedImage(url, "png", file, height);
                apiResponse = new ApiError(this.getClass(), SUCCESS);
            }
        }
        catch (IOException e) {
            apiResponse = new ApiError(this.getClass(), INTERNAL_ERROR);
        }

        return apiResponse;
    }


    public Object generateMetaData(String option) throws IOException, XmlRpcException {
        boolean updated = false;
        boolean processAssembly = false;
        boolean processCongress = false;
        boolean processSenate = false;

        if (option.equals("all")) {
            processAssembly = true;
            processSenate = true;
            processCongress = true;
        }
        else if (option.equals("assembly") || option.equals("a")) {
            processAssembly = true;
        }
        else if (option.equals("congress") || option.equals("c")) {
            processCongress = true;
        }
        else if (option.equals("senate") || option.equals("s")) {
            processSenate = true;
        }
        else {
            logger.error(option+": Invalid option");
            return new ApiError(this.getClass(), API_REQUEST_INVALID);
        }

        if (processAssembly) {
            updated = generateAssemblyData();
        }

        if (processCongress) {
            updated = generateCongressionalData();
        }

        if (processSenate) {
            updated = generateSenateData();
        }

        if (updated) {
            return new ApiError(this.getClass(), SUCCESS);
        }
        else {
            return new ApiError(this.getClass(), INTERNAL_ERROR);
        }
    }

    /**
     * Retrieves Congressional member data from an external source and updates the
     * relevant data in the database.
     */
    private boolean generateCongressionalData() {
        boolean updated = false;

        logger.info("Indexing NY Congress by scraping its website...");

        /** Retrieve the congressional members and insert into the database */
        List<Congressional> congressionals = CongressScraper.getCongressionals();
        for (Congressional congressional : congressionals) {
            int district = congressional.getDistrict();
            Congressional existingCongressional = sqlCongressionalDao.getCongressionalByDistrict(district);

            if (existingCongressional == null) {
                updated = true;
                sqlCongressionalDao.insertCongressional(congressional);
            } else if (!isCongressionalDataUpdated(existingCongressional, congressional)) {
                updated = true;
                sqlCongressionalDao.deleteCongressional(district);
                sqlCongressionalDao.insertCongressional(congressional);
            }
        }
        return updated;
    }

    /**
     * Retrieves Assembly member data from an external source and updates the
     * relevant data in the database.
     */
    private boolean generateAssemblyData() {
        logger.info("Indexing NY Assembly by scraping its website...");
        boolean updated = false;

        /** Retrieve the assemblies and insert into the database */
        List<Assembly> assemblies = AssemblyScraper.getAssemblies();
        for (Assembly assembly : assemblies) {
            int district = assembly.getDistrict();
            Assembly existingAssembly = sqlAssemblyDao.getAssemblyByDistrict(district);

            if (existingAssembly == null) {
                updated = true;
                sqlAssemblyDao.insertAssembly(assembly);
            } else if (!isAssemblyDataUpdated(existingAssembly, assembly)) {
                updated = true;
                sqlAssemblyDao.deleteAssemblies(district);
                sqlAssemblyDao.insertAssembly(assembly);
            }
        }
        return updated;
    }

    /**
     * Retrieves senate data from the NY Senate API Client and stores it in
     * the database.
     *
     * @throws XmlRpcException
     */
    private boolean generateSenateData() throws XmlRpcException, IOException {
        boolean updated = false;

        NYSenateClientService senateClient;

        logger.info("Generating senate data from NY Senate client services");

        /** Obtain the senate client service */

        String domain = env.getNysenateDomain();
        senateClient = new NYSenateJSONClient(domain);

        /** Retrieve the list of senators from the client API */
        List<Senator> senators = senateClient.getSenators();

        for (Senator senator : senators) {
            int district = senator.getDistrict().getNumber();
            if (district > 0) {
                Senator existingSenator = sqlSenateDao.getSenatorByDistrict(district);
                for (Office office : senator.getOffices()) {
                    getUpdatedGeocode(office);
                }
                if (verifyOfficeGeocode(senator)) {
                    if (existingSenator == null) {
                        sqlSenateDao.insertSenate(senator.getDistrict());
                        sqlSenateDao.insertSenator(senator);
                    }
                    else {
                        sqlSenateDao.deleteSenator(district);
                        sqlSenateDao.insertSenator(senator);
                    }
                }
                else {
                    logger.info("Could not update Senator " + senator.getName() + " District: " + district);
                }
                updated = true;
            }
        }
        if (updated) {
            sqlDistrictShapefileDao.cacheDistrictMaps();
        }
        return updated;
    }

    private boolean isCongressionalDataUpdated(Congressional c1, Congressional c2) {
        if (c1 != null && c2 != null) {
            if (!(c1.getDistrict() == c2.getDistrict() &&
                    c1.getMemberName().equals(c2.getMemberName()) &&
                    c1.getMemberUrl().equals(c2.getMemberUrl()))) {
                logger.info("Congressional District " + c1.getDistrict() + " [" + c1.getMemberName() + "] updated");
                return true;
            }
        } else if (c1 == null && c2 != null) {
            return true;
        }
        return false;
    }

    private boolean isAssemblyDataUpdated(Assembly a1, Assembly a2) {
        if (a1 != null && a2 != null) {
            if (!(a1.getDistrict() == a2.getDistrict() &&
                    a1.getMemberName().equals(a2.getMemberName()) &&
                    a1.getMemberUrl().trim().equals(a2.getMemberUrl().trim()))) {
                logger.info("Assembly District " + a1.getDistrict() + " [" + a1.getMemberName() + "] updated");
                return true;
            }
        } else if (a1 == null && a2 != null) {
            return true;
        }
        return false;
    }

    private void getUpdatedGeocode(Office senatorOffice) {

        String urlString = env.getBaseUrl() + "/api/v2/geo/geocode?addr1=" +
                senatorOffice.getStreet() + "&city=" + senatorOffice.getCity() +
                "&state=NY&zip5=" + senatorOffice.getPostalCode();
        urlString = urlString.replaceAll(" ", "%20");
        try {
            URL url = new URL(urlString);
            InputStream is = url.openStream();
            String sageReponse = IOUtils.toString(is, "UTF-8");
            JsonNode jsonResonse = new ObjectMapper().readTree(sageReponse);
            is.close();
            Geocode geocodedOffice = new ObjectMapper().readValue(jsonResonse.get("geocode").toString(), Geocode.class);
            if (geocodedOffice != null) {
                senatorOffice.setLatitude( geocodedOffice.getLat() );
                senatorOffice.setLongitude( geocodedOffice.getLon() );
            }
            else {
                logger.error("SAGE was unable to geocode the address in the url: " + urlString);
            }

        }
        catch (IOException e) {
            logger.error("Unable to complete geocoding request to Senate Office " + senatorOffice.getStreet() +
                    ", " + senatorOffice.getCity() + ", NY " + senatorOffice.getPostalCode() + " " +e.getMessage());
        }
    }

    private boolean verifyOfficeGeocode(Senator senator) {
        List<Office> offices = senator.getOffices();

        for (Office office : offices) {
            Double latitude = office.getLatitude();
            Double longitude = office.getLongitude();

            if (latitude == null || longitude == null
                    || latitude == 0.0 || longitude == 0.0
                    || latitude.isNaN() ||  longitude.isNaN()) {
                return false;
            }
        }

        return true;
    }

}