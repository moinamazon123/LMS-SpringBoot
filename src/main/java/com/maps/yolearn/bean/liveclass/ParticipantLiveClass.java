package com.maps.yolearn.bean.liveclass;

/**
 * @author PREMNATH
 */
public class ParticipantLiveClass {

    private String participantUniqueName;
    private String startTime;
    private String endTime;
    private int totatlTimeDuration;

    public String getParticipantUniqueName() {
        return participantUniqueName;
    }

    public void setParticipantUniqueName(String participantUniqueName) {
        this.participantUniqueName = participantUniqueName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getTotatlTimeDuration() {
        return totatlTimeDuration;
    }

    public void setTotatlTimeDuration(int totatlTimeDuration) {
        this.totatlTimeDuration = totatlTimeDuration;
    }

}
