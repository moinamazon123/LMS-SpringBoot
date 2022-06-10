package com.maps.yolearn.model.liveclass;

import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "STU_CLSS_ACCESS")
public class StudentBlockedByAdmin implements Serializable {

    @Id
    @Column(name = "BLOCK_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String blockId;

    @Column(name = "STUDENT_ID")
    @Length.List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    private String studentAccountId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "DATE_OF_CREATION")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public String getStudentAccountId() {
        return studentAccountId;
    }

    public void setStudentAccountId(String studentAccountId) {
        this.studentAccountId = studentAccountId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

}
