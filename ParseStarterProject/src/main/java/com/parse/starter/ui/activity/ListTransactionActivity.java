package com.parse.starter.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.starter.R;
import com.parse.starter.parse.Account;
import com.parse.starter.parse.Transaction;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;
import com.parse.starter.util.functions;

import java.util.ArrayList;
import java.util.List;


public class ListTransactionActivity extends Activity {
    private User curr = null;
    private String phone;
    private User to = null;
    ParseQueryAdapter<ParseObject> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaction);
        curr = CurrentUser.getInstance().getUser();
        phone = getIntent().getStringExtra("ToPhone");

        adapter = new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                to = functions.getUserFromPhone(phone);

                ParseQuery<ParseObject> givenTo = ParseQuery.getQuery("Transaction");
                givenTo.whereEqualTo("From", curr);
                givenTo.whereEqualTo("To", to);

                ParseQuery<ParseObject> takenFrom = ParseQuery.getQuery("Transaction");
                takenFrom.whereEqualTo("From",to);
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
                    v = View.inflate(getContext(), R.layout.tran_row, null);
                }
                Transaction tran = (Transaction) object;
                TextView descriptionView = (TextView) v.findViewById(R.id.description);
                TextView amountView = (TextView) v.findViewById(R.id.amount);
                descriptionView.setText(tran.getDescription());
                amountView.setText("Rs. "+tran.getAmount());
                if(tran.getFrom().getName().compareTo(curr.getName()) ==0){
                   amountView.setTextColor(Color.RED);
                } else amountView.setTextColor(Color.GREEN);
                return v;
            }

        };

        ListView listView = (ListView) findViewById(R.id.tran_list);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_transaction, menu);
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

    public void settle(View view) {

        Boolean possible = false;

        Account currAcc = functions.getAccount(curr,to);

        Log.d("aviral","Give from user " +currAcc.getTo().getName());
        Log.d("aviral","Give to user " +currAcc.getFrom().getName());
        Log.d("aviral","amount " +currAcc.getAmount());

        if(currAcc.getFrom().equals(curr)){
            if(currAcc.getAmount() > 0)
                possible = true;
        } else if(currAcc.getAmount() < 0) {
            possible = true;
        }

        if(!possible) {
            Toast.makeText(this,to.getName()+ " can only settle this!",Toast.LENGTH_LONG).show();
            return;
        }

        Transaction tmp = new Transaction();
        tmp.setFrom(curr);
        tmp.setTo(to);

        if(currAcc.getAmount() < 0){
            tmp.setFrom(to);
            tmp.setTo(curr);
        }

        tmp.setAmount(currAcc.getAmount());
        tmp.setDescription("Settled by transaction id 34563456");
        tmp.add();

        adapter.notifyDataSetChanged();
        return;
    }

}
