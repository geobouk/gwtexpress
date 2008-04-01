package com.gwtexpress.client.ui.table;

import com.gwtexpress.client.rpc.model.GLogixMetaData;

import com.gwtexpress.client.ui.ex.GLogixUIBuilder;

import java.util.ArrayList;
import java.util.List;

import net.mygwt.ui.client.widget.table.Table;
import net.mygwt.ui.client.widget.table.TableColumnModel;
import net.mygwt.ui.client.widget.table.TableItem;

public class SimpleTable extends Table {
    public List<String[]> data;
    public GLogixMetaData metaData;
    public ArrayList<TableItem> items;
    public int[] visibleColumnIndexes;
    public String[] lastFetchParams = null;

    public SimpleTable(int style, TableColumnModel cm, 
                       GLogixMetaData metaData) {
        super(style, cm);
        setBorders(true);
        this.metaData = metaData;
        visibleColumnIndexes = metaData.getResultColumnIndexes();
        setId("st_" + metaData.getServiceName() + "_id");
        setVerticalLines(true);
    }

    public void setData(List<String[]> newData) {
        if (data != null)
            data.clear();
        data = null;
        data = newData;
        removeAll();
        items = new ArrayList<TableItem>(data.size());
        for (int i = 0; i < data.size(); i++) {
            insertRow(data.get(i), i);
        }
        recalculate();
    }

    public String[] getCurrentRow() {
        int i = items.indexOf(getSelectedItem());
        return data.get(i);
    }

    public void insertRow(String[] row) {
        insertRow(row, getItemCount());
    }

    public void insertRow(String[] row, int index) {
        String[] tRow = new String[visibleColumnIndexes.length];
        for (int i = 0; i < visibleColumnIndexes.length; i++) {
            tRow[i] = GLogixUIBuilder.decodeData(metaData.getColumnTypes()[visibleColumnIndexes[i]], 
                                               row[visibleColumnIndexes[i]]);
        }
        TableItem item = new TableItem(tRow);
        items.add(index, item);
        insert(item, index);
    }

    public GLogixMetaData getMetaData() {
        return metaData;
    }

    public void refresh() {
        if (lastFetchParams != null)
            GLogixUIBuilder.fetchData(this, metaData, lastFetchParams);
    }

    public void fetchData(String[] params) {
        lastFetchParams = params;
        GLogixUIBuilder.fetchData(this, metaData, lastFetchParams);
    }
}
