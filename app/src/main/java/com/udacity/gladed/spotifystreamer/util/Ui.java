package com.udacity.gladed.spotifystreamer.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Ui {
    private static Toast mToast;

    public static void runOnUiThread(Context context, Runnable toRun) {
        new Handler(context.getMainLooper()).post(toRun);
    }

    public static Toast toast(Context context, String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
        return mToast;
    }
}
