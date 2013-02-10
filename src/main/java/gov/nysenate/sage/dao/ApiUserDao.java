package gov.nysenate.sage.dao;

import gov.nysenate.sage.model.auth.ApiUser;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.log4j.Logger;

import java.sql.SQLException;

/**
 * ApiUserDao provides database persistence for the ApiUser model.
 */
public class ApiUserDao extends BaseDao
{
    private Logger logger = Logger.getLogger(ApiUserDao.class);
    private ResultSetHandler<ApiUser> handler = new BeanHandler<>(ApiUser.class);
    private QueryRunner run = getQueryRunner();

    /**
     * Retrieves an ApiUser from the database by key.
     * @param id         The api user id.
     * @return ApiUser   The matched ApiUser or null if not found.
     */
    public ApiUser getApiUserById(int id)
    {
        try {
            return run.query("SELECT * FROM apiuser WHERE id = ?", handler, id);
        }
        catch (SQLException sqlEx)
        {
            logger.error("Failed to get ApiUser by id in ApiUserDAO!");
            logger.error(sqlEx.getMessage());
        }
        return null;
    }

    /**
     * Retrieves an ApiUser from the database by key.
     * @param key        The api key.
     * @return ApiUser   The matched ApiUser or null if not found.
     */
    public ApiUser getApiUserByKey(String key)
    {
        try {
            return run.query("SELECT * FROM apiuser WHERE apikey = ?", handler, key);
        }
        catch (SQLException sqlEx)
        {
            logger.error("Failed to get ApiUser by key in ApiUserDAO!");
            logger.error(sqlEx.getMessage());
        }
        return null;
    }

    /**
     * Adds an API User to the database.
     * @param apiUser   The ApiUser to add.
     * @return int      1 if user was inserted, 0 otherwise.
     */
    public int addApiUser(ApiUser apiUser)
    {
        try {
            return run.update("INSERT INTO apiuser (apikey,name,description) VALUES (?,?,?)",
                              apiUser.getApiKey(), apiUser.getName(), apiUser.getDescription());
        }
        catch (SQLException sqlEx)
        {
            logger.error("Failed to add ApiUser in ApiUserDAO!");
            logger.error(sqlEx.getMessage());
        }
        return 0;
    }

    /**
     * Removes an API User from the database.
     * @param apiUser   The ApiUser to add.
     * @return int      1 if user was removed, 0 otherwise.
     */
    public int removeApiUser(ApiUser apiUser)
    {
        try {
            return run.update("DELETE FROM apiuser WHERE id = ?", apiUser.getId());
        }
        catch (SQLException sqlEx)
        {
            logger.error("Failed to remove ApiUser in ApiUserDAO!");
            logger.error(sqlEx.getMessage());
        }
        return 0;
    }



}
