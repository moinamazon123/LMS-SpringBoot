package com.maps.yolearn.model.liveclass;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

/**
 * @author PREMNATH
 */
@Entity
@Table(name = "LEARNTRON_STATS")
public class LearnTronLiveStats implements Serializable {

    @Id
    @Column(name = "STAT_ID")
    private String statId;

    @Column(name = "SESSION_ID")
    private String sessionId;

    @Column(name = "PARTIC_UNIQUENAME")
    private String participantUniqueName;

    @OneToMany(mappedBy = "learnTronLiveStats", cascade = CascadeType.ALL)
    private Collection<LearnTronLiveStatsDetails> learnTronLiveStatsDetails;

    public String getStatId() {
        return statId;
    }

    public void setStatId(String statId) {
        this.statId = statId;
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

    public Collection<LearnTronLiveStatsDetails> getLearnTronLiveStatsDetails() {
        return learnTronLiveStatsDetails;
    }

    public void setLearnTronLiveStatsDetails(Collection<LearnTronLiveStatsDetails> learnTronLiveStatsDetails) {
        this.learnTronLiveStatsDetails = learnTronLiveStatsDetails;
    }

}
