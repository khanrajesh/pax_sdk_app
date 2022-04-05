package com.matm.matmsdk.aepsmodule.balanceenquiry;

public class BalanceEnquiryAEPS2Presenter  {
    /**
     * Initialize LoginView
     */
   // private BalanceEnquiryContract.View balanceEnquiryContractView;
   // private AEPSAPIService aepsapiService;
    //private Session session;
    /**
     * Initialize LoginPresenter
     */
    /*public BalanceEnquiryAEPS2Presenter(BalanceEnquiryContract.View balanceEnquiryContractView) {
        this.balanceEnquiryContractView = balanceEnquiryContractView;
    }*/


  /* @Override
    public void performBalanceEnquiry(final String token, final BalanceEnquiryRequestModel balanceEnquiryRequestModel) {
            balanceEnquiryContractView.showLoader();

            if (this.aepsapiService == null) {
                this.aepsapiService = new AEPSAPIService();
            }

        AndroidNetworking.get("https://itpl.iserveu.tech/generate/v65")
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
                           encryptBalanceEnquiry(token,balanceEnquiryRequestModel,encodedUrl);

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
    public void encryptBalanceEnquiry(String token, BalanceEnquiryRequestModel balanceEnquiryRequestModel, String encodedUrl){
        BalanceEnquiryAPI balanceEnquiryAPI =this.aepsapiService.getClient().create(BalanceEnquiryAPI.class);

        balanceEnquiryAPI.checkBalanceEnquiryy(token,balanceEnquiryRequestModel,encodedUrl).enqueue(new Callback<AepsResponse>() {
            @Override
            public void onResponse(Call<AepsResponse> call, Response<AepsResponse> response) {

                if(response.isSuccessful()) {
                    if (response.body().getStatus() !=null && !response.body().getStatus().matches("")) {

                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2(response.body().getStatus(), "Balance Enquiry Success",response.body());
                    }else{
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed",null);
                    }

                }else{

                    if(response.errorBody() != null) {
                        JsonParser parser = new JsonParser();
                        JsonElement mJson = null;
                        try {
                            mJson = parser.parse(response.errorBody().string());
                            Gson gson = new Gson();
                            AepsResponse errorResponse = gson.fromJson(mJson, AepsResponse.class);
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkBalanceEnquiryAEPS2(errorResponse.getStatus(), "Balance Enquiry Failed",errorResponse);

                        } catch (IOException ex) {
                            ex.printStackTrace();
                            balanceEnquiryContractView.hideLoader();
                            balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed",null);
                        }
                    }else{
                        balanceEnquiryContractView.hideLoader();
                        balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed",null);

                    }
                   }
            }

            @Override
            public void onFailure(Call<AepsResponse> call, Throwable t) {
                balanceEnquiryContractView.hideLoader();
                balanceEnquiryContractView.checkBalanceEnquiryAEPS2("", "Balance Enquiry Failed",null);
            }
        });
    }
*/


}
