package com.acmatics.securityguardexchange.gcm;

public interface OnGCMTokenRegistered {
	public abstract void onGCMTokenRegisteredSucessfully(boolean isEverythingOk, String GCMToken);
}
