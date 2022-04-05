package com.matm.matmsdk.aepsmodule.cashwithdrawal;



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

public interface CoreCashWithdrawalAPI {
    //"/aeps/transaction/mob/v1"
    @POST()
    Call<CashWithdrawalResponse> checkCashWithDrawal(@Header("Authorization") String token, @Body CashWithdrawalRequestModel body, @Url String url);

    @POST()
    Call<AepsResponse> checkCashWithDrawall(@Header("Authorization") String token, @Body CashWithdrawalRequestModel body, @Url String url);


}

