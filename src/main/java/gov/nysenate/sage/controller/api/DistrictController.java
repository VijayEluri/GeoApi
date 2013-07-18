package gov.nysenate.sage.controller.api;

import gov.nysenate.sage.client.response.base.ApiError;
import gov.nysenate.sage.client.response.district.*;
import gov.nysenate.sage.dao.logger.DistrictRequestLogger;
import gov.nysenate.sage.dao.logger.DistrictResultLogger;
import gov.nysenate.sage.dao.logger.GeocodeRequestLogger;
import gov.nysenate.sage.dao.logger.GeocodeResultLogger;
import gov.nysenate.sage.factory.ApplicationFactory;
import gov.nysenate.sage.model.address.Address;
import gov.nysenate.sage.model.address.GeocodedAddress;
import gov.nysenate.sage.model.address.StreetAddress;
import gov.nysenate.sage.model.api.ApiRequest;
import gov.nysenate.sage.model.api.DistrictRequest;
import gov.nysenate.sage.model.api.GeocodeRequest;
import gov.nysenate.sage.model.district.DistrictType;
import gov.nysenate.sage.model.geo.Geocode;
import gov.nysenate.sage.model.geo.GeocodeQuality;
import gov.nysenate.sage.model.geo.Point;
import gov.nysenate.sage.model.result.AddressResult;
import gov.nysenate.sage.model.result.DistrictResult;
import gov.nysenate.sage.model.result.GeocodeResult;
import gov.nysenate.sage.model.result.ResultStatus;
import gov.nysenate.sage.service.address.AddressServiceProvider;
import gov.nysenate.sage.service.address.CityZipServiceProvider;
import gov.nysenate.sage.service.district.DistrictMemberProvider;
import gov.nysenate.sage.service.district.DistrictServiceProvider;
import gov.nysenate.sage.service.geo.GeocodeServiceProvider;
import gov.nysenate.sage.service.geo.RevGeocodeServiceProvider;
import gov.nysenate.sage.service.map.MapServiceProvider;
import gov.nysenate.sage.util.Config;
import gov.nysenate.sage.util.FormatUtil;
import gov.nysenate.sage.util.StreetAddressParser;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static gov.nysenate.sage.model.result.ResultStatus.*;
import static gov.nysenate.sage.service.district.DistrictServiceProvider.*;

/** Handles District Api requests */
public class DistrictController extends BaseApiController implements Observer
{
    private static Logger logger = Logger.getLogger(DistrictController.class);
    private static Config config = ApplicationFactory.getConfig();

    /** Service Providers */
    private static AddressServiceProvider addressProvider = ApplicationFactory.getAddressServiceProvider();
    private static DistrictServiceProvider districtProvider = ApplicationFactory.getDistrictServiceProvider();
    private static GeocodeServiceProvider geocodeProvider = ApplicationFactory.getGeocodeServiceProvider();
    private static RevGeocodeServiceProvider revGeocodeProvider = ApplicationFactory.getRevGeocodeServiceProvider();
    private static MapServiceProvider mapProvider = ApplicationFactory.getMapServiceProvider();
    private static CityZipServiceProvider cityZipProvider = ApplicationFactory.getCityZipServiceProvider();

    /** Loggers */
    private static GeocodeRequestLogger geocodeRequestLogger;
    private static GeocodeResultLogger geocodeResultLogger;
    private static DistrictRequestLogger districtRequestLogger;
    private static DistrictResultLogger districtResultLogger;

    private static String BLUEBIRD_DISTRICT_STRATEGY;

    private static Boolean LOGGING_ENABLED = false;

    @Override
    public void update(Observable o, Object arg) {
        BLUEBIRD_DISTRICT_STRATEGY = config.getValue("district.strategy.bluebird");
    }

    @Override
    public void init(ServletConfig config) throws ServletException
    {
        DistrictController.config.notifyOnChange(this);
        update(null, null);
        geocodeRequestLogger = new GeocodeRequestLogger();
        geocodeResultLogger = new GeocodeResultLogger();
        districtRequestLogger = new DistrictRequestLogger();
        districtResultLogger = new DistrictResultLogger();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.doGet(request, response);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Object districtResponse = null;

        /** Get the ApiRequest */
        ApiRequest apiRequest = getApiRequest(request);
        String provider = apiRequest.getProvider();

        /** Allow for specifying which geocoder to use */
        String geoProvider = request.getParameter("geoProvider");

        /** Fetch senator and other member info if true */
        Boolean showMembers = Boolean.parseBoolean(request.getParameter("showMembers"));

        /** Specify whether or not to return map data */
        Boolean showMaps = Boolean.parseBoolean(request.getParameter("showMaps"));

        /** Indicates whether address validation is required */
        Boolean uspsValidate = Boolean.parseBoolean(request.getParameter("uspsValidate"));

        /** Specify whether or not to geocode (Warning: If false, district assignment will be impaired) */
        Boolean skipGeocode = Boolean.parseBoolean(request.getParameter("skipGeocode"));

        /** Indicates whether info for multiple possible districts should be shown. */
        Boolean showMultiMatch = Boolean.parseBoolean(request.getParameter("showMultiMatch"));

        /** Specify district strategy */
        String districtStrategy = request.getParameter("districtStrategy");

        DistrictRequest districtRequest = new DistrictRequest();
        districtRequest.setApiRequest(apiRequest);
        districtRequest.setAddress(getAddressFromParams(request));
        districtRequest.setPoint(getPointFromParams(request));
        districtRequest.setProvider(provider);
        districtRequest.setGeoProvider(geoProvider);
        districtRequest.setShowMembers(showMembers);
        districtRequest.setShowMaps(showMaps);
        districtRequest.setUspsValidate(uspsValidate);
        districtRequest.setSkipGeocode(skipGeocode);
        districtRequest.setRequestTime(new Timestamp(new Date().getTime()));
        districtRequest.setDistrictStrategy(config.getValue("district.strategy.single", "neighborMatch"));

        logger.info("--------------------------------------");
        logger.info(String.format("District Request | Mode: %s", apiRequest.getRequest()));
        logger.info("--------------------------------------");

        /**
         * If providers are specified then make sure they match the available providers. Send an
         * api error and return if the provider is not supported.
         */
        if (provider != null && !provider.isEmpty() && !districtProvider.isRegistered(provider)) {
            setApiResponse(new ApiError(this.getClass(), DISTRICT_PROVIDER_NOT_SUPPORTED), request);
            return;
        }
        if (geoProvider != null && !geoProvider.isEmpty() && !geocodeProvider.isRegistered(geoProvider)) {
            setApiResponse(new ApiError(this.getClass(), GEOCODE_PROVIDER_NOT_SUPPORTED), request);
            return;
        }

        switch (apiRequest.getRequest())
        {
            case "assign": {
                /** Handle single district assign request using the supplied query parameters. */
                if (!apiRequest.isBatch()) {
                    DistrictResult districtResult = handleDistrictRequest(districtRequest);
                    if (districtResult.isMultiMatch() && showMultiMatch) {
                        districtResponse = (showMaps) ? new MappedMultiDistrictResponse(districtResult) : new MultiDistrictResponse(districtResult);
                    }
                    else {
                        districtResponse = (showMaps) ? new MappedDistrictResponse(districtResult) : new DistrictResponse(districtResult);
                    }
                }
                /** Handle batch district assign request using the supplied query parameters. */
                else {
                    List<Address> addresses = getAddressesFromJsonBody(request);
                    if (addresses.size() > 0) {
                        List<DistrictResult> districtResults =
                                handleBatchDistrictRequest(addresses, provider, geoProvider, uspsValidate, !skipGeocode, showMembers,
                                        false, districtStrategy);
                        districtResponse = new BatchDistrictResponse(districtResults);
                    }
                    else {
                        districtResponse = new ApiError(this.getClass(), INVALID_BATCH_ADDRESSES);
                    }
                }
                break;
            }
            case "bluebird":
            {
                /** Handle single bluebird assign */
                if (!apiRequest.isBatch()) {
                    Address address = getAddressFromParams(request);
                    if (address != null && !address.isEmpty()) {
                        DistrictRequest bluebirdRequest = DistrictRequest.buildBluebirdRequest(apiRequest, address,
                                                                                               BLUEBIRD_DISTRICT_STRATEGY);
                        DistrictResult districtResult = handleDistrictRequest(bluebirdRequest);
                        districtResponse = new DistrictResponse(districtResult);
                    }
                    else {
                        districtResponse = new ApiError(this.getClass(), MISSING_ADDRESS);
                    }
                }
                /** Handle batch bluebird assign */
                else {
                    logger.info("Batch bluebird district assign");
                    List<Address> addresses = getAddressesFromJsonBody(request);
                    if (addresses.size() > 0) {
                        List<DistrictResult> districtResults =
                                handleBatchDistrictRequest(addresses, null, null, true, true, false, false, BLUEBIRD_DISTRICT_STRATEGY);
                        districtResponse = new BatchDistrictResponse(districtResults);
                    }
                    else {
                        districtResponse = new ApiError(this.getClass(), INVALID_BATCH_ADDRESSES);
                    }
                }
                break;
            }
            default : {
                districtResponse = new ApiError(this.getClass(), SERVICE_NOT_SUPPORTED);
            }
        }
        setApiResponse(districtResponse, request);
    }

    /**
     *
     * @param districtRequest
     * @return
     */
    private DistrictResult handleDistrictRequest(DistrictRequest districtRequest)
    {
        Address address = districtRequest.getAddress();
        Point point = districtRequest.getPoint();
        DistrictResult districtResult;
        GeocodedAddress geocodedAddress;
        Boolean zipProvided = false;
        Boolean isPoBox = false;

        /** Parse the input address */
        StreetAddress streetAddress = null;
        try {
            streetAddress = StreetAddressParser.parseAddress(address);
        }
        catch (Exception ex) {
            logger.debug("Failed to parse input address");
        }

        /** Perform geocoding for address input */
        if (districtRequest.getAddress() != null && !districtRequest.getAddress().isEmpty()) {
            geocodedAddress = new GeocodedAddress(address);
            zipProvided = isZipProvided(streetAddress);
            isPoBox = (streetAddress != null) ? streetAddress.isPoBoxAddress() : false;
            logger.trace("Is PO BOX: " + isPoBox);

            /** Geocode address unless opted out */
            if (!districtRequest.isSkipGeocode()) {
                GeocodeRequest geocodeRequest = new GeocodeRequest(districtRequest.getApiRequest(), address, districtRequest.getGeoProvider(), true, true);
                geocodedAddress = performGeocode(geocodeRequest, isPoBox);
            }
        }
        /** Possibly perform reverse geocoding for point input */
        else if (districtRequest.getPoint() != null) {
            Address revGeoAddress = null;
            /** Note: If the provider is `streetfile` then we must resolve the point into an address */
            if (!districtRequest.isSkipGeocode()) {
                GeocodeResult geocodeResult = revGeocodeProvider.reverseGeocode(point);
                if (geocodeResult.isSuccess()) {
                    revGeoAddress = geocodeResult.getAddress();
                }
            }
            geocodedAddress = new GeocodedAddress(revGeoAddress, new Geocode(districtRequest.getPoint(), GeocodeQuality.POINT, "User Supplied"));
        }
        else {
            districtResult = new DistrictResult(this.getClass());
            districtResult.setStatusCode(MISSING_INPUT_PARAMS);
            return districtResult;
        }

        /** Obtain district result */
        districtResult = performDistrictAssign(districtRequest, geocodedAddress, zipProvided, isPoBox);
        logger.debug("Obtained district result with assigned districts: " + FormatUtil.toJsonString(districtResult.getAssignedDistricts()));

        /** Perform usps address correction if requested */
        if (districtRequest.isUspsValidate()) {
            districtResult.setUspsValidated(performAddressCorrection(address, geocodedAddress, isPoBox, streetAddress));
        }
        /** Adjust address if it's a PO BOX */
        else if (isPoBox && districtResult.getAddress() != null) {
            districtResult.getAddress().setAddr1("Po Box " + streetAddress.getPoBox());
        }

        /** Add map and boundary information to the district result */
        if (districtResult.isSuccess()) {
            mapProvider.assignMapsToDistrictInfo(districtResult.getDistrictInfo(), districtResult.getDistrictMatchLevel(), false);
        }

        /** Ensure all members (senators,assemblyman, etc) are presented if requested */
        if (districtRequest.isShowMembers() && districtResult.isSuccess()) {
            DistrictMemberProvider.assignDistrictMembers(districtResult);
        }

        if (LOGGING_ENABLED) {
            int requestId = districtRequestLogger.logDistrictRequest(districtRequest);
            districtResultLogger.logDistrictResult(requestId, districtResult);
            logger.debug("logged it");
        }

        return districtResult;
    }

    /**
     * Determines if a zip5 was specified in the input address.
     * @param streetAddress  Parsed input Address
     * @return  True if zip5 was provided, false otherwise
     */
    private Boolean isZipProvided(StreetAddress streetAddress) {
        Boolean zipProvided = false;
        if (streetAddress != null) {
            String zip5 = streetAddress.getZip5();
            zipProvided = (!zip5.isEmpty() && zip5.length() == 5);
        }
        return zipProvided;
    }

    /**
     * Performs geocoding using the default geocode service provider.
     * @param geoRequest The GeocodeRequest to handle.
     * @return GeocodedAddress
     */
    private GeocodedAddress performGeocode(GeocodeRequest geoRequest, boolean isPoBox)
    {
        Address address = geoRequest.getAddress();
        String geoProvider = geoRequest.getProvider();

        /** Geocoding for Po Box works better when the address line is empty */
        if (isPoBox && address != null && address.isParsed()) {
            address.setAddr1("");
        }
        GeocodeResult geocodeResult = (geoProvider != null) ? geocodeProvider.geocode(address, geoProvider, false, false)
                                                            : geocodeProvider.geocode(address);
        /** Log geocode request/result to database */
        if (LOGGING_ENABLED) {
            int requestId = geocodeRequestLogger.logGeocodeRequest(geoRequest);
            geocodeResultLogger.logGeocodeResult(requestId, geocodeResult);
        }

        return (geocodeResult != null) ? geocodeResult.getGeocodedAddress() : null;
    }

    /** Perform USPS address correction on either the geocoded address or the input address.
     * If the geocoded address is invalid, the original address will be corrected and set as the address
     * on the supplied geocodedAddress parameter.
     * @return GeocodedAddress the address corrected geocodedAddress.
     */
    private boolean performAddressCorrection(Address address, GeocodedAddress geocodedAddress, boolean isPoBox, StreetAddress streetAddress)
    {
        Address addressToCorrect = (geocodedAddress != null && geocodedAddress.isValidAddress())
                                   ? geocodedAddress.getAddress() : address;
        if (isPoBox && addressToCorrect != null) {
            addressToCorrect.setAddr1("PO BOX " + streetAddress.getPoBox());
        }
        AddressResult addressResult = addressProvider.newInstance("usps").validate(addressToCorrect);
        if (addressResult.isValidated() && geocodedAddress != null) {
            logger.debug("USPS Validated Address: " + addressResult.getAddress());
            geocodedAddress.setAddress(addressResult.getAddress());
            return true;
        }
        return false;
    }

    /**
     *
     * @param dr
     * @param geocodedAddress
     * @return
     */
    private DistrictResult performDistrictAssign(DistrictRequest dr, GeocodedAddress geocodedAddress, Boolean zipProvided, Boolean isPoBox)
    {
        DistrictResult districtResult = new DistrictResult(this.getClass());
        districtResult.setStatusCode(NO_DISTRICT_RESULT);

        if (geocodedAddress != null) {
            if (geocodedAddress.isValidAddress()) {
                if (geocodedAddress.isValidGeocode()) {
                    GeocodeQuality level = geocodedAddress.getGeocode().getQuality();
                    Address address = geocodedAddress.getAddress();
                    logger.trace(FormatUtil.toJsonString(geocodedAddress));
                    /** House level matches and above can utilize default district assignment behaviour */
                    if (level.compareTo(GeocodeQuality.HOUSE) >= 0 || isPoBox) {
                        districtResult = districtProvider.assignDistricts(geocodedAddress, dr.getProvider(),
                                DistrictType.getStandardTypes(), dr.getDistrictStrategy());
                    }
                    /** All other level matches are routed to the overlap assignment method */
                    else {
                        districtResult = districtProvider.assignMultiMatchDistricts(geocodedAddress, zipProvided);
                    }
                }
                else {
                    districtResult.setStatusCode(ResultStatus.INVALID_GEOCODE);
                }
            }
            else if (geocodedAddress.isValidGeocode()) {
                districtResult = districtProvider.assignDistricts(geocodedAddress, "shapefile", DistrictType.getStandardTypes(), dr.getDistrictStrategy());
            }
            else {
                districtResult.setStatusCode(ResultStatus.INSUFFICIENT_ADDRESS);
            }
        }
        else {
            districtResult.setStatusCode(ResultStatus.MISSING_GEOCODED_ADDRESS);
        }

        return districtResult;
    }

    /**
     * Utilizes the service providers to perform batch address validation, geo-coding, and district assignment for an address.
     * @return List<DistrictResult>
     */
    private List<DistrictResult> handleBatchDistrictRequest(List<Address> addresses, String provider, String geoProvider, Boolean uspsValidate,
                                                            Boolean performGeocode, Boolean showMembers, Boolean showMaps, String districtStrategy)
    {
        List<GeocodedAddress> geocodedAddresses = new ArrayList<>();
        for (Address address : addresses) {
            geocodedAddresses.add(new GeocodedAddress(address));
        }

        if (performGeocode) {
            List<GeocodeResult> geocodeResults =
                    (geoProvider != null) ? geocodeProvider.geocode(addresses, geoProvider, false, false) : geocodeProvider.geocode(addresses);
            for (int i = 0; i < geocodeResults.size(); i++) {
                geocodedAddresses.set(i, geocodeResults.get(i).getGeocodedAddress());
            }
        }

        if (uspsValidate) {
            List<AddressResult> addressResults = addressProvider.newInstance("usps").validate((ArrayList) addresses);
            for (int i = 0; i < addressResults.size(); i++) {
                if (addressResults.get(i).isValidated() && !geocodedAddresses.isEmpty()) {
                    geocodedAddresses.get(i).setAddress(addressResults.get(i).getAddress());
                }
            }
        }

        DistrictStrategy strategy;
        try {
            strategy = DistrictStrategy.valueOf(districtStrategy);
        }
        catch (Exception ex) {
            strategy = null;
        }

        return districtProvider.assignDistricts(geocodedAddresses, provider, DistrictType.getStandardTypes(), strategy);
    }
}