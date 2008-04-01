package com.gwtexpress.client.rpc;


import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import java.util.ArrayList;
import java.util.HashMap;

public class ClientService {

    public void findService(String serviceName, 
                            AsyncCallback<ArrayList<String[]>> callback) {
        GLogixServiceAsync gLogixService = createGLogixServiceAsync();
        gLogixService.findService(RPCSession.getInstance().getSessionId(), 
                                  serviceName, callback);
    }

    public void findService(String serviceName, String[] params, 
                            AsyncCallback<ArrayList<String[]>> callback) {
        GLogixServiceAsync gLogixService = createGLogixServiceAsync();

        gLogixService.findService(RPCSession.getInstance().getSessionId(), 
                                  serviceName, params, callback);
    }


    public void findService(String serviceName, ArrayList<String[]> paramList, 
                            String andOr, 
                            AsyncCallback<ArrayList<String[]>> callback) {
        GLogixServiceAsync gLogixService = createGLogixServiceAsync();
        gLogixService.findService(RPCSession.getInstance().getSessionId(), 
                                  serviceName, paramList, andOr, callback);
    }

    public void findServiceMD(String serviceName, String[] params, 
                              AsyncCallback<HashMap<String, ArrayList<String[]>>> callback) {
        GLogixServiceAsync gLogixService = createGLogixServiceAsync();

        gLogixService.findServiceMD(RPCSession.getInstance().getSessionId(), 
                                    serviceName, params, callback);
    }

    public void postService(String serviceName, 
                            HashMap<String, ArrayList<String[]>> childFormsData, 
                            AsyncCallback<HashMap<String, ArrayList<String[]>>> callback) {
        GLogixServiceAsync gLogixService = createGLogixServiceAsync();
        gLogixService.postService(RPCSession.getInstance().getSessionId(), 
                                  serviceName, childFormsData, callback);
    }

    public void postService(String serviceName, ArrayList<String[]> data, 
                            AsyncCallback<ArrayList<String[]>> callback) {
        GLogixServiceAsync gLogixService = createGLogixServiceAsync();
        gLogixService.postService(RPCSession.getInstance().getSessionId(), 
                                  serviceName, data, callback);
    }
    GLogixServiceAsync simpleRPCService = null;

    private GLogixServiceAsync createGLogixServiceAsync() {
        if (simpleRPCService == null) {
            simpleRPCService = 
                    (GLogixServiceAsync)GWT.create(GLogixService.class);
            setServiceURL(simpleRPCService);
        }
        return simpleRPCService;
    }

    private void setServiceURL(GLogixServiceAsync simpleRPCService) {
        ServiceDefTarget endpoint = (ServiceDefTarget)simpleRPCService;
        String moduleBaseURL = GWT.getModuleBaseURL();
        String moduleRelativeURL = 
            moduleBaseURL + "gLogixService";
        Log.debug("ServiceEntryPoint=" + moduleRelativeURL);
        endpoint.setServiceEntryPoint(moduleRelativeURL);
    }
}

