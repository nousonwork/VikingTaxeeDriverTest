package com.cabgurudriver.util;

import com.cabgurudriver.pojo.gson.BookingDetails;
import com.cabgurudriver.pojo.gson.Driver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {
	
	String TAG = "MySQLiteHelper";

	public static final String DRIVER_TABLE = "driver";
	public static final String COLUMN_DRIVER_LOGIN_PHONE = "phone";
	public static final String COLUMN_LOGIN_DRIVER_ID = "driverId";
	public static final String COLUMN_LOGIN_DRIVER_STATUS = "driverStatus";	

	private static final String DATABASE_NAME = "driver_booking_details.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement

	private static final String CREATE_TABLE_DRIVER = "create table "
			+ DRIVER_TABLE + "( " + COLUMN_DRIVER_LOGIN_PHONE
			+ " text primary key not null, " + COLUMN_LOGIN_DRIVER_ID
			+ " text not null," + COLUMN_LOGIN_DRIVER_STATUS + " text not null"
			+ ");";

	

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {		
		database.execSQL(CREATE_TABLE_DRIVER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + DRIVER_TABLE);		
		onCreate(db);
	}

	public long insertDriverData(String phone, String driverId,
			String driverStatus) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(COLUMN_DRIVER_LOGIN_PHONE, phone);
		values.put(COLUMN_LOGIN_DRIVER_ID, driverId);
		values.put(COLUMN_LOGIN_DRIVER_STATUS, driverStatus);

		// insert row
		long inisert_id = db.insert(DRIVER_TABLE, null, values);
		// assigning tags to todo

		return inisert_id;
	}

	public boolean updateDriverData(String phone, String driverId,
			String driverStatus) {

		String updateQuery = "UPDATE  " + DRIVER_TABLE + " SET "
				+ COLUMN_LOGIN_DRIVER_STATUS + "=\"" + driverStatus
				+ "\" WHERE " + COLUMN_DRIVER_LOGIN_PHONE + "=\"" + phone
				+ "\"";
		Log.e("MySQLiteHelper", updateQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(updateQuery, null);
		c.moveToFirst();
		c.close();

		return true;

		/*
		 * SQLiteDatabase db = this.getWritableDatabase(); ContentValues values
		 * = new ContentValues(); //values.put(COLUMN_DRIVER_LOGIN_PHONE,
		 * phone); values.put(COLUMN_LOGIN_DRIVER_ID, driverId);
		 * values.put(COLUMN_LOGIN_DRIVER_STATUS, driverStatus);
		 * 
		 * // insert row long inisert_id = db.update(DRIVER_TABLE, values,
		 * COLUMN_DRIVER_LOGIN_PHONE, new String [] {phone});
		 */
		// assigning tags to todo

	}

	public Driver getDriverData(String phone) {
		Driver driver = null;

		String selectQuery = "SELECT  * FROM " + DRIVER_TABLE + " WHERE "
				+ COLUMN_DRIVER_LOGIN_PHONE + "=\"" + phone + "\"";
		Log.e("MySQLiteHelper", selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		if (c != null && c.moveToFirst()) {
			driver = new Driver();
			driver.setPhoneNumber(c.getString(c
					.getColumnIndex(COLUMN_DRIVER_LOGIN_PHONE)));
			driver.setDriverId(c.getString(c
					.getColumnIndex(COLUMN_LOGIN_DRIVER_ID)));
			driver.setStatus(c.getString(c
					.getColumnIndex(COLUMN_LOGIN_DRIVER_STATUS)));
			c.close();
		}
		return driver;
	}

	public int getDriverDataCount() {
		int driverCount = 0;

		String selectQuery = "SELECT  * FROM " + DRIVER_TABLE;
		Log.e("MySQLiteHelper", selectQuery);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);
		driverCount = c.getCount();
		return driverCount;
	}	

	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	
	private void DEBUG(String msg){
		Log.i(TAG, msg);
	}
}