package com.mobley.apinventory.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.activities.MainActivity;
import com.mobley.apinventory.sql.SqlDataSource;

import java.util.Calendar;

public class ImportTask extends AsyncTask<Void, Long, Void> {
    protected static final String TAG = ImportTask.class.getSimpleName();

    private Context mContext;
    private SqlDataSource mSqlDataSource;
    private ProgressDialog mProgressDlg;
    private APInventoryApp mApp;
    private String mCurrentType = "";

    public ImportTask(Context context, SqlDataSource src) {
        mContext = context;
        mSqlDataSource = src;

        mApp = (APInventoryApp) mContext.getApplicationContext();

        Calendar cal = Calendar.getInstance();
        SharedPreferences.Editor editor = mApp.getAppPrefs().edit();
        editor.putLong(APInventoryApp.PREF_IMPORT_TIMESTAMP_KEY, cal.getTimeInMillis());
        editor.commit();
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
            mProgressDlg.setMessage(String.format(mApp.getString(R.string.import_progress_msg), mCurrentType, 0l));
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

        if (nAssetsDeleted == 0 && nLocationsDeleted == 0) {
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
        mSqlDataSource.beginTransaction();

        mCurrentType = "Assets";
        for (long i=0; i < 10000; i++) {
            mSqlDataSource.insertAssets(String.valueOf(i+1),
                                        String.valueOf(i+1),
                                        "Desc" + (i+1),
                                        "CIC" + (i+1),
                                        "CMR" + (i+1),
                                        0l,
                                        0l);
            if ((i%100) == 0) {
                publishProgress(i);
            }
        }

        mCurrentType = "Locations";
        for (long i=0; i < 10000; i++) {
            mSqlDataSource.insertLocations(String.valueOf(i+1), "Location"+(i+1));
            if ((i%50) == 0) {
                publishProgress(i);
            }
        }

        mSqlDataSource.commitTransaction();
        mSqlDataSource.endTransaction();
        mSqlDataSource.close();

        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (LogConfig.ON) Log.d(TAG, "onProgressUpdate()");

        mProgressDlg.setMessage(String.format(mApp.getString(R.string.import_progress_msg), mCurrentType, values[0]));
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (LogConfig.ON) Log.d(TAG, "onPostExecute()");

        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
        }

        ((MainActivity) mContext).checkDBCounts(true, false);
    }
}