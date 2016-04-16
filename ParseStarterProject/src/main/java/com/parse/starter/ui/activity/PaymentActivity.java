package com.parse.starter.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.parse.starter.R;
import com.parse.starter.services.AcceptForwardService;

public class PaymentActivity extends Activity {

    String merchantName,amount,merchantCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        this.merchantName = getIntent().getExtras().getString("merchantName");
        this.merchantCard = getIntent().getExtras().getString("merchantCard");
        this.amount = getIntent().getExtras().getString("amount");
        Intent intent = new Intent(this,AcceptForwardService.class);
        startService(intent);

    }


    public void onSplitPayClick(View view) {
        Intent intent = new Intent(this, SplitPaymentActivity.class);
        intent.putExtra("merchantName",merchantName);
        intent.putExtra("amount",amount);
        intent.putExtra("merchantCard", merchantCard);
        startActivity(intent);
    }

    public void onRemotePayClick(View view) {
        Intent intent = new Intent(this, RemotePaymentActivity.class);
        intent.putExtra("merchantName",merchantName);
        intent.putExtra("amount",amount);
        intent.putExtra("merchantCard", merchantCard);
        startActivity(intent);
    }

}