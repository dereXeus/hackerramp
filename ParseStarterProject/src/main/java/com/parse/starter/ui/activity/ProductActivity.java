package com.parse.starter.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.starter.R;
import com.parse.starter.services.models.Product;
import com.parse.starter.services.models.Response;
import com.parse.starter.services.MyntraSearchService;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;


public class ProductActivity extends Activity {

    MyntraSearchService myntraSearchService;

    private static final String TAG = SearchListActivity.class.getSimpleName();

    public static final String INTENT_STYLEID = "search_key";

    TextView responseTxt;

    String amount;
    String description;

    String url = "http://developer.myntra.com/style/550809";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product);
        String styleid = getIntent().getStringExtra(INTENT_STYLEID);
        TextView tv = (TextView) findViewById(R.id.price);
        tv.setText("\u20B91799");
        //tv.setText("This is strike-thru");
        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        setUpApis();
        myntraSearchService.getProductDetails(styleid, new Callback<Response>() {
            @Override
            public void success(Response response, retrofit.client.Response resp){
                showData(response);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(ProductActivity.this,"Failed to fetch products",Toast.LENGTH_SHORT).show();
                Log.d("Error",error.getMessage());
            }
        });

        Button shareBuyButton = (Button) findViewById(R.id.share_buy);
        shareBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this,PaymentActivity.class);
                intent.putExtra("amount",amount);
                intent.putExtra("description",description);
                startActivity(intent);
            }
        });
    }


    private void showData(Response response){
        TextView company = (TextView) findViewById(R.id.company);
        company.setText(response.data.brandName);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(response.data.productDisplayName);
        TextView dPrice = (TextView) findViewById(R.id.discountedPrice);
        dPrice.setText("\u20B9" + response.data.discountedPrice);
        //tv.setText("This is strike-thru");
        TextView price = (TextView) findViewById(R.id.price);
        price.setText("\u20B9" + response.data.price);
        price.setPaintFlags(price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ImageView img = (ImageView) findViewById(R.id.styleImage);
        Picasso.with(this).load(response.data.styleImages._default.imageURL).into(img);

        this.amount = response.data.discountedPrice.toString();
        this.description = response.data.productDisplayName;

    }

    private void setUpApis() {
        final RestAdapter.Builder builder = new RestAdapter.Builder();
        myntraSearchService = builder.setEndpoint("http://developer.myntra.com").setLogLevel(RestAdapter.LogLevel.FULL).build().create(MyntraSearchService.class);
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
