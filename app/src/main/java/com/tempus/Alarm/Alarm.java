/*
 * Copyright (c) 2016. This app was made by Otavio Tarelho and Diego Nunes as requirement to get their major certificate. Any copy of this project will suffer legal penalties under Copyrights Laws.
 */

package com.tempus.Alarm;

import com.tempus.Events.Event;

import java.io.Serializable;

public class Alarm implements Serializable {
    private long ID;
    private String alarmName, alarmETA, alarmTime, ringtone, type;
    //private Set<String> repeat;
    private Event event;
    //private boolean snooze;
    private boolean active;

    public Alarm(){}

    public Alarm(String alarmName, String alarmETA, String alarmTime,String ringtone,/*
                 Set<String> repeat,*/ String type, /*boolean snooze,*/ boolean active, Event event) {
        this.alarmName = alarmName;
        this.alarmETA = alarmETA;
        this.alarmTime = alarmTime;
        this.active = active;
        this.event = event;
        this.ringtone = ringtone;
        //this.snooze = snooze;
        //this.repeat = repeat;
        this.type = type;
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

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    /*public void setRepeat(Set<String> repeat) {
        this.repeat = repeat;
    }*/

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

   /* public boolean isSnooze() {
        return snooze;
    }

    public void setSnooze(boolean snooze) {
        this.snooze = snooze;
    }*/

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /*public Set<String> getRepeat() {
        return repeat;
    }*/

}
