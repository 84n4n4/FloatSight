package com.watchthybridle.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import com.watchthybridle.floatsight.data.FileImportData;

import static com.watchthybridle.floatsight.data.FileImportData.IMPORTING_ERRORS;

public class FileImportDataViewModel extends DataViewModel<FileImportData> {

    private MutableLiveData<FileImportData> fileData;

    public FileImportDataViewModel() {
        fileData = new MutableLiveData<>();
    }

    @Override
    public LiveData<FileImportData> getLiveData() {
        return fileData;
    }

    @Override
    public MutableLiveData<FileImportData> getMutableLiveData() {
        return fileData;
    }

    @Override
    public boolean containsValidData() {
        return fileData.getValue() != null
                && !fileData.getValue().getFiles().isEmpty()
                && fileData.getValue().getImportingStatus() != IMPORTING_ERRORS;
    }
}
