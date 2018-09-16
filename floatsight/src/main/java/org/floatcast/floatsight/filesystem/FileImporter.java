package org.floatcast.floatsight.filesystem;

import android.arch.lifecycle.MutableLiveData;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import org.apache.commons.lang3.time.DatePrinter;
import org.apache.commons.lang3.time.FastDateFormat;
import org.floatcast.floatsight.data.FileImportData;
import org.floatcast.floatsight.viewmodel.DataViewModel;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;
import static android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE;
import static org.floatcast.floatsight.data.FileImportData.*;

public class FileImporter {

    private static final DatePrinter DATE_PRINTER = FastDateFormat.getInstance("yyyy-MM-dd'T'HH_mm_ss");
    private ContentResolver contentResolver;

    public FileImporter(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public void importTracks(List<Uri> urisList, MutableLiveData<FileImportData> importData) {
        Uri[] urisArray = new Uri[urisList.size()];
        urisList.toArray(urisArray);
        new ImportFilesTask(importData).execute(urisArray);
    }

    private class ImportFilesTask extends AsyncTask<Uri, Integer, Long> {

        private FileImportData data = new FileImportData();
        private MutableLiveData<FileImportData> liveData;

        ImportFilesTask(MutableLiveData<FileImportData> liveData) {
            this.liveData = liveData;
        }

        @Override
        protected Long doInBackground(Uri... uris) {
            Process.setThreadPriority(THREAD_PRIORITY_BACKGROUND + THREAD_PRIORITY_MORE_FAVORABLE);
            data.setImportingStatus(IMPORTING_SUCCESS);
            File directory;

            try {
                directory = createImportFolder();
                data.setImportFolder(directory);
                List<File> files = new ArrayList<>();
                for (Uri uri : uris) {
                    String fileName = resolveFileName(uri);
                    try {
                        files.add(importTrack(contentResolver, fileName, directory, uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (uris.length == files.size()) {
                    data.setImportingStatus(IMPORTING_SUCCESS);
                } else if (uris.length > 0 && files.size() > 0) {
                    data.setImportingStatus(IMPORTING_ERRORS);
                } else {
                    data.setImportingStatus(IMPORTING_FAIL);
                }
            } catch (FileNotFoundException e) {
                data.setImportingStatus(IMPORTING_FAIL);
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            liveData.setValue(data);
        }
    }

    public static File createImportFolder() throws FileNotFoundException {
        File directory = new File(PathBuilder.getTracksFolder(), DATE_PRINTER.format(Calendar.getInstance().getTime()));

        if (!directory.exists()) {
            directory.mkdirs();
        }

        if (!directory.exists() || !directory.isDirectory()) {
            throw new FileNotFoundException("Could not access folder on local storage");
        }
        return directory;
    }

    public static File importTrack(ContentResolver contentResolver, String fileName, File directory, Uri uri) throws IOException {
        File outFile = new File(directory, fileName);

        byte[] buffer = new byte[8 * 1024];
        int length;
        try (InputStream inputStream = contentResolver.openInputStream(uri); OutputStream outputStream = new FileOutputStream(outFile)) {
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        }
        return outFile;
    }

    private String resolveFileName(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = contentResolver.query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }

        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }

        return result;
    }
}
