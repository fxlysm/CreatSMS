package com.fxly.creatsms.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Lambert Liu on 2016-09-01.
 */
public class SmsSendService extends Service {
    public SmsSendService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }
}
