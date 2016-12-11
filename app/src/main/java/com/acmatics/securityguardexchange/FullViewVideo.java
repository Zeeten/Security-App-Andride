package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.acmatics.securityguardexchange.bean.RowItem;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.GcmRegistrationConstant;
import com.acmatics.securityguardexchange.helper.VideoDataHelper;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;

/**
 * Created by kaira on 4/18/2016.
 */
public class FullViewVideo extends YouTubeFailureRecoveryActivity {

    private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;


    private String videoId;
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.fullview_activity);

        Intent intent = getIntent();
        videoId = intent.getStringExtra("urlId");
//        setTitle(intent.getStringExtra("videoName"));

        YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(GcmRegistrationConstant.DEVELOPER_KEY, this);

        findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayer.release();
                finish();
            }
        });


    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        youTubePlayer = player;
        if (!wasRestored) {
            player.cueVideo(videoId);
            int controlFlags = player.getFullscreenControlFlags();
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(PORTRAIT_ORIENTATION);
            controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
            player.setFullscreenControlFlags(controlFlags);
            player.loadVideo(videoId);
            player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                @Override
                public void onLoading() {

                }

                @Override
                public void onLoaded(String s) {

                }

                @Override
                public void onAdStarted() {

                }

                @Override
                public void onVideoStarted() {

                }

                @Override
                public void onVideoEnded() {
                    finish();
                    youTubePlayer.release();
                }

                @Override
                public void onError(YouTubePlayer.ErrorReason errorReason) {

                }
            });

        }

    }

    @Override
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }


}