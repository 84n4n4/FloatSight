package org.floatcast.floatsight.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import org.floatcast.floatsight.data.FileImportData;

import static org.floatcast.floatsight.data.FileImportData.IMPORTING_ERRORS;

public class FileImportDataViewModel extends DataViewModel<FileImportData> {

    private final MutableLiveData<FileImportData> fileData;

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
                && fileData.getValue().getImportingStatus() != IMPORTING_ERRORS
                && fileData.getValue().getImportFolder().isDirectory();
    }
}
