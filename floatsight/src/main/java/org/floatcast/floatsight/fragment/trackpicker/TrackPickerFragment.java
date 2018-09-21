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

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.floatcast.floatsight.R;
import org.floatcast.floatsight.TrackActivity;
import org.floatcast.floatsight.TrackPickerActivity;
import org.floatcast.floatsight.filesystem.PathBuilder;
import org.floatcast.floatsight.permissionactivity.PermissionStrategy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.floatcast.floatsight.TrackPickerActivity.TAG_FILE_PICKER_FRAGMENT;

public class TrackPickerFragment extends Fragment implements FileAdapter.FileAdapterItemClickListener {

    public static final String PATH_BUNDLE_TAG = "PATH_BUNDLE_TAG";
    public static final int TRACK_PICKER_PERMISSION_REQUEST_CODE = 400;

    static final int OPEN = 0;
    static final int RENAME = 1;
    static final int DELETE = 2;

    FileAdapter fileAdapter;

    public TrackPickerFragment() {
        // intentional empty constructor for fragments
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_file_picker, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        fileAdapter.update(getFiles());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.file_list_view);
        recyclerView.setHasFixedSize(true);
        fileAdapter = new FileAdapter();
        fileAdapter.setItemClickListener(this);
        recyclerView.setAdapter(fileAdapter);
        try {
            String fullPath = getCurrentFolder().getPath();
            String currentFolder = fullPath.substring(fullPath.indexOf(PathBuilder.TRACKS_FOLDER_NAME) + PathBuilder.TRACKS_FOLDER_NAME.length()) + "/";
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(currentFolder);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(FileAdapterItem fileAdapterItem) {
        if (fileAdapterItem.isEnabled) {
            if (fileAdapterItem.file.isDirectory()) {
                Bundle pathBundle = new Bundle();
                pathBundle.putString(PATH_BUNDLE_TAG, fileAdapterItem.file.getAbsolutePath());
                TrackPickerFragment fileTrackPickerFragment = new TrackPickerFragment();
                fileTrackPickerFragment.setArguments(pathBundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fileTrackPickerFragment, TAG_FILE_PICKER_FRAGMENT)
                        .addToBackStack(TAG_FILE_PICKER_FRAGMENT)
                        .commit();
            } else {
                Intent showTrackIntent = new Intent(getActivity(), TrackActivity.class);
                showTrackIntent.setDataAndType(Uri.fromFile(fileAdapterItem.file), "text/csv");
                startActivity(showTrackIntent);
            }
        }
    }

    @Override
    public void onItemLongClick(FileAdapterItem fileAdapterItem) {
        new AlertDialog.Builder(getContext())
                .setTitle(fileAdapterItem.fileName)
                .setItems(R.array.context_menu_file, new OnDialogItemClickListener(fileAdapterItem))
                .show();
    }

    File getCurrentFolder() throws FileNotFoundException {
        File folder;
        if (getArguments() == null || getArguments().getString(PATH_BUNDLE_TAG) == null) {
            folder = PathBuilder.getTracksFolder();
        } else {
            String pathString = getArguments().getString(PATH_BUNDLE_TAG);
            folder = new File(pathString);
        }
        return folder;
    }

    private List<FileAdapterItem> getFiles() {
        List<FileAdapterItem> files = new ArrayList<>();
        TrackPickerActivity activity = (TrackPickerActivity) getActivity();

        if (activity == null) {
            return files;
        }

        new PermissionStrategy(TRACK_PICKER_PERMISSION_REQUEST_CODE) {
            public void task() {
                try {
                    File folder = getCurrentFolder();
                    for (File file : folder.listFiles()) {
                        files.add(new FileAdapterItem(file));
                    }
                } catch (FileNotFoundException e) {
                    showErrorMessage(R.string.error_opening_local_storage);
                }
            }
        }.execute(activity);

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
                new RenameFileAdapterItemDialogTextWatcher(fileAdapter, item.fileName, inputLayout, alertDialog, item.file.isDirectory());
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
                default:
                     break;
            }
        }
    }
}

