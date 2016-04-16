package com.parse.starter;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.starter.parse.Account;
import com.parse.starter.parse.Card;
import com.parse.starter.parse.Notification;
import com.parse.starter.parse.Transaction;
import com.parse.starter.parse.User;

public class ParseApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();


    // Initialize Crash Reporting.
    //ParseCrashReporting.enable(this);

    // Enable Local Datastore.
    //Parse.enableLocalDatastore(this);

    ParseObject.registerSubclass(Transaction.class);
    ParseObject.registerSubclass(User.class);
    ParseObject.registerSubclass(Account.class);
    ParseObject.registerSubclass(Notification.class);
    ParseObject.registerSubclass(Card.class);

    // Add your initialization code here
    Parse.initialize(this, "wFotluHcDow5Jie9eUpB7UMpuzdBH0egbwDqjKE7", "wu6R6Cu508cwuFNKiU9NPFfGNHbTYEMtM2UOFQzs");
    ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    defaultACL.setPublicReadAccess(true);
  }
}
