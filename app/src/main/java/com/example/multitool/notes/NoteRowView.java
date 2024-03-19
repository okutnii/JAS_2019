package com.example.multitool.notes;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.multitool.R;

import java.util.regex.Pattern;

public class NoteRowView extends RelativeLayout {
    private String data;
    private boolean starred;

    public NoteRowView(Context context, String data, boolean starred) {
        super(context);
        this.starred = starred;
        initComponents(data);
    }

    private void initComponents(String data) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.note_view_row, this);

        TextView tvTime = findViewById(R.id.timeToView);
        TextView tvData = findViewById(R.id.data_to_view);
        ImageView imageView = findViewById(R.id.imageStarred);

        if(starred)
            imageView.setImageResource(R.drawable.ic_star);


        Pattern pattern = Pattern.compile(";TEXT;");
        String[] arr1 = pattern.split(data);
        tvTime.setText(arr1[0]);

        pattern = Pattern.compile(";BOOL;");
        String[] arr2 = pattern.split(arr1[1]);
        tvData.setText(arr2[0]);



    }


}
