package com.parse.starter.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.R;
import com.parse.starter.parse.Card;
import com.parse.starter.parse.Transaction;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;

import java.util.List;


public class ForwardPayActivity extends ActionBarActivity {
    private static CurrentUser instance = CurrentUser.getInstance();
    /** Called when the activity is first created. */


    void make_payment(String  merchant_nm, String merchant_card, String amount){
       // AftTrans.doTrans();
       // OctTrans.doTrans();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), SearchActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.accept_forward_pay_request);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        setTitle("Share Pay Request ");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String request_user = extras.getString("request_user");
            final String amount = extras.getString("amount");
            final String merchant_nm = extras.getString("merchant_nm");
            final String merchant_card = extras.getString("merchantCard");
            TextView content = (TextView) findViewById(R.id.content);
            content.setText(request_user + " requested forward pay for " + merchant_nm);
            TextView tv_amount = (TextView) findViewById(R.id.amount);
            tv_amount.setText(amount);

            findViewById(R.id.payButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ForwardPayActivity.this);
                        builder.setTitle("Are you sure you want to Pay ?");

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User rqst_usr = null;
                                ParseQuery<User> usr_query = ParseQuery.getQuery("User");
                                usr_query.whereEqualTo("name", request_user);
                                try {

                                    rqst_usr = usr_query.find().get(0);
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
                                    query.whereEqualTo("forward_to", instance.getUser());
                                    query.whereEqualTo("request_user", rqst_usr);
                                    query.whereEqualTo("status", "S");
                                    query.orderByDescending("createdAt");
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                        @Override
                                        public void done(List<ParseObject> requests, com.parse.ParseException e) {
                                            for (ParseObject request : requests) {
                                                request.put("status", "P");
                                                try {
                                                    request.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                            }
                                        });
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                make_payment(merchant_nm, merchant_card, amount);

                                Toast.makeText(getApplicationContext(), "Payment request for " + request_user + " is successfull", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(ForwardPayActivity.this, SearchActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                User rqst_usr = null;
                                ParseQuery<User> usr_query = ParseQuery.getQuery("User");
                                usr_query.whereEqualTo("name", request_user);
                                try {

                                    rqst_usr = usr_query.find().get(0);
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
                                    query.whereEqualTo("forward_to", instance.getUser());
                                    query.whereEqualTo("request_user", rqst_usr);
                                    query.whereEqualTo("status", "S");
                                    query.orderByDescending("createdAt");
                                    query.findInBackground(new FindCallback<ParseObject>() {
                                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                                        @Override
                                        public void done(List<ParseObject> requests, com.parse.ParseException e) {
                                            for (ParseObject request : requests) {
                                                request.put("status", "C");
                                                try {
                                                    request.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
                                            }
                                        }
                                    });
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(getApplicationContext(), "Payment request for " + request_user + " is cancelled", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(ForwardPayActivity.this, SearchActivity.class);
                                startActivity(intent);
                            }
                        });
                        builder.show();
                }
            });

            //service onDestroy callback method will be called
            findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseQuery<User> usr_query = ParseQuery.getQuery("User");
                    usr_query.whereEqualTo("name", request_user);
                    User rqst_usr = null;
                    try {
                        rqst_usr = usr_query.find().get(0);
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Notification");
                        query.whereEqualTo("forward_to", instance.getUser());
                        query.whereEqualTo("request_user", rqst_usr);
                        query.whereEqualTo("status", "S");
                        query.orderByDescending("createdAt");
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                            @Override
                            public void done(List<ParseObject> requests, com.parse.ParseException e) {
                                for (ParseObject request : requests) {
                                    request.put("status", "C");
                                    try {
                                        request.save();
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(getApplicationContext(), "Cancelled forward payment request " + request_user, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
