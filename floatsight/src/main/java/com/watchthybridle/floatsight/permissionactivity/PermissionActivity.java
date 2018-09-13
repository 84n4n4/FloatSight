package com.watchthybridle.floatsight.permissionactivity;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import com.watchthybridle.floatsight.R;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PermissionActivity extends AppCompatActivity {

    private List<PermissionStrategy> permissionStrategyList;

    public PermissionActivity() {
        permissionStrategyList = new ArrayList<>();
    }

    public void addToPermissionStrategyList(PermissionStrategy strategy) {
        permissionStrategyList.add(strategy);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        boolean accepted = false;
        if (grantResults.length > 1) {
            accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED;
        }

        for(PermissionStrategy strategy : new ArrayList<>(permissionStrategyList)) {
            if(strategy.getPermissionRequestId() == requestCode) {
                if (accepted) {
                    permissionStrategyList.remove(strategy);
                    strategy.execute(this);
                } else {
                    showPermissionRationale(strategy.getPermissionRequestId());
                }
            }
        }
    }

    private void showPermissionRationale(int permissionRequestId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                showAlertOKCancel(getResources().getString(R.string.permissions_rationale), (dialog, which) ->
                        requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, permissionRequestId)
                );
            }
        }
    }

    public void showAlertOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.ok, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
}
