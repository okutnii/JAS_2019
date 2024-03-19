package com.example.multitool.notes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.data.Note;
import com.example.multitool.main.GenieActivity;
import com.example.multitool.main.MainActivity;
import com.example.multitool.planning.DatePickerFragment;
import com.example.multitool.planning.Planning;
import com.example.multitool.scheduler.ScheduleMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class NotesMainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, NavigationView.OnNavigationItemSelectedListener {

    protected NoteRow frame;
    private ArrayList<NoteRow> frameList;
    private static boolean fabsHidden = true;
    private LinearLayout linearLayout;
    private Button btnChangeDate;
    private FloatingActionButton fabMore, fabSave, fabAddRow;
    private TextView tvDate;
    private ArrayList<NoteRow> rowsArrayList = new ArrayList<>();
    private boolean edit = false;
    private String data, date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_notes);

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

        notifyUser();

        init();

        Bundle extras = getIntent().getExtras();
        if(extras != null){

            edit = extras.getBoolean("edit");
            data = extras.getString("data");
            date = extras.getString("date");

            Pattern pattern = Pattern.compile(";END;");
            String[] rows = pattern.split(data);

            for(String data: rows){
                createNoteRow(data);
            }
        } else
            createNoteRow(null);

    }

    private void init(){
        linearLayout = findViewById(R.id.linear_layout);
        tvDate = findViewById(R.id.tv_date);
        btnChangeDate = findViewById(R.id.btn_change_date);
        fabMore = findViewById(R.id.fab_more);
        fabAddRow = findViewById(R.id.fab_add_note_row);
        fabSave = findViewById(R.id.fab_save);
        btnChangeDate.setOnClickListener(onBtnChangeDateClick);
        fabAddRow.setOnClickListener(onFabAddRowClick);
        fabMore.setOnClickListener(onFabMoreClick);
        fabSave.setOnClickListener(onFabSaveClick);


        Calendar c = Calendar.getInstance();
        if(date != null){
            tvDate.setText(date);
        } else
            tvDate.setText(c.get(Calendar.YEAR)+ "/" + (1+c.get(Calendar.MONTH)) +  "/" + c.get(Calendar.DAY_OF_MONTH) );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toNoteListItem:
                Intent intent = new Intent(this, NoteList.class);
                startActivity(intent);
                return true;
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

    private final View.OnClickListener onFabMoreClick = new View.OnClickListener() {
        public void onClick(View view) {
            animFab();
        }
    };

    private void animFab(){
        FrameLayout.LayoutParams layoutParams1 = (FrameLayout.LayoutParams) fabAddRow.getLayoutParams();
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fabSave.getLayoutParams();

        Animation show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        Animation hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        Animation show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        Animation hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);


        if(fabsHidden){
            fabsHidden = false;

            layoutParams1.rightMargin += (int) (fabAddRow.getWidth() * 0.25);
            layoutParams1.bottomMargin += (int) (fabAddRow.getHeight() * 1.7);
            fabAddRow.setLayoutParams(layoutParams1);
            fabAddRow.startAnimation(show_fab_1);
            fabAddRow.setClickable(true);

            layoutParams2.rightMargin += (int) (fabSave.getWidth() * 1.7);
            layoutParams2.bottomMargin += (int) (fabSave.getHeight() * 0.25);
            fabSave.setLayoutParams(layoutParams2);
            fabSave.startAnimation(show_fab_2);
            fabSave.setClickable(true);


        } else {
            fabsHidden = true;

            layoutParams1.rightMargin -= (int) (fabAddRow.getWidth() * 0.25);
            layoutParams1.bottomMargin -= (int) (fabAddRow.getHeight() * 1.7);
            fabAddRow.setLayoutParams(layoutParams1);
            fabAddRow.startAnimation(hide_fab_1);
            fabAddRow.setClickable(false);

            layoutParams2.rightMargin -= (int) (fabSave.getWidth() * 1.7);
            layoutParams2.bottomMargin -= (int) (fabSave.getHeight() * 0.25);
            fabSave.setLayoutParams(layoutParams2);
            fabSave.startAnimation(hide_fab_2);
            fabSave.setClickable(false);
        }
    }

    private final View.OnClickListener onFabSaveClick= new View.OnClickListener() {
        public void onClick(View view) {
            save();

            Intent intent = new Intent(NotesMainActivity.this, NoteList.class);
            startActivity(intent);

            animFab();
        }
    };

    private final View.OnClickListener onFabAddRowClick  = new View.OnClickListener() {
        public void onClick(View view) {
            createNoteRow(null);
        }
    };

    private void createNoteRow(String data){
        frame = new NoteRow(getApplicationContext(), linearLayout, rowsArrayList, this, getSupportFragmentManager(), data);
        rowsArrayList.add(frame);
        linearLayout.addView(frame);
    }

    private final View.OnClickListener onBtnChangeDateClick = new View.OnClickListener() {
        public void onClick(View view) {
            DialogFragment dialogFragment = new DatePickerFragment();
            dialogFragment.show(getSupportFragmentManager(), "date picker");
        }
    };

    private void save(){
        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(NotesMainActivity.this);
        StringBuilder data = new StringBuilder();

        for (NoteRow t: rowsArrayList){
            data.append(t.getRowData());
        }
        Note temp = new Note(tvDate.getText().toString(), data.toString());
        int uniq = databaseHelper.isUniqueNote(temp);
        if(uniq < 0)
            databaseHelper.insertNote(temp.date, temp.data);
        else {
            Note existingNote = databaseHelper.getNote(uniq);
            databaseHelper.editNote(existingNote, temp, !edit);
        }
    }

    private void notifyUser(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        AlertDialog dialog = builder.setTitle("Описуйте Ваші емоції, враження та думки!")
                .setPositiveButton("Оу, єєс!)", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {

                    }
                }).create();

        // Make some UI changes for AlertDialog
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                // Add or create own background drawable for AlertDialog window
                Window view = ((AlertDialog)dialog).getWindow();
                view.setBackgroundDrawableResource(R.color.colorPrimaryDark);

                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
                positiveButton.invalidate();

            }
        });

        dialog.show();

    }

    public void createNoteDialog(final EditText etNoteLine){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Get the layout inflater
        LayoutInflater inflater = LayoutInflater.from(this);
        final View root = inflater.inflate(R.layout.dialog_note, null);

        final EditText etNote = root.findViewById(R.id.et_note);
        if(!etNoteLine.getText().toString().equals("")){
            etNote.setText(etNoteLine.getText().toString());
        }
        AlertDialog dialog = builder.setTitle("Опишіть ваш нотаток")
                .setView(root)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int i) {
                        etNoteLine.setText(etNote.getText().toString());
                    }
                })
                .setNegativeButton("Відмінити", new DialogInterface.OnClickListener() {
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


                // Customize POSITIVE, NEGATIVE and NEUTRAL buttons.
                Button positiveButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                positiveButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                positiveButton.setTypeface(Typeface.DEFAULT_BOLD);
                positiveButton.invalidate();

                Button negativeButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                negativeButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                negativeButton.setTypeface(Typeface.DEFAULT_BOLD);
                negativeButton.invalidate();

                Button neutralButton = ((AlertDialog)dialog).getButton(DialogInterface.BUTTON_NEUTRAL);
                neutralButton.setTextColor(getApplicationContext().getResources().getColor(R.color.colorPrimary));
                neutralButton.setTypeface(Typeface.DEFAULT_BOLD);
                neutralButton.invalidate();

            }
        });

        dialog.show();
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        tvDate.setText(year + "/" + ++month + "/" + dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        frame.updateTvTime(minute, hourOfDay);
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
            case R.id.nav_main:
                intent = new Intent(getApplicationContext(), MainActivity.class);
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

}
