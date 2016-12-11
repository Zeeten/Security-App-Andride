package com.acmatics.securityguardexchange;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.bean.RowItem;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.helper.VideoDataHelper;
import com.acmatics.securityguardexchange.util.SessionUtil;

import java.util.List;

/**
 * Created by kaira on 12/5/2015.
 */
public class TrainingVideosActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    public static final String PREFS_NAME = "HNR_Preferences";
    private List<RowItem> rowItems;
    private String videoLyrics;
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_training_videos);
        app = (SecuritySkillsApplication) getApplication();
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);

        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.videos_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(getApplicationContext());
        if (!prefs.contains(SessionUtil.IS_VIDEO_RUN)) {
            SessionUtil.saveVideoRun(TrainingVideosActivity.this, "firstRun");
            Intent i = new Intent(TrainingVideosActivity.this, ImageShowFullView.class);
            i.putExtra("urlId","videos");
            startActivity(i);
//            showVideoDialog();
        }
        showDisclaimerIfNeeded();
        rowItems = VideoDataHelper.getRowItems();

        ListView mylistview = (ListView) findViewById(R.id.video_list);
        TrainingVideoAdapter adapter = new TrainingVideoAdapter(this, rowItems);
        mylistview.setAdapter(adapter);
        mylistview.setOnItemClickListener(this);
    }

    private void showVideoDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_video);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        SessionUtil.saveVideoRun(TrainingVideosActivity.this, "firstRun");
        dialog.show();

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        };

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                handler.removeCallbacks(runnable);
            }
        });
        handler.postDelayed(runnable, 3000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(),
                "com.acmatics.securityguardexchange.PlayerActivity"));
        RowItem rowItem = (RowItem) rowItems.get(position);
        intent.putExtra("videoId", rowItem.getVideoId());
        intent.putExtra("videoName", rowItem.getVideoName());
        startActivity(intent);
    }

    public void showLyrics(View view) {
        int position = (Integer) view.getTag();
        RowItem rowItem = (RowItem) rowItems.get(position);
        Intent intent = new Intent(this, LyricsActivity.class);
        intent.putExtra("videoLyrics", rowItem.getVideoLyrics());
        startActivity(intent);
    }

    private void showDisclaimerIfNeeded() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean disclaimerAccepted = settings.getBoolean("disclaimerAccepted",
                false);

        if (!disclaimerAccepted) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setTitle(getResources().getString(R.string.disclaimer));

            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.google_term_condition))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.accept),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.dismiss();
                                    SharedPreferences settings = getSharedPreferences(
                                            PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings
                                            .edit();
                                    editor.putBoolean("disclaimerAccepted",
                                            true);
                                    editor.commit();
                                }
                            })
                    .setNegativeButton(getResources().getString(R.string.decline),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    finish();
                                }
                            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("Videos Activity");
    }

}
