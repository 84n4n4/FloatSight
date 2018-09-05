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

package com.watchthybridle.floatsight.fragment.stats;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties.METRIC;

public class TrackStatsFragment extends Fragment {

	public static final int FILENAME = 0;
	public static final int START_TIME = 1;
	public static final int DURATION = 2;
	public static final int MAX_ALTITUDE = 3;
	public static final int MIN_ALTITUDE = 4;
	public static final int DISTANCE = 5;
	public static final int MAX_HOR_VELOCITY = 6;
	public static final int MIN_HOR_VELOCITY = 7;
	public static final int MAX_VERT_VELOCITY = 8;
	public static final int MIN_VERT_VELOCITY = 9;

	private FlySightTrackDataViewModel trackDataViewModel;
	private StatsAdapter statsAdapter;
	private String unitSystem = METRIC;

	private static final DatePrinter PRETTY_DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	private List<ChartDataSetProperties> dataSetPropertiesList;

	public TrackStatsFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trackDataViewModel = ViewModelProviders.of(getActivity()).get(FlySightTrackDataViewModel.class);

		trackDataViewModel.getLiveData()
				.observe(this, flySightTrackData -> actOnDataChanged(flySightTrackData));
	}

	protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
		if(getActivity() != null) {
			getActivity().invalidateOptionsMenu();
		}

		if (!isValid(flySightTrackData)) {
			return;
		}
		updateStatsItems(flySightTrackData);

		statsAdapter.notifyDataSetChanged();
	}

	private void updateStatsItems(FlySightTrackData flySightTrackData) {
		TrackPointValueProvider dummyXProvider = TrackPointValueProvider.TIME_VALUE_PROVIDER;
		statsAdapter.statsItems.get(FILENAME).value = flySightTrackData.getSourceFileName();

		long unixStartTime = flySightTrackData.getFlySightTrackPoints().get(0).unixTimeStamp;
		String formattedStartTime = PRETTY_DATE_PRINTER.format(unixStartTime);
		statsAdapter.statsItems.get(START_TIME).value = formattedStartTime;

		ChartDataSetProperties timeProperties = new ChartDataSetProperties.TimeDataSetProperties(dummyXProvider);
		long unixEndTime = flySightTrackData.getFlySightTrackPoints().get(flySightTrackData.getFlySightTrackPoints().size() - 1).unixTimeStamp - unixStartTime;
		String formattedDuration = getContext().getString(R.string.seconds, timeProperties.decimalFormat.format(unixEndTime));
		statsAdapter.statsItems.get(DURATION).value = formattedDuration;

		ChartDataSetProperties altitudeProperties = new ChartDataSetProperties.AltitudeDataSetProperties(dummyXProvider, unitSystem);
		statsAdapter.statsItems.get(MAX_ALTITUDE).value = altitudeProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_ALTITUDE).value = altitudeProperties.getFormattedYMin(getContext(), flySightTrackData);

		ChartDataSetProperties distanceProperties = new ChartDataSetProperties.DistanceDataSetProperties(dummyXProvider, unitSystem);
		statsAdapter.statsItems.get(DISTANCE).value = distanceProperties.getFormattedYMax(getContext(), flySightTrackData);

		ChartDataSetProperties horVelocityProperties = new ChartDataSetProperties.HorizontalVelocityDataSetProperties(dummyXProvider, unitSystem);
		statsAdapter.statsItems.get(MAX_HOR_VELOCITY).value = horVelocityProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_HOR_VELOCITY).value = horVelocityProperties.getFormattedYMin(getContext(), flySightTrackData);

		ChartDataSetProperties vertVelocityProperties = new ChartDataSetProperties.VerticalVelocityDataSetProperties(dummyXProvider, unitSystem);
		statsAdapter.statsItems.get(MAX_VERT_VELOCITY).value = vertVelocityProperties.getFormattedYMax(getContext(), flySightTrackData);
		statsAdapter.statsItems.get(MIN_VERT_VELOCITY).value = vertVelocityProperties.getFormattedYMin(getContext(), flySightTrackData);
	}

	public boolean isValid(FlySightTrackData flySightTrackData) {
		return !flySightTrackData.getFlySightTrackPoints().isEmpty()
				&& !(flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL);
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
        statsAdapter = new StatsAdapter(getStatsItems());
		recyclerView.setAdapter(statsAdapter);
		recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
	}

	private List<StatsItem> getStatsItems() {
		List<StatsItem> statsItemList = new ArrayList<>();
		statsItemList.add(new StatsItem(R.string.stats_filename, "", R.drawable.disk));
		statsItemList.add(new StatsItem(R.string.stats_start_time, "", R.drawable.ic_time));
		statsItemList.add(new StatsItem(R.string.stats_duration, "", R.drawable.ic_time));
		statsItemList.add(new StatsItem(R.string.stats_max_altitude, "", R.drawable.ic_alt_tot));
		statsItemList.add(new StatsItem(R.string.stats_min_altitude, "", R.drawable.ic_alt_tot));
		statsItemList.add(new StatsItem(R.string.stats_distance, "", R.drawable.ic_dist_tot));
		statsItemList.add(new StatsItem(R.string.stats_max_horizontal_velocity, "", R.drawable.ic_hor_vel));
		statsItemList.add(new StatsItem(R.string.stats_min_horizontal_velocity, "", R.drawable.ic_hor_vel));
		statsItemList.add(new StatsItem(R.string.stats_max_vertical_velocity, "", R.drawable.ic_vert_vel));
		statsItemList.add(new StatsItem(R.string.stats_min_vertical_velocity, "", R.drawable.ic_vert_vel));
		return statsItemList;
	}
}
