package com.parse.starter.services;

/**
 * Created by sugaddam on 7/25/2015.
 */

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.parse.ParseQuery;
import com.parse.starter.ui.activity.ForwardPayActivity;
import com.parse.starter.R;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;

import java.util.List;

public class AcceptForwardService extends Service{
    private static CurrentUser instance = CurrentUser.getInstance();
    private NotificationManager mNotificationManager;
    Notification.Builder builder;

    private static final String TAG = "ForwardService";

    private boolean isRunning  = false;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");

        isRunning = true;
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void issueNotification( String request_user, String amount,String merch_nm) {
        String msg = request_user + " requested to pay for " + amount;
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Constructs the Builder object.
        builder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Payment Request")
                        .setContentText(msg)
                        .setDefaults(Notification.DEFAULT_ALL) // requires VIBRATE permission
                        .setAutoCancel(true)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(msg));

        /*
         * Clicking the notification itself displays ResultActivity, which provides
         * UI for snoozing or dismissing the notification.
         * This is available through either the normal view or big view.
         */
        Intent resultIntent = new Intent(this, ForwardPayActivity.class);
        resultIntent.putExtra("request_user",request_user);
        resultIntent.putExtra("amount",amount);
        resultIntent.putExtra("merchant_nm",merch_nm);

        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Because clicking the notification opens a new ("special") activity, there's
        //` no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        // Including the notification ID allows you to update the notification later on.
        mNotificationManager.notify(0, builder.build());

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "ForwardService onStartCommand");
        //Creating new thread for my service
        //Always write your long running tasks in a separate thread, to avoid ANR
        new Thread(new Runnable() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void run() {
                while(true) {
                    if(isRunning){
                        try {
                            ParseQuery<com.parse.starter.parse.Notification> query = ParseQuery.getQuery("Notification");
                            query.whereEqualTo("forward_to", instance.getUser());
                            query.whereEqualTo("status","U");
                            List<com.parse.starter.parse.Notification> requests =  query.find();
                            if (requests.size()>0){
                                Log.i(TAG, requests.toString());
                                com.parse.starter.parse.Notification request = requests.get(0);
                                request.put("status","P");
                                String  request_user = ((User)request.getParseObject("request_user")).getName();
                                String amount = request.get("amount").toString();
                                String merchant_name = request.get("merchant_nm").toString();
                                Log.i(TAG,request_user + " requested to pay for "+ amount  );
                                try {
                                    request.save();
                                } catch (com.parse.ParseException e1) {
                                    e1.printStackTrace();
                                }
                                issueNotification(request_user, amount, merchant_name);
                            }

                            Thread.sleep(5000);
                        } catch (Exception e) {

                        }
                    } else {
                        break;
                    }
                }
            }
        }).start();

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {

        isRunning = false;

        Log.i(TAG, "Service onDestroy");
    }
}