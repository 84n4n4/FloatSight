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

package org.floatcast.floatsight.fragment.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.floatcast.floatsight.ConfigActivity;
import org.floatcast.floatsight.MainActivity;
import org.floatcast.floatsight.R;
import org.floatcast.floatsight.TrackPickerActivity;
import org.floatcast.floatsight.fragment.ButtonAdapter;
import org.floatcast.floatsight.fragment.ButtonItem;
import org.floatcast.floatsight.fragment.Dialogs;
import org.floatcast.floatsight.recyclerview.DividerLineDecorator;

import java.util.ArrayList;
import java.util.List;

public class MainMenuFragment extends Fragment implements ButtonAdapter.ButtonItemClickListener {

    private static final int BUTTON_IMPORT = 0;
    private static final int BUTTON_LOAD = 1;
    private static final int BUTTON_ABOUT = 2;
    private static final int BUTTON_CONFIG = 3;

    public MainMenuFragment() {
        // intentional empty constructor for fragments
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
        mainMenuButtonList.add(new ButtonItem(BUTTON_ABOUT, R.string.button_about_title, R.string.button_about_description, R.drawable.info));
        //mainMenuButtonList.add(new ButtonItem(BUTTON_CONFIG, R.string.button_config_title, R.string.button_config_description, R.drawable.config));
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
