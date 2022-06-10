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
@Table(name = "STUDENT_ACCOUNT")
public class StudentAccount implements Serializable {

    @Id
    @Column(name = "STUDENT_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String studentAccountId;

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

    @Column(name = "PARENT_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String parentAccountId;

    @Column(name = "STATUS")
    private Boolean status;

    @Column(name = "SCHOOL_NAME")
    @List({
            @Length(min = 5, message = "The field must be at least 5 characters")
            ,
            @Length(max = 30, message = "The field must be less than 30 characters")
    })
    private String schoolName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ACCOUNT_ID")
    private Registration registration;

    @Column(name = "SUBSCRIPTION_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String subscribeId;

    @Column(name = "GRADE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String gradeId;

    @Column(name = "SYLLABUS_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String syllabusId;

    @Column(name = "BATCH_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String batchId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getStudentAccountId() {
        return studentAccountId;
    }

    public void setStudentAccountId(String studentAccountId) {
        this.studentAccountId = studentAccountId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
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

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(Registration registration) {
        this.registration = registration;
    }

    public String getParentAccountId() {
        return parentAccountId;
    }

    public void setParentAccountId(String parentAccountId) {
        this.parentAccountId = parentAccountId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    @Override
    public String toString() {
        return "StudentAccount{" + "studentAccountId=" + studentAccountId + ", firstName=" + firstName + ", lastName=" + lastName + ", primaryEmail=" + primaryEmail + ", password=" + password + ", countryCode=" + countryCode + ", dateOfCreation=" + dateOfCreation + ", address=" + address + ", city=" + city + ", parentAccountId=" + parentAccountId + ", status=" + status + ", schoolName=" + schoolName + ", registration=" + registration + ", subscribeId=" + subscribeId + ", gradeId=" + gradeId + ", syllabusId=" + syllabusId + ", batchId=" + batchId + '}';
    }


}
