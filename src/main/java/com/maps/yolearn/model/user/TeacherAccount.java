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
@Table(name = "TEACHER_ACCOUNT")
public class TeacherAccount implements Serializable {

    @Id
    @Column(name = "TEACHER_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String teacherAccountId;

    @Column(name = "FIRSTNAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 20, message = "The field must be less than 20 characters")
    })
    private String firstName;

    @Column(name = "LASTNAME")
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

    @Column(name = "STATUS")
    private Boolean status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ACCOUNT_ID")
    private Registration registration;

    public String getTeacherAccountId() {
        return teacherAccountId;
    }

    public void setTeacherAccountId(String teacherAccountId) {
        this.teacherAccountId = teacherAccountId;
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

}
