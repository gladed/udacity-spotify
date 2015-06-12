package com.udacity.gladed.spotifystreamer.util;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class Ui {
    private static Toast mToast;

    /** Run later, on the UI thread */
    public static void runOnUiThread(Context context, Runnable toRun) {
        new Handler(context.getMainLooper()).post(toRun);
    }

    /** Show toast, cancelling the last toast if there was one */
    public static Toast toast(Context context, String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();
        return mToast;
    }
}
