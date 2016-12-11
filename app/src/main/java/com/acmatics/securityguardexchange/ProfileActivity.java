package com.acmatics.securityguardexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.acmatics.securityguardexchange.util.ImageLoadingUtils;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Rubal on 11-12-2015.
 */
public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private EditText name, email, number, address, city, state, country, pincode, company;
    private SecuritySkillsApplication app;
    private ImageView profilePic;
    private String mediaFileName;
    private Bitmap bitmap;
    private String encodedImageString;
    ImageLoadingUtils imageUtil;
    ProgressDialog progress;
    private AQuery aq;
    private InternetConnectionDetector internetConnectionDetector;
    private static final int UPLOAD_IMG = 1;
    private Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_profile);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.profile_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        imageUtil = new ImageLoadingUtils(this);
        aq = new AQuery(this);
        internetConnectionDetector = new InternetConnectionDetector(getApplicationContext());
        name = (EditText) findViewById(R.id.p_name);
        email = (EditText) findViewById(R.id.p_email);
        number = (EditText) findViewById(R.id.p_number);
        address = (EditText) findViewById(R.id.edt_address);
        city = (EditText) findViewById(R.id.edt_city);
        state = ((EditText) findViewById(R.id.edt_state));
        country = (EditText) findViewById(R.id.edt_country);
        pincode = (EditText) findViewById(R.id.edt_pin_code);
        company = ((EditText) findViewById(R.id.edt_company_name));
        profilePic = (ImageView) findViewById(R.id.profileImg);
        save = (Button) findViewById(R.id.btnEditProfile);
        String userName = SessionUtil.getUserName(this);
        String userEmail = SessionUtil.getEmail(this);
        String userNumber = SessionUtil.getNumber(this);
        String userAddress = SessionUtil.getAddress(this);
        String userCity = SessionUtil.getCity(this);
        String userState = SessionUtil.getState(this);
        String userCountry = SessionUtil.getCountry(this);
        String userPincode = SessionUtil.getPinCode(this);
        String userCompany = SessionUtil.getCompany(this);
        SharedPreferences userDetails;
        userDetails = this.getSharedPreferences(SessionUtil.USER_SESSION_PREF,
                Context.MODE_PRIVATE);
        String imageUrl = UrlConstants.BASE_URL + UrlConstants.DOWNLOAD_URL + userDetails.getString(SessionUtil.PROFILE_PIC_PATH, "");
        if (!TextUtils.isEmpty(userDetails.getString(SessionUtil.PROFILE_PIC_PATH, ""))) {
            aq.id(profilePic).image(imageUrl, true, true, 0, 0, null, AQuery.FADE_IN);
        } else {
            profilePic.setImageResource(R.drawable.anon_user);
        }
        if(TextUtils.isEmpty(userName)) {
            name.setHint("");
        } else {
            name.setText(userName);
        }
        if(TextUtils.isEmpty(userEmail)) {
            email.setHint("");
        } else {
            email.setText(userEmail);
        }
        if(TextUtils.isEmpty(userAddress)) {
            address.setHint("");
        } else {
            address.setText(userAddress);
        }
        if(TextUtils.isEmpty(userCity)) {
            city.setHint("");
        } else {
            city.setText(userCity);
        }
        if(TextUtils.isEmpty(userState)) {
            state.setHint("");
        } else {
            state.setText(userState);
        }
        if(TextUtils.isEmpty(userCountry)) {
            country.setHint("");
        } else {
            country.setText(userCountry);
        }
        if(TextUtils.isEmpty(userPincode)) {
            pincode.setHint("");
        } else {
            pincode.setText(userPincode);
        }
        if(TextUtils.isEmpty(userCompany)) {
            company.setHint("");
        } else {
            company.setText(userCompany);
        }
        number.setText(userNumber);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, UPLOAD_IMG);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void updateProfile() {
        String url = UrlConstants.BASE_URL + UrlConstants.UPDATE_PROFILE;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.progress_text_register));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        JSONObject userData = new JSONObject();
        try {
            userData.put("addressLine", address.getText().toString());
            userData.put("city", city.getText().toString());
            userData.put("state", state.getText().toString());
            userData.put("pinCode", pincode.getText().toString());
            userData.put("country", country.getText().toString());
            userData.put("name", name.getText().toString());
            userData.put("email", email.getText().toString());
            userData.put("companyName", company.getText().toString());
            userData.put("userId", SessionUtil.getUserId(this));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        aq.progress(progress).post(url, userData, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                if (status.getCode() == -101) {
                    if (!internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showNoConnectionDialog(ProfileActivity.this);
                    } else if (internetConnectionDetector.isConnectingToInternet()) {
                        DialogUtil.showSlowConnectionDialog(ProfileActivity.this);
                    }
                } else if (json != null) {
                        SessionUtil.saveUserName(name.getText().toString(), ProfileActivity.this);
                        SessionUtil.saveEmail(email.getText().toString(), ProfileActivity.this);
                        SessionUtil.saveAddress(address.getText().toString(), ProfileActivity.this);
                        SessionUtil.saveCity(city.getText().toString(), ProfileActivity.this);
                        SessionUtil.saveState(state.getText().toString(), ProfileActivity.this);
                        SessionUtil.saveCountry(country.getText().toString(), ProfileActivity.this);
                        SessionUtil.savePinCode(pincode.getText().toString(), ProfileActivity.this);
                        SessionUtil.saveCompany(company.getText().toString(), ProfileActivity.this);
                        Toast.makeText(getApplicationContext(), "Profile Updated !!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPLOAD_IMG && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String fileNameSegments[] = selectedImage.toString().split("/");
            mediaFileName = fileNameSegments[fileNameSegments.length - 1];
            if (mediaFileName.indexOf(".") == -1) {
                mediaFileName = mediaFileName + ".jpg";
            }
            ParcelFileDescriptor parcelFileDescriptor = null;
            try {
                parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(selectedImage, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                bitmap = imageUtil.decodeBitmapFromFileDescriptor(fileDescriptor);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (parcelFileDescriptor != null)
                        parcelFileDescriptor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            encodeImageToString();
        } else {
            Toast.makeText(ProfileActivity.this, "No Image Selected !!", Toast.LENGTH_SHORT).show();
        }

    }

    public void encodeImageToString() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byte_arr = stream.toByteArray();
                // Encode Image to String
                encodedImageString = Base64.encodeToString(byte_arr, 0);
                return "";
            }

            @Override
            protected void onPostExecute(String msg) {
                updateProfilePic();
            }
        }.execute(null, null, null);
        profilePic.setImageBitmap(bitmap);
    }

    private void updateProfilePic() {
        JSONObject profilePicJson = new JSONObject();
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        try {
            profilePicJson.put("encodedImageString", encodedImageString);
            profilePicJson.put("mediaFileName", mediaFileName);
            profilePicJson.put("userId", SessionUtil.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//
        String url = UrlConstants.BASE_URL + UrlConstants.UPDATE_PROFILE_PIC;
        aq.progress(progress).post(url, profilePicJson, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
//
                if (status.getCode() == -101) {
                    Toast.makeText(ProfileActivity.this, "Not Internet Connection !!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    try {
                        SessionUtil.updateProfilePicInSession(ProfileActivity.this, json.getString("mediaFilePath"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setResult(RESULT_OK, intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(ProfileActivity.this, HomeActivity.class);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        back.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(back);
        finish();
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

        app.trackActivity("Profile Activity");
    }
}
