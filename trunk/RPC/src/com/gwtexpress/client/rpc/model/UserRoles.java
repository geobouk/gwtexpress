package com.gwtexpress.client.rpc.model;

public class UserRoles extends AbstractGLogixMetaData{
    String[] columnTitles = {"User Id","Role","User Role Id"};
    String[] columnNames = {"USER_ID","ROLE","USER_ROLE_ID"};
    boolean[] allTrues = {true,true,true};
    boolean[] visible = {true,true,false};
    boolean[] required = {true,true,true};
    int[] columnSizes = {20,20,20};
    char[] columnTypes = {N,V,N};
    String[] columnLookupNames = {"LOV:USER_ID","ROLE",null};
    
    public UserRoles() {
    }

    public String[] getColumnLookupNames() {
        return columnLookupNames;
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
        return "user_roles";
    }

    public String getServiceName() {
        return "UserRoles";
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
        return new int[] { 2 };
    }

    public boolean[] getRequired() {
        return required;
    }

    public int getRowIdIndex() {
        return 2;
    }
}
