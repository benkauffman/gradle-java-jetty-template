package com.krashidbuilt.api.resource;

import com.krashidbuilt.api.data.ApplicationUserData;
import com.krashidbuilt.api.model.ApplicationUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("public/user")
@Api(value = "public/user", description = "Interact with the user object", tags = "user")
public class ApplicationUserResource {
    private static Logger logger = LogManager.getLogger();


    //CREATE
    @GET()
    @Path("{userId}")
    @Produces("application/json")
    @ApiOperation(value = "Get an existing user",
            notes = "Returns the user if it matches the request",
            response = ApplicationUser.class
    )
    @Consumes("application/json")
    public Response getById(@PathParam("userId") int userId, @Context UriInfo uriInfo) {

        logger.debug("Get user by id {} requested at user resource", userId);

        ApplicationUser user = ApplicationUserData.getById(userId);

        if (!user.isValid()) {
            return Response.status(404).build();
        }

        //return user
        logger.debug("User {} found int the database and identified as {} {}.",
                user.getId(),
                user.getFirstName(),
                user.getLastName());
        return Response.ok(user).build();

    }
}
