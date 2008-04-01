package com.gwtexpress.client.util;

import com.gwtexpress.client.rpc.common.DateFormat;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

public class ClientDateFormat implements DateFormat {
    DateTimeFormat dtFormat;

    public ClientDateFormat(String pattern) {
        dtFormat = DateTimeFormat.getFormat(pattern);
    }

    public String format(Date date) {
        return dtFormat.format(date);
    }
}
