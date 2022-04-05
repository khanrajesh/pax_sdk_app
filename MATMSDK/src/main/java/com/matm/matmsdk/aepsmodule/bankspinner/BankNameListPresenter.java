package com.matm.matmsdk.aepsmodule.bankspinner;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class BankNameListPresenter implements BankNameContract.UserActionsListener {
    private BankNameContract.View banknameView;
    public BankNameListPresenter(BankNameContract.View banknameView) {
        this.banknameView = banknameView;
    }

    @Override
    public void loadBankNamesList(Context context) {
        banknameView.showLoader();
       if( Constants.BANK_NAME.equalsIgnoreCase("")) {
            ArrayList<BankNameModel> bankNamesArrayList = new ArrayList<BankNameModel>();
            AndroidNetworking.get("https://us-central1-iserveustaging.cloudfunctions.net/iin/api/v1/getIIN")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Constants.BANK_NAME =response.toString();
                                JSONObject obj = new JSONObject(response.toString());
                                JSONArray jarray = obj.getJSONArray("bankIINs");
                                for (int i = 0; i < jarray.length(); i++) {
                                    BankNameModel bankNameModel = new BankNameModel();
                                    JSONObject innerObj = jarray.getJSONObject(i);
                                    String iin = innerObj.getString("IIN");
                                    String bankName = innerObj.getString("BANKNAME");
                                    bankNameModel.setIin(iin);
                                    SdkConstants.bankIIN = iin;
                                    bankNameModel.setBankName(bankName.trim());
                                    bankNamesArrayList.add(bankNameModel);
                                }

                                banknameView.hideLoader();
                                banknameView.bankNameListReady(bankNamesArrayList);
                                banknameView.showBankNames();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                banknameView.hideLoader();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            banknameView.hideLoader();

                        }
                    });
        }
        else{
           try {
               ArrayList<BankNameModel> bankNamesArrayList = new ArrayList<BankNameModel>();
               JSONObject obj = new JSONObject(Constants.BANK_NAME);
               JSONArray jarray = obj.getJSONArray("bankIINs");
               for (int i = 0; i < jarray.length(); i++) {
                   BankNameModel bankNameModel = new BankNameModel();
                   JSONObject innerObj = jarray.getJSONObject(i);
                   String iin = innerObj.getString("IIN");
                   String bankName = innerObj.getString("BANKNAME");
                   bankNameModel.setIin(iin);
                   bankNameModel.setBankName(bankName);
                   bankNamesArrayList.add(bankNameModel);
               }

               banknameView.hideLoader();
               banknameView.bankNameListReady(bankNamesArrayList);
               banknameView.showBankNames();
           } catch (JSONException e) {
               e.printStackTrace();
               banknameView.hideLoader();
           }
        }
    }

}
