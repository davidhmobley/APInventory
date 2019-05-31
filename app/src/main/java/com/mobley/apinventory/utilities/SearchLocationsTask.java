package com.mobley.apinventory.utilities;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.activities.ViewLocationsActivity;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Locations;

import java.util.List;

public class SearchLocationsTask extends AsyncTask<String, Void, Void> {
    protected static final String TAG = SearchLocationsTask.class.getSimpleName();

    private Context mContext;
    private SqlDataSource mSqlDataSource;
    private List<Locations> mLocationList = null;

    public SearchLocationsTask(Context context, SqlDataSource src) {
        mContext = context;
        mSqlDataSource = src;
    }

    @Override
    protected Void doInBackground(String... args) {
        if (LogConfig.ON) Log.d(TAG, "doInBackground()");

        mSqlDataSource.open();
        mLocationList = mSqlDataSource.getLocationsByDesc(args[0]);
        mSqlDataSource.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (LogConfig.ON) Log.d(TAG, "onPostExecute()");

        ((ViewLocationsActivity) mContext).setLocations(mLocationList);
    }
}