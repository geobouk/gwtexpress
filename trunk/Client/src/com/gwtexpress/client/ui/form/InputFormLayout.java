package com.gwtexpress.client.ui.form;

import com.allen_sauer.gwt.log.client.Log;

import com.gwtexpress.client.rpc.model.AbstractGLogixMetaData;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.ui.Button;
import com.gwtexpress.client.ui.CheckBox;
import com.gwtexpress.client.ui.SuggestBox2;
import com.gwtexpress.client.ui.ex.GLogixUIBuilder;
import com.gwtexpress.client.ui.table.SimpleTable;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.mygwt.ui.client.widget.Info;


public class InputFormLayout extends AbstractFormLayout {
    SimpleTable resultTable;
    AdvancedSearchForm advancedSearchForm;
    public Button saveBtn, findBtn, cancelBtn;
    private boolean showLabels = true;
    private boolean labelsOnTop = false;

    public HashMap<String, ArrayList<InputFormLayout>> childForms;
    boolean debug = true;


    public InputFormLayout(String[] data, GLogixMetaData metaData, int type) {
        this(data, metaData, type, false);
    }

    public InputFormLayout(String[] data, GLogixMetaData metaData, int type, 
                           boolean subForm) {
        this(data, metaData, 2, type, subForm);
    }

    public InputFormLayout(String[] data, GLogixMetaData metaData, 
                           int maxColumns, int type) {
        this(data, metaData, maxColumns, type, false);
    }

    public InputFormLayout(String[] data, GLogixMetaData metaData, 
                           int maxColumns, int type, boolean subForm) {
        this(data, metaData, maxColumns, type, subForm, new int[0], null);
        if (type == EDIT_FORM || type == CREATE_FORM) {
            colIdxs = metaData.getVisibleColumnIndexes();
        } else {
            colIdxs = metaData.getQueryableColumnIndexes();
        }
    }

    public InputFormLayout(String[] data, GLogixMetaData metaData, 
                           int maxColumns, int type, boolean subForm, 
                           String[] colNames, String formTitle) {
        this(data, metaData, maxColumns, type, subForm, 
             ((AbstractGLogixMetaData)metaData).getIndexByNames(colNames), 
             formTitle);
    }

    public InputFormLayout(String[] data, GLogixMetaData metaData, 
                           int maxColumns, int type, boolean subForm, 
                           int[] colIdxs, String formTitle) {
        super(metaData, subForm, formTitle);
        this.formTitle = formTitle;
        this.type = type;
        this.colIdxs = colIdxs;
        if (!subForm) {
            this.origData = data;
            this.data = new String[data.length];
            for (int i = 0; i < data.length; i++) {
                this.data[i] = data[i];
            }
        }
        this.metaData = metaData;
        this.maxColumns = maxColumns;
        columnWidth = metaData.getColumnWidth();
        colTypes = metaData.getColumnTypes();
        lookups = metaData.getColumnLookupNames();
        suggests = metaData.getColumnSuggestNames();
    }

    InputFormLayout masterForm;

    public String getID() {
        return metaData.getServiceName();
    }

    public boolean removeFromMasterForm() {
        InputFormLayout master = getMasterForm();
        if (master == null || master.childForms == null) {
            return false;
        }
        ArrayList<InputFormLayout> forms = 
            master.childForms.get(metaData.getServiceName());
        return forms.remove(this);
    }

    public boolean isFirstChild(InputFormLayout childForm) {
        if (childForms == null)
            return false;
        String name = childForm.metaData.getServiceName();
        ArrayList<InputFormLayout> list = childForms.get(name);
        if (list == null)
            return false;
        return (list.indexOf(childForm) == 0);
    }

    public String[] getLastChildData(InputFormLayout childForm) {
        return getLastChildData(childForm.metaData.getServiceName());
    }

    public String[] getLastChildData(String name) {
        if (childForms == null)
            return null;
        ArrayList<InputFormLayout> list = childForms.get(name);
        if (list == null)
            return null;
        int idx = list.size();
        if (idx <= 0)
            return null;
        return list.get(idx - 1).getData();
    }

    public String[] getPreviousChildData(InputFormLayout childForm) {
        if (childForms == null)
            return null;
        String name = childForm.metaData.getServiceName();
        ArrayList<InputFormLayout> list = childForms.get(name);
        if (list == null)
            return null;
        int idx = list.indexOf(childForm);
        if (idx <= 0)
            return null;
        return list.get(idx - 1).getData();
    }

    public boolean isLastChild(InputFormLayout childForm) {
        if (childForms == null)
            return false;
        String name = childForm.metaData.getServiceName();
        ArrayList<InputFormLayout> list = childForms.get(name);
        if (list == null)
            return false;
        return (list.indexOf(childForm) == list.size() - 1);
    }

    public void removeChildForms() {
        if (childForms != null) {
            Iterator<String> itr = childForms.keySet().iterator();
            String childName;
            while (itr.hasNext()) {
                childName = itr.next();
                if (!"this".equals(childName))
                    childForms.get(childName).clear();
            }
        }
    }

    public boolean removeChildForm(InputFormLayout childForm) {
        if (childForms == null)
            return false;
        String name = childForm.metaData.getServiceName();
        ArrayList<InputFormLayout> list = childForms.get(name);
        if (list == null)
            return false;
        childForm.clearForm(true);
        return list.remove(childForm);
    }

    public void addChildForm(InputFormLayout childForm) {
        if (childForms == null) {
            childForms = new HashMap<String, ArrayList<InputFormLayout>>();
            ArrayList<InputFormLayout> list = new ArrayList<InputFormLayout>();
            list.add(this);
            childForms.put("this", list);
        }
        String name = childForm.metaData.getServiceName();
        ArrayList<InputFormLayout> list = childForms.get(name);
        if (list == null)
            list = new ArrayList<InputFormLayout>();
        list.add(childForm);
        childForm.setMasterForm(this);
        childForms.put(name, list);
    }

    public void clearForm() {
        clearForm(false);
    }

    public void clearForm(boolean force) {
        if (force) {
            setDirty(true);
        }
        if (!isDirty())
            return;
        if (childForms != null) {
            Iterator<String> fItr = childForms.keySet().iterator();
            while (fItr.hasNext()) {
                String fName = fItr.next();
                if ("this".equals(fName))
                    continue;
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null)
                    continue;
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    form.origData = form.metaData.createRow();
                    form.setDirty(true);
                    form.resetForm();
                }
            }
        }
        origData = metaData.createRow();
        resetForm();
    }

    public void setDirty(boolean dirty) {
        super.setDirty(dirty);
        if (isDirty() && getMasterForm() != null)
            getMasterForm().setDirty(dirty);
        initSaveBtn();
    }

    public void setFormType(int type) {
        this.type = type;
        if (saveBtn != null) {
            if (type == EDIT_FORM)
                saveBtn.setHTML(this.updateButtonLabel);
            else if (type == CREATE_FORM)
                saveBtn.setHTML(this.createButtonLabel);
        }
        if (childForms != null) {
            Iterator<String> fItr = childForms.keySet().iterator();
            while (fItr.hasNext()) {
                String fName = fItr.next();
                if ("this".equals(fName))
                    continue;
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null)
                    continue;
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    form.setFormType(type);
                }
            }
        }
    }

    public int getFormType() {
        return type;
    }

    public void resetForm() {
        if (childForms != null) {
            Iterator<String> fItr = childForms.keySet().iterator();
            while (fItr.hasNext()) {
                String fName = fItr.next();
                if ("this".equals(fName))
                    continue;
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null)
                    continue;
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    form.resetForm();
                }
            }
        }
        super.resetForm();
    }

    public void setReadOnly(boolean readOnly) {
        if (childForms != null) {
            Iterator<String> fItr = childForms.keySet().iterator();
            while (fItr.hasNext()) {
                String fName = fItr.next();
                if ("this".equals(fName))
                    continue;
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null)
                    continue;
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    form.setReadOnly(readOnly);
                }
            }
        }
        super.setReadOnly(readOnly);
    }

    public void renderForm() {
        Log.debug("Rending form " + getFormTitle() + ", Service: " + 
                  metaData.getServiceName());
        int j = 0;
        if (type == SEARCH_FORM && advancedSearchForm == null) {
            advancedSearchForm = new AdvancedSearchForm(metaData);
            advancedSearchForm.setVisible(false);
            advancedSearchForm.setSimpleSearch(this);
            advancedSearchForm.setResultTable(resultTable);
            add(advancedSearchForm);
        }
        int labelRowSkip = 1;
        if (labelsOnTop)
            labelRowSkip = 2;
        for (int row = 0; 
             (row < (((colIdxs.length / maxColumns) + 1) * labelRowSkip)) || 
             (j < colIdxs.length); row += labelRowSkip) {
            int labelSkip = 2;
            if (!showLabels)
                labelSkip = 1;
            for (int c = 0; c < (maxColumns * labelSkip) && j < colIdxs.length; 
                 c += labelSkip) {
                int totalCells = 0, totalRows = 0;
                boolean b = flexTable.isCellPresent(row, c);
                Widget w = null;
                if (b)
                    w = flexTable.getWidget(row, c);
                if (b && w != null) {
                    Log.debug("(" + row + "," + c + ") Found " + 
                              w.getClass().getName());
                    continue;
                }
                for (int k = 0; 
                     k <= c && flexTable.isCellPresent(row, k) && flexTable.getWidget(row, 
                                                                                      k) != 
                     null; k++) {
                    int colspan = 
                        flexTable.getFlexCellFormatter().getColSpan(row, k);
                    totalCells += 1;
                    if (colspan > 1) {
                        //k += colspan - 1;
                        totalCells += colspan - 1;
                    }
                }
                Log.debug("Total cells=" + totalCells);
                if (totalCells >= (c + 1)) {
                    Log.debug("Total cells=" + totalCells + ">=" + (c + 1) + 
                              " hence continue...");
                    continue;
                }
                for (int k = 0; 
                     k <= row && flexTable.isCellPresent(k, c) && flexTable.getWidget(k, 
                                                                                      c) != 
                     null; k++) {
                    int rowSpan = 
                        flexTable.getFlexCellFormatter().getRowSpan(k, c);
                    totalRows += 1;
                    if (rowSpan > 1) {
                        //k += colspan - 1;
                        totalRows += rowSpan - 1;
                    }
                }
                Log.debug("Total rows=" + totalRows);
                if (totalRows >= (row + 1)) {
                    Log.debug("Total rows=" + totalCells + ">=" + (row + 1) + 
                              " hence continue...");
                    continue;
                }

                //getFormFields().add(ui);
                final HorizontalPanel hp = createMessageField(colIdxs[j]);
                if (showLabels) {
                    Log.debug("Inserting label at (" + row + "," + c + ")");
                    Label label = 
                        addLabel(row, c, columnTitles[colIdxs[j]], labelsOnTop);
                    labelList.put(hp.getWidget(0), label);
                    label.addClickListener(new ClickListener() {
                                public void onClick(Widget sender) {
                                    Widget w = hp.getWidget(0);
                                    if (w instanceof CheckBox) {
                                        if (((CheckBox)w).isChecked())
                                            ((CheckBox)w).setChecked(false);
                                        else
                                            ((CheckBox)w).setChecked(true);
                                    }
                                    if (w instanceof SuggestBox2) {
                                        ((SuggestBox2)w).setFocus(true);
                                    } else {
                                        ((FocusWidget)w).setFocus(true);
                                    }
                                }
                            });
                }
                if (isCompact())
                    flexCellFormatter.addStyleName(row, c, 
                                                   "a-comp-label-cell");
                int fieldCol = 1 + c;
                if (!showLabels || labelsOnTop)
                    fieldCol = c;
                int fieldRow = row;
                if (labelsOnTop)
                    fieldRow = row + 1;
                Log.debug("Inserting widget at (" + fieldRow + "," + fieldCol + 
                          ")");
                flexTable.setWidget(fieldRow, fieldCol, hp);
                flexCellFormatter.setStylePrimaryName(fieldRow, fieldCol, 
                                                      "a-content-cell");
                j++;
            }
        }
        addButtons();
        if (readOnlyForm) {
            setShowButtons(false);
        }
        add(flexTable);
    }
    boolean showButtons = true;

    public HorizontalPanel createButtons() {
        if (type == CREATE_FORM) {
            saveBtn = addButton("SAVE", this.createButtonLabel);
        } else if (type == EDIT_FORM) {
            saveBtn = addButton("SAVE", this.updateButtonLabel);
        } else {
            findBtn = addButton("FIND", this.findButtonLabel);
            initFindBtn();
        }
        if (type != EDIT_FORM)
            addClearButton();
        if (type == CREATE_FORM) {
            Button cancel = addButton("CANCEL", this.cancelButtonLabel);
        }
        if (type == EDIT_FORM) {
            Button reset = addButton("RESET", this.resetButtonLabel);
            Button cancel = addButton("CANCEL", cancelButtonLabel);
        } else if (type == SEARCH_FORM) {
            Button advSearch = addButton("ADV_SEARCH", COMPLEX_SEARCH);
        }
        return getButtonBar();
    }

    public void addButtons() {
        if (!subForm) {
            int btnBarRow = flexTable.getRowCount();
            flexTable.setWidget(btnBarRow, 0, createButtons());
            flexTable.getFlexCellFormatter().setHorizontalAlignment(btnBarRow, 
                                                                    0, 
                                                                    HasHorizontalAlignment.ALIGN_RIGHT);
            int totCells = flexTable.getCellCount(0);
            if (btnBarRow > 0 && 
                totCells < flexTable.getCellCount(btnBarRow - 1)) {
                totCells = flexTable.getCellCount(btnBarRow - 1);
            }
            flexTable.getFlexCellFormatter().setColSpan(btnBarRow, 0, 
                                                        totCells);
        }
    }

    public void fetchMDData(String[] data) {
        if (type == EDIT_FORM) {
            String[] param = metaData.createBlankRow();
            int[] pkIdxs = metaData.getPkColumnIndexs();
            for (int i = 0; i < pkIdxs.length; i++) {
                param[pkIdxs[i]] = data[pkIdxs[i]];
            }
            GLogixUIBuilder.fetchMDData(this, metaData, param);
        }
    }

    public void setData(String[] data) {
        Log.debug("In setData for " + getID() + "[" + getFormTitle() + "]");
        this.data = data;
        this.origData = data;
        setDirty(true);
        resetForm();
    }

    public String[] getData() {
        return data;
    }

    public void onClick(Widget sender) {
        super.onClick(sender);
        Button btn = (Button)sender;
        if ("FIND".equals(btn.getId())) {
            GLogixUIBuilder.fetchData(resultTable, metaData, data);
        } else if ("ADV_SEARCH".equals(btn.getId())) {
            advancedSearchForm.init();
            flexTable.setVisible(false);
            advancedSearchForm.setVisible(true);
        } else if ("SAVE".equals(btn.getId())) {
            if (isDirty()) {
                postData();
            } else {
                Info.show("Information", NO_CHANGES_TO_POST, "");
            }
        }
    }

    public void setData(HashMap<String, ArrayList<String[]>> data, 
                        boolean newData) {
        //clearForm();
        Log.debug("In (master) setData for " + getID() + "[" + getFormTitle() + 
                  "] newData=" + newData);

        if (childForms != null) {
            Iterator<String> fItr = childForms.keySet().iterator();
            while (fItr.hasNext()) {
                String fName = fItr.next();
                if ("this".equals(fName))
                    continue;
                ArrayList<String[]> fData = data.get(fName);
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null || list.size() == 0) {
                    Log.debug("No childForms for " + fName);
                    continue;
                }
                if (fData == null || fData.size() == 0) {
                    Log.debug("No data for childForms " + fName);
                    continue;
                }
                int row = 0;
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    if (!form.isDirty() && !newData) {
                        Log.debug("Form " + form.getID() + "[" + 
                                  form.getFormTitle() + "][" + i + 
                                  "] not dirty... hence not setting the data");
                        continue;
                    }
                    if (fData.size() > row) {
                        form.setData(fData.get(row++));
                    } else {
                        Log.error("ERROR: Form " + form.getID() + "[" + 
                                  form.getFormTitle() + "][" + i + 
                                  "] is dirty but data missing fData.size()=" + 
                                  fData.size() + ", row=" + row);
                    }
                }
            }
        }
        ArrayList<String[]> masterData = data.get("this");
        if (masterData != null) {
            setData(masterData.get(0));
        }
    }

    public HashMap<String, ArrayList<String[]>> getMData() {
        if (subForm) {
            GLogixUIBuilder.showError("Developer Error...", 
                                      "Sub Form cannot return MData...");
            return null;
        }
        HashMap<String, ArrayList<String[]>> childFormsData = 
            new HashMap<String, ArrayList<String[]>>();
        if (childForms == null) {
            ArrayList<String[]> list = new ArrayList<String[]>();
            list.add(getData());
            childFormsData.put("this", list);
        } else {
            Iterator<String> fItr = childForms.keySet().iterator();
            boolean hasErrors = false;
            while (fItr.hasNext()) {
                String fName = fItr.next();
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null)
                    continue;
                ArrayList<String[]> dlist2 = new ArrayList<String[]>();
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    dlist2.add(form.getData());
                }
                childFormsData.put(fName, dlist2);
            }
        }
        return childFormsData;
    }

    public void postData() {
        if (subForm) {
            GLogixUIBuilder.showError("Developer Error...", 
                                      "Sub Form cannot be posted...");
            return;
        }
        if (!isDirty()) {
            Info.show("Information", NO_CHANGES_TO_POST, "");
            return;
        }        
        if (childForms != null) {
            HashMap<String, ArrayList<String[]>> childFormsData = 
                new HashMap<String, ArrayList<String[]>>();

            Iterator<String> fItr = childForms.keySet().iterator();
            boolean hasErrors = false;
            while (fItr.hasNext()) {
                String fName = fItr.next();
                ArrayList<InputFormLayout> list = childForms.get(fName);
                if (list == null)
                    continue;
                ArrayList<String[]> dlist2 = new ArrayList<String[]>();
                for (int i = 0; i < list.size(); i++) {
                    InputFormLayout form = list.get(i);
                    if (form.isDirty()) {
                        if (!form.isFormValid()) {
                            hasErrors = true;
                        } else {
                            dlist2.add(form.getData());
                        }
                    }
                }
                childFormsData.put(fName, dlist2);
            }
            if (hasErrors) {
                Info.show("Error...", RESOLVE_ERRORS, "");
                return;
            } else {
                GLogixUIBuilder.postData(this, metaData, childFormsData);
            }
            //            fItr = childForms.keySet().iterator();
            //            while (fItr.hasNext()) {
            //                String dName = fItr.next();
            //                ArrayList<InputFormLayout> list = childForms.get(dName);
            //                if (list == null)
            //                    continue;
            //                ArrayList<String[]> dlist2 = new ArrayList<String[]>();
            //                for (int i = 0; i < list.size(); i++) {
            //                    InputFormLayout form = list.get(i);
            //                    dlist2.add(form.getData());
            //                }
            //                childFormsData.put(dName, dlist2);
            //            }
            //            GLogixUIBuilder.postData(this, metaData, childFormsData);
        } else {
            if (isFormValid()) {
                GLogixUIBuilder.postData(this, metaData, data);
                for (int i = 0; i < data.length; i++) {
                    setOrigColumnData(i, getColumnData(i));
                }
            } else {
                Info.show("Error...", RESOLVE_ERRORS, "");
            }
        }
    }

    public void initButtons() {
        initFindBtn();
    }

    private void initSaveBtn() {
        if (saveBtn == null)
            return;
        if (isDirty())
            saveBtn.setEnabled(true);
        else
            saveBtn.setEnabled(false);
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
        if (advancedSearchForm != null)
            advancedSearchForm.setResultTable(resultTable);
    }

    public SimpleTable getResultTable() {
        return resultTable;
    }

    public void setVisible(boolean b) {
        flexTable.setVisible(b);
    }

    public String[] getOrigData() {
        return origData;
    }

    public void setShowButtons(boolean showButtons) {
        this.showButtons = showButtons;
        getButtonBar().setVisible(showButtons);
    }

    public boolean isShowButtons() {
        return showButtons;
    }

    public void log(String msg) {
        System.out.println(msg);
    }

    public void setMasterForm(InputFormLayout masterForm) {
        this.masterForm = masterForm;
    }

    public InputFormLayout getMasterForm() {
        return masterForm;
    }

    public void setShowLabels(boolean showLabels) {
        this.showLabels = showLabels;
    }

    public boolean isShowLabels() {
        return showLabels;
    }

    public void setLabelsOnTop(boolean labelsOnTop) {
        this.labelsOnTop = labelsOnTop;
    }

    public boolean isLabelsOnTop() {
        return labelsOnTop;
    }
}
