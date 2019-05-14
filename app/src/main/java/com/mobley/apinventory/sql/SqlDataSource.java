package com.mobley.apinventory.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mobley.apinventory.APInventoryApp;
import com.mobley.apinventory.LogConfig;
import com.mobley.apinventory.sql.tables.Assets;
import com.mobley.apinventory.sql.tables.Locations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Datasource for the Course table
 * @author mobleyd
 *
 */
public class SqlDataSource {
	protected static final String TAG = SqlDataSource.class.getSimpleName();

	private SQLiteDatabase mDatabase;
	private SqlHelper mSqlHelper;

	private String[] allAssetsCols = {
			Assets.ASSETS_COL_ID,
			Assets.ASSETS_COL_NUM,
			Assets.ASSETS_COL_BCN,
			Assets.ASSETS_COL_CIC,
			Assets.ASSETS_COL_CMR,
			Assets.ASSETS_COL_CHG_DATE,
			Assets.ASSETS_COL_LAST_INV_DATE,
			Assets.ASSETS_COL_DIRTY
	};

	private String[] allLocationsCols = {
			Locations.LOCATIONS_COL_ID,
			Locations.LOCATIONS_COL_NUM,
			Locations.LOCATIONS_COL_DESC
	};

	/** ctor */
	public SqlDataSource(Context context) {
		mSqlHelper = new SqlHelper(context);
	}

	/**
	 * Open the database
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		mDatabase = mSqlHelper.getWritableDatabase();
	}
	
	/**
	 * Close the database
	 */
	public void close() {
		mSqlHelper.close();
	}
	
	/**
	 * Start a transaction
	 */
	public void beginTransaction() {
		mDatabase.beginTransaction();
	}
	
	/**
	 * Commit a transaction
	 */
	public void commitTransaction() {
		mDatabase.setTransactionSuccessful();
	}

	/**
	 * End a transaction
	 */
	public void endTransaction() {
		mDatabase.endTransaction();
	}


	/*****************************/
	/***** ASSETS Functions *****/
	/*****************************/

	public void insertAssets(String assetNum, String bcn, String cic, String cmr, long chgDate, long invDate) {
		if (LogConfig.ON) Log.d(TAG, "insertAssets()");

		ContentValues values = new ContentValues();

		//values.put(Account.ACCT_COL_ID, acct.getId());
		values.put(Assets.ASSETS_COL_NUM, assetNum);
		values.put(Assets.ASSETS_COL_BCN, bcn);
		values.put(Assets.ASSETS_COL_CIC, cic);
		values.put(Assets.ASSETS_COL_CMR, cmr);
		values.put(Assets.ASSETS_COL_CHG_DATE, chgDate);
		values.put(Assets.ASSETS_COL_LAST_INV_DATE, invDate);
		values.put(Assets.ASSETS_COL_DIRTY, APInventoryApp.NO);

		// Insert the record
		mDatabase.insert(Assets.ASSETS_TABLE_NAME, null, values);
	}

	public int deleteAssets() {
		if (LogConfig.ON) Log.d(TAG, "deleteAssets()");

		int count = mDatabase.delete(Assets.ASSETS_TABLE_NAME, "1", null);
		if (LogConfig.ON) Log.d(TAG, "...count: " + count);

		return count;
	}

	public long getNumAssets() {
		if (LogConfig.ON) Log.d(TAG, "getNumAssets()");

		long count = DatabaseUtils.queryNumEntries(mDatabase, Assets.ASSETS_TABLE_NAME);

		return count;
	}

	public long getNumScannedAssets() {
		if (LogConfig.ON) Log.d(TAG, "getNumScannedAssets()");

		long count = DatabaseUtils.queryNumEntries(mDatabase,
												   Assets.ASSETS_TABLE_NAME,
												   Assets.ASSETS_COL_DIRTY + "=?",
												   new String[] { APInventoryApp.YES });

		return count;
	}

	public void setModified(Assets asset) {
		if (LogConfig.ON) Log.d(TAG, "setModified()");

		Calendar cal = Calendar.getInstance();

		ContentValues values = new ContentValues();
		values.put(Assets.ASSETS_COL_DIRTY, APInventoryApp.YES);
		values.put(Assets.ASSETS_COL_CHG_DATE, cal.getTimeInMillis());
		values.put(Assets.ASSETS_COL_LAST_INV_DATE, cal.getTimeInMillis());

		String[] whereArgs = new String[] { String.valueOf(asset.getId()) };

		// Update the record
		int i = mDatabase.update(Assets.ASSETS_TABLE_NAME,
						values,
						Assets.ASSETS_COL_ID + "=?",
						whereArgs);
		Log.i(TAG, "***i: " + i);
	}

	public List<Assets> getAssetNum(String assetNum) {
		if (LogConfig.ON) Log.d(TAG, "getAssetNum(" + assetNum + ")");

		List<Assets> assets = new ArrayList<>();

		Cursor c = mDatabase.query(Assets.ASSETS_TABLE_NAME,
				allAssetsCols,
				Assets.ASSETS_COL_NUM + "=?",
				new String[] { assetNum },
				null,
				null,
				null);

		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				assets.add(new Assets(
						c.getLong(0), // id
						c.getString(1), // assetNum
						c.getString(2), // barcodeNum
						c.getString(3), // cic
						c.getString(4), // cmr
						c.getLong(5), // chg_date
						c.getLong(6), // last_inv_date
						c.getString(7))); // dirty

				c.moveToNext();
			}
			c.close();
		}

		return assets; // probably just one!
	}

	public List<Assets> getScannedAssetNum(String assetNum) {
		if (LogConfig.ON) Log.d(TAG, "getScannedAssetNum(" + assetNum + ")");

		List<Assets> assets = new ArrayList<>();

		Cursor c = mDatabase.query(Assets.ASSETS_TABLE_NAME,
				allAssetsCols,
				Assets.ASSETS_COL_NUM + "=? and " + Assets.ASSETS_COL_DIRTY + "=?",
				new String[] { assetNum, APInventoryApp.YES },
				null,
				null,
				null);

		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				assets.add(new Assets(
						c.getLong(0), // id
						c.getString(1), // assetNum
						c.getString(2), // barcodeNum
						c.getString(3), // cic
						c.getString(4), // cmr
						c.getLong(5), // chg_date
						c.getLong(6), // last_inv_date
						c.getString(7))); // dirty

				c.moveToNext();
			}
			c.close();
		}

		return assets; // probably just one!
	}

	public List<Assets> getAllModifiedAssets() {
		if (LogConfig.ON) Log.d(TAG, "getAllModifiedAssets()");

		List<Assets> assets = new ArrayList<>();

		Cursor c = mDatabase.query(Assets.ASSETS_TABLE_NAME,
				allAssetsCols,
				Assets.ASSETS_COL_DIRTY + "=?",
				new String[] { APInventoryApp.YES },
				null,
				null,
				null);

		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				assets.add(new Assets(
						c.getLong(0), // id
						c.getString(1), // assetNum
						c.getString(2), // barcodeNum
						c.getString(3), // cic
						c.getString(4), // cmr
						c.getLong(5), // chg_date
						c.getLong(6), // last_inv_date
						c.getString(7))); // dirty

				c.moveToNext();
			}
			c.close();
		}

		return assets;
	}

	public List<Assets> getAllAssets() {
		if (LogConfig.ON) Log.d(TAG, "getAllAssets()");

		List<Assets> assets = new ArrayList<>();

		Cursor c = mDatabase.query(Assets.ASSETS_TABLE_NAME,
				allAssetsCols,
				null,
				null,
				null,
				null,
				null);

		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				assets.add(new Assets(
						c.getLong(0), // id
						c.getString(1), // assetNum
						c.getString(2), // barcodeNum
						c.getString(3), // cic
						c.getString(4), // cmr
						c.getLong(5), // chg_date
						c.getLong(6), // last_inv_date
						c.getString(7))); // dirty

				c.moveToNext();
			}
			c.close();
		}

		return assets;
	}


	/*******************************/
	/***** LOCATIONS Functions *****/
	/*******************************/

	public void insertLocations(String locNum, String locDesc) {
		if (LogConfig.ON) Log.d(TAG, "insertLocations()");

		ContentValues values = new ContentValues();

		//values.put(Locations.LOCATIONS_COL_ID, acct.getId());
		values.put(Locations.LOCATIONS_COL_NUM, locNum);
		values.put(Locations.LOCATIONS_COL_DESC, locDesc);

		// Insert the record
		mDatabase.insert(Locations.LOCATIONS_TABLE_NAME, null, values);
	}

	public int deleteLocations() {
		if (LogConfig.ON) Log.d(TAG, "deleteLocations()");

		int count = mDatabase.delete(Locations.LOCATIONS_TABLE_NAME, "1", null);
		if (LogConfig.ON) Log.d(TAG, "...count: " + count);

		return count;
	}

	public long getNumLocations() {
		if (LogConfig.ON) Log.d(TAG, "getNumLocations()");

		long count = DatabaseUtils.queryNumEntries(mDatabase, Locations.LOCATIONS_TABLE_NAME);

		return count;
	}

	public List<Locations> getAllLocations() {
		if (LogConfig.ON) Log.d(TAG, "getAllLocations()");

		List<Locations> locations = new ArrayList<>();

		Cursor c = mDatabase.query(Locations.LOCATIONS_TABLE_NAME,
				allLocationsCols,
				null,
				null,
				null,
				null,
				null);

		if (c != null) {
			c.moveToFirst();
			while (!c.isAfterLast()) {
				locations.add(new Locations(
						c.getLong(0), // id
						c.getString(1), // locationNum
						c.getString(2))); // locationDesc

				c.moveToNext();
			}
			c.close();
		}

		return locations;
	}
}
