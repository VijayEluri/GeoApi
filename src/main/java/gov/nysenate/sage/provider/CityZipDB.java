package gov.nysenate.sage.provider;

import gov.nysenate.sage.dao.provider.CityZipDBDao;
import gov.nysenate.sage.service.address.CityZipService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CityZipDB implements CityZipService
{
    private static Logger logger = LoggerFactory.getLogger(CityZipDB.class);
    private static CityZipDBDao cityZipDBDao = new CityZipDBDao();

    @Override
    public String getCityByZip(String zip5)
    {
        return null;
    }

    /**
     * Retrieves a list of zip5 codes that are contained within the given city.
     * @param city Name of the city
     * @return List of zip5 codes
     */
    @Override
    public List<String> getZipsByCity(String city)
    {
        if (city != null && !city.isEmpty()) {
            return cityZipDBDao.getZipsByCity(city);
        }
        return new ArrayList<>();
    }
}
