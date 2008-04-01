package com.gwtexpress.client.rpc;

import java.util.Date;

public class RPCSession {
    static RPCSession session;
    private String sessionId;
    private String userId;
    private Date creationDate;
    private Date lastAccessTime;
    private Date lastPasswordChanged;
    private String firstName;
    private String lastName;    
    private String displayName;
    private String userName;
    private boolean admin = false;

    public RPCSession() {
    }

    public void removeSession() {
        sessionId = null;
        userId = null;
        creationDate = null;
        lastAccessTime = null;
        lastPasswordChanged = null;
        displayName = null;
        userName = null;
    }

    public static RPCSession getInstance() {
        if (session == null)
            session = new RPCSession();
        return session;
    }

    public void setSession(RPCSession session) {
        this.session = session;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastPasswordChanged(Date lastPasswordChanged) {
        this.lastPasswordChanged = lastPasswordChanged;
    }

    public Date getLastPasswordChanged() {
        return lastPasswordChanged;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }
}
