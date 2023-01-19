package com.neoniequell.locapartment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class UtilPermission {

    public static final byte STORAGE_RQ = 0;

    private UtilPermission() {

    }

    public static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_RQ);
    }

    public static boolean hasStoragePermission(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}
