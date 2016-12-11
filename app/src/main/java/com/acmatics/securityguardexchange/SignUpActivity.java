package com.acmatics.securityguardexchange;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.acmatics.securityguardexchange.bean.UserBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.gcm.GCMRegistrationThread;
import com.acmatics.securityguardexchange.gcm.OnGCMTokenRegistered;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements OnGCMTokenRegistered {

    private Toolbar toolBar;
    private Button signUp;
    private AQuery aq;
    private InternetConnectionDetector internetConnectionDetector;
    String phoneNumber;
    private EditText fullNameTextView, emailTextView, numberTextView, userAddress, userCity, userState, userCountry, userPinCode, userCompanyName;
    private GCMRegistrationThread gcmRegistrationThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_sign_up);

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.signup));
        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        aq = new AQuery(this);
        inflateXmlData();
        String id = android.provider.Settings.System.getString(super.getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("id", id);

        //Call GCM Registration Id
        gcmRegistrationThread = new GCMRegistrationThread(SignUpActivity.this);
        gcmRegistrationThread.setOnGcmTokenRegistered(SignUpActivity.this);
        gcmRegistrationThread.execute("");
    }


    public void inflateXmlData() {
        Intent i = getIntent();
        phoneNumber = i.getStringExtra("phoneNumber");
        fullNameTextView = (EditText) findViewById(R.id.name);
        emailTextView = (EditText) findViewById(R.id.email);
        numberTextView = ((EditText) findViewById(R.id.number));

        userAddress = (EditText) findViewById(R.id.edt_address);
        userCity = (EditText) findViewById(R.id.edt_city);
        userState = ((EditText) findViewById(R.id.edt_state));

        userCountry = (EditText) findViewById(R.id.edt_country);
        userPinCode = (EditText) findViewById(R.id.edt_pin_code);
        userCompanyName = ((EditText) findViewById(R.id.edt_company_name));

        signUp = (Button) findViewById(R.id.btnSignUp);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUser()) {
                    registerUser();
//                   Intent i = new Intent(SignUpActivity.this, HomeActivity.class);
//                   startActivity(i);
                }
            }
        });
        numberTextView.setText(phoneNumber);
        numberTextView.setEnabled(false);
    }

    public boolean validateUser() {
        aq.id(R.id.signupError).visibility(View.GONE);

        final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
        boolean isValid = true;

        fullNameTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        String fullName = fullNameTextView.getText().toString();
        if ("".equals(fullName) && !Pattern.compile(USERNAME_PATTERN).matcher(fullName).matches()) {
            fullNameTextView.setError(getResources().getString(R.string.error_message_name));
            isValid = false;
        }
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        String email = emailTextView.getText().toString();
         if (!"".equals(email) && !Pattern.compile(EMAIL_PATTERN).matcher(email).matches()) {
            emailTextView.setError(getResources().getString(R.string.error_message_address));
            isValid = false;
        }
        String number = numberTextView.getText().toString();
        if ("".equals(number)) {
            numberTextView.setError(getResources().getString(R.string.error_message_number));
            isValid = false;
        } else if (number.length() < 10) {
            numberTextView.setError(getResources().getString(R.string.error_message_max_10));
            isValid = false;
        }

        return isValid;
    }

    public void showPaymentDialog() {
        Dialog paymentDialog = new Dialog(this);
        paymentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paymentDialog.setContentView(R.layout.custom_payment_dialog);
        paymentDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

//        paymentDialog.findViewById(R.id.buy_online).setOnClickListener(SignUpActivity.this);
//        paymentDialog.findViewById(R.id.buy_affiliates).setOnClickListener(SignUpActivity.this);
//        paymentDialog.findViewById(R.id.bank_deposit).setOnClickListener(SignUpActivity.this);
        paymentDialog.show();

    }

    private void registerUser() {
        String url = UrlConstants.BASE_URL + UrlConstants.REGISTER_USER;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.progress_text_register));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        JSONObject userData = new JSONObject();
        JSONObject addressBean = new JSONObject();
        try {
            addressBean.put("addressLine", userAddress.getText().toString());
            addressBean.put("city", userCity.getText().toString());
            addressBean.put("state", userState.getText().toString());
            addressBean.put("pinCode", userPinCode.getText().toString());
            addressBean.put("country", userCountry.getText().toString());
            userData.put("scratchCode", getIntent().getStringExtra("scratchCode"));
            userData.put("name", fullNameTextView.getText().toString());
            userData.put("email", emailTextView.getText().toString());
            userData.put("companyName", userCompanyName.getText().toString());
            userData.put("phone", numberTextView.getText().toString());
            userData.put("gcmRegistrationId", SessionUtil.getGcmRegistrationId(this));
            String scratchCode = SessionUtil.getScratchCode(this);
            if (!scratchCode.isEmpty() && !scratchCode.equals(null)) {
                userData.put("scratchCode", scratchCode);
            }
            userData.put("addressBean", addressBean);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        aq.progress(progress).post(url, userData, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(SignUpActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(SignUpActivity.this);
                    }
                } else if (json != null) {
                    List<UserBean> userList = ConversionHelper.getUserList(json);
                    SessionUtil.saveUserName(userList.get(0).getName() + "", SignUpActivity.this);
                    SessionUtil.saveUserId(userList.get(0).getUserId() + "", SignUpActivity.this);
                    SessionUtil.saveEmail(userList.get(0).getEmail() + "", SignUpActivity.this);
                    SessionUtil.saveNumber(userList.get(0).getPhone() + "", SignUpActivity.this);
                    SessionUtil.saveAddress(userAddress.getText().toString(), SignUpActivity.this);
                    SessionUtil.saveCity(userCity.getText().toString(), SignUpActivity.this);
                    SessionUtil.saveState(userState.getText().toString(), SignUpActivity.this);
                    SessionUtil.saveCountry(userCountry.getText().toString(), SignUpActivity.this);
                    SessionUtil.savePinCode(userPinCode.getText().toString(), SignUpActivity.this);
                    SessionUtil.saveCompany(userCompanyName.getText().toString(), SignUpActivity.this);
                    try {
                        if(json.has("deviceId")) {
                            SessionUtil.saveValueForKey(SignUpActivity.this, SessionUtil.DEVICE_ID, json.getString("deviceId"));
                        }
                        SessionUtil.saveActivationDate(json.getString("activationDate"), SignUpActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (SessionUtil.getScratchCodeVerify(SignUpActivity.this).equals("1")) {
                        SessionUtil.userRegistered("1", SignUpActivity.this);
                        startHomeActivity();
                    } else if (SessionUtil.getIndentifyUserRegistered(SignUpActivity.this).equals("1")) {
                        Intent intent = new Intent(SignUpActivity.this, ScratchCodeActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (SessionUtil.getStartTrial(SignUpActivity.this).equals("1")) {
                        String userId=SessionUtil.getUserId(SignUpActivity.this);
                        startTrial(userId);
                    } else {
                        Intent intent = new Intent(SignUpActivity.this, ScratchCodeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    public void startHomeActivity() {
        Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    @Override
    public void onGCMTokenRegisteredSucessfully(boolean stetus, String GCMToken) {
        if (stetus) {
            SessionUtil.gcmRegistrationId(GCMToken, this);
        } else {
//            Log.e("", "Error : Token is : *" + GCMToken + "*");
        }
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
                        DialogUtil.showNoConnectionDialog(SignUpActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(SignUpActivity.this);
                    }
                } else if (json != null) {
//                    {"trialStartDate":1450088113243}
                    try {
                        Long trialDate = json.getLong("trialStartDate");
                        SessionUtil.saveTrialStartDate(trialDate+"",SignUpActivity.this);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    Intent home = new Intent(SignUpActivity.this, HomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                }
            }
        });
    }


}
