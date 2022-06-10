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
@Table(name = "SUBJECT_REGISTER")
public class Subject implements Serializable {

    @Id
    @Column(name = "SUBJECT_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String subjectId;

    @Column(name = "SUBJECT_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String subjectName;

    @Column(name = "SUBJECT_DESC")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            , @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String subjectDesc;

    @Column(name = "GRADE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String gradeId;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    //    @ManyToOne
//    @JoinColumn(name = "SYLLABUS_ID", referencedColumnName = "SYLLABUS_ID")
//    private Syllabus syllabus;
    @Column(name = "SYLLABUS_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 5 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String syllabusId;

    @Column(name = "DISABLED")
    private boolean disabled = false;

    //    @OneToMany(mappedBy = "subject")
//    private Collection<Chapter> collectionChapter;
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectDesc() {
        return subjectDesc;
    }

    public void setSubjectDesc(String subjectDesc) {
        this.subjectDesc = subjectDesc;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
