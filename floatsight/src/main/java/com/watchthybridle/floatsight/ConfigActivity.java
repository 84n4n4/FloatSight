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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import com.watchthybridle.floatsight.configparser.ConfigParser;
import com.watchthybridle.floatsight.data.ConfigData;
import com.watchthybridle.floatsight.datarepository.DataRepository;
import com.watchthybridle.floatsight.fragment.configeditor.ConfigEditorFragment;
import com.watchthybridle.floatsight.viewmodel.ConfigDataViewModel;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ConfigActivity extends AppCompatActivity {

    public static final String TAG_CONFIG_FRAGMENT = "TAG_CONFIG_FRAGMENT";
    public static final int REQUEST_CONFIG_FILE = 777;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private ConfigDataViewModel configDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_with_fragment_container);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        configDataViewModel = ViewModelProviders.of(this).get(ConfigDataViewModel.class);
        configDataViewModel.getLiveData()
                .observe(this, this::actOnDataChanged);

        showConfigEditorFragment();
    }

    public void actOnDataChanged(ConfigData configData) {
        getSupportActionBar().setSubtitle(configData.getSourceFileName());
        findViewById(R.id.toolbar_progress_bar).setVisibility(GONE);

        if (configDataViewModel.containsValidData()) {
            showConfigEditorFragment();
        }
    }

    private void showConfigEditorFragment() {
        ConfigEditorFragment configEditorFragment = new ConfigEditorFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, configEditorFragment, TAG_CONFIG_FRAGMENT)
                .commit();
    }

    public void startImportFile() {
        if (!checkPermission()) {
            requestPermission();
        } else {
            Intent intent = new Intent()
                    .setType("text/*")
                    .addCategory(Intent.CATEGORY_OPENABLE)
                    .setAction(Intent.ACTION_OPEN_DOCUMENT);
            startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CONFIG_FILE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CONFIG_FILE) {
            if (resultCode == RESULT_OK) {
                loadFlySightConfigData(data.getData());
            } else {
                findViewById(R.id.toolbar_progress_bar).setVisibility(GONE);
            }
        }
    }

    public void loadFlySightConfigData(Uri uri) {
        findViewById(R.id.toolbar_progress_bar).setVisibility(VISIBLE);
        DataRepository<ConfigData> repository =
                new DataRepository<>(ConfigData.class, getContentResolver(), new ConfigParser());
        repository.load(uri, configDataViewModel);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
    }

    private void showAlertOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ConfigActivity.this)
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
}
