package com.gwtexpress.server.util;

import com.google.gwt.user.client.rpc.SerializableException;

public class RPCExceptionUtil {
    public RPCExceptionUtil() {
    }

    public static SerializableException sendErrorToClient(String err) {
        return new SerializableException(err);
    }

    public static SerializableException sendErrorToClient(String err, 
                                                          Throwable ex) {
        StringBuffer sb = new StringBuffer(err);
        sb.append("<p/><span style='color:#FF0000;'>").append(ex.getClass().getName()).append("</span><p/><pre>").append(ex.getMessage()).append("</pre>");
        StackTraceElement[] stacks = ex.getStackTrace();
        for (int i = 0; i < stacks.length; i++) {
            sb.append("<br/>").append(stacks[i].toString());
        }
        return sendErrorToClient(sb.toString());
    }
}
