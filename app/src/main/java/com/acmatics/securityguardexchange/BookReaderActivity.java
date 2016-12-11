package com.acmatics.securityguardexchange;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.bean.TOCDataBean;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.fragments.SinglePageFragment;
import com.acmatics.securityguardexchange.fragments.TableOfContentsFragment;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.Resources;
import nl.siegmann.epublib.domain.Spine;
import nl.siegmann.epublib.domain.SpineReference;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;

public class BookReaderActivity extends AppCompatActivity implements TableOfContentsFragment.NavigationDrawerCallbacks {

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private List<SpineReference> spineList;

    private List<TOCDataBean> contentDetails;
    private SecuritySkillsApplication app;
    private TableOfContentsFragment tocFragment;
    private AQuery aq;
    private String basePathForRead;
    private String bookNumber;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_book_reader);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        TextView title = (TextView) findViewById(R.id.title);
        if (getIntent().getExtras().getString("bookNumber").equalsIgnoreCase("1")){
            title.setText(getResources().getString(R.string.book_title_1));
        }else if (getIntent().getExtras().getString("bookNumber").equalsIgnoreCase("2")){
            title.setText(getResources().getString(R.string.book_title_2));
        }if (getIntent().getExtras().getString("bookNumber").equalsIgnoreCase("3")){
            title.setText(getResources().getString(R.string.book_title_3));
        }

        setSupportActionBar(toolBar);
        Intent i = getIntent();
        bookNumber = i.getStringExtra("bookNumber");

        contentDetails = new ArrayList<>();
        aq = new AQuery(this);
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        final String bookUrl = getIntent().getStringExtra("bookUrl");
        final String fileName = bookUrl.substring(bookUrl.lastIndexOf("/"));
        if (!prefs.contains(bookUrl)) {
            downloadBook(fileName, bookUrl,"book");
        } else {
            setupReader(fileName);
        }
    }

    private void setupReader(final String fileName) {
        Book book = null;
        try {
            File ext = Environment.getExternalStorageDirectory();
            File file = new File(ext, "books/" + fileName);
            if (!file.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setMessage(getResources().getString(R.string.re_download_text))
                        .setPositiveButton(getResources().getString(R.string.re_download), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                final String bookUrl = getIntent().getStringExtra("bookUrl");
                                downloadBook(fileName, bookUrl,"reDownload");
//                                finish();
                            }
                        });
                builder.show();
            } else {
                InputStream epubInputStream = new FileInputStream(file);
                book = (new EpubReader()).readEpub(epubInputStream);
                logContentsTable(book.getTableOfContents().getTocReferences(), 0);

                String basePath = Environment.getExternalStorageDirectory() + "/books/" + fileName.substring(0, fileName.indexOf(".") - 1) + "/";
                basePathForRead = "file://" + basePath;
                downloadResource(basePath, book);

                Spine spine = book.getSpine();
                spineList = new ArrayList<>();
                for (SpineReference s : spine.getSpineReferences()) {
                    if (s.getResource().getId().equalsIgnoreCase("ch00_fm06_contents") || s.getResource().getId().equalsIgnoreCase("f6")) {
                        continue;
                    }
                    spineList.add(s);
                }

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
                        if (mPager.getCurrentItem() < spineList.size() - 1) {
                            mPager.setCurrentItem(mPager.getCurrentItem() + 1);

                        }
                    }
                });

                tocFragment = (TableOfContentsFragment)
                        getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

                tocFragment.setNavigationItems(contentDetails);

                // Setting up the navigation drawer.
                tocFragment.setUp(
                        R.id.navigation_drawer,
                        (DrawerLayout) findViewById(R.id.drawer), bookNumber);

                ImageView close = (ImageView) findViewById(R.id.closeBook);
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.file_not_found) + book, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.io_found) + book, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    private void downloadBook(final String fileName, final String bookUrl, final String reDownload) {
        File ext = Environment.getExternalStorageDirectory();
        File target = new File(ext, "books/" + fileName);
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.download_book));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setProgressNumberFormat(null);
        progress.setIndeterminate(false);
        progress.setCancelable(false);
        aq.progress(progress).download(bookUrl, target, new AjaxCallback<File>() {

            public void callback(String url, File file, AjaxStatus status) {

                if (file != null) {
                    SharedPreferences prefs = SessionUtil.getUserSessionPreferences(BookReaderActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(bookUrl, true);
                    editor.commit();

                    if (reDownload.equalsIgnoreCase("reDownload")){
                        reloadActivity();
                    }else{
                        setupReader(fileName);
                    }
                }
            }

        });
    }


    private void downloadResource(String directory, Book book) {
        try {

            Resources rst = book.getResources();
            Collection<Resource> clrst = rst.getAll();
            Iterator<Resource> itr = clrst.iterator();

            while (itr.hasNext()) {
                Resource rs = itr.next();

                if ((rs.getMediaType() == MediatypeService.JPG)
                        || (rs.getMediaType() == MediatypeService.PNG)
                        || (rs.getMediaType() == MediatypeService.GIF)) {


                    File oppath1 = new File(directory, rs.getHref().replace("OEBPS/", ""));

                    oppath1.getParentFile().mkdirs();
                    oppath1.createNewFile();

                    FileOutputStream fos1 = new FileOutputStream(oppath1);
                    fos1.write(rs.getData());
                    fos1.close();

                } else {

                    File oppath = new File(directory, rs.getHref());

                    oppath.getParentFile().mkdirs();
                    oppath.createNewFile();

                    FileOutputStream fos = new FileOutputStream(oppath);
                    fos.write(rs.getData());
                    fos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        int pos = -1;
        Resource resource = contentDetails.get(position).getResource();
        for (SpineReference ref : spineList) {
            if (ref.getResource().getId().equalsIgnoreCase(resource.getId())) {
                pos = spineList.indexOf(ref);
            }
        }
        if (pos == -1) {
            Toast.makeText(this, resource.getId(), Toast.LENGTH_LONG).show();
        } else {
            mPager.setCurrentItem(pos);
        }
    }

    private class BookPagerAdapter extends FragmentStatePagerAdapter {
        public BookPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            SinglePageFragment pageFragment = new SinglePageFragment();
            pageFragment.setPosition(position);
            pageFragment.setBasePathForRead(basePathForRead);
            return pageFragment;
        }

        @Override
        public int getCount() {
            return spineList.size();
        }
    }

    public List<SpineReference> getSpineList() {
        return spineList;
    }

    private void logContentsTable(List<TOCReference> tocReferences, int depth) {
        if (tocReferences == null) {
            return;
        }
        for (TOCReference tocReference : tocReferences) {
            if (tocReference.getResource().getId().equalsIgnoreCase("ch00_fm06_contents") || tocReference.getResource().getId().equalsIgnoreCase("f6")) {
                continue;
            }
            StringBuilder tocString = new StringBuilder();
            tocString.append(tocReference.getTitle());
            TOCDataBean row = new TOCDataBean();
            row.setIsCategory(true);
            if (depth > 0) {
                row.setIsCategory(false);
            }
            row.setTitle(tocString.toString());
            row.setResource(tocReference.getResource());
            contentDetails.add(row);
            logContentsTable(tocReference.getChildren(), depth + 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        app.trackActivity("Book Reader Activity");
    }
    public void reloadActivity() {
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        final String bookUrl = getIntent().getStringExtra("bookUrl");
        final String fileName = bookUrl.substring(bookUrl.lastIndexOf("/"));
        if (!prefs.contains(bookUrl)) {
            downloadBook(fileName, bookUrl,"book");
        } else {
            setupReader(fileName);
        }
    }
}

