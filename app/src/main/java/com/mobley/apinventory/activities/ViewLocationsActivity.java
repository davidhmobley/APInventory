package com.mobley.apinventory.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.R;
import com.mobley.apinventory.adapters.CustomViewLocationsAdapter;
import com.mobley.apinventory.adapters.RecyclerTouchListener;
import com.mobley.apinventory.dialogs.AssetDialog;
import com.mobley.apinventory.dialogs.LocationDialog;
import com.mobley.apinventory.sql.SqlDataSource;
import com.mobley.apinventory.sql.tables.Assets;
import com.mobley.apinventory.sql.tables.Locations;
import com.mobley.apinventory.utilities.SearchTask;

import java.text.NumberFormat;
import java.util.List;

public class ViewLocationsActivity extends AppCompatActivity {
    protected static final String TAG = ViewLocationsActivity.class.getSimpleName();

    private APInventoryApp mApp;
    private SqlDataSource mSqlDataSource = null;
    private List<Locations> mLocations = null;
    private EditText mLocationsET;
    private RecyclerView mRecyclerView;
    private CustomViewLocationsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CharSequence mCharSeq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (LogConfig.ON) Log.d(TAG, "onCreate()");

        mApp = (APInventoryApp) getApplication();
        mSqlDataSource = new SqlDataSource(this);
        setContentView(R.layout.activity_view_locations);

        mSqlDataSource.open();
        mLocations = mSqlDataSource.getAllLocations();
        mSqlDataSource.close();

        NumberFormat nf = NumberFormat.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(getResources().getString(R.string.app_title));
        actionBar.setSubtitle(String.format(getResources().getString(R.string.view_locations_subtitle),
                nf.format(mLocations.size())));
        //actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mLocationsET = findViewById(R.id.locationsET);
        mLocationsET.requestFocus();
        mLocationsET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                mCharSeq = cs;
            }

            @Override
            public void beforeTextChanged(CharSequence s, int arg1, int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                new SearchTask(ViewLocationsActivity.this, mSqlDataSource).execute(mCharSeq.toString().trim());
            }

        });

        mRecyclerView = findViewById(R.id.locationsRecyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CustomViewLocationsAdapter(mLocations, mApp);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Locations location = mLocations.get(position);

                LocationDialog dlg = (LocationDialog) LocationDialog.newInstance();
                dlg.setNum(location.getLocationNum());
                dlg.setDesc(location.getLocationDesc());
                dlg.show(getSupportFragmentManager(), "Location");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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

    public void setLocations(List<Locations> locations) {
        if (LogConfig.ON) Log.d(TAG, "setLocations()");

        mLocations = locations;

        mAdapter = new CustomViewLocationsAdapter(mLocations, mApp);
        mRecyclerView.setAdapter(mAdapter);
    }
}
