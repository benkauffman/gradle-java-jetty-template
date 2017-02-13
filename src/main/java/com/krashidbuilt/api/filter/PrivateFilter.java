package com.krashidbuilt.api.filter;

import com.krashidbuilt.api.data.AuthenticationData;
import com.krashidbuilt.api.util.DateTime;
import com.krashidbuilt.api.util.ErrorResponse;
import com.krashidbuilt.api.model.Authentication;
import com.krashidbuilt.api.model.ThrowableError;
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
public class PrivateFilter implements Filter {

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

        logger.debug("Destroy private filter : " + ((filterConfig != null) ? filterConfig.toString() : null));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        Authentication auth = new Authentication();

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null) {
            String[] bearer = authHeader.split("Bearer ");
            if (bearer.length == 2) {
                try {
                    auth = AuthenticationData.parseToken(bearer[1], AuthenticationData.TokenType.ACCESS);
                } catch (Exception ex) {
                    logger.error("JWT TOKEN IS NOT VALID OR IS EXPIRED", ex);
                } catch (ThrowableError throwableError) {
                    logger.error(throwableError.getError().getDevMessage(), throwableError);
                }
            }

        }

        if (!auth.isValid()) {
            // is not a valid user
            ErrorResponse.unauthorized(servletResponse);
            return;
        }

        if (auth.isAdmin() && httpRequest.getRequestURL().toString().contains("/api/private/user/")) {
            // admin's do not have access to user endpoints
            ErrorResponse.forbidden(servletResponse);
            return;
        }

        String uuid = "[" + UUID.randomUUID().toString() + "]";
        logger.debug(uuid + " Private request made by " + auth.getName() + " started " + DateTime.nowToString());

        servletRequest.setAttribute("Auth", auth);

        filterChain.doFilter(servletRequest, servletResponse);

        logger.debug(uuid + " Private request made by " + auth.getName() + " completed " + DateTime.nowToString());
    }

}
