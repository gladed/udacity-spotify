package com.udacity.gladed.spotifystreamer.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.util.Json;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

public class SelectTrackActivity extends AppCompatActivity implements TrackListFragment.OnTrackSelectedListener {

    private static final String TAG = "SelectTrackActivity";
    private static final String ARG_ARTIST = "artist";

    private Artist mArtist;

    public static Intent makeIntent(Activity from, Artist artist) {
        return new Intent(from, SelectTrackActivity.class)
                .putExtra(ARG_ARTIST, Json.gson.toJson(artist));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate " + this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_track);

        mArtist = Json.gson.fromJson(getIntent().getExtras().getString(ARG_ARTIST), Artist.class);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mArtist.name);
            actionBar.setSubtitle("Top Tracks");
        }

        // Get the tracklist fragment and tell it about the selected artist
        TrackListFragment trackListFragment = (TrackListFragment)getFragmentManager().findFragmentById(R.id.trackListFragment);
        trackListFragment.setArtist(mArtist);
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
        startActivity(PlayActivity.makeIntent(this, mArtist, track));
    }

}
