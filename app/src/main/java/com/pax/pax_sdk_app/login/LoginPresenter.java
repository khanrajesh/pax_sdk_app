package com.pax.pax_sdk_app.login;

import android.util.Base64;
import android.util.Log;


import com.pax.pax_sdk_app.retrofitService.DMTRetrofitInstance;
import com.pax.pax_sdk_app.retrofitService.GetDataService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter implements LoginContract.UserInteraction {

    private LoginContract.View view;

    public  LoginPresenter(LoginContract.View view){
        this.view = view;
    }

    @Override
    public void getV1Response(String base_url) {
        GetDataService getDataService = DMTRetrofitInstance.getretrofitInstance(base_url).create(GetDataService.class);
        getDataService.getV1().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    try {

                        String res = response.body().string();

                        Log.i("res  ! :::: ",res);


                        JSONObject jsonObject = new JSONObject(res);
                        String url = jsonObject.getString("hello");
                        byte[] data = Base64.decode(url, Base64.DEFAULT);
                        String base64 = new String(data, "UTF-8");

                        Log.i("base64 1  ! :::: ",base64);


                        view.fetchedV1Response(true, base64);
                    }catch (Exception exc){
                        exc.getMessage();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i(" ResponseBody ::::::",":::::::: ResponseBody :::::");
            }
        });
    }
}
