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

package org.floatcast.floatsight.fragment.configeditor;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.floatcast.floatsight.R;
import org.floatcast.floatsight.configparser.ConfigItem;
import org.floatcast.floatsight.data.ConfigData;
import org.floatcast.floatsight.viewmodel.ConfigDataViewModel;

import java.util.ArrayList;
import java.util.List;

public class ConfigEditorFragment extends Fragment {

    private ConfigAdapter adapter;


    public ConfigEditorFragment() {
        // intentional empty constructor for fragments
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ViewModelProviders.of(getActivity())
                .get(ConfigDataViewModel.class)
                .getLiveData().observe(this, this::actOnDataChanged);
    }

    private void actOnDataChanged(ConfigData data) {
        if (adapter != null) {
            adapter.setData(data.getSettings());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ConfigAdapter(getSettings());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(adapter);
    }

    private List<ConfigItem> getSettings() {
        List<ConfigItem> settings = new ArrayList<>();

        settings.add(new ConfigItem("Model", 6,
                "Dynamic model",
                "0 = Portable",
                "2 = Stationary",
                "3 = Pedestrian",
                "4 = Automotive",
                "5 = Sea",
                "6 = Airborne with < 1 G acceleration",
                "7 = Airborne with < 2 G acceleration",
                "8 = Airborne with < 4 G acceleration"));

        settings.add(new ConfigItem("Min", 0,
                "Lowest pitch value",
                "cm/s        in Mode 0, 1, or 4",
                "ratio * 100 in Mode 2 or 3"));

        return settings;
    }
}
