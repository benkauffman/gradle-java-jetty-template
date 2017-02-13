package com.krashidbuilt.api.resource;

import com.krashidbuilt.api.model.Error;
import com.krashidbuilt.api.service.MySQL;
import com.krashidbuilt.api.validation.CustomValidator;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("health")
public final class HealthCheckResource {
    private static Logger logger = LogManager.getLogger();

    @GET()
    @Produces("application/json")
    @SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
    public Response check() {

        logger.debug("HEALTH CHECK HAS BEEN STARTED");

        String email = null;
        MySQL db = new MySQL();
        try {
            db.setpStmt(db.getConn().prepareStatement(
                    "SELECT email FROM application_user WHERE deleted IS NULL LIMIT 1"
            ));
            db.setRs(db.getpStmt().executeQuery());
            while (db.getRs().next()) {
                email = db.getRs().getString(1);
            }
        } catch (SQLException ex) {
            logger.error("HEALTH CHECK ::: UNABLE TO GET EMAIL FROM DATABASE ", ex);
        }

        db.cleanUp();

        if (CustomValidator.isBlank(email)) {
            //UNABLE TO GET A USER FROM DATABASE
            logger.error("HEALTH CHECK FAILED ::: UNABLE TO GET A USER EMAIL FROM DATABASE");
            Error error = Error.internalServerError();
            return Response.status(error.getStatusCode()).build();
        }


        logger.debug("HEALTH CHECK WAS SUCCESSFUL ::: DATA RETRIEVED FROM THE DATABASE");
        return Response.ok().build();
    }
}
