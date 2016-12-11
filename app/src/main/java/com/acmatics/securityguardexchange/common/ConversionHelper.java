package com.acmatics.securityguardexchange.common;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.acmatics.securityguardexchange.bean.AffiliatesBean;
import com.acmatics.securityguardexchange.bean.NotificationBean;
import com.acmatics.securityguardexchange.bean.UserBean;
import com.acmatics.securityguardexchange.util.SessionUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Rubal on 09-12-2015.
 */
public class ConversionHelper {
    public static final String REQUEST_STATUS_REJECT = "Reject";
    public static final String REQUEST_STATUS_OUTOFNETWORK = "OutOfNetwork";

    public static final String REQUEST_STATUS_ACCEPT = "Accept";


    public static List<NotificationBean> getNotificationList(JSONObject notificationJson) {
        List<NotificationBean> notificationList = new ArrayList<NotificationBean>();
        if (notificationJson.has("notificationList")) {
            JSONArray notificationArray = null;
            try {
                notificationArray = notificationJson.getJSONArray("notificationList");
                for (int i = 0; i < notificationArray.length(); i++) {
                    JSONObject notification = notificationArray.getJSONObject(i);
                    NotificationBean notificationBean = getNotificationBean(notification);
                    notificationList.add(notificationBean);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return notificationList;
    }

    private static NotificationBean getNotificationBean(JSONObject notification) {
        NotificationBean notificationBean = new NotificationBean();
        try {
            notificationBean.setTitle(notification.getString("title"));
            notificationBean.setNotificationDetails(notification.getString("notificationMessage"));
            notificationBean.setDate(notification.getString("createdDate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notificationBean;
    }

    public static List<AffiliatesBean> getAffiliatesList(JSONObject jObject) {

        try {
            JSONArray affArray = jObject.getJSONArray("affiliates");

            List<AffiliatesBean> affList = new ArrayList<AffiliatesBean>();
            for (int i = 0; i < affArray.length(); i++) {
                JSONObject affJson = affArray.getJSONObject(i);
                AffiliatesBean affiliatesBean = getAffiliatesBean(affJson);
                affList.add(affiliatesBean);
            }
            return affList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static AffiliatesBean getAffiliatesBean(JSONObject jObject) throws JSONException {
        AffiliatesBean affiliatesBean = new AffiliatesBean();
        affiliatesBean.setAffName(jObject.getString("name"));
        affiliatesBean.setAffNumber(jObject.getString("phone"));
        affiliatesBean.setAffEmail(jObject.getString("emailId"));

        if(!jObject.isNull("pincode")) {
            affiliatesBean.setAffPin(jObject.getString("pincode"));
        }

        JSONObject address = jObject.getJSONObject("addressBean");
        affiliatesBean.setAffAddressLine(address.getString("addressLine"));

        if (!address.isNull("locality")) {
            affiliatesBean.setAffLocality(address.getString("locality"));
        }

        affiliatesBean.setAffCity(address.getString("city"));
        affiliatesBean.setAffState(address.getString("state"));

        if (!address.isNull("pinCode")) {
            affiliatesBean.setAffPinCode(address.getString("pinCode"));
        }

        return affiliatesBean;
    }

    public static List<UserBean> getUserList(JSONObject jObject) {
        try {
            List<UserBean> userList = new ArrayList<UserBean>();
            UserBean userData=new UserBean();
            userData.setPhone(jObject.getString("phoneNumber"));
            userData.setUserId(jObject.getInt("userId"));
            userData.setName(jObject.getString("userName"));
            userData.setEmail(jObject.getString("email"));
            userData.setUserCreated(jObject.getBoolean("userCreated"));
            userList.add(userData);

            return userList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<UserBean> userVerification(JSONObject jObject) {
        try {
            List<UserBean> userList = new ArrayList<UserBean>();
            UserBean userData=new UserBean();
            if (jObject.has("userRegistered")){
                userData.setUserRegistered(jObject.getBoolean("userRegistered"));
            }if (!jObject.isNull("phoneNumber")){
                userData.setPhone(jObject.getString("phoneNumber"));
            }
            userData.setScratchCodeVerified(jObject.getBoolean("scratchCodeVerified"));
            userList.add(userData);

            return userList;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void setAppLanguage(Activity activity) {

        if (SessionUtil.getSelectLanguage(activity).equals("english")) {
            Locale localeHindi = new Locale("en");
            Locale.setDefault(localeHindi);
            Configuration configHindi = new Configuration();
            configHindi.locale = localeHindi;
            activity.getBaseContext().getResources().updateConfiguration(configHindi, activity.getBaseContext().getResources().getDisplayMetrics());

        }else if(SessionUtil.getSelectLanguage(activity).equals("punjabi")) {
            Locale localePunjabi = new Locale("pn");
            Locale.setDefault(localePunjabi);
            Configuration configPunjabi = new Configuration();
            configPunjabi.locale = localePunjabi;
            activity.getBaseContext().getResources().updateConfiguration(configPunjabi, activity.getBaseContext().getResources().getDisplayMetrics());

        }

        else {
            Locale localeEnglish = new Locale("hi");
            Locale.setDefault(localeEnglish);
            Configuration configEnglish = new Configuration();
            configEnglish.locale = localeEnglish;
            activity.getBaseContext().getResources().updateConfiguration(configEnglish, activity.getBaseContext().getResources().getDisplayMetrics());

        }
    }

}
