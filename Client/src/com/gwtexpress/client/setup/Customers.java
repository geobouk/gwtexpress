package com.gwtexpress.client.setup;

import com.gwtexpress.client.rpc.model.CustomersMetaData;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.ui.ex.ExpressPage;
import com.gwtexpress.client.ui.form.FormLayout;
import com.gwtexpress.client.ui.form.InputFormLayout;


public class Customers extends ExpressPage {

    private GLogixMetaData metaData = new CustomersMetaData();
    private String title = "Customers";

    public String getPageTitle() {
        return title;
    }

    public GLogixMetaData getMetaData() {
        return metaData;
    }

    public InputFormLayout getInputFormLayout(GLogixMetaData metaData, 
                                              String[] row) {
        int type;
        if (row == null) {
            row = metaData.createRow();
            type = FormLayout.CREATE_FORM;
        } else {
            type = FormLayout.EDIT_FORM;
        }
        final InputFormLayout mainForm = 
            new InputFormLayout(row, metaData, 2, type, false, 
                                new String[] { "customerNumber", 
                                               "customerName", 
                                               "contactLastName", 
                                               "contactFirstName", "phone", 
                                               "salesRepEmployeeNumber", 
                                               "creditLimit", 
                                               "email_address" }, 
                                "Customer Details");
        InputFormLayout activeDatesForm = 
            new InputFormLayout(row, metaData, 2, type, true, 
                                new String[] { "start_date", "end_date" }, 
                                "Active Dates");
        activeDatesForm.setCompact(true);
        mainForm.addSubForm(5, 0, 1, 4, activeDatesForm);
        InputFormLayout billToAddrForm = 
            new InputFormLayout(row, metaData, 1, type, true, 
                                new String[] { "b_addressLine1", 
                                               "b_addressLine2", "b_city", 
                                               "b_state", "b_postalCode", 
                                               "b_country" }, 
                                "Bill To Address");

        mainForm.addSubForm(6, 0, billToAddrForm);
        InputFormLayout shipToAddrForm = 
            new InputFormLayout(row, metaData, 1, type, true, 
                                new String[] { "s_addressLine1", 
                                               "s_addressLine2", "s_city", 
                                               "s_state", "s_postalCode", 
                                               "s_country" }, 
                                "Ship To Address");
        mainForm.addSubForm(6, 1, shipToAddrForm);
        return mainForm;
    }
}
