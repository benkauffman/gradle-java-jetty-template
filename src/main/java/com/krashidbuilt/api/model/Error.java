package com.krashidbuilt.api.model;

import com.google.gson.Gson;
import com.krashidbuilt.api.util.DateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */


public class Error implements Serializable {

    private static Logger logger = LogManager.getLogger();

    private String devMessage;
    private String userMessage;
    private int errorCode;
    private int statusCode;
    private String stamp;
    private List<Detail> details;

    public Error() {
        devMessage = "";
        userMessage = "";
        errorCode = -1;
        statusCode = -1;
        stamp = DateTime.nowToString();
    }

    public static Error badRequest() {
        logger.debug("Bad request! entity is malformed or an invalid type.");
        Error error = new Error();
        error.setDevMessage("Bad request! entity is malformed or an invalid type.");
        error.setStatusCode(400);
        error.setErrorCode(400);

        return error;
    }

    public static Error validation() {
        logger.debug("Validation error(s) detected.");
        Error error = new Error();
        error.setDevMessage("Validation error(s) detected.");
        error.setStatusCode(422);
        error.setErrorCode(422);

        return error;
    }

    public static Error duplicate(String conflict) {
        logger.debug("Duplicate detected, \"" + conflict + "\" is already in use.");
        Error error = new Error();
        error.setDevMessage("Duplicate detected, \"" + conflict + "\" is already in use.");
        error.setStatusCode(409);
        error.setErrorCode(409);

        return error;
    }

    public static Error notFound(String type, int id) {
        logger.debug("No " + type + " found with id : " + id + " : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("\"" + type + "\" with id \"" + id + "\" was not found.");
        error.setStatusCode(404);
        error.setErrorCode(404);

        return error;
    }

    public static Error notFound(String type, String id) {
        logger.debug("No " + type + " found with identifier : " + id + " : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("\"" + type + "\" with identifier \"" + id + "\" was not found.");
        error.setStatusCode(404);
        error.setErrorCode(404);

        return error;
    }

    public static Error notModified() {
        logger.debug("The entity hasn't been modified since last request.");
        Error error = new Error();
        error.setDevMessage("The entity has not been modified.");
        error.setStatusCode(304);
        error.setErrorCode(304);

        return error;
    }

    public static Error maintenance() {
        logger.debug("The application is currently down for maintenance. Please try again later...");
        Error error = new Error();
        error.setDevMessage("The application is currently down for maintenance. Please try again later...");
        error.setStatusCode(503);
        error.setErrorCode(503);

        return error;
    }

    public static Error xss() {
        logger.debug("A potentially dangerous input value was detected.");
        Error error = new Error();
        error.setDevMessage("A potentially dangerous input value was detected.");
        error.setStatusCode(412);
        error.setErrorCode(412);

        return error;
    }

    public static Error forbidden() {
        logger.debug("You are not authorized to access this resource : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("You are not authorized to access this resource");
        error.setStatusCode(403);
        error.setErrorCode(403);

        return error;
    }

    public static Error unauthorized() {
        logger.debug("You are not authenticated! : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("You are not authenticated, please login!");
        error.setStatusCode(401);
        error.setErrorCode(401);

        return error;
    }

    public static Error security() {
        logger.debug("Connection is not secure! : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("Your connection is not secure.");
        error.setStatusCode(505);
        error.setErrorCode(505);

        return error;
    }

    public static Error unprocessable() {
        logger.debug("Object was not processed as expected! : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("Object was not processed as expected.");
        error.setStatusCode(422);
        error.setErrorCode(422);

        return error;
    }

    public static Error internalServerError() {
        logger.error("INTERNAL SERVER ERROR! : " + DateTime.nowToString());
        Error error = new Error();
        error.setDevMessage("We've encountered an unexpected internal error.");
        error.setStatusCode(500);
        error.setErrorCode(500);

        return error;
    }

    public static Error fromString(String jsonObject) {
        return new Gson().fromJson(jsonObject, Error.class);
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setBothMessages(String message) {
        devMessage = message;
        userMessage = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getDevMessage() {
        return devMessage;
    }

    public void setDevMessage(String devMessage) {
        this.devMessage = devMessage;
    }

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
    }


}
