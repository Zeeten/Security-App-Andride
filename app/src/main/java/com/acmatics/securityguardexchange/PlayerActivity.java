package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.acmatics.securityguardexchange.bean.RowItem;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.GcmRegistrationConstant;
import com.acmatics.securityguardexchange.helper.VideoDataHelper;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.List;


public class PlayerActivity extends YouTubeFailureRecoveryActivity implements
		OnItemClickListener {

	private static final int PORTRAIT_ORIENTATION = Build.VERSION.SDK_INT < 9 ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
			: ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;

	private String videoId;

	private List<RowItem> rowItems;

	private YouTubePlayer rhymePlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConversionHelper.setAppLanguage(this);
		setContentView(R.layout.activity_player);

		/*
		 * AdView adView = (AdView) findViewById(R.id.top_banner_ad_player);
		 * AdRequest adRequest = new AdRequest.Builder().addTestDevice(
		 * AdRequest.DEVICE_ID_EMULATOR).build(); adView.loadAd(adRequest);
		 */

		Intent intent = getIntent();
		videoId = intent.getStringExtra("videoId");
		setTitle(intent.getStringExtra("videoName"));

		YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
		youTubeView.initialize(GcmRegistrationConstant.DEVELOPER_KEY, this);

		rowItems = VideoDataHelper.getRowItems();

		ListView mylistview = (ListView) findViewById(R.id.rhymes_list_player);
		TrainingVideoAdapter adapter = new TrainingVideoAdapter(this, rowItems);
		mylistview.setAdapter(adapter);
		mylistview.setOnItemClickListener(this);
	}

	@Override
	public void onInitializationSuccess(YouTubePlayer.Provider provider,
			YouTubePlayer player, boolean wasRestored) {
		rhymePlayer = player;
		if (!wasRestored) {
			player.cueVideo(videoId);
			int controlFlags = player.getFullscreenControlFlags();
			setRequestedOrientation(PORTRAIT_ORIENTATION);
			controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
			player.setFullscreenControlFlags(controlFlags);
		}
	}

	@Override
	protected YouTubePlayer.Provider getYouTubePlayerProvider() {
		return (YouTubePlayerView) findViewById(R.id.youtube_view);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		RowItem rowItem = (RowItem) rowItems.get(position);
		setTitle(rowItem.getVideoName());
		videoId = rowItem.getVideoId();
		rhymePlayer.cueVideo(videoId);
	}

	public void showLyrics(View view) {
		int position = (Integer) view.getTag();
		RowItem rowItem = (RowItem) rowItems.get(position);
		Intent intent = new Intent(this, LyricsActivity.class);
		intent.putExtra("videoLyrics", rowItem.getVideoLyrics());
		startActivity(intent);
	}
}
