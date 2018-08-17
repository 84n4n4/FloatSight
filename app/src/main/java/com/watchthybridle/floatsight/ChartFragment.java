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
import android.support.annotation.*;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.charts.LineChart;
import com.watchthybridle.floatsight.csvparser.FlySightTrackData;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public abstract class ChartFragment extends Fragment {

    private LineChart chart;

    @Retention(SOURCE)
    @IntDef({DATA_VALID, DATA_INVALID})
    public @interface DataValidForChart {}
    public static final int DATA_VALID = 1;
    public static final int DATA_INVALID = 0;

    public ChartFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FlySightTrackDataViewModel trackDataViewModel = ((MainActivity) getActivity()).getFlySightTrackDataViewModel();
        trackDataViewModel.getFlySightTrackDataLiveData()
                .observe(this, flySightTrackData -> showData(flySightTrackData));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chart, container, false);
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

    abstract protected void showData(FlySightTrackData flySightTrackData);

    public @DataValidForChart int isValidDataAndShowAlerts(FlySightTrackData flySightTrackData) {
        if (flySightTrackData.isAnyMetricEmpty()) {
            return DATA_INVALID;
        } else if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL) {
            showParsingErrorMessage(R.string.fail_parsing_file);
            return DATA_INVALID;
        } else if (flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_ERRORS) {
            showParsingErrorMessage(R.string.some_errors_parsing_file);
        }
        return DATA_VALID;
    }
}
