package com.watchthybridle.floatsight.fragment.mainmenu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.watchthybridle.floatsight.BuildConfig;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.data.FlySightTrackData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MainMenuDialogs {

    private MainMenuDialogs() {
        throw new AssertionError("No.");
    }

    static void showSaveDialog(@NonNull MainMenuFragment mainMenuFragment, FlySightTrackData flySightTrackData) {
        MainActivity mainActivity = ((MainActivity) mainMenuFragment.getActivity());
        if(mainActivity == null) {
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(mainActivity)
                .setView(R.layout.save_dialog)
                .setTitle(R.string.save_dialog_title)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        EditText filenameEditText = ((Dialog) dialogInterface).findViewById(R.id.save_dialog_edit_text);
                        String fileName = filenameEditText.getText().toString();
                        try {
                            File tracksFolder = ((MainActivity) mainActivity).getTracksFolder();
                            File outFile = new File(tracksFolder, fileName);
                            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                            FlySightCsvParser parser = new FlySightCsvParser();
                            parser.write(fileOutputStream, flySightTrackData);
                            Snackbar mySnackbar = Snackbar.make(mainMenuFragment.getView(), R.string.file_save_success, Snackbar.LENGTH_SHORT);
                            mySnackbar.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showErrorMessage(mainActivity, R.string.save_error);
                        }
                    }
                } )
                .setNegativeButton(R.string.cancel, null)
                .create();
        dialog.show();

        TextInputLayout fileNameInputLayout = dialog.findViewById(R.id.save_dialog_input_layout);
        TextInputEditText fileNameEditText = dialog.findViewById(R.id.save_dialog_edit_text);
        fileNameEditText.setText(flySightTrackData.getSourceFileName());

        saveDialogValidateFileName(mainActivity, dialog, flySightTrackData.getSourceFileName(), fileNameInputLayout);
        fileNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                saveDialogValidateFileName(mainActivity, dialog, s.toString(), fileNameInputLayout);
            }
        });
        fileNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if (hasFocus) {
                    EditText editText = (EditText) v;
                    editText.setSelection(0, editText.getText().toString().indexOf("."));
                }
            }
        });
    }

    private static void saveDialogValidateFileName(MainActivity activity, AlertDialog dialog, String fileName, TextInputLayout textInputLayout) {
        List<String> existingFiles = new ArrayList<>();
        try {
            File tracksFolder = activity.getTracksFolder();
            for (File file : Arrays.asList(tracksFolder.listFiles())) {
                existingFiles.add(file.getName());
            }
        } catch (IOException e) {

        }

        if(existingFiles.contains(fileName)) {
            textInputLayout.setError(activity.getString(R.string.file_already_exists));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else if (!fileName.toUpperCase().endsWith(".CSV")) {
            textInputLayout.setError(activity.getString(R.string.file_has_to_be_csv));
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
        } else {
            textInputLayout.setError("");
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
        }
    }

    static void showErrorMessage(Context context, @StringRes int stringResId) {
        if(context != null) {
            new AlertDialog.Builder(context)
                    .setMessage(stringResId)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
    }

    static void showAboutDialog(Context context) {
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
