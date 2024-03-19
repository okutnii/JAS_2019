package com.example.multitool.scheduler;

import android.app.AlarmManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.example.multitool.R;
import com.example.multitool.data.AlarmReminderContract;
import com.example.multitool.data.DayPoint;
import com.example.multitool.data.MyDatabaseHelper;

import java.util.ArrayList;

public class ScheduleListAdapter extends SimpleCursorAdapter {
    Context context;
    MyDatabaseHelper myDatabaseHelper;
    ArrayList<DayPoint> al;
    LayoutInflater li;
    int dayNo;
    static AlarmManager alarmManager;
    private View convertView;

    public ScheduleListAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
                               int flags, LayoutInflater li, String day, int dayNo, AlarmManager alarmManager) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(context);
        al = myDatabaseHelper.getAllPointsOfDay(day);
        this.li = li;
        this.dayNo = dayNo;
        this.alarmManager = alarmManager;
    }

    public void refreshAdapter(Context context, String day) {

        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(context);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.query(AlarmReminderContract.AlarmReminderEntry.TABLE_NAME,
                new String[]{AlarmReminderContract.AlarmReminderEntry._ID,
                        AlarmReminderContract.AlarmReminderEntry.KEY_HOURS,
                        AlarmReminderContract.AlarmReminderEntry.KEY_MINUTES,
                        AlarmReminderContract.AlarmReminderEntry.KEY_DESCRIPTION,
                        AlarmReminderContract.AlarmReminderEntry.KEY_ACTIVE_VOICE,
                        AlarmReminderContract.AlarmReminderEntry.KEY_DAY},
                null, null, null, null, "hours, minutes, description");
        al = myDatabaseHelper.getAllPointsOfDay(day);
        changeCursor(cursor);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int position) {
        return al.get(position);
    }

    public DayPoint getLastPoint(){
        return al.get(al.size()-1);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final DayPoint temp = (DayPoint) getItem(position);
        final ScheduleListAdapter.ViewHolder vh ;
        if(convertView != null){
            vh = (ScheduleListAdapter.ViewHolder)convertView.getTag();
        }else{
            convertView = li.inflate(R.layout.day_point_line, null);
            vh = new ScheduleListAdapter.ViewHolder();
            vh.tvTime = convertView.findViewById(R.id.tv_time_point);
            vh.tvDescr = convertView.findViewById(R.id.tv_point_description);
            vh.switchVoice = convertView.findViewById(R.id.switch_point_voice);
        }
        this.convertView = convertView;
        vh.switchVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper dbHelper = new MyDatabaseHelper(context);
                dbHelper.onGetSwitchedVoice(temp, !temp.voice);

                ScheduleMenu.setAlarm(context,temp, true, dayNo, vh.switchVoice.isChecked());

                // refresh
                refreshAdapter(context, temp.dayOfWeek);

            }
        });
        // setting texts
        String min = "" + temp.minutes;
        if (temp.minutes < 10)min = "0" + temp.minutes;
        vh.tvTime.setText(""+ temp.hours + " : " + min);
        vh.tvDescr.setText(temp.description);
        vh.switchVoice.setChecked(temp.voice);
        convertView.setTag(vh);
        // setting listeners
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        refreshAdapter(context, temp.dayOfWeek);
        return convertView;
    }
    class ViewHolder {
        TextView tvTime; TextView tvDescr; Switch switchVoice;
    }

}

