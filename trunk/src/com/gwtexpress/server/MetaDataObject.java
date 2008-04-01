package com.gwtexpress.server;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MetaDataObject extends HashMap implements IsSerializable {

    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
    Map lastChild;
    String lastChildName, lastChildrenName;
    String name;
    String[] columnNames;
    String[] columnTitles;
    char[] columnTypes;
    int[] columnSizes;
    String[] row;
    public boolean[] required;

    /**
     * @gwt.typeArgs <java.lang.String[]>
     */
    ArrayList<String[]> data = new ArrayList<String[]>();
    int currentRowIndex;
    int totalRows;
    int totalColumns;


    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
    List lastChildren;

    public MetaDataObject() {
        super();
        currentRowIndex = -1;
        totalRows = -1;
        totalColumns = -1;
    }

    public MetaDataObject(int len) {
        super();
    }

    public MetaDataObject(String name) {
        this();
        this.name = name;
    }

    public MetaDataObject(String name, String[] columnNames, 
                          String[] columnTitles, char[] columnTypes, 
                          int[] columnSizes) {
        this(name);
        this.columnNames = columnNames;
        this.columnTitles = columnTitles;
        this.columnTypes = columnTypes;
        this.columnSizes = columnSizes;
        totalColumns = columnNames.length;
    }

    public void print(String msg) {
        System.out.println(msg);
    }

    public String getTitle(String title) {
        title = title.replaceAll("_", " ");
        title = title.toLowerCase();
        return title;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < totalColumns; i++) {
            sb.append(columnNames[i]).append(',');
        }
        sb.append('\n');

        for (int i = 0; i < totalColumns; i++) {
            sb.append(columnTitles[i]).append(',');
        }
        sb.append('\n');

        for (int i = 0; i < totalColumns; i++) {
            sb.append(columnTypes[i]).append(',');
        }
        sb.append('\n');

        for (int r = 0; r < data.size(); r++) {
            String[] row = (String[])data.get(r);
            for (int j = 0; j < totalColumns; j++) {
                sb.append(row[j]).append(',');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    public void print() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < totalColumns; i++) {
            sb.append(columnNames[i]).append(',');
        }
        print(sb.toString());
        sb.setLength(0);

        for (int i = 0; i < totalColumns; i++) {
            sb.append(columnTitles[i]).append(',');
        }
        print(sb.toString());
        sb.setLength(0);

        for (int i = 0; i < totalColumns; i++) {
            sb.append(columnTypes[i]).append(',');
        }
        print(sb.toString());
        sb.setLength(0);

        for (int r = 0; r < data.size(); r++) {
            String[] row = (String[])data.get(r);
            for (int j = 0; j < totalColumns; j++) {
                sb.append(row[j]).append(',');
            }
            print(sb.toString());
            sb.setLength(0);
        }

    }

    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
    public Map getChild(String childName) {
        if (!childName.equals(lastChildName)) {
            lastChildName = childName;
            lastChild = (Map)get(childName);
        }
        return lastChild;
    }

    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
    public List getChildren(String childrenName) {
        if (!childrenName.equals(lastChildrenName)) {
            lastChildrenName = childrenName;
            lastChildren = (ArrayList)get(childrenName);
        }
        return lastChildren;
    }


    /**
     * @gwt.typeArgs child <java.lang.String,java.lang.String>
     */
    public void setChildren(String childName, List child) {
        put(childName, child);
    }

    /**
     * @gwt.typeArgs child <java.lang.String,java.lang.String>
     */
    public void setChild(String childName, Map child) {
        put(childName, child);
    }

    /**
     * @gwt.typeArgs child <java.lang.String,java.lang.String>
     */
    public void addChild(String childName, Map child) {
        getChildren(childName).add(child);
    }

    /**
     * @gwt.typeArgs child <java.lang.String,java.lang.String>
     */
    public void addChild(String childName, int loc, Map child) {
        getChildren(childName).add(loc, child);
    }

    /**
     * @gwt.typeArgs <java.lang.String,java.lang.String>
     */
    public Map getChild(String childName, int loc) {
        return (Map)getChildren(childName).get(loc);
    }

    public void putValue(String expression, String value) {
        int i = expression.indexOf('.');
        String val;
        if (i > 0) {
            val = expression.substring(0, i);
            Object o = get(val);
            if (o != null && o instanceof MetaDataObject) {
                ((MetaDataObject)o).putValue(expression.substring(i + 1), 
                                             value);
            }
        }
        put(expression, value);
    }

    public String getValue(String expression) {
        String retValue = null;
        String subExp;
        int i = expression.indexOf('.');
        if (i > 0) {
            subExp = expression.substring(0, i);
            Object o = get(subExp);
            if (o == null)
                return null;
            if (o instanceof MetaDataObject) {
                return ((MetaDataObject)o).getValue(expression.substring(i + 
                                                                         1));
            }
        } else {
            retValue = (String)get(expression);
        }
        return retValue;
    }

    public void setName(String newname) {
        this.name = newname;
    }

    public String getName() {
        return name;
    }

    public void setColumnNames(String[] newcolumnNames) {
        this.columnNames = newcolumnNames;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnTitles(String[] newcolumnTitles) {
        this.columnTitles = newcolumnTitles;
    }

    public String[] getColumnTitles() {
        return columnTitles;
    }

    public void setColumnTypes(char[] newcolumnTypes) {
        this.columnTypes = newcolumnTypes;
    }

    public char[] getColumnTypes() {
        return columnTypes;
    }

    public void addRow(String[] newrow) {
        data.add(newrow);
    }

    public void setRow(int i, String[] newrow) {
        data.add(i, newrow);
    }

    public String[] getRow(int i) {
        if (i < totalRows) {
            return (String[])data.get(i);
        } else {
            return null;
        }
    }

    public void setData(ArrayList newdata) {
        this.data = newdata;
    }

    public ArrayList<String[]> getData() {
        return data;
    }

    public void setCurrentRowIndex(int newcurrentRowIndex) {
        this.currentRowIndex = newcurrentRowIndex;
    }

    public int getCurrentRowIndex() {
        return currentRowIndex;
    }

    public String[] getCurrentRow() {
        return (String[])data.get(currentRowIndex);
    }

    public int getTotalRows() {
        return data.size();
    }

    public String[] getNextRow() {
        if ((currentRowIndex + 1) < getTotalRows()) {
            return (String[])data.get(++currentRowIndex);
        } else {
            return null;
        }
    }

    public void printColumnNames() {
        StringBuffer sb = new StringBuffer();
        StringBuffer bb = new StringBuffer();
        StringBuffer sbAllFalse = new StringBuffer();
        StringBuffer nb = new StringBuffer();
        StringBuffer req = new StringBuffer();
        sb.append("String[] columnNames = {");
        bb.append("boolean[] allTrues = {");
        sbAllFalse.append("boolean[] allFalse = {");
        nb.append("String[] allNulls = {");
        req.append("boolean[] required = {");
        for (int i = 0; i < columnNames.length; i++) {
            if (i > 0) {
                sb.append(",");
                bb.append(",");
                sbAllFalse.append(",");
                nb.append(",");
                req.append(",");
            }
            sb.append("\"").append(columnNames[i]).append("\"");
            bb.append("true");
            sbAllFalse.append("false");
            nb.append("null");
            req.append(required[i]);
        }
        sb.append("};");
        bb.append("};");
        sbAllFalse.append("};");
        nb.append("};");
        req.append("};");
        System.out.println(sb.toString());
        System.out.println(bb.toString());
        System.out.println(sbAllFalse.toString());
        System.out.println(nb.toString());
        System.out.println(req.toString());
    }

    public void printTitles() {
        StringBuffer sb = new StringBuffer();
        sb.append("String[] columnTitles = {");
        for (int i = 0; i < columnTitles.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append("\"").append(columnTitles[i]).append("\"");
        }
        sb.append("};");
        System.out.println(sb.toString());
    }

    public void printSizes() {
        StringBuffer sb = new StringBuffer();
        sb.append("int[] columnSizes = {");
        for (int i = 0; i < columnSizes.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append(columnSizes[i]);
        }
        sb.append("};");
        System.out.println(sb.toString());
    }

    public void printTypes() {
        StringBuffer sb = new StringBuffer();
        sb.append("char[] columnTypes = {");
        for (int i = 0; i < columnTypes.length; i++) {
            if (i > 0)
                sb.append(',');
            sb.append(columnTypes[i]);
        }
        sb.append("};");
        System.out.println(sb.toString());
    }
}
