package com.watchthybridle.floatsight.fragment;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import com.watchthybridle.floatsight.BuildConfig;
import com.watchthybridle.floatsight.R;

public final class Dialogs {

    private Dialogs() {
        throw new AssertionError("No.");
    }

    public static void showErrorMessage(Context context, @StringRes int stringResId) {
        if (context == null) {
            return;
        }

        new AlertDialog.Builder(context)
                .setMessage(stringResId)
                .setPositiveButton(R.string.ok, null)
                .create()
                .show();
    }

    public static void showAboutDialog(Context context) {
        if (context == null) {
            return;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setView(R.layout.about_dialog)
                .setPositiveButton(R.string.ok, null)
                .create();

        alertDialog.show();

        TextView versionTextView = alertDialog.findViewById(R.id.about_dialog_version);
        versionTextView.setText(context.getString(R.string.about_dialog_version, BuildConfig.VERSION_NAME));
    }
}
