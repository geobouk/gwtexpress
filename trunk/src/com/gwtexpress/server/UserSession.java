package com.gwtexpress.server;

import java.math.BigDecimal;

import java.sql.Timestamp;


public class UserSession {

    private String sessionId;
    private BigDecimal userId;
    private Timestamp creationDate;
    private Timestamp lastAccessTime;
    private Timestamp lastPasswordChanged;
    private String firstName;
    private String lastName;
    private String displayName;
    private String userName;
    private boolean admin = false;
    private String passwordHash;
    public UserSession() {

    }

    public String getSessionId() {
        return this.sessionId;
    }

    public BigDecimal getUserId() {
        return this.userId;
    }


    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setLastAccessTime(Timestamp lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Timestamp getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastPasswordChanged(Timestamp lastPasswordChanged) {
        this.lastPasswordChanged = lastPasswordChanged;
    }

    public Timestamp getLastPasswordChanged() {
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

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
