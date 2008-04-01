package com.gwtexpress.client.rpc.model;

import java.util.ArrayList;

public class LookupsMetaData extends AbstractGLogixMetaData {

    public LookupsMetaData() {
    }

    public String[] getColumnTitles() {
        return new String[] { "Lookup Type", "Lookup Value", "Lookup Meaning", "Display Seq",
                              "Rowid", "Start Date", "End Date" };
    }

    public String getTableName() {
        return "tm_lookup_values";
    }

    public String[] getColumnNames() {
        return new String[] { "LOOKUP_TYPE", "LOOKUP_VALUE", "LOOKUP_MEANING", "DISPLAY_SEQ",
                              "ROWID", "START_DATE", "END_DATE",  };
    }

    public boolean[] getEditable() {
        return new boolean[] { true, true, true, true, false, true, true };
    }

    public boolean[] getUpdatable() {
        return new boolean[] { false, false, true, true, false, false, true };
    }

    public boolean[] getVisible() {
        return new boolean[] { true, true, true, true, false, true, true };
    }

    public boolean[] getQueryable() {
        return new boolean[] { true, true, true, false, false, true, true };
    }

    public int[] getPkColumnIndexs() {
        return new int[] { 4 };
    }

    public int[] getColumnWidth() {
        return new int[] { 30, 80, 250, 11, 20, 11, 20 };
    }

    public String getServiceName() {
        return "Lookups";
    }

    public int getRowIdIndex() {
        return 4;
    }

    public boolean[] getRequired() {
        return new boolean[] { true, true, true, false, true, true, false };
    }

    public char[] getColumnTypes() {
        return new char[] { 'V', 'V', 'V', 'N','N', 'D', 'T' };
    }


}
