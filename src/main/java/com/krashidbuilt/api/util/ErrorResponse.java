package com.krashidbuilt.api.util;

import com.google.gson.Gson;
import com.krashidbuilt.api.model.Error;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Ben Kauffman on 6/28/16.
 */
public final class ErrorResponse {
    private static Gson gson = new Gson();
    private static Logger logger = LogManager.getLogger();
    private ErrorResponse() {

    }

    public static void badRequest(ServletResponse servletResponse) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        PrintWriter writer = resp.getWriter();
        writer.write(gson.toJson(Error.badRequest()));
        writer.close();
    }


    public static void notModified(ServletResponse servletResponse) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        PrintWriter writer = resp.getWriter();
        writer.write(gson.toJson(Error.notModified()));
        writer.close();
    }


    public static void serviceUnavailable(ServletResponse servletResponse) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        PrintWriter writer = resp.getWriter();
        writer.write(gson.toJson(Error.maintenance()));
        writer.close();
    }

    public static void unauthorized(ServletResponse servletResponse) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = resp.getWriter();
        writer.write(gson.toJson(Error.unauthorized()));
        writer.close();

    }

    public static void forbidden(ServletResponse servletResponse) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter writer = resp.getWriter();
        writer.write(gson.toJson(Error.forbidden()));
        writer.close();
    }

    public static void httpVersionNotSupported(ServletResponse servletResponse) throws IOException {
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.setHeader("Content-Type", "application/json");
        resp.setStatus(HttpServletResponse.SC_HTTP_VERSION_NOT_SUPPORTED);
        PrintWriter writer = resp.getWriter();
        writer.write(gson.toJson(Error.security()));
        writer.close();
    }

    public static void internalServerError(ServletResponse servletResponse) {
        try {
            HttpServletResponse resp = (HttpServletResponse) servletResponse;
            resp.setHeader("Content-Type", "application/json");
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter writer = resp.getWriter();
            writer.write(gson.toJson(Error.internalServerError()));
            writer.close();
        } catch (Exception ex) {
            logger.fatal("UNABLE TO RETURN INTERNAL SERVER ERROR", ex);
        }

    }
}
