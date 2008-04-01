package com.gwtexpress.server;

public class CustomServiceManager {
    public CustomServiceManager() {
    }

    public static GLogixCustomService getFetchCustomService(DBMetaDataObject dbMetaDataObject, 
                                                            String serviceName) {
        GLogixCustomService customService = null;
        return customService;
    }

    public static GLogixCustomService getPostCustomService(DBMetaDataObject dbMetaDataObject, 
                                                           String serviceName) {
        GLogixCustomService customService = null;
        if ("MyAccountMetaData".equals(serviceName)) {
            customService = new MyAccountCustomService();
            customService.init(dbMetaDataObject);
            return customService;
        }
        return customService;
    }

}
