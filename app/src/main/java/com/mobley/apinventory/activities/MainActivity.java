package com.mobley.apinventory.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;
import com.mobley.apinventory.utilities.ExportCounts;
import com.mobley.apinventory.utilities.ExportTask;
import com.mobley.apinventory.utilities.ImportTask;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_READ_WRITE_PERMISSION = 1;

    private TextView mAinTV, mCicTV, mCmrTV, mNumAssetsTV2, mNumLocationsTV2;
    private TextView mAinTV2, mCicTV2, mCmrTV2;
    private Button mScanButton, mImportButton, mExportButton;

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource;
    private ImportTask mImportTask = null;
    private ExportTask mExportTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LogConfig.ON) Log.d(TAG, "onCreate()");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(getResources().getString(R.string.app_title));
        actionBar.setSubtitle(getResources().getString(R.string.main_subtitle));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mApp = (APInventoryApp) getApplication();
        mSqlDataSource = new SqlDataSource(this);

        setContentView(R.layout.activity_main);

        mAinTV = findViewById(R.id.mainAINTV);
        mCicTV = findViewById(R.id.mainCICTV);
        mCmrTV = findViewById(R.id.mainCMRTV);
        mNumAssetsTV2 = findViewById(R.id.mainAssetsTV2);
        mNumLocationsTV2 = findViewById(R.id.mainLocationsTV2);

        // input widgets
        mAinTV2 = findViewById(R.id.mainAINTV2);
        mCicTV2 = findViewById(R.id.mainCICTV2);
        mCmrTV2 = findViewById(R.id.mainCMRTV2);

        mScanButton = findViewById(R.id.mainScanButton);
        mScanButton.setOnClickListener(this);
        mImportButton = findViewById(R.id.mainImportButton);
        mImportButton.setOnClickListener(this);
        mExportButton = findViewById(R.id.mainExportButton);
        mExportButton.setOnClickListener(this);

        verifyPermissions(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean bOK = false;
        long count = 0;
        Intent intent;

        switch(item.getItemId()) {
            case R.id.action_settings:
                bOK = true; // processed

                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);

                break;
            case R.id.action_view_assets:
                bOK = true; // processed

                mSqlDataSource.open();
                count = mSqlDataSource.getNumAssets();
                mSqlDataSource.close();

                intent = new Intent(this, ViewAssetsActivity.class);
                intent.putExtra("Count", count);
                startActivity(intent);

                break;
            case R.id.action_view_locations:
                bOK = true; // processed

                mSqlDataSource.open();
                count = mSqlDataSource.getNumLocations();
                mSqlDataSource.close();

                intent = new Intent(this, ViewLocationsActivity.class);
                intent.putExtra("Count", count);
                startActivity(intent);

                break;
            case R.id.action_exit:
                bOK = true; // processed

                onBackPressed();

                break;
            default:
                bOK = super.onOptionsItemSelected(item);
                break;
        }

        return bOK;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LogConfig.ON) Log.d(TAG, "onPause()");

        if (mImportTask != null) {
            mImportTask.cancel(true);
            mImportTask = null; // reset
        }

        if (mExportTask != null) {
            mExportTask.cancel(true);
            mExportTask = null; // reset
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LogConfig.ON) Log.d(TAG, "onResume()");

        mAinTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_AIN_KEY, getString(R.string.default_ain)));
        mCicTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CIC_KEY, getString(R.string.default_cic)));
        mCmrTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CMR_KEY, getString(R.string.default_cmr)));

        checkDBCounts();
    }

    public void checkDBCounts() {
        if (LogConfig.ON) Log.d(TAG, "checkDBCounts()");

        mSqlDataSource.open();
        long count = mSqlDataSource.getNumAssets();
        mNumAssetsTV2.setText(String.valueOf(count));
        mNumLocationsTV2.setText(String.valueOf(mSqlDataSource.getNumLocations()));
        mSqlDataSource.close();

        if (count == 0) {
            mScanButton.setEnabled(false);
        } else {
            mScanButton.setEnabled(true);
        }
    }

    public void showExportCounts(ExportCounts exportCounts) {
        if (LogConfig.ON) Log.d(TAG, "showExportCounts()");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(getString(R.string.main_alert_export_title))
                .setMessage(String.format(getString(R.string.main_alert_export_msg),
                                            exportCounts.getModifiedAssets(),
                                            exportCounts.getExportedAssets()))
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel(); // get out!
                    }
                });

        AlertDialog confirm = builder.create();
        confirm.show();
    }

    private void verifyPermissions(Activity context) {
        if (LogConfig.ON) Log.d(TAG, "verifyPermissions()");

        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_READ_WRITE_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
        if (LogConfig.ON) Log.d(TAG, "onRequestPermissionsResult()");

        switch(requestCode) {
            case(REQUEST_READ_WRITE_PERMISSION):
                if (results.length > 0 && results[0] == PackageManager.PERMISSION_GRANTED) {
                    // got it!
                    mApp.setReadWriteGranted(true);
                } else {
                    mApp.setReadWriteGranted(false);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        if (LogConfig.ON) Log.d(TAG, "onClick()");

        if (view == mScanButton) {
            // TODO: do something
            mSqlDataSource.open();
            List<Assets> assets = mSqlDataSource.getAllAssets();
            mSqlDataSource.close();

            int i = 0;
            for (Assets asset : assets) {
                if ((i%100) == 0) {
                    mSqlDataSource.open();
                    mSqlDataSource.setModified(asset);
                    mSqlDataSource.close();
                }

                i++;
            }

        } else if (view == mImportButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true)
                    .setTitle(getString(R.string.main_alert_verify_delete))
                    .setMessage(getString(R.string.main_alert_delete_msg))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            mImportTask = new ImportTask(MainActivity.this, mSqlDataSource);
                            mImportTask.execute();

                            dialog.cancel(); // get out!
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.cancel(); // get out!
                        }
                    });

            AlertDialog confirm = builder.create();
            confirm.show();
        } else if (view == mExportButton) {
            mExportTask = new ExportTask(MainActivity.this, mSqlDataSource);
            mExportTask.execute();
        }
    }
}
