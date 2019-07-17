package com.techiedhruv.apkextrator.util;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.techiedhruv.apkextrator.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dhurv on 05-01-2018.
 */


public class APKBackup {

    String TAG = "APK_BACKUP_ACTIVITY";
    Context context;


    public APKBackup(Context context) {
        this.context = context;
        File sdcard = Environment.getExternalStorageDirectory();
        File f = new File(sdcard + Constants.directory);
        f.mkdir();

    }


    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void extractapk(final String packagename) {
        Log.d(TAG, "APK BACKUP STARTED FOR APP" + packagename);
        PackageManager packageManager = context.getPackageManager();
        try {
            File file = new File(packageManager.getApplicationInfo(packagename, PackageManager.GET_META_DATA).publicSourceDir);
            File output = new File(Environment.getExternalStorageDirectory().getPath() + Constants.directory + packagename + ".apk");
            try {
                output.createNewFile();
                FileOutputStream fos;
                try {

                    InputStream assetFile = new FileInputStream(file);
                    fos = new FileOutputStream(output);
                    copyFile(assetFile, fos);
                    fos.close();
                    assetFile.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "APK EXTRACTED!", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(context)
                .setTitle("APK Extracted")
                .setMessage("You can find APK File in APK_EXTRACTOR directory of your file manager!")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.checkbox_on_background)
                .show();
    }


}