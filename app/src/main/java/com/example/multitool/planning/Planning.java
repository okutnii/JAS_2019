package com.example.multitool.planning;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.data.Plan;
import com.example.multitool.main.GenieActivity;
import com.example.multitool.main.MainActivity;
import com.example.multitool.notes.NotesMainActivity;
import com.example.multitool.receivers.PlanAlarmBroadcastReceiver;
import com.example.multitool.scheduler.ScheduleMenu;

public class Planning extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    public static PlanListAdapter ma;
    private static Plan selectedPlan;

    @Override
    protected void onResume() {
        refresh();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        ListView planList = findViewById(R.id.plan_list);
        FloatingActionButton createPlan = findViewById(R.id.create_plan);

        // CREATING PlanListAdapter ma
        {
            SQLiteOpenHelper myDatabaseHelper = new MyDatabaseHelper(this);
            SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
//
            Cursor cursor;
            cursor = db.query("PLANNING",
                    new String[]{"_id", "NAME", "FULL_DESCRIPTION", "SHORT_DESCRIPTION","DATE","DAYS", "COMPLETED"},
                    null, null, null, null, "COMPLETED, NAME");
            ma = new PlanListAdapter(
                    getApplicationContext(), android.R.layout.simple_list_item_2,
                    cursor, new String[]{"NAME", "SHORT_DESCRIPTION"},
                    new int[]{android.R.id.text1, android.R.id.text2}, 0, this.getLayoutInflater(), this);
        }
        // CONNECTING ADAPTER TO LIST
        planList.setAdapter(ma);
        planList.setEmptyView(findViewById(R.id.empty_view));
        planList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPlan = (Plan)ma.getItem(position);

                Intent intent = new Intent(getApplicationContext(), CreatePlanActivity.class);
                intent.putExtra("name", selectedPlan.name)
                        .putExtra("short", selectedPlan.sDescription)
                        .putExtra("days", selectedPlan.days)
                        .putExtra("data", selectedPlan.fDescription)
                        .putExtra("completed", selectedPlan.completed);
                startActivity(intent);
                return false;
            }
        });
        createPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlan();
            }
        });
    }

    public void addPlan() {

        Intent intent = new Intent(getApplicationContext(), CreatePlanActivity.class);
        startActivity(intent);
    }

    public void refresh() {
        ListView planList = findViewById(R.id.plan_list);
        PlanListAdapter adapterView = (PlanListAdapter) planList.getAdapter();
        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(Planning.this);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
        Cursor cursor;
        cursor = db.query("PLANNING",
                new String[]{"_id", "NAME", "FULL_DESCRIPTION", "SHORT_DESCRIPTION", "DAYS", "COMPLETED"},
                null, null, null, null, "COMPLETED, NAME");
        adapterView.refreshAdapter(Planning.this);
        adapterView.changeCursor(cursor);
        adapterView.notifyDataSetChanged();
    }

    public static void setPlanAlarm(Context context, int days, String name, String descr, long id, boolean bool){
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context);
        databaseHelper.setPlanCount(name, descr, 1);

        long timestamp = days * AlarmManager.INTERVAL_DAY;
        long oneThird = timestamp/3;

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, PlanAlarmBroadcastReceiver.class);

        intent.putExtra("name", name).putExtra("descr", descr).putExtra("id", id)
                .putExtra("sum", 3).putExtra("timestamp", oneThird);


        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, (int)(id), intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if(bool){
            if(databaseHelper.getAlarmed(name, descr))return;

            databaseHelper.setAlarmed(name, descr, true);

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +    oneThird,  pendingIntent);

            Log.d("INFO", "---------------------------------------------------------PlanAlarm is set");
        } else {
            if(!databaseHelper.getAlarmed(name, descr))return;
            databaseHelper.setAlarmed(name, descr, false);

            alarmManager.cancel(pendingIntent);
            Log.d("INFO", "---------------------------------------------------------План alarm is canceled");
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
            case R.id.nav_scheduler:
                intent = new Intent(getApplicationContext(), ScheduleMenu.class);
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