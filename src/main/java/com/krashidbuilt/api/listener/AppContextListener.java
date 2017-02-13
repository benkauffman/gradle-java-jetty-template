package com.krashidbuilt.api.listener;

import com.krashidbuilt.api.util.DateTime;
import com.krashidbuilt.api.server.HealthCheckResource;
import com.krashidbuilt.api.service.MySQL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.Response;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ben Kauffman on 9/30/2016.
 * to add  a timertask http://stacktips.com/tutorials/java/servlets/running-infinite-timertask-in-java-servlet
 */

public class AppContextListener implements ServletContextListener {

    private static Logger logger = LogManager.getLogger();

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

        logger.info("AppContextListener has been destroyed.");
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        logger.info("AppContextListener has been initialized.");

        logger.info("CONNECTION POOL INITIALIZED");
        MySQL db = new MySQL();
        db.cleanUp();

        TimerTask healthCheck = new HealthCheckTimerTask();
        Timer healthCheckTimer = new Timer();
        healthCheckTimer.schedule(healthCheck, 0, 300000); //every 5 minutes

    }

    static class HealthCheckTimerTask extends TimerTask {

        @Override
        public void run() {
            System.out.println("[" + DateTime.nowToString() + "]" + " EXECUTING HEALTH CHECK TIMER TASK");
            HealthCheckResource resource = new HealthCheckResource();
            Response response = resource.check();
            System.out.println("[" + DateTime.nowToString() + "]"
                    + " COMPLETED HEALTH CHECK TIMER TASK [STATUS CODE = " + response.getStatus() + "]");
        }
    }
}
