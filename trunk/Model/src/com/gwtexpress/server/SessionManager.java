package com.gwtexpress.server;

import com.google.gwt.user.client.rpc.SerializableException;

import com.gwtexpress.server.util.RPCExceptionUtil;
import com.gwtexpress.server.util.SendEmail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;


public class SessionManager {
    private static final String INSERT_SESSION_SQL = 
        "INSERT INTO user_sessions (SESSION_ID, USER_ID, CREATION_DATE, LAST_ACCESS_TIME) VALUES (?, ?, ?, ?)";
    //    private static final String SELECT_USER_SQL = 
    //        "SELECT u.LAST_PASSWORD_CHANGED, u.DISPLAY_NAME FROM user_accounts u WHERE u.USER_ID = ?";
    private static final String DELETE_SESSION_SQL = 
        "DELETE FROM user_sessions WHERE SESSION_ID=?";
    private static final String SELECT_SESSION_SQL = 
        "SELECT s.USER_ID, s.CREATION_DATE, s.LAST_ACCESS_TIME, u.USER_NAME, u.LAST_PASSWORD_CHANGED, u.DISPLAY_NAME , u.FIRST_NAME, u.LAST_NAME, u.PASSWORD_HASH FROM user_accounts u , user_sessions s WHERE u.USER_ID = s.USER_ID AND s.SESSION_ID=?";
    private static final String SELECT_USER_ACCOUNT_SQL = 
        "SELECT USER_ID,USER_NAME, PASSWORD_HASH,CREATION_DATE, LAST_UPDATE_DATE,LAST_PASSWORD_CHANGED,DISPLAY_NAME, FIRST_NAME, LAST_NAME, START_DATE, END_DATE, EMP_NO FROM user_accounts WHERE USER_NAME=? AND IFNULL(START_DATE, ADDDATE(SYSDATE(), 1)) < SYSDATE() AND IFNULL(END_DATE, ADDDATE(SYSDATE(), 1)) > SYSDATE()";
    private static final String UPDATE_SESSION_SQL = 
        "UPDATE user_sessions SET LAST_ACCESS_TIME = ? WHERE SESSION_ID=?";
    private static final String RESET_PWD_SQL = 
        "UPDATE user_accounts SET LAST_PASSWORD_CHANGED = ?, PASSWORD_HASH = ? WHERE USER_NAME=?";
    private static final String SELECT_ROLES_SQL = 
        "SELECT ROLE FROM user_roles WHERE USER_ID = ?";
    UUID uuid;
    DBMetaDataObject dbMetaDataObject;
    static final int SESSION_TIMEOUT_IN_MINUTES = 1440;

    public SessionManager(DBMetaDataObject dbMetaDataObject) {
        //uuid = UUID.fromString(sessionID);
        this.dbMetaDataObject = dbMetaDataObject;
    }

    public UserSession validateSession(String sessionID) throws SQLException, 
                                                                SerializableException {
        UserSession session = getUserSession(sessionID);
        return session;
    }

    public static String getHashValue(String value) {
        return BCrypt.hashpw(value, BCrypt.gensalt());
    }

    public void createUserAccount(UserAccount userAccount) throws SQLException, 
                                                                  Exception {
        if (userAccount.getUserName() == null || 
            userAccount.getPassword() == null || 
            userAccount.getDisplayName() == null) {
            throw new Exception("Missing username or password or display name");
        }
        if (getUserAccount(userAccount.getUserName()) != null) {
            throw new Exception("Username already exists.");
        }
        userAccount.setPasswordHash(BCrypt.hashpw(userAccount.getPassword(), BCrypt.gensalt()));
        String sql = 
            "INSERT   INTO user_accounts" + "              (USER_ID," + 
            "               USER_NAME," + "               PASSWORD_HASH," + 
            "               CREATION_DATE," + 
            "               LAST_UPDATE_DATE," + 
            "               LAST_PASSWORD_CHANGED," + 
            "               DISPLAY_NAME)" + "   VALUES (?,?,?,?,?,?,?)";
        Timestamp sysdate = 
            new Timestamp(Calendar.getInstance().getTimeInMillis());

        userAccount.setCreationDate(sysdate);
        userAccount.setLastPasswordChanged(sysdate);
        userAccount.setLastUpdateDate(sysdate);

        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement insertPS = con.prepareStatement(sql);
        insertPS.setObject(1, null);
        insertPS.setString(2, userAccount.getUserName().toUpperCase());
        insertPS.setString(3, userAccount.getPasswordHash());
        insertPS.setTimestamp(4, sysdate);
        insertPS.setTimestamp(5, sysdate);
        insertPS.setTimestamp(6, sysdate);
        insertPS.setString(7, userAccount.getDisplayName());
        insertPS.executeUpdate();
        ResultSet rs = insertPS.getGeneratedKeys();
        while (rs.next()) {
            userAccount.setUserId(rs.getBigDecimal(1));
        }
        con.commit();
    }

    public UserSession createUserSession(UserAccount userAccount) throws SQLException, 
                                                                         SerializableException {
        if (userAccount.getUserId() == null) {
            throw RPCExceptionUtil.sendErrorToClient("Error creating session. Missing user ID");
        }
        uuid = UUID.randomUUID();
        String sessionID = uuid.toString();
        UserSession session = new UserSession();
        session.setSessionId(sessionID);
        session.setUserId(userAccount.getUserId());
        session.setUserName(userAccount.getUserName());
        session.setLastPasswordChanged(userAccount.getLastPasswordChanged());
        session.setDisplayName(userAccount.getDisplayName());

        String sql = INSERT_SESSION_SQL;
        Timestamp sysdate = 
            new Timestamp(Calendar.getInstance().getTimeInMillis());

        session.setCreationDate(sysdate);
        session.setLastAccessTime(sysdate);

        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement insertPS = con.prepareStatement(sql);
        insertPS.setObject(1, sessionID);
        insertPS.setBigDecimal(2, userAccount.getUserId());
        insertPS.setTimestamp(3, sysdate);
        insertPS.setTimestamp(4, sysdate);
        insertPS.executeUpdate();
        con.commit();

        try {
            if (insertPS != null)
                insertPS.close();
        } catch (Throwable e) {
        }
        return getUserSession(sessionID);
    }

    public void removeSession(String sessionID) throws SQLException {
        if (sessionID == null)
            return;

        String sql = DELETE_SESSION_SQL;
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, sessionID);
        int i = ps.executeUpdate();
        con.commit();
        try {
            if (ps != null)
                ps.close();
        } catch (Throwable e) {
        }
    }

    public UserSession getUserSession(String sessionID) throws SQLException, 
                                                               SerializableException {
        if (sessionID == null)
            return null;

        String sql = SELECT_SESSION_SQL;
        Timestamp sysdate = 
            new Timestamp(Calendar.getInstance().getTimeInMillis());
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, sessionID);

        UserSession session = new UserSession();
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            session.setSessionId(sessionID);
            session.setUserId(rs.getBigDecimal(1));
            session.setCreationDate(rs.getTimestamp(2));
            session.setLastAccessTime(rs.getTimestamp(3));
            session.setUserName(rs.getString(4));
            session.setLastPasswordChanged(rs.getTimestamp(5));
            session.setDisplayName(rs.getString(6));
            session.setFirstName(rs.getString(7));
            session.setLastName(rs.getString(8));
            session.setPasswordHash(rs.getString(9));
            try {
                if (rs != null)
                    rs.close();
            } catch (Throwable e) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Throwable e) {
            }

            long diff = 
                sysdate.getTime() - session.getLastAccessTime().getTime();
            if (diff / (1000 * 60) > SESSION_TIMEOUT_IN_MINUTES) {
                throw RPCExceptionUtil.sendErrorToClient("You session expired on the server. Please login again.");
            }
            String updateSql = UPDATE_SESSION_SQL;
            ps = con.prepareStatement(updateSql);
            ps.setTimestamp(1, sysdate);
            ps.setString(2, sessionID);
            int i = ps.executeUpdate();
            con.commit();
            try {
                if (ps != null)
                    ps.close();
            } catch (Throwable e) {
            }

            ps = con.prepareStatement(SELECT_ROLES_SQL);
            ps.setBigDecimal(1, session.getUserId());
            rs = ps.executeQuery();
            String role;
            while (rs.next()) {
                role = rs.getString(1);
                if ("ADMIN".equals(role)) {
                    session.setAdmin(true);
                }
            }
            try {
                if (rs != null)
                    rs.close();
            } catch (Throwable e) {
            }
            try {
                if (ps != null)
                    ps.close();
            } catch (Throwable e) {
            }
        } else {
            throw RPCExceptionUtil.sendErrorToClient("Invalid Session. Please login.");
        }
        return session;
    }

    public UserAccount getUserAccount(String userName) throws SQLException {
        if (userName == null)
            return null;
        String sql = SELECT_USER_ACCOUNT_SQL;
        Timestamp sysdate = 
            new Timestamp(Calendar.getInstance().getTimeInMillis());
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);

        ps.setString(1, userName.toUpperCase());

        UserAccount userAccount = new UserAccount();
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            userAccount.setUserId(rs.getBigDecimal(1));
            userAccount.setUserName(rs.getString(2));
            userAccount.setPasswordHash(rs.getString(3));
            userAccount.setCreationDate(rs.getTimestamp(4));
            userAccount.setLastUpdateDate(rs.getTimestamp(5));
            userAccount.setLastPasswordChanged(rs.getTimestamp(6));
            userAccount.setDisplayName(rs.getString(7));
            userAccount.setFirstName(rs.getString(8));
            userAccount.setLastName(rs.getString(9));
            userAccount.setStartDate(rs.getTimestamp(10));
            userAccount.setEndDate(rs.getTimestamp(11));
            userAccount.setEmpNo(rs.getString(12));
        } else {
            return null;
        }
        try {
            if (rs != null)
                rs.close();
        } catch (Throwable e) {
        }
        try {
            if (ps != null)
                ps.close();
        } catch (Throwable e) {
        }
        return userAccount;
    }

    public UserSession authenticateUser(String userName, 
                                        String password) throws SQLException, 
                                                                SerializableException {
        UserAccount userAccount = getUserAccount(userName);
        if (userAccount != null && userAccount.getPasswordHash() != null) {
            boolean validPwd = BCrypt.checkpw(password, userAccount.getPasswordHash());
            if (validPwd) {
                UserSession userSession = createUserSession(userAccount);
                return userSession;
            }
        }
        throw RPCExceptionUtil.sendErrorToClient("IUP");
    }

    public static String getPassword(int n) {
        char[] pw = new char[n];
        int c = 'A';
        int r1 = 0;
        for (int i = 0; i < n; i++) {
            r1 = (int)(Math.random() * 3);
            switch (r1) {
            case 0:
                c = '0' + (int)(Math.random() * 10);
                break;
            case 1:
                c = 'a' + (int)(Math.random() * 26);
                break;
            case 2:
                c = 'A' + (int)(Math.random() * 26);
                break;
            }
            pw[i] = (char)c;
        }
        return new String(pw);
    }

    public void resetPassword(String userName) throws SQLException, 
                                                      SerializableException {
        if (userName == null)
            return;
        Date sysdate = 
            new Date(Calendar.getInstance().getTimeInMillis());
        Connection con = dbMetaDataObject.getConnection();
        PreparedStatement ps = con.prepareStatement(RESET_PWD_SQL);
        ps.setDate(1, sysdate);
        String newPwd = getPassword(12);
        ps.setString(2, getHashValue(newPwd));
        ps.setString(3, userName.toUpperCase());
        int i = ps.executeUpdate();
        if (i==0){
            throw RPCExceptionUtil.sendErrorToClient("Invalid username");
        }
        con.commit();
        ArrayList<String> toList = new ArrayList<String>(1);
        toList.add(userName.toLowerCase());
        StringBuffer body = new StringBuffer();
        body.append("Your new password is ");
        body.append(newPwd);
        SendEmail.sendEmail(toList, "You new password", body.toString());
        try {
            if (ps != null)
                ps.close();
        } catch (Throwable e) {
        }
    }
}
