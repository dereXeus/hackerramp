package com.parse.starter.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import javax.xml.transform.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRCodeReader extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    static String contents;
    public static final int REQUEST_CODE = 1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        //Log.i("TAG", "before activity"); // Prints scan results
        setContentView(mScannerView);                // Set the scanner view as the content view
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        //startActivityForResult(, 0);
        //Log.i("TAG", "after activity"); // Prints scan results

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }
    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(com.google.zxing.Result result) {
        // Do something with the result here
        //Log.i("TAG", result.getText()); // Prints scan results
        // Log.i("TAG", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
        Intent resultIntent = new Intent(this,RemotePaymentActivity.class);
        String[] res = result.getText().split("\n");
        resultIntent.putExtra("merchantName", res[0].substring(res[0].indexOf(":")+1));
        resultIntent.putExtra("amount", res[1].substring(res[1].indexOf(":") + 1));
        resultIntent.putExtra("merchantCard", res[2].substring(res[2].indexOf(":")+1));
        this.startActivity(resultIntent);
    }
}