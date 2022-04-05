package com.matm.matmsdk.aepsmodule.bankspinner;

import android.content.Context;

import java.util.ArrayList;

public class BankNameContract {
    /**
     * View interface sends report list to ReportActivity
     */
    public interface View {

        /**
         * showReports() showReports on ReportActivity
         */
        void bankNameListReady(ArrayList<BankNameModel> bankNameModelArrayList);
        void showBankNames();
        void showLoader();
        void hideLoader();
        void emptyBanks();


    }

    /**
     * UserActionsListener interface checks the load of Reports
     */
    interface UserActionsListener {
        void loadBankNamesList(Context context);
    }

}
