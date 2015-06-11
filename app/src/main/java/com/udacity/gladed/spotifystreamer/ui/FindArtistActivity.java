package com.udacity.gladed.spotifystreamer.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.service.MusicService;

import kaaes.spotify.webapi.android.models.Artist;

public class FindArtistActivity extends AppCompatActivity implements ArtistListFragment.OnArtistSelectedListener {

    private static final String TAG = "SearchActivity";

    private final MusicService.Connection mMusicServiceConnection = new MusicService.Connection();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_artist);
        mMusicServiceConnection.bind(this);
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        mMusicServiceConnection.unbind();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        SearchView artistSearch = (SearchView) menu.findItem(R.id.artistSearch).getActionView();
        artistSearch.setQueryHint(getString(R.string.artistSearchHint));
        artistSearch.setIconifiedByDefault(false);
        artistSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO: Cache changes to query string (orientation changes)
                // TODO: Do something with controls when user is done searching?
                Log.i(TAG, "onQueryTextSubmit '" + query + "'");
                if (mMusicServiceConnection.getService() != null) {
                    mMusicServiceConnection.getService()
                            .findArtists(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange to '" + newText + "'");
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
        Log.i(TAG, "ARTIST SELECTED: " + artist.name);
        startActivity(SelectTrackActivity.makeIntent(this, artist));
    }
}
