package com.maps.yolearn.model.liveclass;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Length.List;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author KOTARAJA
 */
@Entity
@Table(name = "CLASS_SCHEDULER")
public class ClassScheduler implements Serializable {

    @Id
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "CLASS_ID")
    private String classID;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 150, message = "The field must be less than 150 characters")
    })
    @Column(name = "TITLE")
    private String title;

    @Column(name = "DURATION_MINUTES")
    private int durationinMinutes;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 30, message = "The field must be less than 30 characters")
    })
    @Column(name = "PRESENTER_UNIQUE_NAME")
    private String presenterUniqueName;

    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 15, message = "The field must be less than 15 characters")
    })
    @Column(name = "PRESENTER_DISPALY_NAME")
    private String presenterDisplayName;

    @Column(name = "CAN_EXTEND")
    private Boolean canExtend;

    @Column(name = "IS_RECORDED_SESSION_VIEABLE")
    private Boolean isRecordedSessionViewable;

    @Column(name = "FORCE_EXIT_PARTICIPANTS")
    private Boolean forceExitParticipants;

    @Column(name = "RESTART_SESSION")
    private Boolean restartSession;

    @Column(name = "SCHEDULE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date scheduledDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "TEACHER_ID")
    private String teacherId;

    @Column(name = "SUBSCRIPTION_ID")
    private String subscriptionId;

    @Column(name = "PREPARE_URL")
    private String prepareUrl;
    @Column(name = "PRESENT_URL")
    private String presentUrl;
    @Column(name = "REPLAY_URL")
    private String replayUrl;
    @Column(name = "GUEST_URL")
    private String guestUrl;
    @Column(name = "GRADE_ID")
    private String gradeId;

    @Column(name = "GRADE_NAME")
    @List({
            @Length(max = 5, message = "The field must be less than 5 characters")
    })
    private String gradeName;

    @Column(name = "SYLLABUS_ID")
    private String syllabusId;

    @Column(name = "SYLLABUS_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String syllabusName;

    @Column(name = "SUBJECT_ID")
    private String subjectId;

    @Column(name = "SUBJECT_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String subjectName;

    @Column(name = "ACCESS_TO")
    private String accessTo;

    @Column(name = "AVG_RATING")
    private float avgRating;

    @Column(name = "CHAPTER_ID")
    private String chapterId;

    @Column(name = "CHAPTER_NAME")
    @List({
            @Length(min = 2, message = "The field must be at least 2 characters")
            ,
            @Length(max = 50, message = "The field must be less than 50 characters")
    })
    private String chapterName;

    @Column(name = "NO_OF_SEATS")
    private int noOfSeats;

    @Column(name = "BATCH_ID")
    private String batchId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getAccessTo() {
        return accessTo;
    }

    public void setAccessTo(String accessTo) {
        this.accessTo = accessTo;
    }

    public int getDurationinMinutes() {
        return durationinMinutes;
    }

    public void setDurationinMinutes(int durationinMinutes) {
        this.durationinMinutes = durationinMinutes;
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

    public String getPrepareUrl() {
        return prepareUrl;
    }

    public void setPrepareUrl(String prepareUrl) {
        this.prepareUrl = prepareUrl;
    }

    public String getPresentUrl() {
        return presentUrl;
    }

    public void setPresentUrl(String presentUrl) {
        this.presentUrl = presentUrl;
    }

    public String getReplayUrl() {
        return replayUrl;
    }

    public void setReplayUrl(String replayUrl) {
        this.replayUrl = replayUrl;
    }

    public String getGuestUrl() {
        return guestUrl;
    }

    public void setGuestUrl(String guestUrl) {
        this.guestUrl = guestUrl;
    }

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getClassID() {
        return classID;
    }

    public void setClassID(String classID) {
        this.classID = classID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPresenterUniqueName() {
        return presenterUniqueName;
    }

    public void setPresenterUniqueName(String presenterUniqueName) {
        this.presenterUniqueName = presenterUniqueName;
    }

    public String getPresenterDisplayName() {
        return presenterDisplayName;
    }

    public void setPresenterDisplayName(String presenterDisplayName) {
        this.presenterDisplayName = presenterDisplayName;
    }

    public Boolean getCanExtend() {
        return canExtend;
    }

    public void setCanExtend(Boolean canExtend) {
        this.canExtend = canExtend;
    }

    public Boolean getIsRecordedSessionViewable() {
        return isRecordedSessionViewable;
    }

    public void setIsRecordedSessionViewable(Boolean isRecordedSessionViewable) {
        this.isRecordedSessionViewable = isRecordedSessionViewable;
    }

    public Boolean getForceExitParticipants() {
        return forceExitParticipants;
    }

    public void setForceExitParticipants(Boolean forceExitParticipants) {
        this.forceExitParticipants = forceExitParticipants;
    }

    public Boolean getRestartSession() {
        return restartSession;
    }

    public void setRestartSession(Boolean restartSession) {
        this.restartSession = restartSession;
    }

    public Date getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(Date scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public float getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getSyllabusName() {
        return syllabusName;
    }

    public void setSyllabusName(String syllabusName) {
        this.syllabusName = syllabusName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(int noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

}
