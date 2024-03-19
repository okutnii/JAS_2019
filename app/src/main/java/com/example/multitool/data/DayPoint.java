package com.example.multitool.data;

import android.app.PendingIntent;

public class DayPoint {
    public int idPoint;
    public int hours, minutes;
    public boolean voice;
    public String description, dayOfWeek;

    public DayPoint(int hours, int minutes, String description, boolean voice, String dayOfWeek, int idPoint) {
        this.hours = hours; this.minutes = minutes; this.voice = voice;
        this.description = description; this.dayOfWeek = dayOfWeek;
        this.idPoint = idPoint;
    }

}
