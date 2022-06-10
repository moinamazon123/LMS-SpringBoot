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
@Table(name = "SYLLABUS_REGISTER")
public class Syllabus implements Serializable {

    @Id
    @Column(name = "SYLLABUS_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String syllabusId;

    @Column(name = "SYLLABUS_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String syllabusName;

    @Column(name = "SYLLABUS_DESC")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String syllabusDesc;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    //    @ManyToOne
//    @JoinColumn(name = "GRADE_ID", referencedColumnName = "GRADE_ID")
//    private Grade grade;
    @Column(name = "GRADE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String gradeId;

    @Column(name = "DISABLED")
    private boolean disabled = false;

    //    @OneToMany(mappedBy = "syllabus")
//    private Collection<Subject> collectionSubject;
    public String getSyllabusId() {
        return syllabusId;
    }

    public void setSyllabusId(String syllabusId) {
        this.syllabusId = syllabusId;
    }

    public String getSyllabusName() {
        return syllabusName;
    }

    public void setSyllabusName(String syllabusName) {
        this.syllabusName = syllabusName;
    }

    public String getSyllabusDesc() {
        return syllabusDesc;
    }

    public void setSyllabusDesc(String syllabusDesc) {
        this.syllabusDesc = syllabusDesc;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
