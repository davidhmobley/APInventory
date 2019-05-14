package com.mobley.apinventory;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class APInventoryApp extends Application {
    protected static final String TAG = APInventoryApp.class.getSimpleName();

    public static String PREF_VERSION_KEY;
    public static String PREF_AIN_KEY;
    public static String PREF_CIC_KEY;
    public static String PREF_CMR_KEY;
    public static final String YES = "Y";
    public static final String NO = "N";

    private SharedPreferences mAppPrefs = null;
    private boolean mReadWriteGranted = false;
    private int mViewAssetType; // All or Scanned

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

    public void mySnackbar(View view, String msg, boolean bLong) {
        if (LogConfig.ON) Log.d(TAG, "mySnackbar()");

        Snackbar snackbar;
        if (bLong) {
            snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_LONG);
        } else {
            snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT);
        }

        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        TextView tv1 = (TextView) (snackbar.getView()).findViewById(android.support.design.R.id.snackbar_text);
        tv1.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        snackbar.show();
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

    public int getViewAssetType() {
        return mViewAssetType;
    }

    public void setViewAssetType(int mViewAssetType) {
        this.mViewAssetType = mViewAssetType;
    }
}
