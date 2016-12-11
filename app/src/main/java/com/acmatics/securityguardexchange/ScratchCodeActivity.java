package com.acmatics.securityguardexchange;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.bean.UserBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Rubal on 03-12-2015.
 */
public class ScratchCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolBar;
    private RelativeLayout freeTrial;
    private AQuery aq;
    private Map<String, Object> code;
    InternetConnectionDetector internetConnectionDetector;
    private EditText scratchCodeText;
    private TextView validPhoneNo;
    Dialog forgotScratchCodeDialog;
    private SecuritySkillsApplication app;
    private ProgressDialog pDialog;
    private VideoView videoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ConversionHelper.setAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scratch_code);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.scratch_code));
        setSupportActionBar(toolBar);
        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        aq = new AQuery(this);
        inflateXmlData();


    }
    public void inflateXmlData(){
        scratchCodeText = (EditText) findViewById(R.id.scratchCodeText);
        findViewById(R.id.freeTrialBtn).setOnClickListener(this);
        findViewById(R.id.btnScratch).setOnClickListener(this);
        findViewById(R.id.buyScratchCode).setOnClickListener(this);
        findViewById(R.id.forgotScratchCode).setOnClickListener(this);

        String trialDate = SessionUtil.getTrialStartDate(ScratchCodeActivity.this);


        if (!TextUtils.isEmpty(trialDate)) {
            Long trailDateMillis = Long.parseLong(trialDate);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(trailDateMillis);
            Calendar CurrentCal = Calendar.getInstance();
            int testCount = daysBetween(cal, CurrentCal);
            TextView remainingDays = (TextView) findViewById(R.id.tv_remaining_days);
            if (testCount <= 5) {
                int days = 5;
                days = days - testCount;
                if (days == 1) {

                    remainingDays.setText(days + " " + getResources().getString(R.string.day_remaining_in_your_trial_period));
                } else {
                    remainingDays.setText(days + " " + getResources().getString(R.string.day_remaining_in_your_trial_period));
                }
            } else {
                freeTrial = (RelativeLayout) findViewById(R.id.freeTrialBtn);
                freeTrial.setEnabled(false);
                freeTrial.setBackgroundColor(getResources().getColor(R.color.separator_color));
                remainingDays.setText(getResources().getString(R.string.expired_trial_period));
                remainingDays.setTextColor(getResources().getColor(R.color.secondary_text));
            }
        }
    }

    @Override
    protected void onResume() {
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(getApplicationContext());
        if (!prefs.contains(SessionUtil.IS_FIRST_RUN)) {
            SessionUtil.saveFirstFollow(ScratchCodeActivity.this, "firstRun");
            Intent i = new Intent(ScratchCodeActivity.this, FullViewVideo.class);
            i.putExtra("urlId", getResources().getString(R.string.main_guide_line_url));
            startActivityForResult(i,1);
        }
        super.onResume();
        app.trackActivity("Scratch Code Activity");
    }

   /* @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showFollowDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_video);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        SessionUtil.saveFirstFollow(ScratchCodeActivity.this, "firstRun");
        dialog.setCancelable(false);
        videoview = (VideoView) dialog.findViewById(R.id.VideoView);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);

        try {
            MediaController mediacontroller = new MediaController(ScratchCodeActivity.this);
            mediacontroller.setAnchorView(videoview);
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
        dialog.findViewById(R.id.btnSkip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
*/
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
    public void onClick(View v) {
        String userId = SessionUtil.getUserId(this);
        if (v.getId() == R.id.btnScratch) {
//            Intent intent = new Intent(ScratchCodeActivity.this, QuizListActivity.class);
//            startActivity(intent);
            if (validateUser(scratchCodeText)) {
                String text = scratchCodeText.getText().toString();
                verifyScratchCode(text);
                SessionUtil.saveScratchCode(text, this);

            }
//            Log.e("text===",scratchCodeText.getText().toString());
        } else if (v.getId() == R.id.buyScratchCode) {
//            Intent i = new Intent(ScratchCodeActivity.this, SignUpActivity.class);
//            i.putExtra("buyNow","buyNow");
//            startActivity(i);
            if (!userId.equals("") && !userId.equals(null)) {
                showPaymentDialog();
            } else {
                SessionUtil.saveIndentifyUserRegistered("1", this);
                signUp(null);
            }

        } else if (v.getId() == R.id.buy_online) {
            Intent intent = new Intent(ScratchCodeActivity.this, OnlinePaymentActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.buy_affiliates) {
            Intent intent = new Intent(this, SearchAffiliatesActivity.class);
            startActivity(intent);

        } else if (v.getId() == R.id.bank_deposit) {
            Intent intent = new Intent(ScratchCodeActivity.this, BankDepositActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.forgotScratchCode) {
            showForgotCodeDialog();
        } else if (v.getId() == R.id.freeTrialBtn) {
            if (!userId.equals("") && !userId.equals(null)) {
                startTrial(userId);
            } else {
                SessionUtil.saveStartTrial("1", this);
                signUp(null);
            }
        } else if (v.getId() == R.id.btn_get_code) {
            if (validateUser((EditText) forgotScratchCodeDialog.findViewById(R.id.forgotScratchCodeText))) {
                forgotScratchCode(validPhoneNo.getText().toString());
            }
        }
    }

    /*This Dialog use for payment option */
    public void showPaymentDialog() {
        Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.custom_payment_dialog);
        paymentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        paymentDialog.findViewById(R.id.buy_online).setOnClickListener(this);
        paymentDialog.findViewById(R.id.buy_affiliates).setOnClickListener(this);
        paymentDialog.findViewById(R.id.bank_deposit).setOnClickListener(this);
        paymentDialog.show();

    }

    /* This dialog use for forgot scratch code */
    public void showForgotCodeDialog() {
        forgotScratchCodeDialog = new Dialog(this);
        forgotScratchCodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        forgotScratchCodeDialog.setContentView(R.layout.custom_forgotcode_dialog);
        forgotScratchCodeDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        validPhoneNo = (TextView) forgotScratchCodeDialog.findViewById(R.id.forgotScratchCodeText);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.FLAG_SPLIT_TOUCH);
        forgotScratchCodeDialog.findViewById(R.id.btn_get_code).setOnClickListener(this);
        forgotScratchCodeDialog.show();
    }

    /* this is use for Text field validation */
    public boolean validateUser(EditText scratchCodeText) {
        boolean isValid = true;
//        String phoneNo = validPhoneNo.getText().toString();
        if ("".equals(scratchCodeText.getText().toString())) {
            scratchCodeText.setError(getResources().getString(R.string.please_enter_valid_scratch_code));
            isValid = false;
        }
//         else if ("".equals(phoneNo)) {
//            validPhoneNo.setError("Please enter valid phone no");
//            isValid = false;
//        }
        return isValid;
    }

    private void verifyScratchCode(final String scratchCode) {
        String url = UrlConstants.BASE_URL + UrlConstants.VERIFY_SCRATCH_CODE;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.verify_scratch_code));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        Map code = new HashMap<>();
        code.put("scratchCode", scratchCode
        );
        aq.progress(progress).ajax(url, code, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(ScratchCodeActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(ScratchCodeActivity.this);
                    }
                } else if (json != null) {
                    List<UserBean> userDataList = ConversionHelper.userVerification(json);
                    if (userDataList.get(0).isScratchCodeVerified()) {
                        SessionUtil.scratchCodeVerify("1", ScratchCodeActivity.this);
                        if (!userDataList.get(0).isUserRegistered()) {
                            signUp(scratchCode);
                        } else {
                            generateOtp(userDataList.get(0).getPhone());
                            try {
                                SessionUtil.saveActivationDate(json.getString("activationDate"), ScratchCodeActivity.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            /*try {
                                SessionUtil.saveUserId(json.getInt("userId") + "", ScratchCodeActivity.this);
                                SessionUtil.saveUserName(json.getString("userName"), ScratchCodeActivity.this);
                                SessionUtil.saveActivationDate(json.getString("activationDate"), ScratchCodeActivity.this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            SessionUtil.userRegistered("1", ScratchCodeActivity.this);
                            Intent i = new Intent(ScratchCodeActivity.this, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();*/
                        }
                    } else {
                        Toast.makeText(ScratchCodeActivity.this, getResources().getString(R.string.not_valid_scratch_code), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }

    private void forgotScratchCode(String phoneNo) {
        String url = UrlConstants.BASE_URL + UrlConstants.FORGOT_SCRATCH_CODE;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.sending_code_Please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        Map code = new HashMap<>();
        code.put("phoneNumber", phoneNo);
        aq.progress(progress).ajax(url, code, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(ScratchCodeActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(ScratchCodeActivity.this);
                    }
                } else if (json != null) {


                    try {

                        if (json.has("scratchCodeNotFound") && json.getBoolean("scratchCodeNotFound")) {
                            ((EditText) forgotScratchCodeDialog.findViewById(R.id.forgotScratchCodeText)).setError(getResources().getString(R.string.scratch_code_not_found));
                        } else {
                            Toast.makeText(ScratchCodeActivity.this, getResources().getString(R.string.scratch_code_sms_sent), Toast.LENGTH_LONG).show();
                            forgotScratchCodeDialog.cancel();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    if (scratchStatus) {
//                        Intent intent = new Intent(ScratchCodeActivity.this, QuizListActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                }
            }
        });
    }

    private void startTrial(String userId) {
        String url = UrlConstants.BASE_URL + UrlConstants.START_TRIAL;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.start_trial_please_wait));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        Map code = new HashMap<>();
        code.put("userId", userId);
        aq.progress(progress).ajax(url, code, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(ScratchCodeActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(ScratchCodeActivity.this);
                    }
                } else if (json != null) {
                    try {
                        Long trialDate = json.getLong("trialStartDate");
                        SessionUtil.saveTrialStartDate(trialDate + "", ScratchCodeActivity.this);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent home = new Intent(ScratchCodeActivity.this, HomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                }
            }
        });
    }

    public void signUp(String scratchCode) {
        Intent i = new Intent(ScratchCodeActivity.this, VerifyNumberActivity.class);
        i.putExtra("scratchCode", scratchCode);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1){
            ConversionHelper.setAppLanguage(this);
           setContentView(R.layout.activity_scratch_code);
            toolBar = (Toolbar) findViewById(R.id.toolBar);
            toolBar.setTitleTextColor(Color.WHITE);
            toolBar.setTitle(getResources().getString(R.string.scratch_code));
            setSupportActionBar(toolBar);
            internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
            aq = new AQuery(this);
            inflateXmlData();
        }
    }

    private void generateOtp(final String phnNumber) {
        String url = UrlConstants.BASE_URL + UrlConstants.GENERATE_OTP;
        HashMap params = new HashMap();
        params.put("phoneNumber", phnNumber);
       ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.generating_otp));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        aq.progress(progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (json != null) {
                    if (json.has("phoneNumberAlreadyRegister")) {
                        findViewById(R.id.otpError).setVisibility(View.VISIBLE);
                    } else {
                        Intent i = new Intent(ScratchCodeActivity.this, VerifyOtpActivity.class);
                        i.putExtra("scratchCode", scratchCodeText.getText().toString());
                        i.putExtra("phoneNumber", phnNumber);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(ScratchCodeActivity.this, getResources().getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
