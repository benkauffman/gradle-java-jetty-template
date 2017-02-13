package com.krashidbuilt.api.filter;

import com.krashidbuilt.api.util.DateTime;
import com.krashidbuilt.api.util.ETagResponseWrapper;
import com.krashidbuilt.api.util.ErrorResponse;
import com.krashidbuilt.api.service.CacheService;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public class CacheFilter implements Filter {
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

        logger.debug("Destroy CacheFilter : " + ((filterConfig != null) ? filterConfig.toString() : null));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException,
            ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) req;
        HttpServletResponse servletResponse = (HttpServletResponse) res;


        if (servletRequest.getMethod().equalsIgnoreCase("GET")
                && servletRequest.getRequestURI().length() >= 6
                && servletRequest.getRequestURI().substring(0, 5).equalsIgnoreCase("/api/")) {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ETagResponseWrapper wrappedResponse = new ETagResponseWrapper(servletResponse, baos);
            filterChain.doFilter(servletRequest, wrappedResponse);

            byte[] bytes = baos.toByteArray();

            String token = CacheService.getMd5Digest(bytes);
            servletResponse.setHeader("ETag", token); // always store the ETag in the header


            String previousToken = servletRequest.getHeader("If-None-Match");

            logger.debug("NEW ETAG = {}", token);
            logger.debug("PREVIOUS ETAG = {}", previousToken);

            // compare previous token with current one
            if (previousToken != null && previousToken.equals(token)) {
                logger.debug("ETag match: returning 304 Not Modified");
                // use the same date we sent when we created the ETag the first time through

                if (servletRequest.getHeader("If-Modified-Since") != null
                        && !servletRequest.getHeader("If-Modified-Since").isEmpty()) {
                    Date lastModified = new Date(servletRequest.getDateHeader("If-Modified-Since"));
                    servletResponse.setDateHeader("Last-Modified", lastModified.getTime());
                } else {
                    servletResponse.setDateHeader("Last-Modified", DateTime.getEpochMillis());
                }

                ErrorResponse.notModified(servletResponse);

            } else {
                // first time through - set last modified time to now
                servletResponse.setDateHeader("Last-Modified", DateTime.getEpochMillis());

                logger.debug("Writing body content");
                servletResponse.setContentLength(bytes.length);
                ServletOutputStream sos = servletResponse.getOutputStream();
                sos.write(bytes);
                sos.flush();
                sos.close();
            }
        } else {
            //ignore because it's not a GET request and shouldn't be cached
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
}
