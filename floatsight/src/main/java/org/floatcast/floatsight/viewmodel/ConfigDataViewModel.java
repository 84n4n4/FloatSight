package org.floatcast.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import org.floatcast.floatsight.data.ConfigData;

import static org.floatcast.floatsight.data.ParsableData.PARSING_FAIL;

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

    @Override
    public boolean containsValidData() {
        return configData.getValue() != null
                && !configData.getValue().getSettings().isEmpty()
                && configData.getValue().getParsingStatus() != PARSING_FAIL;
    }
}
