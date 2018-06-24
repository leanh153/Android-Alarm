package com.example.leanh.model;

import java.io.Serializable;

/**
 * this is Alarm class present for alarm object implements
 * Serializable to support in transfer object Alarm
 * through intent
 */
public class Alarm implements Serializable {

    private int id;           // alarm's id this was create at the adding time
    private int hour_x;         // alarm's hour
    private int minute_x;       // alarm's minute
    private String alarm_Name;  // alarm's name
    private int onOff;          // alarm's on off

    // first constructor is used at the import data from database
    public Alarm(int id, int hour_x, int minute_x, String alarm_Name, int onOff) {
        this.id = id;
        this.hour_x = hour_x;
        this.minute_x = minute_x;
        this.alarm_Name = alarm_Name;
        this.onOff = onOff;
    }

    // second constructor is used at the add or edit time in the initAlarm method
    public Alarm(int hour_x, int minute_x, String alarm_Name, int onOff) {
        this.hour_x = hour_x;
        this.minute_x = minute_x;
        this.alarm_Name = alarm_Name;
        this.onOff = onOff;
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour_x() {
        return hour_x;
    }

    public void setHour_x(int hour_x) {
        this.hour_x = hour_x;
    }

    public int getMinute_x() {
        return minute_x;
    }

    public void setMinute_x(int minute_x) {
        this.minute_x = minute_x;
    }

    public String getAlarm_Name() {
        return alarm_Name;
    }

    public void setAlarm_Name(String alarm_Name) {
        this.alarm_Name = alarm_Name;
    }

    public int getOnOff() {
        return onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
    }
}

