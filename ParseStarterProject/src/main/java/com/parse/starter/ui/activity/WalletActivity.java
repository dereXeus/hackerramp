package com.parse.starter.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.parse.Account;
import com.parse.starter.parse.User;
import com.parse.starter.ui.fragment.ManagerFragment;
import com.parse.starter.R;
import com.parse.starter.services.AcceptForwardService;
import com.parse.starter.util.CurrentUser;
import com.parse.starter.util.functions;


public class WalletActivity extends Activity {

    private User curr = null;
    private String phone;
    private ParseQueryAdapter<ParseObject> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        curr = CurrentUser.getInstance().getUser();

        adapter = new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                Log.d("WalletActivity","User name ====*******====xxxx=====>>> " + curr.getName());

                ParseQuery<ParseObject> givenTo = ParseQuery.getQuery("Account");
                givenTo.whereEqualTo("From", curr);

                ParseQuery<ParseObject> takenFrom = ParseQuery.getQuery("Account");
                takenFrom.whereEqualTo("To",curr);

                List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
                queries.add(givenTo);
                queries.add(takenFrom);

                ParseQuery mainQuery = ParseQuery.or(queries);


                return mainQuery;

            }
        })
        {
            @Override
            public View getItemView(ParseObject object, View v, ViewGroup parent) {
                if (v == null) {
                    v = View.inflate(getContext(), R.layout.list_row, null);
                }
                Account acc = (Account) object;

                Log.d("WalletActivity","Account : " + acc.getAmount() + " " + acc.getFrom().getName() + " " + acc.getTo().getName());

                TextView descriptionView = (TextView) v.findViewById(R.id.description);
                TextView amountView = (TextView) v.findViewById(R.id.amount);

                amountView.setText("Rs. " + acc.getAmount());

                User giveTo = acc.getFrom();

                if(acc.getFrom().equals(curr)) {
                    giveTo = acc.getTo();
                }

                descriptionView.setText(giveTo.getName());
                //Bitmap scaled = Bitmap.createScaledBitmap(photo,60,60,true);
                //imgView.setImageBitmap(scaled);

                return v;
            }

        };

        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Account account = (Account) adapter.getItem(position);
                Intent intent = new Intent(WalletActivity.this, ListTransactionActivity.class);
                if (account.getFrom().equals(curr))
                    intent.putExtra("ToPhone", account.getTo().getPhone());
                else intent.putExtra("ToPhone", account.getFrom().getPhone());
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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




}
