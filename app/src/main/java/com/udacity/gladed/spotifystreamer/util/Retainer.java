package com.udacity.gladed.spotifystreamer.util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows objects to be retained between activity reconfiguration
 */
public class Retainer extends Fragment {

    private static final String TAG = "Retainer";

    /** The current application context */
    private static Context sContext;

    /** Defines how to create an arbitrary object */
    public interface Factory<T> {
        T create(Context context);
    }
    /** Defines how to create an arbitrary object */
    public interface NamedFactory<T> extends Factory<T> {
        String getName();
    }

    /** Get or create the retainer object */
    public static Retainer obtain(Activity activity) {
        sContext = activity.getApplicationContext();
        FragmentManager fm = activity.getFragmentManager();
        Retainer retainer = (Retainer) fm.findFragmentByTag(TAG);
        if (retainer == null) {
            retainer = new Retainer();
            fm.beginTransaction()
                    .add(retainer, TAG)
                    .commit();
            // See http://stackoverflow.com/questions/11884025/cant-find-fragment-by-tag
            fm.executePendingTransactions();
        }
        return retainer;
    }

    // Map of objects to retain
    private Map<String, Object> mData = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    /** Set data to be retained */
    public <T> void set(String tag, T data) {
        mData.put(tag, data);
    }

    /** Retrieve retained data from a tag */
    public <T> T get(String tag) {
        return (T)mData.get(tag);
    }

    /** Retrieve or create data for the tag. */
    public <T> T get(String tag, Factory<T> factory) {
        T value = (T)mData.get(tag);
        if (value == null) {
            value = factory.create(sContext);
            set(tag, value);
        }
        return value;
    }

    public <T> T get(NamedFactory<T> factory) {
        return get(factory.getName(), factory);
    }
}