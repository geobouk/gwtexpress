package com.gwtexpress.client.rpc;

import com.google.gwt.user.client.rpc.RemoteService;

import com.google.gwt.user.client.rpc.SerializableException;

import java.util.ArrayList;
import java.util.HashMap;


public interface GLogixService extends RemoteService {

    ArrayList<String[]> findService(String sessionID, 
                               String serviceName) throws SerializableException;


    ArrayList<String[]> findService(String sessionID, String serviceName, 
                               String[] params) throws SerializableException;


    ArrayList<String[]> findService(String sessionID, String serviceName, 
                               ArrayList<String[]> paramList, 
                               String andOr) throws SerializableException;

    ArrayList<String[]> postService(String sessionID, String serviceName, 
                               ArrayList<String[]> data) throws SerializableException;

    HashMap<String, ArrayList<String[]>> postService(String sessionID, 
                                            String serviceName, 
                                            HashMap<String, ArrayList<String[]>> data) throws SerializableException;

    HashMap<String, ArrayList<String[]>> findServiceMD(String sessionID, 
                                              String serviceName, 
                                              String[] params) throws SerializableException;

}
