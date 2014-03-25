package com.example.imagesearch;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null
				&& activeNetworkInfo.isConnectedOrConnecting();
	}
	
	public static void showAlertDialog(Context context, String title, Boolean status) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
        .setCancelable(false)
        .setIcon((status) ? R.drawable.ac_action_success : R.drawable.ic_action_error)
        .setNegativeButton(context.getString(R.string.btnClose), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
       
}
