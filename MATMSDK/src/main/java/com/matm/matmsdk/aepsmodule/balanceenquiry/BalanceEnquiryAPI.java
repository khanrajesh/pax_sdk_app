package com.matm.matmsdk.aepsmodule.balanceenquiry;




import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.ministatement.StatementResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;


public interface BalanceEnquiryAPI {
    // @POST("/SDK/aepsapi/balanceenquiry/{retailer}")
    //@POST("/CALL_BACK_TEST/aepsapi/balanceenquiry/{retailer}")
    @POST("/aepsapi/balanceenquiry/{retailer}")
    Call<BalanceEnquiryResponse> checkBalanceEnquiry(@Path("retailer") String retailer, @Header("Authorization") String token, @Body BalanceEnquiryRequestModel body);



    @POST()
    Call<BalanceInqAeps2Model> checkBalanceEnquiryy(@Header("Authorization") String token, @Body BalanceEnquiryAEPS2RequestModel body, @Url String url);

    @POST()
    Call<BalanceInqAeps2Model> checkStatementEnquiryy(@Header("Authorization") String token, @Body BalanceEnquiryAEPS2RequestModel body, @Url String url);

}

