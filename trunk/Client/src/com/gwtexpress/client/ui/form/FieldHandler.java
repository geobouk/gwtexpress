package com.gwtexpress.client.ui.form;

import com.google.gwt.user.client.ui.Widget;

public interface FieldHandler {

    /**
     * Fires when the field is modified and passed basic data validate and needs to be validated funtionally. 
     * When multiple handlers exists, this will be fired only when all the previous handlers passed validation
     * @param field Field being validated
     * @return Error Message if invalid; else return null
     */
    String isValid(Widget field);

}
