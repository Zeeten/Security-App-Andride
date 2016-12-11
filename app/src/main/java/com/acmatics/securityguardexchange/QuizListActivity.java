package com.acmatics.securityguardexchange;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.Quiz;
import com.acmatics.securityguardexchange.dao.QuizDao;
import com.acmatics.securityguardexchange.dao.UserQuizReport;
import com.acmatics.securityguardexchange.dao.UserQuizReportDao;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.dao.query.QueryBuilder;


public class QuizListActivity extends AppCompatActivity {

    private CardView qLevel1, qLevel2, qLevel3;
    private DaoSession daoSession;
    private Boolean isScratchCodeActivated;
    private AQuery aq;
    private String VideoURL="http://118.67.250.173:8080/securityskillsws/resources/video/cssdquizguideline.wmv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_quiz_listing);

        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        /*ImageView play = (ImageView) toolBar.findViewById(R.id.playMovie);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showFollowDialog();

            }
        });*/
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.quiz_list));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DatabaseConstants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        if (!prefs.contains(SessionUtil.IS_TEST_RUN)) {
            SessionUtil.saveTestRun(QuizListActivity.this, "firstRun");
            Intent i = new Intent(QuizListActivity.this, ImageShowFullView.class);
            i.putExtra("urlId","quiz");
            startActivity(i);
//            showFollowDialog();
        }
        aq= new AQuery(this);
        if(prefs.contains(SessionUtil.ACTIVATION_DATE)){
            isScratchCodeActivated = true;
            String activationDate =  prefs.getString(SessionUtil.ACTIVATION_DATE,"");
            Long activationDateMillSec = Long.parseLong(activationDate);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(activationDateMillSec);
            cal.add(Calendar.MONTH, 2);
            cal.set(Calendar.DATE, 1);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy");
            ((TextView)findViewById(R.id.level1_date)).setText(formatter.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
            ((TextView)findViewById(R.id.level2_date)).setText(formatter.format(cal.getTime()));
            cal.add(Calendar.MONTH, 1);
            ((TextView)findViewById(R.id.level3_date)).setText(formatter.format(cal.getTime()));
        }else{
            isScratchCodeActivated = false;
            ((TextView)findViewById(R.id.level1_date)).setText(getResources().getString(R.string.activate_scratch_code));
            ((TextView)findViewById(R.id.level2_date)).setText(getResources().getString(R.string.activate_scratch_code));
            ((TextView)findViewById(R.id.level3_date)).setText(getResources().getString(R.string.activate_scratch_code));
        }
        qLevel1 = (CardView) findViewById(R.id.qLevel1);
        qLevel2 = (CardView) findViewById(R.id.qLevel2);
        qLevel3 = (CardView) findViewById(R.id.qLevel3);

        qLevel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelOneDialog();

            }
        });

        qLevel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelTwoDialog();
            }
        });

        qLevel3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLevelThreeDialog();
            }
        });
    }



    public void showLevelOneDialog() {
        final Quiz quiz = daoSession.getQuizDao().queryBuilder().where(QuizDao.Properties.QuizTypeId.eq(1l)).unique();

        final Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.custom_quiz_list);
        paymentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogUpDownAnimation;
        TextView time = (TextView) paymentDialog.findViewById(R.id.quizTime);
        TextView ques = (TextView) paymentDialog.findViewById(R.id.quizQues);
        TextView level = (TextView) paymentDialog.findViewById(R.id.level);
        level.setText(getResources().getString(R.string.levelOne));
        time.setText(getResources().getString(R.string.thirty_min));
        ques.setText(getResources().getString(R.string.thirty_ques));
        Button cont = (Button) paymentDialog.findViewById(R.id.continueQuiz);
        TextView quizDate = (TextView) paymentDialog.findViewById(R.id.quizDate);
        quizDate.setText(((TextView)findViewById(R.id.level1_date)).getText());
        isScratchCodeActivated(cont);
        QueryBuilder testReportQueryBuilder = daoSession.getUserQuizReportDao().queryBuilder();
        final UserQuizReport quizReport = (UserQuizReport) testReportQueryBuilder.where(UserQuizReportDao.Properties.QuizId.eq(1l)).unique();
        if (quizReport != null) {
            cont.setText(getResources().getString(R.string.quiz_results));

        }
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserEligibility(1l,"30",paymentDialog);
            }
        });
        paymentDialog.show();
    }

    public void showLevelTwoDialog() {
        final Quiz quiz = daoSession.getQuizDao().queryBuilder().where(QuizDao.Properties.QuizTypeId.eq(2l)).unique();

        final Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.custom_quiz_list);
        paymentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogUpDownAnimation;
        TextView time = (TextView) paymentDialog.findViewById(R.id.quizTime);
        TextView ques = (TextView) paymentDialog.findViewById(R.id.quizQues);
        TextView level = (TextView) paymentDialog.findViewById(R.id.level);
        level.setText(getResources().getString(R.string.levelTwo));
        time.setText(getResources().getString(R.string.sixty_min));
        ques.setText(getResources().getString(R.string.sixty_ques));
        Button cont = (Button) paymentDialog.findViewById(R.id.continueQuiz);
        TextView quizDate = (TextView) paymentDialog.findViewById(R.id.quizDate);
        quizDate.setText(((TextView)findViewById(R.id.level2_date)).getText());
        isScratchCodeActivated(cont);
        QueryBuilder testReportQueryBuilder = daoSession.getUserQuizReportDao().queryBuilder();
        final UserQuizReport quizReport = (UserQuizReport) testReportQueryBuilder.where(UserQuizReportDao.Properties.QuizId.eq(2l)).unique();
        if (quizReport != null) {
            cont.setText(getResources().getString(R.string.quiz_results));

        }
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserEligibility(2l, "60", paymentDialog);
               /* Intent intent = new Intent(QuizListActivity.this, StartTestActivity.class);
                intent.putExtra("quizId",2l);
                intent.putExtra("totalqus","60");
                startActivity(intent);
                paymentDialog.dismiss();*/
            }
        });
        paymentDialog.show();

    }

    private void isScratchCodeActivated(Button cont) {
        if(isScratchCodeActivated) {
            cont.setBackgroundResource(R.drawable.bg_primary_button);
            cont.setEnabled(true);
            cont.setClickable(true);
        } else {
            cont.setBackgroundResource(R.drawable.bg_disabled_button);
            cont.setEnabled(false);
            cont.setClickable(false);
        }
    }

    private void checkUserEligibility(final Long quizId, final String totalQues,final Dialog paymentDialog) {
        String url = UrlConstants.BASE_URL + UrlConstants.GET_ACTIVATION_DETAIL;
        Map params = new HashMap();
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        params.put("userId", prefs.getString(SessionUtil.USER_ID, ""));
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.verifying_eligibility));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        aq.progress(progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    if (json.has("scratchCodeNotActivated") || json.has("scratchCodeNotFound")) {
                       /* findViewById(R.id.otpError).setVisibility(View.VISIBLE);*/
                    } else {
                        try {
                            Long activationDateMillSec = json.getLong("activationDate");
                            Long currentDateMillSec = json.getLong("currentDate");
                            Calendar activationDate = Calendar.getInstance();
                            Calendar currentDate = Calendar.getInstance();
                            currentDate.setTimeInMillis(currentDateMillSec);
                            activationDate.setTimeInMillis(activationDateMillSec);
                            /*activationDate.add(Calendar.MONTH, 2);*/
                            activationDate.set(Calendar.DATE, 1);
                            if(quizId == 1){
                                activationDate.add(Calendar.MONTH, 2);
                            }else if (quizId == 2){
                                activationDate.add(Calendar.MONTH, 3);
                            }else if (quizId == 3){
                                activationDate.add(Calendar.MONTH, 4);
                            }
                            QueryBuilder testReportQueryBuilder = daoSession.getUserQuizReportDao().queryBuilder();
                            final UserQuizReport quizReport = (UserQuizReport) testReportQueryBuilder.where(UserQuizReportDao.Properties.QuizId.eq(quizId)).unique();
                            Boolean attempted = false;
                            if (quizReport != null) {
                                attempted = true;

                            }
                            if (currentDate.compareTo(activationDate) >= 0) {
                                if(attempted) {
                                    Intent intent = new Intent(QuizListActivity.this, ReportActivity.class);
                                    intent.putExtra("quizId", quizId.toString());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(QuizListActivity.this, StartTestActivity.class);
                                    intent.putExtra("quizId", quizId);
                                    intent.putExtra("totalqus", totalQues);
                                    startActivity(intent);
                                }
                            } else {
                                paymentDialog.dismiss();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd-MMMM-yyyy");
                                Toast.makeText(QuizListActivity.this, getResources().getString(R.string.you_will_be_able) + formatter.format(activationDate.getTime()), Toast.LENGTH_LONG).show();
                            }
                            paymentDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Toast.makeText(QuizListActivity.this, getResources().getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void showLevelThreeDialog() {
        final Quiz quiz = daoSession.getQuizDao().queryBuilder().where(QuizDao.Properties.QuizTypeId.eq(3l)).unique();

        final Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.custom_quiz_list);
        paymentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogUpDownAnimation;
        TextView time = (TextView) paymentDialog.findViewById(R.id.quizTime);
        TextView ques = (TextView) paymentDialog.findViewById(R.id.quizQues);
        TextView level = (TextView) paymentDialog.findViewById(R.id.level);
        level.setText(getResources().getString(R.string.levelThree));
        time.setText(getResources().getString(R.string.hundred_min));
        ques.setText(getResources().getString(R.string.hundred_ques));
        Button cont = (Button) paymentDialog.findViewById(R.id.continueQuiz);
        TextView quizDate = (TextView) paymentDialog.findViewById(R.id.quizDate);
        quizDate.setText(((TextView)findViewById(R.id.level3_date)).getText());
        isScratchCodeActivated(cont);
        QueryBuilder testReportQueryBuilder = daoSession.getUserQuizReportDao().queryBuilder();
        final UserQuizReport quizReport = (UserQuizReport) testReportQueryBuilder.where(UserQuizReportDao.Properties.QuizId.eq(3l)).unique();
        if (quizReport != null) {
            cont.setText(getResources().getString(R.string.quiz_results));

        }

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserEligibility(3l,"100",paymentDialog);
               /* Intent intent = new Intent(QuizListActivity.this, StartTestActivity.class);
                intent.putExtra("quizId",3l);
                intent.putExtra("totalqus","100");
                startActivity(intent);
                paymentDialog.dismiss();*/
            }
        });
        paymentDialog.show();

    }

    /*public void loadAllQuizData() {
        recyclerView = (RecyclerView) findViewById(R.id.quizRecyclerView);
        quizList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            QuizDataBean list = new QuizDataBean();
            list.setQuizName("HoNahaar Quiz Level " + (i + 1));
            quizList.add(list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAdapter();
        recyclerView.setAdapter(quizAdapter);
    }*/


    /*public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
        protected View itemView;

        @Override
        public int getItemCount() {
            return quizList.size();
        }

        @Override
        public void onBindViewHolder(final QuizViewHolder vh, final int i) {
            if (i % 2 == 1) {
                itemView.setBackgroundResource(R.color.even_list_row_color);
            } else if (i % 2 == 0) {
                itemView.setBackgroundResource(R.color.white);
            }
            final QuizDataBean data = quizList.get(i);
            vh.quizName.setText(data.getQuizName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (i == 2) {
                        Intent intent = new Intent(QuizListActivity.this, QuizReportActivity.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(QuizListActivity.this, StartTestActivity.class);
                        startActivity(intent);
                    }
                }
            });
            if (i == 2) {
                vh.quizReport.setVisibility(View.VISIBLE);
                vh.startQuiz.setVisibility(View.GONE);
            }
        }

        @Override
        public QuizViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.quiz_list_item, viewGroup, false);
            return new QuizViewHolder(itemView);
        }

        public class QuizViewHolder extends RecyclerView.ViewHolder {
            protected TextView quizName;
            protected TextView quizTime;
            protected TextView quizReport;
            protected ImageView startQuiz;
            protected TextView quizNo;

            public QuizViewHolder(View v) {
                super(v);
                quizName = (TextView) v.findViewById(R.id.tv_quiz_name);
                quizTime = (TextView) v.findViewById(R.id.tv_quiz_time);
                quizReport = (TextView) v.findViewById(R.id.tv_quiz_result);
                startQuiz = (ImageView) v.findViewById(R.id.iv_start_quiz);
                quizNo = (TextView) v.findViewById(R.id.tv_quiz_no);
            }
        }
    }
*/
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showFollowDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_video);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        SessionUtil.saveTestRun(QuizListActivity.this, "firstRun");
        dialog.setCancelable(false);
        final VideoView videoview = (VideoView)dialog. findViewById(R.id.VideoView);
        final ProgressBar  progressBar=(ProgressBar)dialog. findViewById(R.id.progressBar);

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(QuizListActivity.this);
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
}
