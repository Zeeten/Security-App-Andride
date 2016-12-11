package com.acmatics.securityguardexchange;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.acmatics.securityguardexchange.common.ConversionHelper;
import com.acmatics.securityguardexchange.util.SessionUtil;

/**
 * Created by Rubal on 04-12-2015.
 */
public class BooksActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolBar;
    CardView bookOne, bookTwo, bookThree, bookFour,bookFive,bookSix;
    private SecuritySkillsApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConversionHelper.setAppLanguage(this);
        setContentView(R.layout.activity_books);
        app = (SecuritySkillsApplication) getApplication();
        toolBar = (Toolbar) findViewById(R.id.toolBar);
        /*ImageView play = (ImageView) toolBar.findViewById(R.id.playMovie);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(BooksActivity.this, GuidlineVideos.class);
                i.putExtra("urlId","fkZHVKMTVT4");
                startActivity(i);
            }
        });*/
        toolBar.setTitleTextColor(Color.WHITE);
        toolBar.setTitle(getResources().getString(R.string.books_title));
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        bookOne = (CardView) findViewById(R.id.book03);
        bookTwo = (CardView) findViewById(R.id.book04);
        bookThree = (CardView) findViewById(R.id.book01);
        bookFour = (CardView) findViewById(R.id.book02);
        bookFive = (CardView) findViewById(R.id.book05);
        bookSix = (CardView) findViewById(R.id.book06);

        SharedPreferences prefs = SessionUtil.getUserSessionPreferences(getApplicationContext());
        if (!prefs.contains(SessionUtil.IS_BOOK_RUN)) {
            SessionUtil.saveBookRun(BooksActivity.this, "firstRun");
            Intent i = new Intent(BooksActivity.this, ImageShowFullView.class);
            i.putExtra("urlId","books");
            startActivity(i);
//            showFollowDialog();
        }

        String selectedLanguage = SessionUtil.getSelectLanguage(this);
        if(selectedLanguage.equalsIgnoreCase("hindi")){
            ((ImageView)findViewById(R.id.img1)).setImageResource(R.drawable.book1_hindi);
            ((ImageView)findViewById(R.id.img2)).setImageResource(R.drawable.book2_hindi);
            ((ImageView)findViewById(R.id.img3)).setImageResource(R.drawable.book3_hindi);
            ((ImageView)findViewById(R.id.img4)).setImageResource(R.drawable.book4_hindi);
            ((ImageView)findViewById(R.id.img5)).setImageResource(R.drawable.book5);
            ((ImageView)findViewById(R.id.img6)).setImageResource(R.drawable.book6);
        } else {
            ((ImageView)findViewById(R.id.img1)).setImageResource(R.drawable.book_1);
            ((ImageView)findViewById(R.id.img2)).setImageResource(R.drawable.new_book2);
            ((ImageView)findViewById(R.id.img3)).setImageResource(R.drawable.new_book3);
            ((ImageView)findViewById(R.id.img4)).setImageResource(R.drawable.book_2);
            ((ImageView)findViewById(R.id.img5)).setImageResource(R.drawable.book_3);
            ((ImageView)findViewById(R.id.img6)).setImageResource(R.drawable.book_4);
        }

        bookOne.setOnClickListener(this);
        bookTwo.setOnClickListener(this);
        bookThree.setOnClickListener(this);
        bookFour.setOnClickListener(this);
        bookFive.setOnClickListener(this);
        bookSix.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent i = new Intent(BooksActivity.this, BookDetailsActivity.class);

        if (v.getId() == R.id.book03) {
            i.putExtra("bookNumber", "3");
            startActivity(i);
        }

        if (v.getId() == R.id.book04) {
            //i.putExtra("bookNumber", "bookTwo");
            i.putExtra("bookNumber", "4");
            SharedPreferences prefs = SessionUtil.getUserSessionPreferences(this);
            if(prefs.contains(SessionUtil.ACTIVATION_DATE)){
                startActivity(i);
            } else {
                startActivity(new Intent(getApplicationContext(),ScratchCodeActivity.class));
            }

        }

        else if (v.getId() == R.id.book01) {
            i.putExtra("bookNumber", "1");
            startActivity(i);
        }

        else if (v.getId() == R.id.book02) {
            //i.putExtra("bookNumber", "bookFour");
            i.putExtra("bookNumber", "2");
            startActivity(i);
        }
        else if (v.getId() == R.id.book05) {
            i.putExtra("bookNumber", "5");
            startActivity(i);
        }

        else if (v.getId() == R.id.book06) {
            //i.putExtra("bookNumber", "bookFour");
            i.putExtra("bookNumber", "6");
            startActivity(i);
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

        app.trackActivity("Books Activity");
    }

   /* @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void showFollowDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_video);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogFadeAnimation;
        SessionUtil.saveBookRun(BooksActivity.this, "firstRun");
        dialog.setCancelable(false);
        final VideoView videoview = (VideoView) dialog.findViewById(R.id.VideoView);
        final ProgressBar progressBar = (ProgressBar) dialog.findViewById(R.id.progressBar);
        try {
            MediaController mediacontroller = new MediaController(BooksActivity.this);
            mediacontroller.setAnchorView(videoview);
            Uri video = Uri.parse(videoURL);
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
//                progressBar.setVisibility(View.GONE);
                videoview.start();


            }
        });
        videoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
//                        videoview.start();
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
//                        progressBar.setVisibility(View.GONE);
//                        videoview.start();
                        return true;
                    }
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                        progressBar.setVisibility(View.GONE);
//                        videoview.start();
                        return true;
                    }
                }
                return false;
            }
        });
        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.btnSkip)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }*/

}
