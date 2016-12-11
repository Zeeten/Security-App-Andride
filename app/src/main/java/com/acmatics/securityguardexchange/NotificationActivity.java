package com.acmatics.securityguardexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.bean.NotificationBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<NotificationBean> notificationList;
    private NotificationAdapter notificationAdapter;
    private AQuery aq;
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_notification);
        app = (SecuritySkillsApplication) getApplication();
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.notification_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        aq = new AQuery(this);
        recyclerView = (RecyclerView) findViewById(R.id.notificationRecView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadNotifications();
    }

    private void loadNotifications() {
        if (isConnection()) {
            String url = UrlConstants.BASE_URL + UrlConstants.GET_NOTIFICATION;
            ProgressDialog progressDialog = new ProgressDialog(NotificationActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.loading_notification));
            Map<String, String> params = new HashMap<String, String>();
            aq.progress(progressDialog).ajax(url, params, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json != null) {
                        notificationList = ConversionHelper.getNotificationList(json);
                        notificationAdapter = new NotificationAdapter();
                        recyclerView.setAdapter(notificationAdapter);
                        notificationAdapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(aq.getContext(), R.string.request_couldnt_be_completed, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            showConnectionErrorDialog();
        }

    }

    protected boolean isConnection() {
        ConnectivityManager manage = (ConnectivityManager) NotificationActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manage.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {

            return true;
        } else {
            return false;
        }
    }

    public void showConnectionErrorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.no_internet));
        builder.setMessage(getResources().getString(R.string.no_internet_message));
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                NotificationActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

   /* public void loadNotifications() {
        recyclerView = (RecyclerView) findViewById(R.id.notificationRecView);
        notificationList = new ArrayList<>();
        NotificationBean list = new NotificationBean();
        for (int i = 0; i < 4; i++) {
            list.setTitle("Notification - " + i);
            list.setNotificationDetails("Description of the notification - " + i);
            notificationList.add(list);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter();
        recyclerView.setAdapter(notificationAdapter);
    }*/

    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.QuizViewHolder> {
        protected View itemView;

        @Override
        public int getItemCount() {
            return notificationList.size();
        }

        @Override
        public void onBindViewHolder(QuizViewHolder vh,final int i) {
            if (i % 2 == 1) {
                vh.layout.setBackgroundResource(R.color.even_list_row_color);
            } else if (i % 2 == 0) {
                vh.layout.setBackgroundResource(R.color.white);
            }
            NotificationBean data = notificationList.get(i);
            vh.title.setText(data.getTitle());
            vh.desc.setText(data.getNotificationDetails());
            vh.date.setText(data.getDate());
//            vh.delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    removeAt(i);
//                }
//            });
        }

        public void removeAt(int position) {
            notificationList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, notificationList.size());
        }

        @Override
        public QuizViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.notification_item, viewGroup, false);
            return new QuizViewHolder(itemView);
        }

        public class QuizViewHolder extends RecyclerView.ViewHolder {
            protected TextView title;
            protected TextView desc;
            protected TextView date;
//            protected TextView delete;
            protected RelativeLayout layout;

            public QuizViewHolder(View v) {
                super(v);
                title = (TextView) v.findViewById(R.id.notificationTitle);
                desc = (TextView) v.findViewById(R.id.notificationDesc);
                date = (TextView) v.findViewById(R.id.createdDate);
//                delete = (TextView) v.findViewById(R.id.deleteNotification);
                layout = (RelativeLayout) v.findViewById(R.id.layout);
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

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("Notifications Activity");
    }
}
