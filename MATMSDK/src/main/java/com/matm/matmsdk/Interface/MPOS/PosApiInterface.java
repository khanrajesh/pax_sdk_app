package com.matm.matmsdk.Interface.MPOS;

import com.google.gson.JsonObject;
import com.matm.matmsdk.Model.MPOS.PosTransResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PosApiInterface {
   /* @Headers({
            "content-type: application/json;charset=UTF-8"
    })*/
    @POST("/DONTDEPLOY/doCashWithdral")
    //@POST("/IttyMAppAuth/doCashWithdral")
    Call<PosTransResponse> SendTransRequest(@Header("Authorization") String token,@Body JsonObject posTransRequest);

//https://matm.iserveu.tech/DONTDEPLOY/

   /* @Headers({
            "content-type: application/json;charset=UTF-8"
    })*/
    @POST("/DONTDEPLOY/doBalanceInquiry")
    //@POST("/IttyMAppAuth/doBalanceInquiry")
    Call<PosTransResponse> SendTransRequestBalanceInq(@Header("Authorization") String token,@Body JsonObject posTransRequest);
}
