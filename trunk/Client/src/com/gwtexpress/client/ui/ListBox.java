package com.gwtexpress.client.ui;

public class ListBox extends com.google.gwt.user.client.ui.ListBox implements GLogixField {
    int columnIndex, rowIndex;
    boolean required = false;

    public ListBox(int rowIndex, int columnIndex) {
        super();
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setColumnIndex(int index) {
        this.columnIndex = index;
    }

    public void setRowIndex(int index) {
        this.rowIndex = index;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }
}
