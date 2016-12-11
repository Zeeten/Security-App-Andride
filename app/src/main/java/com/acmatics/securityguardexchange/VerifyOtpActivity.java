package com.acmatics.securityguardexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rubal on 03-12-2015.
 */
public class VerifyOtpActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private Button verify;
    private EditText otp;
    private AQuery aq;
    String phoneNumber;
    private Map otpData;
    private Map params;
    private ProgressDialog progress;
    private InternetConnectionDetector internetConnectionDetector;
    private TextView resendOtp;
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_verify_otp);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.otp_screen_title));
        setSupportActionBar(toolBar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
//        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        aq = new AQuery(this);
        internetConnectionDetector = new InternetConnectionDetector(this);
        Intent i = getIntent();
        phoneNumber = i.getStringExtra("phoneNumber");
        otpData = new HashMap();
        otpData.put("phoneNumber", phoneNumber);
        otp = (EditText) findViewById(R.id.etOtp);
        verify = (Button) findViewById(R.id.btnVerify);
        resendOtp = (TextView) findViewById(R.id.resendOtp);
        verify.setBackgroundResource(R.drawable.bg_disabled_button);
        verify.setEnabled(false);
        verify.setClickable(false);
        final TextWatcher mTextEditorWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 4) {
                    verify.setBackgroundResource(R.drawable.bg_disabled_button);
                    verify.setEnabled(false);
                    verify.setClickable(false);
                } else if (s.length() == 4) {
                    verify.setBackgroundResource(R.drawable.bg_primary_button);
                    verify.setEnabled(true);
                    verify.setClickable(true);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        otp.addTextChangedListener(mTextEditorWatcher);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnectionDetector.isConnectingToInternet()) {
                    DialogUtil.showNoConnectionDialog(VerifyOtpActivity.this);
                } else {
                    verifyOtp(otp.getText().toString());
                }
            }
        });

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnectionDetector.isConnectingToInternet()) {
                    DialogUtil.showNoConnectionDialog(VerifyOtpActivity.this);
                } else {
                    generateOtp();
                }
            }
        });
    }

    private void verifyOtp(String otp) {
        String url = UrlConstants.BASE_URL + UrlConstants.VERIFY_OTP;
        otpData.put("otp", otp);
        otpData.put("scratchCode", getIntent().getStringExtra("scratchCode"));
        progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.verifying_otp));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        aq.progress(progress).ajax(url, otpData, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                try {
                    if (json != null && json.has("otpVerified") && json.getBoolean("otpVerified")) {
                        if(json.has("deviceId")) {
                            SessionUtil.saveValueForKey(VerifyOtpActivity.this, SessionUtil.DEVICE_ID, json.getString("deviceId"));
                        }
                        if (json.has("userExist") && json.getBoolean("userExist")) {
                            SessionUtil.saveUserId(json.getString("userId"), VerifyOtpActivity.this);
                            SessionUtil.saveUserName(json.getString("userName"), VerifyOtpActivity.this);
                            SessionUtil.saveNumber(phoneNumber + "", VerifyOtpActivity.this);
                            if (json.has("scratchCodeExist") && json.getBoolean("scratchCodeExist")){
                                SessionUtil.userRegistered("1", VerifyOtpActivity.this);
                                SessionUtil.scratchCodeVerify("1", VerifyOtpActivity.this);
                                SessionUtil.saveActivationDate(json.getString("activationDate"), VerifyOtpActivity.this);
                                startHomeActivity();
                            } else if(json.has("trialStartDate") && !json.isNull("trialStartDate")) {
                                SessionUtil.saveTrialStartDate(json.getString("trialStartDate"), VerifyOtpActivity.this);
                                String trialDate = SessionUtil.getTrialStartDate(VerifyOtpActivity.this);
                                    Long trailDateMillis = Long.parseLong(trialDate);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTimeInMillis(trailDateMillis);
                                    Calendar CurrentCal = Calendar.getInstance();
                                    int testCount = daysBetween(cal, CurrentCal) ;
                                    TextView remainingDays = (TextView) findViewById(R.id.tv_remaining_days);
                                    if (testCount <= 5) {
                                        startHomeActivity();
                                    }

                                   else{
                                        Intent intent = new Intent(VerifyOtpActivity.this, ScratchCodeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                            }else if(json.has("trialExpired")){
                                SessionUtil.saveTrialExpired(true,VerifyOtpActivity.this);
                                Intent intent = new Intent(VerifyOtpActivity.this, ScratchCodeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }

                        } else {
                            Intent i = new Intent(VerifyOtpActivity.this, SignUpActivity.class);
                            i.putExtra("phoneNumber", phoneNumber);
                            i.putExtra("scratchCode", getIntent().getStringExtra("scratchCode"));
                            startActivity(i);
                            finish();
                        }
                        EditText myEditText = (EditText) findViewById(R.id.etOtp);
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
                    } else if (json != null && json.has("otpVerified") && !json.getBoolean("otpVerified")) {
                        Toast.makeText(VerifyOtpActivity.this, getResources().getString(R.string.incorrect_otp), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(VerifyOtpActivity.this, getResources().getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void generateOtp() {
        findViewById(R.id.otpError).setVisibility(View.GONE);
        String url = UrlConstants.BASE_URL + UrlConstants.GENERATE_OTP;
        params = new HashMap();
        params.put("phoneNumber", phoneNumber);
        progress = new ProgressDialog(this);
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
                        Toast.makeText(VerifyOtpActivity.this, getResources().getString(R.string.otp_receivied_on_number) + phoneNumber, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(VerifyOtpActivity.this, getResources().getString(R.string.otp_error), Toast.LENGTH_LONG).show();
                }
            }
        });
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
    public void startHomeActivity() {
        Intent intent = new Intent(VerifyOtpActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

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

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("OTP Verification Activity");
    }
}
