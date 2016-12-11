package com.acmatics.securityguardexchange.gcm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.acmatics.securityguardexchange.common.GcmRegistrationConstant;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This class act as a Thread to register device in the google could messaging server for push notification
 */
public class GCMRegistrationThread extends AsyncTask<String, String, String>{

	private OnGCMTokenRegistered onGCMTokenRegistered;
	private GoogleCloudMessaging gcm;
	private Context context;
	private final String TAG = GCMRegistrationThread.this.getClass().getSimpleName(); 
	private static int FROM_WHICH_SCREEN = 10;

	public void setOnGcmTokenRegistered(OnGCMTokenRegistered onGcmTokenRegistered){
		this.onGCMTokenRegistered = onGcmTokenRegistered;
	}
	public GCMRegistrationThread(Context context) {
		this.context = context;
		gcm = GoogleCloudMessaging.getInstance(context);
	}
	@Override
	protected String doInBackground(String... params) {
		String regid = "";
		try {
			if (gcm == null) {
				gcm = GoogleCloudMessaging.getInstance(context);
			}
			regid = gcm.register(GcmRegistrationConstant.SENDER_ID);
			return regid;
		}catch(Exception ae){
			Log.e("", "Error in GCM Registration"+ae.getMessage());
			return "";
		}
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (!result.equals("")) {
			Log.e("","Registration ID Received : "+result);
			if (this.onGCMTokenRegistered!= null) {
				this.onGCMTokenRegistered.onGCMTokenRegisteredSucessfully(true, result);
			}else{
				this.onGCMTokenRegistered.onGCMTokenRegisteredSucessfully(false, null);
			}
		}
	} 

}
