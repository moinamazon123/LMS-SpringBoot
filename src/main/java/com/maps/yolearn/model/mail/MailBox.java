package com.maps.yolearn.model.mail;

import javax.persistence.*;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "MAIL_BOX")
public class MailBox {

    @Id
    @Column(name = "MAIL_ID")
    private String mailId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MAIL")
    private String senderMail;

    @Column(name = "subject")
    private String subject;

    @Column(name = "BODY")
    private String body;

    @Column(name = "STATUS")
    private Boolean status;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSenderMail() {
        return senderMail;
    }

    public void setSenderMail(String senderMail) {
        this.senderMail = senderMail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

}
