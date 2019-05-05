package com.mobley.apinventory.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.activities.MainActivity;
import com.mobley.apinventory.sql.SqlDataSource;

public class ImportTask extends AsyncTask<Void, Void, Void> {
    protected static final String TAG = ImportTask.class.getSimpleName();

    private Context mContext;
    private SqlDataSource mSqlDataSource;
    private ProgressDialog mProgressDlg;
    private APInventoryApp mApp;

    public ImportTask(Context context, SqlDataSource src) {
        mContext = context;
        mSqlDataSource = src;

        mApp = (APInventoryApp) mContext.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (LogConfig.ON) Log.d(TAG, "onPreExecute()");

        if (mProgressDlg == null || !mProgressDlg.isShowing()) {
            mProgressDlg = new ProgressDialog(mContext);
            mProgressDlg.setCancelable(true);
            mProgressDlg.setIndeterminate(true);
            mProgressDlg.setCanceledOnTouchOutside(false);
            mProgressDlg.setMessage(mApp.getString(R.string.progress_msg));
            mProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDlg.show();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (LogConfig.ON) Log.d(TAG, "doInBackground()");

        /****************************
         * delete Assets & Locations
         */
        int nAssetsDeleted = 0, nLocationsDeleted = 0;

        mSqlDataSource.open();
        mSqlDataSource.beginTransaction();

        nAssetsDeleted = mSqlDataSource.deleteAssets();
        nLocationsDeleted = mSqlDataSource.deleteLocations();

        if (nAssetsDeleted == 0 || nLocationsDeleted == 0) {
            // rollback
        } else {
            mSqlDataSource.commitTransaction();
        }
        mSqlDataSource.endTransaction();
        mSqlDataSource.close();

        // TODO: read in Assets & Locations

        // TODO: parse JSON Assets & Locations

        // TODO: insert some dummy data
        mSqlDataSource.open();
        mSqlDataSource.insertAssets("12345", "99999", "123", "444");
        mSqlDataSource.insertAssets("23456", "98988", "123", "444");
        mSqlDataSource.insertAssets("34567", "66666", "123", "444");
        mSqlDataSource.insertAssets("45678", "77777", "123", "444");
        mSqlDataSource.insertAssets("56789", "88888", "123", "444");
        mSqlDataSource.insertAssets("67890", "98976", "123", "444");
        for (int i=0; i < 1000; i++) {
            mSqlDataSource.insertLocations(String.valueOf(i+1), "Location"+(i+1));
        }
        mSqlDataSource.close();

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (LogConfig.ON) Log.d(TAG, "onPostExecute()");

        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
        }

        ((MainActivity) mContext).checkDBCounts();
    }
}