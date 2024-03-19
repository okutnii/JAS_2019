package com.example.multitool.notes;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.multitool.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NoteViewerActivity extends AppCompatActivity {

    private NoteRowView frame;
    private String data, date;
    private TextView tvDate, tvInfoNotes;
    private LinearLayout layout;
    private ArrayList<String> starred = new ArrayList<>();
    private ArrayList<String> notStarred = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_viewer);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        data = extras.getString("data");
        date = extras.getString("date");

        init();



    }
    private void init(){

        tvDate = findViewById(R.id.tv_date);
        tvInfoNotes = findViewById(R.id.tv_info_notes);
        layout = findViewById(R.id.linear_layout);

        // getting array with all notes
        Pattern pattern = Pattern.compile(";END;");
        String[] dataArr = pattern.split(data);

        // separate starred rows from not starred
        for(String t: dataArr){
            if(t.contains(";BOOL;true")){
                starred.add(t);
            }else {
                notStarred.add(t);
            }
        }

        tvDate.setText(date);
        tvInfoNotes.setText("Містить " + dataArr.length + " записів: " + starred.size() + " важливих");

        setRows(starred, true);
        setRows(notStarred, false);
    }

    private void setRows(ArrayList<String> data, boolean b){
        for(int i = 0; i < data.size(); i++){
            frame = new NoteRowView(getApplicationContext(), data.get(i), b);
            layout.addView(frame);
        }
    }
}
