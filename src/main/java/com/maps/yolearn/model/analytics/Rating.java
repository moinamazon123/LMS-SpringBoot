
package com.maps.yolearn.model.analytics;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "RATING_LIVE_SESSION")
public class Rating {
    @Id
    @Column(name = "RATING_ID")
    private String id;

    @Column(name = "SESSION_ID")
    private String sesionId;

    @Column(name = "TOTAL_RATING")
    private int totalRating;

    @Column(name = "FEEDBACK")
    private String feedBack;

    @Column(name = "COMMENT")
    private String comment;
    @Column(name = "STUDENT_ID")
    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSesionId() {
        return sesionId;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(int totalRating) {
        this.totalRating = totalRating;
    }


    public String getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(String feedBack) {
        this.feedBack = feedBack;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


}
