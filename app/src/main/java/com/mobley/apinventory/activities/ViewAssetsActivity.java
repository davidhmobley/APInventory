package com.mobley.apinventory.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.adapters.CustomViewAssetsAdapter;
import com.mobley.apinventory.adapters.RecyclerTouchListener;
import com.mobley.apinventory.dialogs.AssetDialog;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;

import java.text.NumberFormat;
import java.util.List;

public class ViewAssetsActivity extends AppCompatActivity {
    protected static final String TAG = ViewAssetsActivity.class.getSimpleName();

    public static final String SHOW_WHAT = "ShowWhat";
    public static final int ALL_ASSETS = 0;
    public static final int ALL_SCANNED_ASSETS = 1;

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource = null;
    private List<Assets> mAssets = null;
    private RecyclerView mRecyclerView;
    private CustomViewAssetsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean skipFetchAllAssets = false;
    private int mShowWhat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LogConfig.ON) Log.d(TAG, "onCreate()");

        mApp = (APInventoryApp) getApplication();
        mSqlDataSource = new SqlDataSource(this);
        setContentView(R.layout.activity_view_assets);

        Intent intent = getIntent();
        if (intent != null) {
            // what Assets am i showing this time?
            mShowWhat = intent.getIntExtra(SHOW_WHAT, ALL_ASSETS);

            // Get the intent, verify the action and get the query
            skipFetchAllAssets = false;
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
                doMySearch(query);
                skipFetchAllAssets = true;
            }
        }

        if (!skipFetchAllAssets) {
            mSqlDataSource.open();

            if (mShowWhat == ALL_ASSETS) {
                mAssets = mSqlDataSource.getAllAssets();
            } else {
                mAssets = mSqlDataSource.getAllModifiedAssets();
            }

            mSqlDataSource.close();
        }

        NumberFormat nf = NumberFormat.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(getResources().getString(R.string.app_title));

        if (mShowWhat == ALL_ASSETS) {
            actionBar.setSubtitle(String.format(getResources().getString(R.string.view_assets_subtitle),
                    nf.format(mAssets.size())));
        } else {
            actionBar.setSubtitle(String.format(getResources().getString(R.string.view_scanned_assets_subtitle),
                    nf.format(mAssets.size())));
        }
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mRecyclerView = findViewById(R.id.assetsRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomViewAssetsAdapter(mAssets, mApp);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Assets asset = mAssets.get(position);

                AssetDialog dlg = (AssetDialog) AssetDialog.newInstance();
                dlg.setNum(asset.getAssetNum());
                dlg.setCIC(asset.getCIC());
                dlg.setCMR(asset.getCMR());
                dlg.setLastInvDate(asset.getLastInvDate());
                dlg.show(getSupportFragmentManager(), "Asset");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the options menu from XML
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_view_assets, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setSubmitButtonEnabled(true);

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

    private void doMySearch(String query) {
        if (LogConfig.ON) Log.d(TAG, "doMySearch(" + query + ")");

        mSqlDataSource.open();

        if (mShowWhat == ALL_ASSETS) {
            mAssets = mSqlDataSource.getAssetNum(query);
        } else if (mShowWhat == ALL_SCANNED_ASSETS) {
            mAssets = mSqlDataSource.getScannedAssetNum(query);
        }

        mSqlDataSource.close();
    }
}
