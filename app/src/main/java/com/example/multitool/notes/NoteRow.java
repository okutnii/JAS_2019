package com.example.multitool.notes;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.multitool.R;
import com.example.multitool.scheduler.TimePickerFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Pattern;

public class NoteRow extends RelativeLayout {

    private Context context;
    String dataRows[];
    private ArrayList<NoteRow> rowsArrayList;
    private FragmentManager fragmentManager;
    private NotesMainActivity caller;
    private LinearLayout linearLayout;
    private TextView tvTime;
    private EditText etNoteLine;
    private ImageButton btnStar, btnClose;
    private boolean isStarred = false;

    public NoteRow(Context context, LinearLayout linearLayout, ArrayList<NoteRow> rowsArrayList,
                   NotesMainActivity caller, FragmentManager fragmentManager,
                   String data){
        super(context);
        this.context = context; this.linearLayout = linearLayout; this.caller = caller;
        this.fragmentManager = fragmentManager; this.rowsArrayList = rowsArrayList;
        initComponents();
        restoreRow(data);
    }

    private void restoreRow(String data) {
        if(data != null) {
            Pattern pattern = Pattern.compile(";TEXT;");
            String[] arr1 = pattern.split(data);
            tvTime.setText(arr1[0]);

            pattern = Pattern.compile(";BOOL;");
            String[] arr2 = pattern.split(arr1[1]);
            etNoteLine.setText(arr2[0]);

            if (!Boolean.parseBoolean(arr2[1])) {
                btnStar.setImageResource(R.drawable.ic_star_border);
            } else {
                btnStar.setImageResource(R.drawable.ic_star);
            }
        }
    }


    private void initComponents(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.note_row, this);
        //  linearLayout = findViewById(R.id.linearLayout);
        tvTime = findViewById(R.id.tv_time);
        etNoteLine = findViewById(R.id.et_add_line);
        btnStar = findViewById(R.id.btn_star);
        btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(onBtnCloseClick);
        btnStar.setOnClickListener(onBtnStarClick);
        etNoteLine.setOnClickListener(onClickEditText);
        tvTime.setOnClickListener(onTvTimeClick);

        Calendar c = Calendar.getInstance();
        updateTvTime(c.get(Calendar.MINUTE), c.get(Calendar.HOUR_OF_DAY));
    }

    private final OnClickListener onClickEditText = new OnClickListener() {
        public void onClick(View view) {
            caller.createNoteDialog(etNoteLine);
        }
    };

    private final OnClickListener onBtnCloseClick = new OnClickListener() {
        public void onClick(View view) {
            linearLayout.removeView(getNoteRow());
            removeItem();
        }
    };

    private final OnClickListener onBtnStarClick = new OnClickListener() {
        public void onClick(View view) {
            isStarred = !isStarred;
            if (!isStarred){
                btnStar.setImageResource(R.drawable.ic_star_border);
            } else {
                btnStar.setImageResource(R.drawable.ic_star);
            }
        }
    };

    private void removeItem(){
        rowsArrayList.remove(this);
    }

    private NoteRow getNoteRow(){
        return this;
    }

    public String getRowData(){

        String data = "";

        data += tvTime.getText().toString().trim() + ";TEXT;";
        data += etNoteLine.getText().toString().trim() + ";BOOL;";
        data += isStarred + ";END;";

        return data;
    }

    public void updateTvTime(int mins, int hours){
        String m,h;
        if (mins < 10){
            m = "0" + mins;
        } else m = "" + mins;
        h = "" + hours;


        tvTime.setText(h + ":" + m + " ");

        // update db
    }


    private void defineFrame(){
        caller.frame = this;
    }


    private final OnClickListener onTvTimeClick = new OnClickListener() {
        public void onClick(View view) {
            defineFrame();
            DialogFragment dialogFragment = new TimePickerFragment();
            dialogFragment.show(fragmentManager, "time picker");
        }
    };




}
