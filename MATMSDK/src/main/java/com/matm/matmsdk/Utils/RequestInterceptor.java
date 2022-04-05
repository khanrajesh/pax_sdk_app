package com.matm.matmsdk.Utils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RequestInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {

        Request originalRequest = chain.request();

        Request newRequest = originalRequest.newBuilder()
                .addHeader("Authorization", EnvData.token)
                .build();

        Response response = chain.proceed(newRequest);
        return response;
    }
}
