package com.parse.starter.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.starter.ui.fragment.ManagerFragment;
import com.parse.starter.R;
import com.parse.starter.parse.User;
import com.parse.starter.util.CurrentUser;

import java.util.List;


public class Login extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    public void loginUser(View view) {
        EditText phoneText = (EditText) findViewById(R.id.phone);
        String phone = phoneText.getText().toString();

        ParseQuery<User> query = ParseQuery.getQuery("User");
        query.whereEqualTo("phone",phone);

        User a = new User();
        Boolean foundUser = false;

        try{
            List<User> all= query.find();
            if(all.size() != 0 ) {
                foundUser = true;
                a = all.get(0);
            }
        } catch (ParseException e){
            e.printStackTrace();
        }

        Intent intent = new Intent(this,MenuActivity.class);
        String type = getIntent().getStringExtra("Type");
        intent.putExtra("Type",type);
        if(foundUser) {
            //if(type.equals("Merchant")) intent = new Intent(this,MerchantSide.class);
            //else intent = new Intent(this, ManagerFragment.class);
            CurrentUser.getInstance().setUser(a);
        } else {
            intent.putExtra("Phone",phone);
        }
        startActivity(intent);
    }
}
