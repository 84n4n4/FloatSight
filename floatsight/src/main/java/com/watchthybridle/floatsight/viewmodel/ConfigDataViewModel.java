package com.watchthybridle.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.watchthybridle.floatsight.data.ConfigData;

public class ConfigDataViewModel extends DataViewModel<ConfigData> {

    private MutableLiveData<ConfigData> configData;

    public ConfigDataViewModel() {
        configData = new MutableLiveData<>();
    }

    @Override
    public LiveData<ConfigData> getLiveData() {
        return configData;
    }

    @Override
    public MutableLiveData<ConfigData> getMutableLiveData() {
        return configData;
    }

    public boolean containsValidData() {
        return configData.getValue() != null
                && !configData.getValue().getSettings().isEmpty()
                && configData.getValue().getParsingStatus() != ConfigData.PARSING_FAIL;
    }
}
