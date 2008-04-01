package com.gwtexpress.client.ui.ex.secure;

import com.gwtexpress.client.index;
import com.gwtexpress.client.rpc.ClientService;
import com.gwtexpress.client.ui.ex.GLogixUIBuilder;
import com.gwtexpress.client.ui.ex.Page;

import com.gwtexpress.client.ui.ex.VersionUtil;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;

import java.util.Date;

import net.mygwt.ui.client.Style;
import net.mygwt.ui.client.widget.ContentPanel;
import net.mygwt.ui.client.widget.LoadingPanel;
import net.mygwt.ui.client.widget.WidgetContainer;
import net.mygwt.ui.client.widget.layout.FillLayout;
import net.mygwt.ui.client.widget.layout.RowData;


public class Login extends Page {
    WidgetContainer loginContainer;

    public Login() {
    }

    public void onShow() {
    }
    TextBox username = new TextBox();
    PasswordTextBox password = new PasswordTextBox();
    CheckBox rememberMe = new CheckBox();

    protected void createWidget(WidgetContainer container) {
        loginContainer = container;
        VerticalPanel vp = new VerticalPanel();
        Grid grid = new Grid(4, 2);
        grid.setText(0, 0, "Username:");
        grid.setText(1, 0, "Password:");
        grid.setWidget(0, 1, username);
        grid.setWidget(2, 1, rememberMe);
        grid.setWidget(1, 1, password);
        rememberMe.setHTML("Remember Me");
        String uname = Cookies.getCookie("gwtusername");
        if (uname != null && uname.length() > 0) {
            username.setText(uname);
            rememberMe.setChecked(true);
        }
        Button button = new Button("Login", new ClickListener() {
                    public void onClick(Widget widget) {
                        if (rememberMe.isChecked()) {
                            final long DURATION = 
                                1000 * 60 * 60 * 24 * 10;
                            Date expires = 
                                new Date(System.currentTimeMillis() + 
                                         DURATION);
                            Cookies.setCookie("gwtusername", 
                                              username.getText(), expires, 
                                              null, "/", false);
                        } else {
                            Cookies.setCookie("gwtusername", "", 
                                              new Date(System.currentTimeMillis() - 
                                                       10000), null, "/", 
                                              false);
                        }
                        LoadingPanel.get().show(loginContainer, 
                                                "Performing login. Please wait...");
                        ClientService service = new ClientService();

                        String[] params = 
                        { username.getText(), password.getText(),VersionUtil.getInstance().getClientVersion()};
                        password.setText("");
                        service.findService("LOGIN", params, 
                                            createLoginServiceCallback("Home"));

                    }
                });
        grid.setWidget(3, 1, button);
        HorizontalPanel hp = new HorizontalPanel();
        hp.add(button);
        button = new Button("Reset Password", new ClickListener() {
                        public void onClick(Widget widget) {
                            String uname = username.getText();
                            if (uname == null || uname.trim().length() == 0) {
                                GLogixUIBuilder.showError("Invalid Username", 
                                                          "You must enter your username before hitting forgot password.");
                                return;
                            }
                            String pwd = password.getText();
                            if (pwd != null && pwd.trim().length() > 0) {
                                GLogixUIBuilder.showError("Remove Password", 
                                                          "You must remove your password before hitting forgot password.");
                                return;
                            }
                            ClientService service = new ClientService();
                            String[] params = { username.getText() };
                            service.findService("RPWD", params, 
                                                createLoginServiceCallback("RPWD"));

                        }
                    });
        hp.add(button);
        hp.setSpacing(5);
        grid.setWidget(3, 1, hp);

        vp.add(grid);

        ContentPanel createRegion = new ContentPanel(Style.NONE);
        createRegion.setFrame(true);
        createRegion.setLayout(new FillLayout());
        createRegion.setScrollEnabled(true);
        createRegion.add(vp, new RowData(RowData.FILL_HORIZONTAL));
        container.setLayout(new FillLayout());
        container.add(createRegion, new RowData(RowData.FILL_HORIZONTAL));
    }

    private static AsyncCallback<ArrayList<String[]>> createLoginServiceCallback(final String token) {
        return new AsyncCallback<ArrayList<String[]>>() {
                public void onFailure(Throwable caught) {
                    try {
                        LoadingPanel.get().hide();
                        throw caught;
                    } catch (SerializableException e) {
                        if ("IUP".equals(e.getMessage())) {
                            GLogixUIBuilder.showError("Login Failed!", 
                                                      "Invalid username and/or password...");
                        } else {
                            GLogixUIBuilder.showError("Login Failed!", 
                                                      e.getMessage());
                        }
                    } catch (Throwable e) {
                        GLogixUIBuilder.showError("Login Failed!", e);
                    }
                }

                public void onSuccess(ArrayList<String[]> data) {
                    if ("RPWD".equals(token)){
                        LoadingPanel.get().hide();
                        GLogixUIBuilder.showError("Password Reset", "Your password had been reset and emailed to you. Please check your email for your new password.");
                        return;
                    }
                    if (data != null && data.size() == 1) {
                        String[] sessionDetails = data.get(0);
                        index.setSessionDetails(sessionDetails);
                        index.westPanel.setVisible(true);
                        index.mMenuBar.setVisible(true);
                        if (index.initalHistoryToken == null || index.initalHistoryToken.trim().length() == 0 ||index.initalHistoryToken.startsWith("Login") || index.initalHistoryToken.startsWith("Logout")) {
                            History.newItem("Home");
                        } else {
                            History.newItem(index.initalHistoryToken);
                        }
                        LoadingPanel.get().hide();
                    }
                }
            };
    }

    public String getPageTitle() {
        return "Login";
    }
}
