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
import android.widget.ListView;
import com.watchthybridle.floatsight.chartfragments.AllMetricsTimeChartFragment;
import com.watchthybridle.floatsight.chartfragments.ChartFragment;
import com.watchthybridle.floatsight.chartfragments.DistanceVsAltitudeChartFragment;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.MainActivity.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class MainMenuFragment extends ChartFragment implements AdapterView.OnItemClickListener {
	@Retention(SOURCE)
	@IntDef({BUTTON_IMPORT, BUTTON_ALL_METRICS_CHART, BUTTON_DISTANCE_VS_TIME_CHART, BUTTON_ABOUT})
	private @interface MainMenuButtonPosition {}
	private static final int BUTTON_IMPORT = 0;
	private static final int BUTTON_ALL_METRICS_CHART = 1;
	private static final int BUTTON_DISTANCE_VS_TIME_CHART = 2;
	private static final int BUTTON_ABOUT = 3;

	public MainMenuFragment() {
	}

	protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
		if (flySightTrackData.isAnyMetricEmpty()
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
		mainMenuButtonList.add(new MainMenuButtonItem(R.string.button_all_metrics_chart_title, R.string.button_all_metrics_chart_description));
		mainMenuButtonList.add(new MainMenuButtonItem(R.string.button_altitude_distance_chart_title, R.string.button_altitude_distance_chart_description));
		mainMenuButtonList.add(new MainMenuButtonItem(R.string.button_about_title, R.string.button_about_description));
		return mainMenuButtonList;
	}

	@Override
	public void onItemClick(AdapterView<?> av, View v, @MainMenuButtonPosition int pos, long arg3) {
		switch (pos) {
			case BUTTON_IMPORT:
				startImportFile();
				break;
			case BUTTON_ALL_METRICS_CHART:
				showAllMetricsFragmentChart();
				break;
			case BUTTON_DISTANCE_VS_TIME_CHART:
				showDistanceAltitudeFragmentChart();
				break;
			case BUTTON_ABOUT:
				showAboutDialog();
				break;
			default:
				break;
		}
	}

	private void showAboutDialog() {
		Context context = getContext();
		if (context != null) {
			String message = getString(R.string.about_dialog_message, BuildConfig.VERSION_NAME);
			new AlertDialog.Builder(getContext())
					.setTitle(R.string.app_name)
					.setMessage(message)
					.setPositiveButton(R.string.ok, null)
					.create()
					.show();
		}
	}

	private void startImportFile() {
		MainActivity mainActivity = (MainActivity) getActivity();
		if (mainActivity != null) {
			mainActivity.startImportFile();
		}
	}

	private void showAllMetricsFragmentChart() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			AllMetricsTimeChartFragment allMetricsTimeChartFragment = new AllMetricsTimeChartFragment();
			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction
					.replace(R.id.fragment_container, allMetricsTimeChartFragment,
					TAG_ALL_METRICS_V_TIME_CHART_FRAGMENT)
					.addToBackStack(TAG_MAIN_MENU_FRAGMENT)
					.commit();
		}
	}

	private void showDistanceAltitudeFragmentChart() {
		FragmentActivity activity = getActivity();
		if (activity != null) {
			DistanceVsAltitudeChartFragment distanceVsAltitudeChartFragment = new DistanceVsAltitudeChartFragment();
			FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, distanceVsAltitudeChartFragment,
					TAG_DISTANCE_V_ALTITUDE_CHART_FRAGMENT)
					.addToBackStack(TAG_MAIN_MENU_FRAGMENT)
					.commit();
		}
	}
}
