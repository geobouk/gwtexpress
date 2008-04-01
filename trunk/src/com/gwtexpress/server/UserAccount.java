package com.gwtexpress.server;

import java.math.BigDecimal;

import java.sql.Timestamp;

public class UserAccount {

    private BigDecimal userId;
    private String userName;
    private String passwordHash;
    private String password;
    private Timestamp creationDate;
    private Timestamp lastUpdateDate;
    private Timestamp lastPasswordChanged;
    private String displayName;
    private String firstName;
    private String lastName;
    private Timestamp startDate;
    private Timestamp endDate;
    private String empNo;


    public UserAccount() {
    }

    // { "USER_ID", "USER_NAME", "FIRST_NAME", "LAST_NAME", "DISPLAY_NAME", 
    //   "PASSWORD_HASH", "CREATION_DATE", "LAST_UPDATE_DATE", 
    //   "LAST_PASSWORD_CHANGED", "EMP_NO", "START_DATE", "END_DATE" };

    public UserAccount(String[] row) {
        if (row[0] != null)
            this.userId = new BigDecimal(row[0]);
        this.userName = row[1];
        this.firstName = row[2];
        this.lastName = row[3];
        this.displayName = row[4];
        this.passwordHash = row[5];
//        this.creationDate = row[6];
//        this.lastUpdateDate = row[7];
//        this.lastPasswordChanged = row[8];
//        this.empNo = row[9];
//        this.startDate = row[9];
//        this.endDate = row[9];

    }

    public void setUserId(BigDecimal userId) {
        this.userId = userId;
    }

    public BigDecimal getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setLastUpdateDate(Timestamp lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Timestamp getLastUpdateDate() {
        return lastUpdateDate;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
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

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEmpNo() {
        return empNo;
    }
}
