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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.bean.AffiliatesBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.UrlConstants;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by kaira on 12/7/2015.
 */
public class SearchAffiliatesActivity extends AppCompatActivity {
    private Toolbar toolBar;
    private Spinner stateSpinner, districtSpinner, locationSpinner;
    private RecyclerView recyclerView;
    private List<AffiliatesBean> affiliatesList;
    private AQuery aq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_search_affiliates);

        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.search_affiliates_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        aq = new AQuery(this);
        recyclerView = (RecyclerView) findViewById(R.id.affRecView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        loadSearchAffiliates();
        inflateXmlData();

    }

    private void loadSearchAffiliates() {
        if (isConnection()) {
            String url = UrlConstants.BASE_URL + UrlConstants.GET_AFFILIATES;
            ProgressDialog progressDialog = new ProgressDialog(SearchAffiliatesActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getResources().getString(R.string.loading_affiliates_please_wait));
            aq.progress(progressDialog).ajax(url, null, JSONObject.class, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject json, AjaxStatus status) {
                    if (json != null) {
                        affiliatesList = ConversionHelper.getAffiliatesList(json);
                        AffiliatesAdapter affiliatesAdapter = new AffiliatesAdapter();
                        recyclerView.setAdapter(affiliatesAdapter);
                        affiliatesAdapter.notifyDataSetChanged();

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
        ConnectivityManager manage = (ConnectivityManager) SearchAffiliatesActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                SearchAffiliatesActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void inflateXmlData() {
        stateSpinner = (Spinner) findViewById(R.id.statespinner);
        districtSpinner = (Spinner) findViewById(R.id.districtspinner);
        locationSpinner = (Spinner) findViewById(R.id.locationspinner);

        ArrayAdapter<CharSequence> stateadapter = ArrayAdapter.createFromResource(this,
                R.array.state_array, R.layout.simple_spinner_item);
        stateadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateadapter);

        ArrayAdapter<CharSequence> districtadapter = ArrayAdapter.createFromResource(this,
                R.array.district_array, R.layout.simple_spinner_item);
        districtadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(districtadapter);

        ArrayAdapter<CharSequence> locationadapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, R.layout.simple_spinner_item);
        locationadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationadapter);


    }


    class AffiliatesAdapter extends RecyclerView.Adapter<AffiliatesAdapter.AffiliatesViewHolder> {

        @Override
        public int getItemCount() {
            return affiliatesList.size();
        }

        @Override
        public void onBindViewHolder(AffiliatesViewHolder vh, int i) {
            AffiliatesBean affiliatesBean = affiliatesList.get(i);
            vh.affName.setText(affiliatesBean.getAffName());
            vh.affNumber.setText(affiliatesBean.getAffNumber());
            vh.affEmail.setText(affiliatesBean.getAffEmail());
            String locality;
            if (affiliatesBean.getAffLocality() == null) {
                locality = "";
            } else {
                locality = affiliatesBean.getAffLocality() + " ";
            }
            vh.affAddress.setText(affiliatesBean.getAffAddressLine() + " " + locality + affiliatesBean.getAffCity() + " " + affiliatesBean.getAffState() + " " + affiliatesBean.getAffPinCode());

        }

        @Override
        public AffiliatesViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
            View orderItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.affiliates_list_item, viewGroup, false);
            AffiliatesViewHolder holder = new AffiliatesViewHolder(orderItemView);
            return holder;
        }

        public class AffiliatesViewHolder extends RecyclerView.ViewHolder {
            TextView affName, affNumber, affEmail, affAddress;

            public AffiliatesViewHolder(View v) {
                super(v);
                affName = (TextView) v.findViewById(R.id.affName);
                affNumber = (TextView) v.findViewById(R.id.affNumber);
                affEmail = (TextView) v.findViewById(R.id.affEmail);
                affAddress = (TextView) v.findViewById(R.id.affAddress);
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
