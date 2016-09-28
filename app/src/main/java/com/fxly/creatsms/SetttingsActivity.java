package com.fxly.creatsms;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.annotation.SuppressLint;
import android.widget.TextView;
import android.widget.Toast;

import com.fxly.creatsms.adapter.Get;
import com.fxly.creatsms.adapter.ToastMessage;
import com.fxly.datetimepicker.SlideDateTimeListener;
import com.fxly.datetimepicker.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class SetttingsActivity extends AppCompatActivity {


    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM - dd - yyyy  hh:mm:aa");
    private SimpleDateFormat  mFormatter_year= new SimpleDateFormat("yyyy");
    private SimpleDateFormat  mFormatter_month= new SimpleDateFormat("MMMM");
    private SimpleDateFormat  mFormatter_day= new SimpleDateFormat("dd");
    private SimpleDateFormat  mFormatter_hour= new SimpleDateFormat("hh");
    private SimpleDateFormat  mFormatter_minute= new SimpleDateFormat("mm");
    private SimpleDateFormat  mFormatter_second= new SimpleDateFormat("aa");

    private TextView time_select,count_string;

    private LinearLayout select_time,setting_count;

    private AlertDialog myDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        select_time= (LinearLayout) findViewById(R.id.select_time);
        setting_count= (LinearLayout) findViewById(R.id.setting_count);


        count_string= (TextView) findViewById(R.id.count_string);
        time_select = (TextView) findViewById(R.id.time_select);


        select_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                        .setListener(listener)
                        .setInitialDate(new Date())
//                        .setMinDate(minDate)
//                        .setMaxDate(maxDate)
                        .setIs24HourTime(true)
//                        .setTheme(SlideDateTimePicker.HOLO_DARK)
//                        .setIndicatorColor(Color.parseColor("#990000"))
                        .build()
                        .show();
            }
        });


        setting_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new AlertDialog.Builder(SetttingsActivity.this).
//                        setTitle(R.string.message_count_pop).
//                        setIcon(android.R.drawable.ic_dialog_info)
//                        .setView(new EditText(SetttingsActivity.this))
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                ToastMessage.show(SetttingsActivity.this,"OK");
//                            }
//                        })
//                        .setNegativeButton("取消", null)
//                        .show();
                LayoutInflater factory = LayoutInflater.from(SetttingsActivity.this);
                final View textEntryView = factory.inflate(R.layout.dialog_message_count, null);
                AlertDialog dlg = new AlertDialog.Builder(SetttingsActivity.this)

//                        .setTitle("User Login")
                        .setView(textEntryView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                System.out.println("-------------->6");
                                SharedPreferences sharedPref  = getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
                                EditText count = (EditText) textEntryView.findViewById(R.id.message_count);
                                String inputPwd = count.getText().toString();
                                count.setText(sharedPref.getString("message_count","2"));
                                count_string.setText(inputPwd);
                                count_string.setText(String.format(getResources().getString(R.string.settings_select_count),  inputPwd));
//                                setTitle(inputPwd);

                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("message_count",inputPwd).commit();
                                System.out.println("-------------->1");
                                ToastMessage.show(SetttingsActivity.this,inputPwd);
//                                TextView tv=(TextView)findViewById(R.id.tv);
//                                tv.setText(inputPwd);
//输入的内容会在页面上显示来因为是做来测试，所以功能不是很全，只写了username没有学password

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                System.out.println("-------------->2");

                            }
                        })
                        .create();
                dlg.show();
            }
        });
    }


    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {

            String year=mFormatter_year.format(date);
            String month=mFormatter_month.format(date);
            String day=mFormatter_day.format(date);
            String hour=mFormatter_hour.format(date);
            String minute=mFormatter_minute.format(date);
//            String second=mFormatter_second.format(date);

            Toast.makeText(SetttingsActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
            time_select.setText(mFormatter.format(date));
            SharedPreferences sharedPref  = getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("year", Integer.valueOf(year).intValue());
            if(month.endsWith("一月")||month.endsWith("January")){editor.putInt("month",1);}
            else if(month.endsWith("二月")||month.endsWith("February")){editor.putInt("month",2);}
            else if(month.endsWith("三月")||month.endsWith("March")){editor.putInt("month",3);}
            else if(month.endsWith("四月")||month.endsWith("April")){editor.putInt("month",4);}
            else if(month.endsWith("五月")||month.endsWith("May")){editor.putInt("month",5);}
            else if(month.endsWith("六月")||month.endsWith("June")){editor.putInt("month",6);}
            else if(month.endsWith("七月")||month.endsWith("July")){editor.putInt("month",7);}
            else if(month.endsWith("八月")||month.endsWith("August")){editor.putInt("month",8);}
            else if(month.endsWith("九月")||month.endsWith("September")){editor.putInt("month",9);}
            else if(month.endsWith("十月")||month.endsWith("October")){editor.putInt("month",10);}
            else if(month.endsWith("十一月")||month.endsWith("November")){editor.putInt("month",11);}
            else if(month.endsWith("十二月")||month.endsWith("December")){editor.putInt("month",12);}
//            editor.putString("month",month);
            editor.putInt("day",Integer.valueOf(day).intValue());
            editor.putInt("hour",Integer.valueOf(hour).intValue());
            editor.putInt("minute",Integer.valueOf(minute).intValue());
//            editor.putInt("second",Integer.valueOf(second).intValue());
            editor.putString("time",mFormatter.format(date));
            editor.commit();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(SetttingsActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onStart() {
        SharedPreferences sharedPref  = getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
        time_select.setText(sharedPref.getString("time",getString(R.string.settings_select_time_detail)));
        count_string.setText(String.format(getResources().getString(R.string.settings_select_count),  sharedPref.getString("message_count","2")));

        super.onStart();
//        ToastMessage.show(getApplicationContext(),String.valueOf(sharedPref.getInt("month",1)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
