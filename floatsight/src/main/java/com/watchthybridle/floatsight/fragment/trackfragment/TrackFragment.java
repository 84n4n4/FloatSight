package com.watchthybridle.floatsight.fragment.trackfragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.data.FlySightTrackData;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.CappedTrackPointValueProvider;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.TrackPointValueProvider;
import com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.XAxisValueProviderWrapper;
import com.watchthybridle.floatsight.viewmodel.FlySightTrackDataViewModel;

import static com.watchthybridle.floatsight.mpandroidchart.linedatasetcreation.ChartDataSetProperties.METRIC;

public abstract class TrackFragment extends Fragment {

    protected String unitSystem = METRIC;
    protected FlySightTrackDataViewModel trackDataViewModel;

    public abstract void onUnitsDialogCheckboxClicked(String unitSystem);

    abstract protected void actOnDataChanged(FlySightTrackData flySightTrackData);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        trackDataViewModel = ViewModelProviders.of(getActivity()).get(FlySightTrackDataViewModel.class);
        trackDataViewModel.getLiveData()
                .observe(this, flySightTrackData -> actOnDataChanged(flySightTrackData));
        super.onCreate(savedInstanceState);
    }

    public boolean isValid(FlySightTrackData flySightTrackData) {
        return !flySightTrackData.getFlySightTrackPoints().isEmpty()
                && !(flySightTrackData.getParsingStatus() == FlySightTrackData.PARSING_FAIL);
    }

    public void showUnitsDialog(String currentUnitSystem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.plot_units_dialog_title)
                .setPositiveButton(R.string.ok, null);
        final FrameLayout frameView = new FrameLayout(getContext());
        builder.setView(frameView);

        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = dialog.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.units_dialog, frameView);
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_metric)).setChecked(currentUnitSystem.equals(ChartDataSetProperties.METRIC));
        ((CheckBox) dialoglayout.findViewById(R.id.checkbox_imperial)).setChecked(currentUnitSystem.equals(ChartDataSetProperties.IMPERIAL));
        dialog.show();
    }

    public void onUnitsDialogCheckboxClicked(View view) {
        String unitSystem = ChartDataSetProperties.METRIC;
        boolean checked = ((CheckBox) view).isChecked();
        switch (view.getId()) {
            case R.id.checkbox_metric:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_imperial)).setChecked(!checked);
                if (checked) {
                    unitSystem = ChartDataSetProperties.METRIC;
                } else {
                    unitSystem = ChartDataSetProperties.IMPERIAL;
                }
                break;
            case R.id.checkbox_imperial:
                ((CheckBox) view.getRootView().findViewById(R.id.checkbox_metric)).setChecked(!checked);
                if (checked) {
                    unitSystem = ChartDataSetProperties.IMPERIAL;

                } else {
                    unitSystem = ChartDataSetProperties.METRIC;
                }
                break;
        }
        onUnitsDialogCheckboxClicked(unitSystem);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ChartDataSetProperties.UNIT_BUNDLE_KEY, unitSystem);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null) {
            unitSystem = savedInstanceState.getString(ChartDataSetProperties.UNIT_BUNDLE_KEY);
            unitSystem = unitSystem == null ? METRIC : unitSystem;
        }
    }
}
