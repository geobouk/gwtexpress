package com.gwtexpress.server;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.Calendar;


public class AccessManager {
    DBMetaDataObject dbMetaDataObject;

    public AccessManager(DBMetaDataObject dbMetaDataObject) {
        this.dbMetaDataObject = dbMetaDataObject;
    }
    static final String viewSql = 
        "SELECT 'Y' FROM user_roles u, role_functions f WHERE f.ROLE_NAME = u.ROLE AND (f.SERVICE_NAME = ? OR f.SERVICE_NAME='*') AND u.USER_ID=? AND VIEW_ALLOWED='Y'";

    public boolean isViewAllowed(String serviceName, 
                                 BigDecimal userID) throws SQLException {
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(viewSql);
        ps.setString(1, serviceName);
        ps.setBigDecimal(2, userID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

    public boolean isCreateAllowed(String serviceName, 
                                 BigDecimal userID) throws SQLException {
        String sql = 
            "SELECT 'Y' FROM user_roles u, role_functions f WHERE f.ROLE_NAME = u.ROLE AND (f.SERVICE_NAME = ? OR f.SERVICE_NAME='*') AND u.USER_ID=? AND CREATE_ALLOWED='Y'";
        Timestamp sysdate = 
            new Timestamp(Calendar.getInstance().getTimeInMillis());
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, serviceName);
        ps.setBigDecimal(2, userID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

    public boolean isEditAllowed(String serviceName, 
                                 BigDecimal userID) throws SQLException {
        String sql = 
            "SELECT 'Y' FROM user_roles u, role_functions f WHERE f.ROLE_NAME = u.ROLE AND (f.SERVICE_NAME = ? OR f.SERVICE_NAME='*') AND u.USER_ID=? AND EDIT_ALLOWED='Y'";
        Timestamp sysdate = 
            new Timestamp(Calendar.getInstance().getTimeInMillis());
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, serviceName);
        ps.setBigDecimal(2, userID);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

}
