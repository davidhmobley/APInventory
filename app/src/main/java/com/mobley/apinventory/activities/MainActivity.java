package com.mobley.apinventory.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mobley.apinventory.R;

public class MainActivity extends AppCompatActivity {
    protected static final String TAG = MainActivity.class.getSimpleName();

    private TextView mAinTV, mCicTV, mCmrTV, mNumAssetsTV2, mNumLocationsTV2;
    private EditText mAinET, mCicET, mCmrET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.mipmap.ic_launcher);
        actionBar.setTitle(getResources().getString(R.string.app_title));
        actionBar.setSubtitle(getResources().getString(R.string.main_subtitle));
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

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

        // fake data
        mAinET.setText(String.valueOf(2380351));
        mCicET.setText(String.valueOf(2342));
        mCmrET.setText(String.valueOf(234));
        mNumAssetsTV2.setText(String.valueOf(5555555));
        mNumLocationsTV2.setText(String.valueOf(7777777));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
