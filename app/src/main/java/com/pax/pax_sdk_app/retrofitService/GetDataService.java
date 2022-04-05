package com.pax.pax_sdk_app.retrofitService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GetDataService {

    @GET("/generate/v1")
    Call<ResponseBody> getV1();

    @GET("/generate/v2")
    Call<ResponseBody> getV2();

    @GET("/generate/v4")
    Call<ResponseBody> getV5();
}
