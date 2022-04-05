package com.matm.matmsdk.Interface.Dashboard;


import com.matm.matmsdk.Model.Dashboard.Dashboard;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface DashboardApiInterface {
    @Headers({
            "content-type: application/json;charset=UTF-8"
    })
    @GET("/user/dashboard.json")
    Call<Dashboard> getDashboard();
}
