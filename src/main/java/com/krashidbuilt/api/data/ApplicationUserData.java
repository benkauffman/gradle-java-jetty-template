package com.krashidbuilt.api.data;

import com.krashidbuilt.api.util.ObjectMapper;
import com.krashidbuilt.api.model.ApplicationUser;
import com.krashidbuilt.api.model.ThrowableError;
import com.krashidbuilt.api.service.MySQL;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
public final class ApplicationUserData {

    private static Logger logger = LogManager.getLogger();


    private ApplicationUserData() {

    }

    public static ApplicationUser create(ApplicationUser in) throws ThrowableError {
        logger.debug("CREATE USER START");

        MySQL db = new MySQL();

        String sql = "INSERT INTO application_user " +
                "(id, email, password, first_name, last_name, username)\n" +
                "VALUES (?, ?, ?, ?, ?, ?)\n" +
                "ON DUPLICATE KEY UPDATE\n" +
                "  password = VALUES(password),\n" +
                "  first_name = VALUES(first_name),\n" +
                "  last_name = VALUES(last_name),\n" +
                "  username = VALUES(username)";
        try {

            db.setpStmt(db.getConn().prepareStatement(sql));

            db.getpStmt().setInt(1, 0);
            db.getpStmt().setString(2, in.getEmail());
            db.getpStmt().setString(3, in.getPassword());
            db.getpStmt().setString(4, in.getFirstName());
            db.getpStmt().setString(5, in.getLastName());
            db.getpStmt().setString(6, in.getUsername());

            db.getpStmt().executeUpdate();

        } catch (SQLException e) {
            logger.error("UNABLE TO CREATE USER", e);
        }

        db.cleanUp();

        logger.debug("CREATE USER END");

        return in;
    }

    public static ApplicationUser getById(int userId) {
        logger.debug("GET USER {} BY ID", userId);
        ApplicationUser user = new ApplicationUser();
        MySQL db = new MySQL();

        String sql = "SELECT * FROM application_user WHERE id = ? LIMIT 1";
        try {

            db.setpStmt(db.getConn().prepareStatement(sql));
            db.getpStmt().setInt(1, userId);

            db.setRs(db.getpStmt().executeQuery());

            while (db.getRs().next()) {
                user = ObjectMapper.applicationUser(db.getRs());
            }


        } catch (SQLException e) {
            logger.error("UNABLE TO GET USER BY ID", e);
        }

        db.cleanUp();

        logger.debug("GET USER {} BY ID END", userId);
        return user;
    }

    public static ApplicationUser getByEmail(String email) {
        logger.debug("GET USER {} BY EMAIL", email);
        ApplicationUser user = new ApplicationUser();
        MySQL db = new MySQL();

        String sql = "SELECT * FROM application_user WHERE email = ? LIMIT 1";
        try {

            db.setpStmt(db.getConn().prepareStatement(sql));
            db.getpStmt().setString(1, email);

            db.setRs(db.getpStmt().executeQuery());

            while (db.getRs().next()) {
                user = ObjectMapper.applicationUser(db.getRs());
            }

        } catch (SQLException e) {
            logger.error("UNABLE TO GET USER BY EMAIL", e);
        }

        db.cleanUp();

        logger.debug("GET USER {} BY EMAIL", email);
        return user;
    }

}
