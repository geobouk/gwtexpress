package com.gwtexpress.client;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.gwtexpress.client.rpc.ClientService;
import com.gwtexpress.client.rpc.RPCSession;
import com.gwtexpress.client.rpc.model.MyAccountMetaData;
import com.gwtexpress.client.setup.Customers;
import com.gwtexpress.client.setup.Lookups;
import com.gwtexpress.client.ui.ex.GLogixUIBuilder;
import com.gwtexpress.client.ui.ex.Page;
import com.gwtexpress.client.ui.ex.VersionUtil;
import com.gwtexpress.client.ui.ex.secure.About;
import com.gwtexpress.client.ui.ex.secure.Home;
import com.gwtexpress.client.ui.ex.secure.Login;
import com.gwtexpress.client.ui.ex.secure.MyAccount;
import com.gwtexpress.client.ui.ex.secure.RoleFuncPage;
import com.gwtexpress.client.ui.ex.secure.UserAccountsPage;
import com.gwtexpress.client.ui.ex.secure.UserRolesPage;
import com.gwtexpress.client.util.QueryParameter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import net.mygwt.ui.client.Events;
import net.mygwt.ui.client.Style;
import net.mygwt.ui.client.event.BaseEvent;
import net.mygwt.ui.client.event.Listener;
import net.mygwt.ui.client.event.SelectionListener;
import net.mygwt.ui.client.widget.ContentPanel;
import net.mygwt.ui.client.widget.ExpandBar;
import net.mygwt.ui.client.widget.ExpandItem;
import net.mygwt.ui.client.widget.LoadingPanel;
import net.mygwt.ui.client.widget.ThemeSelector;
import net.mygwt.ui.client.widget.ToolButton;
import net.mygwt.ui.client.widget.Viewport;
import net.mygwt.ui.client.widget.WidgetContainer;
import net.mygwt.ui.client.widget.layout.BorderLayout;
import net.mygwt.ui.client.widget.layout.BorderLayoutData;
import net.mygwt.ui.client.widget.layout.FillLayout;
import net.mygwt.ui.client.widget.layout.RowData;
import net.mygwt.ui.client.widget.layout.RowLayout;
import net.mygwt.ui.client.widget.tree.Tree;
import net.mygwt.ui.client.widget.tree.TreeItem;


public class index implements EntryPoint, HistoryListener {
    private Viewport viewport;
    public static ContentPanel westPanel;
    private ContentPanel centerPanel;

    private Tree tree;
    private TreeItem current;

    private Page currentPage;
    private static ArrayList<Page> pages = new ArrayList<Page>();
    public static String mSessionID;
    static ExpandItem toDoBox;
    public static ExpandBar expandBar;
    public static boolean profileRetrived = false;
    public static HorizontalPanel mMenuBar = null;
    static TreeItem admin;
    public static HashMap<String, ArrayList<String[]>> mUserProfileData;
    public static String[] myAccountData;

    public index() {
        init();
    }

    public void init() {
        currentPage = null;
        mSessionID = null;
        pages.clear();
        pages.add(new Login());
        pages.add(new Home());
        pages.add(new UserRolesPage());
        pages.add(new UserAccountsPage());
        pages.add(new Lookups());
        pages.add(new RoleFuncPage());
        pages.add(new About());
        pages.add(new MyAccount());
        pages.add(new Customers());
        
    }

    public static Page findPage(String historyToken) {
        Page page = null;
        for (int i = 0; i < pages.size(); i++) {
            page = pages.get(i);
            if (page.getId().equals(historyToken)) {
                break;
            }
        }
        return page;
    }

    public static void replacePage(Page newPage) {
        Page page = null;
        String newPageId = newPage.getId();
        for (int i = 0; i < pages.size(); i++) {
            page = pages.get(i);
            if (page.getId().equals(newPageId)) {
                break;
            }
        }
        if (page != null) {
            pages.remove(page);
            page.removeFromParent();
            pages.add(newPage);
        }
    }

    private void show(String historyToken) {
        QueryParameter qp = new QueryParameter(historyToken);
        String token = qp.getValue("this");
        TreeItem item = tree.getItemById(token);
        if (item == null) {
            show("Home", "Home");
        } else {
            tree.expandPath(item.getPath());
            tree.setSelection(item);
            show(item);
        }
    }

    private void show(TreeItem item) {
        if (current == item) {
            return;
        }
        current = item;
        String pageId = item.getId();
        StringBuffer sb = new StringBuffer();
        while (!item.isRoot()) {
            sb.insert(0, " / " + item.getText());
            item = item.getParentItem();
        }
        show(sb.substring(3), pageId);
    }

    private void show(final String title, final String historyToken) {
        if (mSessionID == null) {
            String sessionID = Cookies.getCookie("sessionID");
            if ((sessionID == null || sessionID.length() == 0) && 
                !"Login".equals(historyToken)) {
                History.newItem("Login");
                return;
            } else {
                mSessionID = sessionID;
            }
        }

        if (sessionDetails == null && !"Login".equals(historyToken)) {
            LoadingPanel.get().show("Please wait while your session information is being retrieved from the server...");
            Timer t = new Timer() {
                    public void run() {
                        if (sessionDetails != null) {
                            show(title, historyToken);
                            LoadingPanel.get().hide();
                        } else {
                            schedule(500);
                        }
                    }
                };
            t.schedule(500);
            return;
        }

        currentPage = findPage(historyToken);

        centerPanel.setText(title);

        if (currentPage == null) {
            centerPanel.removeAll();
            centerPanel.layout(true);
            return;
        }

        if (!currentPage.initialized) {
            currentPage.init();
        }

        centerPanel.removeAll();
        centerPanel.add(currentPage);
        Window.setTitle(currentPage.getPageTitle());
        currentPage.onShow();
        centerPanel.layout(true);
        LoadingPanel.get().hide();
    }


    private Hyperlink getMenuBarLink(String item, String target) {
        Hyperlink link = new Hyperlink(item, target);
        link.setStylePrimaryName("tm_pageTopHyperlink");
        return link;
    }

    private HTML getMenuSeperator() {
        HTML sepr = new HTML("|");
        sepr.setStylePrimaryName("tm_sepr");
        return sepr;
    }

    private Panel getFooterMenuBar() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(getMenuBarLink("Home", "Home"));
        panel.add(getMenuSeperator());
        panel.add(getMenuBarLink("Contact Us", "ContactUs"));
        panel.add(getMenuSeperator());
        panel.add(getMenuBarLink("Privacy Policy", "PrivacyPolicy"));
        panel.add(getMenuSeperator());
        panel.add(getMenuBarLink("Site Map", "SiteMap"));
        return panel;
    }

    private Panel getMenuBar() {
        if (mMenuBar == null) {
            mMenuBar = new HorizontalPanel();
            mMenuBar.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
            loggedInUserName.setStylePrimaryName("tm_pageTopHyperlink");
            mMenuBar.add(loggedInUserName);
            mMenuBar.add(getMenuSeperator());
            mMenuBar.add(getMenuBarLink("Home", "Home"));
            mMenuBar.add(getMenuSeperator());
            mMenuBar.add(getMenuBarLink("My Account", "MyAccount"));
            mMenuBar.add(getMenuSeperator());
            //            mMenuBar.add(getMenuBarLink("My Profile", "UserProfilePage"));
            //            mMenuBar.add(getMenuSeperator());
            mMenuBar.add(signInStatusLink);
            signInStatusLink.setStylePrimaryName("tm_pageTopHyperlink");
            mMenuBar.add(getMenuSeperator());
            //            mMenuBar.add(getMenuBarLink("About", "About"));
            //            mMenuBar.add(getMenuSeperator());
        }
        return mMenuBar;
    }

    public void validateSession() {
        currentPage = null;
        profileRetrived = false;
        mUserProfileData = null;
        myAccountData = null;
        ClientService service = new ClientService();
        String[] params = 
        { mSessionID, VersionUtil.getInstance().getClientVersion() };
        service.findService("VALIDATE_SESSION", params, 
                            createValidateSessionCallback("Home"));
    }

    public void removeSessionInServer() {
        ClientService service = new ClientService();
        service.findService("LOGOUT", removeSessionInServerCallback());
    }
    public static final Hyperlink signInStatusLink = 
        new Hyperlink("Sign In", "Login");
    public static final Label loggedInUserName = new Label();
    public static String[] sessionDetails;

    private AsyncCallback<ArrayList<String[]>> removeSessionInServerCallback() {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable caught) {
                    removeSession();
                }

                public void onSuccess(ArrayList<String[]> data) {
                    removeSession();
                }
            };
    }

    private AsyncCallback<ArrayList<String[]>> createValidateSessionCallback(String token) {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable e) {
                    GLogixUIBuilder.showError("Error validating Session", e);
                    removeSession();
                }

                public void onSuccess(ArrayList<String[]> data) {
                    if (data != null && data.size() == 1) {
                        String[] params = data.get(0);
                        setSessionDetails(params);
                    } else {
                        removeSession();
                    }
                }
            };
    }

    public static void setSessionDetails(String[] params) {
        if (params != null && params.length == 8) {
            sessionDetails = params;
            RPCSession session = RPCSession.getInstance();
            session.setSessionId(params[0]);
            session.setUserName(params[1]);
            session.setDisplayName(params[2]);
            if ("Y".equals(params[5])) {
                session.setAdmin(true);
                admin.setVisible(true);
            } else {
                session.setAdmin(false);
                admin.setVisible(false);
            }
            session.setFirstName(params[6]);
            session.setLastName(params[7]);
            MyAccountMetaData userAccountMD = new MyAccountMetaData();
            int[] uaIdxs = 
                userAccountMD.getIndexByNames(new String[] { "USER_ID", 
                                                             "FIRST_NAME", 
                                                             "LAST_NAME", 
                                                             "DISPLAY_NAME" });
            myAccountData = userAccountMD.createBlankRow();
            myAccountData[uaIdxs[0]] = session.getUserId();
            myAccountData[uaIdxs[1]] = session.getFirstName();
            myAccountData[uaIdxs[2]] = session.getLastName();
            myAccountData[uaIdxs[3]] = session.getDisplayName();

            mSessionID = params[0];
            String displayNameValue = params[2];
            if (session.isAdmin())
                displayNameValue = displayNameValue + " [Admin]";

            loggedInUserName.setText(displayNameValue);
            signInStatusLink.setText("Sign Out");
            signInStatusLink.setTargetHistoryToken("Logout");
            Log.debug("Got session details");
            Log.debug("Username:" + params[1]);
            Log.debug("SessionID:" + mSessionID);
            Log.debug("Name:" + params[2]);
            final long DURATION = 
                1000 * 60 * 60 * 24 * 10;
            Date expires = new Date(System.currentTimeMillis() + DURATION);
            Cookies.setCookie("sessionID", mSessionID, expires, null, "/", 
                              false);
            preFetch();
        }
    }

    String mRequestToken;

    public void onModuleLoad() {
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
                    public void onUncaughtException(Throwable e) {
                        GLogixUIBuilder.showError("Uncaught Exception:", e);
                    }
                });
        DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        onModuleLoad2();
                    }
                });
    }

    public static String initalHistoryToken;
    private long startTimeMillis;

    private static void preFetchLookup(final String lkpName) {

        DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        GLogixUIBuilder.populateLookup(null, null, lkpName, 
                                                       null);
                    }
                });
    }

    private static void preFetch() {
        final String[] pfLkpNames = { "SELECT_TIME", "YES_NO" };
        for (int i = 0; i < pfLkpNames.length; i++) {
            preFetchLookup(pfLkpNames[i]);
        }
    }

    public void onModuleLoad2() {

        if (Log.isDebugEnabled()) {
            startTimeMillis = System.currentTimeMillis();
        }
        viewport = new Viewport();
        viewport.setStyleName("my-border-layout");
        History.addHistoryListener(this);
        viewport.setLayout(new RowLayout());
        viewport.setBorders(false);
        viewport.hideLoadingPanel("loading");
        mSessionID = Cookies.getCookie("sessionID");
        mRequestToken = History.getToken();
        initalHistoryToken = mRequestToken;
        admin = new TreeItem();
        admin.setId("Administration");
        admin.setText("Administration Setup");
        admin.setVisible(false);

        if (mSessionID != null) {
            Log.debug("Found session:" + mSessionID);
            RPCSession session = RPCSession.getInstance();
            session.setSessionId(mSessionID);
            validateSession();
        }

        HorizontalPanel header = new HorizontalPanel();
        Label lbl = new Label("GWTExpress Demo Version 0.1");
        lbl.setStyleName("title");
        header.setStylePrimaryName("tm_pageTop");
        header.add(lbl);


        Panel menuBar = getMenuBar();
        header.add(menuBar);
        header.setCellHorizontalAlignment(menuBar, 
                                          HasHorizontalAlignment.ALIGN_RIGHT);


        VerticalPanel footer = new VerticalPanel();
        Label rights = new HTML("All rights reserved &reg; GWTExpress.com");
        rights.setStylePrimaryName("tm_copyrights");
        Panel footerBar = getFooterMenuBar();
        footer.add(footerBar);
        footer.add(rights);
        footer.setStylePrimaryName("tm_pageTop");
        footer.setCellHorizontalAlignment(footerBar, 
                                          HasHorizontalAlignment.ALIGN_CENTER);
        footer.setCellHorizontalAlignment(rights, 
                                          HasHorizontalAlignment.ALIGN_CENTER);

        WidgetContainer main = new WidgetContainer();
        main.setLayout(new BorderLayout());

        tree = new Tree(Style.SINGLE);
        tree.setItemImageStyle("icon-list");

        WidgetContainer ct = new WidgetContainer();
        ct.setScrollEnabled(true);
        ct.add(tree);

        TreeItem root = tree.getRootItem();

        TreeItem home = new TreeItem();
        home.setId("Home");
        home.setText("Home");
        root.add(home);

        TreeItem item = new TreeItem();
        item.setId("Customers");
        item.setText("Customer Maintenance");
        home.add(item);

        item = new TreeItem();
        item.setId("Lookups");
        item.setText("Setup Lookups");
        home.add(item);
        
        root.add(admin);

        item = new TreeItem();
        item.setId("UserAccountsPage");
        item.setText("Setup User Accounts");
        admin.add(item);

        item = new TreeItem();
        item.setId("UserRolesPage");
        item.setText("Setup User Roles");
        admin.add(item);

        item = new TreeItem();
        item.setId("RoleFuncPage");
        item.setText("Setup Role Functions");
        admin.add(item);

        tree.addListener(Events.SelectionChange, new Listener() {
                    public void handleEvent(BaseEvent be) {
                        TreeItem item = (TreeItem)be.item;
                        String itemID = item.getId();
                        if ("1".equals(itemID)) {
                            itemID = "Home";
                            TreeItem homeItem = tree.getItemById("Home");
                            tree.expandPath(homeItem.getPath());
                            tree.setSelection(homeItem);
                            return;
                        }
                        if (item != null) {
                            QueryParameter qp = new QueryParameter();
                            if (itemID.equals(qp.getValue("this"))) {
                                show(item);
                            } else {
                                History.newItem(itemID);
                            }
                        }
                    }
                });

        BorderLayoutData westData = 
            new BorderLayoutData(Style.WEST, 200, 150, 300);

        westPanel = new ContentPanel(Style.HEADER);
        westPanel.setLayout(new FillLayout());

        expandBar = new ExpandBar(Style.MULTI);
        westPanel.add(expandBar);
        expandBar.setBorders(false);

        final ExpandItem navItem = new ExpandItem();
        navItem.setText("Navigation");
        navItem.getContainer().add(ct);
        expandBar.add(navItem);
        navItem.setExpanded(true);

        toDoBox = new ExpandItem();
        toDoBox.setText("To Do List");
        toDoBox.setExpanded(true);

        ToolButton expandAll = new ToolButton("my-tool-plus");
        expandAll.setToolTip("Expand All");
        expandAll.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(BaseEvent be) {
                        tree.expandAll();
                        navItem.setExpanded(true);
                    }
                });
        navItem.getHeader().addWidget(expandAll);

        ToolButton collapseAll = new ToolButton("my-tool-minus");
        collapseAll.setToolTip("Collapse All");
        collapseAll.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(BaseEvent be) {
                        tree.collapseAll();
                        navItem.setExpanded(false);
                    }
                });
        navItem.getHeader().addWidget(collapseAll);

        centerPanel = new ContentPanel(Style.HEADER);
        centerPanel.getHeader().addWidget(new ThemeSelector());
        centerPanel.setLayout(new FillLayout());

        main.add(westPanel, westData);
        main.add(centerPanel, new BorderLayoutData(Style.CENTER));
        main.setStyleName("tm_pageTop");

        viewport.add(header, new RowData(RowData.FILL_HORIZONTAL));
        viewport.add(main, new RowData(RowData.FILL_BOTH));
        viewport.add(footer, new RowData(RowData.FILL_HORIZONTAL));
        viewport.layout();
        mRequestToken = History.getToken();

        if (mSessionID == null) {
            if ("Login".equals(History.getToken())) {
                onHistoryChanged("Login");
            } else {
                History.newItem("Login");
            }
        } else {
            if (mRequestToken == null || "".equals(mRequestToken))
                mRequestToken = "Home";

            show(mRequestToken);
        }

        if (Log.isDebugEnabled()) {
            long endTimeMillis = System.currentTimeMillis();
            float durationSeconds = (endTimeMillis - startTimeMillis) / 1000F;
            Log.debug("Load Duration: " + durationSeconds + " seconds");
        }
    }

    public native void reloadApplication() /*-{
        $wnd.location.reload();
    }-*/;

    public void removeSession() {
        RPCSession.getInstance().removeSession();
        mSessionID = null;
        current = null;
        currentPage = null;
        mUserProfileData = null;
        profileRetrived = false;
        westPanel.setVisible(false);
        init();
        Cookies.removeCookie("sessionID");
        Date expires = new Date(System.currentTimeMillis() - 100000);
        Cookies.setCookie("sessionID", "", expires, null, "/", false);
        signInStatusLink.setText("Sign In");
        signInStatusLink.setTargetHistoryToken("Login");
        loggedInUserName.setText("");
        sessionDetails = null;
        mUserProfileData = null;
        History.newItem("Login?a=1");
        //reloadApplication();
    }

    public String PREVIOUS_HISTORY_TOKEN;

    public void onHistoryChanged(String historyToken) {
        String fullToken = historyToken;
        QueryParameter qp = new QueryParameter(historyToken);
        historyToken = qp.getValue("this");
        if (!fullToken.equals(PREVIOUS_HISTORY_TOKEN)) {
            current = null;
        }
        PREVIOUS_HISTORY_TOKEN = fullToken;
        if ("Login".equals(historyToken)) {
            mMenuBar.setVisible(false);
            westPanel.setVisible(false);
            show("Login", "Login");
            return;
        } else if ("About".equals(historyToken)) {
            show("About", "About");
            return;
        } else if ("MyAccount".equals(historyToken)) {
            tree.collapseAll();
            show("My Account", "MyAccount");
            return;
        }

        if ("Logout".equals(historyToken)) {
            removeSessionInServer();
            return;
        }

        TreeItem treeItem = tree.getItemById(historyToken);
        if (treeItem != null) {
            TreeItem si = tree.getSelectedItem();
            if (si == null || 
                !historyToken.equals(tree.getSelectedItem().getId())) {
                tree.expandAll();
                tree.setSelection(treeItem);
            } else {
                show(treeItem);
            }
        }
    }
}
