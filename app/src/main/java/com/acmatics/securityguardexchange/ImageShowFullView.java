package com.acmatics.securityguardexchange;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.acmatics.securityguardexchange.util.SessionUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kaira on 1/27/2016.
 */
public class ImageShowFullView extends AppCompatActivity implements TextToSpeech.OnUtteranceCompletedListener {
    private ViewPager viewPager;
    private ArrayList<Integer> listOfImages;
    private ArrayList<String> textLists;
    private String selection;
    private TextToSpeech textToSpeech;
    private ProgressDialog dialog;
    private boolean isReading;
    private FloatingActionButton fab;
    private boolean isPlayFirstTime=false;
    private int pagePosition;
//    private FixedSpeedScroller scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_view);
        selection = getIntent().getStringExtra("urlId");
        textToSpeech = new TextToSpeech(this, mOnInitListener);
        inflateXmlData();
        dialog = new ProgressDialog(ImageShowFullView.this);
        dialog.setMessage(getResources().getString(R.string.load_book));
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setProgress(0);
        dialog.show();
    }

    private void inflateXmlData() {
        listOfImages = new ArrayList<Integer>();
        textLists = new ArrayList<String>();
        if (selection.equalsIgnoreCase("books")) {
            if (SessionUtil.getSelectLanguage(ImageShowFullView.this).equalsIgnoreCase("hindi")) {
                listOfImages.add(0, R.drawable.bh1);
                listOfImages.add(1, R.drawable.bh2);
                listOfImages.add(2, R.drawable.bh3);
            } else {
                listOfImages.add(0, R.drawable.b1);
                listOfImages.add(1, R.drawable.b2);
                listOfImages.add(2, R.drawable.b3);
            }
            textLists.add(0, getResources().getString(R.string.bookpage1));
            textLists.add(1, getResources().getString(R.string.bookpage2));
            textLists.add(2, getResources().getString(R.string.bookpage3));
        } else if (selection.equalsIgnoreCase("test")) {
            if (SessionUtil.getSelectLanguage(ImageShowFullView.this).equalsIgnoreCase("hindi")) {
                listOfImages.add(0, R.drawable.ph1);
                listOfImages.add(1, R.drawable.ph2);
                listOfImages.add(2, R.drawable.ph3);
                listOfImages.add(3, R.drawable.ph4);
                listOfImages.add(4, R.drawable.ph5);
                listOfImages.add(5, R.drawable.ph6);
                listOfImages.add(6, R.drawable.ph7);
                listOfImages.add(7, R.drawable.ph8);
            } else {
                listOfImages.add(0, R.drawable.p1);
                listOfImages.add(1, R.drawable.p2);
                listOfImages.add(2, R.drawable.p3);
                listOfImages.add(3, R.drawable.p4);
                listOfImages.add(4, R.drawable.p5);
                listOfImages.add(5, R.drawable.p6);
                listOfImages.add(6, R.drawable.p7);
                listOfImages.add(7, R.drawable.p8);
            }
            textLists.add(0, getResources().getString(R.string.testpage1));
            textLists.add(1, getResources().getString(R.string.testpage2));
            textLists.add(2, getResources().getString(R.string.testpage3));
            textLists.add(3, getResources().getString(R.string.testpage4));
            textLists.add(4, getResources().getString(R.string.testpage5));
            textLists.add(5, getResources().getString(R.string.testpage6));
            textLists.add(6, getResources().getString(R.string.testpage7));
            textLists.add(7, getResources().getString(R.string.testpage8));
        } else if (selection.equalsIgnoreCase("quiz")) {
            if (SessionUtil.getSelectLanguage(ImageShowFullView.this).equalsIgnoreCase("hindi")) {
                listOfImages.add(0, R.drawable.qh1);
                listOfImages.add(1, R.drawable.qh2);
                listOfImages.add(2, R.drawable.qh3);
                listOfImages.add(3, R.drawable.qh4);
                listOfImages.add(4, R.drawable.qh5);
                listOfImages.add(5, R.drawable.qh6);
                textLists.add(0, "इस भाग में आपको तीन लेवल्स के क्विज कॉम्पिटिशन में हिस्सा लेने का अवसर मिलेगी. पहले लेवल में आपको ३० प्रश्न मिलेंगी जिसमें आप १५ मिनट्स तक दिए गए प्रश्नों के उत्तर चयन कर सकते है. दूसरे लेवल के क्विज में आपको ६० प्रश्न मिलेंगी जिसमें आप ३० मिनट्स तक दिए गए प्रश्नों के उत्तरों का चयन करना पड़ेगा. इसी प्रकार, तीसरे लेवल के क्विज में आपको १०० प्रश्न मिलेंगी जिसमे आप ५० मिनट्स तक दिए गए प्रश्नों के उत्तरों का चयन कर सकते है. आपको टॉप रैंक और खास इनाम पाने के लिए प्रत्येक लेवल में प्रश्नों के उत्तरों का चयन कम से कम समाये में करना होगा.\n");
                textLists.add(1, "सी.एस.एस.डी. क्विज में हिस्सा लेने के लिए यहाँ पर क्लिक करें.");
                textLists.add(2, "सी.एस.एस.डी. क्विज की परीक्षण शुरू करने के लिए यहाँ पर क्लिक करें. ");
                textLists.add(3, "सही उत्तर चयन करने के लिए यहाँ क्लिक करें. प्रश्न सुनने के लिए यहाँ क्लिक करें.");
                textLists.add(4, "सही उत्तर चयन करके यहाँ पर क्लिक करें और अगले  प्रश्नों का भी इसी तरह चयन करें.");
                textLists.add(5, "आपको सी.एस.एस.डी. क्विज स्तर - ई पूरा करने के बाद इस स्क्रीन पर आपका स्कोर देखने को मिलेगा. एसे ही आप सभी सी.एस.एस.डी. टेस्ट को करते जाइये और अपना हुनर बढ़ाते जाइये.");
            } else {
                listOfImages.add(0, R.drawable.q1);
                listOfImages.add(1, R.drawable.q2);
                listOfImages.add(2, R.drawable.q3);
                listOfImages.add(3, R.drawable.q4);
                listOfImages.add(4, R.drawable.q5);
                listOfImages.add(5, R.drawable.q6);
                listOfImages.add(6, R.drawable.q7);
                listOfImages.add(7, R.drawable.q8);
                textLists.add(0, "In this section you will get an opportunity to participate in three different levels of C.S.S.D. quiz competition. In first level of C.S.S.D. quiz competition, you will get 30 questions. Wherein, you will get 15 minutes to select correct answers out of these 30 questions.  In second level of C.S.S.D. quiz competition, you will get 60 questions. Wherein, you will get 30 minutes to select correct answers out of these 60 questions.    In third level of C.S.S.D. quiz competition, you will get 100 questions. Wherein, you will get 50 minutes to select correct answers out of these 100 questions.");
                textLists.add(1, "To get handsome rewards and to be listed in top bracket of winners you must try to select correct answers in all levels of C.S.S.D. Quiz competition in minimum time. ");
                textLists.add(2, "Click here to participate in C.S.S.D. Quiz Level - I.");
                textLists.add(3, "Click here to continue.");
                textLists.add(4, "Click here to start C.S.S.D. Quiz competition.");
                textLists.add(5, "Click here to select a correct answer. Click here to listen to the question.");
                textLists.add(6, "Click here after selecting your answer and complete all the questions in the same manner.");
                textLists.add(7, "After completing C.S.S.D. Quiz level - I, this screen will appear to show your score, time and Date. Similarly, continue to solve all C.S.S.D. Quiz Levels and continue to develop your skills.");
            }
        } else if (selection.equalsIgnoreCase("videos")) {
            if (SessionUtil.getSelectLanguage(ImageShowFullView.this).equalsIgnoreCase("hindi")) {
                listOfImages.add(0, R.drawable.vh1);
            } else {
                listOfImages.add(0, R.drawable.v1);
            }

            textLists.add(0, getResources().getString(R.string.videopage1));
        }
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new CustomPagerAdapter(this));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReading) {
                    stopAudio();
                    isReading = false;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
                } else {
                    if (0 < textLists.size()) {
                        if (isPlayFirstTime){
                            speak(textLists.get(0));
                        }else {
                            speak(textLists.get(pagePosition));
                        }
                    }
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_silent));
                }

            }
        });
        findViewById(R.id.imageCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
                finish();
            }
        });
        updateImage();

    }
    private class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return listOfImages.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((RelativeLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(listOfImages.get(position));


            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(final int position) {
                    textToSpeech.stop();
                    isPlayFirstTime=false;
                    pagePosition=position;
                    speak(textLists.get(position));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position == (listOfImages.size() - 1)) {
                        textLists.clear();
                        textToSpeech.stop();
                        ImageShowFullView.this.finish();
                    }
                }
            });
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    public void speak(String text) {
        if (textToSpeech != null) {
            isReading = true;
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end");
            textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, params);
        }
    }

    private TextToSpeech.OnInitListener mOnInitListener = new TextToSpeech.OnInitListener() {

        @SuppressWarnings("deprecation")
        @Override
        public void onInit(int status) {
            int result;
            if (SessionUtil.getSelectLanguage(ImageShowFullView.this).equals("hindi")) {
                result = textToSpeech.setLanguage(new Locale("hi"));
            } else {
                result = textToSpeech.setLanguage(new Locale("en", "IN"));
            }

            result = textToSpeech.setOnUtteranceCompletedListener(ImageShowFullView.this);
            dialog.dismiss();
            speak(textLists.get(0));
            isPlayFirstTime=true;
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        textToSpeech.stop();
    }
    @Override
    public void onUtteranceCompleted(String uttId) {
        //Not working
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
                isReading = false;
            }
        });
    }
    public void stopAudio() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
    public void updateImage() {
       /* ViewPagerAdapter adapter = new ViewPagerAdapter(this, AdvertisementsList);
        galleryViewPager = (ViewPager) findViewById(R.id.galleryVpager);
        galleryViewPager.setAdapter(adapter);
        galleryViewPager.setCurrentItem(0);

        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new FixedSpeedScroller(galleryViewPager.getContext());
            mScroller.set(galleryViewPager, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        final LinearLayout vDots = (LinearLayout) findViewById(R.id.vDots);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        param.setMargins(10, 0, 0, 0);
        vDots.removeAllViews();
        for (int j = 0; j < listOfImages.size(); j++) {
            ImageView img = new ImageView(this);
            img.setImageResource(j == 0 ? R.drawable.ic_circle_selected
                    : R.drawable.ic_circle);
            vDots.addView(img, param);
            if (j == 0) {
                vDots.setTag(img);
            }
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                if (vDots == null || vDots.getTag() == null)
                    return;
                ((ImageView) vDots.getTag())
                        .setImageResource(R.drawable.ic_circle);
                ((ImageView) vDots.getChildAt(pos))
                        .setImageResource(R.drawable.ic_circle_selected);
                vDots.setTag(vDots.getChildAt(pos));
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
