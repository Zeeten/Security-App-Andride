package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.ConversionHelper;

/**
 * Created by kaira on 12/4/2015.
 */
public class QuestionDescriptionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_question_description);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.question_description_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        inflateXmlData();


    }

    public void inflateXmlData() {
        TextView qusNo =(TextView)findViewById(R.id.tv_question_no);
        TextView ans =(TextView)findViewById(R.id.tv_qus_ans);

        String question = getIntent().getExtras().getString("question");
        String questionNo = getIntent().getExtras().getString("questionNo");
        String answer = getIntent().getExtras().getString("ans");

        ans.setText(question + "\n" + answer);
        qusNo.setText(getResources().getString(R.string.question)+ questionNo +getResources().getString(R.string.description_small));

        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(QuestionDescriptionActivity.this, TrainingVideosActivity.class);
//                startActivity(intent);

                finish();
            }
        });
    }


}
