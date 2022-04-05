package com.matm.matmsdk.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import isumatm.androidsdk.equitas.R;

/**
 * Created by lixc on 2017/2/15.
 */

public class Dialog {

    private Dialog() {

    }

    public static void showPromptDialog(Context context, String title) {
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setCancelable(false);
        alert.setTitle(R.string.prompt_title);
        alert.setMessage(title);
        alert.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.dialog_sure),
                (dialog, which) -> alert.dismiss());
        alert.show();
    }

    public static void showExitDialog(Context context) {
        final AlertDialog alert = new AlertDialog.Builder(context).create();
        alert.setCancelable(false);
        alert.setTitle(R.string.prompt_exit_title);
        alert.setMessage(context.getString(R.string.prompt_exit));
        alert.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel),
                (dialog, which) -> alert.dismiss());
        alert.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.dialog_sure),
                (dialog, which) -> {
                    alert.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                });
        alert.show();
    }

    //带ProgressBar的对话框
    public static void showProgressDialog(final Context context, final AlertDialog alertDialog, String title, String message, boolean hasCancel) {

        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        if (message != null) {
            final TextView messageTv = (TextView) view.findViewById(R.id.progressbar_msg);
            messageTv.setText(message);
        }

        if (alertDialog == null) {
            return;
        }

        alertDialog.setTitle(title);
        if (view != null) {
            alertDialog.setView(view);
        }
        alertDialog.setCancelable(false);
        if (hasCancel) {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.dialog_cancel),
                    (dialog, which) -> alertDialog.cancel());
        }
        alertDialog.show();
    }
}
