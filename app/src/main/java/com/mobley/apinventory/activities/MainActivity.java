package com.mobley.apinventory.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.dialogs.InfoDialog;
import com.mobley.apinventory.dialogs.LocationDialog;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;
import com.mobley.apinventory.utilities.ExportCounts;
import com.mobley.apinventory.utilities.ExportTask;
import com.mobley.apinventory.utilities.ImportTask;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_READ_WRITE_PERMISSION = 1;

    private TextView mNumAssetsTV2, mNumScannedAssetsTV2, mNumLocationsTV2;
    private TextView mAinTV2, mCicTV2, mCmrTV2, mLocationTV2, mMainImportTimestampTV;
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

        mAinTV2 = findViewById(R.id.mainAINTV2);
        mCicTV2 = findViewById(R.id.mainCICTV2);
        mCmrTV2 = findViewById(R.id.mainCMRTV2);
        mLocationTV2 = findViewById(R.id.mainLocationTV2);
        mNumAssetsTV2 = findViewById(R.id.mainNumAssetsTV2);
        mNumScannedAssetsTV2 = findViewById(R.id.mainNumScannedAssetsTV2);
        mNumLocationsTV2 = findViewById(R.id.mainNumLocationsTV2);
        mMainImportTimestampTV = findViewById(R.id.mainImportTimestampTV);

        mScanButton = findViewById(R.id.mainScanButton);
        mScanButton.setOnClickListener(this);
        mImportButton = findViewById(R.id.mainImportButton);
        mImportButton.setOnClickListener(this);
        mExportButton = findViewById(R.id.mainExportButton);
        mExportButton.setOnClickListener(this);

        String firstTime = mApp.getAppPrefs().getString(APInventoryApp.PREF_FIRST_TIME_KEY, APInventoryApp.YES);
        if (firstTime.equals(APInventoryApp.YES)) {
            // allow user to easily enter required shared preferences
            InfoDialog dlg = (InfoDialog) InfoDialog.newInstance();
            dlg.setContext(this);
            dlg.show(getSupportFragmentManager(), "Information");

            // turn off
            SharedPreferences.Editor editor = mApp.getAppPrefs().edit();
            editor.putString(APInventoryApp.PREF_FIRST_TIME_KEY, APInventoryApp.NO);
            editor.commit();
        }

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

                SharedPreferences.Editor editor = mApp.getAppPrefs().edit();
                String ver = null;
                try {
                    ver = "v" + getPackageManager().getPackageInfo(getPackageName(),0).versionName;
                } catch (PackageManager.NameNotFoundException e) {
                    ver = "???";
                    e.printStackTrace();
                }
                editor.putString(APInventoryApp.PREF_VERSION_KEY, ver);
                editor.commit();

                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);

                break;
            case R.id.action_view_assets:
                bOK = true; // processed

                mApp.setViewAssetType(ViewAssetsActivity.ALL_ASSETS);

                intent = new Intent(this, ViewAssetsActivity.class);
                startActivity(intent);

                break;
            case R.id.action_view_scanned_assets:
                bOK = true; // processed

                mApp.setViewAssetType(ViewAssetsActivity.ALL_SCANNED_ASSETS);

                intent = new Intent(this, ViewAssetsActivity.class);
                startActivity(intent);

                break;
            case R.id.action_view_locations:
                bOK = true; // processed

                intent = new Intent(this, ViewLocationsActivity.class);
                startActivity(intent);

                break;
            case R.id.action_delete_assets:
                bOK = true; // processed

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true)
                        .setTitle(getString(R.string.main_alert_verify_delete_assets))
                        .setMessage(getString(R.string.main_alert_delete_msg))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mSqlDataSource.open();
                                mSqlDataSource.deleteAssets();
                                mSqlDataSource.close();

                                checkDBCounts(false, true);

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

                break;
            case R.id.action_delete_locations:
                bOK = true; // processed

                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setCancelable(true)
                        .setTitle(getString(R.string.main_alert_verify_delete_locations))
                        .setMessage(getString(R.string.main_alert_delete_msg))
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                mSqlDataSource.open();
                                mSqlDataSource.deleteLocations();
                                mSqlDataSource.close();

                                checkDBCounts(false, true);

                                dialog.cancel(); // get out!
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel(); // get out!
                            }
                        });

                AlertDialog confirm2 = builder2.create();
                confirm2.show();

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

        setSharedPrefs();
        mLocationTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_LOCATION_KEY, getString(R.string.default_location)));

        checkDBCounts(false, false);
    }

    public void setSharedPrefs() {
        if (LogConfig.ON) Log.d(TAG, "setSharedPrefs()");

        mAinTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_AIN_KEY, getString(R.string.default_ain)));
        mCicTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CIC_KEY, getString(R.string.default_cic)));
        mCmrTV2.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CMR_KEY, getString(R.string.default_cmr)));
    }

    public void checkDBCounts(boolean bImportComplete, boolean bDeleteComplete) {
        if (LogConfig.ON) Log.d(TAG, "checkDBCounts()");

        mSqlDataSource.open();
        long countAssets = mSqlDataSource.getNumAssets();
        long countLocations = mSqlDataSource.getNumLocations();

        NumberFormat nf = NumberFormat.getInstance();

        mNumAssetsTV2.setText(String.valueOf(nf.format(countAssets)));
        mNumScannedAssetsTV2.setText(String.valueOf(nf.format(mSqlDataSource.getNumScannedAssets())));
        mNumLocationsTV2.setText(String.valueOf(nf.format(countLocations)));
        mSqlDataSource.close();

        if ((countAssets == 0l || countLocations == 0l) || mLocationTV2.getText().toString().equals("")) {
            mScanButton.setEnabled(false);
        } else if (countAssets == 0l || countLocations == 0l) {
            mScanButton.setEnabled(false);

            // reset import timestamp
            SharedPreferences.Editor editor = mApp.getAppPrefs().edit();
            editor.putLong(APInventoryApp.PREF_IMPORT_TIMESTAMP_KEY, 0l);
            editor.commit();
        } else {
            mScanButton.setEnabled(true);
        }

        setImportTimestamp(); // show when IMPORT occurred

        if (bImportComplete) {
            mApp.mySnackbar(getWindow().getDecorView().findViewById(android.R.id.content),
                    getString(R.string.main_import_complete_msg),
                    true);
        } else if (bDeleteComplete) {
            mApp.mySnackbar(getWindow().getDecorView().findViewById(android.R.id.content),
                    getString(R.string.main_delete_complete_msg),
                    true);
        }
    }

    private void setImportTimestamp() {
        if (LogConfig.ON) Log.d(TAG, "setImportTimestamp()");

        if (mApp.getAppPrefs().getLong(APInventoryApp.PREF_IMPORT_TIMESTAMP_KEY, 0l) == 0l) {
            mMainImportTimestampTV.setText("");
        } else {
            long ts = mApp.getAppPrefs().getLong(APInventoryApp.PREF_IMPORT_TIMESTAMP_KEY, 0l);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(ts);
            mMainImportTimestampTV.setText(
                    String.format(mApp.getString(R.string.timestamp_str2),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH),
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            cal.get(Calendar.SECOND)));
        }
    }

    public void showExportCounts(ExportCounts exportCounts) {
        if (LogConfig.ON) Log.d(TAG, "showExportCounts()");

        /***
        mApp.mySnackbar(getWindow().getDecorView().findViewById(android.R.id.content),
                "Error!!!!",
                true);
        ***/

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
            // Start Scanning
            Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);

        } else if (view == mImportButton) {
            mSqlDataSource.open();
            long aCount = mSqlDataSource.getNumAssets();
            long lCount = mSqlDataSource.getNumLocations();
            mSqlDataSource.close();

            if (aCount == 0 && lCount == 0) {
                mImportTask = new ImportTask(MainActivity.this, mSqlDataSource);
                mImportTask.execute();
            } else {
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
            }
        } else if (view == mExportButton) {
            mExportTask = new ExportTask(MainActivity.this, mSqlDataSource);
            mExportTask.execute();
        }
    }
}
