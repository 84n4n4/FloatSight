package com.watchthybridle.floatsight.fragment.trackmenu;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import com.watchthybridle.floatsight.R;

import java.util.List;

public class SaveFileDialogTextWatcher implements TextWatcher {

    private List<String> filesInFolder;
    private TextInputLayout textInputLayout;
    private AlertDialog alertDialog;
    private String oldFileName;

    public SaveFileDialogTextWatcher(String oldFileName, List<String> filesInFolder, TextInputLayout textInputLayout, AlertDialog alertDialog) {
        this.oldFileName = oldFileName;
        this.filesInFolder = filesInFolder;
        this.textInputLayout = textInputLayout;
        this.alertDialog = alertDialog;
        filesInFolder.remove(oldFileName);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        String input = s.toString().trim();
        String error = null;

        if (!input.endsWith(".csv") && !input.endsWith(".CSV")) {
            error = alertDialog.getContext().getString(R.string.file_has_to_be_csv);
        } else if (input.startsWith(".")) {
            error = alertDialog.getContext().getString(R.string.file_name_empty);
        } else if (filesInFolder.contains(input)) {
            error = alertDialog.getContext().getString(R.string.file_already_exists);
        }

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);

        if (input.equals(oldFileName)) {
            error = alertDialog.getContext().getString(R.string.file_overwrite_warning);
        }
        textInputLayout.setError(error);
    }
}