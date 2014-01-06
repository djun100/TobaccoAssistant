package com.asiaonline.tobaccoassistant.db;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {
	private static int version = 1;
	private static String name = "mydb.db";
	
	public MySQLiteHelper(Context context) {
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table person(name varchar(64)," +
				"address varchar(64)," +
				"gender varchar(10)," +
				"pic varchar(64)," +
				"id_card varchar(64) primary key," +
				"bank_card varchar(64)," +
				"loc_long varchar(64)," +
				"loc_la varchar(64)," +
				"sign_time varchar(64)," +
				"plant_area varchar(64),"+
				"yield varchar(64)," + 
				"plant_type varchar(64)"+
				")";
		db.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}
	
}
