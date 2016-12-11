package com.acmatics.securityguardexchange.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acmatics.securityguardexchange.R;
import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.dao.AnswerChoice;
import com.acmatics.securityguardexchange.dao.AnswerChoiceDao;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.Question;
import com.acmatics.securityguardexchange.util.SessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by kaira on 5/25/2016.
 */
public class SinglePageInteractiveFragment extends Fragment implements  TextToSpeech.OnUtteranceCompletedListener {
    private int position;
    private int count = 0;
    private TextView textQuestion, qusNo;
    private int itemPosition;
    private boolean isSelectedAns;
    private FloatingActionButton fab;

    private QuizAnswerAdapter quizAnswerAdapter;
    private RecyclerView ansRecyclerView;

    DaoMaster.DevOpenHelper helper;
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

    private char[] charArray;

    private ImageView questionImage;

    private Integer timeTaken;
    private final long startTime = 30 * 60 * 1000;
    private final long interval = 1000;

    private Button btnSpeech;
    private TextToSpeech textToSpeech;
    private String ansText = "";
    private String textSpeech;
    private String rightAnswer;
    private final static int CHECK_TTS_ACTIVITY_ID = 2;
    private boolean isQuestionRead = false;
    private ViewGroup rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_interective_session, container, false);
        inflateXml();
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });
        checkTextToSpeech(getActivity(), CHECK_TTS_ACTIVITY_ID);

        helper = new DaoMaster.DevOpenHelper(getActivity(), DatabaseConstants.DATABASE_NAME, null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        questionList = daoSession.getQuestionDao().queryBuilder().list();

        loadQuestion(false);
        return rootView;
    }

    public void setPosition(int position) {
        stopAudio1();
        this.position = position;
    }
    public void inflateXml() {
        textQuestion = (TextView) rootView.findViewById(R.id.textQuestion);
        qusNo = (TextView) rootView.findViewById(R.id.question_no);
        questionImage = (ImageView) rootView.findViewById(R.id.question_image);
        ansRecyclerView = (RecyclerView) rootView.findViewById(R.id.ansRecyclerView);
        charArray = getResources().getString(R.string.answer_option_no).toCharArray();
        rootView.findViewById(R.id.txtCheck).setEnabled(false);
        rootView.findViewById(R.id.txtCheck).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectedAns=true;
                quizAnswerAdapter.notifyDataSetChanged();
            }
        });
    }

    public void loadQuestion(boolean isAns) {
        isSelectedAns=isAns;
        Question question = questionList.get(position);
        int resId = getResources().getIdentifier(question.getTextKey(), "string", getActivity().getPackageName());
        qusNo.setText(position + 1 + " -");
        textQuestion.setText(resId);
        if (question.getImageFlag()) {
            resId = getResources().getIdentifier(question.getTextKey() + "_image", "string", getActivity().getPackageName());
            if (resId != 0) {
                int imageResId = getResources().getIdentifier(getString(resId), "drawable", getActivity().getPackageName());
                questionImage.setImageResource(imageResId);
                questionImage.setVisibility(View.VISIBLE);
            }

        } else {
            questionImage.setVisibility(View.GONE);
        }
        answerChoiceList = daoSession.getAnswerChoiceDao().queryBuilder().where(AnswerChoiceDao.Properties.QuestionId.eq(question.getQuestionId())).list();

        ansRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        quizAnswerAdapter = new QuizAnswerAdapter();
        ansRecyclerView.setAdapter(quizAnswerAdapter);

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
                int resId = getResources().getIdentifier(ans.getTextKey() + "_image", "string", getActivity().getPackageName());
                int imageResId = getResources().getIdentifier(getString(resId), "drawable", getActivity().getPackageName());
                vh.answerImage.setImageResource(imageResId);
                vh.answerImage.setVisibility(View.VISIBLE);
                vh.textAns.setVisibility(View.GONE);
            } else {
                int resId = getResources().getIdentifier(ans.getTextKey(), "string", getActivity().getPackageName());
                vh.textAns.setText(resId);
                vh.answerImage.setVisibility(View.GONE);
                vh.textAns.setVisibility(View.VISIBLE);
                ansText = ansText.concat(" " + getResources().getString(R.string.option_is) +"  "+ vh.textAnsNo.getText().toString() + "." + vh.textAns.getText().toString() + ".");
            }
            if (ans.getIsAnswerFlag()) {
                answer = vh.textAns.getText().toString();
            }

            vh.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isRowSelected = true;
                    itemPosition = i;
                    ansText = "";
                    if (textToSpeech != null) {
                        textToSpeech.stop();
                    }
                    isQuestionRead = false;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
                    quizAnswerAdapter.notifyItemRangeChanged(0, answerChoiceList.size());
                    rootView.findViewById(R.id.txtCheck).setEnabled(true);

                }
            });
            if (isRowSelected) {
                if (itemPosition == i) {
                    vh.textAnsNo.setBackgroundResource(R.color.primary);
                    vh.textAnsNo.setTextColor(getResources().getColor(R.color.white_color));
                } else {
                    vh.textAnsNo.setBackgroundResource(R.color.transparent_color);
                    vh.textAnsNo.setTextColor(getResources().getColor(R.color.secondary_text));
                }

            }
            if (isSelectedAns) {
                vh.itemLayout.setEnabled(false);
                if (itemPosition == i) {
                    if (ans.getIsAnswerFlag()) {
                        vh.textAnsNo.setBackgroundResource(R.color.green_color);
                        isCheckAnswer = true;
                        rightAns++;
                        textSpeech =getResources().getString(R.string.correct_answer);
                        if (textToSpeech != null) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end");
                            textToSpeech.setPitch((float) 1);
                            textToSpeech.setSpeechRate((float) 1);
                            textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_ADD, params);
                        }
                    } else {
                        vh.textAnsNo.setBackgroundResource(R.color.wrong_answer_color);
                        isCheckAnswer = false;
                        wrongAns++;
                        textSpeech = getResources().getString(R.string.incorrect_answer);
                        if (textToSpeech != null) {
                            HashMap<String, String> params = new HashMap<String, String>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end");
                            textToSpeech.setPitch((float) 1);
                            textToSpeech.setSpeechRate((float) 1);
                            textToSpeech.speak(textSpeech, TextToSpeech.QUEUE_ADD, params);
                        }
                    }
//                    showAnsCountColor();
                } else if (ans.getIsAnswerFlag()) {
                    vh.textAnsNo.setBackgroundResource(R.color.green_color);
                }
            }
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
    public void onDestroy() {
        // Don't forget to shutdown!
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_TTS_ACTIVITY_ID) {
            checkTestToSpeechResult(getActivity(), resultCode);
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
                if (SessionUtil.getSelectLanguage(getActivity()).equals("hindi")) {
                    result = textToSpeech.setLanguage(new Locale("hi"));
                } else {
                    result = textToSpeech.setLanguage(new Locale("en", "IN"));
                }
                result = textToSpeech.setOnUtteranceCompletedListener(SinglePageInteractiveFragment.this);
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
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
            SessionUtil.savePractiseTest("silent",getActivity());
        } else {
            SessionUtil.savePractiseTest("read",getActivity());
            speak();
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_silent));
        }
    }

    @Override
    public void onUtteranceCompleted(String uttId) {
        isQuestionRead = false;
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
    }
    public void stopAudio1() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
}







