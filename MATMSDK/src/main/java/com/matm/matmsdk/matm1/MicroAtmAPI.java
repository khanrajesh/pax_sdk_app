package com.matm.matmsdk.matm1;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MicroAtmAPI {

    @POST("/sendEncryptedRequestNFSforClient/{retailer}")
    Call<MicroAtmResponse> checkRequestCode(@Path("retailer") String retailer, @Header("Authorization") String token, @Body MicroAtmRequestModel body);

}
