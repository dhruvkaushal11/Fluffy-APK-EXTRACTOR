package com.techiedhruv.apkextrator.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.techiedhruv.apkextrator.Constants;
import com.techiedhruv.apkextrator.R;
import com.techiedhruv.apkextrator.adapter.RecyclerAdapterApps;
import com.techiedhruv.apkextrator.interfaceCallBack.ExtractCallBack;
import com.techiedhruv.apkextrator.model.AppInfo;
import com.techiedhruv.apkextrator.util.APKBackup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ExtractCallBack {
    private static final String TAG = "MainActivity";
    private List<AppInfo> mAppList = new ArrayList<>();
    private String packageNamePending = "";
    private SharedPreferences pref;
    private RecyclerView mAppRecycler;
    private RecyclerAdapterApps mRecyclerAdapterApps;
    private ImageView mAppFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        mAppRecycler = findViewById(R.id.app_recycler);
        mAppFolder = findViewById(R.id.folder_apps);
        mAppFolder.setOnClickListener(this);
        setUpRecyclerView();
    }

    private void setUpRecyclerView() {
        mAppRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAdapterApps = new RecyclerAdapterApps(this, mAppList);
        mAppRecycler.setAdapter(mRecyclerAdapterApps);
        new AsyncCaller().execute();

    }

    private void listApps() {
        List<PackageInfo> packList = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            if ((packInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packInfo.applicationInfo.loadLabel(getPackageManager()).toString();
                String appPackage = packInfo.packageName;
                Drawable appIcon = packInfo.applicationInfo.loadIcon(getPackageManager());

                mAppList.add(new AppInfo(appName, appPackage, appIcon));
            }
        }
        Collections.sort(mAppList, new Comparator<AppInfo>() {
            public int compare(AppInfo obj1, AppInfo obj2) {
                // ## Ascending order
                return obj1.getmAppName().compareToIgnoreCase(obj2.getmAppName()); // To compare string values
                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                // ## Descending order
                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
            }
        });

    }

    void showTutorial() {
        if (pref.getBoolean(Constants.SEEN_TUTORIAL, false)) {
            return;
        }
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(mAppFolder, "Find your extracted apk's here", "Alternatively your apps are located in APK_EXTRACTOR folder on your phone!")
                        // All options below are optional
                        .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.colorWhite)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.colorWhite)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.colorWhiteLight)  // Specify the color of the description text
                        .textColor(R.color.colorWhite)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.colorBlack)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                        openFolder();
                    }
                });
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.SEEN_TUTORIAL, true);
        editor.apply(); // commit changes
    }

    private void openFolder() {
        Uri selectedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/APK_EXTRACTOR/");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(selectedUri, "resource/folder");

        if (intent.resolveActivityInfo(getPackageManager(), 0) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Goto APK_EXTRACTOR folder on your phone!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.folder_apps:
                openFolder();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            new APKBackup(this).extractapk(packageNamePending);
            showTutorial();

        }
    }

    @Override
    public void extract(String packageName, String AppName) {
        packageNamePending = packageName;
        if (isStoragePermissionGranted()) {
            new APKBackup(this).extractapk(packageName);
            showTutorial();

        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    public void rateUs(View v) {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }

    }

    public void shareApp(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this app at: https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }
    public void donate(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jiitconnect.com/pcconnect/paypal1.html"));
        startActivity(browserIntent);

    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            //this method will be running on background thread so don't update UI frome here
            //do your long running http tasks here,you dont want to pass argument and u can access the parent class' variable url over here
            listApps();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //this method will be running on UI thread
            mRecyclerAdapterApps.notifyDataSetChanged();

            pdLoading.dismiss();
        }

    }
}

