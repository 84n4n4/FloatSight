package com.watchthybridle.floatsight.permissionactivity;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class PermissionStrategy {

    private int permissionRequestId;

    abstract public void task();

    protected PermissionStrategy(int permissionRequestId) {
        this.permissionRequestId = permissionRequestId;
    }

    public int getPermissionRequestId() {
        return permissionRequestId;
    }

    public void execute(PermissionActivity activity) {
        if (!checkPermission(activity)) {
            activity.addToPermissionStrategyList(this);
            ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, permissionRequestId);
        } else {
            task();
        }
    }

    private boolean checkPermission(PermissionActivity activity) {
        int result = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
