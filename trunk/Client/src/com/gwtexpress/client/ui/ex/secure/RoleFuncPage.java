package com.gwtexpress.client.ui.ex.secure;

import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.RolesFunctions;
import com.gwtexpress.client.ui.ex.ExpressPage;


public class RoleFuncPage extends ExpressPage {
    private GLogixMetaData metaData = new RolesFunctions();
    private String title = "Role Functions";
    
    public String getPageTitle(){
        return title;
    }
    public GLogixMetaData getMetaData() {
        return metaData;
    }
}
