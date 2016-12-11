package com.acmatics.securityguardexchange.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acmatics.securityguardexchange.BookDetailsActivity;
import com.acmatics.securityguardexchange.BookReaderActivity;
import com.acmatics.securityguardexchange.R;
import com.acmatics.securityguardexchange.bean.TOCDataBean;
import com.acmatics.securityguardexchange.common.CircularImageView;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;

import java.util.ArrayList;
import java.util.List;

public class TableOfContentsFragment extends Fragment {
    List<TOCDataBean> navigationItems = new ArrayList<>();

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private LinearLayout fragmentLayout;
    private AQuery aq;
    private String bookNumber;

    public TableOfContentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentLayout = (LinearLayout) inflater.inflate(
                R.layout.fragment_table_of_contents, container, false);
        TableOfContentsAdapter adapter = new TableOfContentsAdapter(getActivity(), R.id.sideMenu,
                R.id.title, navigationItems);
        mDrawerListView = (ListView) fragmentLayout.findViewById(R.id.sideMenu);
        /*bookNumber = SessionUtil.getBookName(getActivity());*/



        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        return fragmentLayout;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout,String bookNumber) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        this.bookNumber = bookNumber;
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerListView = (ListView) fragmentLayout.findViewById(R.id.sideMenu);
        TableOfContentsAdapter adapter = new TableOfContentsAdapter(getActivity(), R.id.sideMenu,
                R.id.title, navigationItems);
        mDrawerListView.setAdapter(adapter);

        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        /*if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }*/

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        CircularImageView img = (CircularImageView) fragmentLayout.findViewById(R.id.bookImg);
        TextView bName = (TextView) fragmentLayout.findViewById(R.id.bookNameInDrawer);
        if (bookNumber.equalsIgnoreCase("1")) {
            img.setImageResource(R.drawable.book_1);
            bName.setText(getResources().getString(R.string.book_title_1));
        } else if (bookNumber.equalsIgnoreCase("2")) {
            img.setImageResource(R.drawable.new_book2);
            bName.setText(getResources().getString(R.string.book_title_2));
        } else if (bookNumber.equalsIgnoreCase("3")) {
            img.setImageResource(R.drawable.new_book3);
            bName.setText(getResources().getString(R.string.book_title_3));
        } if (bookNumber.equalsIgnoreCase("4")) {
            img.setImageResource(R.drawable.book_2);
            bName.setText(getResources().getString(R.string.book_title_4));
        }
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //inflater.inflate(R.menu.menu_main, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
//        ActionBar actionBar = getActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        //actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setNavigationItems(List<TOCDataBean> navigationItems) {
        this.navigationItems = navigationItems;
    }

    private class TableOfContentsAdapter extends ArrayAdapter<TOCDataBean> {

        public TableOfContentsAdapter(Context context, int resource,
                                      int textViewResourceId, List<TOCDataBean> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        private class ViewHolder {
            private View row;
            private TextView titleHolder = null;

            public ViewHolder(View row) {
                super();
                this.row = row;
            }

            public TextView getTitle() {
                if (null == titleHolder)
                    titleHolder = (TextView) row.findViewById(R.id.title);
                return titleHolder;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            TextView title = null;
            TOCDataBean rowData = getItem(position);

            if (null == convertView) {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.toc_item, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            holder = (ViewHolder) convertView.getTag();
            title = holder.getTitle();
            ImageView i = (ImageView) convertView.findViewById(R.id.i);
            if (rowData.getIsCategory()) {
                title.setBackgroundColor(getResources().getColor(R.color.white));
                i.setVisibility(View.VISIBLE);
                title.setText("" + rowData.getTitle());
                title.setTextColor(getResources().getColor(R.color.primary_text));
            } else {
                title.setBackgroundColor(getResources().getColor(R.color.white));
                title.setText("\t\t► " + rowData.getTitle());
                i.setVisibility(View.INVISIBLE);
                title.setTextColor(getResources().getColor(R.color.secondary_text));
            }


            return convertView;
        }
    }
}
