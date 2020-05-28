package com.example.image_search;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {
    @Multipart
    @POST("retrieval/image")
    Call<Result> uploadImage(@Part MultipartBody.Part file);
}
