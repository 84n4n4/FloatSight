package com.watchthybridle.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import com.watchthybridle.floatsight.data.ConfigSettingsData;

public class ConfigSettingsDataViewModel extends DataViewModel<ConfigSettingsData> {

    private MutableLiveData<ConfigSettingsData> settingsData;

    public ConfigSettingsDataViewModel() {
        settingsData = new MutableLiveData<>();
    }

    @Override
    public LiveData<ConfigSettingsData> getLiveData() {
        return settingsData;
    }

    @Override
    public MutableLiveData<ConfigSettingsData> getMutableLiveData() {
        return settingsData;
    }
}
