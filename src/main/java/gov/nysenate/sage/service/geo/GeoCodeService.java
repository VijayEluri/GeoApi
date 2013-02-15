package gov.nysenate.sage.service.geo;

import gov.nysenate.sage.model.address.Address;
import gov.nysenate.sage.Result;
import gov.nysenate.sage.model.result.GeocodeResult;

import java.util.ArrayList;

/**
 *  Comment this later..
 */
public interface GeoCodeService
{
    public GeoCodeService newInstance();

    public GeocodeResult geocode(Address address);
    public ArrayList<GeocodeResult> geocode(ArrayList<Address> addresses);

    public GeocodeResult reverseGeocode(Address address);
}
