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

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.ConfigActivity;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.fragment.trackpicker.TrackPickerFragment;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.MainActivity.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainMenuFragment extends Fragment implements MainMenuButtonAdapter.MainMenuItemClickListener {
	@Retention(SOURCE)
	@IntDef({BUTTON_IMPORT, BUTTON_PLOT, BUTTON_SAVE, BUTTON_LOAD, BUTTON_CONFIG, BUTTON_ABOUT})
	private @interface MainMenuButtonId {}
	private static final int BUTTON_IMPORT = 0;
	private static final int BUTTON_PLOT = 1;
	private static final int BUTTON_SAVE = 2;
	private static final int BUTTON_LOAD = 3;
	private static final int BUTTON_CONFIG = 4;
	private static final int BUTTON_ABOUT = 5;

	private FlySightTrackDataViewModel trackDataViewModel;
	private MainMenuButtonAdapter mainMenuButtonAdapter;

	public MainMenuFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trackDataViewModel = ViewModelProviders.of(getActivity()).get(FlySightTrackDataViewModel.class);

		trackDataViewModel.getLiveData()
				.observe(this, flySightTrackData -> actOnDataChanged(flySightTrackData));
	}

	protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
		if (flySightTrackData.getFlySightTrackPoints().isEmpty()
				|| flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL) {
			MainMenuDialogs.showErrorMessage(getContext(), R.string.fail_parsing_file);
		} else if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_ERRORS) {
			MainMenuDialogs.showErrorMessage(getContext(), R.string.some_errors_parsing_file);
		} else {
			Snackbar mySnackbar = Snackbar.make(getView(), R.string.file_import_success, Snackbar.LENGTH_SHORT);
			mySnackbar.show();
		}
		updateButtonVisiblity();
	}

	private void updateButtonVisiblity() {
		mainMenuButtonAdapter.mainMenuButtonItems.get(BUTTON_PLOT).setEnabled(trackDataViewModel.containsValidData());
		mainMenuButtonAdapter.mainMenuButtonItems.get(BUTTON_SAVE).setEnabled(trackDataViewModel.containsValidData());
		mainMenuButtonAdapter.notifyDataSetChanged();
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_menu, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.main_menu_button_list_view);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);
        mainMenuButtonAdapter = new MainMenuButtonAdapter(getButtons());
        mainMenuButtonAdapter.setMainMenuItemClickListener(this);
		recyclerView.setAdapter(mainMenuButtonAdapter);
		recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
		updateButtonVisiblity();
	}

	private List<MainMenuButtonItem> getButtons() {
		List<MainMenuButtonItem> mainMenuButtonList = new ArrayList<>();
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_IMPORT, R.string.button_import_title, R.string.button_import_description, R.drawable.import_grey));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_PLOT, R.string.button_plot_title, R.string.button_plot_description, R.drawable.plot));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_SAVE, R.string.button_save_title, R.string.button_save_description, R.drawable.save));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_LOAD, R.string.button_load_title, R.string.button_load_description, R.drawable.load));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_CONFIG, R.string.button_config_title, R.string.button_config_description, R.drawable.config));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_ABOUT, R.string.button_about_title, R.string.button_about_description, R.drawable.info));
		return mainMenuButtonList;
	}

	public void onItemClick(MainMenuButtonItem buttonItem) {
		if (buttonItem.isEnabled) {
			int id = buttonItem.id;
			switch (id) {
				case BUTTON_IMPORT:
					startImportFile();
					break;
				case BUTTON_PLOT:
					showPlotFragment();
					break;
				case BUTTON_SAVE:
					showSaveDialog();
					break;
				case BUTTON_LOAD:
					showFilePickerFragment();
					break;
				case BUTTON_CONFIG:
					Intent configEditorIntent = new Intent(getActivity(), ConfigActivity.class);
					startActivity(configEditorIntent);
					break;
				case BUTTON_ABOUT:
					MainMenuDialogs.showAboutDialog(getContext());
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


	private void showPlotFragment() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			PlotFragment plotFragment = new PlotFragment();
			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction
					.replace(R.id.fragment_container, plotFragment,
							TAG_PLOT_FRAGMENT)
					.addToBackStack(TAG_MAIN_MENU_FRAGMENT)
					.commit();
		}
	}

	private void showSaveDialog() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity == null) {
			return;
		}

		if (!activity.checkPermission()) {
			activity.requestPermission();
		} else {
			MainMenuDialogs.showSaveDialog(this, trackDataViewModel.getLiveData().getValue());
		}
	}

	private void showFilePickerFragment() {
		MainActivity activity = (MainActivity) getActivity();
		if (activity == null) {
			return;
		}

		if (!activity.checkPermission()) {
			activity.requestPermission();
		} else {
			TrackPickerFragment fileTrackPickerFragment = new TrackPickerFragment();
			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction
					.replace(R.id.fragment_container, fileTrackPickerFragment,
							TAG_FILE_PICKER_FRAGMENT)
					.addToBackStack(TAG_MAIN_MENU_FRAGMENT)
					.commit();
		}
	}
}
