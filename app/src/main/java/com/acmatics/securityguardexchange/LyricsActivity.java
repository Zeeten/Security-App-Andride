package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.ConversionHelper;

public class LyricsActivity extends ActionBarActivity {

	private String videoLyrics;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ConversionHelper.setAppLanguage(this);
		setContentView(R.layout.activity_lyrics);

		Intent intent = getIntent();
		videoLyrics = intent.getStringExtra("videoLyrics");

		TextView lyricsText = (TextView) findViewById(R.id.lyrics);

		String[] lyrics1 = videoLyrics.split("/");
		String adder = new String();
		for (int i = 0; i < lyrics1.length; i++) {
			adder += "\n" + lyrics1[i];
		}
		lyricsText.setText(adder);
	}

}
