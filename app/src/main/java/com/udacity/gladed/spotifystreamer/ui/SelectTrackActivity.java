package com.udacity.gladed.spotifystreamer.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.service.MusicService;
import com.udacity.gladed.spotifystreamer.util.Json;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

public class SelectTrackActivity extends AppCompatActivity implements TrackListFragment.OnTrackSelectedListener {

    private static final String TAG = "SelectTrackActivity";
    private static final String ARG_ARTIST = "artist";

    public static Intent makeIntent(Activity from, Artist artist) {
        return new Intent(from, SelectTrackActivity.class)
                .putExtra(ARG_ARTIST, Json.gson.toJson(artist));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_track);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Grab the artist out of the intent
        Artist artist = Json.gson.fromJson(getIntent().getExtras().getString(ARG_ARTIST), Artist.class);

        // Get the tracklist fragment and tell it about the selected artist
        TrackListFragment trackListFragment = (TrackListFragment)getFragmentManager().findFragmentById(R.id.trackListFragment);
        trackListFragment.setArtist(artist);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onTrackSelected(Track track) {
        Log.i(TAG, "A track was selected: " + track.name);
    }

}
