package com.maps.yolearn.model.liveclass;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "RECORDED_STATS")
public class LearnTronRecordedStats implements Serializable {

    @Id
    @Column(name = "RECSTAT_ID")
    private String recordedStatsId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "UNIQUENAME")
    private String uniqueName;

    @Column(name = "DISP_NAME")
    private String displayName;

    @Column(name = "REC_DURATION")
    private int recordingDuration;

    @Column(name = "END_DURATION")
    private int endDuration;

    @Column(name = "VIEW_PERCETAGE")
    private float viewPercentage;

    public String getRecordedStatsId() {
        return recordedStatsId;
    }

    public void setRecordedStatsId(String recordedStatsId) {
        this.recordedStatsId = recordedStatsId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getRecordingDuration() {
        return recordingDuration;
    }

    public void setRecordingDuration(int recordingDuration) {
        this.recordingDuration = recordingDuration;
    }

    public int getEndDuration() {
        return endDuration;
    }

    public void setEndDuration(int endDuration) {
        this.endDuration = endDuration;
    }

    public float getViewPercentage() {
        return viewPercentage;
    }

    public void setViewPercentage(float viewPercentage) {
        this.viewPercentage = viewPercentage;
    }

    @Override
    public String toString() {
        return "LearnTronRecordedStats{" + "recordedStatsId=" + recordedStatsId + ", sessionId=" + sessionId + ", uniqueName=" + uniqueName + ", displayName=" + displayName + ", recordingDuration=" + recordingDuration + ", endDuration=" + endDuration + ", viewPercentage=" + viewPercentage + '}';
    }

}
