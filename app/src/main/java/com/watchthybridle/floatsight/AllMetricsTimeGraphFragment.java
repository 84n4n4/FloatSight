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
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.linedatasetcreation.TrackLineDataSetWrapper;

import java.util.ArrayList;
import java.util.List;

public class AllMetricsTimeGraphFragment extends Fragment {

    private GlideOverlayChart chart;


    public AllMetricsTimeGraphFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FlySightTrackDataViewModel trackDataViewModel = ((MainActivity) getActivity()).getFlySightTrackDataViewModel();
        trackDataViewModel.getFlySightTrackDataLiveData()
                .observe(this, flySightTrackData -> showData(flySightTrackData));
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = view.findViewById(R.id.root_graph_view);

        chart = new GlideOverlayChart(getContext());
        chart.invalidate();
        chart.setPinchZoom(true);

        frameLayout.addView(chart.glideChart);
        frameLayout.addView(chart);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_metrics_time_graph, container, false);
    }

    private void showParsingErrorMessage(@StringRes int stringResId) {
        Context context = getContext();
        if(context != null) {
            new AlertDialog.Builder(context)
                    .setMessage(stringResId)
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        }
    }

    public void showData(FlySightTrackData flySightTrackData) {
        if (flySightTrackData.isAnyMetricEmpty()) {
            return;
        }

        if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL) {
            showParsingErrorMessage(R.string.fail_parsing_file);
        }

        if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_ERRORS) {
            showParsingErrorMessage(R.string.some_errors_parsing_file);
        }

        List<ILineDataSet> dataSets = new ArrayList<>();

        dataSets.add(TrackLineDataSetWrapper.getLineDataSet(getContext(),
                TrackLineDataSetWrapper.ALTITUDE_VS_TIME,
                flySightTrackData));

        dataSets.add(TrackLineDataSetWrapper.getLineDataSet(getContext(),
                TrackLineDataSetWrapper.VERT_VELOCITY_VS_TIME,
                flySightTrackData));

        dataSets.add(TrackLineDataSetWrapper.getLineDataSet(getContext(),
                TrackLineDataSetWrapper.HOR_VELOCITY_VS_TIME,
                flySightTrackData));

        chart.setData(new LineData(dataSets));

        chart.glideChart.setData(new LineData(TrackLineDataSetWrapper.getLineDataSet(getContext(),
                TrackLineDataSetWrapper.GLIDE_VS_TIME,
                flySightTrackData)));

        chart.invalidate();
        chart.setPinchZoom(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
