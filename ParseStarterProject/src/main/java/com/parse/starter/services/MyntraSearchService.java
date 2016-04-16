package com.parse.starter.services;

import android.content.pm.LauncherApps;

import com.parse.starter.services.models.SearchModel;
import com.parse.starter.services.models.Response;

import javax.security.auth.callback.Callback;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

/**
 * Created by 11162 on 15/07/15.
 */
public interface MyntraSearchService {
    @Headers({
            "Accept: application/vnd.github.v3.full+json",
            "User-Agent: Retrofit-Sample-App",
            "cookie: xtp=5000"
    })
    @GET("/search/data/{q}")
    void getSearchResults(@Path("q") String query, retrofit.Callback<SearchModel> searchModelCallBack);

    @GET("/style/{id}")
    void getProductDetails(@Path("id") String styleID, retrofit.Callback<Response> responseCallback);
}
