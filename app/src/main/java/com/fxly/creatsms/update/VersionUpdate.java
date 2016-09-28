package com.fxly.creatsms.update;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.GetCallback;
import com.fxly.creatsms.R;
import com.fxly.creatsms.adapter.Get;

/**
 * Created by Lambert Liu on 2016-09-26.
 */

public class VersionUpdate {
    public Context context;

    public static Dialog dialog;
    public VersionUpdate(Context context){
        this.context = context;
    }
    public static int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 0;
        }
    }

    public final static void ShowDialog(Context context,String message){
        final Dialog alertDialog = new AlertDialog.Builder(context).
                setTitle(R.string.warning).
                setMessage(message).

                setIcon(R.drawable.ic_menu_warning).

                setNegativeButton(R.string.btn_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub


                    }
                }).
                setNegativeButton(R.string.menu_canncel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                alertDialog.dismiss();


                    }
                }).
                create();
        alertDialog.show();
    }


    public final static void check_update(final Context context){
//        AVOSCloud.initialize(context,"DGV14EWPg8JguMQFcxsd0z9B-gzGzoHsz","3PDI5U2U8uAvyyqrGJ9K9m0z");
        SharedPreferences sharedPreferences = context.getSharedPreferences(Get.SHAREDFILENAME, Activity.MODE_PRIVATE);
//        Locale locale = context.getResources().getConfiguration().locale;
        SharedPreferences shared = context.getSharedPreferences("setting_data", Activity.MODE_PRIVATE);;
        final String language = shared.getString("system_locale_languague_string","en");
        final String language_country = shared.getString("system_locale_country_string","en");
        final String[] system_Language = {""};
        // 第一参数是 className,第二个参数是 objectId
        AVObject todo = AVObject.createWithoutData("appifo", "57e8da59da2f600060dc7ec2");
        todo.fetchInBackground(new GetCallback<AVObject>() {


            @Override
            public void done(AVObject avObject, AVException e) {
                final     String title = avObject.getString("appname");// 读取 title
                final  String url=avObject.getString("updateUrl");
                if (language.endsWith("zh")){
                    if(language_country.endsWith("CN")){

                        system_Language[0] =avObject.getString("Description_zh_rCH");
                    }else {
                        system_Language[0] =avObject.getString("Description_zh_rTW");
                    }

                }else if(language.endsWith("en")){
                    system_Language[0] =avObject.getString("Description_eng");
                }else {
                    system_Language[0] =avObject.getString("Description_eng");
                }
                String appVerision = avObject.getString("appVerision");// 读取 content
                int verisionid=avObject.getInt("VersionCode");

                String description_zhrch=avObject.getString("Description_zh_rCH");
                System.out.println("appname:"+title+"\n"+"appVerision:"+appVerision+"\n"+"verisionid:"+verisionid+"\n"+"description_eng:"+ system_Language[0] );
                int lacalverision=getVersionCode(context);

                if(lacalverision<verisionid){
                    System.out.println(system_Language[0] );
                    new AlertDialog.Builder(context)
                            .setTitle(context.getResources().getString(R.string.update_new_verision)+" "+appVerision)
                            .setMessage(system_Language[0] )
                            .setPositiveButton(R.string.update_now,
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialoginterface, int i){
                                            //按钮事件
                                            Toast.makeText(context, R.string.update_now,Toast.LENGTH_LONG).show();

                                            Intent intent = new Intent(context,UpdateService.class);
                                            intent.putExtra("Key_App_Name",title);
                                            intent.putExtra("URL",url);
                                            context.startService(intent);
                                        }
                                    })
                            .setNegativeButton(R.string.menu_canncel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context, R.string.menu_canncel,Toast.LENGTH_LONG).show();

                                }
                            })
                            .show();


                }else {
                    System.out.println("未发现新版本");
                    Toast.makeText(context, "未发现新版本",Toast.LENGTH_LONG).show();
                }


            }
        });
    }
}
