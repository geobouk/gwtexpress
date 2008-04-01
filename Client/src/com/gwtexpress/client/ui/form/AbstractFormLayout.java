package com.gwtexpress.client.ui.form;

import com.allen_sauer.gwt.log.client.Log;

import com.gwtexpress.client.datePicker.DatePicker;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.ui.CheckBox;
import com.gwtexpress.client.ui.GLogixSuggestOracle;
import com.gwtexpress.client.ui.ListBox;
import com.gwtexpress.client.ui.PasswordTextBox;
import com.gwtexpress.client.ui.SuggestBox2;
import com.gwtexpress.client.ui.TextArea;
import com.gwtexpress.client.ui.TextBox;
import com.gwtexpress.client.ui.TextBoxNumber;
import com.gwtexpress.client.ui.TitledPanel;
import com.gwtexpress.client.ui.ex.GLogixUIBuilder;
import com.gwtexpress.client.util.DateUtil;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.mygwt.ui.client.Events;
import net.mygwt.ui.client.event.BaseEvent;
import net.mygwt.ui.client.event.Listener;
import net.mygwt.ui.client.util.Observable;
import net.mygwt.ui.client.widget.IconButton;


public abstract class AbstractFormLayout extends SimplePanel implements FormLayout {
    String[] origData;
    String[] data;
    int[] colIdxs;
    String findButtonLabel = "Find";
    String cancelButtonLabel = "Cancel";
    String resetButtonLabel = "Reset";
    String updateButtonLabel = "Update";
    String createButtonLabel = "Create";
    public FlexTable.FlexCellFormatter flexCellFormatter;
    public FlexTable flexTable;
    public GLogixMetaData metaData;
    public int maxColumns;
    String[] columnTitles, columnNames;
    char[] colTypes;
    HashMap<Widget, IconButton> invalidFields = 
        new HashMap<Widget, IconButton>();
    private boolean dirty = false;
    Observable observable = new Observable();
    boolean subForm;
    HashMap<AbstractFormLayout, int[]> subFormList;
    public String formTitle;
    private ArrayList<Widget> childrenList = new ArrayList<Widget>();
    HashMap<Widget, Label> labelList = new HashMap<Widget, Label>();
    HashMap<String, Widget> childrenNameList = new HashMap<String, Widget>();
    boolean[] required;
    boolean[] editable, updatable;
    int type;
    protected InputFormListener inputFormListener;
    VerticalPanel content = new VerticalPanel();
    TitledPanel titledPanel;
    private HashMap<SuggestBox2, int[]> suggestMap;
    String mTitle;
    AbstractFormLayout parentForm;
    int labelWidth = -1, fieldWidth = -1;
    HashMap<String, Widget> fieldsMap = new HashMap<String, Widget>();
    boolean compact = false;
    int[] columnWidth;
    String[] lookups;
    String[] suggests;
    boolean updateOnPost = false;
    boolean readOnlyForm = false;

    public AbstractFormLayout(GLogixMetaData metaData, boolean subForm) {
        this(metaData, subForm, null);
    }

    public AbstractFormLayout(GLogixMetaData metaData, boolean subForm, 
                              String title) {
        super();
        if (title != null) {
            formTitle = title;
            titledPanel = new TitledPanel(title);
            setWidget(titledPanel);
            titledPanel.setHeight("100%");
            //titledPanel.setWidth("99%");
            //content.setWidth("100%");
            content.addStyleName("fieldset-cont");
            titledPanel.add(content);
            content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        } else {
            setWidget(content);
        }
        flexTable = new FlexTable();
        flexTable.setWidth("100%");
        flexTable.setCellPadding(1);
        flexTable.setCellSpacing(0);
        flexTable.setBorderWidth(0);
        this.subForm = subForm;
        columnTitles = metaData.getColumnTitles();
        columnNames = metaData.getColumnNames();
        colTypes = metaData.getColumnTypes();
        required = metaData.getRequired();
        editable = metaData.getEditable();
        updatable = metaData.getUpdatable();
        if (columnNames.length != columnTitles.length) {
            content.add(new Label("Column Titles doesn't match the total columns"));
        }
        if (columnNames.length != colTypes.length) {
            content.add(new Label("Column Types doesn't match the total columns"));
        }
        if (columnNames.length != required.length) {
            content.add(new Label("Required Columns doesn't match the total columns"));
        }
        if (columnNames.length != editable.length) {
            content.add(new Label("Editable Columns doesn't match the total columns"));
        }
        if (columnNames.length != metaData.getQueryable().length) {
            content.add(new Label("Queryable Columns doesn't match the total columns"));
        }
        if (columnNames.length != metaData.getResultColumns().length) {
            content.add(new Label("Result Columns doesn't match the total columns"));
        }
        if (columnNames.length != metaData.getColumnWidth().length) {
            content.add(new Label("Column widths doesn't match the total columns"));
        }
        if (metaData.getColumnLookupNames() != null && 
            columnNames.length != metaData.getColumnLookupNames().length) {
            content.add(new Label("Column lookups doesn't match the total columns"));
        }
        if (metaData.getColumnSuggestNames() != null && 
            columnNames.length != metaData.getColumnSuggestNames().length) {
            content.add(new Label("Column suggests doesn't match the total columns"));
        }
        if (metaData.getVisible() != null && 
            columnNames.length != metaData.getVisible().length) {
            content.add(new Label(getTitle() + ": Visible columns [" + 
                                  metaData.getVisible().length + 
                                  "] doesn't match the total columns [" + 
                                  columnNames.length + "]"));
        }
        if (!subForm)
            inputFormListener = new InputFormListener(this, metaData);
        flexCellFormatter = flexTable.getFlexCellFormatter();
    }

    public void add(Widget widget) {
        content.add(widget);
    }

    public boolean isFieldValid(Widget widget) {
        return !(invalidFields.containsKey(widget));
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

    public void onFocus(Widget sender) {
    }

    public void onLostFocus(Widget sender) {
        if (sender instanceof TextBox) {
            TextBox t = ((TextBox)sender);
            int i = t.getColumnIndex();
            String oldVal = getDataValue(t.getRowIndex(), t.getColumnIndex());
            if (oldVal == null)
                oldVal = "";
            String newValue = GLogixUIBuilder.encodeData(metaData.getColumnTypes()[i], 
                                           t.getText());
            if (newValue.equals(oldVal))
                return;
            if (validateField(i, t)) {
                if (newValue == null && "".equals(oldVal)) {
                    // value not chnaged
                } else {
                    if (newValue != null && newValue.equals(oldVal)) {
                        // value not chnaged
                    } else {
                        setDataValue(t.getRowIndex(), i, newValue);
                        setDirty(true);
                    }
                }
            } else {
                setDirty(true);
            }
            initButtons();
        }
    }

    public boolean isFormValid() {
        boolean valid = true;
        if (subFormList != null) {
            Iterator<AbstractFormLayout> sitr = 
                subFormList.keySet().iterator();
            while (sitr.hasNext()) {
                if (!sitr.next().isFormValid()) {
                    valid = false;
                }
            }
        }
        Iterator itr = childrenList.iterator();
        Widget f;
        int i;
        while (itr.hasNext()) {
            f = (Widget)itr.next();
            i = getColumnIndex(f);
            if (!validateField(i, f)) {
                valid = false;
            }
        }
        return valid;
    }

    private boolean validateFieldInternal(int i, final Widget f) {
        boolean b = true;
        if (!editable[i])
            return true;

        String errMsg = null;
        String value = "INVALID_CONTROL_VALUE_LOST";
        if (f instanceof SuggestBox2) {
            SuggestBox2 t = ((SuggestBox2)f);
            value = t.getText();
            if (required[i]) {
                if ((type == EDIT_FORM || type == CREATE_FORM) && 
                    (t.getText() == null || t.getText().length() == 0)) {
                    b = false;
                    errMsg = "Value required";
                }
            }
        } else if (f instanceof ListBox) {
            ListBox t = ((ListBox)f);
            // return true if the list is from advanced search
            if (i == -1)
                return true;
            value = t.getValue(t.getSelectedIndex());

            if (required[i]) {
                if ((type == EDIT_FORM || type == CREATE_FORM) && 
                    (t.getValue(t.getSelectedIndex()) == null || 
                     t.getValue(t.getSelectedIndex()).length() == 0)) {
                    b = false;
                    errMsg = "You must select a value";
                }
            }
        } else if (f instanceof TextArea) {
            TextArea t = ((TextArea)f);
            value = t.getText();
            if (required[i]) {
                if ((type == EDIT_FORM || type == CREATE_FORM) && 
                    (t.getText() == null || t.getText().length() == 0)) {
                    b = false;
                    errMsg = "Value required";
                }
            }
        } else if (f instanceof CheckBox) {
            CheckBox t = ((CheckBox)f);
            value = t.isChecked() ? "Y" : "N";
        } else if (f instanceof PasswordTextBox) {
            PasswordTextBox t = ((PasswordTextBox)f);
            value = t.getText();
            if (required[i]) {
                if ((type == EDIT_FORM || type == CREATE_FORM) && 
                    (t.getText() == null || t.getText().length() == 0)) {
                    b = false;
                    errMsg = "Value required";
                }
            }
        } else if (f instanceof TextBox) {
            TextBox t = ((TextBox)f);
            value = t.getText();
            if (required[i]) {
                if ((type == EDIT_FORM || type == CREATE_FORM) && 
                    (t.getText() == null || t.getText().length() == 0)) {
                    String s = t.getText();
                    b = false;
                    errMsg = "Value required";
                    t.setFocus(true);
                }
            }
            if (t.getText() != null && t.getText().length() > 0) {
                if (colTypes[i] == 'N') {
                    try {
                        Double.parseDouble(t.getText());
                    } catch (NumberFormatException e) {
                        b = false;
                        errMsg = t.getText() + " is an invalid number";
                    }
                }
            }
        } else if (f instanceof DatePicker) {
            DatePicker t = ((DatePicker)f);
            value = t.getText();
            if (required[i]) {
                if ((type == EDIT_FORM || type == CREATE_FORM) && 
                    (t.getText() == null || t.getText().length() == 0)) {
                    String s = t.getText();
                    b = false;
                    errMsg = "Value required";
                    t.setFocus(true);
                }
            }
            if (t.getText() != null && t.getText().length() > 0) {
                if (colTypes[i] == 'D') {
                    try {
                        DateTimeFormat dtf = DateUtil.getDateFormat();
                        dtf.parse(t.getText());
                    } catch (Exception e) {
                        b = false;
                        errMsg = t.getText() + " is an invalid date";
                    }
                } else if (colTypes[i] == 'T') {
                    try {
                        DateTimeFormat dtf = DateUtil.getDateTimeFormat();
                        dtf.parse(t.getText());
                    } catch (Exception e) {
                        b = false;
                        errMsg = t.getText() + " is an invalid date";
                    }
                }
            }
        }
        if (fieldHandlerMap != null) {
            FieldHandlerCollection fhc = fieldHandlerMap.get(f);
            if (b && fhc != null) {
                errMsg = fhc.fireIsValid(f);
                if (errMsg != null)
                    b = false;
            }
        }
        if (b) {
            setFieldValid(f);
            setDataValue(getRowIndex(f), i, GLogixUIBuilder.encodeData(colTypes[i], value));
        } else {
            setFieldInvalid(f, errMsg);
        }
        return b;
    }

    public void setFieldValid(Widget f) {
        if (invalidFields.containsKey(f)) {
            f.removeStyleName("a-err-input");
            invalidFields.get(f).removeFromParent();
            invalidFields.remove(f);
        }
    }

    public void setFieldInvalid(Widget f, String errMsg) {
        if (invalidFields.containsKey(f)) {
            invalidFields.get(f).removeFromParent();
            invalidFields.remove(f);
            f.removeStyleName("a-err-input");
        }
        f.addStyleName("a-err-input");
        IconButton info = new IconButton("icon-error");
        info.setToolTip(errMsg);
        ((HorizontalPanel)f.getParent()).add(info);
        invalidFields.put(f, info);
        //Info.show(f.getTitle() + " has errors", errMsg, errMsg);
    }

    public boolean validateField(int i, final Widget f) {
        if (!isChild(f) && subFormList != null) {
            Iterator<AbstractFormLayout> sitr = 
                subFormList.keySet().iterator();
            while (sitr.hasNext()) {
                AbstractFormLayout cform = sitr.next();
                if (cform.isChild(f)) {
                    return cform.validateField(i, f);
                }
            }
        } else {
            return validateFieldInternal(i, f);
        }
        return false;
    }

    public abstract void renderForm();

    public void setMaxColumns(int maxColumns) {
        this.maxColumns = maxColumns;
    }

    public int getMaxColumns() {
        return maxColumns;
    }

    public void onClick(Widget sender) {
        com.gwtexpress.client.ui.Button btn = (com.gwtexpress.client.ui.Button)sender;
        //System.out.println(btn.getName());
        if ("Cancel".equals(btn.getHTML())) {
            resetForm();
            fireEvent(Events.Close, this, btn);
        } else if ("Clear".equals(btn.getHTML())) {
            clearForm();
        } else if ("Reset".equals(btn.getHTML())) {
            resetForm();
        }
    }

    public void clearForm() {
        //origData = metaData.createRow();
        resetForm();
    }

    void log(String msg) {
        System.out.println(msg);
    }

    public Widget findField(String colName) {
        return getField(colName);
    }

    public void setFieldVisible(Widget field, boolean b) {
        if (!b)
            setFieldValid(field);
        field.setVisible(b);
        Label l = labelList.get(field);
        if (l != null)
            l.setVisible(b);
    }

    public void setFieldVisible(String colName, boolean b) {
        setFieldVisible(getField(colName), b);
    }

    public void resetForm() {
        if (!dirty) {
            Log.debug("NOT resetting form " + getID() + "[" + getFormTitle() + 
                      "] as it's not dirty...");
            return;
        }
        Log.debug("In resetForm for " + getID() + "[" + getFormTitle() + "]");
        data = origData;
        Iterator itr = childrenList.iterator();
        while (itr.hasNext()) {
            Widget w = (Widget)itr.next();
            int rowIdx = getRowIndex(w);
            int i = getColumnIndex(w);
            String origValue = getOriginalDataValue(rowIdx, i);
            if (w instanceof TextBox) {
                ((TextBox)w).setText(GLogixUIBuilder.decodeData(colTypes[i], 
                                                                origValue));
            } else if (w instanceof DatePicker) {
                ((DatePicker)w).setText(GLogixUIBuilder.decodeData(colTypes[i], 
                                                                   origValue));
            } else if (w instanceof ListBox) {
                String value = GLogixUIBuilder.decodeData(colTypes[i], origValue);
                if (value == null)
                    ((ListBox)w).setSelectedIndex(0);
                else {
                    int count = ((ListBox)w).getItemCount();
                    for (int k = 0; k < count; k++) {
                        if (value.equals(((ListBox)w).getValue(k))) {
                            ((ListBox)w).setSelectedIndex(k);
                            break;
                        }
                    }
                }
            } else if (w instanceof CheckBox) {
                if ("Y".equals(origValue))
                    ((CheckBox)w).setChecked(true);
                else {
                    data[i] = "N";
                    origData[i] = "N";
                    ((CheckBox)w).setChecked(false);
                }

            } else if (w instanceof TextArea) {
                ((TextArea)w).setText(GLogixUIBuilder.decodeData(colTypes[i], 
                                                                 origValue));
            } else if (w instanceof PasswordTextBox) {
                ((PasswordTextBox)w).setText(GLogixUIBuilder.decodeData(colTypes[i], 
                                                                        origValue));
            } else if (w instanceof SuggestBox2) {
                ((SuggestBox2)w).setText(GLogixUIBuilder.decodeData(colTypes[i], 
                                                                    origValue));
            }
            w.removeStyleName("a-err-input");
            IconButton ib = invalidFields.get(w);
            if (ib != null)
                ib.removeFromParent();
            //setDataValue(rowIdx, i, origValue);
        }
        invalidFields.clear();
        if (subFormList != null) {
            Iterator<AbstractFormLayout> sitr = 
                subFormList.keySet().iterator();
            while (sitr.hasNext()) {
                AbstractFormLayout sform = sitr.next();
                sform.dirty = true;
                sform.resetForm();
            }
        }
        dirty = false;
        initButtons();
    }

    public abstract void initButtons();

    public boolean isDirty() {
        return dirty;
    }

    public boolean isValid() {
        return (invalidFields.size() == 0);
    }

    public void setFocus() {
        if (childrenList.size() == 0)
            return;
        Widget f = childrenList.get(0);
        if (f instanceof FocusWidget)
            ((FocusWidget)f).setFocus(true);
    }


    public int getRowIndex(Widget f) {
        int i = -1;
        if (f instanceof TextBox) {
            i = ((TextBox)f).getRowIndex();
        } else if (f instanceof DatePicker) {
            i = ((DatePicker)f).getRowIndex();
        } else if (f instanceof PasswordTextBox) {
            i = ((PasswordTextBox)f).getRowIndex();
        } else if (f instanceof TextArea) {
            i = ((TextArea)f).getRowIndex();
        } else if (f instanceof ListBox) {
            i = ((ListBox)f).getRowIndex();
        } else if (f instanceof CheckBox) {
            i = ((CheckBox)f).getRowIndex();
        } else if (f instanceof SuggestBox2) {
            int[] t = getSuggestMap().get(f);
            if (t != null && t.length > 0)
                i = t[0];
        }
        return i;
    }

    public int getColumnIndex(Widget f) {
        int i = -1;
        if (f instanceof TextBox) {
            i = ((TextBox)f).getColumnIndex();
        } else if (f instanceof DatePicker) {
            i = ((DatePicker)f).getColumnIndex();
        } else if (f instanceof PasswordTextBox) {
            i = ((PasswordTextBox)f).getColumnIndex();
        } else if (f instanceof TextArea) {
            i = ((TextArea)f).getColumnIndex();
        } else if (f instanceof ListBox) {
            i = ((ListBox)f).getColumnIndex();
        } else if (f instanceof CheckBox) {
            i = ((CheckBox)f).getColumnIndex();
        } else if (f instanceof SuggestBox2) {
            int[] t = getSuggestMap().get(f);
            if (t != null && t.length > 0)
                i = t[1];
        }
        return i;
    }
    FieldHandlerCollection fieldHandlerCollection;
    HashMap<Widget, FieldHandlerCollection> fieldHandlerMap;

    public final void addFieldHandler(String colName, FieldHandler handler) {
        addFieldHandler(getField(colName), handler);
    }

    public final void addFieldHandler(Widget field, FieldHandler handler) {
        if (fieldHandlerMap == null) {
            fieldHandlerMap = new HashMap<Widget, FieldHandlerCollection>();
        }
        fieldHandlerCollection = fieldHandlerMap.get(field);
        if (fieldHandlerCollection == null) {
            fieldHandlerCollection = new FieldHandlerCollection();
            fieldHandlerMap.put(field, fieldHandlerCollection);
        }
        fieldHandlerCollection.add(handler);
    }

    public void addField(String colName, int index, Widget f) {
        childrenList.add(index, f);
        childrenNameList.put(colName, f);

        if ((type == CREATE_FORM && !editable[getColumnIndex(f)]) || 
            (type == EDIT_FORM && !updatable[getColumnIndex(f)])) {
            // readonly and hence no need for listeners...
        } else {
            if (f instanceof ListBox) {
                ((ListBox)f).addChangeListener(inputFormListener);
                ((ListBox)f).addFocusListener(inputFormListener);
            } else if (f instanceof CheckBox) {
                if (!"Y".equals(data[((CheckBox)f).getColumnIndex()])) {
                    data[((CheckBox)f).getColumnIndex()] = "N";
                }
                ((CheckBox)f).addClickListener(inputFormListener);
            } else if (f instanceof SuggestBox2) {
                ((SuggestBox2)f).addChangeListener(inputFormListener);
                ((SuggestBox2)f).addEventHandler(inputFormListener);
                ((SuggestBox2)f).addFocusListener(inputFormListener);
            } else if (f instanceof DatePicker) {
                ((DatePicker)f).addChangeListener(inputFormListener);
                ((DatePicker)f).addFocusListener(inputFormListener);
            }
            if (f instanceof TextBox) {
                ((TextBox)f).addChangeListener(inputFormListener);
                ((TextBox)f).addFocusListener(inputFormListener);
            } else if (f instanceof PasswordTextBox) {
                ((PasswordTextBox)f).addChangeListener(inputFormListener);
                ((PasswordTextBox)f).addFocusListener(inputFormListener);
            } else if (f instanceof TextArea) {
                ((TextArea)f).addChangeListener(inputFormListener);
                ((TextArea)f).addFocusListener(inputFormListener);
            }
        }
    }

    public void addField(String colName, Widget f) {
        addField(colName, childrenList.size(), f);
    }

    public Widget getField(String colName) {
        return childrenNameList.get(colName);
    }

    public Widget removeField(int index) {
        return childrenList.remove(index);
    }

    public boolean isChild(Widget f) {
        return childrenList.contains(f);
    }

    public boolean removeField(Widget f) {
        return childrenList.remove(f);
    }
    private HorizontalPanel buttonBar;

    public HorizontalPanel getButtonBar() {
        if (buttonBar == null) {
            buttonBar = new HorizontalPanel();
            buttonBar.setSpacing(10);
            buttonBar.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        }
        return buttonBar;
    }

    public com.gwtexpress.client.ui.Button addClearButton() {
        return addButton("CLEAR", "Clear");
    }

    public String getColumnLabel(String colName) {
        return columnTitles[metaData.getColumnIndex(colName)];
    }

    //    public int getColumnIndex(String colName) {
    //        if (colName == null)
    //            return -1;
    //        int index = -1;
    //        for (int i = 0; i < columnNames.length; i++) {
    //            if (columnNames[i].equals(colName)) {
    //                index = i;
    //                break;
    //            }
    //        }
    //        return index;
    //    }

    public Label addLabel(int atRow, int col, String title) {
        return addLabel(atRow, col, title, false);
    }

    public Label addLabel(int atRow, int col, String title, boolean isOnTtop) {
        Label label = new Label(title);
        label.setWordWrap(false);
        flexTable.setWidget(atRow, col, label);
        if (labelWidth >= 0)
            label.setWidth(labelWidth + "px");
        flexCellFormatter.setStylePrimaryName(atRow, col, "a-label-cell");
        if (isOnTtop)
            flexCellFormatter.addStyleName(atRow, col, "a-top-label-cell");
        return label;
    }

    public com.gwtexpress.client.ui.Button addButton(String id, String label) {
        com.gwtexpress.client.ui.Button btn = 
            new com.gwtexpress.client.ui.Button(id, label, inputFormListener);
        //        btn.setWidth(((label.length() * 10) + 20) + "px");
        getButtonBar().add(btn);
        return btn;
    }

    public ArrayList<Widget> getFormFields() {
        return childrenList;
    }

    public void setDirty(boolean dirty) {
        //  if (Log.isDebugEnabled()){
        String a = getID() + "[" + getFormTitle() + "]";
        System.out.println("Setting " + a + " to " + 
                           (dirty ? "dirty" : "clean"));
        //  }
        this.dirty = dirty;
    }
    boolean initialized = false;
    private long startTimeMillis;

    public void init() {
        if (!initialized) {
            initialized = true;
            if (Log.isDebugEnabled()) {
                startTimeMillis = System.currentTimeMillis();
            }
            if (subFormList != null) {
                Iterator itr = subFormList.keySet().iterator();
                while (itr.hasNext()) {
                    AbstractFormLayout subForm = 
                        (AbstractFormLayout)itr.next();
                    subForm.init();
                    if (subForm.flexTable.getRowCount() == 1 && 
                        subForm.flexTable.getWidget(0, 0) instanceof Label && 
                        subForm.getFormTitle() == null && 
                        subForm.isCompact()) {
                        int[] idx = subFormList.get(subForm);
                        this.flexTable.remove(subForm);
                        Label l = (Label)subForm.flexTable.getWidget(0, 0);
                        subForm.flexTable.removeCell(0, 0);
                        flexTable.getFlexCellFormatter().setColSpan(idx[0], 
                                                                    idx[1], 1);
                        flexCellFormatter.setStylePrimaryName(idx[0], idx[1], 
                                                              "a-label-cell");
                        flexTable.setWidget(idx[0], idx[1], l);
                        flexTable.setWidget(idx[0], idx[1] + 1, subForm);
                    }
                }
            }
            renderForm();
            if (Log.isDebugEnabled()) {
                long endTimeMillis = System.currentTimeMillis();
                float durationSeconds = 
                    (endTimeMillis - startTimeMillis) / 1000F;
                Log.debug("Render Duration for Form [" + getFormTitle() + 
                          "]: " + durationSeconds + " seconds");
            }
        }
    }

    public void setOrigColumnData(int colIdx, String data) {
        if (subForm && getParentForm() != null) {
            getParentForm().setOrigColumnData(colIdx, data);
        } else
            origData[colIdx] = data;
    }

    public void setDataValue(int row, int col, String value) {
        if (subForm && getParentForm() != null) {
            getParentForm().setDataValue(row, col, value);
        } else
            data[col] = value;
    }

    public String getDataValue(int row, int col) {
        if (subForm && getParentForm() != null) {
            return getParentForm().getDataValue(row, col);
        } else
            return data[col];
    }

    public String getOriginalDataValue(int row, int col) {
        if (subForm && getParentForm() != null) {
            return getParentForm().getOriginalDataValue(row, col);
        } else
            return origData[col];
    }

    public String getColumnData(int colIdx) {
        if (subForm && getParentForm() != null) {
            return getParentForm().getColumnData(colIdx);
        } else
            return data[colIdx];
    }

    public void addSubForm(int row, int col, 
                           AbstractFormLayout subFormLayout) {
        addSubForm(row, col, 1, 2, subFormLayout);
    }

    public void addSubForm(int row, int col, int rowSpan, int colSpan, 
                           AbstractFormLayout subFormLayout) {
        if (subFormList == null) {
            subFormList = new HashMap<AbstractFormLayout, int[]>();
        }
        subFormLayout.setInputFormListener(inputFormListener);
        if (!subFormList.containsKey(subFormLayout)) {
            subFormList.put(subFormLayout, 
                            new int[] { row, col, rowSpan, colSpan });
            subFormLayout.setParentForm(this);
            subFormLayout.data = data;
            subFormLayout.origData = origData;
        }
        Log.debug("Inserting sub form " + subFormLayout.getFormTitle() + 
                  " at (" + row + "," + col + ") in form " + getFormTitle());
        flexTable.setWidget(row, col, subFormLayout);
        if (colSpan > 1) {
            Log.debug("setColSpan at (" + row + "," + col + ") to " + colSpan);
            flexTable.getFlexCellFormatter().setColSpan(row, col, colSpan);
        }
        if (rowSpan > 1) {
            Log.debug("setRowSpan at (" + row + "," + col + ") to " + rowSpan);
            flexTable.getFlexCellFormatter().setRowSpan(row, col, rowSpan);
        }
    }

    public void setInputFormListener(InputFormListener inputFormListener) {
        this.inputFormListener = inputFormListener;
    }

    public InputFormListener getInputFormListener() {
        return inputFormListener;
    }

    public void setColumnTitles(String[] columnTitles) {
        this.columnTitles = columnTitles;
    }

    public String[] getColumnTitles() {
        return columnTitles;
    }

    public String getFormTitle() {
        return formTitle;
    }

    public void setSuggestMap(HashMap<SuggestBox2, int[]> suggestMap) {
        if (getParentForm() != null)
            getParentForm().setSuggestMap(suggestMap);
        else
            this.suggestMap = suggestMap;
    }

    public HashMap<SuggestBox2, int[]> getSuggestMap() {
        if (this.getParentForm() != null)
            return getParentForm().getSuggestMap();
        else {
            if (suggestMap == null)
                suggestMap = new HashMap<SuggestBox2, int[]>();
            return suggestMap;
        }
    }

    public void setParentForm(AbstractFormLayout parentForm) {
        this.parentForm = parentForm;
    }

    public AbstractFormLayout getParentForm() {
        return parentForm;
    }

    public void setLabelWidth(int labelWidth) {
        this.labelWidth = labelWidth;
    }

    public int getLabelWidth() {
        return labelWidth;
    }

    public void setFieldWidth(int fieldWidth) {
        this.fieldWidth = fieldWidth;
    }

    public int getFieldWidth() {
        return fieldWidth;
    }

    public void setFormType(int type) {
        this.type = type;
    }

    public int getFormType() {
        return type;
    }

    public void setCompact(boolean compact) {
        this.compact = compact;
    }

    public boolean isCompact() {
        return compact;
    }

    public HorizontalPanel createMessageField(String colName) {
        return createMessageField(metaData.getColumnIndex(colName));
    }

    HorizontalPanel createMessageField(int colIdx) {
        Widget ui = createField(colIdx);
        HorizontalPanel hp = new HorizontalPanel();
        if (fieldWidth >= 0)
            hp.setWidth(fieldWidth + "px");
        int width = columnWidth[colIdx];
        if (width > 20)
            width = 20;
        if (width < 5)
            width = 5;
        if (!isCompact())
            hp.setWidth(((width * 8) + 20) + "px");
        hp.add(ui);
        return hp;
    }

    public Widget createField(String colName) {
        return createField(metaData.getColumnIndex(colName));
    }

    Widget createField(final int colIdx) {
        Widget ui = null;

        boolean readOnly = false;

        if (readOnlyForm) {
            readOnly = true;
        }

        int width = columnWidth[colIdx];
        if (width > 20)
            width = 20;
        if (width < 5)
            width = 5;
        if ((type == CREATE_FORM && !editable[colIdx]) || 
            (type == EDIT_FORM && (!updatable[colIdx] || !editable[colIdx]))) {
            readOnly = true;
        }
        if (suggests != null && suggests[colIdx] != null && !readOnly) {
            //TextBox tDummy = new TextBox(0, colIdx);
            //Log.debug("In createField for SuggestBox " + suggests[colIdx] + 
            //          " for " + columnTitles[colIdx]);
            //            SuggestBox suggestBox = 
            //                new SuggestBox(new MultiWordSuggestOracle());
            final GLogixSuggestOracle oracle = new GLogixSuggestOracle();

            final SuggestBox2 suggestBox = new SuggestBox2(oracle);
            suggestBox.setText(getColumnData(colIdx));

            DeferredCommand.addCommand(new Command() {
                        public void execute() {
                            //Log.debug("Executing deffered command to populate suggestions for " + 
                            //          columnTitles[colIdx]);
                            GLogixUIBuilder.populateSuggest(suggestBox, oracle, 
                                                            suggests[colIdx], 
                                                            getColumnData(colIdx));
                        }
                    });
            suggestBox.setWidth((width * 8) + "px");
            ui = suggestBox;
            getSuggestMap().put(suggestBox, new int[] { 0, colIdx });
        } else if (lookups != null && lookups[colIdx] != null) {
            final ListBox lb = new ListBox(0, colIdx);
            if (required[colIdx] && (type == EDIT_FORM || type == CREATE_FORM))
                lb.setRequired(true);
            final AbstractFormLayout thisForm = this;
            DeferredCommand.addCommand(new Command() {
                        public void execute() {
                            GLogixUIBuilder.populateLookup(thisForm, lb, 
                                                           lookups[colIdx], 
                                                           getColumnData(colIdx));
                        }
                    });
            if (columnWidth[colIdx] > 30)
                lb.setWidth((width * 8) + "px");
            if (readOnly)
                lb.setEnabled(false);
            ui = lb;
        } else if (colTypes[colIdx] == 'V' && columnWidth[colIdx] == 1) {
            CheckBox cb = new CheckBox(0, colIdx);
            if ("Y".equals(getColumnData(colIdx))) {
                cb.setChecked(true);
            } else {
                cb.setChecked(false);
            }
            if (readOnly)
                cb.setEnabled(false);
            ui = cb;
        } else if (colTypes[colIdx] == 'P') {
            PasswordTextBox ptb = new PasswordTextBox(0, colIdx);
            ptb.setWidth((width * 8) + "px");
            //t.setName(metaData.getServiceName() + ":" + colIdx);
            ptb.setText(GLogixUIBuilder.decodeData(colTypes[colIdx], 
                                                   getColumnData(colIdx)));
            ptb.setReadOnly(readOnly);
            ui = ptb;
        } else if (colTypes[colIdx] == 'D') {
            DatePicker t = new DatePicker(0, colIdx);
            ((DatePicker)t).setDateFormatter(DateUtil.getDateFormat());
            width = 15;
            t.setMaxLength(10);
            t.setWidth((width * 8) + "px");
            t.setText(GLogixUIBuilder.decodeData(colTypes[colIdx], 
                                                 getColumnData(colIdx)));
            t.setReadOnly(readOnly);
            ui = t;
        } else if (colTypes[colIdx] == 'T') {
            DatePicker t = new DatePicker(0, colIdx);
            ((DatePicker)t).setDateFormatter(DateUtil.getDateTimeFormat());
            width = 20;
            t.setMaxLength(16);
            t.setWidth((width * 8) + "px");
            t.setText(GLogixUIBuilder.decodeData(colTypes[colIdx], 
                                                 getColumnData(colIdx)));
            t.setReadOnly(readOnly);
            ui = t;
        } else {
            TextBox t;
            if (colTypes[colIdx] == 'N') {
                t = new TextBoxNumber(0, colIdx);
                t.setMaxLength(columnWidth[colIdx]);
            } else {
                t = new TextBox(0, colIdx);
                t.setMaxLength(columnWidth[colIdx]);
            }
            t.setWidth((width * 8) + "px");
            //t.setName(metaData.getServiceName() + ":" + colIdx);
            t.setText(GLogixUIBuilder.decodeData(colTypes[colIdx], 
                                                 getColumnData(colIdx)));
            t.setReadOnly(readOnly);
            ui = t;
        }
        ui.setTitle(columnTitles[colIdx]);
        addField(columnNames[colIdx], ui);

        if (required[colIdx] && (type == EDIT_FORM || type == CREATE_FORM))
            ui.addStyleName("a-req-input");
        return ui;
    }

    public void setUpdateOnPost(boolean updateOnPost) {
        this.updateOnPost = updateOnPost;
    }

    public boolean isUpdateOnPost() {
        return updateOnPost;
    }

    public void setReadOnly(boolean readOnly) {
        readOnlyForm = readOnly;
        Log.debug("Setting form " + this.getFormTitle() + " as read-only");
        if (subFormList != null) {
            Iterator<AbstractFormLayout> sitr = 
                subFormList.keySet().iterator();
            while (sitr.hasNext()) {
                AbstractFormLayout sform = sitr.next();
                sform.setReadOnly(readOnly);
            }
        }
    }

    public boolean isReadOnly() {
        return readOnlyForm;
    }

    public void setFindButtonLabel(String findButtonLabel) {
        this.findButtonLabel = findButtonLabel;
    }

    public String getFindButtonLabel() {
        return findButtonLabel;
    }

    public void setCancelButtonLabel(String cancelButtonLabel) {
        this.cancelButtonLabel = cancelButtonLabel;
    }

    public String getCancelButtonLabel() {
        return cancelButtonLabel;
    }

    public void setResetButtonLabel(String resetButtonLabel) {
        this.resetButtonLabel = resetButtonLabel;
    }

    public String getResetButtonLabel() {
        return resetButtonLabel;
    }

    public void setUpdateButtonLabel(String updateButtonLabel) {
        this.updateButtonLabel = updateButtonLabel;
    }

    public String getUpdateButtonLabel() {
        return updateButtonLabel;
    }

    public void setCreateButtonLabel(String createButtonLabel) {
        this.createButtonLabel = createButtonLabel;
    }

    public String getCreateButtonLabel() {
        return createButtonLabel;
    }

    public void printLayout() {
        if (Log.isDebugEnabled()) {
            int rows = flexTable.getRowCount();
            int cols;
            boolean cellExists;
            Widget w;
            for (int r = 0; r < rows; r++) {
                cols = flexTable.getCellCount(r);
                for (int c = 0; c < cols; c++) {
                    cellExists = flexTable.isCellPresent(r, c);
                    if (cellExists) {
                        w = flexTable.getWidget(r, c);
                        if (w != null) {
                            Log.debug(r + "," + c + ": " + 
                                      w.getClass().getName());
                            if (w instanceof Label) {
                                Log.debug(r + "," + c + ": " + 
                                          ((Label)w).getText());
                            }
                            if (w instanceof InputFormLayout) {
                                Log.debug(r + "," + c + ": " + 
                                          ((InputFormLayout)w).getFormTitle());
                            }
                        } else
                            Log.debug(r + "," + c + ": EMPTY");

                        Log.debug("Col span=" + 
                                  flexCellFormatter.getColSpan(r, c));
                        Log.debug("Row span=" + 
                                  flexCellFormatter.getRowSpan(r, c));
                    } else {
                        Log.debug(r + "," + c + ": NO CELL");
                    }
                }
            }
        }
    }
}
