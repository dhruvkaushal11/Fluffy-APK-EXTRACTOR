package com.techiedhruv.apkextrator.model;

import android.graphics.drawable.Drawable;

/**
 * Created by dhurv on 05-01-2018.
 */

public class AppInfo {
    private String mAppName, mPackageName;
    private Drawable mAppIcon;

    public AppInfo(String mAppName, String mPackageName, Drawable mAppIcon) {
        this.mAppName = mAppName;
        this.mAppIcon = mAppIcon;
        this.mPackageName = mPackageName;
    }

    public Drawable getmAppIcon() {
        return mAppIcon;
    }

    public void setmAppIcon(Drawable mAppIcon) {
        this.mAppIcon = mAppIcon;
    }

    public String getmAppName() {
        return mAppName;


    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getmPackageName() {
        return mPackageName;
    }

    public void setmPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }
}
