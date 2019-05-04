package com.mobley.apinventory;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

public class APInventoryApp extends Application {
    protected static final String TAG = APInventoryApp.class.getSimpleName();

    public static String PREF_VERSION_KEY;
    public static String PREF_AIN_KEY;
    public static String PREF_CIC_KEY;
    public static String PREF_CMR_KEY;

    private SharedPreferences mAppPrefs = null;
    private boolean mReadWriteGranted = false;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        PREF_VERSION_KEY = getResources().getString(R.string.pref_version_key);
        if (!mAppPrefs.contains(PREF_VERSION_KEY)) {
            SharedPreferences.Editor editor = mAppPrefs.edit();

            String ver = null;
            try {
                ver = "v" + getPackageManager().getPackageInfo(getPackageName(),0).versionName;
            } catch (PackageManager.NameNotFoundException e) {
                ver = "???";
                e.printStackTrace();
            }

            editor.putString(PREF_VERSION_KEY, ver);
            editor.commit();
        }

        PREF_AIN_KEY = getResources().getString(R.string.pref_ain_key);
        if (!mAppPrefs.contains(PREF_AIN_KEY)) {
            SharedPreferences.Editor editor = mAppPrefs.edit();
            editor.putString(PREF_AIN_KEY, getString(R.string.default_ain));
            editor.commit();
        }

        PREF_CIC_KEY = getResources().getString(R.string.pref_cic_key);
        if (!mAppPrefs.contains(PREF_CIC_KEY)) {
            SharedPreferences.Editor editor = mAppPrefs.edit();
            editor.putString(PREF_CIC_KEY, getString(R.string.default_cic));
            editor.commit();
        }

        PREF_CMR_KEY = getResources().getString(R.string.pref_cmr_key);
        if (!mAppPrefs.contains(PREF_CMR_KEY)) {
            SharedPreferences.Editor editor = mAppPrefs.edit();
            editor.putString(PREF_CMR_KEY, getString(R.string.default_cmr));
            editor.commit();
        }
    }

    public SharedPreferences getAppPrefs() {
        return mAppPrefs;
    }

    public boolean isReadWriteGranted() {
        return mReadWriteGranted;
    }

    public void setReadWriteGranted(boolean rw) {
        mReadWriteGranted = rw;
    }
}
