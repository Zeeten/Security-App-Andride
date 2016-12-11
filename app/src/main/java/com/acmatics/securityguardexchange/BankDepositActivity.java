package com.acmatics.securityguardexchange;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;

import java.util.regex.Pattern;

/**
 * Created by kaira on 12/7/2015.
 */
public class BankDepositActivity extends AppCompatActivity {

    private AQuery aq;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_bank_deposit);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.bank_deposit));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        aq = new AQuery(this);
        submit = (Button) findViewById(R.id.submit);
//        submit.setEnabled(false);
//        submit.setBackgroundResource(R.drawable.primary_button_disable_bg);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuccessDialog();
            }
        });
        inflateXmlData();
        //validateUser();
    }

    public void inflateXmlData() {
        //TextView name = (TextView) findViewById(R.id.name);
        EditText number = (EditText) findViewById(R.id.number);
        EditText email = (EditText) findViewById(R.id.email);
        //name.setText(SessionUtil.getUserName(this));
        number.setText(SessionUtil.getNumber(this));
        number.setClickable(false);
        email.setText(SessionUtil.getEmail(this));
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setHint(getResources().getString(R.string.email));
        }


//        CheckBox check=(CheckBox)findViewById(R.id.checkbox_terms);
//        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    submit.setEnabled(true);
//                    submit.setBackgroundResource(R.drawable.primary_button_bg);
//                } else {
//                    submit.setEnabled(false);
//                    submit.setBackgroundResource(R.drawable.primary_button_disable_bg);
//                }
//            }
//        });
    }


    public void showSuccessDialog() {
        final Dialog successDialog = new Dialog(this);
        successDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        successDialog.setContentView(R.layout.custom_deposit_dialog);
        successDialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        successDialog.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                successDialog.dismiss();
            }
        });
        successDialog.show();

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
