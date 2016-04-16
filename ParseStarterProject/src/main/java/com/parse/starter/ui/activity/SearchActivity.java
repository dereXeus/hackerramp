package com.parse.starter.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.parse.starter.R;


public class SearchActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button searchButton = (Button)  findViewById(R.id.searchButton);
        final EditText searchTerm = (EditText) findViewById(R.id.searchBar);
        searchButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String query = searchTerm.getText().toString();
                        if(TextUtils.isEmpty(query)){
                            Toast.makeText(SearchActivity.this, "Please enter a term in sarch bar",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent searchIntent = new Intent(SearchActivity.this,SearchListActivity.class);
                        searchIntent.putExtra(SearchListActivity.INTENT_EXTRA_QUERY_KEY,query);
                        startActivity(searchIntent);
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
