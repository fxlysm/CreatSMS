package com.fxly.creatsms;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fxly.creatsms.adapter.Get;
import com.fxly.creatsms.adapter.ToastMessage;
import com.fxly.creatsms.language.StaticFunction;
import com.fxly.creatsms.net.ConnectionUtil;
import com.fxly.creatsms.update.VersionUpdate;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.fxly.creatsms.adapter.Get.SHAREDFILENAME;

public class MainUiActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,EasyPermissions.PermissionCallbacks{

    private MaterialEditText sms_body,sms_number;
    private FloatingActionButton fab;

    private static final int REQUEST_CODE_PERMISSIONS_DUO = 2;
    private  String TAG="Creat_Message";

    private String defaultSmsPkg;
    private String mySmsPkg;


    private final int PROGRESS_DIALOG = 1;

    private final int INCREASE = 0;

    private ProgressDialog progressDialog = null;
    private Handler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sms_body=(MaterialEditText)findViewById(R.id.sms_body);
        sms_number=(MaterialEditText)findViewById(R.id.sms_phone_number);


        setSupportActionBar(toolbar);

         fab = (FloatingActionButton) findViewById(R.id.creat_sms_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "you phone Number "+ sms_number.getText().toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(mySmsPkg.equals(Telephony.Sms.getDefaultSmsPackage(MainUiActivity.this))){
                    //        对短信数据库进行处理
                    String smsmember_string=sms_number.getText().toString();
                    String smsbody_string=sms_body.getText().toString();
                    if(sms_number.length()==0||sms_number.length()>15){
                        Snackbar.make(view, "Message number is error", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }else {

                        if(sms_body.length()==0){
                            Snackbar.make(view, "Message body is error", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }else {
                            Calendar now = Calendar.getInstance();
                            SharedPreferences mysharedpreferences = getSharedPreferences(SHAREDFILENAME, Activity.MODE_PRIVATE);
                            if(mysharedpreferences.getInt("year",1)>now.get(Calendar.YEAR)){
                                Snackbar.make(view, "设置的时间超过当前年份", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                                System.out.println(mysharedpreferences.getInt("year",1)+" "+now.get(Calendar.YEAR));
                            }else {
                                if(mysharedpreferences.getInt("month",1)>(now.get(Calendar.MONTH)+1)){
                                    Snackbar.make(view, "Sorry,设置的时间超过当前月份.", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    System.out.println(mysharedpreferences.getInt("month",1)+" "+now.get(Calendar.MONTH));
                                }else {
                                    if(mysharedpreferences.getInt("day",1)>now.get(Calendar.DATE)){
                                        Snackbar.make(view, "Sorry,设置的时间超过当前日期.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                        System.out.println(mysharedpreferences.getInt("day",1)+" "+now.get(Calendar.DATE));
                                    }else {
                                        showDialog(PROGRESS_DIALOG);
                                    }
                                }
                            }

                        }
                    }

                }else {
                    Toast.makeText(MainUiActivity.this,"Sorry,the App is not default Sms App.",
                            Toast.LENGTH_LONG).show();
                    Snackbar.make(view, "Sorry,the App is not default Sms App.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        defaultSmsPkg= Telephony.Sms.getDefaultSmsPackage(this);
        mySmsPkg= this.getPackageName();
        if(!defaultSmsPkg.equals(mySmsPkg)){
//            如果这个App不是默认的Sms App，则修改成默认的SMS APP
//            因为从Android 4.4开始，只有默认的SMS APP才能对SMS数据库进行处理
            Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,mySmsPkg);
            startActivity(intent);
        }



        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case INCREASE:
                        progressDialog.incrementProgressBy(1);
                        if(progressDialog.getProgress() >= 100) {
                            progressDialog.dismiss();
                        }
                        else if(progressDialog.getProgress()==100){

                            restore_default_app();
                            Dialog alertDialog = new AlertDialog.Builder(MainUiActivity.this).
                                    setTitle("Warning").
                                    setMessage("Added SMS successfully!\nPls try to reboot you phone").
                                    setIcon(android.R.drawable.stat_sys_warning).
                                    create();
                            alertDialog.show();
                            progressDialog.dismiss();
                        }
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }
    @Override
    public Dialog onCreateDialog(int id) {
        switch(id) {
            case PROGRESS_DIALOG:
                //this表示该对话框是针对当前Activity的
                progressDialog = new ProgressDialog(this);
                //设置最大值为100
////                EditText creat_sms_num =(EditText)findViewById(R.id.message_count);
//                String max_num=creat_sms_num.getText().toString();
//                int MAX=Integer.valueOf(max_num).intValue();
                SharedPreferences sharedPref  = getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
                int max=Integer.valueOf(sharedPref.getString("message_count","2")).intValue();
                progressDialog.setMax(max);
                //设置进度条风格STYLE_HORIZONTAL
                progressDialog.setProgressStyle(
                        ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle(R.string.adding_sms);
                break;
        }
        return progressDialog;
    }
    @Override
    public void onPrepareDialog(int id, Dialog dialog) {

        switch(id) {
            case PROGRESS_DIALOG:
                //将进度条清0
                progressDialog.incrementProgressBy(-progressDialog.getProgress());
                new Thread() {
                    public void run() {
                        //     message_count =(EditText)findViewById(R.id.message_count);
//                        String max_num=message_count.getText().toString();
//                        int MAX=Integer.valueOf(max_num).intValue();
                        SharedPreferences sharedPref  = getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
                        int max=Integer.valueOf(sharedPref.getString("message_count","2")).intValue();
                        for(int i=0; i<=max; i++) {

                            handler.sendEmptyMessage(INCREASE);
                            if(progressDialog.getProgress() >= max) {
                                progressDialog.dismiss();
                                break;
                            }
                            try {
                                Thread.sleep(50);
                                inset_message();

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                break;
        }
    }


    public void inset_message(){
//        SharedPreferences sharedPref  = getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
//        int max=Integer.valueOf(sharedPref.getString("message_count","2")).intValue();
//        for (int a=0;a<max;max++){
            ContentResolver resolver=getContentResolver();

            ContentValues values=new ContentValues();
            values.put(Telephony.Sms.ADDRESS, sms_number.getText().toString());
            values.put(Telephony.Sms.DATE, Get.gettime(MainUiActivity.this));
            long dateSent=System.currentTimeMillis()-(int)(Math.random() * 999999);
            values.put(Telephony.Sms.DATE_SENT,dateSent);
            values.put(Telephony.Sms.READ,Math.random());
            values.put(Telephony.Sms.SEEN,issenter());
            values.put(Telephony.Sms.STATUS, Telephony.Sms.STATUS_COMPLETE);
            values.put(Telephony.Sms.PERSON,"Lambert");
            values.put(Telephony.Sms.BODY, sms_body.getText().toString());
            values.put(Telephony.Sms.PERSON,"SIM1");
            values.put(Telephony.Sms.SERVICE_CENTER,"SIM1");
//        values.put(Telephony.Sms.CREATOR,"SIM1");

            values.put(Telephony.Sms.TYPE, Telephony.Sms.MESSAGE_TYPE_INBOX);

            Uri uri=resolver.insert(Telephony.Sms.CONTENT_URI,values);
            if(uri!=null){
                long uriId= ContentUris.parseId(uri);
                System.out.println("uriId "+uriId);
            }
//        ToastMessage.show(getApplicationContext(),String.valueOf(Get.gettime(MainUiActivity.this)));
            System.out.print("time"+Get.gettime(MainUiActivity.this)+" Current time"+System.currentTimeMillis());
//        }

    }

    boolean issent;
    public boolean issenter(){
        if(Math.random()==1){
            issent=true;
        }else {
            issent=false;
        }
        return  issent;
    }

    public void restore_default_app(){
        Intent intent=new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,defaultSmsPkg);
        startActivity(intent);
        System.out.println("Recover default SMS App");

//                    打印出收件箱里的最新5条短信
        Cursor cursor=getContentResolver().query(Telephony.Sms.CONTENT_URI,null,null,null,null);
        String msg="";
        while ((cursor.moveToNext()) &&
                (cursor.getPosition()<5)){
            int dateColumn=cursor.getColumnIndex("date");
            int phoneColumn=cursor.getColumnIndex("address");
            int smsColumn=cursor.getColumnIndex("body");

            System.out.println("count "+cursor.getCount()+" position "+cursor.getPosition());
//                        把从短信中获取的时间戳换成一定格式的时间
            SimpleDateFormat sfd=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date=new Date(Long.parseLong(cursor.getString(dateColumn)));
            String time=sfd.format(date);
            msg=msg+time+" "+cursor.getString(phoneColumn)+":"+cursor.getString(smsColumn)+"\n";
//                        mMessageView.setText(msg);
        }
    }


    public void inview(){
        sms_body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        sms_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(sms_number.getText().toString().length()>15||sms_number.getText().toString().length()==0){
                    ToastMessage.show(getApplicationContext(),"Input error");
                }

            }
        });

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            // Handle the camera action
            startActivity(new Intent().setClass(MainUiActivity.this,SetttingsActivity.class));
        }
        else if (id == R.id.language_switch) {
            startActivity(new Intent().setClass(MainUiActivity.this,LanguageSwitchActivity.class));
        }
//        else if (id == R.id.nav_slideshow) {
//
//        }
        else if (id == R.id.nav_feedback) {
            Uri uri = Uri.parse("market://details?id="+getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (id == R.id.nav_update) {
            //检查软件版本
            if(ConnectionUtil.isConn(getApplicationContext())){
                VersionUpdate.check_update(MainUiActivity.this);

            }else {
                ConnectionUtil.setNetworkMethod(MainUiActivity.this);
            }
        } else if (id == R.id.nav_about) {
            startActivity(new Intent().setClass(MainUiActivity.this,AboutActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        // Some permissions have been granted一些已授予的权限
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {


        // Some permissions have been denied一些被拒绝的权限
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_PERMISSIONS_DUO)
    private void requestPermissions(){
        String[] perms = {Manifest.permission.READ_SMS,  Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS
        };
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getResources().getString(R.string.apply_permission_message), REQUEST_CODE_PERMISSIONS_DUO, perms);
        }
    }


    @Override
    protected void onStart() {
        reloadLanguageAction();
        SharedPreferences sharedPref  = getSharedPreferences(SHAREDFILENAME, Activity.MODE_PRIVATE);

        sms_body.setText(sharedPref.getString("sms_body",sms_body.getText().toString()));
        sms_number.setText(sharedPref.getString("sms_number",sms_number.getText().toString()));
        requestPermissions();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences sharedPref  = getSharedPreferences(SHAREDFILENAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("sms_body",sms_body.getText().toString());
        editor.putString("sms_number",sms_number.getText().toString());
        editor.commit();
        super.onDestroy();
    }


    private void reloadLanguageAction() {
        Locale locale = StaticFunction.getSystemLacate(MainUiActivity.this);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, null);
        getBaseContext().getResources().flushLayoutCache();

    }
}
