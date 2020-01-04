package com.example.taskapp.map_server;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.example.taskapp.R;


/**
 * Created by hlink on 23/2/16.
 */
public class AlertDialogCallBack {

    public static AlertDialog.Builder alertDialogBuilder;
    public static AlertDialog alertDialog;
    public static CallBack callBack;

    public static  void createDialog(Context context, String title, String message, final CallBack dialogDismiss) {
        AlertDialogCallBack.callBack = dialogDismiss;
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title).setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialogCallBack.callBack.onAgree();
            }
        });
        alertDialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialogCallBack.callBack.onDismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void onDismiss(){
        if(alertDialog!=null && alertDialog.isShowing()){
            alertDialog.dismiss();
        }
    }

    public interface CallBack {
        void onAgree();

        public void onDismiss();
    }

    public static  void createDialogForFare(Context context, String title, String message, final CallBack dialogDismiss) {
        AlertDialogCallBack.callBack = dialogDismiss;
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title).setMessage(message);
        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialogCallBack.callBack.onAgree();
            }
        });
        alertDialogBuilder.setNegativeButton("skip", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialogCallBack.callBack.onDismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static  void createDialogForFareRideLater(Context context, String title, String message, final CallBack dialogDismiss) {
        AlertDialogCallBack.callBack = dialogDismiss;
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(title).setMessage(message);
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                AlertDialogCallBack.callBack.onAgree();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}

