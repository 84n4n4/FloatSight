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

package org.floatcast.floatsight.datarepository;

import android.arch.lifecycle.MutableLiveData;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;

import org.floatcast.floatsight.Parser;
import org.floatcast.floatsight.data.ParsableData;
import org.floatcast.floatsight.data.SaveFileUriHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static org.floatcast.floatsight.viewmodel.SaveFileDataViewModel.STATUS_SAVE_READ_ERROR;
import static org.floatcast.floatsight.viewmodel.SaveFileDataViewModel.STATUS_SAVE_SUCCESS;
import static org.floatcast.floatsight.viewmodel.SaveFileDataViewModel.STATUS_SAVE_WRITE_ERROR;

public class SaveTrackAsyncTask<T extends ParsableData> extends AsyncTask<String, Integer, Long> {

    private final T data;
    private final Parser<T> parser;
    private final File importFolder;
    private final MutableLiveData<SaveFileUriHolder> mutableLiveData;
    private final SaveFileUriHolder saveFileUriHolder;

    public SaveTrackAsyncTask(Parser<T> parser, T parsableData,
                       MutableLiveData<SaveFileUriHolder> mutableLiveData,
                       File importFolder) {
        data = parsableData;
        this.parser = parser;
        this.importFolder = importFolder;
        this.mutableLiveData = mutableLiveData;
        saveFileUriHolder = new SaveFileUriHolder();
    }

    @Override
    protected Long doInBackground(String... fileName) {
        Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);

        if (fileName.length > 0) {
            try {
                saveFileUriHolder.status = STATUS_SAVE_SUCCESS;
                File saveFile = new File(importFolder, fileName[0]);
                saveFileUriHolder.uri = Uri.fromFile(saveFile);
                parser.write(new FileOutputStream(saveFile), data);
            } catch (FileNotFoundException e) {
                saveFileUriHolder.status = STATUS_SAVE_WRITE_ERROR;
            } catch (IOException e) {
                saveFileUriHolder.status = STATUS_SAVE_READ_ERROR;
            }
        } else {
            saveFileUriHolder.status = STATUS_SAVE_WRITE_ERROR;
        }
        return 0L;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        mutableLiveData.setValue(saveFileUriHolder);
    }
}
