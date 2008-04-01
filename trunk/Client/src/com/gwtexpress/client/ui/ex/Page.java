package com.gwtexpress.client.ui.ex;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import net.mygwt.ui.client.MyGWT;
import net.mygwt.ui.client.widget.WidgetContainer;
import net.mygwt.ui.client.widget.layout.FillLayout;
import net.mygwt.ui.client.widget.layout.RowData;


public abstract class Page extends WidgetContainer {

    public boolean initialized = false;

    protected abstract void createWidget(WidgetContainer container);

    public abstract void onShow();

    public String getVersion() {
        return "1.0";
    }

    public abstract String getPageTitle();

    public final String getId() {
        String s = GWT.getTypeName(this);
        return s.substring(s.lastIndexOf(".") + 1, s.length());
    }

    public void init() {
        initialized = true;
        FillLayout layout = new FillLayout();
        setLayout(layout);
        final Page fPage = this;
        WidgetContainer container = new WidgetContainer();
        container.setScrollEnabled(true);
        add(container, new RowData(RowData.FILL_BOTH));
        createWidget(container);
    }

}
