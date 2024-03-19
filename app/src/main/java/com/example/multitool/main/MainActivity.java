package com.example.multitool.main;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.multitool.notes.NotesMainActivity;
import com.example.multitool.planning.Planning;
import com.example.multitool.R;
import com.example.multitool.scheduler.ScheduleMenu;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Button goToPlanning, goToSchedule, goToNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        goToPlanning = findViewById(R.id.go_to_planning);
        goToPlanning.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, Planning.class);
                        startActivity(intent);
            }
        });
        goToSchedule = findViewById(R.id.go_to_schedule);
        goToSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleMenu.class);
                startActivity(intent);
            }
        });

        goToNotes = findViewById(R.id.go_to_notes);
        goToNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NotesMainActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch(id){
            case R.id.nav_planning:
                intent = new Intent(getApplicationContext(), Planning.class);
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

    public static void alertAboutSlip(Context context, String message){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"lesha.kutny1@gmail.com"});
        email.putExtra(Intent.EXTRA_SUBJECT, "\"Gofer-app\"");
        email.putExtra(Intent.EXTRA_TEXT, message);

        email.setType("message/rfc822");

        context.startActivity(Intent.createChooser(email, "Оберіть email клієнт :"));
    }

}
