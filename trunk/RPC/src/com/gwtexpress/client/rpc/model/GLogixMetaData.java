package com.gwtexpress.client.rpc.model;

import java.util.ArrayList;

public interface GLogixMetaData {
    static final char V = 'V';
    static final char P = 'P';
    static final char N = 'N';
    static final char D = 'D';
    static final char T = 'T';

    int[] getIndexByNames(String[] colNames);

    String[] getOrderByColumnNames();
    
    int getColumnIndex(String colName);
    
    String[] getChildMetaDataNames();

    ArrayList<int[]> getChildMap(String childName);

    int getColumnCount();

    String[] getColumnTitles();

    char[] getColumnTypes();

    int[] getColumnWidth();

    String getServiceName();

    String[] getColumnNames();

    String[] getSelectColumnNames();

    void setSelectColumnNames(String[] columns);

    String[] getColumnLookupNames();

    String[] getColumnSuggestNames();

    String getTableName();

    boolean[] getEditable();

    boolean[] getVisible();

    boolean[] getDownloadable();

    boolean[] getQueryable();

    boolean[] getRequired();

    boolean[] getResultColumns();

    int[] getResultColumnIndexes();

    int[] getQueryableColumnIndexes();

    int[] getVisibleColumnIndexes();

    int[] getPkColumnIndexs();

    String[] getPkColumnNames();

    String[] createRow();

    String[] createBlankRow();

    int getRowIdIndex();

    ArrayList<String[]> getSuggestFields();

    boolean[] getUpdatable();
}
