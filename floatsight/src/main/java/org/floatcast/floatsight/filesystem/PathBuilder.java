package org.floatcast.floatsight.filesystem;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;

public final class PathBuilder {

    public static final String FLOAT_SIGHT_FOLDER_NAME = "FloatSight";
    public static final String TRACKS_FOLDER_NAME = "tracks";
    public static final String EXAMPLE_TRACKS_FOLDER_NAME = "Example";
    public static final File ROOT_DIR = new File(Environment.getExternalStorageDirectory(), FLOAT_SIGHT_FOLDER_NAME);
    public static final File TRACKS_DIR = new File(ROOT_DIR, TRACKS_FOLDER_NAME);
    public static final File EXAMPLE_TRACKS_DIR = new File(TRACKS_DIR, EXAMPLE_TRACKS_FOLDER_NAME);

    private PathBuilder() {
    }

    public static File getTracksFolder() throws FileNotFoundException {
        return createFolder(TRACKS_DIR);
    }

    public static File getExampleTracksFolder() throws FileNotFoundException {
        return createFolder(EXAMPLE_TRACKS_DIR);
    }

    private static File createFolder(File folder) throws FileNotFoundException {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.exists() || !folder.isDirectory()) {
            throw new FileNotFoundException("Could not access folder on local storage");
        }
        return folder;
    }
}
