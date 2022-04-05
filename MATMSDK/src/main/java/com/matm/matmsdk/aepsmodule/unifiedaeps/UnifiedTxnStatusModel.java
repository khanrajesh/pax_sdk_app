package com.matm.matmsdk.aepsmodule.unifiedaeps;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import java.io.Serializable;
import java.util.ArrayList;

public class UnifiedTxnStatusModel implements Parcelable {


    public UnifiedTxnStatusModel() {

    }

    protected UnifiedTxnStatusModel(Parcel in) {
        origin_identifier = in.readString();
        status = in.readString();
        bankName = in.readString();
        apiTid = in.readString();
        balance = in.readString();
        createdDate = in.readString();
        transactionMode = in.readString();
        apiComment = in.readString();
        gateway = in.readString();
        txId = in.readString();
        byte tmpIsRetriable = in.readByte();
        isRetriable = tmpIsRetriable == 0 ? null : tmpIsRetriable == 1;
        ministatement = in.createTypedArrayList(MiniStatement.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(origin_identifier);
        dest.writeString(status);
        dest.writeString(bankName);
        dest.writeString(apiTid);
        dest.writeString(balance);
        dest.writeString(createdDate);
        dest.writeString(transactionMode);
        dest.writeString(apiComment);
        dest.writeString(gateway);
        dest.writeString(txId);
        dest.writeByte((byte) (isRetriable == null ? 0 : isRetriable ? 1 : 2));
        dest.writeTypedList(ministatement);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UnifiedTxnStatusModel> CREATOR = new Creator<UnifiedTxnStatusModel>() {
        @Override
        public UnifiedTxnStatusModel createFromParcel(Parcel in) {
            return new UnifiedTxnStatusModel(in);
        }

        @Override
        public UnifiedTxnStatusModel[] newArray(int size) {
            return new UnifiedTxnStatusModel[size];
        }
    };

    public String getAadharCard() {
        return origin_identifier;
    }

    public String getStatus() {
        return status;
    }

    public String getBankName() {
        return bankName;
    }

    public String getReferenceNo() {
        return apiTid;
    }

    public String getBalanceAmount() {
        return balance;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getTransactionType() {
        return transactionMode;
    }

    public String getApiComment() {
        return apiComment;
    }

    public String getStatusDesc() {
        return gateway;
    }

    public String getTxnID() {
        return txId;
    }

    private String origin_identifier;
    private String status;
    private String bankName;
    private String apiTid;
    private String balance;
    private String createdDate;
    private String transactionMode;
    private String apiComment;
    private String gateway;
    private String txId;
    private Boolean isRetriable;

    public ArrayList<MiniStatement> getMinistatement() {
        return ministatement;
    }

    private ArrayList<MiniStatement> ministatement;


    public Boolean getIsRetriable() {
        return isRetriable;
    }


    /**
     *Inner arraylist for the Ministatement data
     */
    public static class MiniStatement implements Parcelable{
        String Date;
        String Type;
        double Amount;
        String DebitCredit;

        public String getDate() {
            return Date;
        }

        public String getType() {
            return Type;
        }

        public double getAmount() {
            return Amount;
        }

        public String getDebitCredit() {
            return DebitCredit;
        }

        public MiniStatement(String Date, String Type, String DebitCredit, double Amount) {
            this.Date = Date;
            this.Type = Type;
            this.DebitCredit = DebitCredit;
            this.Amount = Amount;
        }

        protected MiniStatement(Parcel in) {
            Date = in.readString();
            Type = in.readString();
            Amount = in.readDouble();
            DebitCredit = in.readString();
        }

        public static final Creator<MiniStatement> CREATOR = new Creator<MiniStatement>() {
            @Override
            public MiniStatement createFromParcel(Parcel in) {
                return new MiniStatement(in);
            }

            @Override
            public MiniStatement[] newArray(int size) {
                return new MiniStatement[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(Date);
            dest.writeString(Type);
            dest.writeDouble(Amount);
            dest.writeString(DebitCredit);
        }
    }
}
