package com.parse.starter.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.starter.services.models.ProductListAdapter;
import com.parse.starter.services.models.Product;
import com.parse.starter.services.models.SearchModel;
import com.parse.starter.services.MyntraSearchService;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import com.parse.starter.R;

/**
 * Created by 11162 on 16/07/15.
 */
public class SearchListActivity extends ActionBarActivity {
    private MyntraSearchService myntraSearchService;

    private static final String TAG = SearchListActivity.class.getSimpleName();

    public static final String INTENT_EXTRA_QUERY_KEY = "search_key";

    private CircularProgressBar progressBar;

    private RecyclerView searchList;
    private GridLayoutManager gridLayoutManager;
    private ProductListAdapter productListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        progressBar = (CircularProgressBar)findViewById(R.id.progress);
        searchList = (RecyclerView)findViewById(R.id.product_list);
        String searchQuery = getIntent().getStringExtra(INTENT_EXTRA_QUERY_KEY);
        if(TextUtils.isEmpty(searchQuery)) {
            throw new RuntimeException("Cannot run search activity without a search term");
        }

        Toast.makeText(this,"search:"+searchQuery,Toast.LENGTH_SHORT).show();
        setUpApis();
        myntraSearchService.getSearchResults(searchQuery, new Callback<SearchModel>() {
            @Override
            public void success(SearchModel searchModel, Response response) {
                progressBar.setVisibility(View.GONE);
                if(searchModel.data.results.products.size() == 0){
                    Toast.makeText(SearchListActivity.this, "no products matching search term",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                gridLayoutManager = new GridLayoutManager(SearchListActivity.this,2);
                if(productListAdapter == null){
                    productListAdapter = new ProductListAdapter(searchModel,new ProductListAdapter.MyItemClickListener() {
                        @Override
                        public void onItemClicked(Product product) {
                            Toast.makeText(SearchListActivity.this,"selected styleid:"+product.styleid,Toast.LENGTH_SHORT).show();
                            Intent searchIntent = new Intent(SearchListActivity.this,ProductActivity.class);
                            searchIntent.putExtra(ProductActivity.INTENT_STYLEID,product.styleid);
                            startActivity(searchIntent);
                        }
                    });
                }
                searchList.setLayoutManager(gridLayoutManager);
                searchList.setAdapter(productListAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchListActivity.this,"Failed to fetch products",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpApis() {
        final RestAdapter.Builder builder = new RestAdapter.Builder();
        myntraSearchService = builder.setEndpoint("http://developer.myntra.com").setLogLevel(RestAdapter.LogLevel.FULL).build().create(MyntraSearchService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
