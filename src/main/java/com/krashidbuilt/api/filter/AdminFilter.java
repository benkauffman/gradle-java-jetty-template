package com.krashidbuilt.api.filter;

import com.krashidbuilt.api.util.ErrorResponse;
import com.krashidbuilt.api.model.Authentication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public class AdminFilter implements Filter {

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

        logger.debug("Destroy admin filter : " + ((filterConfig != null) ? filterConfig.toString() : null));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        // should have been created in the private filter
        Authentication auth = (Authentication) servletRequest.getAttribute("Auth");

        if (auth == null || !auth.isAdmin()) {
            // is not a valid admin
            ErrorResponse.forbidden(servletResponse);
            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }

}
