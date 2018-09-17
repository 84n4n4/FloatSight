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

package org.floatcast.floatsight.data;

import android.support.annotation.LongDef;

import java.io.File;
import java.lang.annotation.Retention;
import java.nio.file.Files;
import java.util.List;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@SuppressWarnings("PMD.FieldDeclarationsShouldBeAtStartOfClass")
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
