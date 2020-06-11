package com.example.image_search.search_result;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.image_search.ApiService;
import com.example.image_search.Product;
import com.example.image_search.Result;
import com.example.image_search.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SearchResultInteractorImpl implements SearchResultContract.Interactor {
    private List<Product> productList = new ArrayList<>();
    private List<Product> productsFilter;

    @Override
    public void filterSortAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces, int selectedSort) {

        if(userSelectedEcommerces.size() == 0 || userSelectedEcommerces.size() == 3) {
            productsFilter = new ArrayList<>(productList);
        }
        else{
            productsFilter = new ArrayList<>();
            for (Product p : productList) {
                for (int i = 0; i < userSelectedEcommerces.size(); i++) {
                    if (p.getSiteName() != null && p.getSiteName().contains(ecommerceList[userSelectedEcommerces.get(i)])) {
                        productsFilter.add(p);
                    }
                }
            }
        }
        switch (selectedSort) {
            case 1:
                sort(false);
                break;
            case 2:
                sort(true);
                break;
        }
    }

    @Override
    public void sort(final boolean ascending) {
        Collections.sort(productsFilter, new Comparator<Product>() {
            @Override
            public int compare(Product p1, Product p2) {
                if(ascending)// sort ascendig
                    return p1.getPrice() - p2.getPrice();
                else // sort descending
                    return p2.getPrice() - p1.getPrice();
            }

        });
    }

    @Override
    public void setProduct(List<Product> mProductList) {
        productList = mProductList;
    }

    @Override
    public List<Product> getProduct(){
        return productList;
    }

    @Override
    public List<Product> getProductFilter() {
        return productsFilter;
    }

}

