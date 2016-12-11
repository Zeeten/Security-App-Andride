package com.acmatics.securityguardexchange.common;

/**
 * Created by Nidhi on 3/30/2015.
 */
public class GcmRegistrationConstant {
    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */
    public static String SENDER_ID = "754488766826";
    public static final String DEVELOPER_KEY = "AIzaSyDozWbQ_Nfduy51zs0Gk_AyKNqV06xmR70";
    /**
     * Tag used on log messages.
     */
    public static final String TAG = "GCM PUSH NOTIFICATION";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
}
