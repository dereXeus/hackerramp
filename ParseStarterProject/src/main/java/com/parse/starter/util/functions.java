package com.parse.starter.util;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.parse.Account;
import com.parse.starter.parse.User;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created by avagarwa on 7/25/2015.
 */
public class functions {
    public static User getUserFromPhone(String phone){
        ParseQuery<User> query = ParseQuery.getQuery("User");
        query.whereEqualTo("phone", phone);
        User res = new User();
        try {
            res = query.find().get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return res;
    };

    public static Account getAccount(User from,User to){
        ParseQuery<Account> query = ParseQuery.getQuery("Account");
        query.whereEqualTo("To", to);
        query.whereEqualTo("From",from);

        ParseQuery<Account> query2 = ParseQuery.getQuery("Account");
        query2.whereEqualTo("To", from);
        query2.whereEqualTo("From",to);

        List<ParseQuery<Account>> queries = new ArrayList<ParseQuery<Account>>();
        queries.add(query);
        queries.add(query2);

        ParseQuery<Account> mainQuery = ParseQuery.or(queries);

        Account res = null;
        try{
            res = mainQuery.find().get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return res;
    };

    public static Bitmap openPhoto(String contactNumber,Context context) {

        contactNumber = Uri.encode(contactNumber);
        long phoneContactID = new Random().nextInt();
        Cursor contactLookupCursor = context.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                        Uri.encode(contactNumber)),new String[] {ContactsContract.PhoneLookup.DISPLAY_NAME,
                        ContactsContract.PhoneLookup._ID},
                null, null, null);
        while(contactLookupCursor.moveToNext()){
            phoneContactID = contactLookupCursor.getInt(contactLookupCursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
        }
        contactLookupCursor.close();

        Log.e("openPhoto", phoneContactID+"");

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, phoneContactID);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[] {ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            Log.e("openPhoto","No record found");
            return null;
        }
        try {
            Log.e("openPhoto",""+cursor.getCount());
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    Log.e("openPhoto","No data found");
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
            Log.e("openPHoto","no records found");

        } finally {
            cursor.close();
        }
        return null;
    }

}
