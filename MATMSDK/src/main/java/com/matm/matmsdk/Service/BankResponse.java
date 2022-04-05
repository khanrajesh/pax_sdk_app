package com.matm.matmsdk.Service;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.matm.matmsdk.Utils.PAXScreen;
import com.paxsz.easylink.api.EasyLinkSdkManager;

import java.util.HashMap;

public class BankResponse {
    public static String Approved = "00";
    public static String InvalidMerchant = "03";
    public static String PickUp = "04";
    public static String DO_NOT_HONOR = "05";
    public static String Error = "06";
    public static String Invalid_transaction = "12";
    public static String Invalid_amount = "13";
    public static String Invalid_card_number = "14";

    public static String No_such_issuer = "15";
    public static String Customer_cancellation = "17";
    public static String Invalid_response = "20";
    public static String No_action_taken = "21";
    public static String Suspected_malfunction = "22";
    public static String Unable_to_locate_record = "25";
    public static String File_Update_field_edit_error = "27";

    public static String Record_already_exist_in_the_file = "28";

    public static String File_Update_not_successful = "29";

    public static String Format_error = "30";
    public static String Bank_not_supported_by_switch = "31";
    public static String Expired_card_capture = "33";

    public static String Suspected_fraud = "34";
    public static String Restricted_card = "36";
    public static String Allowable_PIN = "38";
    public static String No_credit_account = "39";
    public static String Requested_functio = "40";
    public static String Lost_card = "41";
    public static String No_universal_account = "42";

    public static String Stolen_card = "43";
    public static String Not_sufficient_funds = "51";
    public static String No_checking_account = "52";
    public static String No_savings_account = "53";
    public static String Expired_card_decline = "54";
    public static String Incorrect_personal_identification_number = "55";
    public static String No_card_record = "56";
    public static String Transaction_not_permitted_to_Cardholder = "57";
    public static String Suspected_fraud_decline = "59";
    public static String Card_acceptor_contact_acquirer = "60";
    public static String Exceeds_withdrawal_amount_limit = "61";
    public static String Restricted_Card_decline ="62";
    public static String Security_violation = "63";


    public static String Exceeds_withdrawal_frequency = "65";
    public static String Card_acceptor_calls_acquirer = "66";
    public static String Hard_capture = "67";
    public static String Acquirer_time = "68";
    public static String Mobile_number_record_not_found = "69";
    public static String Deemed_Acceptance = "71";
    public static String Transactions_declined_by_Issuer = "74";
    public static String Allowable_number_of_PIN = "75";

    public static String Cryptographic_Error = "81";
    public static String Cut_off_is_in_process = "90";
    public static String Issuer_or_switch_is_inoperative = "91";
    public static String No_routing_available = "92";

    public static String Transaction_cannot_be_completed = "93";
    public static String Duplicate_transmission = "94";
    public static String Reconcile_error = "95";

    public static String ARQC_validation_failed = "E3";
    public static String TVR_validation_failed = "E4";

    public static String CVR_validation_failed_by_Issuer = "E5";
    public static String No_Aadhar_linked_to_Card = "MU";
    public static String INVALID_BIOMETRIC_DATA = "UG";

    public static String BIOMETRIC_DATA_DID_NOT_MATCH = "U3";
    public static String Technical_Decline_UIDAI = "WZ";


    public static String Compliance_error_code_for_issuer = "C1";
    public static String Compliance_error_code_for_acquirer = "CA";
    public static String Compliance_error_code_for_LMM = "M6";
    public static String E_commerce_decline = "ED";
    public static String Approved_or_Completed_successfully = "00";
    public static String System_malfunction = "96";
    public static String Timeout = "91";
    public static String Acquirer_received_ATM_only = "21";
    public static String Acquirer_received = "22";
    public static String Message_edit_failure_during_response_processing_at_NPCI = "CI";
    public static String Acquirer_time_out = "68";
    public static String Customer_cancellation_for_void = "17";
    public static String AAC_GENERATED = "E1";
    public static String Terminal_does_not_receive_AAC_AND_TC = "E2";
    public static String Partial_Reversal = "32";


    private static HashMap<Integer, String> respcodeArry;

    public BankResponse() {
    }

    public static HashMap<Integer, String> getRespcodeArry() {
        return respcodeArry;
    }

    public static void setRespcodeArry(HashMap<Integer, String> var0) {
        respcodeArry = var0;
    }

    @SuppressLint({"UseSparseArrays"})
    public static void initRespCodeTable() {
        respcodeArry = new HashMap();
        respcodeArry.put(00, "Approved");
        respcodeArry.put(03, "InvalidMerchant");
        respcodeArry.put(04, "PickUp");
        respcodeArry.put(05, "DO NOT HONOR");
        respcodeArry.put(06, "Error");
        respcodeArry.put(12, "Invalid Transaction");
        respcodeArry.put(13, "Invalid Amount");
        respcodeArry.put(14, "Invalid Card Number");
        respcodeArry.put(15, "No Such Issuer");
        respcodeArry.put(17, "Customer Cancellation");
        respcodeArry.put(20, "Invalid Response");
        respcodeArry.put(21, "No Action Taken");
        respcodeArry.put(22, "Suspected Malfunction");
        respcodeArry.put(25, "Unable to locate record");
        respcodeArry.put(27, "File Update field edit error");
        respcodeArry.put(28, "Record Already exist in the file");
        respcodeArry.put(29, "File Update not successful");
        respcodeArry.put(30, "Format error");
        respcodeArry.put(31, "Bank not supported by switch");
        respcodeArry.put(32, "Partial Reversal");
        respcodeArry.put(33, "Expired card capture");
        respcodeArry.put(34, "Suspected fraud");
        respcodeArry.put(36, "Restricted card");
        respcodeArry.put(38, "Allowable PIN");
        respcodeArry.put(39, "No credit account");
        respcodeArry.put(40, "Requested Functio");
        respcodeArry.put(41, "Lost card");
        respcodeArry.put(42, "No universal account");
        respcodeArry.put(43, "Stolen card");
        respcodeArry.put(51, "Not sufficient funds");
        respcodeArry.put(52, "No checking account");
        respcodeArry.put(53, "No savings account");
        respcodeArry.put(54, "Expired card decline");
        respcodeArry.put(55, "Incorrect personal identification number");
        respcodeArry.put(56, "No card record");
        respcodeArry.put(57, "Transaction not permitted to Cardholder");
        respcodeArry.put(59, "Suspected fraud decline");
        respcodeArry.put(60, "Card acceptor contact acquirer");
        respcodeArry.put(61, "Exceeds withdrawal amount limit");
        respcodeArry.put(62, "Restricted Card decline");
        respcodeArry.put(63, "Security violation");
        respcodeArry.put(65, "Exceeds withdrawal frequency");
        respcodeArry.put(66, "Card acceptor calls acquirer");
        respcodeArry.put(67, "Hard capture");
        respcodeArry.put(68, "Acquirer time");
        respcodeArry.put(69, "Mobile number record not found");
        respcodeArry.put(71, "Deemed Acceptance");
        respcodeArry.put(74, "Transactions declined by Issuer");
        respcodeArry.put(75, "Allowable number of PIN");
        respcodeArry.put(81, "Cryptographic Error");
        respcodeArry.put(90, "Cut off is in process");
        respcodeArry.put(91, "Issuer or switch is inoperative");
        respcodeArry.put(92, "No routing available");
        respcodeArry.put(93, "Transaction cannot be completed");
        respcodeArry.put(94, "Duplicate transmission");
        respcodeArry.put(95, "Reconcile error");
        respcodeArry.put(96, "System malfunction");
    }

    public static void showStatusMessage(EasyLinkSdkManager manager, String status_code, TextView tv){
        switch (status_code){

            case "55":
                PAXScreen.showBankResponseOnPax(manager,55);
                tv.setText("Incorrect Pin");
                break;
            case "51":
                tv.setText("You don't have sufficient fund in your account to make this transaction");
                PAXScreen.showFailure(manager);
                break;
            case "41":
                tv.setText("Card is not active or expired");
                PAXScreen.showFailure(manager);
                break;
            case "05":
                tv.setText("DO NOT HONOR");
                PAXScreen.showFailure(manager);
                break;

            case "06":
                tv.setText("ERROR");
                PAXScreen.showFailure(manager);
                break;

            case "12":
                tv.setText("Invalid transaction or if member is not able to find any appropriate response code");
                PAXScreen.showFailure(manager);
                break;

            case "03":
                tv.setText("Invalid merchant.");
                PAXScreen.showFailure(manager);
                break;
            case "04":
                tv.setText("Pick-up.");
                PAXScreen.showFailure(manager);
                break;

            case "13":
                tv.setText("Invalid amount.");
                PAXScreen.showFailure(manager);
                break;
            case "14":
                tv.setText("Invalid card number (no such Number).");
                PAXScreen.showFailure(manager);
                break;
            case "15":
                tv.setText("No such issuer.");
                PAXScreen.showFailure(manager);
                break;
            case "17":
                tv.setText("Customer cancellation.");
                PAXScreen.showFailure(manager);
                break;
            case "20":
                tv.setText("Invalid response.");
                PAXScreen.showFailure(manager);
                break;
            case "21":
                tv.setText("No action taken.");
                PAXScreen.showFailure(manager);
                break;
            case "22":
                tv.setText("Suspected malfunction.");
                PAXScreen.showFailure(manager);
                break;
            case "25":
                tv.setText("Unable to locate record");
                PAXScreen.showFailure(manager);
                break;
            case "27":
                tv.setText("File Update field edit error");
                PAXScreen.showFailure(manager);
                break;
            case "28":
                tv.setText("Record already exist in the file");
                PAXScreen.showFailure(manager);
                break;
            case "29":
                tv.setText("File Update not successfu");
                PAXScreen.showFailure(manager);
                break;
            case "30":
                tv.setText("Format error");
                PAXScreen.showFailure(manager);
                break;
            case "31":
                tv.setText("Bank not supported by Switch");
                PAXScreen.showFailure(manager);
                break;
            case "33":
                tv.setText("Expired card, capture");
                PAXScreen.showFailure(manager);
                break;
            case "34":
                tv.setText("Suspected fraud, capture.");
                PAXScreen.showFailure(manager);
                break;
            case "36":
                tv.setText("Restricted card, capture");
                PAXScreen.showFailure(manager);
                break;
            case "38":
                tv.setText("Allowable PIN tries exceeded, capture.");
                PAXScreen.showFailure(manager);
                break;
            case "39":
                tv.setText("No credit account.");
                PAXScreen.showFailure(manager);
                break;
            case "40":
                tv.setText("Requested function not supported.");
                PAXScreen.showFailure(manager);
                break;

            case "42":
                tv.setText("No universal account.");
                PAXScreen.showFailure(manager);
                break;
            case "43":
                tv.setText("Stolen card, capture.");
                PAXScreen.showFailure(manager);
                break;
            case "52":
                tv.setText("No checking account");
                PAXScreen.showFailure(manager);
                break;
            case "53":
                tv.setText("No savings account.");
                PAXScreen.showFailure(manager);
                break;
            case "54":
                tv.setText("Expired card, decline");
                PAXScreen.showFailure(manager);
                break;
            case "56":
                tv.setText("No card record.");
                PAXScreen.showFailure(manager);
                break;
            case "57":
                tv.setText("Transaction not permitted to Cardholder");
                PAXScreen.showFailure(manager);
                break;
            case "58":
                tv.setText("Transaction not permitted to terminal.");
                PAXScreen.showFailure(manager);
                break;
            case "59":
                tv.setText("Suspected fraud, decline / Transactions declined based on Risk Score");
                PAXScreen.showFailure(manager);
                break;
            case "60":
                tv.setText("Card acceptor contact acquirer, decline.");
                PAXScreen.showFailure(manager);
                break;
            case "61":
                tv.setText("Exceeds withdrawal amount limit.");
                PAXScreen.showFailure(manager);
                break;
            case "62":
                tv.setText("Restricted card, decline.");
                PAXScreen.showFailure(manager);
                break;
            case "63":
                tv.setText("Security violation.");
                PAXScreen.showFailure(manager);
                break;
            case "65":
                tv.setText("Exceeds withdrawal frequency limit.");
                PAXScreen.showFailure(manager);
                break;
            case "66":
                tv.setText("Card acceptor calls acquirer’s.");
                PAXScreen.showFailure(manager);
                break;
            case "67":
                tv.setText("Hard capture (requires that card be picked up at ATM)");
                PAXScreen.showFailure(manager);
                break;
            case "68":
                tv.setText("Acquirer time-out");
                PAXScreen.showFailure(manager);
                break;
            case "69":
                tv.setText("Mobile number record not found/ mis-match");
                PAXScreen.showFailure(manager);
                break;
            case "71":
                tv.setText("Deemed Acceptance");
                PAXScreen.showFailure(manager);
                break;
            case "74":
                tv.setText("Transactions declined by Issuer based on Risk Score");
                PAXScreen.showFailure(manager);
                break;
            case "75":
                tv.setText("Allowable number of PIN tries exceeded, decline");
                PAXScreen.showFailure(manager);
                break;
            case "81":
                tv.setText("Cryptographic Error");
                PAXScreen.showFailure(manager);
                break;
            case "90":
                tv.setText("Cut-off is in process.");
                PAXScreen.showFailure(manager);
                break;
            case "91":
                tv.setText("Issuer or switch is inoperative");
                PAXScreen.showFailure(manager);
                break;
            case "92":
                tv.setText("No routing available");
                PAXScreen.showFailure(manager);
                break;
            case "93":
                tv.setText("Transaction cannot be completed. Compliance violation.");
                PAXScreen.showFailure(manager);
                break;
            case "94":
                tv.setText("Duplicate transmission.");
                PAXScreen.showFailure(manager);
                break;
            case "95":
                tv.setText("Reconcile error");
                PAXScreen.showFailure(manager);
                break;
            case "96":
                tv.setText("System malfunction");
                PAXScreen.showFailure(manager);
                break;
            case "E3":
                tv.setText("ARQC validation failed by Issuer");
                PAXScreen.showFailure(manager);
                break;
            case "E4":
                tv.setText("TVR validation failed by Issuer");
                PAXScreen.showFailure(manager);
                break;
            case "E5":
                tv.setText("CVR validation failed by Issuer");
                PAXScreen.showFailure(manager);
                break;
            case "MU":
                tv.setText("No Aadhar linked to Card");
                PAXScreen.showFailure(manager);
                break;
            case "UG":
                tv.setText("INVALID BIOMETRIC DATA");
                PAXScreen.showFailure(manager);
                break;
            case "U3":
                tv.setText("BIOMETRIC DATA DID NOT MATCH");
                PAXScreen.showFailure(manager);
                break;
            case "WZ":
                tv.setText("Technical Decline UIDAI");
                PAXScreen.showFailure(manager);
                break;
            case "CA":
                tv.setText("Compliance error code for acquirer");
                PAXScreen.showFailure(manager);
                break;
            case "CI":
                tv.setText("Compliance error code for issuer");
                PAXScreen.showFailure(manager);
                break;







            default:
                PAXScreen.showFailure(manager);
                tv.setText("We are unable to process your transaction, Please try after sometime");
                break;

        }

    }




}
