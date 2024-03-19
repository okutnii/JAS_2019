package com.example.multitool.notes;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.data.Note;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class NoteListAdapter extends SimpleCursorAdapter {

    Context context;
    MyDatabaseHelper myDatabaseHelper;
    ArrayList<Note> al;
    String date;
    String[] data;
    LayoutInflater li;
    boolean watchOnly;


    public NoteListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, LayoutInflater li) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(context);
        al = myDatabaseHelper.getAllNotes();
        this.li = li;
    }

    public void refreshAdapter(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        al = myDatabaseHelper.getAllNotes();
    }

    @Override
    public int getCount() {
        return al.size();
    }

    @Override
    public Object getItem(int position) {
        return al.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context);
        final Note temp = (Note)getItem(position);
        Pattern patEnd = Pattern.compile(";END;");
        date = temp.date;
        String s = temp.data;
        data = patEnd.split(s);


        final NoteListAdapter.ViewHolder vh ;
        if(convertView != null){
            vh = (NoteListAdapter.ViewHolder)convertView.getTag();
        }else{
            convertView = li.inflate(R.layout.note_point, null);
            vh = new NoteListAdapter.ViewHolder();
            vh.imageType = convertView.findViewById(R.id.image_type);
            vh.tvCount = convertView.findViewById(R.id.tv_count);
            vh.tvDateNote = convertView.findViewById(R.id.tv_date_note);
            vh.btnDeleteNote = convertView.findViewById(R.id.btn_delete_note);
        }


        vh.btnDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context);
                databaseHelper.deleteNote(temp);

                // refresh
                SQLiteDatabase db = myDatabaseHelper.getReadableDatabase();
                Cursor cursor;
                cursor = db.query("NOTES",
                        new String[]{"_id", "DATE_OF_NOTE", "DATA_OF_NOTE"},
                        null, null, null, null, "DATE_OF_NOTE");
                refreshAdapter(context);
                changeCursor(cursor);
                notifyDataSetChanged();
            }
        });
        vh.tvCount.setText("" + data.length);
        vh.tvDateNote.setText(date);
        vh.imageType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (watchOnly)
                    vh.imageType.setImageResource(R.drawable.ic_description);
                else
                    vh.imageType.setImageResource(R.drawable.ic_edit);

                watchOnly = !watchOnly;

                databaseHelper.changeEditableNote(temp, watchOnly);
            }
        });



        convertView.setTag(vh);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(watchOnly){
                    Intent intent = new Intent(context, NotesMainActivity.class);
                    intent.putExtra("edit", true);
                    intent.putExtra("date",temp.date);
                    intent.putExtra("data",temp.data);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, NoteViewerActivity.class);
                    intent.putExtra("date", temp.date);
                    intent.putExtra("data",temp.data);
                    context.startActivity(intent);

                }

            }
        });
        return convertView;
    }
    private class ViewHolder {
        ImageView imageType; TextView tvDateNote, tvCount; ImageButton btnDeleteNote;
    }
}