package com.example.multitool.planning;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.data.Plan;

import java.util.regex.Pattern;

public class PlanViewAdapter extends BaseAdapter {


    class ViewHolder {
        TextView tv;
    }
    MyDatabaseHelper dbHelper;
    String[] data;
    LayoutInflater li;
    Context context;
    Plan target;
    int count = 1;

    PlanViewAdapter(LayoutInflater li, Context context, int plan, String[] data){
        this.data = data;
        this.li = li;
        this.context = context;
        dbHelper = new MyDatabaseHelper(context);
        target = (Plan)Planning.ma.getItem(plan);
    }
    ////////Overwritten
    public Object getItem(int index){ return data[index]; }
    public int getCount(){ return data.length; }
    public long getItemId(int i){ return 0; }

    ///////////////////

    public View getView(final int index, View v, ViewGroup vg){

        ViewHolder vh = null;
        if(v != null){
            vh = (ViewHolder)v.getTag();
        }else{
            v = li.inflate(R.layout.plan_line_view, null);
            vh = new ViewHolder();
            vh.tv = v.findViewById(R.id.data_to_view);
        }

        Pattern pattern = Pattern.compile(";BOOL;");
        final String[] item = pattern.split(data[index]);

        if(item[1].equals("true"))
            v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        else
            v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

        vh.tv.setText(count++ + ". " + item[0]);

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                boolean bool = item[1].equals("true");

                if(!bool)
                    view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
                else
                    view.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));


                target = dbHelper.completePlanItem(target, index, !bool);



               return false;
            }
        });


        v.setTag(vh);
        return v;
    }


}
