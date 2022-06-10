package com.maps.yolearn.bean.mail;

/**
 * @author KOTARAJA
 */
public class MailBean {

    private String mailId;
    private String name;
    private String senderMail;
    private String subject;
    private String body;
    private String dateOfCreation;
    private String phoneNumber;
    private String grade;
    private Boolean status;
    private String parentAccountId;
    private String studentAccountId;
    private String adminAccountId;
    private String accountId;
    private String teacherAccountId;
    private String password;
    private String suppoetMail;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAdminAccountId() {
        return adminAccountId;
    }

    public void setAdminAccountId(String adminAccountId) {
        this.adminAccountId = adminAccountId;
    }

    public String getStudentAccountId() {
        return studentAccountId;
    }

    public void setStudentAccountId(String studentAccountId) {
        this.studentAccountId = studentAccountId;
    }

    public String getTeacherAccountId() {
        return teacherAccountId;
    }

    public void setTeacherAccountId(String teacherAccountId) {
        this.teacherAccountId = teacherAccountId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSuppoetMail() {
        return suppoetMail;
    }

    public void setSuppoetMail(String suppoetMail) {
        this.suppoetMail = suppoetMail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(String dateOfCreation) {
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
