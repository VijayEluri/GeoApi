package gov.nysenate.sage.dao.model.admin;

import gov.nysenate.sage.dao.base.BaseDao;
import gov.nysenate.sage.model.admin.AdminUser;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;

/**
 * AdminUserDao provides database persistence for the AdminUser model.
 */
@Repository
public class SqlAdminUserDao
{
    private Logger logger = LoggerFactory.getLogger(SqlAdminUserDao.class);
    private ResultSetHandler<AdminUser> handler = new BeanHandler<>(AdminUser.class);
    private QueryRunner run;
    private BaseDao baseDao;

    private static String SCHEMA = "public";
    private static String TABLE = "admin";

    String sql = "SELECT * FROM " + SCHEMA + "." + TABLE + "\n" +
            "WHERE username = ?";

    String insertAdmin = "INSERT INTO " + SCHEMA +"." + TABLE + " (username, password) VALUES (?,?) RETURNING id;";

    @Autowired
    public SqlAdminUserDao(BaseDao baseDao) {
        this.baseDao = baseDao;
        run = this.baseDao.getQueryRunner();
    }

    /**
     * Check if the admin user credentials are valid.
     * @param username  Admin username
     * @param password  Admin password
     * @return true if valid credentials, false otherwise.
     */
    public boolean checkAdminUser(String username, String password)
    {
        AdminUser adminUser = null;
        try {
            adminUser = run.query(sql, handler, username);
        }
        catch (SQLException ex) {
            logger.error("Failed to retrieve admin user!", ex);
        }

        if (adminUser != null) {
            return BCrypt.checkpw(password, adminUser.getPassword());
        }
        return false;
    }

    public AdminUser getAdminUser(String username) {
        AdminUser adminUser = null;
        try {
            adminUser = run.query(sql, handler, username);
        }
        catch (SQLException ex) {
            logger.error("Failed to retrieve admin user!", ex);
        }
        return adminUser;
    }

    public void insertAdmin(String username, String password) {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("username", username);
//        params.addValue("password", password);
//        baseDao.geoApiNamedJbdcTemaplate.update(insertAdmin, params);

        try {
            run.update(insertAdmin, username, password);
        }
        catch (SQLException e) {
            logger.error("Failed to insert admin user!", e);
        }

    }
}