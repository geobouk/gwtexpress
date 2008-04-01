package com.gwtexpress.server.util;

import com.gwtexpress.client.rpc.common.DateFormat;

import java.text.SimpleDateFormat;

import java.util.Date;

public class ServerDateFormat implements DateFormat {
    SimpleDateFormat sdf;

    public ServerDateFormat(SimpleDateFormat sdf) {
        this.sdf = sdf;
    }

    public String format(Date date) {
        return sdf.format(date);
    }
}
