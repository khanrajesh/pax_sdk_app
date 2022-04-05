package com.matm.matmsdk.aepsmodule.balanceenquiry;




import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * This class represents the Login API, all endpoints can stay here.
 *
 *
 * @author Subhalaxmi Panda
 * @date 22/06/18.
 *
 */

public interface CoreBalanceEnquiryAPI {
    //"/aeps/balanceenquiry/mob/v1"
    @POST()
    Call<BalanceEnquiryResponse> checkBalanceEnquiry(@Header("Authorization") String token, @Body BalanceEnquiryRequestModel body, @Url String url);

    @POST()
    Call<AepsResponse> checkBalanceEnquiryy(@Header("Authorization") String token, @Body BalanceEnquiryRequestModel body, @Url String url);


}

