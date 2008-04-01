package com.gwtexpress.client.rpc.model;

public class UserAccounts extends AbstractGLogixMetaData {

    String[] columnTitles = 
    { "User ID", "User Name", "First Name", "Last Name", "Display Name", 
      "Password Hash", "Creation Date", "Last Update Date", 
      "Last Password Changed", "Emp No", "Start Date", "End Date" };
    String[] columnNames = 
    { "USER_ID", "USER_NAME", "FIRST_NAME", "LAST_NAME", "DISPLAY_NAME", 
      "PASSWORD_HASH", "CREATION_DATE", "LAST_UPDATE_DATE", 
      "LAST_PASSWORD_CHANGED", "EMP_NO", "START_DATE", "END_DATE" };
    boolean[] allTrues = 
    { true, true, true, true, true, true, true, true, true, true, true, true };
    boolean[] allFalse = 
    { false, false, false, false, false, false, false, false, false, false, 
      false, false };
    String[] allNulls = 
    { null, null, null, null, null, null, null, null, null, null, null, null };
    boolean[] required = 
    { true, true, true, true, true, true, true, true, false, false, true, 
      false };
    int[] columnSizes = { 20, 150, 80, 80, 100, 255, 19, 19, 19, 20, 19, 19 };
    char[] columnTypes = { N, V, V, V, V, V, T, T, T, V, T, T };

    boolean[] downloadableCols = 
    { true, true, true, true, true, false, true, false, false, true, true, 
      true };

    boolean[] visible = 
    { true, true, true, true, true, false, true, false, false, true, true, 
      true };
    boolean[] editable = 
    { false, true, true, true, true, false, false, false, false, true, true, 
      true };
    boolean[] updatable = 
    { false, false, true, true, true, false, false, false, false, true, true, 
      true };


    public UserAccounts() {
    }

    public boolean[] getResultColumns() {
        return visible;
    }

    public int[] getColumnWidth() {
        return columnSizes;
    }

    public char[] getColumnTypes() {
        return columnTypes;
    }

    public boolean[] getDownloadable() {
        return downloadableCols;
    }

    public String[] getColumnTitles() {
        return columnTitles;
    }

    public String getTableName() {
        return "user_accounts";
    }

    public String getServiceName() {
        return "UserAccounts";
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean[] getEditable() {
        return editable;
    }

    public boolean[] getUpdatable() {
        return updatable;
    }

    public boolean[] getVisible() {
        return visible;
    }

    public boolean[] getQueryable() {
        return visible;
    }

    public int[] getPkColumnIndexs() {
        return new int[] { 0 };
    }

    public boolean[] getRequired() {
        return required;
    }

    public int getRowIdIndex() {
        return 0;
    }
}
