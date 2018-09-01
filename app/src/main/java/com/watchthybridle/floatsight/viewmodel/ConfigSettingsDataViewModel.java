package com.watchthybridle.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.watchthybridle.floatsight.data.ConfigSettingsData;
import com.watchthybridle.floatsight.data.FlySightTrackData;

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

    public boolean containsValidData() {
        return settingsData.getValue() != null
                && !settingsData.getValue().getSettings().isEmpty()
                && settingsData.getValue().getParsingStatus() != ConfigSettingsData.PARSING_FAIL;
    }
}
