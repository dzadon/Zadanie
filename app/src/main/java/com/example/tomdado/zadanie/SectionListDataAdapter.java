package com.example.tomdado.zadanie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;


public class SectionListDataAdapter extends RecyclerView.Adapter<SectionListDataAdapter.SingleItemRowHolder>{

    private ArrayList<SingleItemModel> itemModels;
    private Context mContext;
    private static final int TYPE_ONE = 1;
    private static final int TYPE_TWO = 2;
    private View viewSingleCard;
    private View viewSingleCardProfile;
    private SimpleExoPlayer player;

    public SectionListDataAdapter(ArrayList<SingleItemModel> itemModels, Context mContext) {
        this.itemModels = itemModels;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        viewSingleCard =LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card, parent, false);
        viewSingleCardProfile = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card_profile, parent, false);
        if (viewType == TYPE_ONE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card_profile, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_single_card, parent, false);
        }
        return new SingleItemRowHolder(view);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull SingleItemRowHolder holder, int position) {
        SingleItemModel itemModel = itemModels.get(position);
        if (itemModel.isProfileView()) {
            holder.postTextViewName.setText("Name " + itemModel.getAuthor());
            holder.postTextViewRegDatetime.setText("DateTime of registration " + itemModel.getDateTimeOfRegistration());
            holder.postTextViewNumberOfPosts.setText("Number of posts " + itemModel.getNumberOfPosts());
        } else {
            holder.postAuthor.setText("Autor: " + itemModel.getAuthor());
            holder.postTime.setText("Dátum a čas: " + itemModel.getDateTimeOfPost());
            if(itemModel.isImage()){
                holder.videoView_post.setVisibility(View.GONE);
                Glide.with(mContext)
                        .load("http://mobv.mcomputing.eu/upload/v/"+ itemModel.getUrl()) // or URI/path
                        .apply(new RequestOptions()
                                .fitCenter()
                                .centerCrop()
                        )
                        .into(holder.imageView_post);
            }else{
                holder.imageView_post.setVisibility(View.GONE);
                DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(); //test

                TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                // 2. Create the player
                player = VideoPlayer.getInstance(mContext); //ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
                holder.videoView_post.setUseController(false);
                holder.videoView_post.requestFocus();
                holder.videoView_post.setPlayer(player);

                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mContext,
                        Util.getUserAgent(mContext, "Zadanie"), bandwidthMeter);

                DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse("http://mobv.mcomputing.eu/upload/v/"+ itemModel.getUrl()));

                holder.videoView_post.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                holder.setIsRecyclable(false);

                player.prepare(videoSource);
                player.setPlayWhenReady(true);

                holder.videoView_post.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(player.getPlayWhenReady()){
                            player.setPlayWhenReady(false);
                        }
                        else {
                            player.setPlayWhenReady(true);
                        }
                        return false;
                    }
                });
            }

        }
    }

    // determine which layout to use for the row
    @Override
    public int getItemViewType(int position) {
        SingleItemModel singleItemModel = itemModels.get(position);
        if (singleItemModel.isProfileView()) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }


    @Override
    public int getItemCount() {
        return (null != itemModels ? itemModels.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView postAuthor;
        protected TextView postTime;
        protected ImageView imageView_post;
        protected TextView postTextViewName;
        protected TextView postTextViewRegDatetime;
        protected TextView postTextViewNumberOfPosts;
        protected PlayerView videoView_post;

        public SingleItemRowHolder(View itemView) {
            super(itemView);
            this.postAuthor = itemView.findViewById(R.id.postAuthor);
            this.postTime = itemView.findViewById(R.id.postTime);
            this.imageView_post = itemView.findViewById(R.id.imageView_post);
            this.videoView_post = itemView.findViewById(R.id.videoView_post);
            this.postTextViewName = itemView.findViewById(R.id.postTextViewName);
            this.postTextViewRegDatetime = itemView.findViewById(R.id.postTextViewRegDatetime);
            this.postTextViewNumberOfPosts = itemView.findViewById(R.id.postTextViewNumberOfPosts);
        }
    }

}
