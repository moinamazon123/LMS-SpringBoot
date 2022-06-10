package com.maps.yolearn.model.liveclass;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "PARTICIPANTS")
public class Participants implements Serializable {

    @Id
    @Column(name = "PARTICIPANT_ID")
    private String participantId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "PARTIC_UNIQUENAME")
    private String participantUniqueName;

    @Column(name = "PARTIC_DISPNAME")
    private String participantDisplayName;

    @Column(name = "VIEWDURATION")
    private int viewDuration;

    @Column(name = "VIEW_PERCETAGE")
    private float viewPercentage;

    @ManyToOne
    @JoinColumn(name = "STATDETAIL_ID", referencedColumnName = "STATDETAIL_ID")
    private LearnTronLiveStatsDetails learnTronLiveStatsDetails;

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getParticipantUniqueName() {
        return participantUniqueName;
    }

    public void setParticipantUniqueName(String participantUniqueName) {
        this.participantUniqueName = participantUniqueName;
    }

    public String getParticipantDisplayName() {
        return participantDisplayName;
    }

    public void setParticipantDisplayName(String participantDisplayName) {
        this.participantDisplayName = participantDisplayName;
    }

    public int getViewDuration() {
        return viewDuration;
    }

    public void setViewDuration(int viewDuration) {
        this.viewDuration = viewDuration;
    }

    public LearnTronLiveStatsDetails getLearnTronLiveStatsDetails() {
        return learnTronLiveStatsDetails;
    }

    public void setLearnTronLiveStatsDetails(LearnTronLiveStatsDetails learnTronLiveStatsDetails) {
        this.learnTronLiveStatsDetails = learnTronLiveStatsDetails;
    }

    public float getViewPercentage() {
        return viewPercentage;
    }

    public void setViewPercentage(float viewPercentage) {
        this.viewPercentage = viewPercentage;
    }

}
