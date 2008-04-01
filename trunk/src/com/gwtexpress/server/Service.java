package com.gwtexpress.server;

import com.gwtexpress.client.rpc.GLogixService;

import com.google.gwt.user.client.rpc.SerializableException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.gwtexpress.server.util.Log;

import com.gwtexpress.server.util.RPCExceptionUtil;

import java.io.IOException;

import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class Service extends RemoteServiceServlet implements GLogixService {
    static final String GLOGIX_NAME = "GWTExpress Server Module";
    static final String GLOGIX_VERSION = "Version 0.1";
    static final String GLOGIX_LAST_UPDATE_DATE = 
        "Last Modified: 5th Mar 2008";

    static Date date = Calendar.getInstance().getTime();
    private static final String TD_TD = "</td><td>";
    private static final String TR_TD = "<tr><td>";
    private static final String TD_TR = "</td></tr>";
    Date thisDate = Calendar.getInstance().getTime();
    SimpleDateFormat sdf = new SimpleDateFormat();
    static boolean debug = false;

    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>GWTExpress</title><body><pre>");
        out.println("<table><tr><td>");
        out.println(TR_TD);
        out.println(GLOGIX_NAME);
        out.println(TD_TD);
        out.println(GLOGIX_VERSION);
        out.println(TD_TR);
        out.println(GLOGIX_LAST_UPDATE_DATE);
        out.println(TR_TD);
        out.println("Server is up & running since");
        out.println(TD_TD);
        out.println(date.toString());
        out.println(TD_TR);
        out.println(TR_TD);
        out.println("Instance of this service is up & running since");
        out.println(TD_TD);
        out.println(thisDate.toString());
        out.println(TD_TR);
        out.println(TR_TD);
        out.println("Web Server Time");
        out.println(TD_TD);
        out.println(Calendar.getInstance().getTime().toString());
        out.println(TD_TR);
        out.println(TR_TD);
        out.println("No of services served");
        out.println(TD_TD);
        out.println(Worker.instanceCount);
        out.println(TD_TR);

        if ("Y".equals(request.getParameter("debug"))) {
            debug = true;
        } else if ("N".equals(request.getParameter("debug"))) {
            debug = false;
        }

        if ("Y".equals(request.getParameter("dbstatus"))) {
            //if (Service.isDebug()) Log.debug("dbstatus=Y");
            Connection con;
            DBMetaDataObject db = null;
            try {
                db = new DBMetaDataObject();
                db.startTxn();
                con = db.getConnection();
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery("select sysdate() from dual");
                if (rs.next()) {
                    out.println(TR_TD);
                    out.println("DB Server Time");
                    out.println(TD_TD);
                    out.println(rs.getObject(1).toString());
                    out.println(TD_TR);
                }
                out.println(TR_TD);
                out.println("DB Pool Status");
                out.println(TD_TD);
                out.println(db.getStatus());
                out.println(TD_TR);
                rs.close();
                st.close();
            } catch (Throwable e) {
                out.println(e.getMessage());
            } finally {
                if (db != null)
                    db.endTxn();
            }
        }
        out.println("</table></pre></body></html>");
    }

    public ArrayList<String[]> findService(String sessionID, 
                                           String serviceName) throws SerializableException {
        try {
            Worker worker = new Worker(getThreadLocalRequest());
            return worker.findService(sessionID, serviceName);
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient(e.getMessage());
        }
    }

    public ArrayList<String[]> findService(String sessionID, 
                                           String serviceName, 
                                           String[] params) throws SerializableException {
        try {
            Worker worker = new Worker();
            return worker.findService(sessionID, serviceName, params);
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Exception", e);
        }
    }

    public ArrayList<String[]> findService(String sessionID, 
                                           String serviceName, 
                                           ArrayList<String[]> paramList, 
                                           String andOr) throws SerializableException {
        try {
            Worker worker = new Worker();
            return worker.findService(sessionID, serviceName, paramList, 
                                      andOr);
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient(e.getMessage());
        }
    }

    public ArrayList<String[]> postService(String sessionID, 
                                           String serviceName, 
                                           ArrayList<String[]> data) throws SerializableException {
        try {
            Worker worker = new Worker();
            return worker.postService(sessionID, serviceName, data);
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient(e.getMessage());
        }
    }

    public HashMap<String, ArrayList<String[]>> postService(String sessionID, 
                                                            String serviceName, 
                                                            HashMap<String, ArrayList<String[]>> data) throws SerializableException {
        try {
            Worker worker = new Worker();
            return worker.postService(sessionID, serviceName, data);
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient(e.getMessage());
        }
    }

    public HashMap<String, ArrayList<String[]>> findServiceMD(String sessionID, 
                                                              String serviceName, 
                                                              String[] params) throws SerializableException {
        try {
            Worker worker = new Worker();
            return worker.findServiceMD(sessionID, serviceName, params);
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient(e.getMessage());
        }
    }

    public static void setDebug(boolean debug) {
        Service.debug = debug;
        if (debug) {
            Log.start();
        } else {
            Log.stop();
        }
    }

    public static boolean isDebug() {
        return debug;
    }
}
