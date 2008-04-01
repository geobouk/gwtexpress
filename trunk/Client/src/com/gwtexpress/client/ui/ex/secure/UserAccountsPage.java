package com.gwtexpress.client.ui.ex.secure;

import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.UserAccounts;
import com.gwtexpress.client.ui.ex.ExpressPage;

public class UserAccountsPage extends ExpressPage {
    private GLogixMetaData metaData = new UserAccounts();
    private String title = "User Accounts";

    public String getPageTitle() {
        return title;
    }

    public GLogixMetaData getMetaData() {
        return metaData;
    }
}
