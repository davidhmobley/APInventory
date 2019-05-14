package com.mobley.apinventory.utilities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.activities.MainActivity;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportTask extends AsyncTask<Void, Long, ExportCounts> {
    protected static final String TAG = ExportTask.class.getSimpleName();

    public static final String DIRECTORY = "apinv";

    private Context mContext;
    private SqlDataSource mSqlDataSource;
    private ProgressDialog mProgressDlg = null;
    private APInventoryApp mApp;
    private ExportCounts mExportCounts = null;

    public ExportTask(Context context, SqlDataSource src) {
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
            mProgressDlg.setMessage(String.format(mApp.getString(R.string.export_progress_msg), 0l));
            mProgressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDlg.show();
        }
    }

    @Override
    protected ExportCounts doInBackground(Void... voids) {
        if (LogConfig.ON) Log.d(TAG, "doInBackground()");

        long nAssetsExported = 0;
        List<Assets> modifiedAssets;

        mSqlDataSource.open();
        modifiedAssets = mSqlDataSource.getAllModifiedAssets();
        mSqlDataSource.close();

        if (modifiedAssets.size() > 0) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
            File dir = new File (path + DIRECTORY);
            dir.mkdirs();

            File file = new File(dir, "genesis.csv");
            try {
                FileWriter fw = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fw);

                for (Assets asset : modifiedAssets) {
                    writer.write(asset.getAssetNum());
                    writer.write(",");
                    writer.write(asset.getDescription());
                    writer.write(",");
                    writer.write(asset.getCIC());
                    writer.write(",");
                    writer.write(asset.getCMR());
                    writer.newLine();

                    nAssetsExported++;
                    if (nAssetsExported%50 == 0) {
                        publishProgress(nAssetsExported);
                    }
                }
                writer.flush();
                writer.close();

                fw.flush();
                fw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "******* File not found. Did you" +
                        " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mExportCounts = new ExportCounts(modifiedAssets.size(), nAssetsExported);

        return mExportCounts;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        super.onProgressUpdate(values);
        if (LogConfig.ON) Log.d(TAG, "onProgressUpdate()");

        mProgressDlg.setMessage(String.format(mApp.getString(R.string.export_progress_msg), values[0]));
    }

    @Override
    protected void onPostExecute(ExportCounts exportCounts) {
        super.onPostExecute(exportCounts);
        if (LogConfig.ON) Log.d(TAG, "onPostExecute()");

        if (mProgressDlg != null && mProgressDlg.isShowing()) {
            mProgressDlg.dismiss();
        }

        ((MainActivity) mContext).showExportCounts(exportCounts);
    }
}