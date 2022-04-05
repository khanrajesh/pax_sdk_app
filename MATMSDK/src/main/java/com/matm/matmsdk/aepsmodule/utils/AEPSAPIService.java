package com.matm.matmsdk.aepsmodule.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.matm.matmsdk.Utils.SdkConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AEPSAPIService {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                    .connectTimeout(360, TimeUnit.SECONDS)
                    .readTimeout(360, TimeUnit.SECONDS)
                    .writeTimeout(360, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(SdkConstants.BASE_URL).client (okHttpClient  )
                    .addConverterFactory(GsonConverterFactory.create(gson))

                    .build();
        }
        return retrofit;
    }
}
