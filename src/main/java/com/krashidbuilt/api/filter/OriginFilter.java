package com.krashidbuilt.api.filter;

import com.krashidbuilt.api.util.DateTime;
import com.krashidbuilt.api.util.ErrorResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public class OriginFilter implements Filter {

    private static Logger logger = LogManager.getLogger();
    private FilterConfig filterConfig;

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void init(FilterConfig filterConfig2) throws ServletException {
        setFilterConfig(filterConfig2);
    }

    @Override
    public void destroy() {

        logger.debug("Destroy OriginFilter : " + ((filterConfig != null) ? filterConfig.toString() : null));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        try {
            HttpServletRequest request = ((HttpServletRequest) servletRequest);
            String url = request.getRequestURL().toString();

            if (url.contains("/api/")) {
                String uuid = "[" + UUID.randomUUID().toString() + "]";
                long start = DateTime.getEpochMillis();
                logger.debug(uuid + " Origin request started at " + DateTime.nowToString());

                String secured = request.isSecure() ? "SECURED" : "UNSECURED ";
                String verb = request.getMethod();
                logger.debug(secured + verb + " REQUEST AT " + url);


                filterChain.doFilter(servletRequest, servletResponse);
                logger.debug(uuid + " Origin request completed in " + (DateTime.getEpochMillis() - start) +
                        "ms at " + DateTime.nowToString());
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }

        } catch (Exception ex) {
            if (ex.getMessage() != null
                    && ex.getMessage().contains("com.fasterxml.jackson.databind.JsonMappingException")) {
                // this is likely malformed JSON and should be treated as such
                ErrorResponse.badRequest(servletResponse);
            } else {
                logger.error("UNHANDLED EXCEPTION", ex);
                ErrorResponse.internalServerError(servletResponse);
            }

        }
    }
}
