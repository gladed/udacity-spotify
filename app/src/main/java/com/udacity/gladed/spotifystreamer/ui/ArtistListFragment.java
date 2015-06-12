package com.udacity.gladed.spotifystreamer.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.gladed.spotifystreamer.R;
import com.udacity.gladed.spotifystreamer.service.MusicService;
import com.udacity.gladed.spotifystreamer.util.Retainer;
import com.udacity.gladed.spotifystreamer.util.Ui;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Show a list of artists recently searched. Containing activity must implement OnArtistSelectedListener.
 */
public class ArtistListFragment extends Fragment implements ImageAdapter.ImageInfoSelectionListener<Artist> {
    private static final String TAG = "ArtistListFragment";

    /** Adapter managing the list of artists */
    ImageAdapter<Artist> mAdapter;

    /** Listening activity */
    private OnArtistSelectedListener mListener;

    MusicService.Connection mMusicServiceConnection = new MusicServiceConnection();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (mAdapter == null) {
            mAdapter = new ImageAdapter<>(getActivity().getApplicationContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup group = (ViewGroup)inflater.inflate(R.layout.fragment_image_list, container, false);

        RecyclerView searchResults = (RecyclerView) group.findViewById(R.id.items);
        searchResults.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchResults.setAdapter(mAdapter);
        mAdapter.setImageInfoSelectionListener(this);

        return group;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter.setImageInfoSelectionListener(null);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnArtistSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArtistSelectedListener");
        }
        mMusicServiceConnection.bind(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mMusicServiceConnection.unbind();
    }

    public interface OnArtistSelectedListener {
        void onArtistSelected(Artist artist);
    }

    @Override
    public void onImageInfoSelected(ImageAdapter.ImageInfo<Artist> selected) {
        if (mListener != null) {
            mListener.onArtistSelected(selected.getRoot());
        }
    }

    private class MusicServiceConnection extends MusicService.Connection implements MusicService.FindArtistsListener {
        @Override
        public void onArtistsFound(String search, List<Artist> artists, String error) {
            if (mAdapter == null) return;

            if (artists == null) {
                Ui.toast(getActivity(),
                        String.format(getActivity().getString(R.string.errorDuringSearch),error));
                return;
            }

            List<ImageAdapter.ImageInfo<Artist>> infos = new ArrayList<>();
            for (Artist artist : artists) {
                if (artist.images.size() > 1) {
                    // Normally 3 images are supplied; take the second (medium size) one
                    infos.add(new ImageAdapter.ImageInfo<>(
                            artist,
                            artist.name,
                            null,
                            artist.images.get(1).url));
                }
                // Skip artists that don't have photos.
            }

            if (artists.size() == 0) {
                Ui.toast(getActivity(), getActivity().getString(R.string.noMatchingArtist));
            }
            mAdapter.setImageInfos(infos);
        }

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            super.onServiceConnected(componentName, iBinder);
            getService().addFindArtistsListener(this);
        }

        @Override
        public void unbind() {
            getService().removeFindArtistsListener(this);
            super.unbind();
        }
    }
}
