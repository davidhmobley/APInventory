package com.mobley.apinventory.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;
import com.mobley.apinventory.sql.SqlDataSource;

public class MainActivity extends AppCompatActivity {
    protected static final String TAG = MainActivity.class.getSimpleName();

    public static final int REQUEST_READ_WRITE_PERMISSION = 1;

    private TextView mAinTV, mCicTV, mCmrTV, mNumAssetsTV2, mNumLocationsTV2;
    private EditText mAinET, mCicET, mCmrET;

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mAinET.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_AIN_KEY, getString(R.string.default_ain)));
        mCicET.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CIC_KEY, getString(R.string.default_cic)));
        mCmrET.setText(mApp.getAppPrefs().getString(APInventoryApp.PREF_CMR_KEY, getString(R.string.default_cmr)));

        mSqlDataSource.open();
        mNumAssetsTV2.setText(String.valueOf(mSqlDataSource.getNumAssets()));
        mNumLocationsTV2.setText(String.valueOf(mSqlDataSource.getNumLocations()));
        mSqlDataSource.close();
    }

    private void verifyPermissions(Activity context) {
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE },
                    REQUEST_READ_WRITE_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
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
}
