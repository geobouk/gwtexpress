package com.gwtexpress.client.ui.ex.secure;

import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.UserRoles;
import com.gwtexpress.client.ui.ex.ExpressPage;


public class UserRolesPage extends ExpressPage {
    private GLogixMetaData metaData = new UserRoles();
    private String title = "User Roles";
    
    public String getPageTitle(){
        return title;
    }
    public GLogixMetaData getMetaData() {
        return metaData;
    }
}
