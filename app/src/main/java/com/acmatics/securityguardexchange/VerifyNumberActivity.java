package com.acmatics.securityguardexchange;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rubal on 21-09-2015.
 */
public class VerifyNumberActivity extends AppCompatActivity {

    private TextView verifyText;
    private Button verifyButton;
    private EditText phnNumber;
    private ProgressDialog progress;
    private Map params;
    private AQuery aq;
    private InternetConnectionDetector internetConnectionDetector;
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_verify_number);
        app = (SecuritySkillsApplication) getApplication();
        aq = new AQuery(this);
         
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.verify_otp_title));
        setSupportActionBar(toolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        internetConnectionDetector = new InternetConnectionDetector(this);
        verifyText = (TextView) findViewById(R.id.textView2);
        phnNumber = (EditText) findViewById(R.id.number);
        verifyButton = (Button) findViewById(R.id.button);
        verifyButton.setEnabled(false);
        verifyText.setText(Html.fromHtml(getResources().getString(R.string.sms_confirmation)));


        final TextWatcher mTextEditorWatcher = new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 10) {
                    verifyButton.setBackgroundResource(R.drawable.bg_disabled_button);
                    verifyButton.setEnabled(false);
                } else if (s.length() == 10) {
                    verifyButton.setBackgroundResource(R.drawable.bg_primary_button);
                    verifyButton.setEnabled(true);
                }
            }

            public void afterTextChanged(Editable s) {
            }
        };
        phnNumber.addTextChangedListener(mTextEditorWatcher);

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!internetConnectionDetector.isConnectingToInternet()) {
                    DialogUtil.showNoConnectionDialog(VerifyNumberActivity.this);
                } else {
                    generateOtp();
                }
            }
        });
    }

    private void generateOtp() {
        findViewById(R.id.otpError).setVisibility(View.GONE);
        String url = UrlConstants.BASE_URL + UrlConstants.GENERATE_OTP;
        params = new HashMap();
        params.put("phoneNumber", phnNumber.getText().toString());
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
                        Toast.makeText(VerifyNumberActivity.this, getResources().getString(R.string.otp_receivied_on_number) + phnNumber.getText().toString(), Toast.LENGTH_LONG).show();
                        Intent i = new Intent(VerifyNumberActivity.this, VerifyOtpActivity.class);
                        i.putExtra("scratchCode", getIntent().getStringExtra("scratchCode"));
                        i.putExtra("phoneNumber", phnNumber.getText().toString());
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(VerifyNumberActivity.this, getResources().getString(R.string.otp_error), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("Verify Number Activity");
    }
}
