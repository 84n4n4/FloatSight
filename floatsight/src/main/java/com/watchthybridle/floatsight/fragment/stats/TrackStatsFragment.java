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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.fragment.Dialogs;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.MainActivity.TAG_PLOT_FRAGMENT;

public class TrackStatsFragment extends Fragment {

	private FlySightTrackDataViewModel trackDataViewModel;
	private StatsAdapter statsAdapter;

	private static final DatePrinter PRETTY_DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

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
		if (flySightTrackData.getFlySightTrackPoints().isEmpty()
				|| flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL) {
			Dialogs.showErrorMessage(getContext(), R.string.fail_parsing_file);
		} else {
			//TODO set stats to RV items
		}
		statsAdapter.notifyDataSetChanged();
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
        statsAdapter = new StatsAdapter(getButtons());
		recyclerView.setAdapter(statsAdapter);
		recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
	}

	private List<StatsItem> getButtons() {
		List<StatsItem> statsItemList = new ArrayList<>();
		//TODO add stats items
		return statsItemList;
	}
}
