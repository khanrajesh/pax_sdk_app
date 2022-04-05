package com.matm.matmsdk.matm1;

public class MicroAtmContract {



    public interface View {

        void checkRequestCode(String status, String message, MicroAtmResponse microAtmResponse);
        void checkEmptyFields();
        void showLoader();
        void hideLoader();


    }


    interface UserActionsListener {
        void performRequestData(String retailer, String token, MicroAtmRequestModel microAtmRequestModel);

    }

}
