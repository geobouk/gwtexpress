package com.gwtexpress.server.util;


import com.google.gwt.user.client.rpc.SerializableException;

import com.gwtexpress.server.DBMetaDataObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Log {
    static DBMetaDataObject dbMetaDataObject;
    static Connection con;
    static PreparedStatement ps;

    private Log() {
    }

    public static void start() {
        try {
            if (dbMetaDataObject != null)
                stop();
            dbMetaDataObject = new DBMetaDataObject();
            dbMetaDataObject.startTxn();
            con = dbMetaDataObject.getConnection();
            ps = 
 con.prepareStatement("INSERT INTO log_table (log_message) values (?)");
        } catch (SQLException e) {
            // TODO
        } catch (SerializableException e) {
            // TODO
        }
    }

    public static void stop() {
        try {
            if (ps != null)
                ps.close();
            ps = null;
        } catch (SQLException e) {
        }
        if (dbMetaDataObject != null)
            dbMetaDataObject.endTxn();
        dbMetaDataObject = null;
        con = null;
    }

    public static void debug(String str) {
        if (dbMetaDataObject == null)
            return;
        try {
            ps.setString(1, str);
            ps.executeUpdate();
        } catch (SQLException e) {
            // TODO
        }
        try {
            con.commit();
        } catch (SQLException e) {
            // TODO
        }
    }
}
