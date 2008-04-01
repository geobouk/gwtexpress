package com.gwtexpress.client.ui.table;

import java.util.List;

import net.mygwt.ui.client.event.BaseEvent;
import net.mygwt.ui.client.event.Listener;
import net.mygwt.ui.client.widget.table.TableItem;

public class TableListener implements Listener {
    List data;

    public TableListener(List data) {
        this.data = data;
    }

    public void handleEvent(BaseEvent be) {
         int i = data.indexOf(((TableItem)be.item).getValues());
         System.out.println(i);
    }
}
