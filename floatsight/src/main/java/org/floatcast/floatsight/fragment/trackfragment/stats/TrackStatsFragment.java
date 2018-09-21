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

package org.floatcast.floatsight.fragment.trackfragment.stats;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.data.FlySightTrackData;
import org.floatcast.floatsight.fragment.trackfragment.TrackFragment;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.CappedTrackPointValueProvider;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;
import org.floatcast.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;
import org.floatcast.floatsight.recyclerview.DividerLineDecorator;

import java.util.ArrayList;
import java.util.List;

public class TrackStatsFragment extends TrackFragment {

	public static final int FILENAME = 0;
	public static final int START_TIME = 1;
	public static final int DURATION = 2;
	public static final int MAX_ALTITUDE = 3;
	public static final int MIN_ALTITUDE = 4;
	public static final int DISTANCE = 5;
	public static final int ONE_TO_ONE = 6;
	public static final int MAX_HOR_VELOCITY = 7;
	public static final int MIN_HOR_VELOCITY = 8;
	public static final int AVG_HOR_VELOCITY = 9;
	public static final int MAX_VERT_VELOCITY = 10;
	public static final int MIN_VERT_VELOCITY = 11;
	public static final int AVG_VERT_VELOCITY = 12;
	public static final int MAX_GLIDE = 13;
	public static final int MIN_GLIDE = 14;
	public static final int AVG_GLIDE = 15;

    private static final DatePrinter PRETTY_DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	private StatsAdapter statsAdapter;

	public TrackStatsFragment() {
		// intentional empty constructor for fragments
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.stats_fragment_menu, menu);
		boolean enabled = trackDataViewModel != null && trackDataViewModel.containsValidData();
		menu.findItem(R.id.menu_item_units).setEnabled(enabled);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_item_units:
				showUnitsDialog(unitSystem);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
		if (!isValid(flySightTrackData)) {
			return;
		}
		updateStatsItems(flySightTrackData);
		statsAdapter.notifyDataSetChanged();

		invalidateOptionsMenu();
	}

	private void updateStatsItems(FlySightTrackData flySightTrackData) {
		TrackPointValueProvider timeXProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
		if(flySightTrackData.isDirty()) {
			statsAdapter.statsItems.get(FILENAME).value = getContext().getString(R.string.button_label_file_modified, flySightTrackData.getSourceFileName());
		} else {
			statsAdapter.statsItems.get(FILENAME).value = flySightTrackData.getSourceFileName();
		}

		float trackTimeInSeconds = flySightTrackData.getFlySightTrackPoints().get(flySightTrackData.getFlySightTrackPoints().size() - 1).trackTimeInSeconds;
		long unixStartTime = flySightTrackData.getFlySightTrackPoints().get(0).unixTimeStamp;
        statsAdapter.statsItems.get(START_TIME).value = PRETTY_DATE_PRINTER.format(unixStartTime);

		ChartDataSetProperties timeProperties = new ChartDataSetProperties.TimeDataSetProperties(timeXProvider);
        statsAdapter.statsItems.get(DURATION).value = timeProperties.getFormattedYMax(getContext(), flySightTrackData);

        ChartDataSetProperties altitudeProperties = new ChartDataSetProperties.AltitudeDataSetProperties(timeXProvider, unitSystem);
		statsAdapter.statsItems.get(MAX_ALTITUDE).value = altitudeProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_ALTITUDE).value = altitudeProperties.getFormattedYMin(getContext(), flySightTrackData);

		ChartDataSetProperties distanceProperties = new ChartDataSetProperties.DistanceDataSetProperties(timeXProvider, unitSystem);
		statsAdapter.statsItems.get(DISTANCE).value = distanceProperties.getFormattedYMax(getContext(), flySightTrackData);

		float distanceSurpassesAltitude = flySightTrackData.distanceSurpassesAltitude();
		if(unitSystem.equals(ChartDataSetProperties.IMPERIAL)) {
			distanceSurpassesAltitude = distanceSurpassesAltitude * 3.28084f;
		}
		statsAdapter.statsItems.get(ONE_TO_ONE).value = distanceProperties.getFormattedValue(getContext(), distanceSurpassesAltitude);

		ChartDataSetProperties horVelocityProperties = new ChartDataSetProperties.HorizontalVelocityDataSetProperties(timeXProvider, unitSystem);
		statsAdapter.statsItems.get(MAX_HOR_VELOCITY).value = horVelocityProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_HOR_VELOCITY).value = horVelocityProperties.getFormattedYMin(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(AVG_HOR_VELOCITY).value = horVelocityProperties.getFormattedValueForRange(getContext(), 0f, trackTimeInSeconds, flySightTrackData);

		ChartDataSetProperties vertVelocityProperties = new ChartDataSetProperties.VerticalVelocityDataSetProperties(timeXProvider, unitSystem);
		statsAdapter.statsItems.get(MAX_VERT_VELOCITY).value = vertVelocityProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_VERT_VELOCITY).value = vertVelocityProperties.getFormattedYMin(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(AVG_VERT_VELOCITY).value = vertVelocityProperties.getFormattedValueForRange(getContext(), 0f, trackTimeInSeconds, flySightTrackData);

		ChartDataSetProperties glideProperties = new ChartDataSetProperties.GlideDataSetProperties(timeXProvider, new CappedTrackPointValueProvider(TrackPointValueProvider.GLIDE_VALUE_PROVIDER, Float.MAX_VALUE));
		statsAdapter.statsItems.get(MAX_GLIDE).value = glideProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_GLIDE).value = glideProperties.getFormattedYMin(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(AVG_GLIDE).value = glideProperties.getFormattedValueForRange(getContext(), 0f, trackTimeInSeconds, flySightTrackData);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_rv_button_menu, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		RecyclerView recyclerView =  view.findViewById(R.id.button_list_view);
		recyclerView.setHasFixedSize(true);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(layoutManager);
        statsAdapter = new StatsAdapter(getStatsItems());
		recyclerView.setAdapter(statsAdapter);
		recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
	}

	private List<StatsItem> getStatsItems() {
		List<StatsItem> statsItemList = new ArrayList<>();
		statsItemList.add(new StatsItem(R.string.stats_filename, "", R.drawable.disk));
		statsItemList.add(new StatsItem(R.string.stats_start_time, "", R.drawable.time_stats));
		statsItemList.add(new StatsItem(R.string.stats_duration, "", R.drawable.time_stats));
		statsItemList.add(new StatsItem(R.string.stats_max_altitude, "", R.drawable.alt_stats));
		statsItemList.add(new StatsItem(R.string.stats_min_altitude, "", R.drawable.alt_stats));
		statsItemList.add(new StatsItem(R.string.stats_distance, "", R.drawable.dist_stats));
		statsItemList.add(new StatsItem(R.string.stats_one_to_one, "", R.drawable.one_to_one_stats));
		statsItemList.add(new StatsItem(R.string.stats_max_horizontal_velocity, "", R.drawable.hor_stats));
		statsItemList.add(new StatsItem(R.string.stats_min_horizontal_velocity, "", R.drawable.hor_stats));
		statsItemList.add(new StatsItem(R.string.stats_avg_horizontal_velocity, "", R.drawable.hor_avg_stats));
		statsItemList.add(new StatsItem(R.string.stats_max_vertical_velocity, "", R.drawable.vert_stats));
		statsItemList.add(new StatsItem(R.string.stats_min_vertical_velocity, "", R.drawable.vert_stats));
		statsItemList.add(new StatsItem(R.string.stats_avg_vertical_velocity, "", R.drawable.vert_avg_stats));
		statsItemList.add(new StatsItem(R.string.stats_max_glide, "", R.drawable.glide_stats));
		statsItemList.add(new StatsItem(R.string.stats_min_glide, "", R.drawable.glide_stats));
		statsItemList.add(new StatsItem(R.string.stats_avg_glide, "", R.drawable.glide_avg_stats));
		return statsItemList;
	}

	@Override
	public void onUnitsDialogCheckboxClicked(String unitSystem) {
		this.unitSystem = unitSystem;
		actOnDataChanged(trackDataViewModel.getLiveData().getValue());
	}
}
