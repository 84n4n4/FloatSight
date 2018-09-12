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

package com.watchthybridle.floatsight.fragment.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.watchthybridle.floatsight.ConfigActivity;
import com.watchthybridle.floatsight.MainActivity;
import com.watchthybridle.floatsight.R;
import com.watchthybridle.floatsight.TrackPickerActivity;
import com.watchthybridle.floatsight.fragment.ButtonAdapter;
import com.watchthybridle.floatsight.fragment.ButtonItem;
import com.watchthybridle.floatsight.fragment.Dialogs;
import com.watchthybridle.floatsight.recyclerview.DividerLineDecorator;

import java.util.ArrayList;
import java.util.List;

public class MainMenuFragment extends Fragment implements ButtonAdapter.ButtonItemClickListener {

    private static final int BUTTON_IMPORT = 0;
    private static final int BUTTON_LOAD = 1;
    private static final int BUTTON_CONFIG = 2;
    private static final int BUTTON_ABOUT = 3;

    public MainMenuFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rv_button_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.button_list_view);
        recyclerView.setHasFixedSize(true);
        ButtonAdapter buttonAdapter = new ButtonAdapter(getButtons());
        buttonAdapter.setButtonItemClickListener(this);
        recyclerView.setAdapter(buttonAdapter);
        recyclerView.addItemDecoration(new DividerLineDecorator(view.getContext()));
    }

    private List<ButtonItem> getButtons() {
        List<ButtonItem> mainMenuButtonList = new ArrayList<>();
        mainMenuButtonList.add(new ButtonItem(BUTTON_IMPORT, R.string.button_import_title, R.string.button_import_description, R.drawable.import_grey));
        mainMenuButtonList.add(new ButtonItem(BUTTON_LOAD, R.string.button_load_title, R.string.button_load_description, R.drawable.folder));
        mainMenuButtonList.add(new ButtonItem(BUTTON_CONFIG, R.string.button_config_title, R.string.button_config_description, R.drawable.config));
        mainMenuButtonList.add(new ButtonItem(BUTTON_ABOUT, R.string.button_about_title, R.string.button_about_description, R.drawable.info));
        return mainMenuButtonList;
    }

    @Override
    public void onItemClick(ButtonItem buttonItem) {
        if (buttonItem.isEnabled) {
            switch (buttonItem.id) {
                case BUTTON_IMPORT:
                    startImportFile();
                    break;
                case BUTTON_LOAD:
                    Intent showTrackIntent = new Intent(getActivity(), TrackPickerActivity.class);
                    startActivity(showTrackIntent);
                    break;
                case BUTTON_CONFIG:
                    Intent configEditorIntent = new Intent(getActivity(), ConfigActivity.class);
                    startActivity(configEditorIntent);
                    break;
                case BUTTON_ABOUT:
                    Dialogs.showAboutDialog(getContext());
                    break;
                default:
                    break;
            }
        }
    }

    private void startImportFile() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.startImportFile();
        }
    }
}
