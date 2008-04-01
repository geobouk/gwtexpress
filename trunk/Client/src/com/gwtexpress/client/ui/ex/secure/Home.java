package com.gwtexpress.client.ui.ex.secure;

import com.gwtexpress.client.ui.ex.Page;

import net.mygwt.ui.client.widget.WidgetContainer;
import net.mygwt.ui.client.widget.layout.FillLayout;


public class Home extends Page {

    public Home() {
    }


    public void init() {
        initialized = true;
        FillLayout layout = new FillLayout();
        setLayout(layout);
    }

    public void onShow() {

    }


    protected void createWidget(WidgetContainer container) {
    }

    public String getPageTitle() {
        return "Home";
    }
}
