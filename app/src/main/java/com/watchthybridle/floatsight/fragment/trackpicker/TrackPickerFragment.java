package com.watchthybridle.floatsight.fragment.trackpicker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;
import com.watchthybridle.floatsight.recyclerview.ItemClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TrackPickerFragment extends Fragment implements ItemClickListener<FileAdapterItem> {

    private static final int OPEN = 0;
    private static final int RENAME = 1;
    private static final int DELETE = 2;

    FileAdapter fileAdapter;

    public TrackPickerFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.file_list_view);
        recyclerView.setHasFixedSize(true);

        fileAdapter = new FileAdapter(getFiles());
        fileAdapter.setItemClickListener(this);
        recyclerView.setAdapter(fileAdapter);
        recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
    }

    @Override
    public void onItemClick(FileAdapterItem fileAdapterItem) {
        if (fileAdapterItem.isEnabled) {
            Uri uri = Uri.fromFile(fileAdapterItem.file);
            ((MainActivity) getActivity()).loadFlySightTrackData(uri);
            getFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    public void onItemLongClick(FileAdapterItem fileAdapterItem) {
        new AlertDialog.Builder(getContext())
                .setTitle(fileAdapterItem.fileName)
                .setItems(R.array.context_menu_file, new OnDialogItemClickListener(fileAdapterItem))
                .show();
    }

    private List<FileAdapterItem> getFiles() {
        List<FileAdapterItem> files = new ArrayList<>();
        try {
            File tracksFolder = ((MainActivity) getActivity()).getTracksFolder();
            for (File file : tracksFolder.listFiles()) {
                files.add(new FileAdapterItem(file));
            }
        } catch (FileNotFoundException e) {
            showErrorMessage(R.string.error_opening_local_storage);
        }
        return files;
    }

    public void deleteItem(FileAdapterItem item) {
        fileAdapter.remove(item);
    }

    public void renameItem(FileAdapterItem item) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(R.layout.text_input_layout)
                .setPositiveButton(getString(R.string.rename), (dialog, which) -> {
                    TextInputLayout textInputLayout = ((Dialog) dialog).findViewById(R.id.text_input_layout);
                    String newFileName = textInputLayout.getEditText().getText().toString().trim();
                    fileAdapter.rename(item, newFileName);
                })
                .create();

        alertDialog.show();

        TextInputLayout inputLayout = alertDialog.findViewById(R.id.text_input_layout);
        inputLayout.setHint(getString(R.string.file_name_hint));
        inputLayout.getEditText().setText(item.fileName);
        inputLayout.getEditText().setSelection(0,
                item.fileName.contains(".") ? item.fileName.lastIndexOf(".") : item.fileName.length());

        RenameFileAdapterItemDialogTextWatcher textWatcher =
                new RenameFileAdapterItemDialogTextWatcher(item, inputLayout, alertDialog);
        inputLayout.getEditText().addTextChangedListener(textWatcher);
    }

    protected void showErrorMessage(@StringRes int stringResId) {
        Context context = getContext();
        if (context != null) {
            new AlertDialog.Builder(context)
                    .setMessage(stringResId)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
    }

    private class OnDialogItemClickListener implements DialogInterface.OnClickListener {

        FileAdapterItem fileAdapterItem;

        OnDialogItemClickListener(FileAdapterItem fileAdapterItem) {
            this.fileAdapterItem = fileAdapterItem;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case OPEN:
                    onItemClick(fileAdapterItem);
                    break;
                case RENAME:
                    renameItem(fileAdapterItem);
                    break;
                case DELETE:
                    deleteItem(fileAdapterItem);
                    break;
            }
        }
    }

    private class RenameFileAdapterItemDialogTextWatcher implements TextWatcher {

        private FileAdapterItem item;
        private TextInputLayout textInputLayout;
        private AlertDialog alertDialog;

        RenameFileAdapterItemDialogTextWatcher(FileAdapterItem item, TextInputLayout textInputLayout, AlertDialog alertDialog) {
            this.item = item;
            this.textInputLayout = textInputLayout;
            this.alertDialog = alertDialog;
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
                error = getString(R.string.file_has_to_be_csv);
            } else if (input.startsWith(".")) {
                error = getString(R.string.file_name_empty);
            } else if (!input.equals(item.fileName) && fileAdapter.contains(input)) {
                error = getString(R.string.file_already_exists);
            }

            textInputLayout.setError(error);
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);
        }
    }
}

