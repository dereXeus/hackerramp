package com.parse.starter.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.R;
import com.parse.starter.parse.Notification;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;

import java.util.ArrayList;
import java.util.List;


public class SplitPaymentActivity extends Activity {

    ArrayList<User> splitUsers = new ArrayList<User>();
    ArrayAdapter<User> splitAdapter;
    ListView splitListView;
    String merchantName;
    String merchantCard;
    String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split);

        ArrayList<User> contactList = getContactList();

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

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        splitAdapter = new ArrayAdapter<User>(this, 0, splitUsers) {
            @Override
            public View getView(int position, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.custom_spinner, null);
                }
                User usr = getItem(position);
                TextView nameView = (TextView) v.findViewById(R.id.Name);
                nameView.setText(usr.getName() + "    (" + usr.getPhone() + ")");
                return v;
            }

        };

        ListView splitListView = (ListView) findViewById(R.id.split_list);
        splitListView.setAdapter(splitAdapter);

        splitListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                User tmp = splitUsers.get(position);
                AlertDialog.Builder adb = new AlertDialog.Builder(SplitPaymentActivity.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to remove " + tmp.getName() + " ?");
                final int positionToRemove = position;
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        splitUsers.remove(positionToRemove);
                        splitAdapter.notifyDataSetChanged();
                    }
                });
                adb.show();
            }
        });

        this.merchantName = getIntent().getExtras().getString("merchantName");
        this.merchantCard = getIntent().getExtras().getString("merchantCard");
        this.amount = getIntent().getExtras().getString("amount");
        TextView tv_mct_nmm = (TextView)findViewById(R.id.tv_mcht_nm);
        tv_mct_nmm.setText(this.merchantName);
        TextView tv_amt =  (TextView)findViewById(R.id.tv_amnt);
        tv_amt.setText(this.amount + " Rs");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_split, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<User> getContactList() {
        ArrayList<User> list = new ArrayList<User>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                                .replaceAll("-", "").replaceAll(" ", "");

                        User tmp = new User();
                        tmp.setName(name);
                        tmp.setPhone(phoneNo.substring(phoneNo.length() - 10));
                        list.add(tmp);
                        //Toast.makeText(NativeContentProvider.this, "Name: " + name + ", Phone No: " + phoneNo, Toast.LENGTH_SHORT).show();
                    }
                    pCur.close();
                }
            }

        }

        return list;
    }

    public void addSplitUser(View view) {
        Spinner spinner  = (Spinner) findViewById(R.id.spinner);
        User curr = (User)spinner.getSelectedItem();

        if (!splitUsers.contains(curr)) {
            splitUsers.add(curr);
            splitAdapter.notifyDataSetChanged();
            spinner.setSelection(0);
        }
    }

    public void splitAmount(View view) {
        if (splitUsers.size() == 0) {
            Toast.makeText(this, "Please add people to split", Toast.LENGTH_LONG);
            return;
        }
        int totalAmount = Integer.parseInt(amount);
        int indAmount = totalAmount/splitUsers.size();
        String mcht_nm = merchantName;

        for(User a:splitUsers){
            Notification tmp = new Notification();
            tmp.setForwardToUser(a);
            tmp.setAmount(indAmount + "");
            tmp.setMerchantName(mcht_nm);
            tmp.setRequestUser(CurrentUser.getInstance().getUser());
            tmp.setStatus("U"); tmp.setPaymentType("S");
            try {
                tmp.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        (new SplitPaymentTask()).execute();

    }

    private class SplitPaymentTask extends AsyncTask<String, Void, Integer> {
        ParseQuery<Notification> notificationParseQuery;
        String status;
        String merchant_card;
        int amount_share;


        SplitPaymentTask(){
            notificationParseQuery = ParseQuery.getQuery("Notification");
            notificationParseQuery.whereEqualTo("request_user", CurrentUser.getInstance().getUser());
            this.notificationParseQuery.whereEqualTo("pay_type","S");
        }

        private List<Notification> getPaymentStatus(){
            try {
                List<Notification> notifications = notificationParseQuery.find();
                return notifications;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

        /* write fund transfer code here for the */
        public void makePayment(){

        }

        @Override
        protected Integer doInBackground(String... params) {
            int trials = 0;
            while(true){
                try {
                    if(trials>20){
                        break;
                    }
                    List<Notification> status = getPaymentStatus();
                    if(checkPaymentApproved(status)){
                        makePayment();
                        return  0;
                    }
                    Thread.sleep(5000);
                    trials = trials + 1;
                } catch (InterruptedException e) {
                    Thread.interrupted();
                    break;
                }
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer result) {
            String toastString ;
            if(result==0){
                toastString = "Payment Successfull.. ";
            }else{
                toastString = "Payment Request Timeout.. ";
            }
            Toast.makeText(SplitPaymentActivity.this, toastString, Toast.LENGTH_LONG).show();

            try {
                List<Notification> clearNotifications = notificationParseQuery.find();
                for (Notification clearNotification:clearNotifications){
                    clearNotification.delete();
                    clearNotification.saveEventually();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Button payButton = (Button) findViewById(R.id.split);
            payButton.setEnabled(true);
        }

        @Override
        protected void onPreExecute() {
            Button payButton = (Button) findViewById(R.id.split);
            payButton.setEnabled(false);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private boolean checkPaymentApproved(List<Notification> all){
        boolean approved=true;
        for(Notification  a:all){
            int index = splitUsers.indexOf(a.getRequestUser());
            if (a.getStatus().compareTo("P") == 0) {
            }else if (a.getStatus().compareTo("C")==0){
               approved = false;
            }
        }
        return approved;
    }

    private int changeState(List<Notification> all){
        try {
            for(Notification  a:all) {
                a.setStatus("A");
                a.save();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 1;
    }
}