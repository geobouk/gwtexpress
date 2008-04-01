package com.gwtexpress.client.rpc.model;

import java.util.ArrayList;

public class LookupValues extends AbstractGLogixMetaData {

    public LookupValues() {
    }

    public String[] getColumnTitles() {
        return new String[] { "Lookup Type", "Lookup Value", "Lookup Meaning", 
                              "Rowid", "Start Date", "End Date" };
    }

    public String getTableName() {
        return "tm_lookup_values";
    }

    public String[] getColumnNames() {
        return new String[] { "LOOKUP_TYPE", "LOOKUP_VALUE", "LOOKUP_MEANING", 
                              "ROWID", "START_DATE", "END_DATE" };
    }

    public String[] getSelectColumnNames() {
        return new String[] { "LOOKUP_VALUE", "LOOKUP_MEANING" };
    }

    public String[] getOrderByColumnNames() {
        return new String[] { "DISPLAY_SEQ", "LOOKUP_MEANING" };
    }
    
    public boolean[] getEditable() {
        return new boolean[] { true, true, true, false, true, true };
    }

    public boolean[] getVisible() {
        return new boolean[] { true, true, true, false, true, true };
    }

    public boolean[] getQueryable() {
        return new boolean[] { true, true, true, false, true, true };
    }

    public int[] getPkColumnIndexs() {
        return new int[] { 3 };
    }

    public int[] getColumnWidth() {
        return new int[] { 30, 80, 250, 20, 11, 20 };
    }

    public String getServiceName() {
        return "LookupValues";
    }

    public int getRowIdIndex() {
        return 3;
    }

    public boolean[] getRequired() {
        return new boolean[] { true, true, true, true, true, false };
    }

    public char[] getColumnTypes() {
        return new char[] { 'V', 'V', 'V', 'N', 'D', 'T' };
    }
}
