package com.gwtexpress.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ChangeListenerCollection;

public class FileUpload extends com.google.gwt.user.client.ui.FileUpload {
    private ChangeListenerCollection changeListeners;

    public FileUpload() {
        super();
    }
    
    public void addChangeListener(ChangeListener listener) {
        if (changeListeners == null) {
            changeListeners = new ChangeListenerCollection();
            sinkEvents(Event.ONCHANGE);
        }
        changeListeners.add(listener);
    }

    public void onBrowserEvent(Event event) {
        int type = DOM.eventGetType(event);
        if (type == Event.ONCHANGE) {
            // Fire the change event.
            if (changeListeners != null) {
                changeListeners.fireChange(this);
            }
        } else {
            // Handles Focus and Click events.
            super.onBrowserEvent(event);
        }
        
    }
    
    
}
