package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.fragments.NavigationDrawerFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Rubal on 03-12-2015.
 */
public class HomeActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, View.OnClickListener {
    private Toolbar toolBar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_home);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.home_title));
        setSupportActionBar(toolBar);
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Setting up the navigation drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer));

        findViewById(R.id.booksCard).setOnClickListener(HomeActivity.this);
        findViewById(R.id.quizCard).setOnClickListener(HomeActivity.this);
        findViewById(R.id.testCard).setOnClickListener(HomeActivity.this);
        findViewById(R.id.videoCard).setOnClickListener(HomeActivity.this);
        findViewById(R.id.playMovie1).setOnClickListener(HomeActivity.this);
        findViewById(R.id.playMovie2).setOnClickListener(HomeActivity.this);
        findViewById(R.id.playMovie3).setOnClickListener(HomeActivity.this);
        findViewById(R.id.playMovie4).setOnClickListener(HomeActivity.this);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        if (position == 0) {  //home

        } else if (position == 1) {  //Test reports
            Intent i = new Intent(HomeActivity.this, PracticeListActivity.class);
            i.putExtra("attempted", "home");
            startActivity(i);

        } else if (position == 2) {  //quiz results
            Intent i = new Intent(HomeActivity.this, QuizListActivity.class);
            i.putExtra("attemptedQuiz", "home");
            startActivity(i);

        } else if (position == 3) {  //awards
            Intent i = new Intent(HomeActivity.this, AwardActivity.class);
            startActivity(i);
        } else if (position == 4) {  //academic records
            Intent i = new Intent(HomeActivity.this, AcademicRecordsActivity.class);
            startActivity(i);
        } else if (position == 5) {  //notifications
            Intent i = new Intent(HomeActivity.this, NotificationActivity.class);
            startActivity(i);
        } else if (position == 6) {  //profile
            Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(i);
        } else if (position == 7) {  //profile
            Intent i = new Intent(HomeActivity.this, SelectLanguageActivity.class);
            i.putExtra("home", "homeScreen");
            startActivity(i);
        } else if (position == 8) {  //scan qr
            Intent i = new Intent(HomeActivity.this, ScanQRActivity.class);
            startActivity(i);
        } else if (position == 9) {  //scan qr
            String[] blacklist = new String[]{"com.android.bluetooth", "com.google.android.apps.docs", "com.dropbox.android", "com.estrongs.android.pop", "com.sec.android.app.memo", "com.google.provider.NotePad", "com.socialnmobile.dictapps.notepad.color.note"};
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Download Security Skills App at https://play.google.com/store/apps/details?id=com.acmatics.securitygaurdexchange to help security personnel of Private Security Sector (PSS) to continuously develop their security skills");
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Security Skills");
            startActivity(generateCustomChooserIntent(intent, blacklist));
        }
    }

    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
        Intent chooserIntent;
        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = this.getPackageManager().queryIntentActivities(dummy, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;

                HashMap<String, String> info = new HashMap<String, String>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(this.getPackageManager())));
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
// sorting for nice readability
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

// create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), "Share via");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, "securityskills");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.booksCard) {
            Intent intent = new Intent(HomeActivity.this, BooksActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.testCard) {
            Intent intent = new Intent(HomeActivity.this, PracticeListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.quizCard) {
            Intent intent = new Intent(HomeActivity.this, QuizListActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.videoCard) {
            Intent intent = new Intent(HomeActivity.this, TrainingVideosActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.playMovie1) {
            Intent i = new Intent(HomeActivity.this, ImageShowFullView.class);
            i.putExtra("urlId", "books");
            startActivity(i);
        } else if (v.getId() == R.id.playMovie2) {
            Intent i = new Intent(HomeActivity.this, ImageShowFullView.class);
            i.putExtra("urlId", "test");
            startActivity(i);
        } else if (v.getId() == R.id.playMovie3) {
            Intent i = new Intent(HomeActivity.this, ImageShowFullView.class);
            i.putExtra("urlId", "quiz");
            startActivity(i);
        } else if (v.getId() == R.id.playMovie4) {
            Intent i = new Intent(HomeActivity.this, ImageShowFullView.class);
            i.putExtra("urlId", "videos");
            startActivity(i);
        }

        /*else if (v.getId() == R.id.playMovie1) {
            Intent i = new Intent(HomeActivity.this, GuidlineVideos.class);
            i.putExtra("urlId", getResources().getString(R.string.book_guide_line_url));
            startActivity(i);
        } else if (v.getId() == R.id.playMovie2) {
            Intent i = new Intent(HomeActivity.this, GuidlineVideos.class);
            i.putExtra("urlId", getResources().getString(R.string.practice_test_guide_line_url));
            startActivity(i);
        } else if (v.getId() == R.id.playMovie3) {
            Intent i = new Intent(HomeActivity.this, GuidlineVideos.class);
            i.putExtra("urlId", getResources().getString(R.string.cssd_quiz_guide_line_url));
            startActivity(i);
        } else if (v.getId() == R.id.playMovie4) {
            Intent i = new Intent(HomeActivity.this, GuidlineVideos.class);
            i.putExtra("urlId", getResources().getString(R.string.video_part_guide_line_url));
            startActivity(i);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("HomePage Activity");
    }

}
