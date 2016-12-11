package com.acmatics.securityguardexchange;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.bean.PracticeBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.TestReport;
import com.acmatics.securityguardexchange.dao.TestReportDao;
import com.acmatics.securityguardexchange.dao.UserTest;
import com.acmatics.securityguardexchange.dao.UserTestDao;
import com.acmatics.securityguardexchange.util.SessionUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;


public class PracticeListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<PracticeBean> testList;
    private PracticeAdapter practiceAdapter;
    DaoSession daoSession;
    private String VideoURL="http://118.67.250.173:8080/securityskillsws/resources/video/practicetestguideline.wmv";
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_quiz_list);
        app = (SecuritySkillsApplication) getApplication();
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        if (getIntent().hasExtra("attempted")) {
            toolBar.setTitle(getResources().getString(R.string.test_reports));
        }else{
            toolBar.setTitle(getResources().getString(R.string.practise_test_small));
        }
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DatabaseConstants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(getApplicationContext());
        if (!prefs.contains(SessionUtil.IS_PRACTICE_RUN)) {
            SessionUtil.savePracticeRun(PracticeListActivity.this, "firstRun");
            Intent i = new Intent(PracticeListActivity.this, ImageShowFullView.class);
            i.putExtra("urlId","test");
            startActivity(i);
//            showFollowDialog();
        }
        loadAllPracticeTests();
    }


    private void loadAllPracticeTests() {
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        Long activationDateMillis = null;
        if(prefs.contains(SessionUtil.ACTIVATION_DATE)){
            activationDateMillis = Long.parseLong(prefs.getString(SessionUtil.ACTIVATION_DATE,""));
        }else {
            activationDateMillis = Long.parseLong(SessionUtil.getTrialStartDate(this));
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(activationDateMillis);

        int testCount = daysBetween(Calendar.getInstance(), cal) + 1;
        if (testCount > 100) {
            testCount = 100;
        }

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DatabaseConstants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        List<UserTest> practiceTests = new ArrayList<>();
        if (getIntent().hasExtra("attempted")) {
            List<TestReport> userTestReports = daoSession.getTestReportDao().queryBuilder().orderAsc(TestReportDao.Properties.UserTestId).list();
            for (TestReport testReport : userTestReports) {
                practiceTests.add(testReport.getUserTest());
            }
        } else {
            practiceTests = daoSession.getUserTestDao().queryBuilder().orderAsc(UserTestDao.Properties.UserTestId).limit(testCount).list();
        }
        recyclerView = (RecyclerView) findViewById(R.id.quizRecyclerView);
        testList = new ArrayList<>();


        for (int i = 0; i < practiceTests.size(); i++) {
            PracticeBean list = new PracticeBean();
            list.setTestId(practiceTests.get(i).getUserTestId());
            list.setTestNumber(getResources().getString(R.string.practise_test_small) + practiceTests.get(i).getUserTestId());
            Calendar activationDate = Calendar.getInstance();
            activationDate.setTimeInMillis(activationDateMillis);

            activationDate.add(Calendar.DATE, i);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            list.setTestDate(formatter.format(activationDate.getTime()));
            list.setTestAttempts("5/10");
            testList.add(list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        practiceAdapter = new PracticeAdapter();
        recyclerView.setAdapter(practiceAdapter);
    }

    private int daysBetween(Calendar day1, Calendar day2) {
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.QuizViewHolder> {
        protected View itemView;

        @Override
        public int getItemCount() {
            return testList.size();
        }

        @Override
        public void onBindViewHolder(final QuizViewHolder vh, final int i) {
            final PracticeBean data = testList.get(i);
            if (i % 2 == 1) {
                vh.layout.setBackgroundResource(R.color.even_list_row_color);
            }
            if (i % 2 == 0) {
                vh.layout.setBackgroundResource(R.color.white);
            }
            vh.practiceNum.setText(data.getTestNumber());
            vh.practiceDate.setText(data.getTestDate());

            vh.layout.setTag(R.string.tag_test_id, data.getTestId());
            final Long testId = data.getTestId();

            QueryBuilder testReportQueryBuilder = daoSession.getTestReportDao().queryBuilder();
            final TestReport testReport = (TestReport) testReportQueryBuilder.where(TestReportDao.Properties.UserTestId.eq(testId)).unique();
            Boolean attempted = false;
            if (testReport != null) {
                attempted = true;
                vh.next.setVisibility(View.GONE);
                vh.practiceAttempts.setVisibility(View.VISIBLE);
                vh.practiceAttempts.setText(testReport.getScore() + "/10");
                vh.practiceAttempts.setTextColor(getResources().getColor(R.color.white));
            } else {
                vh.next.setVisibility(View.VISIBLE);
                vh.practiceAttempts.setVisibility(View.GONE);
            }

            vh.layout.setTag(R.string.tag_attempted, attempted);

            vh.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long testId = (Long) v.getTag(R.string.tag_test_id);
                    Boolean attempted = (Boolean) v.getTag(R.string.tag_attempted);
                    if (attempted) {
                        Intent intent = new Intent(PracticeListActivity.this, ReportActivity.class);
                        intent.putExtra("testId", testId.toString());
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(PracticeListActivity.this, StartTestActivity.class);
                        intent.putExtra("testId", testId);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public QuizViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.practise_list_item, viewGroup, false);
            return new QuizViewHolder(itemView);
        }

        public class QuizViewHolder extends RecyclerView.ViewHolder {
            protected TextView practiceNum;
            protected TextView practiceDate;
            protected TextView practiceAttempts;
            protected ImageView next;
            protected RelativeLayout layout;

            public QuizViewHolder(View v) {
                super(v);
                practiceNum = (TextView) v.findViewById(R.id.testNumber);
                practiceDate = (TextView) v.findViewById(R.id.testDate);
                practiceAttempts = (TextView) v.findViewById(R.id.testAttempts);
                next = (ImageView) v.findViewById(R.id.iv_start_quiz);
                layout = (RelativeLayout) v.findViewById(R.id.layout);
            }
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
    protected void onRestart() {
        loadAllPracticeTests();
        super.onRestart();
    }
    private void showFollowDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_video);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        SessionUtil.savePracticeRun(PracticeListActivity.this, "firstRun");
        dialog.setCancelable(false);
        final VideoView videoview = (VideoView)dialog. findViewById(R.id.VideoView);
        final ProgressBar  progressBar=(ProgressBar)dialog. findViewById(R.id.progressBar);

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(PracticeListActivity.this);
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(VideoURL);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
//                progressBar.setVisibility(View.GONE);
                videoview.start();


            }
        });
        videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
//                        videoview.start();
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
//                        progressBar.setVisibility(View.GONE);
//                        videoview.start();
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        progressBar.setVisibility(View.GONE);
//                        videoview.start();
                        return true;
                    }
                }
                return false;
            }
        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dialog.dismiss();
            }
        });
        ((Button)dialog.findViewById(R.id.btnSkip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("Practice Activity");
    }

}
