package com.gwtexpress.client.rpc.model;

public class MyAccountMetaData extends AbstractGLogixMetaData {

    String[] columnTitles = 
    { "User ID", "First Name", "Last Name", "Display Name", "Old Password", 
      "New Password", "Confirm New Password" };
    String[] columnNames = 
    { "USER_ID", "FIRST_NAME", "LAST_NAME", "DISPLAY_NAME", "OLD_PASSWORD", 
      "NEW_PASSWORD1", "NEW_PASSWORD2" };

    boolean[] required = { true, true, true, true, false, false, false };
    int[] columnSizes = { 20, 80, 80, 100, 30, 30, 30 };
    char[] columnTypes = { N, V, V, V, P, P, P };

    boolean[] visible = { false, true, true, true, true, true, true };


    public MyAccountMetaData() {
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
        return visible;
    }

    public String[] getColumnTitles() {
        return columnTitles;
    }

    public String getTableName() {
        return "user_accounts";
    }

    public String getServiceName() {
        return "MyAccountMetaData";
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean[] getEditable() {
        return visible;
    }

    public boolean[] getUpdatable() {
        return visible;
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
