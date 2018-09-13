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

package com.watchthybridle.floatsight;

import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import com.watchthybridle.floatsight.data.FileImportData;
import com.watchthybridle.floatsight.filesystem.FileImporter;
import com.watchthybridle.floatsight.fragment.mainmenu.MainMenuFragment;
import com.watchthybridle.floatsight.permissionactivity.PermissionActivity;
import com.watchthybridle.floatsight.permissionactivity.PermissionStrategy;
import com.watchthybridle.floatsight.viewmodel.FileImportDataViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.TrackPickerActivity.TRACK_PICKER_PATH;

public class MainActivity extends PermissionActivity {

    public static final String TAG_MAIN_MENU_FRAGMENT = "TAG_MAIN_MENU_FRAGMENT";

    public static final int REQUEST_FILE = 666;
    private static final int IMPORT_PERMISSION_REQUEST_CODE = 200;

    private FileImportDataViewModel fileImportDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_with_fragment_container);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            showMainMenuFragment();
        }

        fileImportDataViewModel = ViewModelProviders.of(this).get(FileImportDataViewModel.class);
        fileImportDataViewModel.getLiveData().observe(this, this::actOnDataChanged);
    }

    private void actOnDataChanged(FileImportData fileImportData) {
        findViewById(R.id.toolbar_progress_bar).setVisibility(View.GONE);
        @StringRes int message = R.string.file_import_fail;
        if (fileImportData.getImportingStatus() == FileImportData.IMPORTING_SUCCESS) {
            message = R.string.file_import_success;
        } else if (fileImportData.getImportingStatus() == FileImportData.IMPORTING_ERRORS) {
            message = R.string.file_import_some_errors;
        }

        if (!(fileImportData.getImportingStatus() == FileImportData.IMPORTING_ERRORS)) {
            Intent showTrackIntent = new Intent(this, TrackPickerActivity.class);
            showTrackIntent.putExtra(TRACK_PICKER_PATH, fileImportData.getImportFolder().getAbsolutePath());
            startActivity(showTrackIntent);
        }

        Snackbar.make(findViewById(R.id.fragment_container), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void showMainMenuFragment() {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainMenuFragment, TAG_MAIN_MENU_FRAGMENT)
                .commit();
    }

    public void startImportFile() {
        new PermissionStrategy(IMPORT_PERMISSION_REQUEST_CODE) {
            @Override
            public void task() {
                Intent intent = new Intent()
                        .setType("text/*")
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setAction(Intent.ACTION_OPEN_DOCUMENT)
                        .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE);
            }
        }.execute(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_FILE) {
            if (resultCode == RESULT_OK) {
                importData(data);
            } else {
                findViewById(R.id.toolbar_progress_bar).setVisibility(View.GONE);
            }
        }
    }

    private void importData(Intent data) {
        if (data == null) {
            return;
        }

        List<Uri> uris = new ArrayList<>();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int index = 0; index < clipData.getItemCount(); index++) {
                uris.add(clipData.getItemAt(index).getUri());
            }
        } else {
            uris.add(data.getData());
        }

        FileImporter importer = new FileImporter(getContentResolver());
        importer.importTracks(uris, fileImportDataViewModel);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
