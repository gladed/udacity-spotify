package com.udacity.gladed.spotifystreamer.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.udacity.gladed.spotifystreamer.util.Ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** A service that supplies an interface to the back-end streaming service */
public class MusicService extends Service {

    private static final String TAG = "SpotifyService";
    private static final String CountryCode = Locale.getDefault().getCountry();

    private final IBinder mBinder = new MusicServiceBinder();
    final SpotifyService mSpotify = new SpotifyApi().getService();
    List<FindArtistsListener> mFindArtistsListeners = new ArrayList<>();
    List<FindTracksListener>mFindTracksListeners = new ArrayList<>();

    public void addFindArtistsListener(FindArtistsListener listener) {
        mFindArtistsListeners.add(listener);
    }

    public void removeFindArtistsListener(FindArtistsListener listener) {
        mFindArtistsListeners.remove(listener);
    }

    public void addFindTracksListener(FindTracksListener listener) {
        mFindTracksListeners.add(listener);
    }

    public void removeFindTracksListener(FindTracksListener listener) {
        mFindTracksListeners.remove(listener);
    }

    /**
     * Given an artist, looks up the top tracks for that artist
     */
    public void findTopTracks(final Artist artist) {
        /** getArtistTopTrack requires a COUNTRY option so pull this from locale */
        Map<String, Object> options = new HashMap<String, Object>() {{
            put(SpotifyService.COUNTRY, CountryCode);
        }};

        // Note: Could add LRU cache for better performance
        mSpotify.getArtistTopTrack(artist.id, options, new Callback<Tracks>() {
            @Override
            public void success(final Tracks tracks, Response response) {
                Ui.runOnUiThread(MusicService.this, new Runnable() {
                    @Override
                    public void run() {
                        List<FindTracksListener> toNotify = new ArrayList<>(mFindTracksListeners);
                        for (FindTracksListener listener : toNotify) {
                            listener.onTracksFound(artist, tracks.tracks, null);
                        }

                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                Ui.runOnUiThread(MusicService.this, new Runnable() {
                    @Override
                    public void run() {
                        List<FindTracksListener> toNotify = new ArrayList<>(mFindTracksListeners);
                        for (FindTracksListener listener : toNotify) {
                            listener.onTracksFound(artist, null, error.toString());
                        }

                    }
                });
            }
        });
    }

    /**
     * Requests a search for a given search string, giving results to all ArtistSearchListeners.
     */
    public void findArtists(final String searchString) {
        // Note: Could add LRU cache
        // Note: Could optimize for many "findArtists" calls (e.g. don't overlap requests)
        mSpotify.searchArtists(searchString, new Callback<ArtistsPager>() {
            @Override
            public void success(final ArtistsPager artistsPager, Response response) {
                Ui.runOnUiThread(MusicService.this, new Runnable() {
                    public void run() {
                        List<FindArtistsListener> toNotify = new ArrayList<>(mFindArtistsListeners);
                        Log.i(TAG, "For search " + searchString + " found " + artistsPager.artists.items.size() + " artists");
                        for (FindArtistsListener listener : toNotify) {
                            listener.onArtistsFound(searchString, artistsPager.artists.items, null);
                        }
                    }
                });
            }

            @Override
            public void failure(final RetrofitError error) {
                Ui.runOnUiThread(MusicService.this, new Runnable() {
                    public void run() {
                        List<FindArtistsListener> toNotify = new ArrayList<>(mFindArtistsListeners);
                        for (FindArtistsListener listener : toNotify) {
                            listener.onArtistsFound(searchString, null, error.toString());
                        }
                    }
                });
            }
        });
    }

    public class MusicServiceBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** Can be used by clients to manage connection to this service */
    public static class Connection implements ServiceConnection {
        private MusicService mService;
        private Context mContext;
        private List<Runnable> mWhenConnected = new ArrayList<>();

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mService = ((MusicServiceBinder)iBinder).getService();

            // Run all outstanding requests
            while(mWhenConnected.size() > 0) {
                Ui.runOnUiThread(mContext, mWhenConnected.remove(0));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }

        /** Return the currently connected service */
        public MusicService getService() {
            return mService;
        }

        /** Initiate a binding to the service */
        public boolean bind(Context context) {
            mContext = context;
            return context.bindService(new Intent(context, MusicService.class),
                    this, Context.BIND_AUTO_CREATE);
        }

        /** Unbind from the service */
        public void unbind() {
            mContext.unbindService(this);
        }

        /** Run something as soon as a connection exists */
        public void whenConnected(Runnable runnable) {
            if (mService != null) {
                // Run now
                runnable.run();
            } else {
                // Run later, when a connection exists
                mWhenConnected.add(runnable);
            }
        }
    }

    public interface FindArtistsListener {
        void onArtistsFound(String search, List<Artist> artists, String error);
    }

    public interface FindTracksListener {
        void onTracksFound(Artist artist, List<Track> artists, String error);
    }

}
