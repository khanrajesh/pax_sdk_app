package com.pax.pax_sdk_app.retrofitService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DMTRetrofitInstance {

    private static Retrofit retrofit;
    public static  Retrofit getretrofitInstance(String baseurl){

        if(retrofit == null){

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseurl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}

