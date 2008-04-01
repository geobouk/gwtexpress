package com.gwtexpress.client.rpc.model;


public class CustomersMetaData extends AbstractGLogixMetaData {

    String[] columnTitles = 
    { "Customer Number", "Customer Name", "Contact Last Name", 
      "Contact First Name", "Phone", "Bill To Address 1", "Bill To Address 2", 
      "City", "Bill To State", "Zip", "Bill To Country", 
      "Ship To Addressline1", "Ship To Addressline2", "Ship To City", 
      "Ship To State", "Ship To Postalcode", "Ship To Country", 
      "Salesrep Employee Number", "Credit Limit", "Start Date", "-", 
      "Email Address" };
    String[] columnNames = 
    { "customerNumber", "customerName", "contactLastName", "contactFirstName", 
      "phone", "b_addressLine1", "b_addressLine2", "b_city", "b_state", 
      "b_postalCode", "b_country", "s_addressLine1", "s_addressLine2", 
      "s_city", "s_state", "s_postalCode", "s_country", 
      "salesRepEmployeeNumber", "creditLimit", "start_date", "end_date", 
      "email_address" };

    boolean[] editableCols = 
    { false, true, true, true, true, true, true, true, true, true, true, true, 
      true, true, true, true, true, true, true, true, true, true };

    boolean[] queryCols = 
    { false, true, true, true, true, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, true, true, true, 
      true };

    boolean[] allTrues = 
    { true, true, true, true, true, true, true, true, true, true, true, true, 
      true, true, true, true, true, true, true, true, true, true };
    boolean[] allFalse = 
    { false, false, false, false, false, false, false, false, false, false, 
      false, false, false, false, false, false, false, false, false, false, 
      false, false };
    String[] allNulls = 
    { null, null, null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null };

    String[] columnSuggestNames = 
    { null, null, null, null, null, null, null, "CITIES", "STATES", null, null, 
      null, null, null, null, null, null, null, null, null, null, null };

    String[] columnLookupNames = 
    { null, null, null, null, null, null, null, null, null, null, null, null, 
      null, "CITIES", "STATES", null, null, null, null, null, null, null };

    boolean[] required = 
    { true, true, false, false, false, true, false, true, true, true, true, 
      false, false, false, false, false, false, false, false, true, false, 
      false };
    int[] columnSizes = 
    { 20, 50, 50, 50, 50, 50, 50, 50, 50, 15, 50, 50, 50, 50, 50, 15, 50, 11, 
      22, 19, 10, 80 };
    char[] columnTypes = 
    { N, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, V, N, T, D, V };


    public CustomersMetaData() {
    }

    public String[] getColumnSuggestNames() {
        return columnSuggestNames;
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
        return "customers";
    }

    public String getServiceName() {
        return "CustomersMetaData";
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public boolean[] getEditable() {
        return editableCols;
    }

    public boolean[] getVisible() {
        return allTrues;
    }

    public boolean[] getQueryable() {
        return queryCols;
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
