/*
 *
 *     FloatSight
 *     Copyright 2018 Thomas Hirsch
 *     https://github.com/84n4n4/FloatSight
 *
 *     This file is part of FloatSight.
 *     FloatSight is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     FloatSight is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with FloatSight.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package org.floatcast.floatsight.fragment;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import org.floatcast.floatsight.BuildConfig;
import org.floatcast.floatsight.R;

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
