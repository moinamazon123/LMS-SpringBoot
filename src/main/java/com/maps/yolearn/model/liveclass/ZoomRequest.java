package com.maps.yolearn.model.liveclass;

public class ZoomRequest {

    public String agenda;
    public boolean default_password;
    public int duration;
    public String password;
    public boolean pre_schedule;
    public Settings settings;
    public String audio;

    @Override
    public String toString() {
        return "ZoomRequest{" +
                "agenda='" + agenda + '\'' +
                ", default_password=" + default_password +
                ", duration=" + duration +
                ", password='" + password + '\'' +
                ", pre_schedule=" + pre_schedule +
                ", settings=" + settings +
                ", audio='" + audio + '\'' +
                ", auto_recording='" + auto_recording + '\'' +
                ", calendar_type=" + calendar_type +
                ", close_registration=" + close_registration +
                ", cn_meeting=" + cn_meeting +
                ", email_notification=" + email_notification +
                ", host_video=" + host_video +
                ", in_meeting=" + in_meeting +
                ", join_before_host=" + join_before_host +
                ", start_time='" + start_time + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }

    public String auto_recording;
    public int calendar_type;
    public boolean close_registration;
    public boolean cn_meeting;
    public boolean email_notification;
    public boolean host_video;
    public boolean in_meeting;
    public boolean join_before_host;
    public String start_time;
    public String topic;

    public String getAgenda() {
        return agenda;
    }

    public void setAgenda(String agenda) {
        this.agenda = agenda;
    }

    public boolean isDefault_password() {
        return default_password;
    }

    public void setDefault_password(boolean default_password) {
        this.default_password = default_password;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPre_schedule() {
        return pre_schedule;
    }

    public void setPre_schedule(boolean pre_schedule) {
        this.pre_schedule = pre_schedule;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getAuto_recording() {
        return auto_recording;
    }

    public void setAuto_recording(String auto_recording) {
        this.auto_recording = auto_recording;
    }

    public int getCalendar_type() {
        return calendar_type;
    }

    public void setCalendar_type(int calendar_type) {
        this.calendar_type = calendar_type;
    }

    public boolean isClose_registration() {
        return close_registration;
    }

    public void setClose_registration(boolean close_registration) {
        this.close_registration = close_registration;
    }

    public boolean isCn_meeting() {
        return cn_meeting;
    }

    public void setCn_meeting(boolean cn_meeting) {
        this.cn_meeting = cn_meeting;
    }

    public boolean isEmail_notification() {
        return email_notification;
    }

    public void setEmail_notification(boolean email_notification) {
        this.email_notification = email_notification;
    }

    public boolean isHost_video() {
        return host_video;
    }

    public void setHost_video(boolean host_video) {
        this.host_video = host_video;
    }

    public boolean isIn_meeting() {
        return in_meeting;
    }

    public void setIn_meeting(boolean in_meeting) {
        this.in_meeting = in_meeting;
    }

    public boolean isJoin_before_host() {
        return join_before_host;
    }

    public void setJoin_before_host(boolean join_before_host) {
        this.join_before_host = join_before_host;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
