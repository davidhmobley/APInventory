package com.mobley.apinventory.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.activities.ViewAssetsActivity;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;

import java.util.List;

public class SearchAssetsTask extends AsyncTask<String, Void, Void> {
    protected static final String TAG = SearchAssetsTask.class.getSimpleName();

    private Context mContext;
    private SqlDataSource mSqlDataSource;
    private List<Assets> mAssetList = null;
    private APInventoryApp mApp;

    public SearchAssetsTask(Context context, SqlDataSource src) {
        mContext = context;
        mSqlDataSource = src;

        mApp = (APInventoryApp) context.getApplicationContext();
    }

    @Override
    protected Void doInBackground(String... args) {
        if (LogConfig.ON) Log.d(TAG, "doInBackground()");

        mSqlDataSource.open();

        if (mApp.getViewAssetType() == ViewAssetsActivity.ALL_ASSETS) {
            mAssetList = mSqlDataSource.getAssetsByDesc(args[0]);
        } else {
            mAssetList = mSqlDataSource.getScannedAssetsByDesc(args[0]);
        }
        mSqlDataSource.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (LogConfig.ON) Log.d(TAG, "onPostExecute()");

        ((ViewAssetsActivity) mContext).setAssets(mAssetList);
    }
}