package com.gwtexpress.client.rpc;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.HashMap;

public interface GLogixServiceAsync {
    void findService(String sessionID, String serviceName, 
                     AsyncCallback<ArrayList<String[]>> callback);

    void findService(String sessionID, String serviceName, String[] params, 
                     AsyncCallback<ArrayList<String[]>> callback);

    void findService(String sessionID, String serviceName, 
                     ArrayList<String[]> paramList, String andOr, 
                     AsyncCallback<ArrayList<String[]>> callback);

    void postService(String sessionID, String serviceName, ArrayList<String[]> data, 
                     AsyncCallback callback);

    void postService(String sessionID, String serviceName, 
                     HashMap<String, ArrayList<String[]>> data, 
                     AsyncCallback callback);

    void findServiceMD(String sessionID, String serviceName, String[] params, 
                       AsyncCallback callback);
}
