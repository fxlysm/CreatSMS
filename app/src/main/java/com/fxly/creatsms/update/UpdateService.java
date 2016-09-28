package com.fxly.creatsms.update;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.fxly.creatsms.MainUiActivity;
import com.fxly.creatsms.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UpdateService extends Service {
    private static final int NOTIF_ID=9527;
    private static final int MSG_UPDATE=0;
    private static final int MSG_FINISH=1;
    private static final int MSG_ERROR=2;
    private static final int CONNECTION_TIMEOUT=10000;
    private static final int READ_TIMEOUT=20000;
    private NotificationManager manager;
    private Notification notif;
    private File saveFile ;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE:
                    Log.d("download", "change progress");
                    int len = (Integer) msg.obj;
                    notif.contentView.setTextViewText(R.id.tvProgress, len + "%");
                    notif.contentView.setProgressBar(R.id.pbProgress, 100, len, false);
                    manager.notify(NOTIF_ID, notif);
                    break;
                case MSG_FINISH:
                    notif.contentView.setTextViewText(R.id.tvProgress, getString(R.string.download_complete_tip_to_install));
                    notif.contentView.setProgressBar(R.id.pbProgress, 100, 100, false);
                    Intent installIntent = new Intent(Intent.ACTION_VIEW);
                    installIntent.setDataAndType(Uri.fromFile(saveFile), "application/vnd.android.package-archive");
                    notif.contentIntent = PendingIntent.getActivity(UpdateService.this, 8880, installIntent,
                            PendingIntent.FLAG_ONE_SHOT);
                    notif.flags |= Notification.FLAG_AUTO_CANCEL;
                    manager.notify(NOTIF_ID, notif);
                    Toast.makeText(UpdateService.this, R.string.download_complete, Toast.LENGTH_SHORT).show();
                    stopSelf();
                    break;
                case MSG_ERROR:
                    String errorMsg=(String) msg.obj;
                    if(TextUtils.isEmpty(errorMsg)){
                        errorMsg=getString(R.string.error_bad_network);
                    }
                    errorMsg+=","+getString(R.string.download_faild);
                    notif.contentView.setTextViewText(R.id.tvProgress, getString(R.string.download_faild));
                    notif.flags |= Notification.FLAG_AUTO_CANCEL;
                    manager.notify(NOTIF_ID, notif);
                    Toast.makeText(UpdateService.this, errorMsg, Toast.LENGTH_SHORT).show();
                    stopSelf();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };


    @Override
    public void onCreate() {
        saveFile = new File(Environment.getExternalStorageDirectory(), getpkg()+".apk");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent activityIntent = new Intent(this, MainUiActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notif = new Notification();
        notif.icon = R.mipmap.message_icon;
        notif.tickerText = getString(R.string.app_name);
        // 通知栏显示所用到的布局文件
        notif.contentView = new RemoteViews(getPackageName(), R.layout.update_notification_item);
        notif.flags = Notification.FLAG_ONGOING_EVENT;
        notif.contentIntent = pIntent;
        manager.notify(NOTIF_ID, notif);
        String downLoadUrl=intent.getStringExtra("URL");
        if(TextUtils.isEmpty(downLoadUrl)){
            Message msg=handler.obtainMessage(MSG_ERROR, getString(R.string.error_url_empty));
            handler.sendMessage(msg);
        }else{
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                new DownLoadThread(downLoadUrl).start();
            }else{
                Message msg=handler.obtainMessage(MSG_ERROR, getString(R.string.error_sdcard_not_available));
                handler.sendMessage(msg);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    class DownLoadThread extends Thread {
        private final int NOTIFICATION_STEP=1;  //没隔百分之几更新任务栏一次
        int downloadPercent = 0;                //已下载的百分比
        long downloadedSize = 0;                //已下载文件大小
        int totalSize = 0;                      //下载文件总大小
        private String donwloadUrl="";

        public DownLoadThread(String donwloadUrl){
            this.donwloadUrl=donwloadUrl;
        }
        @Override
        public void run() {
            HttpURLConnection httpConnection = null;
            InputStream is = null;
            FileOutputStream fos = null;

            try {
                if (saveFile != null && !saveFile.exists()) {
                    saveFile.createNewFile();
                }
                if(saveFile==null||!saveFile.exists()){
                    throw new Exception(getString(R.string.error_create_faild));
                }
                URL url = new URL(donwloadUrl);
                httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestProperty("User-Agent", "PacificHttpClient");
                httpConnection.setConnectTimeout(CONNECTION_TIMEOUT);
                httpConnection.setReadTimeout(READ_TIMEOUT);
                totalSize = httpConnection.getContentLength();
                Log.d("download", "updateTotalSize:" + totalSize);
                if (httpConnection.getResponseCode() == 404) {
                    throw new Exception(getString(R.string.error_http_404));
                }
                is = httpConnection.getInputStream();
                fos = new FileOutputStream(saveFile, false);
                byte buffer[] = new byte[4096];
                int readsize = 0;
                while ((readsize = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, readsize);
                    downloadedSize += readsize;
                    Log.d("download", "已下载大小:" + downloadedSize + ",下载百分比:" + downloadPercent);
                    // 为了防止频繁的通知导致应用吃紧，百分比增加设定值才通知一次
                    if ((downloadPercent == 0) || (int) (downloadedSize * 100 / totalSize) - downloadPercent > NOTIFICATION_STEP) {
                        downloadPercent += NOTIFICATION_STEP;
                        Message msg = handler.obtainMessage(MSG_UPDATE, downloadPercent);
                        handler.sendMessage(msg);
                    }
                }
                handler.sendEmptyMessage(MSG_FINISH);
            } catch (MalformedURLException e) {
                handler.sendEmptyMessage(MSG_ERROR);
                e.printStackTrace();
            } catch (IOException e) {
                handler.sendEmptyMessage(MSG_ERROR);
                e.printStackTrace();
            } catch (Exception e) {
                String errorMsg=e.getMessage();
                Message msg=handler.obtainMessage(MSG_ERROR, errorMsg);
                handler.sendMessage(msg);
                e.printStackTrace();
            } finally {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.run();
        }

    }

    @Override
    public void onDestroy() {
        Log.d("download","service destroy");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }


    public String  getpkg(){
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> taskInfo = activityManager.getRunningTasks(1);

        ComponentName componentInfo = taskInfo.get(0).topActivity;

//        Log.d(tag, "当前应用:" + componentInfo.getPackageName());

        return componentInfo.getPackageName();
    }
}