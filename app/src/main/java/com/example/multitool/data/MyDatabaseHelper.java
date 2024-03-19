package com.example.multitool.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "sas";
    private final static int DB_VERSION = 1;
    public static  int idPoint = 0;

    public  MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("CREATE TABLE NOTES( " +

                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "DATE_OF_NOTE TEXT, " +
                "DATA_OF_NOTE TEXT," +
                "EDITABLE_NOTE TEXT " +
                "  );");

            db.execSQL("CREATE TABLE PLANNING(" +

                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "NAME TEXT, " +
                    "FULL_DESCRIPTION TEXT, " +
                    "SHORT_DESCRIPTION TEXT, " +
                    "DAYS INTEGER," +
                    "DATE TEXT, " +
                    "COUNT INTEGER, " +
                    "ALARMED BOOLEAN, " +
                    "COMPLETED BOOLEAN);");

            db.execSQL("CREATE TABLE "+ AlarmReminderContract.AlarmReminderEntry.TABLE_NAME +" (" +
                    AlarmReminderContract.AlarmReminderEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AlarmReminderContract.AlarmReminderEntry.KEY_HOURS + " INTEGER, " +
                    AlarmReminderContract.AlarmReminderEntry.KEY_MINUTES + " INTEGER, " +
                    AlarmReminderContract.AlarmReminderEntry.KEY_DAY + " TEXT, " +
                    AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE_VOICE + " BOOLEAN, " +
                    AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION + " TEXT, " +
                    " id_point TEXT "+ //WTF i`ve done?
                    ");");


    }

    public long insertPlan(String name, String fDescription, String sDescription, int days, String date, boolean completed){
        if (date == null || date.equals("")){
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, days);
            date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);
        }
        ContentValues content = new ContentValues();
        content.put("NAME", name);
        content.put("FULL_DESCRIPTION", fDescription);
        content.put("SHORT_DESCRIPTION", sDescription);
        content.put("DAYS", days);
        content.put("DATE", date);
        content.put("ALARMED", false);
        content.put("COMPLETED", completed);
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert("PLANNING", null, content);
        db.close();
        return id;
    }

    public void insertDayPoint(int hours, int minutes, String description, boolean voice, String dayOfWeek){
        ContentValues content = new ContentValues();
        content.put(AlarmReminderContract.AlarmReminderEntry.KEY_HOURS, hours);
        content.put(AlarmReminderContract.AlarmReminderEntry.KEY_MINUTES, minutes);
        content.put(AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION, description);
        content.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE_VOICE, voice);
        content.put(AlarmReminderContract.AlarmReminderEntry.KEY_DAY, dayOfWeek);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(AlarmReminderContract.AlarmReminderEntry.TABLE_NAME, null, content);
        db.close();
    }

    public void insertNote(String date, String data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("DATE_OF_NOTE", date);
        contentValues.put("DATA_OF_NOTE", data);
        contentValues.put("EDITABLE_NOTE", "TRUE");

        db.insert("NOTES", null, contentValues);
        db.close();

        Log.d("DBase", "Inserted!");
    }

    public void deletePlan(Plan plan){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("PLANNING","NAME = ? AND SHORT_DESCRIPTION = ?", new String[]{plan.name, plan.sDescription});
        db.close();
    }

    public void deletePoint(DayPoint p){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,"hours = ? AND minutes = ? AND day = ? AND description = ?", new String[]{""+p.hours, ""+p.minutes, p.dayOfWeek, p.description});
        db.close();
    }

    public void deleteNote(Note n){
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete("NOTES","DATE_OF_NOTE = ? AND DATA_OF_NOTE = ?", new String[]{n.date, n.data});
        db.close();
    }

    public void editPlan(Plan oldPlan, String name, String fDescription, String sDescription,
                         int days, boolean completed){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, days);
        String  date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put("NAME", name);
        content.put("FULL_DESCRIPTION", fDescription);
        content.put("SHORT_DESCRIPTION", sDescription);
        content.put("DAYS", days);
        content.put("DATE", date);
        content.put("COMPLETED", completed);
        db.update("PLANNING", content, "NAME = ? AND SHORT_DESCRIPTION = ?", new String[]{oldPlan.name, oldPlan.sDescription});
        db.close();
    }

    public void  editNote(Note oldNote, Note newNote, boolean insert){
        if(!insert) {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues content = new ContentValues();
            content.put("DATE_OF_NOTE", newNote.date);
            content.put("DATA_OF_NOTE", newNote.data);
            content.put("EDITABLE_NOTE", "TRUE");
            db.update("NOTES", content, "DATE_OF_NOTE = ? AND DATA_OF_NOTE = ?", new String[]{oldNote.date, oldNote.data});
            db.close();
        } else {
            SQLiteDatabase db = this.getReadableDatabase();
            ContentValues content = new ContentValues();
            content.put("DATE_OF_NOTE", newNote.date);
            content.put("DATA_OF_NOTE", (oldNote.data + newNote.data));
            content.put("EDITABLE_NOTE", "TRUE");
            db.update("NOTES", content, "DATE_OF_NOTE = ? AND DATA_OF_NOTE = ?", new String[]{oldNote.date, oldNote.data});
            db.close();
        }
    }

    public ArrayList<Plan> getAllPlans(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("PLANNING",
                new String[]{"NAME", "FULL_DESCRIPTION","SHORT_DESCRIPTION","DATE","DAYS","COMPLETED", "_id"},
                null, null,
                null, null,
                "COMPLETED, NAME");
        ArrayList<Plan> res = new ArrayList<>();
        if(cursor.moveToFirst()) {
            res.add(new Plan(cursor.getString(0), cursor.getString(1),
                    cursor.getString(2),cursor.getString(3), cursor.getInt(4),
                    1==cursor.getInt(5), cursor.getInt(6)));
            while (cursor.moveToNext())
                res.add(new Plan(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2),cursor.getString(3), cursor.getInt(4),
                        1==cursor.getInt(5), cursor.getInt(6)));
        }
        db.close();
        cursor.close();
        return res;
    }

    public ArrayList<DayPoint> getAllPointsOfDay(String day){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
                new String[]{AlarmReminderContract.AlarmReminderEntry.KEY_HOURS,
                        AlarmReminderContract.AlarmReminderEntry.KEY_MINUTES,
                        AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION,
                        AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE_VOICE,
                        AlarmReminderContract.AlarmReminderEntry.KEY_DAY,
                        AlarmReminderContract.AlarmReminderEntry._ID
                },
                "day = ?", new String[]{day.toString()},
                null, null,
                AlarmReminderContract.AlarmReminderEntry.KEY_HOURS +
                        " , " + AlarmReminderContract.AlarmReminderEntry.KEY_MINUTES);
        ArrayList<DayPoint> res = new ArrayList<>();
        if(cursor.moveToFirst()) {
            res.add(new DayPoint(cursor.getInt(0),cursor.getInt(1), cursor.getString(2),
                    (1==cursor.getInt(3)),cursor.getString(4), cursor.getInt(5)));
            while (cursor.moveToNext())
                res.add(new DayPoint(cursor.getInt(0),cursor.getInt(1), cursor.getString(2),
                        (1==cursor.getInt(3)),cursor.getString(4), cursor.getInt(5)));
        }
        db.close();
        cursor.close();
        return res;
    }

    public ArrayList<Note> getAllNotes(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("NOTES",
                new String[]{"DATE_OF_NOTE", "DATA_OF_NOTE"},
                null, null,
                null, null,
                "DATE_OF_NOTE");
        ArrayList<Note> res = new ArrayList<>();
        if(cursor.moveToFirst()) {
            res.add(new Note(cursor.getString(0), cursor.getString(1)));
            while (cursor.moveToNext())
                res.add(new Note(cursor.getString(0), cursor.getString(1)));
        }
        db.close();
        cursor.close();
        return res;
    }

    public ArrayList<String> getAllNoteDates(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("NOTES",
                new String[]{"DATE_OF_NOTE"},
                null, null,
                null, null,
                "DATE_OF_NOTE");
        ArrayList<String> temp = new ArrayList<>();
        if(cursor.moveToFirst()) {
            temp.add(cursor.getString(0));
            while (cursor.moveToNext())
                temp.add(cursor.getString(0));
        }
        db.close();
        cursor.close();
        return temp;
    }

    public Note getNote(int position){
        return getAllNotes().get(position);
    }

    public void setPlanCompleted(String name, String sDescription, boolean b){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put("COMPLETED", b);
        db.update("PLANNING", content, "NAME = ? AND SHORT_DESCRIPTION = ?", new String[]{name, sDescription});
        db.close();
    }

    public void onGetSwitchedVoice(DayPoint target, boolean b){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put(AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE_VOICE, b);
        db.update(AlarmReminderContract.AlarmReminderEntry.TABLE_NAME, content,
                "minutes = ? AND description = ? AND day = ?",
                new String[]{""+target.minutes, target.description, target.dayOfWeek});
        db.close();
    }

    public int isUniqueNote(Note note){

        ArrayList<String> dates = getAllNoteDates();
        for(int i = 0; i < dates.size(); i++){
            if (dates.get(i).equals(note.date))
                return i;
        }
        return -1;
    }

    public int isUniquePlan(Plan point){
        ArrayList<Plan> dates = getAllPlans();
        for(int i = 0; i < dates.size(); i++){
            if (dates.get(i).equals(point.name))
                return i;
        }
        return -1;
    }

    public void changeEditableNote(Note target, boolean b){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();

        content.put("EDITABLE_NOTE", b);
        db.update("NOTES", content,
                "DATE_OF_NOTE = ? AND DATA_OF_NOTE = ?",
                new String[]{target.date, target.data});
        db.close();
    }

    public  boolean getEditableOfNote(Note n){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("NOTES",
                new String[]{"DATE_OF_NOTE, EDITABLE_NOTE"},
                "DATE_OF_NOTE", new String[]{n.date},
                null, null,null);

        return "TRUE".equals(cursor.getString(1));
    }

    public Plan completePlanItem(Plan target, int index, boolean b){
        StringBuilder fDescription = new StringBuilder();
        String temp;

        Pattern pattern = Pattern.compile(";POINT;");
        String[] dataArr = pattern.split(target.fDescription);

        pattern = Pattern.compile(";BOOL;");
        String[] item = pattern.split(dataArr[index]);
        item[1] = "" + b;

        temp = item[0] + ";BOOL;" + item[1];

        for(int i = 0; i < dataArr.length; i++){
            if (i == index){
                fDescription.append(temp + ";POINT;");
            } else {
                fDescription.append(dataArr[i] + ";POINT;");
            }
        }


        target.fDescription = fDescription.toString();


        if(!target.fDescription.contains(";BOOL;false"))
            target.completed = true;
        else
            target.completed = false;

        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put("FULL_DESCRIPTION", fDescription.toString());
        content.put("COMPLETED", target.completed);
        db.update("PLANNING", content, "NAME = ? AND SHORT_DESCRIPTION = ?", new String[]{target.name, target.sDescription});
        db.close();

        return target;
    }


    public  int getPlanId(String name, String shortD){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("PLANNING",
                new String[]{"_id, NAME, SHORT_DESCRIPTION"}, "NAME = ? " +
                        "AND SHORT_DESCRIPTION = ?", new String[]{name, shortD},
                null, null,null);

        cursor.moveToFirst();
        int num = cursor.getInt(0);
        cursor.close();
        return num;
    }

    public boolean getAlarmed(String name, String shortD){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("PLANNING",
                new String[]{"ALARMED, NAME, SHORT_DESCRIPTION"}, "NAME = ? " +
                        "AND SHORT_DESCRIPTION = ?", new String[]{name, shortD},
                null, null,null);

        cursor.moveToFirst();
        boolean b = 1==cursor.getInt(0);
        cursor.close();
        return b;
    }

    public void setAlarmed(String name, String shortD, boolean b){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put("ALARMED", b);
        db.update("PLANNING", content, "NAME = ? AND SHORT_DESCRIPTION = ?", new String[]{name, shortD});
        db.close();
    }
    public int getPlanCount(String name, String shortD){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("PLANNING",
                new String[]{"COUNT, NAME, SHORT_DESCRIPTION"}, "NAME = ? " +
                        "AND SHORT_DESCRIPTION = ?", new String[]{name, shortD},
                null, null,null);

        cursor.moveToFirst();
        int num = cursor.getInt(0);
        cursor.close();
        return num;
    }

    public void setPlanCount(String name, String shortD, int count){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues content = new ContentValues();
        content.put("COUNT", count);
        db.update("PLANNING", content, "NAME = ? AND SHORT_DESCRIPTION = ?", new String[]{name, shortD});
        db.close();
    }

}
