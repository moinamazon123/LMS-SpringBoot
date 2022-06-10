package com.maps.yolearn.model.grade;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length.List;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "CHAPTER_REGISTER")
public class Chapter implements Serializable {

    @Id
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "CHAPTER_ID")
    private String chapterId;

    @Column(name = "CHAPTER_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String chapterName;

    @Column(name = "CHAPTER_DESC")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String chapterDesc;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

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

    //    @ManyToOne
//    @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "SUBJECT_ID")
//    private Subject subject;
    @Column(name = "SUBJECT_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String subjectId;

    @Column(name = "DISABLED")
    private boolean disabled = false;

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterDesc() {
        return chapterDesc;
    }

    public void setChapterDesc(String chapterDesc) {
        this.chapterDesc = chapterDesc;
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

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
