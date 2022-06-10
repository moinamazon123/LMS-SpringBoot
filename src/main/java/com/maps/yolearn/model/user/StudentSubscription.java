package com.maps.yolearn.model.user;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author VINAYKUMAR
 */
@Entity
@Table(name = "student_subscriptions")
public class StudentSubscription implements Serializable {


    @Id
    @Column(name = "MUL_SUB_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long mulSubscribeID;

    @Column(name = "STUDENT_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String studentAccountId;

    @Column(name = "BATCH_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String batchId;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "GRADE_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })

    private String gradeId;

    @Column(name = "SUBSCRIPTION_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String subscribeId;

    @Column(name = "SYLLABUS_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters"),
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String syllabusId;

    public long getMulSubscribeID() {
        return mulSubscribeID;
    }

    public void setMulSubscribeID(long mulSubscribeID) {
        this.mulSubscribeID = mulSubscribeID;
    }


    public String getStudentAccountId() {
        return studentAccountId;
    }

    public void setStudentAccountId(String studentAccountId) {
        this.studentAccountId = studentAccountId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    @Override
    public String toString() {
        return "StudentSubscription{" + "mulSubscribeID=" + mulSubscribeID + ", studentAccountId=" + studentAccountId + ", batchId=" + batchId + ", dateOfCreation=" + dateOfCreation + ", gradeId=" + gradeId + ", subscribeId=" + subscribeId + ", syllabusId=" + syllabusId + '}';
    }


}
