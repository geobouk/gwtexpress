package com.gwtexpress.server;

import com.google.gwt.user.client.rpc.SerializableException;

import com.gwtexpress.client.rpc.GLogixService;
import com.gwtexpress.client.rpc.model.CustomersMetaData;
import com.gwtexpress.client.rpc.model.GLogixMetaData;
import com.gwtexpress.client.rpc.model.LookupValues;
import com.gwtexpress.client.rpc.model.LookupsMetaData;
import com.gwtexpress.client.rpc.model.MyAccountMetaData;
import com.gwtexpress.client.rpc.model.RolesFunctions;
import com.gwtexpress.client.rpc.model.UserAccounts;
import com.gwtexpress.client.rpc.model.UserRoles;
import com.gwtexpress.server.util.Log;
import com.gwtexpress.server.util.RPCExceptionUtil;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class Worker implements GLogixService {

    private static final String ACCESS_DENIED_MSG = 
        "You do not have access to perform this function. Please contact your system administrator.";
    static HashMap<String, GLogixMetaData> metaDataMap = 
        new HashMap<String, GLogixMetaData>(10);
    private DBMetaDataObject dbMetaDataObject;
    StringBuffer sb = new StringBuffer();
    GLogixMetaData metaData;
    public static long instanceCount = 0;
    UserSession session;
    HttpServletRequest request;
    public static final String SERVER_VERSION = "0.8.3.31";

    public Worker() {
        instanceCount++;
    }

    public Worker(HttpServletRequest request) {
        this();
        this.request = request;
    }

    static {
        metaDataMap.put("Lookups", new LookupsMetaData());
        metaDataMap.put("LookupValues", new LookupValues());
        metaDataMap.put("UserRoles", new UserRoles());
        metaDataMap.put("UserAccounts", new UserAccounts());
        metaDataMap.put("MyAccountMetaData", new MyAccountMetaData());
        metaDataMap.put("RolesFunctions", new RolesFunctions());
        metaDataMap.put("CustomersMetaData", new CustomersMetaData());
    }

    public ArrayList<String[]> getUploadStatus() throws SerializableException {
        HttpSession s = request.getSession();
        String gwtex_usize = (String)s.getAttribute("gwtex_usize");
        String gwtex_tsize = (String)s.getAttribute("gwtex_tsize");
        ArrayList<String[]> val = new ArrayList<String[]>(1);
        if (gwtex_usize == null || gwtex_tsize == null) {
            val.add(new String[] { "..." });
        } else {
            String str = 
                "Uploaded " + gwtex_usize + " of " + gwtex_tsize + " KB";
            if (gwtex_usize.equals(gwtex_tsize)) {
                s.invalidate();
                str = "OK";
            }
            val.add(new String[] { str });
        }
        return val;
    }


    public ArrayList<String[]> findService(String sessionID, 
                                           String serviceName) throws SerializableException {
        if ("fus".equals(serviceName)) {
            return getUploadStatus();
        } else {
            return findService(sessionID, serviceName, new String[] { });
        }
    }

    public ArrayList<String[]> getLOVData(String lovName, 
                                          String[] params) throws SQLException {
        if ("USER_ID".equals(lovName)) {
            MetaDataObject metaDataObject;
            metaDataObject = 
                    dbMetaDataObject.getMetaDataObject("SELECT USER_ID, USER_NAME FROM user_accounts ORDER BY 2", 
                                                       null);
            return metaDataObject.getData();
        } else {
            return null;
        }
    }

    public ArrayList<String[]> findService(String sessionID, 
                                           String serviceName, 
                                           ArrayList<String[]> paramList, 
                                           String andOr) throws SerializableException {

        try {
            validate(sessionID, serviceName);

            MetaDataObject metaDataObject;
            metaDataObject = 
                    dbMetaDataObject.getMetaDataObject(metaData, paramList, 
                                                       andOr);
            ArrayList<String[]> result = metaDataObject.getData();
            return result;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Exception", e);
        } finally {
            try {
                if (dbMetaDataObject != null)
                    dbMetaDataObject.endTxn();
            } catch (Throwable e) {
                throw RPCExceptionUtil.sendErrorToClient("Error in endTxn", e);
            }
        }
    }

    public ArrayList<String[]> findService(String sessionID, 
                                           String serviceName, 
                                           String[] params) throws SerializableException {


        try {

            if ("VALIDATE_SESSION".equals(serviceName) || 
                "LOGIN".equals(serviceName) || "LOGOUT".equals(serviceName) || 
                "RPWD".equals(serviceName)) {
                if (Service.isDebug())
                    Log.debug("Before doSpecialService");
                return doSpecialService(sessionID, serviceName, params);
            }

            if (Service.isDebug())
                Log.debug("Before validate session");
            validate(sessionID, serviceName);
            if (Service.isDebug())
                Log.debug("After validate session");

            if ("LookupValues".equals(serviceName) && 
                params[0].startsWith("LOV:")) {
                if (params[0].indexOf('|') > 0) {
                    StringTokenizer st = 
                        new StringTokenizer(params[0].substring(4), "|");
                    String token;
                    String[] newParams = new String[st.countTokens() - 1];
                    int i = 0;
                    String lovName = st.nextToken();
                    while (st.hasMoreTokens()) {
                        token = st.nextToken();
                        newParams[i++] = token;
                    }
                    return getLOVData(lovName, newParams);
                } else {
                    return getLOVData(params[0].substring(4), null);
                }
            }
            if (Service.isDebug())
                Log.debug("Before getFetchCustomService");

            GLogixCustomService customService = CustomServiceManager.getFetchCustomService(dbMetaDataObject, 
                                                           serviceName);
            if (Service.isDebug())
                Log.debug("After getFetchCustomService");

            ArrayList<String[]> result;
            if (customService != null) {
                result = customService.customFindService(params);
            } else {
                if (Service.isDebug())
                    Log.debug("Before getMetaDataObject");
                MetaDataObject metaDataObject;
                metaDataObject = 
                        dbMetaDataObject.getMetaDataObject(metaData, params);
                if (Service.isDebug())
                    Log.debug("After getMetaDataObject");
                result = metaDataObject.getData();
                if (Service.isDebug())
                    Log.debug("After getData");
            }
            return result;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Exception", e);
        } finally {
            try {
                if (dbMetaDataObject != null)
                    dbMetaDataObject.endTxn();
            } catch (Throwable e) {
                throw RPCExceptionUtil.sendErrorToClient("Error in endTxn", e);
            }
        }
    }

    public ArrayList<String[]> postService(String sessionID, 
                                           String serviceName, 
                                           ArrayList<String[]> data) throws SerializableException {
        try {
            if (sessionID == null || serviceName == null || data == null)
                return null;
            if (Service.isDebug())
                Log.debug("postService: Before validate session");
            validate(sessionID, serviceName);
            if (Service.isDebug())
                Log.debug("postService: After validate session");
            for (int i = 0; i < data.size(); i++) {
                String[] row = data.get(i);
                if ("UserAccounts".equals(serviceName) && row[0] == null) {
                    row[1] = row[1].toUpperCase();
                    SessionManager sessionManager = 
                        new SessionManager(dbMetaDataObject);
                    if (sessionManager.getUserAccount(row[1]) != null) {
                        throw new SerializableException("Username already exists.");
                    }
                    row[5] = SessionManager.getHashValue("welcome");
                }

                if (Service.isDebug())
                    Log.debug("postService: Before getPostCustomService");
                GLogixCustomService customService = CustomServiceManager.getPostCustomService(dbMetaDataObject, 
                                                              serviceName);
                if (Service.isDebug())
                    Log.debug("postService: After getPostCustomService");
                if (customService != null) {
                    customService.customPostService(data);
                } else {
                    if (Service.isDebug())
                        Log.debug("Before save");
                    dbMetaDataObject.save(metaData, row);
                    if (Service.isDebug())
                        Log.debug("After save");
                }
                // }
            }
            dbMetaDataObject.commit();
            return data;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Error posting service", 
                                                     e);
        } finally {
            try {
                if (dbMetaDataObject != null)
                    dbMetaDataObject.endTxn();
            } catch (Throwable e) {
                throw RPCExceptionUtil.sendErrorToClient("Error in endTxn", e);
            }
        }
    }

    public HashMap<String, ArrayList<String[]>> postService(String sessionID, 
                                                            String serviceName, 
                                                            HashMap<String, ArrayList<String[]>> mdData) throws SerializableException {
        try {
            validate(sessionID, serviceName);
            Iterator<String> fItr = mdData.keySet().iterator();
            ArrayList<String[]> masterDataList = mdData.get("this");
            if (masterDataList == null || masterDataList.size() != 1) {
                throw RPCExceptionUtil.sendErrorToClient("Developer Exception: Master data missing.");
            }

            String[] masterData = masterDataList.get(0);

            dbMetaDataObject.save(metaData, masterData);

            while (fItr.hasNext()) {
                String _serviceName = fItr.next();
                if ("this".equals(_serviceName))
                    continue;
                ArrayList<String[]> list = mdData.get(_serviceName);
                ArrayList<int[]> maps = metaData.getChildMap(_serviceName);
                if (maps == null || maps.size() == 0 || 
                    maps.get(0).length != 2) {
                    throw RPCExceptionUtil.sendErrorToClient("Invalid or no relationship defined between " + 
                                                             _serviceName + 
                                                             " & " + 
                                                             serviceName);
                }

                for (int i = 0; i < list.size(); i++) {
                    String[] data = list.get(i);
                    for (int m = 0; m < maps.size(); m++) {
                        int[] map = maps.get(m);
                        data[map[0]] = masterData[map[1]];
                    }
                    dbMetaDataObject.save(metaDataMap.get(_serviceName), data);
                }
            }
            dbMetaDataObject.commit();
            return mdData;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Error posting data", e);
        } finally {
            try {
                if (dbMetaDataObject != null)
                    dbMetaDataObject.endTxn();
            } catch (Throwable e) {
                throw RPCExceptionUtil.sendErrorToClient("Error in endTxn", e);
            }
        }
    }

    public HashMap<String, ArrayList<String[]>> findServiceMD(String sessionID, 
                                                              String serviceName, 
                                                              String[] params) throws SerializableException {
        validate(sessionID, serviceName);
        return findServiceMD(dbMetaDataObject, serviceName, params);
    }

    public HashMap<String, ArrayList<String[]>> findServiceMD(DBMetaDataObject dbMetaDataObject, 
                                                              String serviceName, 
                                                              String[] params) throws SerializableException {

        try {
            HashMap<String, ArrayList<String[]>> mdData = 
                new HashMap<String, ArrayList<String[]>>();

            GLogixMetaData masterMetaData = metaDataMap.get(serviceName);
            MetaDataObject metaDataObject;
            metaDataObject = 
                    dbMetaDataObject.getMetaDataObject(masterMetaData, params);
            ArrayList<String[]> masterData = metaDataObject.getData();

            if (masterData == null || masterData.size() > 1)
                throw RPCExceptionUtil.sendErrorToClient("Too many rows found where only one is expected.");

            if (masterData == null || masterData.size() == 0)
                return mdData;

            String[] mparams = masterData.get(0);
            String[] childSrvs = masterMetaData.getChildMetaDataNames();
            mdData.put("this", masterData);
            String[] childParams;
            String childServiceName = null;
            GLogixMetaData childMetaData;
            if (childSrvs != null && childSrvs.length > 0) {
                for (int i = 0; i < childSrvs.length; i++) {
                    childServiceName = childSrvs[i];
                    childMetaData = metaDataMap.get(childServiceName);
                    childParams = childMetaData.createBlankRow();
                    ArrayList<int[]> maps = 
                        masterMetaData.getChildMap(childServiceName);
                    if (maps == null || maps.size() == 0 || 
                        maps.get(0).length != 2) {
                        throw RPCExceptionUtil.sendErrorToClient("Invalid or no relationship defined between " + 
                                                                 childServiceName + 
                                                                 " & " + 
                                                                 serviceName);
                    }
                    for (int m = 0; m < maps.size(); m++) {
                        int[] map = maps.get(m);
                        childParams[map[0]] = mparams[map[1]];
                    }
                    metaDataObject = 
                            dbMetaDataObject.getMetaDataObject(childMetaData, 
                                                               childParams);
                    ArrayList<String[]> childData = metaDataObject.getData();
                    mdData.put(childServiceName, childData);
                }
            }
            return mdData;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Error posting data", e);
        } finally {
            try {
                if (dbMetaDataObject != null)
                    dbMetaDataObject.endTxn();
            } catch (Throwable e) {
                throw RPCExceptionUtil.sendErrorToClient("Error in endTxn", e);
            }
        }
    }

    private ArrayList<String[]> getSessionDetailsForClient(UserSession session) {
        String[] str = 
        { session.getSessionId(), session.getUserName(), session.getDisplayName(), 
          (session.getLastPasswordChanged() == null) ? null : 
          session.getLastPasswordChanged().getTime() + "", 
          (session.getLastAccessTime() == null) ? null : 
          session.getLastAccessTime().getTime() + "", 
          session.isAdmin() ? "Y" : "N", session.getFirstName(), 
          session.getLastName() };
        ArrayList<String[]> list = new ArrayList<String[]>(1);
        list.add(str);
        return list;
    }

    private ArrayList<String[]> doSpecialService(String sessionID, 
                                                 String serviceName, 
                                                 String[] params) throws SerializableException {
        if (dbMetaDataObject == null)
            dbMetaDataObject = new DBMetaDataObject();
        try {
            dbMetaDataObject.startTxn();
            SessionManager sessionManager = 
                new SessionManager(dbMetaDataObject);
            if ("VALIDATE_SESSION".equals(serviceName)) {
                if (params.length == 2 && SERVER_VERSION.equals(params[1])) {
                    UserSession session = 
                        sessionManager.validateSession(params[0]);
                    return getSessionDetailsForClient(session);
                } else {
                    String clientVersion = "NO_VERSION";
                    if (params.length == 2) {
                        clientVersion = params[1];
                    }
                    throw RPCExceptionUtil.sendErrorToClient("Server Version [" + 
                                                             SERVER_VERSION + 
                                                             "] is not in sync with the client version [" + 
                                                             clientVersion + 
                                                             "]. Please refresh your browser window.");
                }
            } else if ("LOGIN".equals(serviceName)) {
                if (params.length == 3 && SERVER_VERSION.equals(params[2])) {
                    UserSession session = 
                        sessionManager.authenticateUser(params[0], params[1]);
                    return getSessionDetailsForClient(session);
                } else {
                    String clientVersion = "NO_VERSION";
                    if (params.length == 3) {
                        clientVersion = params[2];
                    }
                    throw RPCExceptionUtil.sendErrorToClient("Server Version [" + 
                                                             SERVER_VERSION + 
                                                             "] is not in sync with the client version [" + 
                                                             clientVersion + 
                                                             "]. Please refresh your browser window.");
                }
            } else if ("LOGOUT".equals(serviceName)) {
                sessionManager.removeSession(sessionID);
            } else if ("RPWD".equals(serviceName)) {
                sessionManager.resetPassword(params[0]);
                return new ArrayList<String[]>();
            }
            return null;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Exception", e);
        }
    }


    public UserSession validate(String sessionID, 
                                String serviceName) throws SerializableException {
        try {
            dbMetaDataObject = new DBMetaDataObject();
            dbMetaDataObject.startTxn();
            SessionManager sessionManager = 
                new SessionManager(dbMetaDataObject);

            if ("LOGIN".equals(serviceName))
                return null;

            if (sessionID == null || sessionID.length() == 0)
                throw RPCExceptionUtil.sendErrorToClient("Invalid Session");

            metaData = metaDataMap.get(serviceName);
            if (metaData == null) {
                throw RPCExceptionUtil.sendErrorToClient("Invalid service name (" + 
                                                         serviceName + ")");
            }
            session = sessionManager.validateSession(sessionID);
            dbMetaDataObject.setSession(session);
            AccessManager accessManager = new AccessManager(dbMetaDataObject);
            if (!"MyAccountMetaData".equals(serviceName)) {
                boolean accessAllowed = 
                    accessManager.isViewAllowed(serviceName, 
                                                session.getUserId());


                if (!accessAllowed) {
                    throw RPCExceptionUtil.sendErrorToClient("[" + 
                                                             serviceName + 
                                                             "] " + 
                                                             ACCESS_DENIED_MSG);
                }
            }
            return session;
        } catch (SerializableException e) {
            throw e;
        } catch (Throwable e) {
            throw RPCExceptionUtil.sendErrorToClient("Exception", e);
        }
    }

    public DBMetaDataObject getDbMetaDataObject() {
        return dbMetaDataObject;
    }

}
