package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.TestReport;
import com.acmatics.securityguardexchange.dao.TestReportDao;
import com.acmatics.securityguardexchange.dao.UserQuizReport;
import com.acmatics.securityguardexchange.dao.UserQuizReportDao;

import java.text.SimpleDateFormat;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by kaira on 12/3/2015.
 */
public class ReportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_quiz_report);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        if(getIntent().hasExtra("testId")) {
            toolBar.setTitle(getResources().getString(R.string.report));
        } else {
            toolBar.setTitle(getResources().getString(R.string.quiz_results));
        }
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        inflateXml();
    }

    public void inflateXml() {
        TextView testNum = (TextView) findViewById(R.id.quiz_no);
        TextView attempt = (TextView)findViewById(R.id.attempts_ques);
        TextView testDate = (TextView) findViewById(R.id.quiz_date);
        TextView total = (TextView) findViewById(R.id.totale_score);
        TextView quizTime = (TextView) findViewById(R.id.quiz_time);
        TextView grade = (TextView) findViewById(R.id.grade);
        Button next = (Button) findViewById(R.id.btnReTest);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(ReportActivity.this, DatabaseConstants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        final DaoSession daoSession = daoMaster.newSession();
        if(getIntent().hasExtra("testId")) {
            next.setText(getResources().getString(R.string.re_test));
            generateTestReport(testNum, attempt, testDate, total, quizTime, grade, daoSession);
        }else{
            next.setText(getResources().getString(R.string.back_to_quiz));
            generateQuizReport(testNum, attempt, testDate, total, quizTime, grade, daoSession);
        }

        /*findViewById(R.id.btnBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizReportActivity.this, QuizAllReportsActivity.class);
                startActivity(intent);
            }
        });*/
        findViewById(R.id.btnReTest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getIntent().hasExtra("testId")){
                Intent intent = new Intent(ReportActivity.this, PractiseTestActivity.class);
                intent.putExtra("testId",Long.parseLong(getIntent().getStringExtra("testId")));
                startActivity(intent);
                finish();
                }else{
                    Intent intent = new Intent(ReportActivity.this, QuizListActivity.class);
                    //intent.putExtra("quizId",Long.parseLong(getIntent().getStringExtra("quizId")));
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void generateTestReport(TextView testNum, TextView attempt, TextView testDate, TextView total, TextView quizTime, TextView grade, DaoSession daoSession) {
        QueryBuilder testReportQueryBuilder = daoSession.getTestReportDao().queryBuilder();
        TestReport testReport = (TestReport)testReportQueryBuilder.where(TestReportDao.Properties.UserTestId.eq(Long.parseLong(getIntent().getStringExtra("testId")))).unique();
        testNum.setText(getResources().getString(R.string.test_number) + testReport.getUserTestId());
        int secs = testReport.getDuration();
        int mins = secs / 60;
        secs = secs % 60;
        int hour=0;
        if (mins >= 60) {
            hour = mins / 60;
            mins=mins%60;
        }

        quizTime.setText(getResources().getString(R.string.time)+" : " + hour +":"+ mins +":"+ secs );
        total.setText(getResources().getString(R.string.score)+" : "  + testReport.getScore() + "/" + "10");
        attempt.setText(getResources().getString(R.string.attempt)+" : " + testReport.getAttemptCount().toString());
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        testDate.setText(getResources().getString(R.string.date)+" : " + formatter.format(testReport.getTestAttemptTime()));
        if(testReport.getScore() >= 8) {
            grade.setText(getResources().getString(R.string.grade_a));
        } else if(testReport.getScore() >= 6) {
            grade.setText(getResources().getString(R.string.grade_b));
        } else if(testReport.getScore() >= 4) {
            grade.setText(getResources().getString(R.string.grade_c));
        }else {
            grade.setText(getResources().getString(R.string.grade_d));
        }
    }
    private void generateQuizReport(TextView testNum, TextView attempt, TextView testDate, TextView total, TextView quizTime, TextView grade, DaoSession daoSession) {
        QueryBuilder quizReportQueryBuilder = daoSession.getUserQuizReportDao().queryBuilder();
        UserQuizReport quizReport = (UserQuizReport)quizReportQueryBuilder.where(UserQuizReportDao.Properties.QuizId.eq(Long.parseLong(getIntent().getStringExtra("quizId")))).unique();
        Long quizId = quizReport.getQuizId();
        if(quizId == 1l){
            testNum.setText(getResources().getString(R.string.QuizlevelOne));
            total.setText(getResources().getString(R.string.score)+" : " + quizReport.getScore() + "/" + "30");
        } else if (quizId == 2l){
            testNum.setText(getResources().getString(R.string.QuizlevelTwo));
            total.setText(getResources().getString(R.string.score)+" : " + quizReport.getScore() + "/" + "60");
        } else if(quizId == 3l) {
            testNum.setText(getResources().getString(R.string.QuizlevelThree));
            total.setText(getResources().getString(R.string.score)+" : " + quizReport.getScore() + "/" + "100");
        }
        int secs =  quizReport.getDuration();
        int mins = secs / 60;
        int hour=0;
        if (mins >= 60) {
            hour = mins / 60;
            mins=mins%60;
        }
        secs = secs % 60;
        quizTime.setText(getResources().getString(R.string.time)+" : " + hour +":"+ mins +":"+ secs );
        attempt.setVisibility(View.GONE);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        testDate.setText(getResources().getString(R.string.date)+" : " + formatter.format(quizReport.getQuizAttemptTime()));
        grade.setVisibility(View.GONE);

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
}
