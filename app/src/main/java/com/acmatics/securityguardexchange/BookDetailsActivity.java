package com.acmatics.securityguardexchange;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.acmatics.securityguardexchange.common.BookUrlConstants;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.common.InternetConnectionDetector;
import com.acmatics.securityguardexchange.util.DialogUtil;
import com.acmatics.securityguardexchange.util.SessionUtil;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Rubal on 04-12-2015.
 */
public class BookDetailsActivity extends AppCompatActivity {
    private Toolbar toolBar;
    private ImageView bookImage;
    private TextView bookTitle, isbn, publisher, bookPart, bookDescription;
    private Button downloadNow;
    private String bookNumber;
    private String bookUrl;
    private AQuery aq;
    private InternetConnectionDetector internetConnectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_book_detail);
        Intent i = getIntent();
        bookNumber = i.getStringExtra("bookNumber");
        aq = new AQuery(this);
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.book_details_book_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        internetConnectionDetector = new InternetConnectionDetector(this);
        bookImage = (ImageView) findViewById(R.id.bookImage);
        bookTitle = (TextView) findViewById(R.id.bookTitle);
        bookPart = (TextView) findViewById(R.id.bookPart);
        bookDescription = (TextView) findViewById(R.id.bookDescription);
        isbn = (TextView) findViewById(R.id.isbn);
        publisher = (TextView) findViewById(R.id.publisher);
        downloadNow = (Button) findViewById(R.id.downloadNow);
        downloadNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bookNumber.equalsIgnoreCase("4")) {
                    Intent interactive = new Intent(BookDetailsActivity.this, InteractiveSessionsBook.class);
//                    Interactive.putExtra()
                    startActivity(interactive);
                } else if (downloadNow.getText().equals(getResources().getString(R.string.read_book))) {
                    Intent intent = new Intent(BookDetailsActivity.this, BookReaderActivity.class);
                    final ProgressDialog dialog = new ProgressDialog(BookDetailsActivity.this);
                    dialog.setMessage(getResources().getString(R.string.load_book));
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setMax(100);
                    dialog.setProgressNumberFormat(null);
                    dialog.setProgressPercentFormat(null);
                    dialog.setIndeterminate(false);
                    dialog.setCancelable(false);
                    dialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2500);
                    intent.putExtra("bookUrl", bookUrl);
                    intent.putExtra("bookNumber", bookNumber);
                    startActivity(intent);
                } else if (!internetConnectionDetector.isConnectingToInternet()) {
                    DialogUtil.showNoConnectionDialog(BookDetailsActivity.this);
                } else {

                    Intent intent = new Intent(BookDetailsActivity.this, BookReaderActivity.class);
                    //if (!bookNumber.contains("4")) {
                    final ProgressDialog dialog = new ProgressDialog(BookDetailsActivity.this);
                    dialog.setMessage(getResources().getString(R.string.load_book));
                    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    dialog.setMax(100);
                    dialog.setProgressNumberFormat(null);
                    dialog.setProgressPercentFormat(null);
                    dialog.setIndeterminate(false);
                    dialog.setCancelable(false);
                    dialog.show();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            dialog.dismiss();
                        }
                    }, 2500);
                    intent.putExtra("bookUrl", bookUrl);
                    intent.putExtra("bookNumber", bookNumber);
                    startActivity(intent);
                    /*} else {
                        final String fileName = bookUrl.substring(bookUrl.lastIndexOf("/"));
                        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(BookDetailsActivity.this);
                        if (prefs.contains(bookUrl)) {
                            checkIfGitDenInstall();
                        } else {
                            downloadBook(fileName, bookUrl);

                        }
                    }*/
                }
            }
        });

        String selectedLanguage = SessionUtil.getSelectLanguage(this);
        if (bookNumber.equalsIgnoreCase("1")) {
            bookTitle.setText(getResources().getString(R.string.book_title_1));
            //bookPart.setVisibility(View.VISIBLE);
            //bookPart.setText(getResources().getString(R.string.basic));
            //bookDescription.setText(getResources().getString(R.string.book1_description));
            publisher.setText(getResources().getString(R.string.book_publisher));
            if (selectedLanguage.equalsIgnoreCase("hindi")) {
                bookUrl = BookUrlConstants.BOOK_THREE_HINDI_URL;
                bookImage.setImageResource(R.drawable.book1_hindi);
                isbn.setText("978-93-5156-112-5");
            } else {
                bookUrl = BookUrlConstants.BOOK_THREE_ENGLISH_URL;
                bookImage.setImageResource(R.drawable.book_1);
                isbn.setText("978-93-5156-111-8");
            }
        } else if (bookNumber.equalsIgnoreCase("2")) {

            bookTitle.setText(getResources().getString(R.string.book_title_2));
            //bookPart.setVisibility(View.VISIBLE);
            //bookPart.setText(getResources().getString(R.string.advance));
            //bookDescription.setText(getResources().getString(R.string.book1_description));
            publisher.setText(getResources().getString(R.string.book_publisher));
            if (selectedLanguage.equalsIgnoreCase("hindi")) {
                bookUrl = BookUrlConstants.BOOK_PMKVY_HINDI_URL;
                bookImage.setImageResource(R.drawable.book2_hindi);
                isbn.setText("978-93-5156-112-5");
            } else {
                bookUrl = BookUrlConstants.BOOK_PMKVY_ENGLISH_URL;
                bookImage.setImageResource(R.drawable.new_book2);
                isbn.setText("978-93-5156-111-8");
            }
        } else if (bookNumber.equalsIgnoreCase("3")) {
            bookImage.setImageResource(R.drawable.new_book3);
            bookTitle.setText(getResources().getString(R.string.book_title_3));
            //bookPart.setVisibility(View.VISIBLE);
            //bookPart.setText(getResources().getString(R.string.english_ver));
            //bookDescription.setVisibility(View.GONE);
            publisher.setText(getResources().getString(R.string.book_publisher));
            if (selectedLanguage.equalsIgnoreCase("hindi")) {
                bookUrl = BookUrlConstants.PARTICIPANT_HANDBOOK_HINDI_URL;
                bookImage.setImageResource(R.drawable.book3_hindi);
                isbn.setText("978-93-5156-112-5");
            } else {
                bookUrl = BookUrlConstants.PARTICIPANT_HANDBOOK_ENGLISH_URL;
                bookImage.setImageResource(R.drawable.new_book3);
                isbn.setText("978-93-5156-111-8");
            }
        } else if (bookNumber.equalsIgnoreCase("4")) {

            bookTitle.setText(getResources().getString(R.string.book_title_4));
            //bookPart.setVisibility(View.VISIBLE);
            //bookDescription.setVisibility(View.GONE);
            //bookPart.setText(getResources().getString(R.string.english_ver));
            publisher.setText(getResources().getString(R.string.book_publisher));
            if (selectedLanguage.equalsIgnoreCase("hindi")) {
                bookUrl = BookUrlConstants.BOOK_FOUR_HINDI_URL;
                bookImage.setImageResource(R.drawable.book4_hindi);
                isbn.setText("-");
            } else {
                bookUrl = BookUrlConstants.BOOK_FOUR_ENGLISH_URL;
                bookImage.setImageResource(R.drawable.book_2);
                isbn.setText("-");
            }
        }else if (bookNumber.equalsIgnoreCase("5")) {

            bookTitle.setText(getResources().getString(R.string.book_title_5));
            //bookPart.setVisibility(View.VISIBLE);
            //bookDescription.setVisibility(View.GONE);
            //bookPart.setText(getResources().getString(R.string.english_ver));
            publisher.setText(getResources().getString(R.string.book_publisher));
            if (selectedLanguage.equalsIgnoreCase("hindi")) {
                bookUrl = BookUrlConstants.BOOK_FIVE_HINDI_URL;
                bookImage.setImageResource(R.drawable.book5);
                isbn.setText("978-93-5104-104-7");
            } else {
                bookUrl = BookUrlConstants.BOOK_FIVE_ENGLISH_URL;
                bookImage.setImageResource(R.drawable.book_3);
                isbn.setText("978-93-5104-103-0");
            }
        }else if (bookNumber.equalsIgnoreCase("6")) {

            bookTitle.setText(getResources().getString(R.string.book_title_6));
            //bookPart.setVisibility(View.VISIBLE);
            //bookDescription.setVisibility(View.GONE);
            //bookPart.setText(getResources().getString(R.string.english_ver));
            publisher.setText(getResources().getString(R.string.book_publisher));
            if (selectedLanguage.equalsIgnoreCase("hindi")) {
                bookUrl = BookUrlConstants.BOOK_SIX_HINDI_URL;
                bookImage.setImageResource(R.drawable.book6);
                isbn.setText("978-93-5104-326-3");
            } else {
                bookUrl = BookUrlConstants.BOOK_SIX_ENGLISH_URL;
                bookImage.setImageResource(R.drawable.book_4);
                isbn.setText("978-93-5104-325-6");
            }
        }
        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
        if (prefs.contains(bookUrl) || bookNumber.equalsIgnoreCase("4")) {
            downloadNow.setText(getResources().getString(R.string.read_book));
        } /*else if (bookNumber.equalsIgnoreCase("3")) {
            downloadNow.setClickable(false);
            downloadNow.setText(getResources().getString(R.string.released_soon));
        }*/ else {
            downloadNow.setText(getResources().getString(R.string.download_now));
        }
        Animation zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        bookImage.setAnimation(zoomIn);
        bookImage.startAnimation(zoomIn);
    }

    private void openGitDen() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.gitden.epub.reader.app");
        startActivity(launchIntent);
    }

    private void downloadBook(final String fileName, final String bookUrl) {
        File ext = Environment.getExternalStorageDirectory();
        File target = new File(ext, "securityskills" + fileName);
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage(getResources().getString(R.string.download_book));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgressNumberFormat(null);
        progress.setCancelable(false);
        aq.progress(progress).download(bookUrl, target, new AjaxCallback<File>() {

            public void callback(String url, File file, AjaxStatus status) {

                if (file != null) {
                    SharedPreferences prefs = SessionUtil.getUserSessionPreferences(BookDetailsActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(bookUrl, true);
                    editor.commit();
                    checkIfGitDenInstall();

                }
            }

        });
    }

    private void checkIfGitDenInstall() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo("com.gitden.epub.reader.app", PackageManager.GET_META_DATA);
            openGitDen();
        } catch (PackageManager.NameNotFoundException e) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("This book requires Gitden App to be installed. Would you like to install it?");
            // on pressing cancel button
            alertDialog.setPositiveButton("Install", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Uri uri = Uri.parse("market://details?id=" + "com.gitden.epub.reader.app");
                    Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(myAppLinkToMarket);
                    } catch (ActivityNotFoundException e) {
                /*Toast.makeText(this, " Unable to find market app", Toast.LENGTH_LONG).show();*/
                    }

                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
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
