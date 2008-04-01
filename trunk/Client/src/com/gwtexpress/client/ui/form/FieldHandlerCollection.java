package com.gwtexpress.client.ui.form;

import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

public class FieldHandlerCollection extends ArrayList<FieldHandler> {

    public String fireIsValid(Widget sender) {
        String errMsg = null;
        for (FieldHandler listener : this) {
            errMsg = listener.isValid(sender);
            if (errMsg != null)
                break;
        }
        return errMsg;
    }
}
