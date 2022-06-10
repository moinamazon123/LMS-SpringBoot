package com.maps.yolearn.model.analytics;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "REC_VID_ANALYTICS")
public class RecordedVideoAnalytics implements Serializable {

    @Id
    @Column(name = "VIDEO_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String analyticsId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "ACCOUNT_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String accountId;

    @Column(name = "REASON")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 30, message = "The field must be less than 30 characters")
    })
    private String reasonDescription;

    @Column(name = "ACCESS_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date accessDate;

    public String getAnalyticsId() {
        return analyticsId;
    }

    public void setAnalyticsId(String analyticsId) {
        this.analyticsId = analyticsId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getReasonDescription() {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription) {
        this.reasonDescription = reasonDescription;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

}
