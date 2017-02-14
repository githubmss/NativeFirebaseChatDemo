package com.mss.myfirebasedemo.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidadvance.topsnackbar.TSnackbar;

public class AppUtils {

    public static void showErrorOnTop(ViewGroup viewGroup, Activity activity, String error) {
        TSnackbar snackbar = TSnackbar.make(viewGroup, error, TSnackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundColor(Color.BLACK);
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

}









