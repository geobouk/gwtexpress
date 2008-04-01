package com.gwtexpress.server;

import com.google.gwt.user.client.rpc.SerializableException;

import java.util.ArrayList;

public interface GLogixCustomService {

    public void init(DBMetaDataObject dbMetaDataObject);

    public ArrayList<String[]> customPostService(ArrayList<String[]> data) throws SerializableException;

    public ArrayList<String[]> customFindService(String[] params) throws SerializableException;

}
