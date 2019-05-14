package com.mobley.apinventory.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.adapters.CustomViewAssetsAdapter;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;

import java.util.List;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {
    protected static final String TAG = ScanActivity.class.getSimpleName();

    private Button mGoButton;

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource = null;
    private List<Assets> mAssets = null;
    private TextView mScanAssetTV, mScanAssetDescTV2;
    private EditText mScanAssetET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LogConfig.ON) Log.d(TAG, "onCreate()");

        mApp = (APInventoryApp) getApplication();
        mSqlDataSource = new SqlDataSource(this);
        setContentView(R.layout.activity_scan);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(getResources().getString(R.string.app_title));
        actionBar.setSubtitle(getString(R.string.view_scan_subtitle));
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mScanAssetTV = findViewById(R.id.scanAssetTV);
        mScanAssetET = findViewById(R.id.scanAssetET);
        mScanAssetET.requestFocus();
        mScanAssetDescTV2 = findViewById(R.id.scanAssetDescTV2);
        mGoButton = findViewById(R.id.scanAssetButton);
        mGoButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean bOK = false;

        switch(item.getItemId()) {
            case R.id.action_return:
            case android.R.id.home:
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
    public void onClick(View view) {
        if (LogConfig.ON) Log.d(TAG, "onClick()");

        boolean bGood = true;

        if (view == mGoButton) {
            hideKeyboard(this);

            if (TextUtils.isEmpty(mScanAssetET.getText())) {
                bGood = false;
                mScanAssetTV.setTextColor(Color.RED);
                mScanAssetET.requestFocus();
            } else {
                mScanAssetTV.setTextColor(Color.BLACK);
            }

            if (bGood) {
                mSqlDataSource.open();
                List<Assets> assets = mSqlDataSource.getAssetNum(mScanAssetET.getText().toString());
                mSqlDataSource.close();

                if (assets.size() == 0) {
                    mApp.mySnackbar(view, "no match", true);
                } else if (assets.size() > 1) {
                    mApp.mySnackbar(view, "too many matches", true);
                } else { // exactly one!
                    mScanAssetDescTV2.setText(assets.get(0).getDescription());
                    // mark this record as "scanned"
                    mSqlDataSource.open();
                    mSqlDataSource.setModified(assets.get(0));
                    mSqlDataSource.close();
                }
            }
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
