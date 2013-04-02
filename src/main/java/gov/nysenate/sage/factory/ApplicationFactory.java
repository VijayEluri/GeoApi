package gov.nysenate.sage.factory;

import gov.nysenate.sage.provider.*;
import gov.nysenate.sage.listener.SageConfigurationListener;
import gov.nysenate.sage.service.base.ServiceProviders;
import gov.nysenate.sage.service.address.AddressService;
import gov.nysenate.sage.service.district.DistrictService;
import gov.nysenate.sage.service.geo.GeocodeService;
import gov.nysenate.sage.service.geo.GeocodeServiceProvider;
import gov.nysenate.sage.util.Config;
import gov.nysenate.sage.util.DB;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * ApplicationFactory is responsible for instantiating all single-instance objects that are utilized
 * across the application and providing a single access point for them. By utilizing the ApplicationFactory
 * all classes that would typically be implemented as singletons can be instantiated like regular classes
 * which allows for unit testing.
 *
 * The buildInstances method must be called once when the application is starting up. However if only
 * unit tests are to be run, the buildTestInstances method should be called instead. While these two
 * methods may setup similar dependencies, it will allow for using different configurations and
 * implementations for running unit tests.
 *
 * @author Ash
 */
public class ApplicationFactory
{
    private static final Logger logger = Logger.getLogger(ApplicationFactory.class);

    /** Static factory instance */
    private static final ApplicationFactory factoryInstance = new ApplicationFactory();

    /** Dependency instances */
    private SageConfigurationListener configurationListener;
    private Config config;
    private DB baseDB;
    private DB tigerDB;

    /** Service Providers */
    private ServiceProviders<AddressService> addressServiceProviders = new ServiceProviders<>();
    private ServiceProviders<DistrictService> districtServiceProviders = new ServiceProviders<>();
    private GeocodeServiceProvider geocodeServiceProvider = new GeocodeServiceProvider();

    /** Default values */
    private static String defaultPropertyFileName = "app.properties";
    private static String defaultTestPropertyFileName = "test.app.properties";

    /**
     * Public access call to build()
     * @return boolean - If true then build succeeded
     */
    public static boolean buildInstances()
    {
        return factoryInstance.build(defaultPropertyFileName);
    }

    /**
     * Public access call to buildTesting()
     * @return boolean - If true then build succeeded
     */
    public static boolean buildTestInstances()
    {
        return factoryInstance.build(defaultTestPropertyFileName);
    }

    /**
     * The build() method will construct all the objects and their necessary dependencies that are
     * needed in the application scope..
     *
     * @return boolean  If true then build succeeded
     */
    private boolean build(String propertyFileName)
    {
        try
        {
            /** Setup application config */
            this.configurationListener = new SageConfigurationListener();
            this.config = new Config(propertyFileName, this.configurationListener);
            this.baseDB = new DB(this.config, "db");
            this.tigerDB = new DB(this.config, "tiger.db");

            /** Setup service providers ( MOVE INTO CONFIG ) */
            addressServiceProviders.registerDefaultProvider("usps", new USPS());
            addressServiceProviders.registerProvider("mapquest", new MapQuest());

            geocodeServiceProvider.registerDefaultProvider("yahoo", new Yahoo());
            geocodeServiceProvider.registerProvider("tiger", new TigerGeocoder());
            geocodeServiceProvider.registerProvider("mapquest", new MapQuest());
            geocodeServiceProvider.registerProvider("yahooboss", new YahooBoss());
            geocodeServiceProvider.registerProvider("osm", new OSM());
            geocodeServiceProvider.registerProvider("ruby", new RubyGeocoder());

            districtServiceProviders.registerDefaultProvider("geoserver", new Geoserver());
            districtServiceProviders.registerProvider("streetfile", new StreetFile());
            districtServiceProviders.registerProvider("shapefile", new DistrictShapefile());

            return true;
        }
        catch (ConfigurationException ce)
        {
            logger.fatal("Failed to load configuration file " + defaultPropertyFileName);
            logger.fatal(ce.getMessage());
        }
        catch (Exception ex)
        {
            logger.fatal("An exception occurred while building dependencies");
            logger.fatal(ex.getMessage());
        }
        return false;
    }

    /** Config / Database Accessor */

    public static Config getConfig()
    {
        return factoryInstance.config;
    }

    public static DataSource getDataSource()
    {
        return factoryInstance.baseDB.getDataSource();
    }

    public static DataSource getTigerDataSource()
    {
        return factoryInstance.tigerDB.getDataSource();
    }

    /** Service Providers Accessor */

    public static ServiceProviders<AddressService> getAddressServiceProviders()
    {
        return factoryInstance.addressServiceProviders;
    }

    public static ServiceProviders<DistrictService> getDistrictServiceProviders()
    {
        return factoryInstance.districtServiceProviders;
    }

    public static GeocodeServiceProvider getGeocodeServiceProvider()
    {
        return factoryInstance.geocodeServiceProvider;
    }
}