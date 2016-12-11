package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.ConversionHelper;

/**
 * Created by kaira on 12/7/2015.
 */
public class StartTestActivity extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_start_text);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        TextView qus = (TextView) findViewById(R.id.testQuestions);
        TextView time = (TextView) findViewById(R.id.testMinutes);
        if (getIntent().hasExtra("testId")) {
            toolBar.setTitle(getResources().getString(R.string.practise_test_small) + " " + getIntent().getLongExtra("testId", 01));
            qus.setText("10" + "\n" + getResources().getString(R.string.questions));
            time.setText("30 " + "\n" + getResources().getString(R.string.minutes));
        } else {
            toolBar.setTitle(getResources().getString(R.string.quiz_list));
        }
        if (getIntent().hasExtra("totalqus")) {
            qus.setText(getIntent().getExtras().getString("totalqus") + "\n"+ getResources().getString(R.string.questions));
            if (getIntent().getExtras().getString("totalqus").equals("30")) {
                time.setText("30 " + "\n" + getResources().getString(R.string.minutes));
            } else if (getIntent().getExtras().getString("totalqus").equals("60")) {
                time.setText("60 " + "\n" + getResources().getString(R.string.minutes));
            } else if (getIntent().getExtras().getString("totalqus").equals("100")) {
                time.setText("100 " + "\n" +  getResources().getString(R.string.minutes));
            }
        }
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        inflateXmlData();

    }

    public void inflateXmlData() {
        findViewById(R.id.tv_start_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_start_test) {
            if (getIntent().hasExtra("testId")) {
                Intent intent = new Intent(StartTestActivity.this, PractiseTestActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(StartTestActivity.this, QuizActivity.class);
                if (getIntent().hasExtra("totalqus")) {
                    if (getIntent().getExtras().getString("totalqus").equals("30")) {
                        intent.putExtra("totalQus","30");
                    } else if (getIntent().getExtras().getString("totalqus").equals("60")) {
                        intent.putExtra("totalQus", "60");
                    } else if (getIntent().getExtras().getString("totalqus").equals("100")) {
                        intent.putExtra("totalQus","100");
                    }
                }
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
            }

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
