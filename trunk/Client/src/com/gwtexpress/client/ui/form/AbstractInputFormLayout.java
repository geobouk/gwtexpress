package com.gwtexpress.client.ui.form;

import com.gwtexpress.client.rpc.model.AbstractGLogixMetaData;
import com.gwtexpress.client.rpc.model.GLogixMetaData;

public abstract class AbstractInputFormLayout extends InputFormLayout {

    public AbstractInputFormLayout(String[] data, GLogixMetaData metaData, 
                                   int maxColumns, int type, boolean subForm, 
                                   String[] colNames, String formTitle) {
        super(data, metaData, maxColumns, type, subForm, 
              ((AbstractGLogixMetaData)metaData).getIndexByNames(colNames), 
              formTitle);
    }

    public abstract void renderForm();
}
