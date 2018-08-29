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

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.watchthybridle.floatsight.chartfragments.ChartFragment;
import com.watchthybridle.floatsight.chartfragments.PlotFragment;
import com.watchthybridle.floatsight.data.FlySightTrackData;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.MainActivity.TAG_PLOT_FRAGMENT;
import static com.watchthybridle.floatsight.MainActivity.TAG_MAIN_MENU_FRAGMENT;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainMenuFragment extends ChartFragment implements AdapterView.OnItemClickListener {
	@Retention(SOURCE)
	@IntDef({BUTTON_IMPORT, BUTTON_PLOT, BUTTON_ABOUT})
	private @interface MainMenuButtonPosition {}
	private static final int BUTTON_IMPORT = 0;
	private static final int BUTTON_PLOT = 1;
	private static final int BUTTON_ABOUT = 2;

	public MainMenuFragment() {
	}

	protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
		if (flySightTrackData.getFlySightTrackPoints().isEmpty()
				|| flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL) {
			showParsingErrorMessage(R.string.fail_parsing_file);
		} else if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_ERRORS) {
			showParsingErrorMessage(R.string.some_errors_parsing_file);
		} else {
			Snackbar mySnackbar = Snackbar.make(getView(), R.string.file_import_success, Snackbar.LENGTH_SHORT);
			mySnackbar.show();
		}
	}

	protected void showParsingErrorMessage(@StringRes int stringResId) {
		Context context = getContext();
		if(context != null) {
			new AlertDialog.Builder(context)
					.setMessage(stringResId)
					.setPositiveButton(R.string.ok, null)
					.create()
					.show();
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_menu, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		MainMenuButtonAdapter mainMenuButtonAdapter = new MainMenuButtonAdapter(getContext(), getButtons());
		ListView mainMenuButtonListView = view.findViewById(R.id.main_menu_button_list_view);
		mainMenuButtonListView.setAdapter(mainMenuButtonAdapter);
		mainMenuButtonListView.setOnItemClickListener(this);
	}

	private List<MainMenuButtonItem> getButtons() {
		List<MainMenuButtonItem> mainMenuButtonList = new ArrayList<>();
		mainMenuButtonList.add(new MainMenuButtonItem(R.string.button_import_title, R.string.button_import_description));
		mainMenuButtonList.add(new MainMenuButtonItem(R.string.button_plot_title, R.string.button_plot_description));
		mainMenuButtonList.add(new MainMenuButtonItem(R.string.button_about_title, R.string.button_about_description));
		return mainMenuButtonList;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, @MainMenuButtonPosition int pos, long arg3) {
		switch (pos) {
			case BUTTON_IMPORT:
				startImportFile();
				break;
			case BUTTON_PLOT:
				showPlotFragment();
				break;
			case BUTTON_ABOUT:
				showAboutDialog();
				break;
			default:
				break;
		}
	}

    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(getContext());
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.about_dialog, frameView);
        TextView versionTextView = dialoglayout.findViewById(R.id.about_dialog_version);
		versionTextView.setText(getString(R.string.about_dialog_version, BuildConfig.VERSION_NAME));
        dialog.show();
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
}
