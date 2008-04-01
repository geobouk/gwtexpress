package com.gwtexpress.client.ui.form;

import com.gwtexpress.client.datePicker.DatePicker;
import com.gwtexpress.client.rpc.model.GLogixMetaData;

import com.gwtexpress.client.ui.CheckBox;
import com.gwtexpress.client.ui.SuggestionEvent2;
import com.gwtexpress.client.ui.ex.GLogixUIBuilder;
import com.gwtexpress.client.ui.ListBox;
import com.gwtexpress.client.ui.PasswordTextBox;
import com.gwtexpress.client.ui.SuggestBox2;
import com.gwtexpress.client.ui.SuggestionHandler2;
import com.gwtexpress.client.ui.TextArea;
import com.gwtexpress.client.ui.TextBox;

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusListener;
import com.google.gwt.user.client.ui.Widget;

public class InputFormListener implements ChangeListener, FocusListener, 
                                          ClickListener, SuggestionHandler2 {
    FormLayout form;
    GLogixMetaData metaData;
    char[] colTypes;
    Widget currentField;

    public InputFormListener(FormLayout form, GLogixMetaData metaData) {
        this.metaData = metaData;
        this.form = form;
        colTypes = metaData.getColumnTypes();
    }

    public void onChange(Widget sender) {
        form.setDirty(true);
        if (sender instanceof ListBox) {
            ListBox t = ((ListBox)sender);
            int i = form.getColumnIndex(sender);
            if (form.validateField(i, t)) {
                form.setDataValue(t.getRowIndex(), i, 
                                  t.getValue(t.getSelectedIndex()));
            }
        } else if (sender instanceof TextBox) {
            TextBox t = ((TextBox)sender);
            int i = form.getColumnIndex(sender);
            if (t.getText() != null)
                t.setText(t.getText().trim());
            if (form.validateField(i, t)) {
                form.setDataValue(t.getRowIndex(), i, GLogixUIBuilder.encodeData(colTypes[i], 
                                                             t.getText()));
            }
        } else if (sender instanceof DatePicker) {
            DatePicker t = ((DatePicker)sender);
            int i = form.getColumnIndex(sender);
            if (t.getText() != null)
                t.setText(t.getText().trim());
            if (form.validateField(i, t)) {
                form.setDataValue(t.getRowIndex(), i, GLogixUIBuilder.encodeData(colTypes[i], 
                                                             t.getText()));
            }
        } else if (sender instanceof PasswordTextBox) {
            PasswordTextBox t = ((PasswordTextBox)sender);
            int i = form.getColumnIndex(sender);
            if (t.getText() != null)
                t.setText(t.getText().trim());
            if (form.validateField(i, t)) {
                form.setDataValue(t.getRowIndex(), i, GLogixUIBuilder.encodeData(colTypes[i], 
                                                             t.getText()));
            }
        } else if (sender instanceof TextArea) {
            TextArea t = ((TextArea)sender);
            int i = form.getColumnIndex(sender);
            if (t.getText() != null)
                t.setText(t.getText().trim());
            if (form.validateField(i, t)) {
                form.setDataValue(t.getRowIndex(), i, GLogixUIBuilder.encodeData(colTypes[i], 
                                                             t.getText()));
            }
        } else if (sender instanceof SuggestBox2) {
            SuggestBox2 t = ((SuggestBox2)sender);
            int row = form.getRowIndex(sender);
            int col = form.getColumnIndex(sender);
            if (t.getText() != null)
                t.setText(t.getText().trim());
            if (form.validateField(col, t)) {
                form.setDataValue(row, col, GLogixUIBuilder.encodeData(colTypes[col], 
                                                             t.getText()));
            }
        }
        form.initButtons();
    }

    public void onFocus(Widget sender) {
        if (currentField!=null) currentField.removeStyleName("current_field");
        sender.addStyleName("current_field");
        currentField = sender;
    }

    public void onLostFocus(Widget sender) {
        sender.removeStyleName("current_field");
        currentField = null;
        //form.onLostFocus(sender);
    }

    public void onClick(Widget sender) {
        if (sender instanceof CheckBox) {
            form.setDirty(true);
            CheckBox t = ((CheckBox)sender);
            int row = form.getRowIndex(sender);
            int col = form.getColumnIndex(sender);
            if (t.isChecked()) {
                form.setDataValue(row, col, "Y");
            } else {
                form.setDataValue(row, col, "N");
            }
            form.initButtons();
        } else
            form.onClick(sender);
    }

    public void onSuggestionSelected(SuggestionEvent2 event) {
        String value = event.getSelectedSuggestion().getReplacementString();
        SuggestBox2 t = (SuggestBox2)event.getSource();
        int row = form.getRowIndex(t);
        int col = form.getColumnIndex(t);
        if (t.getText() != null)
            t.setText(t.getText().trim());
        form.setDirty(true);
        if (form.validateField(col, t)) {
            form.setDataValue(row, col, GLogixUIBuilder.encodeData(colTypes[col], 
                                                         value));
        }
        // onChange doesn't fire in some cases and hence perform initButtons here as well
        form.initButtons();
    }

}
