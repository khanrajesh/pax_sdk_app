package com.matm.matmsdk.aepsmodule.balanceenquiry;


import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;

/**
 * ReportContract class handles the communication between ReportView and Presenter
 *
 * @author Subhalaxmi Panda
 * @date 23/06/18.
 */
public class CoreBalanceEnquiryContract {


    /**
     * View interface sends report list to ReportActivity
     */
    public interface View {

        /**
         * checkLoginStatus() checks  whether login is a failure or success. Status "0" is failure and Status "1" is success
         */
        void checkBalanceEnquiryStatus(String status, String message, BalanceEnquiryResponse balanceEnquiryResponse);

        void checkBalanceEnquiryAEPS2(String status, String message, AepsResponse balanceEnquiryResponse);

        /**
         * checkEmptyFields() validates whether username and password are empty
         */
        void checkEmptyFields();
        void showLoader();
        void hideLoader();


    }

    /**
     * UserActionsListener interface checks the load of Reports
     */
    interface UserActionsListener {
        void performBalanceEnquiry(String aadharNumber,String token, BalanceEnquiryRequestModel balanceEnquiryRequestModel);
    }


}

