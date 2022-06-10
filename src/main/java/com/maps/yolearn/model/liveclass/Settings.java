package com.maps.yolearn.model.liveclass;

public class Settings {
    public boolean allow_multiple_devices;
    public String alternative_hosts;
    public boolean alternative_hosts_email_notification;
    public int approval_type;
    public boolean enable;
    public String method;

    public boolean isAllow_multiple_devices() {
        return allow_multiple_devices;
    }

    public void setAllow_multiple_devices(boolean allow_multiple_devices) {
        this.allow_multiple_devices = allow_multiple_devices;
    }

    public String getAlternative_hosts() {
        return alternative_hosts;
    }

    public void setAlternative_hosts(String alternative_hosts) {
        this.alternative_hosts = alternative_hosts;
    }

    public boolean isAlternative_hosts_email_notification() {
        return alternative_hosts_email_notification;
    }

    public void setAlternative_hosts_email_notification(boolean alternative_hosts_email_notification) {
        this.alternative_hosts_email_notification = alternative_hosts_email_notification;
    }

    public int getApproval_type() {
        return approval_type;
    }

    public void setApproval_type(int approval_type) {
        this.approval_type = approval_type;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
