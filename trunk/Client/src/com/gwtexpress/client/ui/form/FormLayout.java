package com.gwtexpress.client.ui.form;

import com.gwtexpress.client.ui.SuggestBox2;
import com.gwtexpress.client.ui.TextBox;

import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;

import net.mygwt.ui.client.event.Listener;

public interface FormLayout {
    public static final int SEARCH_FORM = 0;
    public static final int CREATE_FORM = 1;
    public static final int EDIT_FORM = 2;
    public static final int READONLY_FORM = 3;
    public static final int ADVANCED_SEARCH_FORM = 4;
    static final String SIMPLE_SEARCH = "Simple Search";
    static final String AND = "AND";
    static final String OR = "OR";
    static final String COMPLEX_SEARCH = "Complex Search";
    static final String NO_CHANGES_TO_POST = "No changes to post...";
    static final String RESOLVE_ERRORS = 
        "You must resolve the errors before submiting";

    String getID();

    public void onClick(Widget sender);

    void addListener(int eventType, Listener listener);

    void onLostFocus(Widget sender);

    boolean isFormValid();

    boolean validateField(int i, Widget f);

    void renderForm();

    void setMaxColumns(int maxColumns);

    int getMaxColumns();

    void clearForm();

    void resetForm();

    void initButtons();

    boolean isValid();

    void setDataValue(int row, int col, String value);

    String getDataValue(int row, int col);

    String getOriginalDataValue(int row, int col);

    void setVisible(boolean b);

    boolean isDirty();

    void setDirty(boolean dirty);

    void init();

    void addSubForm(int fromRow, int fromCol, 
                    AbstractFormLayout subFormLayout);

    void addSubForm(int fromRow, int toRow, int fromCol, int toCol, 
                    AbstractFormLayout subFormLayout);

    void setInputFormListener(InputFormListener inputFormListener);

    InputFormListener getInputFormListener();

    void setColumnTitles(String[] columnTitles);

    String[] getColumnTitles();

    void setSuggestMap(HashMap<SuggestBox2, int[]> suggestMap);

    HashMap<SuggestBox2, int[]> getSuggestMap();

    int getRowIndex(Widget f);

    int getColumnIndex(Widget f);
}
