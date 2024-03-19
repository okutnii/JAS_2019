package com.example.multitool.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
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
import android.widget.ListView;

import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.main.GenieActivity;

public class NoteList extends AppCompatActivity {

    NoteListAdapter ma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView noteList = findViewById(R.id.note_list);

        SQLiteOpenHelper myDatabaseHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();

        Cursor cursor;
        cursor = db.query("NOTES",
                new String[]{"_id", "DATE_OF_NOTE", "DATA_OF_NOTE"},
                null, null, null, null, "DATE_OF_NOTE");
        ma = new NoteListAdapter(
                this, android.R.layout.simple_list_item_2,
                cursor, new String[]{"DATE_OF_NOTE", "DATA_OF_NOTE"},
                new int[]{android.R.id.text1, android.R.id.text2}, 0, getLayoutInflater());

        noteList.setAdapter(ma);
        noteList.setEmptyView(findViewById(R.id.empty_view));

        FloatingActionButton createNote = findViewById(R.id.button_create_note);
        createNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotesMainActivity.class));
            }
        });


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
