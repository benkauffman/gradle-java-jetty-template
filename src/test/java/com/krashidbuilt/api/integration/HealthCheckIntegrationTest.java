package com.krashidbuilt.api.integration;

import com.krashidbuilt.api.model.ThrowableError;
import com.krashidbuilt.api.service.Settings;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ben Kauffman on 2/3/2017.
 */
public class HealthCheckIntegrationTest {

    private static final String BASE_URL = Settings.getStringSetting("test.baseUrl");
    private static Logger logger = LogManager.getLogger();
    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        protected void failed(Throwable e, Description description) {
            logger.info("" + description.getDisplayName() + " failed " + e.getMessage());
            super.failed(e, description);
        }

    };
    private HttpClient client = HttpClientBuilder.create().build();

    @Before
    public void setup() throws ThrowableError {
        logger.debug("SETUP TEST");
    }

    @After
    public void destroy() throws ThrowableError {
        logger.debug("DESTROY TEST");
    }


    @Test
    public void testHealthCheck() throws Exception {
        logger.debug("\n\n\nINTEGRATION TEST SERVER HEALTH CHECK");
        HttpGet get = new HttpGet(BASE_URL + "health");
        HttpResponse response = client.execute(get);

        int responseCode = response.getStatusLine().getStatusCode();
        //successful get
        assertEquals(200, responseCode, 0);


        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        logger.info("RETURNED=" + result.toString());
        assertEquals("", result.toString());
    }
}
