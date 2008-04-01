package com.gwtexpress.client.ui.ex;

public class VersionUtil {
    String CLIENT_VERSION = "0.8.3.31";
    static VersionUtil me;

    public VersionUtil() {
    }

    public static VersionUtil getInstance() {
        if (me == null)
            me = new VersionUtil();
        return me;
    }

    public String getClientVersion() {
        return CLIENT_VERSION;
    }
}
