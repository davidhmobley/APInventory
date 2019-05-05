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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_READ_WRITE_PERMISSION = 1;

    private TextView mAinTV, mCicTV, mCmrTV, mNumAssetsTV2, mNumLocationsTV2;
    private EditText mAinET, mCicET, mCmrET;
    private Button mImportButton, mViewAssetsButton, mViewLocationsButton;

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource;

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
        mAinET = findViewById(R.id.mainAINET);
        mCicET = findViewById(R.id.mainCICET);
        mCmrET = findViewById(R.id.mainCMRET);

        mImportButton = findViewById(R.id.mainImportButton);
        mImportButton.setOnClickListener(this);
        mViewAssetsButton = findViewById(R.id.mainViewAssetsButton);
        mViewAssetsButton.setOnClickListener(this);
        mViewLocationsButton = findViewById(R.id.mainViewLocationsButton);
        mViewLocationsButton.setOnClickListener(this);

        verifyPermissions(this);

        // TODO: insert some dummy data
        mSqlDataSource.open();
        mSqlDataSource.insertAssets("12345", "99999", "123", "444");
        mSqlDataSource.insertAssets("23456", "98988", "123", "444");
        mSqlDataSource.insertAssets("34567", "66666", "123", "444");
        mSqlDataSource.insertAssets("45678", "77777", "123", "444");
        mSqlDataSource.insertAssets("56789", "88888", "123", "444");
        mSqlDataSource.insertAssets("67890", "98976", "123", "444");
        mSqlDataSource.insertLocations("1", "Location1");
        mSqlDataSource.insertLocations("2", "Location2");
        mSqlDataSource.insertLocations("3", "Location3");
        mSqlDataSource.insertLocations("4", "Location4");
        mSqlDataSource.insertLocations("5", "Location5");
        mSqlDataSource.insertLocations("6", "Location6");
        mSqlDataSource.close();
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

        switch(item.getItemId()) {
            case R.id.action_settings:
                bOK = true; // processed

                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);

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
    protected void onResume() {
        super.onResume();
        if (LogConfig.ON) Log.d(TAG, "onResume()");

        mAinET.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_AIN_KEY, getString(R.string.default_ain)));
        mCicET.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CIC_KEY, getString(R.string.default_cic)));
        mCmrET.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CMR_KEY, getString(R.string.default_cmr)));

        checkDBCounts();
    }

    private void checkDBCounts() {
        if (LogConfig.ON) Log.d(TAG, "checkDBCounts()");

        mSqlDataSource.open();
        mNumAssetsTV2.setText(String.valueOf(mSqlDataSource.getNumAssets()));
        mNumLocationsTV2.setText(String.valueOf(mSqlDataSource.getNumLocations()));
        mSqlDataSource.close();
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

        if (view == mImportButton) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true)
                    .setTitle(getString(R.string.main_alert_verify_delete))
                    .setMessage(getString(R.string.main_alert_delete_msg))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int id) {
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

                            checkDBCounts();

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

        } else if (view == mViewAssetsButton) {
            Intent intent = new Intent(this, ViewAssetsActivity.class);
            startActivity(intent);
        } else if (view == mViewLocationsButton) {
            Intent intent = new Intent(this, ViewLocationsActivity.class);
            startActivity(intent);
        }
    }
}
