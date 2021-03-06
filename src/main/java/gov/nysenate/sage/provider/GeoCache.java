package gov.nysenate.sage.provider;

import gov.nysenate.sage.dao.provider.GeoCacheDao;
import gov.nysenate.sage.model.address.Address;
import gov.nysenate.sage.model.address.GeocodedAddress;
import gov.nysenate.sage.model.address.GeocodedStreetAddress;
import gov.nysenate.sage.model.address.StreetAddress;
import gov.nysenate.sage.model.result.GeocodeResult;
import gov.nysenate.sage.service.geo.GeocodeCacheService;
import gov.nysenate.sage.service.geo.GeocodeService;
import gov.nysenate.sage.service.geo.ParallelGeocodeService;
import gov.nysenate.sage.util.StreetAddressParser;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static gov.nysenate.sage.service.geo.GeocodeServiceValidator.validateGeocodeInput;
import static gov.nysenate.sage.service.geo.GeocodeServiceValidator.validateGeocodeResult;

public class GeoCache implements GeocodeCacheService
{
    private final Logger logger = LoggerFactory.getLogger(GeoCache.class);
    private static Set<Class<? extends GeocodeService>> cacheableProviders = new HashSet<>();
    private GeoCacheDao geoCacheDao;

    public GeoCache() {
        this.geoCacheDao = new GeoCacheDao();
        logger.debug("Instantiated GeoCache.");
    }

    /**
     * Designates a provider (that has been registered) as a reliable source for caching results.
     * @param provider the provider to be added to the cacheableProviders list
     */
    public static void registerProviderAsCacheable(Class<? extends GeocodeService> provider)
    {
        if (provider != null) {
            cacheableProviders.add(provider);
        }
    }

    /**
     * Checks if providerName is allowed to save result into cache
     * @param provider check name of this provider to see if it is registered as cacheable
     * @return true if it is allowed, false otherwise
     */
    public static boolean isProviderCacheable(Class<? extends GeocodeService> provider)
    {
        return cacheableProviders.contains(provider);
    }

    @Override
    public GeocodeResult geocode(Address address)
    {
        logger.trace("Attempting geocode cache lookup");
        GeocodeResult geocodeResult  = new GeocodeResult(this.getClass());

        /* Proceed only on valid input */
        if (!validateGeocodeInput(address, geocodeResult)) return geocodeResult;

        /* Retrieve geocoded address from cache */
        StreetAddress sa = StreetAddressParser.parseAddress(address);
        GeocodedStreetAddress geocodedStreetAddress = geoCacheDao.getCacheHit(sa);

        /* Validate and return */
        if (!validateGeocodeResult(this.getClass(), geocodedStreetAddress, geocodeResult, false)) {
            logger.trace("Failed to find cache hit for " + address.toString());
        }
        return geocodeResult;
    }

    @Override
    public ArrayList<GeocodeResult> geocode(ArrayList<Address> addresses)
    {
        return ParallelGeocodeService.geocode(this, addresses);
    }

    @Override
    public void saveToCache(GeocodeResult geocodeResult)
    {
        if (geocodeResult != null && geocodeResult.isSuccess() && geocodeResult.getSource() != null) {
            if (isProviderCacheable(geocodeResult.getSource())) {
                geoCacheDao.cacheGeocodedAddress(geocodeResult.getGeocodedAddress());
            }
        }
    }

    @Override
    public void saveToCacheAndFlush(GeocodeResult geocodeResult)
    {
        this.saveToCache(geocodeResult);
        geoCacheDao.flushCacheBuffer();
    }

    @Override
    public void saveToCache(List<GeocodeResult> geocodeResults)
    {
        List<GeocodedAddress> geocodedAddresses = new ArrayList<>();
        for (GeocodeResult geocodeResult : geocodeResults) {
            if (geocodeResult != null && geocodeResult.isSuccess()) {
                if (isProviderCacheable(geocodeResult.getSource())) {
                    geocodedAddresses.add(geocodeResult.getGeocodedAddress());
                }
            }
        }
        geoCacheDao.cacheGeocodedAddresses(geocodedAddresses);
    }

    @Override
    public void saveToCacheAndFlush(List<GeocodeResult> geocodeResults)
    {
        this.saveToCache(geocodeResults);
        geoCacheDao.flushCacheBuffer();
    }
}
