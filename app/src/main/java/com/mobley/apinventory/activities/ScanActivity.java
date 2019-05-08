package com.mobley.apinventory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;
import com.mobley.apinventory.adapters.CustomViewAssetsAdapter;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;

import java.util.List;

public class ScanActivity extends AppCompatActivity {
    protected static final String TAG = ScanActivity.class.getSimpleName();

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource = null;
    private List<Assets> mAssets = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
}