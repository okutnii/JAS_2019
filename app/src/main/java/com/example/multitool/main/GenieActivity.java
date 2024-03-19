package com.example.multitool.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import com.example.multitool.R;
import com.example.multitool.notes.NotesMainActivity;
import com.example.multitool.planning.Planning;
import com.example.multitool.scheduler.ScheduleMenu;

import java.util.regex.Pattern;

public class GenieActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.genie_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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

        Bundle extras = getIntent().getExtras();
        String extraT = extras.getString("query");

        Pattern pattern = Pattern.compile(" ");
        String []data = pattern.split(extraT);

        StringBuilder res = new StringBuilder();
        for(int i = 0; i < data.length; i++){
            if(i == 0) res.append(data[i]);
            else res.append("+").append(data[i]);
        }

        web=(WebView)findViewById(R.id.web_view);
        web.getSettings().setLoadsImagesAutomatically(true);
        web.loadUrl("http://www.google.com/search?q="+res);
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
            case R.id.nav_main:
                intent = new Intent(getApplicationContext(), MainActivity.class);
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


}
