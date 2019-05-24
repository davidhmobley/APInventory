package com.mobley.apinventory.activities;

import java.util.List;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.R;

/**
 * Preference Activity for this application
 * @author mobleyd
 */
public class SettingsActivity extends PreferenceActivity {
	/** Logging tag */
	protected static final String TAG = SettingsActivity.class.getSimpleName();

	private APInventoryApp mApp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApp = (APInventoryApp) getApplication();

		getActionBar().setDisplayHomeAsUpEnabled(true);
       	getActionBar().setTitle(getString(R.string.app_name));
       	getActionBar().setSubtitle(getResources().getString(R.string.pref_subtitle));

       	setTheme(android.R.style.Theme_Holo);
	}

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (fragmentName.equals("com.mobley.apinventory.activities.SettingsActivity$InfoPreferences") ||
				fragmentName.equals("com.mobley.apinventory.activities.SettingsActivity$AboutPreferences"))
        {
			return true;
		} else {
			return false;
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean bOK = false;
    	
    	switch(item.getItemId()) {
    	case R.id.action_exit:
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
	
    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.prefs, target);
    }

	/**
	 * About Preferences Fragment
	 * @author mobleyd
	 *
	 */
	public static class AboutPreferences extends PreferenceFragment {
		/** Logging tag */
		protected static final String TAG = AboutPreferences.class.getSimpleName();

		APInventoryApp mApp;

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mApp = (APInventoryApp) getActivity().getApplication();

			addPreferencesFromResource(R.xml.about_prefs);

			// Version
			EditTextPreference versionPref = (EditTextPreference)findPreference(APInventoryApp.PREF_VERSION_KEY);
			String version = mApp.getAppPrefs().getString(APInventoryApp.PREF_VERSION_KEY, "???");
			versionPref.setTitle(getString(R.string.pref_version_str) + " " + version);

			// Author
			EditTextPreference aboutPref = (EditTextPreference)findPreference(APInventoryApp.PREF_AUTHOR_KEY);
			String author = mApp.getAppPrefs().getString(APInventoryApp.PREF_AUTHOR_KEY, "???");
			aboutPref.setTitle(getString(R.string.pref_author_str) + " " + author);
		}
	}

	/**
	 * Info Preferences Fragment
	 * @author mobleyd
	 *
	 */
	public static class InfoPreferences extends PreferenceFragment {
		/** Logging tag */
		protected static final String TAG = InfoPreferences.class.getSimpleName();

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			addPreferencesFromResource(R.xml.info_prefs);
		}
	}
}