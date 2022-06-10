/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.maps.yolearn.model.user;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "ADMIN_ACCOUNT")
public class AdminAccount {

    @Id
    @Column(name = "ADMIN_ID")
    private String adminId;

    @Column(name = "FIRSTNAME")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String firstName;

    @Column(name = "LASTNAME")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String lastName;

    @Column(name = "PRIMARY_EMAIL")
    @Length.List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 100, message = "The field must be less than 100 characters")
    })
    private String primaryEmail;

    @Column(name = "PASSWORD")
    @Length.List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String password;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "MOBILE_NUMBER")
    private Long mobileNum;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "ADDRESS")
    @Length.List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 100, message = "The field must be less than 100 characters")
    })
    private String address;

    @Column(name = "CITY")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 30, message = "The field must be less than 30 characters")
    })
    private String city;

    @Column(name = "STATUS")
    private Boolean status;

    @Column(name = "PARENT_ACCESS")
    private Boolean parentAccess;

    @Column(name = "TEACHER_ACCESS")
    private Boolean teacherAccess;

    @Column(name = "STUDENT_ACCESS")
    private Boolean studentAccess;

    @Column(name = "DEMO_MEMBERS_ACCESS")
    private Boolean demoMemberAccess;

    @Column(name = "CLASS_ROOM_ACCESS")
    private Boolean classRoomAccess;

    @Column(name = "SCHEDULE_ACCESS")
    private Boolean scheduleAccess;

    @Column(name = "PRODUCT_ACCESS")
    private Boolean produtAccess;

    @Column(name = "SUBSCRIPTION_ACCESS")
    private Boolean subscriptionAccess;

    @Column(name = "BATCH_ACCESS")
    private Boolean batchAccess;

    @Column(name = "TALENT_ACCESS")
    private Boolean talentAccess;

    @Column(name = "RECORDED_ACCESS")
    private Boolean recordedAccess;

    @Column(name = "UPCOMING_ACCESS")
    private Boolean upcomingAccess;

    @Column(name = "ASSIGNMENT_ACCESS")
    private Boolean assignmentAccess;

    @Column(name = "TEST_ACCESS")
    private Boolean testAccess;
    @Column(name = "ADMIN_ACCESS")
    private Boolean adminAccess;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ACCOUNT_ID")
    private Registration registration;

    public Boolean getAdminAccess() {
        return adminAccess;
    }

    public void setAdminAccess(Boolean adminAccess) {
        this.adminAccess = adminAccess;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(Long mobileNum) {
        this.mobileNum = mobileNum;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Boolean getParentAccess() {
        return parentAccess;
    }

    public void setParentAccess(Boolean parentAccess) {
        this.parentAccess = parentAccess;
    }

    public Boolean getTeacherAccess() {
        return teacherAccess;
    }

    public void setTeacherAccess(Boolean teacherAccess) {
        this.teacherAccess = teacherAccess;
    }

    public Boolean getStudentAccess() {
        return studentAccess;
    }

    public void setStudentAccess(Boolean studentAccess) {
        this.studentAccess = studentAccess;
    }

    public Boolean getDemoMemberAccess() {
        return demoMemberAccess;
    }

    public void setDemoMemberAccess(Boolean demoMemberAccess) {
        this.demoMemberAccess = demoMemberAccess;
    }

    public Boolean getClassRoomAccess() {
        return classRoomAccess;
    }

    public void setClassRoomAccess(Boolean classRoomAccess) {
        this.classRoomAccess = classRoomAccess;
    }

    public Boolean getScheduleAccess() {
        return scheduleAccess;
    }

    public void setScheduleAccess(Boolean scheduleAccess) {
        this.scheduleAccess = scheduleAccess;
    }

    public Boolean getProdutAccess() {
        return produtAccess;
    }

    public void setProdutAccess(Boolean produtAccess) {
        this.produtAccess = produtAccess;
    }

    public Boolean getSubscriptionAccess() {
        return subscriptionAccess;
    }

    public void setSubscriptionAccess(Boolean subscriptionAccess) {
        this.subscriptionAccess = subscriptionAccess;
    }

    public Boolean getBatchAccess() {
        return batchAccess;
    }

    public void setBatchAccess(Boolean batchAccess) {
        this.batchAccess = batchAccess;
    }

    public Boolean getTalentAccess() {
        return talentAccess;
    }

    public void setTalentAccess(Boolean talentAccess) {
        this.talentAccess = talentAccess;
    }

    public Boolean getRecordedAccess() {
        return recordedAccess;
    }

    public void setRecordedAccess(Boolean recordedAccess) {
        this.recordedAccess = recordedAccess;
    }

    public Boolean getUpcomingAccess() {
        return upcomingAccess;
    }

    public void setUpcomingAccess(Boolean upcomingAccess) {
        this.upcomingAccess = upcomingAccess;
    }

    public Boolean getAssignmentAccess() {
        return assignmentAccess;
    }

    public void setAssignmentAccess(Boolean assignmentAccess) {
        this.assignmentAccess = assignmentAccess;
    }

    public Boolean getTestAccess() {
        return testAccess;
    }

    public void setTestAccess(Boolean testAccess) {
        this.testAccess = testAccess;
    }


}
