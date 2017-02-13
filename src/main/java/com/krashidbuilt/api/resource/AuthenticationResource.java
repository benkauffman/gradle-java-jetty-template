package com.krashidbuilt.api.resource;

import com.krashidbuilt.api.data.ApplicationUserData;
import com.krashidbuilt.api.data.AuthenticationData;
import com.krashidbuilt.api.model.ApplicationUser;
import com.krashidbuilt.api.model.Authentication;
import com.krashidbuilt.api.model.Detail;
import com.krashidbuilt.api.model.Error;
import com.krashidbuilt.api.model.ThrowableError;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import static com.krashidbuilt.api.data.AuthenticationData.TokenType;

@Path("public/auth")
@Api(value = "public/auth", description = "Authenticate a user", tags = "authentication")
public final class AuthenticationResource {
    private static Logger logger = LogManager.getLogger();

    @POST()
    @Path("login")
    @Produces("application/json")
    @ApiOperation(value = "Login a user",
            notes = "Returns the application user with a refresh and access token",
            response = ApplicationUser.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Unauthorized, login failed", response = Error.class)
    })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@NotNull @FormParam("email") String email,
                          @NotNull @FormParam("password") String password,
                          @Context UriInfo uriInfo) {

        ApplicationUser user;
        try {
            user = AuthenticationData.login(email, password);
        } catch (ThrowableError ex) {
            Error error = ex.getError();
            return Response.status(error.getStatusCode()).entity(error).build();
        }

        logger.debug("Login user at login resource." + user.toString());
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        return Response.created(builder.build()).entity(user).build();
    }

    @POST()
    @Path("refresh")
    @Produces("application/json")
    @ApiOperation(value = "Refresh an access token",
            notes = "Returns the application user with an updated refresh and access token",
            response = ApplicationUser.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 401, message = "Unauthorized, refresh token is expired", response = Error.class)
    })
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response refresh(@NotNull @FormParam("refresh_token") String refreshToken, @Context UriInfo uriInfo) {

        Authentication auth = new Authentication();
        try {
            auth = AuthenticationData.parseToken(refreshToken, TokenType.REFRESH);
        } catch (Exception ex) {
            logger.error("UNABLE TO GET USER FROM REFRESH TOKEN", ex);
        } catch (ThrowableError throwableError) {
            logger.error(throwableError.getError().getDevMessage(), throwableError);
            Error error = throwableError.getError();
            return Response.status(error.getStatusCode()).entity(error).build();
        }

        if (auth.isValid()) {
            //get the full user object
            ApplicationUser user = ApplicationUserData.getById(auth.getUserId());

            AuthenticationData.generateTokens(auth, user);
            logger.debug("{} {} {} has refreshed an access token with a refresh token.",
                    auth.getUserId(), auth.getEmail(), auth.getName());
            UriBuilder builder = uriInfo.getAbsolutePathBuilder();

            return Response.created(builder.build()).entity(user).build();
        }

        Error error = Error.unauthorized();
        error.setDetails(
                new Detail("Refresh token is expired or is NOT valid.", "refresh_token", refreshToken).toList()
        );
        return Response.status(error.getStatusCode()).entity(error).build();
    }

    //CREATE
    @POST()
    @Path("register")
    @Produces("application/json")
    @ApiOperation(value = "Create new user",
            notes = "Returns the newly created user if successfully registered",
            response = ApplicationUser.class
    )
    @ApiResponses(value = {
            @ApiResponse(code = 422, message = "User validation failure", response = Error.class)
    })
    @Consumes("application/json")
    public Response register(ApplicationUser in, @Context UriInfo uriInfo) {

        ApplicationUser out;
        //check to see if user object passes validation
        try {
            out = ApplicationUserData.create(in);
        } catch (ThrowableError ex) {
            logger.debug("CAN'T REGISTER USER {} {}", in.getEmail(), ex.getError().getDevMessage());
            Error error = ex.getError();
            return Response.status(error.getStatusCode()).entity(error).build();
        }

        //return newly created user
        logger.debug("Create single application user object at register controller." + out.toString());
        UriBuilder builder = uriInfo.getAbsolutePathBuilder();
        return Response.created(builder.build()).entity(out).build();

    }
}
