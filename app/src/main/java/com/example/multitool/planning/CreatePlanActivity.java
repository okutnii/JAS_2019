package com.example.multitool.planning;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.data.Plan;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

import static com.example.multitool.planning.Planning.setPlanAlarm;

public class CreatePlanActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private FloatingActionButton fabAddRow, fabSave, fabMore;
    private EditText editName, editShortDescr;
    private LinearLayout layout;

    private Planning parent;

    private ArrayList<PlanRow> arrayList;
    private PlanRow frame;

    private static boolean fabsHidden = true;
    private static int count = 1;
    private int days, planId = 0;
    private boolean completed, recovery = false;
    private String name, shortD, fullD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();

    }

    private void init(){
        layout = findViewById(R.id.linear);

        parent = new Planning();

        editName = findViewById(R.id.edit_name);
        editShortDescr = findViewById(R.id.edit_short_descript);

        fabMore = findViewById(R.id.fab_more);
        fabAddRow = findViewById(R.id.fab_add_point_row);
        fabSave = findViewById(R.id.fab_save);
        fabAddRow.setOnClickListener(onFabAddRowClick);
        fabMore.setOnClickListener(onFabMoreClick);
        fabSave.setOnClickListener(onFabSaveClick);

        arrayList = new ArrayList<>();


        Bundle extras = getIntent().getExtras();
        if(extras != null){
            recovery = true;

            name = extras.getString("name");
            days = extras.getInt("days");
            shortD = extras.getString("short");
            fullD = extras.getString("data");
            completed = extras.getBoolean("completed");

            editName.setText(name);
            editShortDescr.setText(shortD);
            resetRows(fullD);
        } else
            createPointRow(null);

    }

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

    private final View.OnClickListener onFabAddRowClick  = new View.OnClickListener() {
        public void onClick(View view) { createPointRow(null); }
    };



    private final View.OnClickListener onFabMoreClick = new View.OnClickListener() {
        public void onClick(View view) {
            animFab();
        }
    };

    private final View.OnClickListener onFabSaveClick = new View.OnClickListener() {
        public void onClick(View view) {
            callDatePicker();


            animFab();
        }
    };

    private void callDatePicker(){
        DialogFragment dialogFragment = new DatePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "date picker");
    }

    private void save() {
        if(!recovery) {
            MyDatabaseHelper databaseHelper = new MyDatabaseHelper(CreatePlanActivity.this);
            StringBuilder fullDescr = new StringBuilder();

            long id;

            for (PlanRow t : arrayList) {
                fullDescr.append(t.getRowData());
            }

            String name = editName.getText().toString();
            String shortDescr = editShortDescr.getText().toString();
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, days);
            String date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);


            databaseHelper.insertPlan(name, fullDescr.toString(), shortDescr, days, date, false);

            id = databaseHelper.getPlanId(name, shortDescr);

            Intent intent = new Intent(getApplicationContext(), Planning.class);
            setPlanAlarm(getApplicationContext(), days, name, shortDescr, id, true);

            startActivity(intent);
        } else
        {
            MyDatabaseHelper databaseHelper = new MyDatabaseHelper(CreatePlanActivity.this);
            StringBuilder fullDescr = new StringBuilder();



            for (PlanRow t : arrayList) {
                fullDescr.append(t.getRowData());
            }
            String editedName = editName.getText().toString();
            String editedShortDescr = editShortDescr.getText().toString();

            long id = databaseHelper.getPlanId(name, shortD);

            Calendar c = Calendar.getInstance();
            c.add(Calendar.DAY_OF_MONTH, days);
            String date = "" + c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR);

            Plan temp = new Plan(name, fullD, shortD, date, days, false, id);

            databaseHelper.editPlan(temp, editedName, fullDescr.toString(), editedShortDescr, days, false);
            setPlanAlarm(getApplicationContext(), days, name, shortD, id, true);

            Intent intent = new Intent(getApplicationContext(), Planning.class);
            startActivity(intent);
        }

    }

    private void createPointRow(String data) {
        frame = new PlanRow(getApplicationContext(), layout, count++, data);
        arrayList.add(frame);
        layout.addView(frame);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(recovery)
            getMenuInflater().inflate(R.menu.menu_plan_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete_plan:
                MyDatabaseHelper db = new MyDatabaseHelper(this);

                planId = db.getPlanId(name, shortD);

                Plan selectedPlan = new Plan(name, fullD, shortD, null, days,completed, planId);

                setPlanAlarm(getApplicationContext(),days, name, shortD,
                        planId,false);
                db.deletePlan(selectedPlan);

                startActivity(new Intent(getApplicationContext(), Planning.class));

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar now = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        long daysInMillis = 0;
        date.set(year, month, dayOfMonth);
        if(date.compareTo(now) > 0){
           daysInMillis = date.getTimeInMillis() - now.getTimeInMillis();
           days = (int)(daysInMillis / 86400000);
           save();
        } else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this );
            AlertDialog dialog = builder.setTitle("Оберіть дату, яка ще не настала!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface d, int i) {
                            DialogFragment dialogFragment = new DatePickerFragment();
                            dialogFragment.show(getSupportFragmentManager(), "date picker");
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

    }

    private void resetRows(String data) {

        Pattern pattern = Pattern.compile(";POINT;");
        String[] rows = pattern.split(data);

        for(String s: rows)
            createPointRow(s);
    }
}
