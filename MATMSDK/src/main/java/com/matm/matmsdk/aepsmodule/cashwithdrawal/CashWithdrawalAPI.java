package com.matm.matmsdk.aepsmodule.cashwithdrawal;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface CashWithdrawalAPI {
//    @POST("/SDKTest/aepsapi/transaction/{retailer}")
   // @POST("/CALL_BACK_TEST/aepsapi/transaction/{retailer}")
   @POST("/aepsapi/transaction/{retailer}")

    Call<CashWithdrawalResponse> checkCashWithDrawal(@Path("retailer") String retailer, @Header("Authorization") String token, @Body CashWithdrawalRequestModel body);

    @POST()
    Call<AepsResponse> checkCashWithDrawall(@Header("Authorization") String token, @Body CashWithdrawalAEPS2RequestModel body, @Url String url);


}

