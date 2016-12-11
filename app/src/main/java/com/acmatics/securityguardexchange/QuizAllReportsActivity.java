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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acmatics.securityguardexchange.bean.QuizReportBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kaira on 12/3/2015.
 */
public class QuizAllReportsActivity extends AppCompatActivity {
    private RecyclerView quizReportsrecyclerView;
    private QuizReportAdapter quizReportAdapter;
    private List<QuizReportBean> quizReportList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_all_quiz_reports);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.quiz_list));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        loadAllQuizData();

    }
    public void loadAllQuizData(){
        quizReportsrecyclerView=(RecyclerView)findViewById(R.id.quizReportsRecyclerView);
        quizReportList = new ArrayList<>();
        QuizReportBean list = new QuizReportBean();
        for (int i = 0; i < 4; i++) {
            list.setQuizDate("11/12/2015");
            quizReportList.add(list);
        }
        quizReportsrecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizReportAdapter = new QuizReportAdapter();
        quizReportsrecyclerView.setAdapter(quizReportAdapter);
    }
    public class QuizReportAdapter extends RecyclerView.Adapter<QuizReportAdapter.QuizViewHolder> {
        protected View itemView;

        @Override
        public int getItemCount() {
            return quizReportList.size();
        }

        @Override
        public void onBindViewHolder(QuizViewHolder vh, int i) {
            QuizReportBean data = quizReportList.get(i);
            vh.quizDate.setText(data.getQuizDate());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(QuizAllReportsActivity.this,ReportActivity.class);
                    startActivity(intent);
                }
            });
        }

        @Override
        public QuizViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.quiz_reports_item, viewGroup, false);
            return new QuizViewHolder(itemView);
        }

        public class QuizViewHolder extends RecyclerView.ViewHolder {
            protected TextView quizDate;

            public QuizViewHolder(View v) {
                super(v);
                quizDate = (TextView) v.findViewById(R.id.tv_quiz_date);
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
