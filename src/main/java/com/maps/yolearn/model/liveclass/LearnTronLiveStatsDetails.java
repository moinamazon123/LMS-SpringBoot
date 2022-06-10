package com.maps.yolearn.model.liveclass;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "LEARNT_STAT_DET")
public class LearnTronLiveStatsDetails implements Serializable {

    @Id
    @Column(name = "STATDETAIL_ID")
    private String statdetailId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "INSTRUCT_FNAME")
    private String instructorFirstName;

    @Column(name = "INSTRUCT_LNAME")
    private String instructorLastName;

    @Column(name = "PARTIC_UNIQUENAME")
    private String participantUniqueName;

    @Column(name = "TIME_DURATION")
    private int timeDuration;

    @ManyToOne
    @JoinColumn(name = "STAT_ID", referencedColumnName = "STAT_ID")
    private LearnTronLiveStats learnTronLiveStats;

    @OneToMany(mappedBy = "learnTronLiveStatsDetails", cascade = CascadeType.ALL)
    private Collection<Participants> participants;

    public String getStatdetailId() {
        return statdetailId;
    }

    public void setStatdetailId(String statdetailId) {
        this.statdetailId = statdetailId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getInstructorFirstName() {
        return instructorFirstName;
    }

    public void setInstructorFirstName(String instructorFirstName) {
        this.instructorFirstName = instructorFirstName;
    }

    public String getInstructorLastName() {
        return instructorLastName;
    }

    public void setInstructorLastName(String instructorLastName) {
        this.instructorLastName = instructorLastName;
    }

    public String getParticipantUniqueName() {
        return participantUniqueName;
    }

    public void setParticipantUniqueName(String participantUniqueName) {
        this.participantUniqueName = participantUniqueName;
    }

    public int getTimeDuration() {
        return timeDuration;
    }

    public void setTimeDuration(int timeDuration) {
        this.timeDuration = timeDuration;
    }

    public LearnTronLiveStats getLearnTronLiveStats() {
        return learnTronLiveStats;
    }

    public void setLearnTronLiveStats(LearnTronLiveStats learnTronLiveStats) {
        this.learnTronLiveStats = learnTronLiveStats;
    }

    public Collection<Participants> getParticipants() {
        return participants;
    }

    public void setParticipants(Collection<Participants> participants) {
        this.participants = participants;
    }

}
