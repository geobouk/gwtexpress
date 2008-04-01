package com.gwtexpress.client.ui.ex.secure;

import com.gwtexpress.client.index;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.MyAccountMetaData;
import com.gwtexpress.client.ui.Button;
import com.gwtexpress.client.ui.PasswordTextBox;
import com.gwtexpress.client.ui.ex.ExpressPage;
import com.gwtexpress.client.ui.form.FieldHandler;
import com.gwtexpress.client.ui.form.FormLayout;
import com.gwtexpress.client.ui.form.InputFormLayout;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

import net.mygwt.ui.client.Events;
import net.mygwt.ui.client.Style;
import net.mygwt.ui.client.event.BaseEvent;
import net.mygwt.ui.client.event.Listener;
import net.mygwt.ui.client.widget.ContentPanel;
import net.mygwt.ui.client.widget.WidgetContainer;
import net.mygwt.ui.client.widget.layout.FillLayout;
import net.mygwt.ui.client.widget.layout.RowData;
import net.mygwt.ui.client.widget.layout.RowLayout;


public class MyAccount extends ExpressPage {
    private GLogixMetaData metaData = new MyAccountMetaData();
    private String title = "My Account";

    public MyAccount() {
        setShowResults(false);
    }

    public String getPageTitle() {
        return title;
    }

    public GLogixMetaData getMetaData() {
        return metaData;
    }

    void setSize(InputFormLayout form) {
        form.setFieldWidth(150);
        form.setLabelWidth(120);
    }

    public InputFormLayout getInputFormLayout(GLogixMetaData metaData, 
                                              String[] row) {
        int type;
        type = FormLayout.EDIT_FORM;
        final InputFormLayout form = 
            new InputFormLayout(index.myAccountData, metaData, 1, type, false, 
                                new String[] { "FIRST_NAME", "LAST_NAME", 
                                               "DISPLAY_NAME" }, 
                                "Account Information");

        form.setUpdateOnPost(true);
        form.addListener(Events.Close, new Listener() {
                    public void handleEvent(BaseEvent be) {
                        History.newItem("Home");
                    }
                });
        setSize(form);
        form.setShowButtons(false);


        InputFormLayout pwdChangeForm = 
                new InputFormLayout(null, metaData, 1, type, true, new String[] { "OLD_PASSWORD", 
                                                                                  "NEW_PASSWORD1", 
                                                                                  "NEW_PASSWORD2" }, 
                                    "Change Password");
        form.addSubForm(6, 0, pwdChangeForm);

        setSize(pwdChangeForm);
        return form;
    }

    public void init() {
        initialized = true;
        final GLogixMetaData metaData = getMetaData();
        FillLayout layout = new FillLayout();
        setLayout(layout);
        WidgetContainer container = new WidgetContainer();
        final InputFormLayout pwdChangeForm = getInputFormLayout(metaData, null);
        pwdChangeForm.init();

        Button saveBtn = new Button("SAVE", "Save");
        saveBtn.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        pwdChangeForm.postData();
                    }
                });
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(saveBtn);

        ContentPanel createRegion = new ContentPanel(Style.NONE);
        createRegion.setFrame(true);
        createRegion.setLayout(new RowLayout());
        createRegion.setScrollEnabled(true);
        createRegion.add(pwdChangeForm, new RowData(RowData.FILL_HORIZONTAL));
        createRegion.add(hp, new RowData(RowData.FILL_HORIZONTAL));
        container.setLayout(new FillLayout());
        container.add(createRegion, new RowData(RowData.FILL_HORIZONTAL));

        add(container);
        FieldHandler fh = new FieldHandler() {
                public String isValid(Widget field) {
                    PasswordTextBox pw1 = 
                        (PasswordTextBox)pwdChangeForm.getField("NEW_PASSWORD1");
                    PasswordTextBox pw2 = 
                        (PasswordTextBox)pwdChangeForm.getField("NEW_PASSWORD2");
                    if (field == pw1 || field == pw2) {
                        String p1 = pw1.getText();
                        String p2 = pw2.getText();
                        if (p1 != null && p2 != null && p1.length() > 0 && 
                            p2.length() > 0 && !p1.equals(p2)) {
                            return "New passwords doesn't match each other. Please verify.";
                        }
                    }
                    return null;
                }
            };
        pwdChangeForm.addFieldHandler("OLD_PASSWORD", fh);
        pwdChangeForm.addFieldHandler("NEW_PASSWORD1", fh);
        pwdChangeForm.addFieldHandler("NEW_PASSWORD2", fh);
    }
}
