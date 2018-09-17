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

package org.floatcast.floatsight.permissionactivity;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public abstract class PermissionStrategy {

    private final int permissionRequestId;

    abstract public void task();

    protected PermissionStrategy(int permissionRequestId) {
        this.permissionRequestId = permissionRequestId;
    }

    public int getPermissionRequestId() {
        return permissionRequestId;
    }

    public void execute(PermissionActivity activity) {
        if (checkPermission(activity)) {
            task();
        } else {
            activity.addToPermissionStrategyList(this);
            ActivityCompat.requestPermissions(activity, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, permissionRequestId);
        }
    }

    private boolean checkPermission(PermissionActivity activity) {
        int result = ContextCompat.checkSelfPermission(activity, READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(activity, WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
