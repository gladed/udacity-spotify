package com.udacity.gladed.spotifystreamer.ui;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Bundle;
import android.app.Fragment;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.service.MusicService;
import com.udacity.gladed.spotifystreamer.util.Ui;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;


/**
 * Displays and manages a list of tracks matching an artist
 */
public class TrackListFragment extends Fragment implements ImageAdapter.ImageInfoSelectionListener<Track> {
    private static final String TAG = "TrackListFragment";

    /** Selected artist, when specified */
    private Artist mArtist;

    /** Listening activity */
    private OnTrackSelectedListener mListener;

    /** Control showing list of tracks */
    ImageAdapter<Track> mAdapter;

    /** Connection to the music service */
    MusicServiceConnection mMusicServiceConnection = new MusicServiceConnection();

    public TrackListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // Create the adapter that will be used by later RecyclerViews
        if (mAdapter == null) {
            mAdapter = new ImageAdapter<>(getActivity().getApplicationContext());
        }
    }

    @Override
    public void onImageInfoSelected(ImageAdapter.ImageInfo<Track> selected) {
        if (mListener != null) {
            mListener.onTrackSelected(selected.getRoot());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_image_list, container, false);

        // Configure the RecyclerView with the adapter we created earlier
        RecyclerView searchResults = (RecyclerView) view.findViewById(R.id.items);
        searchResults.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchResults.setAdapter(mAdapter);
        mAdapter.setImageInfoSelectionListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "onDestroyView");
        super.onDestroyView();
        mAdapter.setImageInfoSelectionListener(null);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "onAttach");

        super.onAttach(activity);
        try {
            mListener = (OnTrackSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnTrackSelectedListener");
        }

        // Set up a connection to the music service
        mMusicServiceConnection.bind(activity);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        // Release listener and our connection to the music service
        mListener = null;
        mMusicServiceConnection.unbind();
    }

    /** Configure this fragment to display tracks for the specified artist */
    public void setArtist(Artist artist) {
        // If no change, ignore
        if (mArtist != null && mArtist.id.equals(artist.id)) return;
        mMusicServiceConnection.whenConnected(new Runnable() {
            @Override
            public void run() {
                if (mArtist != null) {
                    mMusicServiceConnection.getService().findTopTracks(mArtist);
                }
            }
        });
        mArtist = artist;
    }

    /** Connects to the music service and listens for updates to the list of tracks */
    class MusicServiceConnection extends MusicService.Connection implements MusicService.FindTracksListener {

        @Override
        public void onTracksFound(Artist artist, List<Track> tracks, String error) {
            Log.i(TAG, "For " + artist.name + " found " + tracks.size() + " tracks");
            if (mAdapter == null) return;

            if (tracks == null) {
                Ui.toast(getActivity(), "Could not show tracks for artist: " + error);
                return;
            }

            List<ImageAdapter.ImageInfo<Track>> infos = new ArrayList<>();
            for (Track track: tracks) {
                if (track.album.images.size() > 1) {
                    infos.add(new ImageAdapter.ImageInfo<>(
                            track,
                            track.name,
                            track.album.name,
                            track.album.images.get(1).url));
                }
            }
            mAdapter.setImageInfos(infos);
            // TODO: If no matches, show different view?
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            super.onServiceConnected(componentName, iBinder);
            getService().addFindTracksListener(this);
        }

        @Override
        public void unbind() {
            super.unbind();
            getService().removeFindTracksListener(this);
        }
    }

    /** Activity containing this fragment must implement this interface */
    public interface OnTrackSelectedListener {
        void onTrackSelected(Track track);
    }
}
