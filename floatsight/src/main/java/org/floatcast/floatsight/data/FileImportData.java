package org.floatcast.floatsight.data;

import android.support.annotation.LongDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.nio.file.Files;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class FileImportData {

    @Retention(SOURCE)
    @LongDef({IMPORTING_SUCCESS, IMPORTING_FAIL, IMPORTING_ERRORS})
    public @interface ImportingResult {}
    public static final long IMPORTING_SUCCESS = 0;
    public static final long IMPORTING_FAIL = -1;
    public static final long IMPORTING_ERRORS = 1;

    private long importingStatus = IMPORTING_SUCCESS;
    private List<Files> files;
    private File importFolder;

    @ImportingResult
    public long getImportingStatus() {
        return importingStatus;
    }

    public void setImportingStatus(@ImportingResult long importingStatus) {
        this.importingStatus = importingStatus;
    }

    public List<Files> getFiles() {
        return files;
    }

    public File getImportFolder() {
        return importFolder;
    }

    public void setImportFolder(File importFolder) {
        this.importFolder = importFolder;
    }
}
