package com.krashidbuilt.api;

import org.junit.Test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Enumeration;

/**
 * Created by Ben Kauffman on 10/10/2016.
 */
public class BootstrapTest {

    private ServletConfig config;

    public BootstrapTest() {
        config = new ServletConfig() {
            @Override
            public String getServletName() {
                return null;
            }

            @Override
            public ServletContext getServletContext() {
                return null;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return null;
            }
        };
    }


    @Test
    public void init() throws ServletException {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.init(config);
    }
}
