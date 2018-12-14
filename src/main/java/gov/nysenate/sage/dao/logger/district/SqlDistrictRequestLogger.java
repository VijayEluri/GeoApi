package gov.nysenate.sage.dao.logger.district;

import gov.nysenate.sage.dao.base.BaseDao;
import gov.nysenate.sage.dao.base.ReturnIdHandler;
import gov.nysenate.sage.dao.logger.address.SqlAddressLogger;
import gov.nysenate.sage.model.api.ApiRequest;
import gov.nysenate.sage.model.api.DistrictRequest;
import gov.nysenate.sage.model.job.JobProcess;
import org.apache.commons.dbutils.QueryRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.sql.SQLException;

@Repository
public class SqlDistrictRequestLogger
{
    private static Logger logger = LoggerFactory.getLogger(SqlDistrictRequestLogger.class);
    private static SqlAddressLogger sqlAddressLogger;
    private static String SCHEMA = "log";
    private static String TABLE = "districtRequest";
    private QueryRunner run;

    private BaseDao baseDao;

    @Autowired
    public SqlDistrictRequestLogger(SqlAddressLogger sqlAddressLogger, BaseDao baseDao) {
        this.sqlAddressLogger = sqlAddressLogger;
        this.baseDao = baseDao;
        run = this.baseDao.getQueryRunner();
    }

    /**
     * Log a DistrictRequest to the database
     * @param dr DistrictRequest
     * @return id of district request. This id is set to the supplied districtRequest as well.
     */
    public int logDistrictRequest(DistrictRequest dr)
    {
        if (dr != null) {
            ApiRequest apiRequest = dr.getApiRequest();
            JobProcess jobProcess = dr.getJobProcess();

            try {
                int addressId = (dr.getGeocodedAddress() != null) ? sqlAddressLogger.logAddress(dr.getGeocodedAddress().getAddress()) : 0;
                String strategy = (dr.getDistrictStrategy() != null) ? dr.getDistrictStrategy().name() : null;
                int requestId = run.query(
                    "INSERT INTO " + SCHEMA + "." + TABLE + "(apiRequestId, jobProcessId, addressId, provider, geoProvider, showMembers, showMaps, uspsValidate, skipGeocode, districtStrategy, requestTime) \n" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n" +
                    "RETURNING id", new ReturnIdHandler(), (apiRequest != null) ? apiRequest.getId() : null,
                                                           (jobProcess != null) ? jobProcess.getId() : null,
                                                           (addressId > 0) ? addressId : null, dr.getProvider(), dr.getGeoProvider(), dr.isShowMembers(),
                                                           dr.isShowMaps(), dr.isUspsValidate(), dr.isSkipGeocode(), strategy, dr.getRequestTime());
                dr.setId(requestId);
                return requestId;
            }
            catch (SQLException ex) {
                logger.error("Failed to log district request!", ex);
            }
        }
        else {
            logger.error("DistrictRequest was null, cannot be logged!");
        }
        return 0;
    }
}