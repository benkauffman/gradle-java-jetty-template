package com.krashidbuilt.api.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;

import static com.krashidbuilt.api.service.Settings.getStringSetting;

/**
 * Created by Ben Kauffman on 10/5/2016.
 */

public class ConnectionPool implements Runnable {
    private static Logger logger = LogManager.getLogger();
    private static DataSource datasource;

    public static synchronized DataSource getDataSource() {
        if (datasource == null) {
            logger.debug("CREATE NEW CONNECTION POOL");
            HikariConfig config = new HikariConfig();

            config.setJdbcUrl(getStringSetting("sql.connection"));
            config.setUsername(getStringSetting("sql.user"));
            config.setPassword(getStringSetting("sql.pass"));

            // pool efficiency formula
            // ((cpu core count * 2) + effective spindle count)
            // ((4 * 2) + 1) = 9 ... bump it to 10 to handle heavy loads

            config.setMinimumIdle(6);
            config.setMaximumPoolSize(10);
            config.setAutoCommit(true);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");


            config.setLeakDetectionThreshold(2500); // 2.5 seconds
            config.setConnectionTimeout(5000); // 5 seconds


            //https://support.rackspace.com/how-to/how-to-change-the-mysql-timeout-on-a-server/
            //idleTimeout and maxLifeTime settings should be one minute less than the wait_timeout of MySQL
            //default mysql is 28800 seconds (8 hours)
            //SHOW SESSION VARIABLES LIKE "%wait%";

            config.setIdleTimeout(900000); // 15 minutes
            config.setMaxLifetime(28440000); // 7.9 hours

            datasource = new HikariDataSource(config);

        }

        return datasource;
    }

    public void run() {
        //required to be a runnable
    }

    public void print() {
    }
}

