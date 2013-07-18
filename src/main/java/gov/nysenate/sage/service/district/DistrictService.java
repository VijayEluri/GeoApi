package gov.nysenate.sage.service.district;

import gov.nysenate.sage.model.address.GeocodedAddress;
import gov.nysenate.sage.model.district.DistrictMap;
import gov.nysenate.sage.model.district.DistrictType;
import gov.nysenate.sage.model.result.DistrictResult;
import gov.nysenate.sage.model.result.ResultStatus;
import org.apache.log4j.Logger;
import java.util.List;
import java.util.Map;

/**
 * DistrictService is used to assign district information to addresses and may or may not require
 * geo-coordinate information.
 */
public interface DistrictService
{
    /** Indicates whether the service needs a geocode to perform district assignment */
    public boolean requiresGeocode();

    /** District Assignment */
    public DistrictResult assignDistricts(GeocodedAddress geocodedAddress);
    public DistrictResult assignDistricts(GeocodedAddress geocodedAddress, List<DistrictType> reqTypes);
    public List<DistrictResult> assignDistricts(List<GeocodedAddress> geocodedAddresses);
    public List<DistrictResult> assignDistricts(List<GeocodedAddress> geocodedAddresses, List<DistrictType> reqTypes);

    /** Nearby District Maps */
    public Map<String, DistrictMap> nearbyDistricts(GeocodedAddress geocodedAddress, DistrictType districtType);
    public Map<String, DistrictMap> nearbyDistricts(GeocodedAddress geocodedAddress, DistrictType districtType, int count);
}