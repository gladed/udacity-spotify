package com.udacity.gladed.spotifystreamer.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.udacity.gladed.spotifystreamer.R;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Shows the currently playing track information and allows the user
 * to control playback.
 */
public class PlayFragment extends Fragment {

    public PlayFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    public void setInfo(Artist artist, Track track) {
        TextView album = (TextView)getView().findViewById(R.id.album);
        album.setText(track.album.name);

        if (track.album.images.size() > 0) {
            ImageView cover = (ImageView) getView().findViewById(R.id.cover);
            Glide.with(this.getActivity())
                    .load(track.album.images.get(0).url)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .into(cover);
        }
    }
}
