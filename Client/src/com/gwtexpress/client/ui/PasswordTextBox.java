package com.gwtexpress.client.ui;

public class PasswordTextBox extends com.google.gwt.user.client.ui.PasswordTextBox implements GLogixField {
    int columnIndex, rowIndex;

    public PasswordTextBox(int rowIndex, int columnIndex) {
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
}
