package com.maps.yolearn.model.liveclass;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "BATCH")
public class Batch implements Serializable {

    @Id
    @Column(name = "BATCH_ID")
    private String batchId;
    @Column(name = "BATCH_NAME")
    private String batchName;
    @Column(name = "SYLLABUS_ID")
    private String syllabusId;
    @Column(name = "GRADE_ID")
    private String gradeId;
    @Column(name = "DESCRIPTION")
    private String description;

    //    @Column(name = "DATE_FROM")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateFrom;
//
//    @Column(name = "DATE_TILL")
//    @Temporal(TemporalType.TIMESTAMP)
//    private Date dateTill;
    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

}
