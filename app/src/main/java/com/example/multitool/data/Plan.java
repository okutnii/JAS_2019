package com.example.multitool.data;

import android.util.Log;

import java.util.Calendar;
import java.util.regex.Pattern;

public class Plan {
    public String name,fDescription, sDescription;
    public boolean completed;
    public int days;
    public long planId;
    public String date;


    public Plan(String name, String fDescription,
                String sDescription, String date, int days, boolean completed, long planId){
        this.name = name;
        this.fDescription = fDescription;
        this.sDescription = sDescription;
        this.days = days;
        this.completed = completed;
        this.planId = planId;

        if(date == null || date.equals("")) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, days);
            date = "" + c.get(Calendar.DAY_OF_MONTH)
                    + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        }
        else this.date = date;
    }


    public String toString(){
        return this.name + " : " + this.sDescription;
    }

}
