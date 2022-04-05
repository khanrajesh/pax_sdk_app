package com.matm.matmsdk.Interface.Login;


import com.matm.matmsdk.Model.LoginResponse;
import com.matm.matmsdk.Model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginApiInterface {
    @POST("/getlogintoken")
    Call<LoginResponse> doLogin(@Body User user);
}
