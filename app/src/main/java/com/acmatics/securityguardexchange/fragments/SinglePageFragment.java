package com.acmatics.securityguardexchange.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.acmatics.securityguardexchange.BookReaderActivity;
import com.acmatics.securityguardexchange.R;
import com.acmatics.securityguardexchange.XhtmlToText;
import com.acmatics.securityguardexchange.XmlUtil;
import com.acmatics.securityguardexchange.util.SessionUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.domain.SpineReference;

public class SinglePageFragment extends Fragment implements OnUtteranceCompletedListener {
    private int position;
    private String basePathForRead;
    private static TextToSpeech textToSpeech;
    private String lines = null;

    private ArrayList<String> mText;
    private FloatingActionButton fab;
    private boolean isReading;
    private final static int CHECK_TTS_ACTIVITY_ID = 2;
    private ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_single_page, container, false);
        mText = new ArrayList<String>();
        fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isReading) {
                    stopAudio();
                    isReading = false;
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
                } else {
                    /*if (0 < mText.size()) {
                        speak(mText.get(0));
                    }*/
                    for(String text : mText) {

                         speak(text);

                    }
                    fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_silent));
                }

            }
        });
        checkTextToSpeech(getActivity(), CHECK_TTS_ACTIVITY_ID);
        StringBuilder string = new StringBuilder();
        SpineReference spineReference = ((BookReaderActivity) getActivity()).getSpineList().get(position);
        Resource res = spineReference.getResource();

        try {
            InputStream is = res.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    lines = string.append(line + "\n").toString();
//                     text=reader.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //do something with stream
        } catch (IOException e) {
            e.printStackTrace();
        }

        lines = lines.replace("../", "");
        final String mimeType;
        if(basePathForRead.equalsIgnoreCase("file:///storage/emulated/0/books//InteractiveSessionEnglis/") || basePathForRead.equalsIgnoreCase("file:///storage/emulated/0/books//InteractiveSessionHind/")) {
            mimeType = "application/xhtml+xml";
        } else {
            mimeType = "text/html";
        }
        final String encoding = "UTF-8";

        WebView wb = (WebView) rootView.findViewById(R.id.book_view);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setTextZoom(200);
        wb.loadDataWithBaseURL(basePathForRead, lines, mimeType, encoding, null);

          /**/
        int no = position;
        mText.clear();
        SpineReference spineReference1 = ((BookReaderActivity) getActivity()).getSpineList().get(no);
        Resource resource = spineReference1.getResource();
        try {
            InputStream inPutStream = resource.getInputStream();
            XmlUtil.parseXmlResource(inPutStream, new XhtmlToText(mText), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wb.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
               rootView.findViewById(R.id.loadingProgress).setVisibility(View.GONE);
            }
        });
        return rootView;
    }

    public void setPosition(int position) {
        stopAudio();
        this.position = position;
    }

    public String getBasePathForRead() {
        return basePathForRead;
    }

    public void setBasePathForRead(String basePathForRead) {
        this.basePathForRead = basePathForRead;
    }

//    @Override
//    public void onInit(int status) {
//        if (status == TextToSpeech.SUCCESS) {
//            int result;
//            if (SessionUtil.getSelectLanguage(getActivity()).equals("hindi")) {
//                result = textToSpeech.setLanguage(new Locale("hi"));
//            } else {
//                result = textToSpeech.setLanguage(new Locale("en", "IN"));
//            }
//            if (result == TextToSpeech.LANG_MISSING_DATA
//                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                Log.e("TTS", "Language is not supported");
//            }
//        } else {
//            Log.e("TTS", "Initilization Failed");
//        }
//    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void speakOut(String text) {
//        textToSpeech.setPitch((float) 1);
//        textToSpeech.setSpeechRate((float) 1);
//        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
//        isReading = true;
//    }

    @Override
    public void onDestroy() {
        stopAudio();
        super.onDestroy();
    }
//
//    @Override
//    public void onDetach() {
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//        super.onDetach();
//    }


//    public void speak(TextToSpeechWrapper ttsWrapper) {
//        mTtsWrapper = ttsWrapper;
//        if (0 < mText.size()) {
//            speakOut(mText.get(0));
//        }
//    }

    /*
        * Should return with epub or chapter to load
        */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHECK_TTS_ACTIVITY_ID) {
            checkTestToSpeechResult(getActivity(), resultCode);
            return;
        }
    }

    public void checkTextToSpeech(Activity activity, int activityId) {
        if (textToSpeech == null) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Intent checkIntent = new Intent();
                checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
                activity.startActivityForResult(checkIntent, activityId);
            } else {
                textToSpeech = new TextToSpeech(activity, mOnInitListener);
            }
        }
    }

    private TextToSpeech.OnInitListener mOnInitListener = new TextToSpeech.OnInitListener() {

        @SuppressWarnings("deprecation")
        @Override
        public void onInit(int status) {
            int result;
            if (SessionUtil.getSelectLanguage(getActivity()).equals("hindi")) {
                result = textToSpeech.setLanguage(new Locale("hi"));
            } else {
                result = textToSpeech.setLanguage(new Locale("en", "IN"));
            }


            result= textToSpeech.setOnUtteranceCompletedListener((SinglePageFragment.this));
        }
    };

    public void checkTestToSpeechResult(Context context, int resultCode) {
        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            textToSpeech = new TextToSpeech(context, mOnInitListener);
        }
    }

    public void speak(String text) {
        String finalText;
        if(basePathForRead.equalsIgnoreCase("file:///storage/emulated/0/books//InteractiveSessionEnglis/")) {
            String t1 = text.replaceAll("Interactive Session 1" + "\t\t\n" + "		Q", "");
            t1 = t1.replaceAll("Absolutely Correct!!", "");
            t1 = t1.replaceAll("Wrong!!", "");
            t1 = t1.replaceAll("No. You haven\\'t been careful. Try again.", "");
            t1 = t1.replaceAll("CheckTry AgainReset", "");
            t1 = t1.replaceAll("0", "");
            t1 = t1.replaceAll("1", "");
            finalText = t1.substring(22, t1.length());
        } else {
            finalText = text;
        }
        if (textToSpeech != null) {
            HashMap<String, String> params = new HashMap<String, String>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "end");
            textToSpeech.speak(finalText, TextToSpeech.QUEUE_ADD, params);

        }
        isReading = true;
    }

    public void stopAudio() {
        if (textToSpeech != null && textToSpeech.isSpeaking()) {
            textToSpeech.stop();
        }
    }
    @Override
    public void onUtteranceCompleted(String uttId) {
        //Not working
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker));
                isReading = false;
            }
        });


    }

}
