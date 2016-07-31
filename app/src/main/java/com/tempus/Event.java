package com.tempus;

/**
 * Created by otaviotarelho on 7/31/16.
 */
public class Event {
    private String name, location, time_start, time_end, day_start, day_end;
    private int id;

    public Event(String name, String location, String time_start, String time_end, String day_start, String day_end, int id) {
        this.name = name;
        this.location = location;
        this.time_start = time_start;
        this.time_end = time_end;
        this.day_start = day_start;
        this.day_end = day_end;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getDay_start() {
        return day_start;
    }

    public void setDay_start(String day_start) {
        this.day_start = day_start;
    }

    public String getDay_end() {
        return day_end;
    }

    public void setDay_end(String day_end) {
        this.day_end = day_end;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
