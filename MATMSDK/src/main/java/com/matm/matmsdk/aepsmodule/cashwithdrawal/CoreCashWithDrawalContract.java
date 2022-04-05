package com.matm.matmsdk.aepsmodule.cashwithdrawal;

/**
 * ReportContract class handles the communication between ReportView and Presenter
 *
 * @author Subhalaxmi Panda
 * @date 23/06/18.
 */
public class CoreCashWithDrawalContract {


    /**
     * View interface sends report list to ReportActivity
     */
    public interface View {

        /**
         * checkLoginStatus() checks  whether login is a failure or success. Status "0" is failure and Status "1" is success
         */
        void checkCashWithdrawalStatus(String status, String message, CashWithdrawalResponse cashWithdrawalResponse);
        void checkCashWithdrawalAEPS2(String status, String message, AepsResponse cashWithdrawalResponse);

        /**
         *
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
        void performCashWithdrawal(String token, CashWithdrawalRequestModel cashWithdrawalRequestModel);
    }


}

