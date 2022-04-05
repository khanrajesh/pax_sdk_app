package com.matm.matmsdk.Service;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.matm.matmsdk.Utils.EnvData.TEST_URL;

public class TempApiAuthFactory {
    private static Retrofit retrofit = null;
    public static Retrofit getTestClient() {
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(360, TimeUnit.SECONDS)
                .readTimeout(360, TimeUnit.SECONDS)
                .writeTimeout(360, TimeUnit.SECONDS)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl(TEST_URL).client (okHttpClient)
                //.baseUrl(TEST_URL_TEMP).client (okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }
}
