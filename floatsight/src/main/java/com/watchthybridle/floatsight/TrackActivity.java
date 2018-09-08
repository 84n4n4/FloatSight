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
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.datarepository.DataRepository;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.fragment.trackmenu.TrackMenuFragment;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TrackActivity extends AppCompatActivity {

    public static final String TAG_PLOT_FRAGMENT = "TAG_PLOT_FRAGMENT";
    public static final String TAG_STATS_FRAGMENT = "TAG_STATS_FRAGMENT";
    public static final String TAG_TRACK_MENU_FRAGMENT = "TAG_TRACK_MENU_FRAGMENT";
    public static final String TRACK_FILE_URI = "TRACK_FILE_URI";

    private static final int TRACK_PERMISSION_REQUEST_CODE = 300;

    private FlySightTrackDataViewModel flySightTrackDataViewModel;

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
            showTrackMenuFragment();
		}

        flySightTrackDataViewModel = ViewModelProviders.of(this).get(FlySightTrackDataViewModel.class);
        flySightTrackDataViewModel.getLiveData().observe(this, this::actOnDataChanged);

        if (flySightTrackDataViewModel.getLiveData().getValue() == null) {
            loadFlySightTrackData();
        }
    }

    private void actOnDataChanged(FlySightTrackData flySightTrackData) {
        findViewById(R.id.toolbar_progress_bar).setVisibility(View.GONE);
    }

    private void showTrackMenuFragment() {
        TrackMenuFragment trackMenuFragment = new TrackMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, trackMenuFragment, TAG_TRACK_MENU_FRAGMENT)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void loadFlySightTrackData() {
        if (!checkPermission()) {
            requestPermission();
        } else {
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                String trackPath = extras.getString(TRACK_FILE_URI);
                if (trackPath != null) {
                    Uri trackFileUri = Uri.parse(extras.getString(TRACK_FILE_URI));

                    findViewById(R.id.toolbar_progress_bar).setVisibility(View.VISIBLE);
                    DataRepository<FlySightTrackData> repository =
                            new DataRepository<>(FlySightTrackData.class, getContentResolver(), new FlySightCsvParser());
                    repository.load(trackFileUri, flySightTrackDataViewModel);
                }
            }
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, TRACK_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case TRACK_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted && writeAccepted) {
                        loadFlySightTrackData();
                    }
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showAlertOKCancel(getResources().getString(R.string.permissions_rationale),
                                        (dialog, which) -> {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                        TRACK_PERMISSION_REQUEST_CODE);
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }
                break;
            }
        }

    private void showAlertOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(TrackActivity.this)
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

    public void onYAxisDialogCheckboxClicked(View view) {
        PlotFragment plotFragment = (PlotFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_PLOT_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onYAxisDialogCheckboxClicked(view);
        }
    }

    public void onXAxisDialogCheckboxClicked(View view) {
        PlotFragment plotFragment = (PlotFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_PLOT_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onXAxisDialogCheckboxClicked(view);
        }
    }

    public void onUnitsDialogCheckboxClicked(View view) {
        PlotFragment plotFragment = (PlotFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_PLOT_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onUnitsDialogCheckboxClicked(view);
        }
    }
}
