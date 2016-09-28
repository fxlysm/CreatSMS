package com.fxly.creatsms.adapter;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Lambert Liu on 2016-09-20.
 */

public final class ToastMessage {


    public static void show(Context context, String str) {
        Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
    }
}
