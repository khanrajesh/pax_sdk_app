package com.matm.matmsdk.aepsmodule.cashwithdrawal;




/**
 * LoginPresenter class Handle Interaction between Model and View
 *
 *
 * @author Subhalaxmi Panda
 * @date 21/06/18.
 *
 */


public class CashWithdrawalAEPS2Presenter {
    /**
     * Initialize LoginView
     */
   // private CashWithDrawalContract.View cashWithDrawalContractView;
    //private AEPSAPIService aepsapiService;
    //private Session session;
    /**
     * Initialize LoginPresenter
     */
   /* public CashWithdrawalAEPS2Presenter(CashWithDrawalContract.View cashWithDrawalContractView) {
        this.cashWithDrawalContractView = cashWithDrawalContractView;
    }*/




   /* @Override
    public void performCashWithdrawal(final String token, final CashWithdrawalRequestModel cashWithdrawalRequestModel) {

            cashWithDrawalContractView.showLoader();
            if (this.aepsapiService == null) {
                this.aepsapiService = new AEPSAPIService();
            }
        AndroidNetworking.get("https://itpl.iserveu.tech/generate/v66")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String key = obj.getString("hello");
                            System.out.println(">>>>-----"+key);
                            byte[] data = Base64.decode(key, Base64.DEFAULT);
                            String encodedUrl = new String(data, "UTF-8");
                            encryptCashWithDraw(token,cashWithdrawalRequestModel,encodedUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });








    }
*/
/*
    public void encryptCashWithDraw(String token, CashWithdrawalRequestModel cashWithdrawalRequestModel, String encodedUrl){
        CashWithdrawalAPI cashWithdrawalAPI =this.aepsapiService.getClient().create(CashWithdrawalAPI.class);
        cashWithdrawalAPI.checkCashWithDrawall(token,cashWithdrawalRequestModel,encodedUrl).enqueue(new Callback<AepsResponse>() {
            @Override
            public void onResponse(Call<AepsResponse> call, Response<AepsResponse> response) {
                if(response.isSuccessful()) {
                    // String message = "";
                    if (response.body().getStatus() !=null && !response.body().getStatus().matches("")) {
                        //message = "Login Successful";
                        cashWithDrawalContractView.hideLoader();
                        cashWithDrawalContractView.checkCashWithdrawalAEPS2(response.body().getStatus(), "Cash Withdrawal Success",response.body());
                    }else{
                        cashWithDrawalContractView.hideLoader();
                        cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed",null);
                        Log.v("laxmi","hf"+cashWithDrawalContractView);
                    }
                }else{

                    if(response.errorBody() != null) {
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            AepsResponse errorResponse = gson.fromJson(mJson, AepsResponse.class);
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalAEPS2(errorResponse.getStatus(), "Cash Withdrawal Failed",errorResponse);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            cashWithDrawalContractView.hideLoader();
                            cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed",null);
                        }
                    }else{
                        cashWithDrawalContractView.hideLoader();
                        cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed",null);

                    }
                    }
            }
            @Override
            public void onFailure(Call<AepsResponse> call, Throwable t) {
                cashWithDrawalContractView.hideLoader();
                cashWithDrawalContractView.checkCashWithdrawalAEPS2("", "Cash Withdrawal Failed",null);
            }
        });
    }
*/
}
