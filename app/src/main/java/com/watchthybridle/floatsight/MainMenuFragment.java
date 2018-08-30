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

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.chartfragments.ChartFragment;
import com.watchthybridle.floatsight.chartfragments.PlotFragment;
import com.watchthybridle.floatsight.data.FlySightTrackData;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.MainActivity.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainMenuFragment extends ChartFragment implements MainMenuButtonAdapter.MainMenuItemClickListener {
	@Retention(SOURCE)
	@IntDef({BUTTON_IMPORT, BUTTON_PLOT, BUTTON_SAVE, BUTTON_LOAD, BUTTON_ABOUT})
	private @interface MainMenuButtonId {}
	private static final int BUTTON_IMPORT = 0;
	private static final int BUTTON_PLOT = 1;
	private static final int BUTTON_SAVE = 2;
	private static final int BUTTON_LOAD = 3;
	private static final int BUTTON_ABOUT = 4;

	MainMenuButtonAdapter mainMenuButtonAdapter;
	private FlySightTrackData flySightTrackData = new FlySightTrackData();

	public MainMenuFragment() {
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
		this.flySightTrackData = flySightTrackData;
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
		updateButtonVisiblity();
	}

	private List<MainMenuButtonItem> getButtons() {
		List<MainMenuButtonItem> mainMenuButtonList = new ArrayList<>();
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_IMPORT, R.string.button_import_title, R.string.button_import_description));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_PLOT, R.string.button_plot_title, R.string.button_plot_description));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_SAVE, R.string.button_save_title, R.string.button_save_description));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_LOAD, R.string.button_load_title, R.string.button_load_description));
		mainMenuButtonList.add(new MainMenuButtonItem(BUTTON_ABOUT, R.string.button_about_title, R.string.button_about_description));
		return mainMenuButtonList;
	}

	public void onItemClick(@MainMenuButtonId int id) {
		switch (id) {
			case BUTTON_IMPORT:
				startImportFile();
				break;
			case BUTTON_PLOT:
				showPlotFragment();
				break;
			case BUTTON_SAVE:
				MainMenuDialogs.showSaveDialog(this, flySightTrackData);
				break;
			case BUTTON_LOAD:
				showFilePickerFragment();
				break;
			case BUTTON_ABOUT:
				MainMenuDialogs.showAboutDialog(getContext());
				break;
			default:
				break;
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

	private void showFilePickerFragment() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			FilePickerFragment filePickerFragment = new FilePickerFragment();
			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction
					.replace(R.id.fragment_container, filePickerFragment,
							TAG_FILE_PICKER_FRAGMENT)
					.addToBackStack(TAG_MAIN_MENU_FRAGMENT)
					.commit();
		}
	}
}
