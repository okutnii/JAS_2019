package com.example.multitool.planning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.multitool.R;

import java.util.regex.Pattern;

public class PlanRow extends RelativeLayout {

    private EditText editText;
    private LinearLayout layout;
    private ImageButton buttonClose;
    private int count;
    private String data;

    public PlanRow(Context context, LinearLayout linearLayout, int i, String data) {
        super(context);

        layout = linearLayout;
        count = i;


        if (data != null){
            this.data = data;
        }

        init();
    }


    private void init(){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.plan_point, this);

        buttonClose = findViewById(R.id.btn_close);
        buttonClose.setOnClickListener(onBtnCloseClick);
        editText = findViewById(R.id.edit);
        if(data == null)
            editText.setHint("Опишіть цей пункт плану");
        else {
            Pattern pattern = Pattern.compile(";BOOL;");
            String[] rows = pattern.split(data);

            editText.setText(rows[0]);
        }
    }

    private final OnClickListener onBtnCloseClick = new OnClickListener() {
        public void onClick(View view) { layout.removeView(getPlanRow());
        }
    };

    public String getRowData(){

        String data = "";

        data += editText.getText().toString() + ";BOOL;false" + ";POINT;";

        return data;
    }

    private PlanRow getPlanRow(){
        return this;
    }

}
