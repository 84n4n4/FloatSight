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

package com.watchthybridle.floatsight.fragment.trackmenu;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.IntDef;
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
import com.watchthybridle.floatsight.fragment.ButtonAdapter;
import com.watchthybridle.floatsight.fragment.ButtonItem;
import com.watchthybridle.floatsight.fragment.Dialogs;
import com.watchthybridle.floatsight.fragment.plot.PlotFragment;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;

import java.lang.annotation.Retention;
import java.util.ArrayList;
import java.util.List;

import static com.watchthybridle.floatsight.TrackActivity.TAG_PLOT_FRAGMENT;
import static java.lang.annotation.RetentionPolicy.SOURCE;

public class TrackMenuFragment extends Fragment implements ButtonAdapter.ButtonItemClickListener {
	@Retention(SOURCE)
	@IntDef({BUTTON_PLOT, BUTTON_STATS})
	private @interface TrackButtonId {}
	private static final int BUTTON_LABEL = 0;
	private static final int BUTTON_PLOT = 1;
	private static final int BUTTON_STATS = 2;

	private FlySightTrackDataViewModel trackDataViewModel;
	private ButtonAdapter buttonAdapter;

	private static final DatePrinter PRETTY_DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

	public TrackMenuFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trackDataViewModel = ViewModelProviders.of(getActivity()).get(FlySightTrackDataViewModel.class);

		trackDataViewModel.getLiveData()
				.observe(this, flySightTrackData -> actOnDataChanged(flySightTrackData));
	}

	protected void actOnDataChanged(FlySightTrackData flySightTrackData) {
		updateButtons();
		if (flySightTrackData.getFlySightTrackPoints().isEmpty()
				|| flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL) {
			Dialogs.showErrorMessage(getContext(), R.string.fail_parsing_file);
			ButtonItem labelButton = buttonAdapter.buttonItems.get(BUTTON_LABEL);
			labelButton.overrideTitle = getString(R.string.button_label_parsing_error);
			labelButton.overrideDescription = getString(R.string.fail_parsing_file);
		} else if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_ERRORS) {
				Dialogs.showErrorMessage(getContext(), R.string.some_errors_parsing_file);
		}
	}

	public void updateButtons() {
		ButtonItem labelButton = buttonAdapter.buttonItems.get(BUTTON_LABEL);
		ButtonItem plotButton = buttonAdapter.buttonItems.get(BUTTON_PLOT);
		ButtonItem statsButton = buttonAdapter.buttonItems.get(BUTTON_STATS);
		if (trackDataViewModel.containsValidData()) {
			FlySightTrackData flySightTrackData = trackDataViewModel.getLiveData().getValue();
			labelButton.overrideTitle = getString(R.string.button_label_filename, flySightTrackData.getSourceFileName());
			long unixStartTime = flySightTrackData.getFlySightTrackPoints().get(0).unixTimeStamp;
			String formattedTime = PRETTY_DATE_PRINTER.format(unixStartTime);
			labelButton.overrideDescription = getString(R.string.button_label_start_time, formattedTime);
			plotButton.setEnabled(true);
			statsButton.setEnabled(true);
		} else {
			labelButton.overrideTitle = getString(R.string.button_label_empty);
			labelButton.overrideDescription = getString(R.string.button_label_empty);
			plotButton.setEnabled(false);
			statsButton.setEnabled(false);
		}
		buttonAdapter.notifyDataSetChanged();
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
		updateButtons();
	}

	private List<ButtonItem> getButtons() {
		List<ButtonItem> trackMenuButtonList = new ArrayList<>();
		trackMenuButtonList.add(new ButtonItem(BUTTON_LABEL, R.string.button_label_empty, R.string.button_label_empty, R.drawable.disk));
		trackMenuButtonList.add(new ButtonItem(BUTTON_PLOT, R.string.button_plot_title, R.string.button_plot_description, R.drawable.plot));
		trackMenuButtonList.add(new ButtonItem(BUTTON_STATS, R.string.button_stats_title, R.string.button_stats_description, R.drawable.stats));
		return trackMenuButtonList;
	}

	public void onItemClick(ButtonItem buttonItem) {
		if (buttonItem.isEnabled) {
			int id = buttonItem.id;
			switch (id) {
				case BUTTON_PLOT:
					showPlotFragment();
					break;
				case BUTTON_STATS:
					showPlotFragment();
					break;
				default:
					break;
			}
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
					.addToBackStack(TAG_PLOT_FRAGMENT)
					.commit();
		}
	}
}
