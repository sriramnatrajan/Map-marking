package com.example.taskapp;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

import androidx.appcompat.app.AlertDialog;


/**
 * Created by hlink on 23/2/16.
 */
public class CustomDialogAgree {

    public static AlertDialog.Builder alertDialogBuilder;
    public static AlertDialog alertDialog;
    public static DialogAgree dialogAgree;

    public static void createDialog(Context context, String title, String message, DialogAgree dialogAgree) {

        CustomDialogAgree.dialogAgree = dialogAgree;

        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

                CustomDialogAgree.dialogAgree.onAgree();
            }
        });


        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    public static void onDissmiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public interface DialogAgree {
        public void onAgree();

        public void onCancel();
    }


    public static void createDialogWithOkCancel(Context context, String title, String message, DialogAgree dialogAgree) {

        CustomDialogAgree.dialogAgree = dialogAgree;

        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

                CustomDialogAgree.dialogAgree.onAgree();
            }
        });


        alertDialogBuilder.setNegativeButton(context.getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CustomDialogAgree.dialogAgree.onCancel();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void createDialogWithExitGo(final Context context, String title, String message, DialogAgree dialogAgree) {

        CustomDialogAgree.dialogAgree = dialogAgree;



        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(Html.fromHtml(" <big><font color='#000000'>"+message+"</font> </big>"));
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(context.getString(R.string.btn_go), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();

                CustomDialogAgree.dialogAgree.onAgree();
            }
        });


       /* alertDialogBuilder.setNegativeButton(context.getString(R.string.btn_exit), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                CustomDialogAgree.dialogAgree.onCancel();
            }
        });*/
        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(25);
            }
        });


        alertDialog.show();

    }
}

