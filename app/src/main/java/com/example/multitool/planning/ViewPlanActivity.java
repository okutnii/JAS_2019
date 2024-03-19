package com.example.multitool.planning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.multitool.R;
import com.example.multitool.data.Plan;

import java.util.regex.Pattern;

public class ViewPlanActivity extends AppCompatActivity {

    Context context;
    public static final String EXTRA_PLANID = "planId";
    ListView listView;
    TextView textDaysRemain, textName;
    Toolbar toolbar;
    ActionBar actionBar;
    Button editBtn;
    Plan target;

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_view);

        init();
    }

    private void init(){
        context = getApplicationContext();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        int planId = (int)getIntent().getExtras().get(EXTRA_PLANID);
        target = (Plan) Planning.ma.getItem(planId);

        textName = findViewById(R.id.text_name);
        ((AppCompatActivity)this).getSupportActionBar().setTitle(target.name);

        textDaysRemain = findViewById(R.id.text_days);

        //switchCompleted.setChecked(target.completed);
        textName.setText(" \"" + target.name + "\" визначено на ");
        textDaysRemain.setText(target.date);

        Pattern pattern = Pattern.compile(";POINT;");
        final String[] dataArr = pattern.split(target.fDescription);

        listView = findViewById(R.id.list_plan_items);

        PlanViewAdapter pa = new PlanViewAdapter(this.getLayoutInflater(), this, planId, dataArr);

        listView.setAdapter(pa);

        editBtn = findViewById(R.id.go_to_editor);
        editBtn.setOnClickListener(goToEditor);
    }

    View.OnClickListener goToEditor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), CreatePlanActivity.class);
            intent.putExtra("name", target.name);
            intent.putExtra("short", target.sDescription);
            intent.putExtra("days", target.days);
            intent.putExtra("data", target.fDescription)
                    .putExtra("completed", target.completed);
            startActivity(intent);
        }
    };


}
