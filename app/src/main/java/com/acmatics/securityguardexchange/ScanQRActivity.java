package com.acmatics.securityguardexchange;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.acmatics.securityguardexchange.app.SecuritySkillsApplication;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanQRActivity extends Activity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    private SecuritySkillsApplication app;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        app = (SecuritySkillsApplication) getApplication();// Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
        app.trackActivity("ScanQR Activity");
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        //Toast.makeText(ScanQRActivity.this, rawResult.getText() + " - " + rawResult.getBarcodeFormat().toString(), Toast.LENGTH_SHORT).show();
        if(rawResult.getText().startsWith("https://www.youtube.com/")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rawResult.getText())));
        } else {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(rawResult.getText())));
            //Toast.makeText(ScanQRActivity.this, "This QR code is not containing Youtube URL.", Toast.LENGTH_SHORT).show();
        }
        mScannerView.resumeCameraPreview(this);
    }

}
