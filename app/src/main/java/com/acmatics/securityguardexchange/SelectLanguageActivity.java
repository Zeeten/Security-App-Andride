package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.util.SessionUtil;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Rubal on 03-12-2015.
 */
public class SelectLanguageActivity extends AppCompatActivity {

    private Toolbar toolBar;
    private Spinner spinner;
    private ArrayList<String> languageList;
    private Button next;
    private TextView select;
    private String language="hi";
    private SecuritySkillsApplication app;
    AlertDialog.Builder alert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (SecuritySkillsApplication) getApplication();
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_language);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.language_title));
        setSupportActionBar(toolBar);
        if (getIntent().getExtras().getString("home").equals("homeScreen")) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
         alert = new AlertDialog.Builder(this);
        select = (TextView) findViewById(R.id.label);
        languageList = new ArrayList<String>();
        languageList.add(0, getResources().getString(R.string.hindi));
        languageList.add(1, getResources().getString(R.string.english));
        languageList.add(2, getResources().getString(R.string.punjabi));
        languageList.add(3, getResources().getString(R.string.gujarati));
        languageList.add(4, getResources().getString(R.string.tamil));
        languageList.add(5, getResources().getString(R.string.kannada));
        languageList.add(6, getResources().getString(R.string.malayalam));
        languageList.add(7, getResources().getString(R.string.telugu));
        languageList.add(8, getResources().getString(R.string.marathi));
        languageList.add(9, getResources().getString(R.string.bengali));
        languageList.add(10, getResources().getString(R.string.oriya));
        languageList.add(11, getResources().getString(R.string.assamese));
        languageList.add(12, getResources().getString(R.string.urdu));
        spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.simple_spinner_item, languageList);

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        language="hi";
                        break;

                    case 1:
                        language="en";
                        break;
                    case 2:
                     /*   alert.setMessage("Being uploaded Shortly"+"\n"+"ਛੇਤੀ ਹੀ ਅਪਲੋਡ ਕੀਤਾ ਜਾ ਰਿਹਾ");
                        alert.setPositiveButton("OK", null);
                        alert.show();*/
                        language="pn";
                        break;
                    case 3:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"ટૂંક સમયમાં અપલોડ કરવામાં");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 4:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"விரைவில் பதிவேற்றம் செய்யப்பட்டு");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 5:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"ಸ್ವಲ್ಪ ಅಪ್ಲೋಡ್ ಮಾಡಲಾಗುತ್ತಿದೆ");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 6:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"ഉടൻ അപ്ലോഡുചെയ്യുകയാണ്");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 7:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"త్వరలోనే అప్లోడ్ అవుతోంది");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 8:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"लवकरच अपलोड केला जात");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 9:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"খুব শীঘ্রই আপলোড করা হচ্ছে");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 10:
                        alert.setMessage("Being uploaded Shortly");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 11:
                        alert.setMessage("Being uploaded Shortly");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                    case 12:
                        alert.setMessage("Being uploaded Shortly"+"\n"+"کچھ ہی دیر اپ لوڈ کی جارہی");
                        alert.setPositiveButton("OK", null);
                        alert.show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        next = (Button) findViewById(R.id.btnLanguage);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getIntent().getExtras().getString("home").equals("homeScreen")) {
                    startHomeActivity();
                    setLanguage(language);

                } else {
                    Intent i = new Intent(SelectLanguageActivity.this, ScratchCodeActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    setLanguage(language);
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
                startHomeActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startHomeActivity() {
        Intent i = new Intent(SelectLanguageActivity.this, HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }

    public void setLanguage(String lang) {
        if (lang.equalsIgnoreCase("en")) {
           /* Locale locale = new Locale("en");
            Locale.setDefault(locale);
            Configuration configEnglish = new Configuration();
            configEnglish.locale = locale;
            getBaseContext().getResources().updateConfiguration(configEnglish, getBaseContext().getResources().getDisplayMetrics());*/
            SessionUtil.saveSelectedLanguage("english", getApplicationContext());
        }
        else if(lang.equalsIgnoreCase("pn")) {
           /*Locale localePunjabi = new Locale("pn");
            Locale.setDefault(localePunjabi);
            Configuration configPunjabi = new Configuration();
            configPunjabi.locale = localePunjabi;
            getBaseContext().getResources().updateConfiguration(configPunjabi, getBaseContext().getResources().getDisplayMetrics());*/
            SessionUtil.saveSelectedLanguage("punjabi", getApplicationContext());
        }
        else {
           /*Locale localeHindi = new Locale("hi");
            Locale.setDefault(localeHindi);
            Configuration configHindi = new Configuration();
            configHindi.locale = localeHindi;
            getBaseContext().getResources().updateConfiguration(configHindi, getBaseContext().getResources().getDisplayMetrics());*/
            SessionUtil.saveSelectedLanguage("hindi", getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.trackActivity("Select Language Activity");
    }

}
