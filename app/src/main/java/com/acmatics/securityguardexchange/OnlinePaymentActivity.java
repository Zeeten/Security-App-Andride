package com.acmatics.securityguardexchange;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.bean.UserBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.SecuritySkillsConstants;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.util.AvenuesParams;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.acmatics.securityguardexchange.util.PaymentConstants;
import com.acmatics.securityguardexchange.util.ServiceUtility;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by kaira on 12/7/2015.
 */
public class OnlinePaymentActivity extends AppCompatActivity {

    private AQuery aq;
    private Button submit;
    private String scratchCodeId;
    InternetConnectionDetector internetConnectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_online_payment);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.online_payment_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        aq = new AQuery(this);
        submit = (Button) findViewById(R.id.submit);
        submit.setEnabled(false);
        submit.setBackgroundResource(R.drawable.primary_button_disable_bg);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateScratchCode();
            }
        });
        inflateXmlData();
    }

    public void inflateXmlData() {
        TextView name = (TextView) findViewById(R.id.name);
        TextView number = (TextView) findViewById(R.id.number);
        TextView email = (TextView) findViewById(R.id.email);
        name.setText(SessionUtil.getUserName(this));
        number.setText(SessionUtil.getNumber(this));
        email.setText(SessionUtil.getEmail(this));
        CheckBox check=(CheckBox)findViewById(R.id.checkbox_terms);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    submit.setEnabled(true);
                    submit.setBackgroundResource(R.drawable.primary_button_bg);
                }else{
                    submit.setEnabled(false);
                    submit.setBackgroundResource(R.drawable.primary_button_disable_bg);
                }
            }
        });

    }


    private void generateScratchCode() {
        String url = UrlConstants.BASE_URL + UrlConstants.GENERATE_SCRATCH_CODE_ID;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.generating_scratch_code));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        Map params = new HashMap<>();
        aq.progress(progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(OnlinePaymentActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(OnlinePaymentActivity.this);
                    }
                } else if (json != null) {
                        String vAccessCode = PaymentConstants.ACCESS_CODE;
                        String vMerchantId = PaymentConstants.MERCHANT_ID;
                        String vCurrency = PaymentConstants.CURRENCY;
                        String vAmount = ServiceUtility.chkNull("1.0").toString().trim();
                        if(!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")){
                            Intent intent = new Intent(OnlinePaymentActivity.this,WebViewActivity.class);
                            intent.putExtra(AvenuesParams.ACCESS_CODE, vAccessCode);
                            intent.putExtra(AvenuesParams.MERCHANT_ID, vMerchantId);
                            try {
                                intent.putExtra(AvenuesParams.ORDER_ID, json.getString("scratchCodeId"));
                                scratchCodeId = json.getString("scratchCodeId");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            intent.putExtra(AvenuesParams.CURRENCY, vCurrency);
                            intent.putExtra(AvenuesParams.AMOUNT, vAmount);
                            intent.putExtra(AvenuesParams.REDIRECT_URL, PaymentConstants.REDIRECT_URL);
                            intent.putExtra(AvenuesParams.CANCEL_URL, PaymentConstants.CANCEL_URL);
                            intent.putExtra(AvenuesParams.RSA_KEY_URL, PaymentConstants.RSA_URL);
                            startActivityForResult(intent,1);
                        }

                }
            }
        });
    }
    public boolean validateUser() {
        aq.id(R.id.signupError).visibility(View.GONE);

        final String USERNAME_PATTERN = "^[a-z0-9_-]{3,15}$";
        boolean isValid = true;
        TextView fullNameTextView = ((TextView) findViewById(R.id.name));
        fullNameTextView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        String fullName = fullNameTextView.getText().toString();
        if ("".equals(fullName) && !Pattern.compile(USERNAME_PATTERN).matcher(fullName).matches()) {
            fullNameTextView.setError(getResources().getString(R.string.error_message_name));
            isValid = false;
        }
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        TextView emailTextView = ((TextView) findViewById(R.id.email));
        String email = emailTextView.getText().toString();
        if ("".equals(email)) {
            emailTextView.setError(getResources().getString(R.string.error_message_email));
            isValid = false;
        } else if (!Pattern.compile(EMAIL_PATTERN).matcher(email).matches()) {
            emailTextView.setError(getResources().getString(R.string.error_message_email));
            isValid = false;
        }

        TextView numberTextView = ((TextView) findViewById(R.id.number));
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

    public void showSuccessDialog() {
        final Dialog successDialog = new Dialog(this);
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.custom_success_dialog);
        successDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        successDialog.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
                Intent scratchActivity=new Intent(OnlinePaymentActivity.this,ScratchCodeActivity.class);
                scratchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                scratchActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                scratchActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(scratchActivity);
                finish();
            }
        });
        successDialog.show();

    }
    private void buyScratchCode() {
        String url = UrlConstants.BASE_URL + UrlConstants.BUY_SCRATCH_CODE;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.generating_scratch_code));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        Map params = new HashMap<>();
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        params.put("userId", prefs.getString(SessionUtil.USER_ID, ""));
        params.put("paymentType", SecuritySkillsConstants.PaymentTypeConstants.ONLINE);
        params.put("scratchCodeId", scratchCodeId);
        aq.progress(progress).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, final JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(OnlinePaymentActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(OnlinePaymentActivity.this);
                    }
                } else if (json != null) {
                    showSuccessDialog();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
           buyScratchCode();
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
}
