package com.maps.yolearn.model.user;

import javax.persistence.*;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "USER_HISTORY")
public class UserHistory {

    @Id
    @Column(name = "CLASS_HISTORY_ID")
    private String classHistory;

    @Column(name = "ACCOUNT_ID")
    private String accountId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "CLASS_NAME")
    private String className;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassHistory() {
        return classHistory;
    }

    public void setClassHistory(String classHistory) {
        this.classHistory = classHistory;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

}
