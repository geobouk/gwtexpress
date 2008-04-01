package com.gwtexpress.client.util;

import com.google.gwt.user.client.History;

import java.util.HashMap;

public class QueryParameter {
    private HashMap map = new HashMap();

    public QueryParameter() {
        this(getQueryString());
    }

    public QueryParameter(String search) {
        if ((search != null) && (search.length() > 0) && 
            search.indexOf('?') > 0) {
            int last = search.indexOf('?');
            String name = search.substring(0, last);
            search = search.substring(last + 1);
            map.put("this", name);
            if ((search != null) && (search.length() > 0)) {
                String[] nameValues = search.split("&");
                for (int i = 0; i < nameValues.length; i++) {
                    String[] pair = nameValues[i].split("=");
                    map.put(pair[0], pair[1]);
                }
            }
        } else {
            map.put("this", search);
        }

    }

    public String getValue(String key) {
        return (String)map.get(key);
    }


    private static String getQueryString() {
        return History.getToken();
    }


}


