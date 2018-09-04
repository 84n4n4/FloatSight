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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import com.watchthybridle.floatsight.csvparser.FlySightCsvParser;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.datarepository.DataRepository;
import com.watchthybridle.floatsight.fragment.mainmenu.MainMenuFragment;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_PLOT_FRAGMENT = "TAG_PLOT_FRAGMENT";
    public static final String TAG_FILE_PICKER_FRAGMENT = "TAG_FILE_PICKER_FRAGMENT";
    public static final String TAG_MAIN_MENU_FRAGMENT = "TAG_MAIN_MENU_FRAGMENT";
    public static final String TAG_TRACK_MENU_FRAGMENT = "TAG_TRACK_MENU_FRAGMENT";

    public static final int REQUEST_FILE = 666;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private static final DatePrinter DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH_mm_ss");


    private FlySightTrackDataViewModel flySightTrackDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (savedInstanceState == null) {
            showMainMenuFragment();
		}

        flySightTrackDataViewModel = ViewModelProviders.of(this).get(FlySightTrackDataViewModel.class);
        flySightTrackDataViewModel.getLiveData()
                .observe(this, flySightTrackData -> actOnDataChanged(flySightTrackData));
        FlySightTrackData flySightTrackData = flySightTrackDataViewModel.getLiveData().getValue();
    }

    public void actOnDataChanged(FlySightTrackData flySightTrackData) {
        findViewById(R.id.toolbar_progress_bar).setVisibility(View.GONE);
    }

    private void showMainMenuFragment() {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mainMenuFragment,
                TAG_MAIN_MENU_FRAGMENT);
        transaction.commit();
    }

    public void startImportFile() {
        if (!checkPermission()) {
            requestPermission();
        } else {
            Intent intent = new Intent()
                    .setType("text/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setAction(Intent.ACTION_OPEN_DOCUMENT)
                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE);
        }
    }

    public File getTracksFolder() throws FileNotFoundException {
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator
                + "FloatSight" + File.separator + "tracks" + File.separator);
        if(!folder.exists()) {
            folder.mkdirs();
        }
        if(!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Could not access folder on local storage");
        }
        return folder;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILE) {
            if(resultCode == RESULT_OK) {
                try {
                    importData(data);
                    //todo when done open filepicker at that location
                    //TODO show toast files imported
                } catch (IOException e) {
                    e.printStackTrace();
                    //todo show error message
                }
            } else {
                findViewById(R.id.toolbar_progress_bar).setVisibility(View.GONE);
            }
        }
    }

    //todo move to files importer class
    //todo make async task
    private void importData(Intent data) throws IOException {
        if (data == null) {
            return;
        }
        List<Uri> uriList = new ArrayList<>();
        ClipData clipData = data.getClipData();
        if (clipData != null) {
            for (int index = 0; index < clipData.getItemCount(); index ++) {
                uriList.add(clipData.getItemAt(index).getUri());
            }
        } else {
            uriList.add(data.getData());
        }
        for (Uri uri : uriList) {
            copyFromUri(uri);
        }
    }

    //todo move to files importer class
    private void copyFromUri(Uri uri) throws IOException {
        File folder = new File(getTracksFolder(), DATE_PRINTER.format(Calendar.getInstance().getTime()));
        if(!folder.exists()) {
            folder.mkdirs();
        }
        if(!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Could not access folder on local storage");
        }
        String fileName = File.separator + resolveFileName(uri);
        File outFile = new File(folder, fileName);

        byte[] buffer = new byte[8 * 1024];
        int length;
        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(outFile)) {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    //todo move to files importer class
    private String resolveFileName(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }

    public void loadFlySightTrackData(Uri uri) {
        findViewById(R.id.toolbar_progress_bar).setVisibility(View.VISIBLE);
        DataRepository<FlySightTrackData> repository =
                new DataRepository<>(FlySightTrackData.class, getContentResolver(), new FlySightCsvParser());
        repository.load(uri, flySightTrackDataViewModel);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (readAccepted && writeAccepted) {
                    }
                    else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showAlertOKCancel(getResources().getString(R.string.permissions_rationale),
                                        (dialog, which) -> {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                        PERMISSION_REQUEST_CODE);
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
        new AlertDialog.Builder(MainActivity.this)
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
        PlotFragment plotFragment =
                (PlotFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainActivity.TAG_PLOT_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onYAxisDialogCheckboxClicked(view);
        }
    }

    public void onXAxisDialogCheckboxClicked(View view) {
        PlotFragment plotFragment =
                (PlotFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainActivity.TAG_PLOT_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onXAxisDialogCheckboxClicked(view);
        }
    }

    public void onUnitsDialogCheckboxClicked(View view) {
        PlotFragment plotFragment =
                (PlotFragment) getSupportFragmentManager()
                        .findFragmentByTag(MainActivity.TAG_PLOT_FRAGMENT);
        if (plotFragment != null) {
            plotFragment.onUnitsDialogCheckboxClicked(view);
        }
    }

    @VisibleForTesting
    public FlySightTrackDataViewModel getFlySightTrackDataViewModel() {
        return flySightTrackDataViewModel;
    }
}
