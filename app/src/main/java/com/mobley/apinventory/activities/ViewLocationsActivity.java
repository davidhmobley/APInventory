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
import com.mobley.apinventory.adapters.CustomViewLocationsAdapter;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Locations;

import java.util.List;

public class ViewLocationsActivity extends AppCompatActivity {
    protected static final String TAG = ViewLocationsActivity.class.getSimpleName();

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource = null;
    private List<Locations> mLocations = null;
    private RecyclerView mRecyclerView;
    private CustomViewLocationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private long mCount = 0; // num locations in db

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = (APInventoryApp) getApplication();
        mSqlDataSource = new SqlDataSource(this);
        setContentView(R.layout.activity_view_locations);

        Intent intent = getIntent();
        if (intent != null) {
            mCount = intent.getLongExtra("Count", 0);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(getResources().getString(R.string.app_title));
        actionBar.setSubtitle(String.format(getResources().getString(R.string.view_locations_subtitle), mCount));
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mSqlDataSource.open();
        mLocations = mSqlDataSource.getAllLocations();
        mSqlDataSource.close();

        mRecyclerView = findViewById(R.id.locationsRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomViewLocationsAdapter(mLocations, mApp);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_locations, menu);
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
