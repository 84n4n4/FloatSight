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

package org.floatcast.floatsight;


import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.fragment.trackpicker.TrackPickerFragment;
import org.floatcast.floatsight.permissionactivity.PermissionActivity;
import org.floatcast.floatsight.permissionactivity.PermissionStrategy;
import org.floatcast.floatsight.fragment.trackpicker.TrackPickerFragment;
import org.floatcast.floatsight.permissionactivity.PermissionActivity;
import org.floatcast.floatsight.permissionactivity.PermissionStrategy;

import static org.floatcast.floatsight.fragment.trackpicker.TrackPickerFragment.PATH_BUNDLE_TAG;
import static org.floatcast.floatsight.fragment.trackpicker.TrackPickerFragment.TRACK_PICKER_PERMISSION_REQUEST_CODE;

public class TrackPickerActivity extends PermissionActivity {

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void showTrackPickerFragment() {
        new PermissionStrategy(TrackPickerFragment.TRACK_PICKER_PERMISSION_REQUEST_CODE) {
            public void task() {
                TrackPickerFragment fileTrackPickerFragment = new TrackPickerFragment();
                Bundle pathBundle = new Bundle();

                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    String trackPath = extras.getString(TRACK_PICKER_PATH);
                    if (trackPath != null) {
                        pathBundle.putString(TrackPickerFragment.PATH_BUNDLE_TAG, trackPath);
                    }
                }
                fileTrackPickerFragment.setArguments(pathBundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fileTrackPickerFragment, TAG_FILE_PICKER_FRAGMENT)
                        .commit();
            }
        }.execute(this);
    }
}
