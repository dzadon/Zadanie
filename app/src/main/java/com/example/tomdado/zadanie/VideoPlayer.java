package com.example.tomdado.zadanie;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

public class VideoPlayer{

    private static SimpleExoPlayer player;

    public static SimpleExoPlayer getInstance(Context context) {
        if(player == null){
            Log.d("PLAYERINSTANCE","INSTANCE");
            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            player =  ExoPlayerFactory.newSimpleInstance(context, trackSelector);

        }
        return player;
    }



}
