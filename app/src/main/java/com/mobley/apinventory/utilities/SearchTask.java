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
import com.mobley.apinventory.sql.tables.Locations;

import java.util.Calendar;
import java.util.List;

public class SearchTask extends AsyncTask<String, Long, Void> {
    protected static final String TAG = SearchTask.class.getSimpleName();

    private Context mContext;
    private SqlDataSource mSqlDataSource;
    private ProgressDialog mProgressDlg;
    private APInventoryApp mApp;
    private String mCurrentType = "";

    public SearchTask(Context context, SqlDataSource src) {
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
            mProgressDlg.setMessage("Please wait");
            mProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDlg.show();
        }
    }

    @Override
    protected Void doInBackground(String... args) {
        if (LogConfig.ON) Log.d(TAG, "doInBackground()");

        mSqlDataSource.open();
        List<Locations> locations = mSqlDataSource.getLocationsByDesc(args[0]);
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
    }
}