package com.udacity.gladed.spotifystreamer.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.util.Json;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

public class PlayActivity extends AppCompatActivity {
    private static final String ARG_ARTIST = "artist";
    private static final String ARG_TRACK = "track";

    public static Intent makeIntent(Activity from, Artist artist, Track track) {
        return new Intent(from, PlayActivity.class)
                .putExtra(ARG_ARTIST, Json.gson.toJson(artist))
                .putExtra(ARG_TRACK, Json.gson.toJson(track));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // Grab the artist and track back out of the intent
        Artist artist = Json.gson.fromJson(getIntent().getExtras().getString(ARG_ARTIST), Artist.class);
        Track track = Json.gson.fromJson(getIntent().getExtras().getString(ARG_TRACK), Track.class);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(track.name);
            actionBar.setSubtitle(artist.name);
        }

        PlayFragment playFragment = (PlayFragment)getFragmentManager().findFragmentById(R.id.fragment);
        playFragment.setInfo(artist, track);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
