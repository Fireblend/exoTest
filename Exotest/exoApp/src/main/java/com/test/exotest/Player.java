package com.test.exotest;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

public class Player implements ExoPlayer.EventListener {
    private ExoPlayer exoPlayer;

    private MainActivity activity;

    public void playFile(Context context) {
        activity = (MainActivity) context;

        if(exoPlayer == null){
            //Initialize ExoPlayer
            exoPlayer = ExoPlayerFactory.newSimpleInstance(
                    context, new DefaultTrackSelector(null),
                    new DefaultLoadControl());
            exoPlayer.addListener(this);
        }

        prepareExoPlayerFromFileUri();
    }

    private void prepareExoPlayerFromFileUri(){
        //Get URI for raw resource
        Uri uri = RawResourceDataSource.buildRawResourceUri(R.raw.th_oial_32_float_48khz);

        DataSpec dataSpec = new DataSpec(uri);
        final RawResourceDataSource fileDataSource = new RawResourceDataSource(activity);
        try {
            fileDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };

        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        exoPlayer.setPlayWhenReady(true);
        exoPlayer.prepare(audioSource);
    }

    //-----------------------------------
    //      EventListener Callbacks
    //-----------------------------------

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        activity.showDebug("onTimelineChanged");
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        activity.showDebug("onTracksChanged");
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        activity.showDebug("onLoadingChanged");
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        activity.showDebug("onPlayerStateChanged: playWhenReady = "+String.valueOf(playWhenReady) +" playbackState = "+playbackState);
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        activity.showDebug("onPlaybackError: "+error.getCause().getMessage());
    }

    @Override
    public void onPositionDiscontinuity() {
        activity.showDebug("onPositionDiscontinuity");
    }
}
