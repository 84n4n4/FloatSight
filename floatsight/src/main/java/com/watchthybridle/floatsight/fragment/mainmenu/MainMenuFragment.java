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

package com.watchthybridle.floatsight.fragment.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.ConfigActivity;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.fragment.ButtonAdapter;
import com.watchthybridle.floatsight.fragment.ButtonItem;
import com.watchthybridle.floatsight.fragment.Dialogs;
import com.watchthybridle.floatsight.fragment.trackpicker.TrackPickerFragment;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.watchthybridle.floatsight.MainActivity.LOAD_PERMISSION_REQUEST_CODE;
import static com.watchthybridle.floatsight.MainActivity.TAG_FILE_PICKER_FRAGMENT;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainMenuFragment extends Fragment implements ButtonAdapter.ButtonItemClickListener {
	@Retention(SOURCE)
	@IntDef({BUTTON_IMPORT, BUTTON_LOAD, BUTTON_CONFIG, BUTTON_ABOUT})
	private @interface MainMenuButtonId {}
	private static final int BUTTON_IMPORT = 0;
	private static final int BUTTON_LOAD = 1;
	private static final int BUTTON_CONFIG = 2;
	private static final int BUTTON_ABOUT = 3;

	private ButtonAdapter buttonAdapter;

	public MainMenuFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_rv_button_menu, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.button_list_view);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);
        buttonAdapter = new ButtonAdapter(getButtons());
        buttonAdapter.setButtonItemClickListener(this);
		recyclerView.setAdapter(buttonAdapter);
		recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
	}

	private List<ButtonItem> getButtons() {
		List<ButtonItem> mainMenuButtonList = new ArrayList<>();
		mainMenuButtonList.add(new ButtonItem(BUTTON_IMPORT, R.string.button_import_title, R.string.button_import_description, R.drawable.import_grey));
        mainMenuButtonList.add(new ButtonItem(BUTTON_LOAD, R.string.button_load_title, R.string.button_load_description, R.drawable.folder));
		mainMenuButtonList.add(new ButtonItem(BUTTON_CONFIG, R.string.button_config_title, R.string.button_config_description, R.drawable.config));
		mainMenuButtonList.add(new ButtonItem(BUTTON_ABOUT, R.string.button_about_title, R.string.button_about_description, R.drawable.info));
		return mainMenuButtonList;
	}

	public void onItemClick(ButtonItem buttonItem) {
		if (buttonItem.isEnabled) {
			int id = buttonItem.id;
			switch (id) {
				case BUTTON_IMPORT:
					startImportFile();
					break;
				case BUTTON_LOAD:
					showTrackPickerFragment();
					break;
				case BUTTON_CONFIG:
					Intent configEditorIntent = new Intent(getActivity(), ConfigActivity.class);
					startActivity(configEditorIntent);
					break;
				case BUTTON_ABOUT:
					Dialogs.showAboutDialog(getContext());
					break;
				default:
					break;
			}
		}
	}

	private void startImportFile() {
		MainActivity mainActivity = (MainActivity) getActivity();
		if (mainActivity != null) {
			mainActivity.startImportFile();
		}
	}

	public void showTrackPickerFragment() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity == null) {
			return;
		}

		if (!activity.checkPermission()) {
			ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, LOAD_PERMISSION_REQUEST_CODE);
		} else {
			TrackPickerFragment fileTrackPickerFragment = new TrackPickerFragment();
			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction
					.replace(R.id.fragment_container, fileTrackPickerFragment,
							TAG_FILE_PICKER_FRAGMENT)
					.addToBackStack(TAG_FILE_PICKER_FRAGMENT)
					.commit();
		}
	}
}
