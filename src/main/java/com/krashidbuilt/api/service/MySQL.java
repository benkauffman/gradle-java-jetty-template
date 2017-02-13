package com.krashidbuilt.api.service;


import com.krashidbuilt.api.util.DateTime;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Ben Kauffman on 2/9/2016.
 */

@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION")
public class MySQL {

    private static Logger logger = LogManager.getLogger();

    private Connection conn;
    private Statement stmt;
    private PreparedStatement pStmt;
    private CallableStatement cStmt;
    private ResultSet rs;

    public MySQL() {
        conn = createConnection();
    }

    private static Connection createConnection() {
        long poolStarted = DateTime.getEpochMillis();
        long poolStopped = DateTime.getEpochMillis();

        Connection conn = null;
        try {
            conn = ConnectionPool.getDataSource().getConnection();

            poolStopped = DateTime.getEpochMillis();
        } catch (SQLException e) {
            logger.fatal("Unable to get connection from connection pool...", e);
            //last attempt to manually establish a connection
            conn = createConnectionNoPool();
        }
        logger.debug("Pool connection time took " + (poolStopped - poolStarted) + "ms");
        return conn;
    }

    private static Connection createConnectionNoPool() {
        //System.out.print("Create MySQL Connection:");
        Connection conn = null;
        try {

            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(
                    Settings.getStringSetting("sql.connection"),
                    Settings.getStringSetting("sql.user"),
                    Settings.getStringSetting("sql.pass"));

            return conn;
        } catch (Exception e) {
            logger.fatal("Error connecting to the database...", e);
        }

        return conn;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public PreparedStatement getpStmt() throws SQLException {
        return pStmt;
    }

    public void setpStmt(PreparedStatement pStmt) {
        this.pStmt = pStmt;
    }

    public CallableStatement getcStmt() {
        return cStmt;
    }

    public void setcStmt(CallableStatement cStmt) {
        this.cStmt = cStmt;
    }

    public ResultSet getRs() {
        return rs;
    }

    public void setRs(ResultSet rs) {
        this.rs = rs;
    }

    public void cleanUp() {

        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            logger.warn("Error when trying to close result set!", e);
        }

        try {
            if (pStmt != null) {
                pStmt.close();
            }
        } catch (SQLException e) {
            logger.warn("Error when trying to close prepared statement!", e);
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            logger.warn("Error when trying to close statement!", e);
        }

        try {
            if (cStmt != null) {
                cStmt.close();
            }
        } catch (SQLException e) {
            logger.warn("Error when trying to close callable statement!", e);
        }

        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            logger.warn("Error when trying to close database connection!", e);
        }
    }

    public int executeUpdateGetLastInsertedId() {
        return executeUpdateGetLastInsertedId(null);
    }

    public int executeUpdateGetLastInsertedId(String notPreparedSqlStatement) {
        int res = -1;
        try {
            if (pStmt != null) {

                //this IS a prepared statement
                pStmt.executeUpdate();
                rs = pStmt.getGeneratedKeys();
            } else {
                stmt = conn.createStatement();
                if (stmt != null && notPreparedSqlStatement != null) {
                    //this IS normal statement
                    stmt.executeUpdate(notPreparedSqlStatement, Statement.RETURN_GENERATED_KEYS);
                    rs = stmt.getGeneratedKeys();
                }
            }
            //get the newly created id
            if (rs != null && rs.next()) {
                res = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            logger.debug("Prepared Statement = {}", pStmt);
            logger.debug("Sql Statement = {}", stmt);
            logger.warn("Unable to get last inserted id", e);
        }
        return res;
    }

}
