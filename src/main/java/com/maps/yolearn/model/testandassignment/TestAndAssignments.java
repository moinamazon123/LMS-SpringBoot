package com.maps.yolearn.model.testandassignment;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length.List;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "TEST_ASSIGNMENT")
public class TestAndAssignments implements Serializable {

    @Id
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "FILE_ID")
    private String fileId;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 150, message = "The field must be less than 150 characters")
    })
    @Column(name = "TITLE")
    private String title;

    @Column(name = "ASSIGNMENTS_FILE")
    private String assignmentFile;

    @Column(name = "TEST_FILE_NAME")
    private String testFile;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "GRADE_ID")
    private String gradeId;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "SUBJECT_ID")
    private String subjectId;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "SYLLABUS_ID")
    private String syllabusId;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "ACCESS_TO")
    private String accessTo;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "DURATION_MINUTES")
    private int durationinMinutes;

    @Column(name = "SCHEDULE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledDate;

    public int getDurationinMinutes() {
        return durationinMinutes;
    }

    public void setDurationinMinutes(int durationinMinutes) {
        this.durationinMinutes = durationinMinutes;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignmentFile() {
        return assignmentFile;
    }

    public void setAssignmentFile(String assignmentFile) {
        this.assignmentFile = assignmentFile;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getAccessTo() {
        return accessTo;
    }

    public void setAccessTo(String accessTo) {
        this.accessTo = accessTo;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

}
