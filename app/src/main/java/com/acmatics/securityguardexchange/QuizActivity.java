package com.acmatics.securityguardexchange;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.dao.AnswerChoice;
import com.acmatics.securityguardexchange.dao.AnswerChoiceDao;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.Question;
import com.acmatics.securityguardexchange.dao.QuestionDao;
import com.acmatics.securityguardexchange.dao.QuizQuestion;
import com.acmatics.securityguardexchange.dao.QuizQuestionDao;
import com.acmatics.securityguardexchange.dao.TestQuestionMapping;
import com.acmatics.securityguardexchange.dao.TestQuestionMappingDao;
import com.acmatics.securityguardexchange.dao.UserQuizReport;
import com.acmatics.securityguardexchange.dao.UserQuizReportDao;
import com.acmatics.securityguardexchange.util.SessionUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by kaira on 12/16/2015.
 */
public class QuizActivity extends AppCompatActivity implements View.OnClickListener, TextToSpeech.OnUtteranceCompletedListener {
    private boolean isPanelShown;
    private Button btnCheck;
    private int count = 0;
    private TextView textQuestion, qusNo, qusTimer;
    private int itemPosition;
    private boolean isSelectedAns;
    private Toolbar toolBar;
    private SecuritySkillsApplication app;
    private QuizAnswerAdapter quizAnswerAdapter;
    private RecyclerView ansRecyclerView;

    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DatabaseConstants.DATABASE_NAME, null);
    public SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private List<AnswerChoice> answerChoiceList;
    private List<Question> questionList;

    private int rightAns = 0;
    private int wrongAns = 0;
    private boolean isRowSelected;
    private String answer;
    private boolean isCheckAnswer;
    //    private char[] charArray = {'A', 'B', 'C', 'D', 'E', 'F'};
    private char[] charArray;
    private boolean isQuestionRead = false;


    private MalibuCountDownTimer countDownTimer;
    private Integer timeTaken;
    private long startTime;
    private final long interval = 1000;

    private ImageView questionImage;
    private View btnSpeech;
    //private Button btnSpeech;
    private TextToSpeech textToSpeech;
    private String ansText = "";
    private String textSpeech;
    private final static int CHECK_TTS_ACTIVITY_ID = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_quiz_layout);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        TextView title = (TextView) toolBar.findViewById(R.id.title_name);
        //btnSpeech = (Button) toolBar.findViewById(R.id.btn_speech);
        btnSpeech = (View) findViewById(R.id.btn_speech);
        btnSpeech.setOnClickListener(this);
        qusTimer = (TextView) toolBar.findViewById(R.id.tv_timer);
        title.setText(getResources().getString(R.string.quiz_question_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        checkTextToSpeech(QuizActivity.this, CHECK_TTS_ACTIVITY_ID);

        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        if (getIntent().hasExtra("testId")) {
            List<TestQuestionMapping> testQuestions = daoSession.getTestQuestionMappingDao().queryBuilder().where(TestQuestionMappingDao.Properties.UserTestId.eq(getIntent().getLongExtra("testId", 0l))).list();
            List<Long> questionsIds = new ArrayList<>();
            for (TestQuestionMapping mapping : testQuestions) {
                questionsIds.add(mapping.getQuestionId());
            }
            questionList = daoSession.getQuestionDao().queryBuilder().where(QuestionDao.Properties.QuestionId.in(questionsIds)).list();
        } else if (getIntent().hasExtra("quizId")) {
            List<QuizQuestion> quizQuestions = daoSession.getQuizQuestionDao().queryBuilder().where(QuizQuestionDao.Properties.QuizId.eq(getIntent().getLongExtra("quizId", 0l))).list();
            List<Long> questionsIds = new ArrayList<>();
            for (QuizQuestion mapping : quizQuestions) {
                questionsIds.add(mapping.getQuestionId());
            }
            questionList = daoSession.getQuestionDao().queryBuilder().where(QuestionDao.Properties.QuestionId.in(questionsIds)).list();
        }
        if (getIntent().hasExtra("totalQus") && getIntent().getExtras().getString("totalQus").equals("30")) {
            startTime = 30 * 60 * 1000;
        } else if (getIntent().hasExtra("totalQus") && getIntent().getExtras().getString("totalQus").equals("60")) {
            startTime = 60 * 60 * 1000;
        } else if (getIntent().hasExtra("totalQus") && getIntent().getExtras().getString("totalQus").equals("100")) {
            startTime = 100 * 60 * 1000;
        }
        inflateXml();
        loadQuestion();
    }

    public void inflateXml() {
        countDownTimer = new MalibuCountDownTimer(startTime, interval);
        countDownTimer.start();

        btnCheck = (Button) findViewById(R.id.btn_check);
        textQuestion = (TextView) findViewById(R.id.textQuestion);
        questionImage = (ImageView) findViewById(R.id.question_image);
        qusNo = (TextView) findViewById(R.id.question_no);
        ansRecyclerView = (RecyclerView) findViewById(R.id.ansRecyclerView);
        findViewById(R.id.btn_check).setEnabled(false);
        findViewById(R.id.btn_check).setBackgroundResource(R.drawable.primary_button_disable_bg);
        btnCheck.setOnClickListener(this);
//        textToSpeech = new TextToSpeech(this, this);
        charArray = getResources().getString(R.string.answer_option_no).toCharArray();
        ansText = "";//getResources().getString(R.string.option_is);
    }

    public void loadQuestion() {
        Question question = questionList.get(count);
        int resId = getResources().getIdentifier(question.getTextKey(), "string", getPackageName());
        qusNo.setText(count + 1 + " -");
        textQuestion.setText(resId);
        if (question.getImageFlag()) {
            resId = getResources().getIdentifier(question.getTextKey() + "_image", "string", getPackageName());
            if (resId != 0) {
                int imageResId = getResources().getIdentifier(getString(resId), "drawable", getPackageName());
                questionImage.setImageResource(imageResId);
                questionImage.setVisibility(View.VISIBLE);
            }
        } else {
            questionImage.setVisibility(View.GONE);
        }
        answerChoiceList = daoSession.getAnswerChoiceDao().queryBuilder().where(AnswerChoiceDao.Properties.QuestionId.eq(question.getQuestionId())).list();

        ansRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizAnswerAdapter = new QuizAnswerAdapter();
        ansRecyclerView.setAdapter(quizAnswerAdapter);

    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.btn_description) {
//            Intent intent = new Intent(QuizActivity.this, QuestionDescriptionActivity.class);
//            intent.putExtra("question", textQuestion.getText().toString());
//            intent.putExtra("questionNo", qusNo.getText().toString());
//            intent.putExtra("ans", answer);
//            startActivity(intent);
//        }
        if (v.getId() == R.id.btn_speech) {
            stopAudio();
        } else if (v.getId() == R.id.btn_check) {
            btnCheck.setText(getResources().getString(R.string.next));
            findViewById(R.id.btn_check).setEnabled(false);
            findViewById(R.id.btn_check).setBackgroundResource(R.drawable.primary_button_disable_bg);
            quizAnswerAdapter.notifyDataSetChanged();
            count++;
            if (count != questionList.size()) {
                loadQuestion();
            }
            isRowSelected = false;
            isCheckAnswer = false;
            isQuestionRead = false;
            ansText = "";//getResources().getString(R.string.option_is);
            textSpeech = "";
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            ((FloatingActionButton) findViewById(R.id.btn_speech)).setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));

//            if (isPanelShown) {
//                btnCheck.setText(getResources().getString(R.string.check));
//                findViewById(R.id.btn_check).setEnabled(false);
//                findViewById(R.id.btn_check).setBackgroundResource(R.drawable.primary_button_disable_bg);
////                findViewById(R.id.btn_description).setEnabled(false);
////                findViewById(R.id.btn_description).setBackgroundResource(R.drawable.primary_button_disable_bg);
//                quizAnswerAdapter.notifyDataSetChanged();
//                isSelectedAns = false;
//                isPanelShown = false;
//                count++;
//                loadQuestion();
//            } else {
//                btnCheck.setText(getResources().getString(R.string.next));
////                findViewById(R.id.btn_description).setEnabled(true);
//                findViewById(R.id.btn_check).setEnabled(true);
////                findViewById(R.id.btn_description).setBackgroundResource(R.drawable.primary_button_bg);
//                findViewById(R.id.btn_check).setBackgroundResource(R.drawable.primary_button_bg);
//                isPanelShown = true;
//                isSelectedAns = true;
//                isRowSelected = false;
//                quizAnswerAdapter.notifyDataSetChanged();
//            }

            if (count == questionList.size()) {
                countDownTimer.cancel();
                QueryBuilder quizReportQueryBuilder = daoSession.getUserQuizReportDao().queryBuilder();
                UserQuizReport quizReport = (UserQuizReport) quizReportQueryBuilder.where(UserQuizReportDao.Properties.QuizId.eq(Long.parseLong(getIntent().getExtras().get("quizId").toString()))).unique();
                Intent intent = new Intent(QuizActivity.this, ReportActivity.class);
                if (quizReport == null) {
                    quizReport = new UserQuizReport();
                }
                quizReport.setDuration(timeTaken);
                quizReport.setQuizAttemptTime(new Date());
                quizReport.setScore(rightAns);
                quizReport.setQuizId(Long.parseLong(getIntent().getExtras().get("quizId").toString()));
                daoSession.getUserQuizReportDao().insertOrReplace(quizReport);
                intent.putExtra("quizId", getIntent().getExtras().get("quizId").toString());
                startActivity(intent);
                finish();
            }
        }
    }


    /*Answer recyclerView */

    public class QuizAnswerAdapter extends RecyclerView.Adapter<QuizAnswerAdapter.QuizAnswerViewHolder> {
        protected View itemView;

        @Override
        public int getItemCount() {
            return answerChoiceList.size();
        }

        @Override
        public void onBindViewHolder(final QuizAnswerViewHolder vh, final int i) {
            final AnswerChoice ans = answerChoiceList.get(i);
            vh.textAnsNo.setText(charArray[i] + "");
            if (ans.getImageFlag()) {
                int resId = getResources().getIdentifier(ans.getTextKey() + "_image", "string", getPackageName());
                int imageResId = getResources().getIdentifier(getString(resId), "drawable", getPackageName());
                vh.answerImage.setImageResource(imageResId);
                vh.answerImage.setVisibility(View.VISIBLE);
                vh.textAns.setVisibility(View.GONE);
            } else {
                int resId = getResources().getIdentifier(ans.getTextKey(), "string", getPackageName());
                vh.textAns.setText(resId);
                vh.answerImage.setVisibility(View.GONE);
                vh.textAns.setVisibility(View.VISIBLE);
                ansText = ansText.concat(" " + getResources().getString(R.string.option_is) +"  "+ vh.textAnsNo.getText().toString() + ". " + vh.textAns.getText().toString() + ". ");
            }
//            int resId = getResources().getIdentifier(ans.getTextKey(), "string", getPackageName());
//            vh.textAns.setText(resId);

            if (ans.getIsAnswerFlag()) {
                answer = vh.textAns.getText().toString();
            }
            vh.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    findViewById(R.id.btn_check).setEnabled(true);
                    findViewById(R.id.btn_check).setBackgroundResource(R.drawable.primary_button_bg);
                    isRowSelected = true;
                    itemPosition = i;
                    quizAnswerAdapter.notifyItemRangeChanged(0, answerChoiceList.size());
                }
            });
            if (isRowSelected) {
                if (itemPosition == i) {
                    vh.textAnsNo.setBackgroundResource(R.color.primary);
                    vh.textAnsNo.setTextColor(getResources().getColor(R.color.white_color));
                    if (ans.getIsAnswerFlag()) {
                        if (isCheckAnswer) {
                            isCheckAnswer = true;
                        } else {
                            isCheckAnswer = true;
                            rightAns++;
                        }
                    } else {
                        isCheckAnswer = true;
                        wrongAns++;
                    }
                } else {
                    vh.textAnsNo.setBackgroundResource(R.color.transparent_color);
                    vh.textAnsNo.setTextColor(getResources().getColor(R.color.secondary_text));
                }


            }
//            if (isSelectedAns) {
//                vh.itemLayout.setEnabled(false);
//                if (itemPosition == i) {
//                    if (ans.getIsAnswerFlag()) {
//                        vh.textAnsNo.setBackgroundResource(R.color.green_color);
//                        isCheckAnswer = true;
//                        rightAns++;
//                    } else {
//                        vh.textAnsNo.setBackgroundResource(R.color.wrong_answer_color);
//                        isCheckAnswer = false;
//                        wrongAns++;
//                    }
//                } else if (ans.getIsAnswerFlag()) {
//                    vh.textAnsNo.setBackgroundResource(R.color.green_color);
//                }
//            }

        }

        @Override
        public QuizAnswerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.answer_list_item, viewGroup, false);
            return new QuizAnswerViewHolder(itemView);
        }

        public class QuizAnswerViewHolder extends RecyclerView.ViewHolder {
            protected TextView textAnsNo, textAns;
            protected LinearLayout itemLayout;
            protected ImageView answerImage;

            public QuizAnswerViewHolder(View v) {
                super(v);
                textAnsNo = (TextView) v.findViewById(R.id.ans_no);
                textAns = (TextView) v.findViewById(R.id.text_ans);
                itemLayout = (LinearLayout) v.findViewById(R.id.item_layout);
                answerImage = (ImageView) v.findViewById(R.id.answer_image);

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
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);

        builder.setMessage(getResources().getString(R.string.are_you_want_to_quit))
                .setPositiveButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        builder.show();


    }


    // CountDownTimer class
    public class MalibuCountDownTimer extends CountDownTimer {
        public MalibuCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            qusTimer.setText(getResources().getString(R.string.time_up));
            findViewById(R.id.btn_check).setEnabled(false);
            findViewById(R.id.btn_check).setBackgroundResource(R.drawable.primary_button_disable_bg);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int secs = (int) (millisUntilFinished / 1000);
            long totalDuration = startTime / 1000 - secs;
            timeTaken = (int) totalDuration;
            int mins = secs / 60;
            int hour = 0;
            if (mins == 60) {
                hour = +1;
            }
            secs = secs % 60;
            qusTimer.setText(hour + ":" + mins + ":"
                    + String.format("%02d", secs));
        }

    }

//    @Override
//    public void onInit(int status) {
//        // TODO Auto-generated method stub
//
//        if (status == TextToSpeech.SUCCESS) {
//            int result;
//            if (SessionUtil.getSelectLanguage(QuizActivity.this).equals("english")) {
//                result = textToSpeech.setLanguage(Locale.US);
//            } else {
//                result = textToSpeech.setLanguage(new Locale("hi"));
//            }
//            result = textToSpeech.setOnUtteranceCompletedListener(QuizActivity.this);
//            // tts.setPitch(5); // set pitch level
//
//            // tts.setSpeechRate(2); // set speech speed rate
//
//            if (result == TextToSpeech.LANG_MISSING_DATA
//                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS", "Language is not supported");
//            } else {
////                    btnSpeech.setEnabled(true);
////                speakOut();
//            }
//
//        } else {
//            Log.e("TTS", "Initilization Failed");
//        }
//
//    }

    @Override
    protected void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown!
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

//    public void stopAudio() {
//        if (isQuestionRead) {
//            if (textToSpeech != null) {
//                textToSpeech.stop();
//            }
//            isQuestionRead = false;
//            ((FloatingActionButton) findViewById(R.id.btn_speech)).setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
//        } else {
//            speakOut();
//            ((FloatingActionButton) findViewById(R.id.btn_speech)).setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_silent));
//        }
//    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void speakOut() {
//        textSpeech = textQuestion.getText().toString() + ansText;
//        textToSpeech.setPitch((float) 1);
//        textToSpeech.setSpeechRate((float) 1);
//        textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null, null);
//        isQuestionRead = true;
//    }


@Override
public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CHECK_TTS_ACTIVITY_ID) {
        checkTestToSpeechResult(QuizActivity.this, resultCode);
        return;
    }
}

    public void checkTextToSpeech(Activity activity, int activityId) {
        if (textToSpeech == null) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Intent checkIntent = new Intent();
                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                activity.startActivityForResult(checkIntent, activityId);
            } else {
                textToSpeech = new TextToSpeech(activity, mOnInitListener);
            }
        }
    }

    private TextToSpeech.OnInitListener mOnInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result;
                if (SessionUtil.getSelectLanguage(QuizActivity.this).equals("hindi")) {
                    result = textToSpeech.setLanguage(new Locale("hi"));
                } else {
                    result = textToSpeech.setLanguage(new Locale("en", "IN"));
                }
                result = textToSpeech.setOnUtteranceCompletedListener(QuizActivity.this);
            }
        }
    };

    public void checkTestToSpeechResult(Context context, int resultCode) {
        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            textToSpeech = new TextToSpeech(context, mOnInitListener);
        }
    }

    public void speak() {
        textSpeech = textQuestion.getText().toString() + ansText;
        if (textToSpeech != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end");
            textToSpeech.setPitch((float) 1);
            textToSpeech.setSpeechRate((float) 1);
            textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_ADD, params);
            isQuestionRead = true;
        }
    }

    public void stopAudio() {
        if (isQuestionRead) {
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
            isQuestionRead = false;
            ((FloatingActionButton) findViewById(R.id.btn_speech)).setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
        } else {
            speak();
            ((FloatingActionButton) findViewById(R.id.btn_speech)).setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_silent));
        }
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        isQuestionRead = false;
        ((FloatingActionButton) findViewById(R.id.btn_speech)).setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
    }

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("Quiz Activity");
    }

}
