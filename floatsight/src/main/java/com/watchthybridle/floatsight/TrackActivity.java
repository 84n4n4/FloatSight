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

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.datarepository.DataRepository;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.fragment.stats.TrackStatsFragment;
import com.watchthybridle.floatsight.fragment.trackmenu.TrackMenuFragment;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class TrackActivity extends AppCompatActivity {

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
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, TRACK_LOAD_PERMISSION_REQUEST_CODE);
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

    private void reopenActivityWithUri(Uri uri) {
        Intent showTrackIntent = new Intent(this, TrackActivity.class);
        showTrackIntent.putExtra(TRACK_FILE_URI, uri.toString());
        startActivity(showTrackIntent);
        finish();
    }

    public void saveFlySightTrackData() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, TRACK_SAVE_PERMISSION_REQUEST_CODE);
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this)
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
    }

    private class SaveFileDialogTextWatcher implements TextWatcher {

        private List<String> filesInFolder;
        private TextInputLayout textInputLayout;
        private AlertDialog alertDialog;
        private String oldFileName;

        SaveFileDialogTextWatcher(String oldFileName, List<String> filesInFolder, TextInputLayout textInputLayout, AlertDialog alertDialog) {
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
                error = getString(R.string.file_has_to_be_csv);
            } else if (input.startsWith(".")) {
                error = getString(R.string.file_name_empty);
            } else if (filesInFolder.contains(input)) {
                error = getString(R.string.file_already_exists);
            }

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(error == null);

            if (input.equals(oldFileName)) {
                error = getString(R.string.file_overwrite_warning);
            }
            textInputLayout.setError(error);
        }
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
            case TRACK_LOAD_PERMISSION_REQUEST_CODE:
                if (readAccepted && writeAccepted) {
                    loadFlySightTrackData();
                } else {
                    showPermissionRationale(TRACK_LOAD_PERMISSION_REQUEST_CODE);
                }
                break;
            case TRACK_SAVE_PERMISSION_REQUEST_CODE:
                if (readAccepted && writeAccepted) {
                    saveFlySightTrackData();
                } else {
                    showPermissionRationale(TRACK_SAVE_PERMISSION_REQUEST_CODE);
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
        String unitSystem = ChartDataSetProperties.METRIC;
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_metric:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_imperial)).setChecked(!checked);
                if(checked) {
                    unitSystem = ChartDataSetProperties.METRIC;
                } else {
                    unitSystem = ChartDataSetProperties.IMPERIAL;
                }
                break;
            case R.id.checkbox_imperial:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_metric)).setChecked(!checked);
                if(checked) {
                    unitSystem = ChartDataSetProperties.IMPERIAL;

                } else {
                    unitSystem = ChartDataSetProperties.METRIC;
                }
                break;
        }

        PlotFragment plotFragment = (PlotFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_PLOT_FRAGMENT);
        TrackStatsFragment trackStatsFragment = (TrackStatsFragment) getSupportFragmentManager()
                .findFragmentByTag(TrackActivity.TAG_STATS_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onUnitsDialogCheckboxClicked(unitSystem);
        }
        if (trackStatsFragment != null) {
            trackStatsFragment.onUnitsDialogCheckboxClicked(unitSystem);
        }
    }

    public void showUnitsDialog(String currentUnitSystem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.plot_units_dialog_title)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(this);
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.units_dialog, frameView);
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_metric)).setChecked(currentUnitSystem.equals(ChartDataSetProperties.METRIC));
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_imperial)).setChecked(currentUnitSystem.equals(ChartDataSetProperties.IMPERIAL));
        dialog.show();
    }

    @VisibleForTesting
    public FlySightTrackDataViewModel getFlySightTrackDataViewModel() {
        return flySightTrackDataViewModel;
    }
}
