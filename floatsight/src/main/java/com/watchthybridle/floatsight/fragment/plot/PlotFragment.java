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

package com.watchthybridle.floatsight.fragment.plot;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.*;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import com.github.mikephil.charting.components.LimitLine;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.mpandroidchart.customcharts.GlideOverlayChart;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.*;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;

import java.util.List;

import static com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetHolder.*;
import static com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties.METRIC;

public class PlotFragment extends Fragment {

    private GlideOverlayChart chart;
    private XAxisValueProviderWrapper xAxisValueProviderWrapper;
    private CappedTrackPointValueProvider cappedGlideValueProvider;
    private FlySightTrackDataViewModel trackDataViewModel;
    private String unitSystem = METRIC;

    public PlotFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        xAxisValueProviderWrapper = new XAxisValueProviderWrapper(XAxisValueProviderWrapper.TIME, unitSystem);
        cappedGlideValueProvider = new CappedTrackPointValueProvider(TrackPointValueProvider.GLIDE_VALUE_PROVIDER, 5f);
        trackDataViewModel = ViewModelProviders.of(getActivity()).get(FlySightTrackDataViewModel.class);

        trackDataViewModel.getLiveData()
                .observe(this, flySightTrackData -> actOnDataChanged(flySightTrackData));
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_plot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FrameLayout frameLayout = view.findViewById(R.id.root_chart_view);

        chart = new GlideOverlayChart(getContext());

        frameLayout.addView(chart.glideChart);
        frameLayout.addView(chart);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("");
    }

    public void actOnDataChanged(FlySightTrackData flySightTrackData) {
        if(getActivity() != null) {
            getActivity().invalidateOptionsMenu();
        }

        if (!isValid(flySightTrackData)) {
            return;
        }
        ChartDataSetHolder chartDataSetHolder =
                new ChartDataSetHolder(getContext(), flySightTrackData, xAxisValueProviderWrapper.xAxisValueProvider,
                        cappedGlideValueProvider, unitSystem);

        chart.setDataSetHolder(chartDataSetHolder);
        chart.invalidate();
    }

    public boolean isValid(FlySightTrackData flySightTrackData) {
        return !flySightTrackData.getFlySightTrackPoints().isEmpty()
                && !(flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL);
    }

    private void triggerOnDataChanged() {
        if(chart != null && chart.getDataSetHolder() != null) {
            actOnDataChanged(chart.getDataSetHolder().getFlySightTrackData());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.plot_fragment_menu, menu);
        boolean enabled = chart.getDataSetHolder() != null &&
                isValid(chart.getDataSetHolder().getFlySightTrackData());
        menu.findItem(R.id.menu_item_y_axis).setEnabled(enabled);
        menu.findItem(R.id.menu_item_x_axis).setEnabled(enabled);
        menu.findItem(R.id.menu_item_cap_glide).setEnabled(enabled);
        menu.findItem(R.id.menu_item_units).setEnabled(enabled);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_y_axis:
                showYAxisDialog();
                return true;
            case R.id.menu_item_x_axis:
                showXAxisDialog();
                return true;
            case R.id.menu_item_cap_glide:
                PlotFragmentDialogs.showGlideCapDialog(this, cappedGlideValueProvider);
                return true;
            case R.id.menu_item_units:
                showUnitsDialog();
                return true;
            case R.id.menu_item_set_range_marker:
                chart.setRangeMarker();
                return true;
            case R.id.menu_item_crop_range:
                crop();
                return true;
            case R.id.menu_item_discard_markers:
                chart.clearRangeMarkers();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void crop() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.crop_plot_dialog_title)
                .setMessage(R.string.crop_plot_dialog_description)
                .setPositiveButton(R.string.crop_plot_dialog_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<LimitLine> limitLines = chart.getXAxis().getLimitLines();
                        if(limitLines.size() == 2) {
                            trackDataViewModel.crop(limitLines.get(0).getLimit(),
                                    limitLines.get(1).getLimit(),
                                    xAxisValueProviderWrapper.xAxisValueProvider);
                            
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void showYAxisDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.y_axis_dialog_title)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(getContext());
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.y_axis_dialog, frameView);
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_altitude)).setChecked(chart.getDataSetHolder().getDataSetPropertiesList().get(ALTITUDE).iLineDataSet.isVisible());
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_glide)).setChecked(chart.glideChart.getData().getDataSets().get(0).isVisible());
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_hor_velocity)).setChecked(chart.getDataSetHolder().getDataSetPropertiesList().get(HOR_VELOCITY).iLineDataSet.isVisible());
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_vert_velocity)).setChecked(chart.getDataSetHolder().getDataSetPropertiesList().get(VERT_VELOCITY).iLineDataSet.isVisible());
        dialog.show();
    }

    public void onYAxisDialogCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_altitude:
                chart.getDataSetHolder().getDataSetPropertiesList().get(ALTITUDE).iLineDataSet.setVisible(checked);
                break;
            case R.id.checkbox_glide:
                chart.glideChart.getData().getDataSets().get(0).setVisible(checked);
                break;
            case R.id.checkbox_hor_velocity:
                chart.getDataSetHolder().getDataSetPropertiesList().get(HOR_VELOCITY).iLineDataSet.setVisible(checked);
                break;
            case R.id.checkbox_vert_velocity:
                chart.getDataSetHolder().getDataSetPropertiesList().get(VERT_VELOCITY).iLineDataSet.setVisible(checked);
                break;
        }
        chart.invalidate();
    }

    private void showXAxisDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.x_axis_dialog_title)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(getContext());
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.x_axis_dialog, frameView);
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_distance)).setChecked(xAxisValueProviderWrapper.isDistance());
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_time)).setChecked(xAxisValueProviderWrapper.isTime());
        dialog.show();
    }

    public void onXAxisDialogCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_time:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_distance)).setChecked(!checked);
                if(checked) {
                    xAxisValueProviderWrapper.setTime();
                } else {
                    xAxisValueProviderWrapper.setDistance();
                }
                break;
            case R.id.checkbox_distance:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_time)).setChecked(!checked);
                if(checked) {
                    xAxisValueProviderWrapper.setDistance();
                } else {
                    xAxisValueProviderWrapper.setTime();
                }
                break;
        }
        chart.resetUserChanges();
        triggerOnDataChanged();
    }

    private void showUnitsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.plot_units_dialog_title)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(getContext());
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.units_dialog, frameView);
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_metric)).setChecked(unitSystem.equals(ChartDataSetProperties.METRIC));
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_imperial)).setChecked(unitSystem.equals(ChartDataSetProperties.IMPERIAL));
        dialog.show();
    }

    public void onUnitsDialogCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox_metric:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_imperial)).setChecked(!checked);
                if(checked) {
                    unitSystem = ChartDataSetProperties.METRIC;
                } else {
                    unitSystem = ChartDataSetProperties.IMPERIAL;
                }
                break;
            case R.id.checkbox_imperial:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_metric)).setChecked(!checked);
                if(checked) {
                    unitSystem = ChartDataSetProperties.IMPERIAL;

                } else {
                    unitSystem = ChartDataSetProperties.METRIC;
                }
                break;
        }
        xAxisValueProviderWrapper.setUnitSystem(unitSystem);
        chart.resetUserChanges();
        triggerOnDataChanged();
    }

    public void setNewGlideCapValue(float glideCapValue) {
        cappedGlideValueProvider = new CappedTrackPointValueProvider(TrackPointValueProvider.GLIDE_VALUE_PROVIDER, glideCapValue);
        chart.resetUserChanges();
        triggerOnDataChanged();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        chart.saveState(outState);
        chart.glideChart.saveState(outState);
        outState.putString(XAxisValueProviderWrapper.BUNDLE_KEY, xAxisValueProviderWrapper.getStringValue());
        outState.putFloat(CappedTrackPointValueProvider.BUNDLE_KEY, cappedGlideValueProvider.getCapYValueAt());
        outState.putString(ChartDataSetProperties.UNIT_BUNDLE_KEY, unitSystem);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        chart.restoreState(savedInstanceState);
        chart.glideChart.restoreState(savedInstanceState);
        if(savedInstanceState != null) {
            unitSystem = savedInstanceState.getString(ChartDataSetProperties.UNIT_BUNDLE_KEY);
            unitSystem = unitSystem == null ? METRIC : unitSystem;
            xAxisValueProviderWrapper = new XAxisValueProviderWrapper(
                    savedInstanceState.getString(XAxisValueProviderWrapper.BUNDLE_KEY), unitSystem);
            cappedGlideValueProvider = new CappedTrackPointValueProvider(TrackPointValueProvider.GLIDE_VALUE_PROVIDER,
                    savedInstanceState.getFloat(CappedTrackPointValueProvider.BUNDLE_KEY));
        }
    }

    @VisibleForTesting
    public GlideOverlayChart getChart() {
        return chart;
    }
}
