package com.gwtexpress.client.ui.form;

import com.gwtexpress.client.rpc.model.GLogixMetaData;

import java.util.EventListener;

public interface FormListener extends EventListener {
    public static int EVENT_ADD = 1;
    public static int EVENT_REMOVE = 0;

    public void handleEvent(int event, InputFormLayout parent, 
                            GLogixMetaData metadata, String[] data);

}
