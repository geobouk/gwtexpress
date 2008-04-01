package com.gwtexpress.client.ui;

import com.google.gwt.user.client.ui.ClickListener;

public class Button extends com.google.gwt.user.client.ui.Button {
    String id;
    public Button(String id) {
        super();
        this.id = id;
    }

    public Button(String id, String html) {
        this(id);
        setHTML(html);
    }

    public Button(String id, String html, ClickListener listener) {
        this(id);
        setHTML(html);
        addClickListener(listener);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
