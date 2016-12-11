package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.DatabaseConstants;
import com.acmatics.securityguardexchange.dao.DaoMaster;
import com.acmatics.securityguardexchange.dao.DaoSession;
import com.acmatics.securityguardexchange.dao.Question;
import com.acmatics.securityguardexchange.fragments.InteractiveSessionDrawerFragment;
import com.acmatics.securityguardexchange.fragments.SinglePageInteractiveFragment;
import com.androidquery.AQuery;

import java.util.List;

public class InteractiveSessionsBook extends AppCompatActivity implements InteractiveSessionDrawerFragment.NavigationDrawerCallbacks {
    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private List<Question> questionList;

    private SecuritySkillsApplication app;
    private AQuery aq;
    private Toolbar toolBar;

    DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DatabaseConstants.DATABASE_NAME, null);
    public SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private InteractiveSessionDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_interactive_session_book);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.book_title_4));
        setSupportActionBar(toolBar);

        ImageView close = (ImageView) findViewById(R.id.closeBook);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNavigationDrawerFragment = (InteractiveSessionDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        // Setting up the navigation drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer));

        Intent i = getIntent();

        aq = new AQuery(this);

        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        questionList = daoSession.getQuestionDao().queryBuilder().list();

        mPager = (ViewPager) findViewById(R.id.book_pager);
        mPagerAdapter = new BookPagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        findViewById(R.id.previous_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() > 0) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
            }
        });
        findViewById(R.id.next_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPager.getCurrentItem() < questionList.size() - 1) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);

                }
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        mPager.setCurrentItem(position);
    }

    private class BookPagerAdapter extends FragmentStatePagerAdapter {
        public BookPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            SinglePageInteractiveFragment pageFragment = new SinglePageInteractiveFragment();
            pageFragment.setPosition(position);
            return pageFragment;
        }

        @Override
        public int getCount() {
            return questionList.size();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        app.trackActivity("Book Reader Activity");
    }
}
