package gov.nysenate.sage.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.sage.factory.ApplicationFactory;
import gov.nysenate.sage.model.address.Address;
import gov.nysenate.sage.model.address.GeocodedAddress;
import gov.nysenate.sage.model.geo.Geocode;
import gov.nysenate.sage.model.geo.GeocodeQuality;
import gov.nysenate.sage.model.geo.Point;
import gov.nysenate.sage.model.result.GeocodeResult;
import gov.nysenate.sage.model.result.ResultStatus;
import gov.nysenate.sage.service.geo.GeocodeService;
import gov.nysenate.sage.service.geo.GeocodeServiceValidator;
import gov.nysenate.sage.service.geo.ParallelGeocodeService;
import gov.nysenate.sage.util.Config;
import gov.nysenate.sage.util.TimeUtil;
import gov.nysenate.sage.util.UrlRequest;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class OSM implements GeocodeService, Observer
{
    private static final String DEFAULT_BASE_URL = "http://open.mapquestapi.com/nominatim/v1/search";
    private final Logger logger = LoggerFactory.getLogger(OSM.class);
    private Config config;
    private ObjectMapper objectMapper;
    private String baseUrl;

    public OSM()
    {
        this.config = ApplicationFactory.getConfig();
        this.objectMapper = new ObjectMapper();
        configure();
        logger.debug("Initialized OSM Adapter");
    }

    private void configure()
    {
        baseUrl = config.getValue("osm.url");
        if (baseUrl.isEmpty()) {
            baseUrl = DEFAULT_BASE_URL;
        }
    }

    public void update(Observable o, Object arg)
    {
        configure();
    }

    @Override
    public GeocodeResult geocode(Address address)
    {
        GeocodeResult geocodeResult = new GeocodeResult(this.getClass());

        /** Ensure that the geocoder is active, otherwise return error result. */
        if (!GeocodeServiceValidator.isGeocodeServiceActive(this.getClass(), geocodeResult)) {
            return geocodeResult;
        }

        /** Proceed only on valid input */
        if (!GeocodeServiceValidator.validateGeocodeInput(address, geocodeResult)) {
            return geocodeResult;
        }

        try {
            String url = baseUrl +"?format=json&q=" + URLEncoder.encode(address.toString(), "UTF-8")
                    + "&addressdetails=1&limit=3&viewbox=-1.99%2C52.02%2C0.78%2C50.94";
            logger.debug(url);

            String json = UrlRequest.getResponseFromUrl(url);
            logger.debug(json);

            /** TODO add proper status error code */
            if (json == null || json.isEmpty() || json.equals("[]")) {
                logger.debug("No response from OSM");
                geocodeResult.setStatusCode(ResultStatus.RESPONSE_ERROR);
                return geocodeResult;
            }

            /** Parse the response from the first object in the json array */
            JsonNode jsonNode = objectMapper.readTree(json).get(0);
            JsonNode jsonAddress = jsonNode.get("address");

            if (jsonNode.hasNonNull("lat")) {
                String type = jsonNode.get("type").asText();
                double lat = jsonNode.get("lat").asDouble(0.0);
                double lon = jsonNode.get("lon").asDouble(0.0);

                /**
                 * Other types are possible here and can be implemented.
                 * SEE http://wiki.openstreetmap.org/wiki/Map_Features for complete list.
                 */
                String street1 = "";
                if(type.equals("house") && jsonAddress.has("house_number")) {
                    street1 = jsonAddress.get("house_number").asText();
                }
                String addr1 = (jsonAddress.has("road") ? street1 + " " + jsonAddress.get("road").asText() : "");
                String city = (jsonAddress.has("city") ? jsonAddress.get("city").asText() : "");
                String state = (jsonAddress.has("state") ? jsonAddress.get("state").asText() : "");
                String postal = (jsonAddress.has("postcode") ? jsonAddress.get("postcode").asText() : "");

                Address resultAddress = new Address(addr1, city, state, postal);
                Geocode geocode = new Geocode(new Point(lat,lon), GeocodeQuality.UNKNOWN, this.getClass().getSimpleName());
                GeocodedAddress geocodedAddress = new GeocodedAddress(resultAddress, geocode);

                if (!GeocodeServiceValidator.validateGeocodeResult(this.getClass(), geocodedAddress, geocodeResult, true)) {
                    logger.debug("Failed to geocode using OSM!");
                }
                else {
                    return geocodeResult;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            String msg = "UTF-8 encoding not supported!?";
            logger.error(msg);
        }
        catch (MalformedURLException e) {
            String msg = "Malformed URL. Check api key and address values.";
            logger.error(msg, e);
        }
        catch (IOException e) {
            String msg = "Error opening API resource.";
            logger.error(msg, e);
        }
        catch (NullPointerException ex) {
            String msg = "Error while parsing JSON result!";
            logger.error(msg, ex);
        }
        catch (Exception ex) {
            logger.error(ex.getMessage());
        }

        geocodeResult.setStatusCode(ResultStatus.NO_GEOCODE_RESULT);
        geocodeResult.setResultTime(TimeUtil.currentTimestamp());
        geocodeResult.addMessage("Failed to retrieve a geocoded address from the response.");
        return geocodeResult;
    }

    @Override
    public ArrayList<GeocodeResult> geocode(ArrayList<Address> addresses)
    {
        return ParallelGeocodeService.geocode(this, addresses);
    }
}