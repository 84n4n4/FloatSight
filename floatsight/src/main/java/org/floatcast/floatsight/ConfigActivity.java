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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.WindowManager;
import org.floatcast.floatsight.configparser.ConfigParser;
import org.floatcast.floatsight.data.ConfigData;
import org.floatcast.floatsight.datarepository.DataRepository;
import org.floatcast.floatsight.fragment.configeditor.ConfigEditorFragment;
import org.floatcast.floatsight.permissionactivity.PermissionActivity;
import org.floatcast.floatsight.permissionactivity.PermissionStrategy;
import org.floatcast.floatsight.viewmodel.ConfigDataViewModel;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ConfigActivity extends PermissionActivity {

    public static final String TAG_CONFIG_FRAGMENT = "TAG_CONFIG_FRAGMENT";
    public static final int REQUEST_CONFIG_FILE = 777;

    private static final int CONFIG_LOAD_PERMISSION_REQUEST_CODE = 400;

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
        new PermissionStrategy(CONFIG_LOAD_PERMISSION_REQUEST_CODE) {
            public void task() {
                Intent intent = new Intent()
                        .setType("text/*")
                        .addCategory(Intent.CATEGORY_OPENABLE)
                        .setAction(Intent.ACTION_OPEN_DOCUMENT);
                startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CONFIG_FILE);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
