package com.parse.starter.services.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 11162 on 15/07/15.
 */
public class Product {
    @SerializedName("search_image")
    public String search_image;
    @SerializedName("product")
    public String product;
    @SerializedName("price")
    public String price;
    @SerializedName("styleid")
    public String styleid;
}
