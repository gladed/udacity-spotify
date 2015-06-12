package com.udacity.gladed.spotifystreamer.ui;

import android.content.ComponentName;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.service.MusicService;
import com.udacity.gladed.spotifystreamer.util.Ui;

import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class FindArtistActivity extends AppCompatActivity implements ArtistListFragment.OnArtistSelectedListener {

    private static final String TAG = "SearchActivity";
    private static final String KEY_SEARCH = "KEY_SEARCH";

    private final MusicService.Connection mMusicServiceConnection = new MusicServiceConnection();
    private SearchView mArtistSearch;
    private String mStoredQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_artist);
        mMusicServiceConnection.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMusicServiceConnection.unbind();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Store query string so it is not lost across reconfig
        outState.putString(KEY_SEARCH, mArtistSearch.getQuery().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStoredQuery = savedInstanceState.getString(KEY_SEARCH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mArtistSearch = (SearchView) menu.findItem(R.id.artistSearch).getActionView();
        mArtistSearch.setQueryHint(getString(R.string.artistSearchHint));
        mArtistSearch.setIconifiedByDefault(false);
        mArtistSearch.requestFocusFromTouch();

        // Recall the stored query text from before
        if (mStoredQuery != null) {
            mArtistSearch.setQuery(mStoredQuery, false);
        }

        mArtistSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(final String query) {
                // NOTE: Android may call this twice in rapid succession
                Log.i(TAG, "onQueryTextSubmit '" + query + "'");
                mMusicServiceConnection.whenConnected(new Runnable() {
                    @Override
                    public void run() {
                        mMusicServiceConnection.getService().findArtists(query);
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArtistSelected(Artist artist) {
        startActivity(SelectTrackActivity.makeIntent(this, artist));
    }

    class MusicServiceConnection extends MusicService.Connection {

    }
}
