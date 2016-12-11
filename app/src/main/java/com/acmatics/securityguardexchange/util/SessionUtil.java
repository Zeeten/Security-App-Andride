package com.acmatics.securityguardexchange.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.acmatics.securityguardexchange.BooksActivity;

public class SessionUtil {

    public static final String USER_NAME = "userName";
    public static final String FULL_NAME = "name";
    public static final String USER_ID = "userId";
    public static final String DEVICE_ID = "deviceId";
    public static final String PROFILE_PIC_PATH = "profilePicPath";
    public static final String SCRATCH_CODE = "scratchCode";
    public static final String USER_REGISTERED = "userRegistered";
    public static final String  GCM_REGISTRATION_ID= "gcmRegistrationId";
    public static final String  SCRATCH_CODE_VERIFY= "scratchCodeVerify";
    public static final String  IDENTIFY_USER_REGISTERED= "indentifyUserRegistered";
    public static final String  START_TRIAL= "startTrial";
    public static final String TRIAL_EXPIRED = "trialExpired";
    private static SessionUtil manager;
    public static final String USER_SESSION_PREF = "userSessionPref";
    public static final String TRIAL_START_DATE = "trialStartDate";
    public static final String ACTIVATION_DATE = "activationDate";
    public static final String SELECT_LANGUAGE = "selectLanguage";
    public static final String IS_FIRST_RUN = "isFirstRun";
    public static final String IS_BOOK_RUN = "isBookRun";
    public static final String IS_PRACTICE_RUN = "isPractice";
    public static final String IS_TEST_RUN = "isTestRun";
    public static final String IS_VIDEO_RUN = "isVideoRun";
    public static final String READ_PRACTISE_TEST = "readPractiseTest";

    public static SessionUtil getInstance() {
        if (manager == null) {
            manager = new SessionUtil();
        }
        return manager;
    }

    public static SharedPreferences getUserSessionPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return context.getSharedPreferences(USER_SESSION_PREF, Context.MODE_PRIVATE);
    }

    public static void saveValueForKey(Context context, String key, String value) {
        SharedPreferences preferences =  getUserSessionPreferences(context);;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getValueForKey(Context context, String key) {
        SharedPreferences preferences =  getUserSessionPreferences(context);;
        return preferences.getString(key, "");
    }

    public static void saveSelectedLanguage(String startTrail, Context context) {
        SharedPreferences preferences =  getUserSessionPreferences(context);;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SELECT_LANGUAGE, startTrail);
        editor.commit();
    }

    public static String getSelectLanguage(Context context) {
        SharedPreferences preferences =  getUserSessionPreferences(context);;
        return preferences.getString(SELECT_LANGUAGE, "");
    }

    public static void saveTrialExpired(Boolean trialExpired, Context context) {
        SharedPreferences preferences = (SharedPreferences) getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(TRIAL_EXPIRED, trialExpired);
        editor.commit();
    }
    public static void saveTrialStartDate(String trialStartDate, Context context) {
        SharedPreferences preferences = (SharedPreferences) context.getSharedPreferences(SessionUtil.TRIAL_START_DATE, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("trialStartDate", trialStartDate);
        editor.commit();
    }
    public static void saveActivationDate(String activationDate, Context context) {
        SharedPreferences preferences =  getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ACTIVATION_DATE, activationDate);
        editor.commit();
    }

    public static String getTrialStartDate(Context context) {
        SharedPreferences preferences = (SharedPreferences) context.getSharedPreferences(SessionUtil.TRIAL_START_DATE, 0);
        return preferences.getString("trialStartDate", "");
    }

    public static void saveStartTrial(String startTrail, Context context) {
        SharedPreferences preferences = (SharedPreferences) context.getSharedPreferences(SessionUtil.START_TRIAL, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("startTrial", startTrail);
        editor.commit();
    }

    public static String getStartTrial(Context context) {
        SharedPreferences preferences = (SharedPreferences) context.getSharedPreferences(SessionUtil.START_TRIAL, 0);
        return preferences.getString("startTrial", "");
    }

    public static void saveIndentifyUserRegistered(String verify, Context context) {
        SharedPreferences preferences = (SharedPreferences) context.getSharedPreferences(SessionUtil.IDENTIFY_USER_REGISTERED, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("indentifyUserRegistered", verify);
        editor.commit();
    }

    public static String getIndentifyUserRegistered(Context context) {
        SharedPreferences preferences = (SharedPreferences) context.getSharedPreferences(SessionUtil.IDENTIFY_USER_REGISTERED, 0);
        return preferences.getString("indentifyUserRegistered", "");
    }

    public static void scratchCodeVerify(String verify,Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(SCRATCH_CODE_VERIFY, verify);
        editor.commit();
    }

    public static String getScratchCodeVerify(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString(SCRATCH_CODE_VERIFY, "");
    }

    public static void gcmRegistrationId(String gcmRegistrationId, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("gcmRegistrationId", gcmRegistrationId);
        editor.commit();
    }

    public static String getGcmRegistrationId(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("gcmRegistrationId", "");
    }

    public static void userRegistered(String userRegistered, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userRegistered", userRegistered);
        editor.commit();
    }

    public static String getUserRegistered(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("userRegistered", "");
    }

    public static void saveScratchCode(String code, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("scratchCode", code);
        editor.commit();
    }

    public static String getScratchCode(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("scratchCode", "");
    }

    public static void saveUserId(String userId, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userId", userId);
        editor.commit();
    }

    public static String getUserId(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString(USER_ID, "");
    }

    public static void saveUserName(String userName, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userName", userName);
        editor.commit();
    }

    public static String getUserName(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("userName", "");
    }

    public static void saveNumber(String number, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("phoneNumber", number);
        editor.commit();
    }

    public static String getNumber(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("phoneNumber", "");
    }

    public static void saveAddress(String address, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("address", address);
        editor.commit();
    }

    public static String getAddress(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("address", "");
    }

    public static void saveCity(String city, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("city", city);
        editor.commit();
    }

    public static String getCity(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("city", "");
    }

    public static void saveState(String state, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("state", state);
        editor.commit();
    }

    public static String getState(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("state", "");
    }

    public static void saveCountry(String country, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("country", country);
        editor.commit();
    }

    public static String getCountry(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("country", "");
    }

    public static void savePinCode(String pincode, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("pincode", pincode);
        editor.commit();
    }

    public static String getPinCode(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("pincode", "");
    }

    public static void saveEmail(String email, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", email);
        editor.commit();
    }

    public static String getEmail(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("email", "");
    }

    public static void saveCompany(String company, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("company", company);
        editor.commit();
    }

    public static String getCompany(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString("company", "");
    }

    public static void updateProfilePicInSession(Context context, String profilePicPath) {
        final SharedPreferences prefs = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROFILE_PIC_PATH, profilePicPath);
        editor.commit();
    }

    public static void saveFirstFollow(Context context, String firstRun) {
        final SharedPreferences prefs = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IS_FIRST_RUN, firstRun);
        editor.commit();
    }

    public static void saveBookRun(Context context, String firstRun) {
        final SharedPreferences prefs = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IS_BOOK_RUN, firstRun);
        editor.commit();
    }

    public static void savePracticeRun(Context context, String firstRun) {
        final SharedPreferences prefs = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IS_PRACTICE_RUN, firstRun);
        editor.commit();
    }

    public static void saveTestRun(Context context, String firstRun) {
        final SharedPreferences prefs = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IS_TEST_RUN, firstRun);
        editor.commit();
    }

    public static void saveVideoRun(Context context, String firstRun) {
        final SharedPreferences prefs = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(IS_VIDEO_RUN, firstRun);
        editor.commit();
    }
    public static void savePractiseTest(String read, Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(READ_PRACTISE_TEST, read);
        editor.commit();
    }

    public static String getPractiseTest(Context context) {
        SharedPreferences preferences = getUserSessionPreferences(context);
        return preferences.getString(READ_PRACTISE_TEST, "");
    }
}
