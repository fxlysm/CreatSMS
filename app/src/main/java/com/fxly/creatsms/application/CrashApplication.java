package com.fxly.creatsms.application;

import android.app.Application;
import android.content.Intent;

import com.avos.avoscloud.AVOSCloud;
import com.fxly.creatsms.MainActivity;

/**
 * Created by Lambert Liu on 2016-09-26.
 */

public class CrashApplication   extends Application {
    protected static CrashApplication instance;
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this,"DGV14EWPg8JguMQFcxsd0z9B-gzGzoHsz","3PDI5U2U8uAvyyqrGJ9K9m0z");

        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程  以下用来捕获程序崩溃异常
    }
    // 创建服务用于捕获崩溃异常
    private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            restartApp();//发生崩溃异常时,重启应用
        }
    };
    public void restartApp(){
        Intent intent = new Intent(instance,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        instance.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
    }
}

