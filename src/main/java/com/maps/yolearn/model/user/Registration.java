package com.maps.yolearn.model.user;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length.List;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "REGISTRATION")
public class Registration implements Serializable {

    @Id
    @Column(name = "ACCOUNT_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String accountId;

    @Column(name = "FIRST_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String firstName;

    @Column(name = "LAST_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String lastName;

    @Column(name = "PRIMARY_EMAIL")
    @List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 100, message = "The field must be less than 100 characters")
    })
    private String primaryEmail;

    @Column(name = "PASSWORD")
    @List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String password;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "MOBILE_NUMBER")
    private Long mobileNum;

    @Column(name = "POSITION")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String userRole;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "ADDRESS")
    @List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 100, message = "The field must be less than 100 characters")
    })
    private String address;

    @Column(name = "CITY")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 30, message = "The field must be less than 30 characters")
    })
    private String city;

    @Column(name = "GRADE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String gradeId;

    @Column(name = "MAIL_SUBSC")
    private Boolean mailSubscriptionStatus;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private ParentAccount parentAccount;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private StudentAccount studentAccount;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private TeacherAccount teacherAccount;

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
    private AdminAccount adminAccount;

//    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL)
//    private AdminAccount adminAccount;

    public AdminAccount getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(AdminAccount adminAccount) {
        this.adminAccount = adminAccount;
    }


    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public Boolean getMailSubscriptionStatus() {
        return mailSubscriptionStatus;
    }

    public void setMailSubscriptionStatus(Boolean mailSubscriptionStatus) {
        this.mailSubscriptionStatus = mailSubscriptionStatus;
    }

    public ParentAccount getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(ParentAccount parentAccount) {
        this.parentAccount = parentAccount;
    }

    public StudentAccount getStudentAccount() {
        return studentAccount;
    }

    public void setStudentAccount(StudentAccount studentAccount) {
        this.studentAccount = studentAccount;
    }

    public TeacherAccount getTeacherAccount() {
        return teacherAccount;
    }

    public void setTeacherAccount(TeacherAccount teacherAccount) {
        this.teacherAccount = teacherAccount;
    }

    //    public AdminAccount getAdminAccount() {
//        return adminAccount;
//    }
//
//    public void setAdminAccount(AdminAccount adminAccount) {
//        this.adminAccount = adminAccount;
//    }
    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    @Override
    public String toString() {
        return "Registration{" + "accountId=" + accountId + ", firstName=" + firstName + ", lastName=" + lastName + ", primaryEmail=" + primaryEmail + ", password=" + password + ", countryCode=" + countryCode + ", mobileNum=" + mobileNum + ", userRole=" + userRole + ", dateOfCreation=" + dateOfCreation + ", address=" + address + ", city=" + city + ", gradeId=" + gradeId + ", mailSubscriptionStatus=" + mailSubscriptionStatus + ", parentAccount=" + parentAccount + ", studentAccount=" + studentAccount + ", teacherAccount=" + teacherAccount + ", adminAccount=" + adminAccount + '}';
    }


}
