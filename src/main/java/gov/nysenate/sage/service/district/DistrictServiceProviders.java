package gov.nysenate.sage.service.district;

import gov.nysenate.sage.service.ServiceProviders;
import gov.nysenate.sage.service.district.DistrictService;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DistrictServiceProviders implements ServiceProviders
{
    private static Logger logger = Logger.getLogger(DistrictServiceProviders.class);
    private static Map<String,DistrictService> providers = new HashMap<>();
    private static final String DEFAULT_PROVIDER = "default";

    private DistrictServiceProviders() {}

    /**
     * Registers the default DistrictService as an instance of the given provider.
     * @param provider  The DistrictService implementation that should be default.
     */
    public static void registerDefaultProvider(DistrictService provider)
    {
        providers.put(DEFAULT_PROVIDER, provider);
    }

    /**
     * Registers an instance of an DistrictService implementation.
     * @param providerName  Key that will be used to reference this provider.
     * @param provider      An instance of the provider.
     */
    public static void registerProvider(String providerName, DistrictService provider)
    {
        providers.put(providerName.toLowerCase(), provider);
    }

    /**
     * Returns a new instance of the default DistrictService implementation.
     * @return   DistrictService if default provider is set.
     *           null if default provider not set.
     */
    public static DistrictService newServiceInstance()
    {
        if (providers.containsKey(DEFAULT_PROVIDER)){
            return providers.get(DEFAULT_PROVIDER).newInstance();
        }
        else {
            logger.debug("Default address provider not registered!");
            return null;
        }
    }

    /**
     * Returns a new instance of the AddressProvider that has been registered
     * with the given providerName.
     * @param providerName
     * @return  DistrictService instance specified by providerName.
     *          null if provider is not registered.
     */
    public static DistrictService newServiceInstance(String providerName)
    {
        if (providers.containsKey(providerName.toLowerCase())){
            return providers.get(providerName.toLowerCase()).newInstance();
        }
        else {
            logger.debug(providerName + " is not a registered address provider!");
            return null;
        }
    }
}
