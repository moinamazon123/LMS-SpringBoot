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
@Table(name = "GRADE_TAB")
public class Grade implements Serializable {

    @Id
    @Column(name = "GRADE_ID")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String gradeId;

    @Column(name = "GRADE_NAME", unique = true)
    @List({
            @Length(max = 5, message = "The field must be less than 5 characters")
    })
    private String gradeName;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @Column(name = "DISABLED")
    private boolean disabled = false;

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

}
