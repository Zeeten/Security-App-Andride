package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmatics.securityguardexchange.bean.PracticeBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rubal on 15-12-2015.
 */
public class PracticeAttemptedList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<PracticeBean> testList;
    private PracticeAdapter practiceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_attempted_test);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle("Practise Test");
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        loadAllQuizData();
    }

    public void loadAllQuizData() {
        recyclerView = (RecyclerView) findViewById(R.id.quizRecyclerView);
        testList = new ArrayList<>();
        PracticeBean list = new PracticeBean();
        for (int i = 0; i <4; i++) {
            list.setTestNumber("Practise Number 1");
            list.setTestDate("12 Dec 2015");
            list.setTestAttempts("5/10");
            testList.add(list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        practiceAdapter = new PracticeAdapter();
        recyclerView.setAdapter(practiceAdapter);
    }

    public class PracticeAdapter extends RecyclerView.Adapter<PracticeAdapter.QuizViewHolder> {
        protected View itemView;

        @Override
        public int getItemCount() {
            return testList.size();
        }

        @Override
        public void onBindViewHolder(final QuizViewHolder vh, final int i) {
            if (i % 2 == 1) {
                itemView.setBackgroundResource(R.color.even_list_row_color);
            } else if (i % 2 == 0) {
                itemView.setBackgroundResource(R.color.white);
            }
            PracticeBean data = testList.get(i);
            vh.practiceNum.setText(data.getTestNumber());
            vh.practiceDate.setText(data.getTestDate());
            vh.practiceAttempts.setText("Attempts\n" + data.getTestAttempts());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Intent intent = new Intent(PracticeAttemptedList.this, PracticeAttemptedList.class);
                    //startActivity(intent);
                }
            });
            if (i == 2) {
                vh.practiceAttempts.setVisibility(View.VISIBLE);
                vh.next.setVisibility(View.GONE);
            }
        }

        @Override
        public QuizViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.attempted_test_list, viewGroup, false);
            return new QuizViewHolder(itemView);
        }

        public class QuizViewHolder extends RecyclerView.ViewHolder {
            protected TextView practiceNum;
            protected TextView practiceDate;
            protected TextView practiceAttempts;
            protected ImageView next;

            public QuizViewHolder(View v) {
                super(v);
                practiceNum = (TextView) v.findViewById(R.id.testNumber);
                practiceDate = (TextView) v.findViewById(R.id.testDate);
                practiceAttempts = (TextView) v.findViewById(R.id.testAttempts);
                next = (ImageView) v.findViewById(R.id.iv_start_quiz);
            }
        }
    }
}
