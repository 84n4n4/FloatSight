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

package org.floatcast.floatsight.fragment.trackpicker;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import org.floatcast.floatsight.R;

public class RenameFileAdapterItemDialogTextWatcher implements TextWatcher {

    private final String fileName;
    private final TextInputLayout textInputLayout;
    private final AlertDialog alertDialog;
    private final FileAdapter fileAdapter;

    public RenameFileAdapterItemDialogTextWatcher(FileAdapter fileAdapter, String fileName, TextInputLayout textInputLayout, AlertDialog alertDialog) {
        this.fileName = fileName;
        this.textInputLayout = textInputLayout;
        this.alertDialog = alertDialog;
        this.fileAdapter = fileAdapter;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    @SuppressWarnings("PMD.ConfusingTernary")
    public void afterTextChanged(Editable s) {

        String input = s.toString().trim();
        String error = null;

        if (!input.endsWith(".csv") && !input.endsWith(".CSV")) {
            error = alertDialog.getContext().getString(R.string.file_has_to_be_csv);
        } else if (input.startsWith(".")) {
            error = alertDialog.getContext().getString(R.string.file_name_empty);
        } else if (!input.equals(fileName) && fileAdapter.contains(input)) {
            error = alertDialog.getContext().getString(R.string.file_already_exists);
        }

        textInputLayout.setError(error);
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
    }
}
