package com.acmatics.securityguardexchange.util;

public class PaymentConstants {
	public static final String PARAMETER_SEP = "&";
	public static final String PARAMETER_EQUALS = "=";
	public static final String TRANS_URL = "https://secure.ccavenue.com/transaction/initTrans";
    public static final String ACCESS_CODE = "AVUE63CL31CH35EUHC";
    public static final String MERCHANT_ID = "70287";
    public static final String CURRENCY = "INR";
    public static final String REDIRECT_URL = "http://118.67.250.173:8080/securityskillsws/resources/merchant/ccavResponseHandler.jsp";
    public static final String CANCEL_URL = "http://118.67.250.173:8080/securityskillsws/resources/merchant/ccavResponseHandler.jsp";
    public static final String RSA_URL = "http://118.67.250.173:8080/securityskillsws/resources/merchant/GetRSA.jsp";

}