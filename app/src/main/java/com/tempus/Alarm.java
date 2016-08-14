/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus;

import java.io.Serializable;

public class Alarm implements Serializable {
    private long ID;
    private String alarmName, alarmETA, alarmTime;
    private boolean active;

    public Alarm(String alarmName, String alarmETA, String alarmTime, boolean active) {
        this.alarmName = alarmName;
        this.alarmETA = alarmETA;
        this.alarmTime = alarmTime;
        this.active = active;
    }

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public String getAlarmETA() {
        return alarmETA;
    }

    public void setAlarmETA(String alarmETA) {
        this.alarmETA = alarmETA;
    }

    public String getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(String alarmTime) {
        this.alarmTime = alarmTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
