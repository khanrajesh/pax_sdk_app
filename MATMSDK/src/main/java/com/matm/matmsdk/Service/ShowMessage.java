package com.matm.matmsdk.Service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import isumatm.androidsdk.equitas.R;

public class ShowMessage {

    public static void display(ViewGroup viewGroup, Context context, String Title, String Message){

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(context).inflate(R.layout.custom_dialog,viewGroup,false);

        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        builder.setNeutralButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                dialog.dismiss();
            }
        });

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
