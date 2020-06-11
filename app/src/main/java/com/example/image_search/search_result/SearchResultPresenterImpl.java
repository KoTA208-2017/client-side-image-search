package com.example.image_search.search_result;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.image_search.ApiService;
import com.example.image_search.Result;
import com.example.image_search.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

public class SearchResultPresenterImpl implements SearchResultContract.Presenter {

    private SearchResultContract.View searchResultView;
    private SearchResultContract.Interactor searchResultInteractor;
    private ProgressDialog progressDialog;

    public SearchResultPresenterImpl(SearchResultContract.View searchResultView) {
        this.searchResultView = searchResultView;
        this.searchResultInteractor = new SearchResultInteractorImpl();
    }

    @Override
    public void uploadImage(String imagePath, Context context) {
        // Set Progress Dialog
        progressDialog = searchResultView.setProgressDialog();

        // Request
        String BASE_URL = "";
        try {
            BASE_URL = "http://" + Util.getProperty("IP",context) + "/";
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressDialog.show();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService service = retrofit.create(ApiService.class);

        //Create a file object using file path
        File file = new File(imagePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), fileReqBody);

        Call<Result> call = service.uploadImage(part);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.code() == 200) {
                    //get response
                    Log.d("message", "succes");
                    progressDialog.dismiss();
                    searchResultInteractor.setProduct(response.body().getResult());
                    searchResultView.showProduct(searchResultInteractor.getProduct());
                } else {
                    progressDialog.dismiss();
                    searchResultView.intentToEmptyActivity();
                }
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                progressDialog.dismiss();
                searchResultView.intentToEmptyActivity();
            }
        });
    }

    @Override
    public void filterSortAction(String[] ecommerceList, ArrayList<Integer> userSelectedEcommerces, int selectedSort, Context context) {
        searchResultInteractor.filterSortAction(ecommerceList, userSelectedEcommerces, selectedSort);
        searchResultView.showProduct(searchResultInteractor.getProductFilter());
    }
}