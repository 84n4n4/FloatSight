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

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.csvparser.FlySightCsvParser;
import org.floatcast.floatsight.data.FlySightTrackData;
import org.floatcast.floatsight.datarepository.DataRepository;
import org.floatcast.floatsight.fragment.trackfragment.TrackFragment;
import org.floatcast.floatsight.fragment.trackfragment.plot.PlotFragment;
import org.floatcast.floatsight.fragment.trackfragment.stats.TrackStatsFragment;
import org.floatcast.floatsight.fragment.trackmenu.SaveFileDialogTextWatcher;
import org.floatcast.floatsight.fragment.trackmenu.TrackMenuFragment;
import org.floatcast.floatsight.permissionactivity.PermissionActivity;
import org.floatcast.floatsight.permissionactivity.PermissionStrategy;
import org.floatcast.floatsight.viewmodel.FlySightTrackDataViewModel;
import org.floatcast.floatsight.csvparser.FlySightCsvParser;
import org.floatcast.floatsight.datarepository.DataRepository;
import org.floatcast.floatsight.fragment.trackfragment.TrackFragment;
import org.floatcast.floatsight.fragment.trackfragment.plot.PlotFragment;
import org.floatcast.floatsight.fragment.trackfragment.stats.TrackStatsFragment;
import org.floatcast.floatsight.fragment.trackmenu.SaveFileDialogTextWatcher;
import org.floatcast.floatsight.fragment.trackmenu.TrackMenuFragment;
import org.floatcast.floatsight.permissionactivity.PermissionActivity;
import org.floatcast.floatsight.permissionactivity.PermissionStrategy;
import org.floatcast.floatsight.viewmodel.FlySightTrackDataViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrackActivity extends PermissionActivity {

    public static final String TAG_PLOT_FRAGMENT = "TAG_PLOT_FRAGMENT";
    public static final String TAG_STATS_FRAGMENT = "TAG_STATS_FRAGMENT";
    public static final String TAG_TRACK_MENU_FRAGMENT = "TAG_TRACK_MENU_FRAGMENT";
    public static final String TRACK_FILE_URI = "TRACK_FILE_URI";

    private static final int TRACK_LOAD_PERMISSION_REQUEST_CODE = 300;
    private static final int TRACK_SAVE_PERMISSION_REQUEST_CODE = 301;

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
        getMenuInflater().inflate(R.menu.menu_track_activity, menu);
        return true;
    }

    public void loadFlySightTrackData() {
        new PermissionStrategy(TRACK_LOAD_PERMISSION_REQUEST_CODE) {
            public void task() {
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
        }.execute(this);
    }

    private void reopenActivityWithUri(Uri uri) {
        Intent showTrackIntent = new Intent(this, TrackActivity.class);
        showTrackIntent.putExtra(TRACK_FILE_URI, uri.toString());
        startActivity(showTrackIntent);
        finish();
    }

    public void saveFlySightTrackData() {
        new PermissionStrategy(TRACK_SAVE_PERMISSION_REQUEST_CODE) {
            public void task() {
                try {
                    AlertDialog alertDialog = new AlertDialog.Builder(TrackActivity.this)
                            .setView(R.layout.text_input_layout)
                            .setPositiveButton(getString(R.string.save), (dialog, which) -> {
                                TextInputLayout textInputLayout = ((Dialog) dialog).findViewById(R.id.text_input_layout);
                                String newFileName = textInputLayout.getEditText().getText().toString().trim();
                                try {
                                    File newFile = new File(getImportFolder(), newFileName);
                                    (new FlySightCsvParser()).write(new FileOutputStream(newFile), flySightTrackDataViewModel.getLiveData().getValue());
                                    reopenActivityWithUri(Uri.fromFile(newFile));
                                } catch (FileNotFoundException e) {
                                    showAlertOKCancel(getResources().getString(R.string.save_error), null);
                                } catch (IOException e) {
                                    showAlertOKCancel(getResources().getString(R.string.error_opening_local_storage), null);
                                }

                            })
                            .create();

                    alertDialog.show();

                    TextInputLayout inputLayout = alertDialog.findViewById(R.id.text_input_layout);
                    inputLayout.setHint(getString(R.string.file_name_hint));
                    String fileName = flySightTrackDataViewModel.getLiveData().getValue().getSourceFileName();
                    inputLayout.getEditText().setText(fileName);
                    inputLayout.getEditText().setSelection(0,
                            fileName.contains(".") ? fileName.lastIndexOf(".") : fileName.length());

                    SaveFileDialogTextWatcher textWatcher =
                            new SaveFileDialogTextWatcher(fileName, getFilesInCurrentImportFolder(), inputLayout, alertDialog);
                    inputLayout.getEditText().addTextChangedListener(textWatcher);
                    inputLayout.setError(getString(R.string.file_already_exists));

                } catch (FileNotFoundException e) {
                    showAlertOKCancel(getResources().getString(R.string.save_error), null);
                }
            }
        }.execute(this);
    }

    private File getImportFolder() throws FileNotFoundException {
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String trackPath = extras.getString(TRACK_FILE_URI);
            if (trackPath != null) {
                Uri trackFileUri = Uri.parse(extras.getString(TRACK_FILE_URI));
                File trackFile = new File(trackFileUri.getPath());
                return trackFile.getParentFile();
            } else {
                throw new FileNotFoundException();
            }
        } else {
            throw new FileNotFoundException();
        }
    }

    private List<String> getFilesInCurrentImportFolder() throws FileNotFoundException {
        List<String> files = new ArrayList<>();
        File importFolder = getImportFolder();
        if(importFolder.exists() && importFolder.isDirectory()) {
            for (File file : importFolder.listFiles()) {
                files.add(file.getName());
            }
        } else {
            throw new FileNotFoundException();
        }
        return files;
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
        TrackFragment trackFragment = (TrackFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_PLOT_FRAGMENT);
        if (trackFragment != null) {
            trackFragment.onUnitsDialogCheckboxClicked(view);
            return;
        }

        trackFragment = (TrackStatsFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_STATS_FRAGMENT);
        if (trackFragment != null) {
            trackFragment.onUnitsDialogCheckboxClicked(view);
        }
    }

    @VisibleForTesting
    public FlySightTrackDataViewModel getFlySightTrackDataViewModel() {
        return flySightTrackDataViewModel;
    }
}
