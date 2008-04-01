package com.gwtexpress.client.ui.form;

import com.gwtexpress.client.datePicker.DatePicker;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.ui.Button;
import com.gwtexpress.client.ui.GLogixField;
import com.gwtexpress.client.ui.ex.GLogixUIBuilder;
import com.gwtexpress.client.ui.ListBox;
import com.gwtexpress.client.ui.table.SimpleTable;
import com.gwtexpress.client.ui.TextBox;
import com.gwtexpress.client.util.DateUtil;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerAdapter;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

import net.mygwt.ui.client.event.BaseEvent;
import net.mygwt.ui.client.event.Listener;


public class AdvancedSearchForm extends AbstractFormLayout {

    SimpleTable resultTable;
    Button saveBtn, findBtn, simpleSearchBtn;
    ArrayList<String[]> paramList;
    ArrayList<ListBox> condItemList = new ArrayList<ListBox>(5);
    FormLayout simpleSearch;
    RadioButton andRB, orRB;

    public AdvancedSearchForm(GLogixMetaData metaData) {
        super(metaData, false);
        type = ADVANCED_SEARCH_FORM;
        this.metaData = metaData;
        maxColumns = 1;
    }

    public String getID(){
        return metaData.getServiceName();
    }
    
    public void addListener(int eventType, Listener listener) {
        observable.addListener(eventType, listener);
    }

    public boolean fireEvent(int eventType, Widget widget, Widget item) {
        BaseEvent be = new BaseEvent();
        be.widget = widget;
        be.item = item;
        return observable.fireEvent(eventType, be);
    }

    private ListBox getCondition(int row, char dataType) {
        ListBox listBox = new ListBox(row, -1);
        condItemList.add(row, listBox);
        listBox.addItem("Equals", "=");
        if ('V' == dataType) {
            listBox.addItem("Contains", "C");
            listBox.addItem("Starts with", "SW");
            listBox.addItem("Ends with", "EW");
        } else if ('=' != dataType) {
            listBox.addItem("Less than", "<");
            listBox.addItem("Greater than", ">");
        }
        listBox.addChangeListener(inputFormListener);
        return listBox;
    }

    public void renderForm() {
        FocusWidget ui = null;
        Label label = null;
        int j = 0;
        int maxColumns = 1;
        int[] qColIdxs;
        qColIdxs = metaData.getQueryableColumnIndexes();
        paramList = new ArrayList<String[]>(qColIdxs.length);

        columnWidth = metaData.getColumnWidth();
        ArrayList<String[]> suggestFields = metaData.getSuggestFields();
        int row = 0;


        for (row = 0; row < (qColIdxs.length / maxColumns) + 1; row++) {
            for (int c = 0; c < (maxColumns * 4) && j < qColIdxs.length; 
                 c += 4) {
                addCriteria(true, qColIdxs[j], row);
                j++;
            }
        }
        HorizontalPanel buttonBar = getButtonBar();
        andRB = new RadioButton("ao", AND);
        andRB.setChecked(true);
        orRB = new RadioButton("ao", OR);
        buttonBar.add(andRB);
        buttonBar.add(orRB);
        findBtn = addButton("FIND", findButtonLabel);
        initFindBtn();

        addClearButton();

        simpleSearchBtn = addButton("SIMPLE_SEARCH", SIMPLE_SEARCH);
        if (simpleSearch == null)
            simpleSearchBtn.setVisible(false);
        add(flexTable);
        flexTable.setWidget(row + 1, 0, buttonBar);
        flexTable.getFlexCellFormatter().setHorizontalAlignment(row + 1, 0, 
                                                                HasHorizontalAlignment.ALIGN_RIGHT);
        flexTable.getFlexCellFormatter().setColSpan(row + 1, 0, 
                                                    flexTable.getCellCount(1));
    }

    public void onClick(Widget sender) {
        super.onClick(sender);
        Button btn = (Button)sender;
        //System.out.println(btn.getName());
        if ("FIND".equals(btn.getId())) {
            String andOr = "AND";
            if (orRB.isChecked())
                andOr = "OR";
            GLogixUIBuilder.fetchData(resultTable, metaData, paramList, andOr);
        } else if ("SIMPLE_SEARCH".equals(btn.getId())) {
            setVisible(false);
            simpleSearch.setVisible(true);
        }
    }

    public void initButtons() {
        initFindBtn();
    }

    private void initFindBtn() {
        if (findBtn == null)
            return;
        if (isDirty())
            findBtn.setEnabled(true);
        else
            findBtn.setEnabled(false);
    }

    public void setResultTable(SimpleTable resultTable) {
        this.resultTable = resultTable;
    }

    public SimpleTable getResultTable() {
        return resultTable;
    }

    public ArrayList<String[]> getParamList() {
        return paramList;
    }

    public void setMaxColumns(int maxColumns) {
    }

    public int getMaxColumns() {
        return 1;
    }

    public void setDataValue(int row, int col, String value) {
        if (col == -1)
            paramList.get(row)[1] = value;
        else
            paramList.get(row)[2] = value;
    }

    public String getDataValue(int row, int col) {
        return paramList.get(row)[2];
    }

    public String getOriginalDataValue(int row, int col) {
        return "";
    }

    public void setSimpleSearch(FormLayout simpleSearch) {
        if (simpleSearchBtn != null)
            simpleSearchBtn.setVisible(true);
        this.simpleSearch = simpleSearch;
    }

    public FormLayout getSimpleSearch() {
        return simpleSearch;
    }

    private void pullFieldUp(int index) {
        for (int i = index; i < getFormFields().size(); i++) {
            ((GLogixField)getFormFields().get(i)).setRowIndex(((GLogixField)getFormFields().get(i)).getRowIndex() - 
                                                              1);
            condItemList.get(i).setRowIndex(condItemList.get(i).getRowIndex() - 
                                            1);
        }
    }

    private void pushFieldDown(int index) {
        for (int i = index; i < getFormFields().size(); i++) {
            ((GLogixField)getFormFields().get(i)).setRowIndex(((GLogixField)getFormFields().get(i)).getRowIndex() + 
                                                              1);
            condItemList.get(i).setRowIndex(condItemList.get(i).getRowIndex() + 
                                            1);
        }
    }

    public void removeCriteria(int atRow) {
        flexTable.removeRow(atRow);
        paramList.remove(atRow);
        pullFieldUp(atRow + 1);
        getFormFields().remove(atRow);
    }

    public void clearForm() {
        paramList.clear();
        getButtonBar().clear();
        flexTable.clear();
        getFormFields().clear();
        condItemList.clear();
        renderForm();
    }

    public void addCriteria(boolean first, final int colIdx, final int atRow) {
        int c = 0;
        flexTable.insertRow(atRow);
        pushFieldDown(atRow);

        Label label = null;
        int width = columnWidth[colIdx];
        if (width > 20)
            width = 20;
        if (width < 5)
            width = 5;
        String[] lookups = metaData.getColumnLookupNames();
        final Widget ui = createField(colIdx);
//        if (lookups != null && lookups[colIdx] != null) {
//            ListBox lb = new ListBox(atRow, colIdx);
//            GLogixUIBuilder.populateLookup(this, lb, lookups[colIdx], null);
//            ui = lb;
//        } else {
//            TextBox t;
//            if (colTypes[colIdx] == 'D') {
//                t = new DatePicker(atRow, colIdx);
//                ((DatePicker)t).setDateFormatter(DateUtil.getDateFormat());
//                width = 10;
//            } else if (colTypes[colIdx] == 'T') {
//                t = new DatePicker(atRow, colIdx);
//                width = 15;
//            } else {
//                //                if (suggestFields != null && suggestFields[qColIdxs[j]])
//                //                    t = new TextBox();
//                //                }else{
//                t = new TextBox(atRow, colIdx);
//                //                }
//            }
//            ui = t;
//            t.setWidth((width * 8) + "px");
//            t.setMaxLength(columnWidth[colIdx]);
//            t.setName(metaData.getServiceName() + ":" + colIdx);
//        }
        addField(columnNames[colIdx], atRow, ui);
        paramList.add(atRow, new String[] { colIdx + "", "=", "" });

        //final FocusWidget field = ui;
        addLabel(atRow, 0 + c, columnTitles[colIdx]);

        char condType = colTypes[colIdx];
        if (lookups != null && lookups[colIdx] != null) {
            condType = '=';
        }
        ListBox lb = getCondition(atRow, condType);
        flexTable.setWidget(atRow, 1 + c, lb);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth(((width * 8) + 20) + "px");
        hp.add(ui);

        HorizontalPanel outerHp = new HorizontalPanel();
        outerHp.add(hp);
        final Image plus = new Image("images/icons/field_group_plus_ena.png");

        plus.addMouseListener(new MouseListener() {
                    public void onMouseEnter(Widget sender) {
                        plus.setUrl("images/icons/field_group_plus_ovr.png");
                    }

                    public void onMouseLeave(Widget sender) {
                        plus.setUrl("images/icons/field_group_plus_ena.png");
                    }

                    public void onMouseDown(Widget sender, int x, int y) {
                        plus.setUrl("images/icons/field_group_plus_dwn.png");
                    }

                    public void onMouseMove(Widget sender, int x, int y) {
                    }

                    public void onMouseUp(Widget sender, int x, int y) {
                        plus.setUrl("images/icons/field_group_plus_ena.png");
                    }
                });
        plus.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        addCriteria(false, colIdx, 
                                    ((GLogixField)ui).getRowIndex() + 1);
                    }
                });

        outerHp.add(plus);
        final Image removeBtn = new Image();
        if (first) {
            removeBtn.setUrl("images/icons/field_group_minus_dis.png");
        } else {
            removeBtn.setUrl("images/icons/field_group_minus_ena.png");
            removeBtn.addMouseListener(new MouseListener() {
                        public void onMouseEnter(Widget sender) {
                            removeBtn.setUrl("images/icons/field_group_minus_ovr.png");
                        }

                        public void onMouseLeave(Widget sender) {
                            removeBtn.setUrl("images/icons/field_group_minus_ena.png");
                        }

                        public void onMouseDown(Widget sender, int x, int y) {
                            removeBtn.setUrl("images/icons/field_group_minus_dwn.png");
                        }

                        public void onMouseMove(Widget sender, int x, int y) {
                        }

                        public void onMouseUp(Widget sender, int x, int y) {
                            removeBtn.setUrl("images/icons/field_group_minus_ena.png");
                        }
                    });
            removeBtn.addClickListener(new ClickListener() {
                        public void onClick(Widget sender) {
                            removeCriteria(((GLogixField)ui).getRowIndex());
                        }
                    });
        }
        outerHp.add(removeBtn);

        flexTable.setWidget(atRow, 2 + c, outerHp);
        flexCellFormatter.setStylePrimaryName(atRow, 2 + c, 
                                              " +    " + "a-content-cell");
    }
}
