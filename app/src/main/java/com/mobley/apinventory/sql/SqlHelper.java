package com.mobley.apinventory.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobley.apinventory.sql.tables.Assets;
import com.mobley.apinventory.sql.tables.Locations;

/**
 * Specialized version of SQLiteOpenHelper for use with AP Inventory
 * @author davidm
 *
 */
public class SqlHelper extends SQLiteOpenHelper {
	protected static final String TAG = SqlHelper.class.getSimpleName();
	
	private static final String DATABASE_NAME = "apinv.db";
	private static final int DATABASE_VERSION = 3;

	public SqlHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/************/
	/** ASSETS **/
	/************/
	private static final String ASSETS_TABLE_CREATE =
			"create table " + Assets.ASSETS_TABLE_NAME + "( " +
					Assets.ASSETS_COL_ID + " integer primary key autoincrement, " +
					Assets.ASSETS_COL_NUM + " text not null, " +
					Assets.ASSETS_COL_BCN + " text not null, " +
					Assets.ASSETS_COL_CIC + " text not null, " +
					Assets.ASSETS_COL_CMR + " text not null, " +
					Assets.ASSETS_COL_CHG_DATE + " integer not null, " +
					Assets.ASSETS_COL_LAST_INV_DATE + " integer not null, " +
					Assets.ASSETS_COL_DIRTY + " text not null);";
	private static final String ASSETS_TABLE_DROP = "drop table if exists " + Assets.ASSETS_TABLE_NAME;

	/**
	private static final String ASSETS_UNIQUE_INDEX_CREATE =
			"create unique index if not exists " + Assets.ASSETS_UNIQUE_INDEX_NAME +
					" on " + Assets.ASSETS_TABLE_NAME + " (" + Assets.ASSETS_COL_NUM + ");";
	private static final String ASSETS_UNIQUE_INDEX_DROP = "drop index if exists " + Assets.ASSETS_UNIQUE_INDEX_NAME;
	 **/

	/***************/
	/** LOCATIONS **/
	/***************/
	private static final String LOCATIONS_TABLE_CREATE =
			"create table " + Locations.LOCATIONS_TABLE_NAME + "( " +
					Locations.LOCATIONS_COL_ID + " integer primary key autoincrement, " +
					Locations.LOCATIONS_COL_NUM + " text not null, " +
					Locations.LOCATIONS_COL_DESC + " text not null);";
	private static final String LOCATIONS_TABLE_DROP = "drop table if exists " + Locations.LOCATIONS_TABLE_NAME;


	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(ASSETS_TABLE_CREATE);
		//db.execSQL(ACCOUNT_UNIQUE_INDEX_CREATE);

		db.execSQL(LOCATIONS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(ACCOUNT_UNIQUE_INDEX_DROP);
		db.execSQL(ASSETS_TABLE_DROP);

		db.execSQL(LOCATIONS_TABLE_DROP);

		onCreate(db);
	}
}
