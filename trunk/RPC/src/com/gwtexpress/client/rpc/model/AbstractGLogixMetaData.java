package com.gwtexpress.client.rpc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class AbstractGLogixMetaData implements GLogixMetaData {

    private String[] selectColumnNames;
    private String[] columnNames;
    private boolean[] visible;
    private boolean[] results;
    private HashMap<String, Integer> colNameMap;

    public abstract int[] getColumnWidth();

    public abstract char[] getColumnTypes();

    public int getColumnCount() {
        if (columnNames == null)
            columnNames = getColumnNames();
        return columnNames.length;
    }

    public boolean[] getDownloadable() {
        return null;
    }
    public String[] getOrderByColumnNames() {
        return null;
    }
    
    public abstract String[] getColumnTitles();

    public abstract String getTableName();

    public abstract String getServiceName();

    public abstract String[] getColumnNames();

    public abstract boolean[] getEditable();

    public boolean[] getUpdatable() {
        return getEditable();
    }

    public abstract boolean[] getVisible();

    public abstract boolean[] getQueryable();
    boolean[] queryable;
    int[] queryableColumnIndexes, visibleColumnIndexes, resultColumnIndexes;

    public boolean[] getResultColumns() {
        if (results == null)
            results = getVisible();
        return results;
    }

    public int[] getResultColumnIndexes() {
        int count = 0;
        if (results == null)
            results = getResultColumns();
        if (resultColumnIndexes == null) {
            for (int i = 0; i < results.length; i++) {
                if (results[i])
                    count++;
            }
            resultColumnIndexes = new int[count];
            count = 0;
            for (int i = 0; i < results.length; i++) {
                if (results[i]) {
                    resultColumnIndexes[count++] = i;
                }
            }
        }
        return resultColumnIndexes;
    }

    public int getColumnIndex(String colName) {
        if (colNameMap == null) {
            colNameMap = new HashMap<String, Integer>();
            if (columnNames == null) {
                columnNames = getColumnNames();
            }
            for (int i = 0; i < columnNames.length; i++) {
                colNameMap.put(columnNames[i], new Integer(i));
            }
        }
        Integer idx = colNameMap.get(colName);
        if (idx != null) {
            return idx.intValue();
        } else {
            return -1;
        }
    }

    public int[] getIndexByNames(String[] colNames) {
        if (colNames == null)
            return null;
        int[] indexes = new int[colNames.length];
        for (int i = 0; i < colNames.length; i++) {
            indexes[i] = getColumnIndex(colNames[i]);
        }
        return indexes;
    }

    public int[] getQueryableColumnIndexes() {
        int count = 0;
        if (queryable == null)
            queryable = getQueryable();
        if (queryableColumnIndexes == null) {
            for (int i = 0; i < queryable.length; i++) {
                if (queryable[i])
                    count++;
            }
            queryableColumnIndexes = new int[count];
            count = 0;
            for (int i = 0; i < queryable.length; i++) {
                if (queryable[i]) {
                    queryableColumnIndexes[count++] = i;
                }
            }
        }
        return queryableColumnIndexes;
    }

    public int[] getVisibleColumnIndexes() {
        int count = 0;
        if (visible == null)
            visible = getVisible();
        if (visibleColumnIndexes == null) {
            for (int i = 0; i < visible.length; i++) {
                if (visible[i])
                    count++;
            }
            visibleColumnIndexes = new int[count];
            count = 0;
            for (int i = 0; i < visible.length; i++) {
                if (visible[i]) {
                    visibleColumnIndexes[count++] = i;
                }
            }
        }
        return visibleColumnIndexes;
    }

    public abstract int[] getPkColumnIndexs();
    private String[] pkColumnNames;
    private int[] pkColumnIndexs;

    public String[] getPkColumnNames() {

        if (columnNames == null)
            columnNames = getColumnNames();
        if (pkColumnIndexs == null)
            pkColumnIndexs = getPkColumnIndexs();
        if (pkColumnNames == null) {
            pkColumnNames = new String[pkColumnIndexs.length];
            for (int i = 0; i < pkColumnIndexs.length; i++) {
                pkColumnNames[i] = columnNames[pkColumnIndexs[i]];
            }
        }
        return pkColumnNames;
    }

    public String[] createRow() {
        String[] row = new String[getColumnCount()];
        return row;
    }

    public String[] createBlankRow() {
        String[] row = new String[getColumnCount()];
        return row;
    }

    public abstract boolean[] getRequired();

    public ArrayList<String[]> getSuggestFields() {
        return null;
    }

    public String[] getColumnLookupNames() {
        return null;
    }

    public String[] getColumnSuggestNames() {
        return null;
    }

    public String[] getChildMetaDataNames() {
        return null;
    }

    public ArrayList<int[]> getChildMap(String childName) {
        return null;
    }

    public String[] getSelectColumnNames() {
        if (selectColumnNames == null)
            return getColumnNames();
        else
            return selectColumnNames;
    }

    public void setSelectColumnNames(String[] selectColumnNames) {
        this.selectColumnNames = selectColumnNames;
    }
}
