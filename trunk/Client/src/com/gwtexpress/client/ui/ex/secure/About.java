package com.gwtexpress.client.ui.ex.secure;

import com.google.gwt.core.client.GWT;

import com.gwtexpress.client.ui.ex.Page;

import net.mygwt.ui.client.widget.WidgetContainer;


public class About extends Page {

    WidgetContainer aboutContainer;

    public About() {
    }

    protected void createWidget(WidgetContainer container) {
        aboutContainer = container;

    }

    public void onShow() {
        aboutContainer.removeAll();


        String url = GWT.getHostPageBaseURL();
        String moduleBaseURL = GWT.getModuleBaseURL();
        String moduleName = GWT.getModuleName();

        aboutContainer.addText("getHostPageBaseURL=" + url);
        aboutContainer.addText("getModuleBaseURL=" + moduleBaseURL);
        aboutContainer.addText("getModuleName=" + moduleName);
    }

    public String getPageTitle() {
        return "About";
    }
}
