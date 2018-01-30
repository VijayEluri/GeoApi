package gov.nysenate.sage.service.geo;

import gov.nysenate.sage.model.api.BatchGeocodeRequest;
import gov.nysenate.sage.model.api.GeocodeRequest;
import gov.nysenate.sage.model.geo.Point;
import gov.nysenate.sage.model.result.GeocodeResult;
import gov.nysenate.sage.model.result.ResultStatus;
import gov.nysenate.sage.provider.GoogleGeocoder;
import gov.nysenate.sage.provider.TigerGeocoder;
import gov.nysenate.sage.service.base.ServiceProviders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
* Point of access for all reverse geocoding requests.
*/
@Service
public class RevGeocodeServiceProvider extends ServiceProviders<RevGeocodeService> implements Observer
{
    private final Logger logger = LogManager.getLogger(GeocodeServiceProvider.class);

    @Autowired
    public RevGeocodeServiceProvider() {
        registerDefaultProvider("google", GoogleGeocoder.class);
        registerProvider("tiger", TigerGeocoder.class);
        setProviderFallbackChain(Arrays.asList("tiger"));
    }

    @Override
    public void update(Observable o, Object arg) {}

    public GeocodeResult reverseGeocode(GeocodeRequest geocodeRequest)
    {
        if (geocodeRequest != null) {
            return reverseGeocode(geocodeRequest.getPoint(), geocodeRequest.getProvider(), geocodeRequest.isUseFallback());
        }
        else {
            return null;
        }
    }

    /**
     * Perform reverse geocode with default options.
     * @param point Point to lookup address for
     * @return      GeocodeResult
     */
    public GeocodeResult reverseGeocode(Point point)
    {
        return reverseGeocode(point, this.defaultProvider, this.defaultFallback, true);
    }

    /**
     * Perform reverse geocode with provider and fallback options.
     * @param point        Point to lookup address for
     * @param provider     Provider to perform reverse geocoding
     * @param useFallback  Set true to use default fallback
     * @return             GeocodeResult
     */
    public GeocodeResult reverseGeocode(Point point, String provider, boolean useFallback)
    {
        return reverseGeocode(point, provider, this.defaultFallback, useFallback);
    }

    /**
     * Perform reverse geocoding with all options specified.
     * @param point             Point to lookup address for
     * @param provider          Provider to perform reverse geocoding
     * @param fallbackProviders Sequence of providers to fallback to
     * @param useFallback       Set true to allow fallback
     * @return                  GeocodeResult
     */
    public GeocodeResult reverseGeocode(Point point, String provider, LinkedList<String> fallbackProviders,
                                        boolean useFallback)
    {
        logger.debug("Performing reverse geocode on point " + point);
        GeocodeResult geocodeResult = new GeocodeResult(this.getClass(), ResultStatus.NO_REVERSE_GEOCODE_RESULT);
        /** Clone the list of fall back reverse geocode providers */
        LinkedList<String> fallback = (fallbackProviders != null) ? new LinkedList<>(fallbackProviders)
                                                                  : new LinkedList<>(this.defaultFallback);

        if (provider != null && !provider.isEmpty()) {
            geocodeResult = this.getInstance(provider).reverseGeocode(point);
        }
        else {
            fallbackProviders.addFirst(this.defaultProvider);
        }

        if (!geocodeResult.isSuccess() && useFallback) {
            Iterator<String> fallbackIterator = fallback.iterator();
            while (!geocodeResult.isSuccess() && fallbackIterator.hasNext()) {
                geocodeResult = this.getInstance(fallbackIterator.next()).reverseGeocode(point);
            }
        }
        geocodeResult.setResultTime(new Timestamp(new Date().getTime()));
        return geocodeResult;
    }

    /**
     * Perform batch reverse geocoding using supploed BatchGeocodeRequest with points set.
     * @param batchRevGeoRequest
     * @return  List<GeocodeResult> or null if batchRevGeoRequest is null.
     */
    public List<GeocodeResult> reverseGeocode(BatchGeocodeRequest batchRevGeoRequest)
    {
        if (batchRevGeoRequest != null) {
            return reverseGeocode(batchRevGeoRequest.getPoints(), batchRevGeoRequest.getProvider(), this.defaultFallback);
        }
        return null;
    }

    /**
    * Perform batch reverse geocoding.
    */
    public List<GeocodeResult> reverseGeocode(List<Point> points)
    {
        return reverseGeocode(points, this.defaultProvider, this.defaultFallback);
    }

    /**
    * Perform batch reverse geocoding.
    */
    public List<GeocodeResult> reverseGeocode(List<Point> points, String provider)
    {
        return reverseGeocode(points, provider, this.defaultFallback);
    }

    /**
    * For batch reverse geocode, simply call the single reverse geocode method iteratively.
    * Keeping things simple because performance here isn't that crucial.
    */
    public List<GeocodeResult> reverseGeocode(List<Point> points, String provider, LinkedList<String> fallbackProviders)
    {
        List<GeocodeResult> geocodeResults = new ArrayList<>();
        for (Point point : points) {
            geocodeResults.add(reverseGeocode(point, provider, fallbackProviders, true));
        }
        return geocodeResults;
    }
}
