package com.parse.starter.ui.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import com.parse.starter.R;
import com.parse.starter.parse.Notification;
import com.parse.starter.parse.Transaction;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;

import java.util.List;

public class RemotePaymentActivity extends Activity {
    Spinner spinner;
    String merchantName;
    String merchantCard;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_payment);
        spinner = (Spinner)findViewById(R.id.users);
        this.merchantName = "CENTRAL PERK";
        this.amount = "500";
        this.merchantCard = "abc";
        TextView tv_mct_nmm = (TextView)findViewById(R.id.tv_mcht_nm);
        tv_mct_nmm.setText(this.merchantName);
        TextView tv_amt =  (TextView)findViewById(R.id.tv_amnt);
        tv_amt.setText(this.amount+" Rs");
        populateDialog(spinner);
    }

    public void onPayBtnClick(View view){
        try {
            Notification notification = new Notification();
            notification.setAmount(amount);
            notification.setMerchantName(merchantName);
            notification.setForwardToUser((User) spinner.getSelectedItem());
            notification.setRequestUser(CurrentUser.getInstance().getUser());
            notification.setStatus("U");notification.setPaymentType("R");
            notification.save();
            new RemotePaymentTask(this).execute("");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    void populateDialog(final Spinner spinner){
        final ParseQueryAdapter<ParseObject> adapter = new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                // Here we can configure a ParseQuery to our heart's desire.
                ParseQuery query = new ParseQuery("User");
                query.whereNotEqualTo("phone", CurrentUser.getInstance().getUser().getPhone());
                return query;
            }
        }) {
            @Override
            public View getItemView(ParseObject object, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.custom_spinner, null);
                }
                User usr = (User) object;
                TextView nameView = (TextView) v.findViewById(R.id.Name);
                nameView.setText(usr.getName() + "    (" + usr.getPhone() + ")");
                return v;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }
        };

        spinner.setAdapter(adapter);

    }


    private class RemotePaymentTask extends AsyncTask<String, Void, String> {
        ParseQuery<Notification> notificationParseQuery;
        Activity activity;
        String status;

        RemotePaymentTask(Activity activity){
            this.activity = activity;
            this.notificationParseQuery = ParseQuery.getQuery("Notification");
            this.notificationParseQuery.whereEqualTo("request_user", CurrentUser.getInstance().getUser());
        }

        private String getPaymentStatus(){
            try {
                List<Notification> notifications = notificationParseQuery.find();
                return notifications.get(0).getStatus();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return "U";
        }

        @Override
        protected String doInBackground(String... params) {
            int trials = 0;
            while(true){
                try {
                    Log.d("RemotePayActivity","Trial : " + trials);
                    if(trials>20){
                        break;
                    }
                    String status = getPaymentStatus();
                    Log.d("RemotePayActivity","Status : " + getPaymentStatus());
                    if("P".equals(status)){
                        return  "Payment Succesfull .. ";
                    }else if("C".equals(status)){
                        return  "Payment Cancelled  by " + ((User)spinner.getSelectedItem()).getName() + "  .. ";
                    }
                    Thread.sleep(1000);
                    trials = trials + 1;
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    break;
                }
            }
            return "Payment Request Timeout .. ";
        }

        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
            try {
                List<Notification> clearNotifications = notificationParseQuery.find();
                for (Notification clearNotification:clearNotifications){
                    clearNotification.delete();
                    clearNotification.save();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Button payButton = (Button)activity.findViewById(R.id.payButton);
            payButton.setEnabled(true);
            if("Payment Succesfull .. ".equals(result)){

                Transaction transaction = new Transaction();
                transaction.setAmount((int)Double.parseDouble(amount));
                transaction.setDescription(merchantName);
                transaction.setTo((User) spinner.getSelectedItem());
                transaction.setFrom(CurrentUser.getInstance().getUser());
                transaction.add();

                Intent intent = new Intent(RemotePaymentActivity.this,MenuActivity.class);
                startActivity(intent);
            }
        }

        @Override
        protected void onPreExecute() {
            Button payButton = (Button) activity.findViewById(R.id.payButton);
            payButton.setEnabled(false);
        }

    }
}
