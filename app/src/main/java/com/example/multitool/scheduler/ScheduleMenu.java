package com.example.multitool.scheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.multitool.R;
import com.example.multitool.data.AlarmReminderContract;
import com.example.multitool.data.DayPoint;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.main.GenieActivity;
import com.example.multitool.main.MainActivity;
import com.example.multitool.notes.NotesMainActivity;
import com.example.multitool.planning.Planning;
import com.example.multitool.receivers.AlarmBroadcastReceiver;

import java.util.Calendar;
import java.util.regex.Pattern;

public class ScheduleMenu extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,
        NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton sundayBtn,mondayBtn, tuesdayBtn,wednesdayBtn,thursdayBtn,fridayBtn,saturdayBtn;
    public ScheduleListAdapter ma;
    public static String day = "SUNDAY";
    public static int dayNo = 1;
    public static int id;
    static int h = 0;int m = 0;
    TextView tvTimePicker;

    private boolean[] thisDay;

    private static final long milDay =   86400000L;
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        thisDay = new boolean[7];

        // set drawer
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        LayoutInflater inflater = LayoutInflater.from(this);
        final View root = inflater.inflate(R.layout.activity_add_point, null);
        tvTimePicker = root.findViewById(R.id.tv_time_picker);
        sundayBtn = findViewById(R.id.sunday_btn);mondayBtn =findViewById(R.id.monday_btn);tuesdayBtn =findViewById(R.id.tuesday_btn);
        wednesdayBtn =findViewById(R.id.wednesday_btn);thursdayBtn =findViewById(R.id.thursday_btn);fridayBtn =findViewById(R.id.friday_btn);
        saturdayBtn =findViewById(R.id.saturday_btn);

        ListView planList = findViewById(R.id.schedule_list);
        FloatingActionButton createPoint = findViewById(R.id.button_create_point);

        initDay();

        //setting listeners on day buttons
        sundayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "SUNDAY";dayNo = 1; refresh();
                setCheckedDay(1);
            }
        });
        mondayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "MONDAY"; dayNo = 2; refresh();
                setCheckedDay(2);
            }
        });
        tuesdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "TUESDAY"; dayNo = 3; refresh();
                setCheckedDay(3);
            }
        });
        wednesdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "WEDNESDAY"; dayNo = 4; refresh();
                setCheckedDay(4);
            }
        });
        thursdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "THURSDAY"; dayNo = 5; refresh();
                setCheckedDay(5);
            }
        });
        fridayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "FRIDAY"; dayNo = 6; refresh();
                setCheckedDay(6);
            }
        });
        saturdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day = "SATURDAY"; dayNo = 7; refresh();
                setCheckedDay(7);
            }
        });

        // CREATING ScheduleListAdapter ma
        SQLiteOpenHelper myDatabaseHelper = new MyDatabaseHelper(this);
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
        ma = new ScheduleListAdapter(
                getApplicationContext(), android.R.layout.simple_list_item_2,
                cursor, new String[]{"day", "description"},
                new int[]{android.R.id.text1, android.R.id.text2}, 0, this.getLayoutInflater(),
                day, dayNo, (AlarmManager)getSystemService(Context.ALARM_SERVICE));

        // CONNECTING ADAPTER TO LIST
        planList.setAdapter(ma);
        planList.setEmptyView(findViewById(R.id.empty_view));
        planList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                callPointInfoDialog(position, id);
                return false;
            }
        });

        createPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPointDialog();
            }
        });

        db.close();

    }

    private boolean setCheckedDay(int dayNo){
        resetCheckedDay();
        switch (dayNo){
            case 1: sundayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark))); break;
            case 2: mondayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));break;
            case 3: tuesdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));break;
            case 4: wednesdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));break;
            case 5: thursdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));break;
            case 6: fridayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark))); break;
            case 7: saturdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));break;
        }
        return true;
    }

    private boolean resetCheckedDay(){
        sundayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        mondayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        tuesdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        wednesdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        thursdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        fridayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        saturdayBtn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        return true;
    }


    private void callPointInfoDialog(int position, long id){
        final DayPoint selectedItem = (DayPoint) ma.getItem(position);
        final MyDatabaseHelper databaseHelper = new MyDatabaseHelper(ScheduleMenu.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this );
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        final View root = inflater.inflate(R.layout.activity_info_point, null);

        //initializing of rows
        final TextView tvTextInfo = root.findViewById(R.id.tv_time_info);
        final TextView tvDescribeInfo = root.findViewById(R.id.tv_describe_info);
        final Switch infoSwitchVoice = root.findViewById(R.id.info_switch_voice);

        String temp = selectedItem.hours + ":";
        if (selectedItem.minutes < 10) temp += "0" + selectedItem.minutes;
        else temp += selectedItem.minutes;
        tvTextInfo.setText(temp);
        tvDescribeInfo.setText(selectedItem.description);
        infoSwitchVoice.setChecked(selectedItem.voice);

        infoSwitchVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //updating database
                databaseHelper.onGetSwitchedVoice(selectedItem, infoSwitchVoice.isChecked());

                refresh();
            }
        });
        AlertDialog dialog = builder.setTitle("Інфо")
                .setView(root)
                .setPositiveButton("Видалити", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        //canceling alarm
                        setAlarm(getApplicationContext(), selectedItem, false, dayNo, false);

                        //deleting item
                        databaseHelper.deletePoint(selectedItem);
                        databaseHelper.close();

                        refresh();
                    }
                })
                .setNeutralButton("Повернутися", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //handling operation: setting or canceling alarm
                        setAlarm(getApplicationContext(), selectedItem, true, dayNo, infoSwitchVoice.isChecked());
                        databaseHelper.close();
                        refresh();
                    }
                }).create();

        // Make some UI changes for AlertDialog
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                // Add or create own background drawable for AlertDialog window
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.colorPrimaryDark);

               // ((AlertDialog)dialog).ge



                // Customize POSITIVE, NEGATIVE and NEUTRAL buttons.
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMainText));
                positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
                positiveButton.invalidate();

                Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMainText));
                negativeButton.setTypeface(Typeface.DEFAULT_BOLD);
                negativeButton.invalidate();

                Button neutralButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                neutralButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMainText));
                neutralButton.setTypeface(Typeface.DEFAULT_BOLD);
                neutralButton.invalidate();

            }
        });

        dialog.show();

    }


    private void addPointDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this );
        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        final View root = inflater.inflate(R.layout.activity_add_point, null);

        //initializing of rows

        this.tvTimePicker = root.findViewById(R.id.tv_time_picker);
        final ImageButton createTimePickerBtn = root.findViewById(R.id.create_time_picker_btn);
        final Switch dialogSwitchVoice = root.findViewById(R.id.dialog_switch_voice);
        final EditText editPointDescription = root.findViewById(R.id.edit_point_description);
        createTimePickerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new TimePickerFragment();
                dialogFragment.show(getSupportFragmentManager(), "time picker");
            }
        });


        Calendar c = Calendar.getInstance();
        String min;
        if(c.get(Calendar.MINUTE) < 10){
            min = "0" + c.get(Calendar.MINUTE);
        } else min = "" + c.get(Calendar.MINUTE);
        this.tvTimePicker.setText(""+ c.get(Calendar.HOUR_OF_DAY) + ":" + min);

        AlertDialog dialog = builder.setTitle("Додати Пункт Дня")
                .setView(root)
                .setPositiveButton("Додати", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {

                        MyDatabaseHelper dbHelper = new MyDatabaseHelper(ScheduleMenu.this);

                        Pattern pattern =Pattern.compile(":");
                        String[] time = pattern.split(tvTimePicker.getText());
                        int hours = Integer.parseInt(time[0].trim());
                        int minutes = Integer.parseInt(time[1].trim());

                        dbHelper.insertDayPoint(hours, minutes, editPointDescription.getText().toString(),dialogSwitchVoice.isChecked() , day);

                        refresh();

                        DayPoint dp = ma.getLastPoint();

                        dp.hours = hours; dp.minutes = minutes;

                        setAlarm(getApplicationContext(), dp, true, dayNo, dialogSwitchVoice.isChecked());
                    }
                })
                .setNeutralButton("Відмінити", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();


        // Make some UI changes for AlertDialog
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                // Add or create own background drawable for AlertDialog window
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.colorPrimaryDark);

                // ((AlertDialog)dialog).ge



                // Customize POSITIVE, NEGATIVE and NEUTRAL buttons.
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMainText));
                positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
                positiveButton.invalidate();

                Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMainText));
                negativeButton.setTypeface(Typeface.DEFAULT_BOLD);
                negativeButton.invalidate();

                Button neutralButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                neutralButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorMainText));
                neutralButton.setTypeface(Typeface.DEFAULT_BOLD);
                neutralButton.invalidate();

            }
        });

        dialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        //setting values
        h = hourOfDay; m = minute;

        //setting text in TimeText view
        String min = "" + m;
        if (m < 10)min = "0" + m;
        String temp = "" + h + " : " + min;
        tvTimePicker.setText(temp);
    }

    public void refresh() {
        ListView planList = findViewById(R.id.schedule_list);
        ScheduleListAdapter adapterView = (ScheduleListAdapter) planList.getAdapter();
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(ScheduleMenu.this);
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
        adapterView.refreshAdapter(ScheduleMenu.this, day);
        adapterView.changeCursor(cursor);
        adapterView.notifyDataSetChanged();
        db.close();
    }

    public static void setAlarm(Context context, DayPoint dp, boolean active, int dayNo, boolean voice){

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra("description", dp.description)
                .putExtra("id", dp.idPoint).putExtra("voice", voice);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, dp.idPoint, intent,
                0);

        long timeStamp = 0;
        Calendar c = Calendar.getInstance();

        long dayTimestamp = 0;
        if (c.get(Calendar.DAY_OF_WEEK) > dayNo){
            dayTimestamp = (c.get(Calendar.DAY_OF_WEEK) - dayNo) * milDay;
        } else if(c.get(Calendar.DAY_OF_WEEK) < dayNo) {
            dayTimestamp = (7 -  dayNo + c.get(Calendar.DAY_OF_WEEK)) * milDay;
        } else  if((dp.hours*60 + dp.minutes) <= (c.get(Calendar.HOUR_OF_DAY)*60 + (c.get(Calendar.MINUTE)))) {
            dayTimestamp = 7*milDay;
        }
        timeStamp += dayTimestamp + ((dp.hours*milHour + dp.minutes*milMinute) - (c.get(Calendar.MINUTE)*milMinute + c.get(Calendar.HOUR_OF_DAY)*milHour));

        if(active){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+   timeStamp,  pendingIntent);

            Log.d("------->>> Timestamp ", ""+dp.hours + " " + dp.minutes + " // " + dayNo + " = " +  day+ " (" + dp.description+ ")");
            Log.d("------->>> Timestamp ", ""+ timeStamp+  " (" + dp.description+ ")");
        } else {
            alarmManager.cancel(pendingIntent);

            Log.d("------->>> Canceled ",  "#" + dp.idPoint +" ("+dp.description + ") ");
        }

    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch(id){
            case R.id.nav_main:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                break;
            case R.id.nav_planning:
                intent = new Intent(getApplicationContext(), Planning.class);
                break;
            case R.id.nav_notes:
                intent = new Intent(getApplicationContext(), NotesMainActivity.class);
                break;
            case  R.id.nav_options:
                MainActivity.alertAboutSlip(this, "");
                break;

        }

        if(intent != null) startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private String initDay(){
        Calendar c = Calendar.getInstance();
        setCheckedDay(c.get(Calendar.DAY_OF_WEEK));
        switch (c.get(Calendar.DAY_OF_WEEK)){
            case 1: day = "SUNDAY"; dayNo =1; break;
            case 2: day = "MONDAY"; dayNo =2; break;
            case 3: day = "TUESDAY"; dayNo =3; break;
            case 4: day = "WEDNESDAY"; dayNo =4; break;
            case 5: day = "THURSDAY"; dayNo =5; break;
            case 6: day = "FRIDAY"; dayNo =6; break;
            case 7: day = "SATURDAY"; dayNo =7; break;
        }
        return day;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.genie:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = LayoutInflater.from(this);
                final View root = inflater.inflate(R.layout.genie, null);

                final EditText editText = root.findViewById(R.id.question);



                Button button = root.findViewById(R.id.came_to_genie);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), GenieActivity.class);
                        intent.putExtra("query", editText.getText().toString());
                        startActivity(intent);
                    }
                });
                builder .setView(root).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
