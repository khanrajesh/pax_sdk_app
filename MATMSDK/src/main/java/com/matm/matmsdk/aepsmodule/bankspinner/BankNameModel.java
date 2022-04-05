package com.matm.matmsdk.aepsmodule.bankspinner;

import java.io.Serializable;

/**
 * Created by USER on 6/27/2018.
 */

public class BankNameModel  implements Serializable {

    private String bankName;
    private String iin;

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }




    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}
