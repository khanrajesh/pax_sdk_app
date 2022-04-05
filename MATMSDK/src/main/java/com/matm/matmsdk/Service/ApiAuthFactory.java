package com.matm.matmsdk.Service;


import com.matm.matmsdk.Utils.RequestInterceptor;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.matm.matmsdk.Utils.EnvData.TEST_URL;


public class ApiAuthFactory {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging) // This is used to add ApplicationInterceptor.
                    .addNetworkInterceptor(new RequestInterceptor()) //This is used to add NetworkInterceptor.
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(TEST_URL)
                    .client(okHttpClient) //The Htttp client to be used for requests
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getTestClient() {
        if (retrofit==null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(logging) // This is used to add ApplicationInterceptor.
                    //.addNetworkInterceptor(new RequestInterceptor()) //This is used to add NetworkInterceptor.
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(TEST_URL)
                    .client(okHttpClient) //The Htttp client to be used for requests
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
