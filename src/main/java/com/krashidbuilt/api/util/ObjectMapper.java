package com.krashidbuilt.api.util;

import com.krashidbuilt.api.model.ApplicationUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ben Kauffman on 1/15/2017.
 */
public final class ObjectMapper {

    private static Logger logger = LogManager.getLogger();

    private ObjectMapper() {

    }

    public static ApplicationUser applicationUser(ResultSet rs) throws SQLException {
        ApplicationUser object = new ApplicationUser();

        try {
            object.setId(rs.getInt("id"));
            object.setFirstName(rs.getString("first_name"));
            object.setLastName(rs.getString("last_name"));
            object.setEmail(rs.getString("email"));
            object.setUsername(rs.getString("username"));

            //don't display password information for any user ... EVER!

            object.setCreated(rs.getString("created"));
            object.setUpdated(rs.getString("updated"));

        } catch (SQLException ex) {
            logger.error("UNABLE TO GET APPLICATION USER FROM THE RESULT SET ", ex);
        }

        return object;
    }


    public static HashMap<String, String> keyStore(ResultSet rs) throws SQLException {
        HashMap<String, String> map = new HashMap<String, String>();
        while (rs.next()) {
            map.put(rs.getString("data_key"), rs.getString("data_value"));
        }
        return map;
    }


    public static List<HashMap<String, Object>> convertResultSetToList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();

        while (rs.next()) {
            HashMap<String, Object> row = new HashMap<String, Object>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }


    @SuppressWarnings("PMD.EmptyCatchBlock")
    public static boolean isNull(String columnName, ResultSet rs) {
        String value = null;
        try {
            value = rs.getString(columnName);
        } catch (SQLException e) {
            //ignore error
//            logger.debug(e);
        }

        return value == null;
    }

}
