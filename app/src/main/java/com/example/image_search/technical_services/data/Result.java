package com.example.image_search.technical_services.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Result {
    @SerializedName("data")
    ArrayList<Product> result;

    public ArrayList<Product> getResult() {
        return result;
    }
}