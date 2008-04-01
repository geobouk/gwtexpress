package com.gwtexpress.client.rpc.model;

public class RolesFunctions extends AbstractGLogixMetaData {

    String[] columnTitles = 
    { "Role Function ID", "Role", "Service", "View Allowed (Y/N)", 
      "Edit Allowed (Y/N)", "Create Allowed (Y/N)" };

    boolean[] allTrues = { true, true, true, true, true, true };
    boolean[] visible = { false, true, true, true, true, true };
    boolean[] updateable = { false, false, false, true, true, true };

    String[] allNulls = { null, null, null, null, null, null };

    String[] columnNames = 
    { "ROLE_FUNCTION_ID", "ROLE_NAME", "SERVICE_NAME", "VIEW_ALLOWED", 
      "EDIT_ALLOWED", "CREATE_ALLOWED" };

    boolean[] required = { true, true, true, true, true, true };
    int[] columnSizes = { 20, 20, 30, 1, 1, 1 };
    char[] columnTypes = { N, V, V, V, V, V };


    public RolesFunctions() {
    }

    public boolean[] getUpdatable() {
        return updateable;
    }

    public int[] getColumnWidth() {
        return columnSizes;
    }

    public char[] getColumnTypes() {
        return columnTypes;
    }

    public String[] getColumnTitles() {
        return columnTitles;
    }

    public String getTableName() {
        return "role_functions";
    }

    public String getServiceName() {
        return "RolesFunctions";
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean[] getEditable() {
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
