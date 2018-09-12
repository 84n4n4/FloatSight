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


import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import com.watchthybridle.floatsight.fragment.trackpicker.TrackPickerFragment;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.watchthybridle.floatsight.fragment.trackpicker.TrackPickerFragment.PATH_BUNDLE_TAG;
import static com.watchthybridle.floatsight.fragment.trackpicker.TrackPickerFragment.TRACK_PICKER_PERMISSION_REQUEST_CODE;

public class TrackPickerActivity extends AppCompatActivity {

    public static final String TAG_FILE_PICKER_FRAGMENT = "TAG_FILE_PICKER_FRAGMENT";
    public static final String TRACK_PICKER_PATH = "TRACK_PICKER_PATH";

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
            showTrackPickerFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_track_picker, menu);
        return true;
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        boolean readAccepted = false;
        boolean writeAccepted = false;
        if (grantResults.length > 0) {
            readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }

        switch (requestCode) {
            case TRACK_PICKER_PERMISSION_REQUEST_CODE:
                if (readAccepted && writeAccepted) {
                    TrackPickerFragment trackPickerFragment = (TrackPickerFragment) getSupportFragmentManager()
                            .findFragmentByTag(TAG_FILE_PICKER_FRAGMENT);
                    if (trackPickerFragment != null && trackPickerFragment.isVisible()) {
                        getSupportFragmentManager().popBackStackImmediate(TAG_FILE_PICKER_FRAGMENT, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    }
                    showTrackPickerFragment();
                } else {
                    showPermissionRationale(TRACK_PICKER_PERMISSION_REQUEST_CODE);
                }
                break;
            default:
                break;
        }
    }

    private void showPermissionRationale(int permissionRequestId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                showAlertOKCancel(getResources().getString(R.string.permissions_rationale), (dialog, which) ->
                        requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, permissionRequestId)
                );
            }
        }
    }

    private void showAlertOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(TrackPickerActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showTrackPickerFragment() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    TRACK_PICKER_PERMISSION_REQUEST_CODE);

        } else {
            TrackPickerFragment fileTrackPickerFragment = new TrackPickerFragment();
            Bundle pathBundle = new Bundle();

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String trackPath = extras.getString(TRACK_PICKER_PATH);
                if (trackPath != null) {
                    pathBundle.putString(PATH_BUNDLE_TAG, trackPath);
                }
            }
            fileTrackPickerFragment.setArguments(pathBundle);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fileTrackPickerFragment, TAG_FILE_PICKER_FRAGMENT)
                    .commit();
        }
    }
}
