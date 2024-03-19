package com.example.multitool.planning;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.multitool.R;
import com.example.multitool.data.MyDatabaseHelper;
import com.example.multitool.data.Plan;

import java.util.ArrayList;
import java.util.Calendar;

import static com.example.multitool.planning.Planning.setPlanAlarm;

public class PlanListAdapter extends SimpleCursorAdapter {
    Context context;
    MyDatabaseHelper myDatabaseHelper;
    ArrayList<Plan> al;
    LayoutInflater li;
    Planning caller;

    public PlanListAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, LayoutInflater li, Planning caller) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        myDatabaseHelper = new MyDatabaseHelper(context);
        al = myDatabaseHelper.getAllPlans();
        this.li = li; this.caller = caller;

    }

    public void refreshAdapter(Context context) {
        myDatabaseHelper = new MyDatabaseHelper(context);
        al = myDatabaseHelper.getAllPlans();
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
        final Plan target = (Plan)getItem(position);

        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(context);
        databaseHelper.setPlanCount(target.name, target.sDescription, -1);
        int id = databaseHelper.getPlanId(target.name, target.sDescription);

        final ViewHolder vh ;
        if(convertView != null){
            vh = (ViewHolder)convertView.getTag();
        }else{
            convertView = li.inflate(R.layout.plan_line, null);
            vh = new ViewHolder();
            vh.iv =convertView.findViewById(R.id.iw);
            vh.tvName = convertView.findViewById(R.id.text_name);
            vh.tvShort = convertView.findViewById(R.id.text_short);
            vh.tvDate = convertView.findViewById(R.id.tv_days);

        }
        // setting texts
        vh.tvName.setText(target.name);
        vh.tvShort.setText(target.sDescription);
        vh.tvDate.setText(target.date);

        if(target.completed){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            setPlanAlarm(context,target.days, target.name, target.sDescription,id,false);
            databaseHelper.setPlanCount(target.name, target.sDescription, -1);
        }
        else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            Calendar calendar = Calendar.getInstance();
            Calendar now = Calendar.getInstance();

            String[] arr = target.date.split("/");

            if(arr.length == 3) {
                int day = Integer.parseInt(arr[0]);
                int month = Integer.parseInt(arr[1]);
                int year = Integer.parseInt(arr[2]);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                calendar.set(Calendar.MONTH, month-1);
                calendar.set(Calendar.YEAR, year);
            }

            long timestamp = calendar.getTimeInMillis() - now.getTimeInMillis();
            int days =(int)(timestamp / AlarmManager.INTERVAL_DAY);

            if(days >= 0)
                setPlanAlarm(context,days, target.name, target.sDescription, id,true);
            else
                convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }

        convertView.setTag(vh);
        // setting listeners
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewPlanActivity.class);
                intent.putExtra(ViewPlanActivity.EXTRA_PLANID, (int)position);
                caller.startActivity(intent);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });





        int count = 1 + position;

        TextDrawable drawable = new TextDrawable.Builder()
                .setColor(context.getResources()
                        .getColor(R.color.colorPrimaryDark))
                        .setShape(TextDrawable.SHAPE_ROUND).setText("" + count)
                .setTextColor(context.getResources()
                        .getColor(R.color.colorMainText)).build(); // radius in px

        vh.iv.setImageDrawable(drawable);
        return convertView;
    }
    class ViewHolder {
        TextView tvName; TextView tvShort; TextView tvDate; ImageView iv;
    }
}
