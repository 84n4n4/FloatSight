package com.watchthybridle.floatsight.fragment;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.watchthybridle.floatsight.BuildConfig;
import com.watchthybridle.floatsight.R;

public final class Dialogs {

    private Dialogs() {
        throw new AssertionError("No.");
    }

    public static void showErrorMessage(Context context, @StringRes int stringResId) {
        if(context != null) {
            new AlertDialog.Builder(context)
                    .setMessage(stringResId)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
    }

    public static void showAboutDialog(Context context) {
        if(context == null) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(context);
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.about_dialog, frameView);
        TextView versionTextView = dialoglayout.findViewById(R.id.about_dialog_version);
        versionTextView.setText(context.getString(R.string.about_dialog_version, BuildConfig.VERSION_NAME));
        dialog.show();
    }
}
