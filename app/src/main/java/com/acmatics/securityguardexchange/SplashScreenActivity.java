package com.acmatics.securityguardexchange;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.dao.AnswerChoice;
import com.acmatics.securityguardexchange.dao.AnswerChoiceDao;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.Question;
import com.acmatics.securityguardexchange.dao.QuestionDao;
import com.acmatics.securityguardexchange.dao.QuestionLevel;
import com.acmatics.securityguardexchange.dao.QuestionType;
import com.acmatics.securityguardexchange.dao.Quiz;
import com.acmatics.securityguardexchange.dao.QuizQuestion;
import com.acmatics.securityguardexchange.dao.QuizType;
import com.acmatics.securityguardexchange.dao.TestQuestionMapping;
import com.acmatics.securityguardexchange.dao.UserTest;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import de.greenrobot.dao.query.QueryBuilder;


public class SplashScreenActivity extends Activity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private String currentVersion;
    InternetConnectionDetector internetConnectionDetector;
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        aq = new AQuery(this);

        setAppLanguage();
        if (checkPlayServices()) {
            new MasterDataTask().execute();
            if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
                Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                        "", null));
            }
        }
        ImageView Image = (ImageView) findViewById(R.id.ImageView2_Left);
        RotateAnimation r;
        r = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration(4000);
        r.setRepeatCount(0);
        Image.startAnimation(r);
        Thread background = new Thread() {
            public void run() {
                try {
                    sleep(4 * 1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.progress).setVisibility(View.VISIBLE);
                            findViewById(R.id.loadingText).setVisibility(View.VISIBLE);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        background.start();
        try {
            currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void gotoNextScreen() {
        String userRegistered = SessionUtil.getUserRegistered(SplashScreenActivity.this);
        String scratchCodeVerify = SessionUtil.getScratchCodeVerify(SplashScreenActivity.this);
        String userId = SessionUtil.getUserId(SplashScreenActivity.this);
        if (userRegistered.equalsIgnoreCase("1") && scratchCodeVerify.equalsIgnoreCase("1")) {
            Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        } else if (!userId.equalsIgnoreCase("")) {
            Intent splashIntent = new Intent(SplashScreenActivity.this, ScratchCodeActivity.class);
            startActivity(splashIntent);
            finish();
        } else {
            Intent splashIntent = new Intent(SplashScreenActivity.this, SelectLanguageActivity.class);
            splashIntent.putExtra("home", "splash");
            startActivity(splashIntent);
            finish();
        }
    }

    private void validateUserAndApp() {
        if (internetConnectionDetector.isConnectingToInternet() && !"".equalsIgnoreCase(SessionUtil.getValueForKey(this, SessionUtil.USER_ID)) && !"".equalsIgnoreCase(SessionUtil.getValueForKey(this, SessionUtil.DEVICE_ID))) {
            String url = UrlConstants.BASE_URL + UrlConstants.VALIDATE_DEVICE;
            Map<String, String> params = new HashMap<>();
            params.put("deviceId", SessionUtil.getValueForKey(this, SessionUtil.DEVICE_ID));
            params.put("userId", SessionUtil.getValueForKey(this, SessionUtil.USER_ID));
            aq.ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json != null) {
                        try {
                            if (json.getBoolean("deviceValid")) {
                                checkAppVersion(json.getString("andriodAppVersion"));
                            } else {
                                File booksDirectory = new File(Environment.getExternalStorageDirectory() + "/books");
                                if (booksDirectory.exists()) {
                                    try {
                                        delete(booksDirectory);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                SessionUtil.saveValueForKey(SplashScreenActivity.this, SessionUtil.USER_REGISTERED, "");
                                SessionUtil.saveValueForKey(SplashScreenActivity.this, SessionUtil.SCRATCH_CODE_VERIFY, "");
                                SessionUtil.saveValueForKey(SplashScreenActivity.this, SessionUtil.USER_ID, "");

                                checkAppVersion(json.getString("andriodAppVersion"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } else {
            gotoNextScreen();
        }
    }

    void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    private void checkAppVersion(String latestVersion) {
        if (BuildConfig.VERSION_NAME.equalsIgnoreCase(latestVersion)) {
            gotoNextScreen();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this);
            builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        //if the user agrees to upgrade
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(android.content.Intent.ACTION_VIEW);
                            i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.acmatics.securitygaurdexchange&hl=en"));
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            gotoNextScreen();
                        }
                    });
            //show the alert message
            builder.create().show();
        }
    }

    private void testQuestion() {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(SplashScreenActivity.this, DatabaseConstants.DATABASE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        final DaoSession daoSession = daoMaster.newSession();
        QueryBuilder questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
        List<Question> questions = questionQueryBuilder.list();
        for (Question question : questions) {
            int resId = getResources().getIdentifier(question.getTextKey(), "string", getPackageName());
            if (resId == 0) {
                System.out.print("Ques Id : " + question.getQuestionId());
            }
            resId = getResources().getIdentifier(question.getDescriptionKey(), "string", getPackageName());
            if (resId == 0) {
                System.out.print("Ques Id : " + question.getQuestionId());
            }
            resId = getResources().getIdentifier(question.getTextKey() + "_image", "string", getPackageName());
            if (resId == 0 && question.getImageFlag()) {
                System.out.print("Ques Id : " + question.getQuestionId());
            }
        }
        List<AnswerChoice> answers = daoSession.getAnswerChoiceDao().queryBuilder().list();
        for (AnswerChoice answer : answers) {
            int resId = getResources().getIdentifier(answer.getTextKey(), "string", getPackageName());
            if (resId == 0 && !answer.getImageFlag()) {
                System.out.print("Ques Id : " + answer.getQuestionId());
            }
            if (answer.getImageFlag()) {
                resId = getResources().getIdentifier(answer.getTextKey() + "_image", "string", getPackageName());

                if (resId == 0) {
                    System.out.print("Ques Id : " + answer.getQuestionId());
                }
            }
        }
    }

    private void startAnimating() {

        TextView logo2 = (TextView) findViewById(R.id.titlenametwo);
        Animation fade2 = AnimationUtils.loadAnimation(this, R.anim.fade_in2);
        logo2.startAnimation(fade2);
        TextView logo3 = (TextView) findViewById(R.id.titlenamethree);
        Animation fade3 = AnimationUtils.loadAnimation(this, R.anim.fade_in3);
        logo3.startAnimation(fade3);
        TextView logo4 = (TextView) findViewById(R.id.titlenamefour);
        Animation fade4 = AnimationUtils.loadAnimation(this, R.anim.fade_in4);
        logo4.startAnimation(fade4);

        // Transition to Main Menu when bottom title finishes animating
        fade4.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationEnd(Animation animation) {
                // The animation has ended, transition to the Main Menu screen
                /*startActivity(new Intent(SplashScreenActivity.this,
                        LanguageActivity.class));
                SplashActivity.this.finish();*/

            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        // Load animations for all views within the TableLayout
//        Animation spinin = AnimationUtils.loadAnimation(this,
//                R.anim.custom_anim);
//        LayoutAnimationController controller = new LayoutAnimationController(spinin);
//
//        TableLayout table = (TableLayout) findViewById(R.id.TableLayout01);
//        for (int i = 0; i < table.getChildCount(); i++) {
//            TableRow row = (TableRow) table.getChildAt(i);
//            row.setLayoutAnimation(controller);
//        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                final String appPackageName = getPackageName();
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1005).show();
            } else if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(getLocalClassName(), "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void generateTestsIfNeeded(DaoSession daoSession) {
        List<UserTest> testList = daoSession.getUserTestDao().loadAll();

        List<Integer> levelOneTypeOneExclusions = new ArrayList<>();
        QueryBuilder questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
        List<Question> levelOneTypeOneQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.eq(1l), QuestionDao.Properties.QuestionTypeId.eq(1l))).list();

        List<Integer> levelOneTypeTwoExclusions = new ArrayList<>();
        questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
        List<Question> levelOneTypeTwoQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.eq(1l), QuestionDao.Properties.QuestionTypeId.eq(2l))).list();

        if (testList.size() == 0) {
            List<Integer> levelTwoTypeOneExclusions = new ArrayList<>();
            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> levelTwoTypeOneQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.eq(2l), QuestionDao.Properties.QuestionTypeId.eq(1l))).list();

            List<Integer> levelTwoTypeTwoExclusions = new ArrayList<>();
            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> levelTwoTypeTwoQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.eq(2l), QuestionDao.Properties.QuestionTypeId.eq(2l))).list();

            List<Integer> levelThreeTypeOneExclusions = new ArrayList<>();
            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> levelThreeTypeOneQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.eq(3l), QuestionDao.Properties.QuestionTypeId.eq(1l))).list();

            List<Integer> levelThreeTypeTwoExclusions = new ArrayList<>();
            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> levelThreeTypeTwoQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.eq(3l), QuestionDao.Properties.QuestionTypeId.eq(2l))).list();

            Random r = new Random();
            for (int i = 1; i <= 100; i++) {
                long testId = daoSession.getUserTestDao().insert(new UserTest(null, 15));

                for (int j = 0; j < 6; j++) {
                    long questionId = -1;
                    if (i <= 30) {
                        int qId = getRandomWithExclusion(r, 1, 180, levelOneTypeOneExclusions);
                        levelOneTypeOneExclusions.add(qId);
                        Collections.sort(levelOneTypeOneExclusions);

                        questionId = levelOneTypeOneQuestions.get(qId - 1).getQuestionId();
                    } else if (i <= 60) {
                        int qId = getRandomWithExclusion(r, 1, 180, levelTwoTypeOneExclusions);
                        levelTwoTypeOneExclusions.add(qId);
                        Collections.sort(levelTwoTypeOneExclusions);

                        questionId = levelTwoTypeOneQuestions.get(qId - 1).getQuestionId();
                    } else {
                        int qId = getRandomWithExclusion(r, 1, 240, levelThreeTypeOneExclusions);
                        levelThreeTypeOneExclusions.add(qId);
                        Collections.sort(levelThreeTypeOneExclusions);

                        questionId = levelThreeTypeOneQuestions.get(qId - 1).getQuestionId();
                    }
                    daoSession.getTestQuestionMappingDao().insert(new TestQuestionMapping(null, questionId, testId));
                }

                for (int j = 0; j < 4; j++) {
                    long questionId = -1;
                    if (i <= 30) {
                        int qId = getRandomWithExclusion(r, 1, 120, levelOneTypeTwoExclusions);
                        levelOneTypeTwoExclusions.add(qId);
                        Collections.sort(levelOneTypeTwoExclusions);

                        questionId = levelOneTypeTwoQuestions.get(qId - 1).getQuestionId();
                    } else if (i <= 60) {
                        int qId = getRandomWithExclusion(r, 1, 120, levelTwoTypeTwoExclusions);
                        levelTwoTypeTwoExclusions.add(qId);
                        Collections.sort(levelTwoTypeTwoExclusions);

                        questionId = levelTwoTypeTwoQuestions.get(qId - 1).getQuestionId();
                    } else {
                        int qId = getRandomWithExclusion(r, 1, 160, levelThreeTypeTwoExclusions);
                        levelThreeTypeTwoExclusions.add(qId);
                        Collections.sort(levelThreeTypeTwoExclusions);

                        questionId = levelThreeTypeTwoQuestions.get(qId - 1).getQuestionId();
                    }
                    daoSession.getTestQuestionMappingDao().insert(new TestQuestionMapping(null, questionId, testId));
                }
            }
        }

        List<Quiz> quizList = daoSession.getQuizDao().loadAll();
        if (quizList.size() == 0) {
            long quizId = daoSession.getQuizDao().insert(new Quiz(null, null, 1l, "30 days", "", 45, 30));
            List<Integer> typeOneExclusion = new ArrayList<>();
            List<Integer> typeTwoExclusions = new ArrayList<>();

            Random r = new Random();
            for (int j = 0; j < 18; j++) {
                long questionId = -1;
                int qId = getRandomWithExclusion(r, 1, 180, typeOneExclusion);
                typeOneExclusion.add(qId);
                Collections.sort(typeOneExclusion);

                questionId = levelOneTypeOneQuestions.get(qId - 1).getQuestionId();
                daoSession.getQuizQuestionDao().insert(new QuizQuestion(null, quizId, questionId));
            }

            for (int j = 0; j < 12; j++) {
                long questionId = -1;
                int qId = getRandomWithExclusion(r, 1, 120, typeTwoExclusions);
                typeTwoExclusions.add(qId);
                Collections.sort(typeTwoExclusions);

                questionId = levelOneTypeTwoQuestions.get(qId - 1).getQuestionId();
                daoSession.getQuizQuestionDao().insert(new QuizQuestion(null, quizId, questionId));
            }

            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> quizTwoTypeOneQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.notEq(3l), QuestionDao.Properties.QuestionTypeId.eq(1l))).list();

            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> quizTwoTypeTwoQuestions = questionQueryBuilder.where(questionQueryBuilder.and(QuestionDao.Properties.QuestionLevelId.notEq(3l), QuestionDao.Properties.QuestionTypeId.eq(2l))).list();

            quizId = daoSession.getQuizDao().insert(new Quiz(null, null, 2l, "60 days", "", 45, 60));
            typeOneExclusion = new ArrayList<>();
            typeTwoExclusions = new ArrayList<>();

            for (int j = 0; j < 36; j++) {
                long questionId = -1;
                int qId = getRandomWithExclusion(r, 1, 360, typeOneExclusion);
                typeOneExclusion.add(qId);
                Collections.sort(typeOneExclusion);

                questionId = quizTwoTypeOneQuestions.get(qId - 1).getQuestionId();
                daoSession.getQuizQuestionDao().insert(new QuizQuestion(null, quizId, questionId));
            }

            for (int j = 0; j < 24; j++) {
                long questionId = -1;
                int qId = getRandomWithExclusion(r, 1, 240, typeTwoExclusions);
                typeTwoExclusions.add(qId);
                Collections.sort(typeTwoExclusions);

                questionId = quizTwoTypeTwoQuestions.get(qId - 1).getQuestionId();
                daoSession.getQuizQuestionDao().insert(new QuizQuestion(null, quizId, questionId));
            }

            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> quizThreeTypeOneQuestions = questionQueryBuilder.where(QuestionDao.Properties.QuestionTypeId.eq(1l)).list();

            questionQueryBuilder = daoSession.getQuestionDao().queryBuilder();
            List<Question> quizThreeTypeTwoQuestions = questionQueryBuilder.where(QuestionDao.Properties.QuestionTypeId.eq(2l)).list();

            quizId = daoSession.getQuizDao().insert(new Quiz(null, null, 3l, "100 days", "", 60, 100));
            typeOneExclusion = new ArrayList<>();
            typeTwoExclusions = new ArrayList<>();

            for (int j = 0; j < 60; j++) {
                long questionId = -1;
                int qId = getRandomWithExclusion(r, 1, 600, typeOneExclusion);
                typeOneExclusion.add(qId);
                Collections.sort(typeOneExclusion);

                questionId = quizThreeTypeOneQuestions.get(qId - 1).getQuestionId();
                daoSession.getQuizQuestionDao().insert(new QuizQuestion(null, quizId, questionId));
            }

            for (int j = 0; j < 40; j++) {
                long questionId = -1;
                int qId = getRandomWithExclusion(r, 1, 400, typeTwoExclusions);
                typeTwoExclusions.add(qId);
                Collections.sort(typeTwoExclusions);

                questionId = quizThreeTypeTwoQuestions.get(qId - 1).getQuestionId();
                daoSession.getQuizQuestionDao().insert(new QuizQuestion(null, quizId, questionId));
            }
        }
    }

    private int getRandomWithExclusion(Random rnd, int start, int end, List<Integer> excludes) {
        int random = start + rnd.nextInt(end - start + 1 - excludes.size());
        while (excludes.contains(random)) {
            int index = excludes.indexOf(random);
            if (index == random - 1) {
                random++;
            } else {
                random--;
            }
        }
        return random;
    }

    private class MasterDataTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(SplashScreenActivity.this, DatabaseConstants.DATABASE_NAME, null);
            SQLiteDatabase db = helper.getWritableDatabase();
            DaoMaster daoMaster = new DaoMaster(db);
            final DaoSession daoSession = daoMaster.newSession();

            if (daoSession.getQuestionTypeDao().queryBuilder().list().size() == 0) {
                //master data for Question type
                daoSession.getQuestionTypeDao().insert(new QuestionType(1l, "True/False", "True/False"));
                daoSession.getQuestionTypeDao().insert(new QuestionType(2l, "MCQ", "MCQ"));

                //master data for Question level
                daoSession.getQuestionLevelDao().insert(new QuestionLevel(1l, "30 Days", "30 Days"));
                daoSession.getQuestionLevelDao().insert(new QuestionLevel(2l, "60 Days", "60 Days"));
                daoSession.getQuestionLevelDao().insert(new QuestionLevel(3l, "100 Days", "100 Days"));

                daoSession.getQuizTypeDao().insert(new QuizType(1l, "30 Days", "30 Days"));
                daoSession.getQuizTypeDao().insert(new QuizType(2l, "60 Days", "60 Days"));
                daoSession.getQuizTypeDao().insert(new QuizType(3l, "100 Days", "100 Days"));

                InputStream inputStream = getResources().openRawResource(R.raw.questions);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                boolean firstLine = true;
                String line = "";
                try {
                    QuestionDao questionDao = daoSession.getQuestionDao();
                    while ((line = reader.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue;
                        }
                        String[] str = line.split(",");
                        questionDao.insert(new Question(Long.parseLong(str[0]), str[1], str[2], Long.parseLong(str[3]), Long.parseLong(str[4]), "1".equals(str[5])));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                inputStream = getResources().openRawResource(R.raw.answers);
                reader = new BufferedReader(new InputStreamReader(inputStream));
                firstLine = true;
                line = "";
                try {
                    AnswerChoiceDao answerChoiceDao = daoSession.getAnswerChoiceDao();
                    while ((line = reader.readLine()) != null) {
                        if (firstLine) {
                            firstLine = false;
                            continue;
                        }
                        String[] str = line.split(",");
                        answerChoiceDao.insert(new AnswerChoice(Long.parseLong(str[0]), str[1], Long.parseLong(str[2]), "1".equals(str[3]), "1".equals(str[4])));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                generateTestsIfNeeded(daoSession);
                validateUserAndApp();
            } else {
                Thread background = new Thread() {
                    public void run() {
                        try {
                            sleep(3 * 1000);
                            generateTestsIfNeeded(daoSession);
                            validateUserAndApp();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                background.start();
            }

            return null;
        }
    }

    public void setAppLanguage() {
        if (SessionUtil.getSelectLanguage(SplashScreenActivity.this).equals("english")) {
            Locale localeEnglish = new Locale("en");
            Locale.setDefault(localeEnglish);
            Configuration configEnglish = new Configuration();
            configEnglish.locale = localeEnglish;
            getBaseContext().getResources().updateConfiguration(configEnglish, getBaseContext().getResources().getDisplayMetrics());
            startAnimating();
        } else if(SessionUtil.getSelectLanguage(SplashScreenActivity.this).equals("punjabi")) {
            Locale localePunjabi = new Locale("pn");
            Locale.setDefault(localePunjabi);
            Configuration configPunjabi = new Configuration();
            configPunjabi.locale = localePunjabi;
            getBaseContext().getResources().updateConfiguration(configPunjabi, getBaseContext().getResources().getDisplayMetrics());
            startAnimating();
        }else{
            Locale localeHindi = new Locale("hi");
            Locale.setDefault(localeHindi);
            Configuration configHindi = new Configuration();
            configHindi.locale = localeHindi;
            getBaseContext().getResources().updateConfiguration(configHindi, getBaseContext().getResources().getDisplayMetrics());
            startAnimating();

        }
    }


}
