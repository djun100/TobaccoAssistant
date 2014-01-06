package com.asiaonline.tobaccoassistant.db;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.asiaonline.tobaccoassistant.Constant;

public class SQLiteHelper {
	private static final int VERSION = 1;
	private SQLiteDatabase db;
	private MySQLiteOpenHelper helper;
	private Context mContext;
	public SQLiteHelper(Context context){
		mContext = context;
		helper = new MySQLiteOpenHelper(context);
	}
	/**
	 * 建立连接
	 */
	public void openConnection() {
		if (null == db) {
			db = helper.getWritableDatabase();
		}
	}
	/**
	 * 关闭连接
	 */
	public void closeConnection() {
		if (null != db) {
			db.close();
			db = null;
		}
	}
	/**
	 * 执行SQL语句
	 * */
	public void execSql(String sql){
		openConnection();
		try {
			db.execSQL(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}catch (Exception e) {
			
		}
		closeConnection();
	}
	/**
	 * 执行插入SQL语句
	 * */
	public void execInsertSql(String sql) throws Exception{
		openConnection();
		db.execSQL(sql);
		closeConnection();
	}
	/**
	 * 执行删除SQL语句
	 * */
	public void execDeleteSql(String sql) throws Exception{
		openConnection();
		db.execSQL(sql);
		closeConnection();
	}
	/**获取查询游标
	 * */
	public Cursor Query(String sql){
		Cursor cursor = null;
		openConnection();
		try {
			cursor = db.rawQuery(sql, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return cursor;
	}
	
	private class MySQLiteOpenHelper extends SQLiteOpenHelper{

		public MySQLiteOpenHelper(Context context) {
			super(context, Constant.DBNAME, null, VERSION);
			
		}
		@Override
		public void onCreate(SQLiteDatabase db) {
			//创建烟农表
			String sql = "create table "+ Constant.TABLENAME+" (" +
					"id integer primary key,"+
					"name varchar(64) not null," +
					"pinyin varchar(64) not null," +
					"gender varchar(64) not null," +
					"image varchar(64) not null," +
					"indentityCard varchar(64) not null," +
					"birthday varchar(64) not null," +
					"national varchar(64) not null," +
					"address varchar(64) not null," +
					"villageName varchar(64) not null," +
					"familyMembers integer,"+
					"workers integer,"+
					"education varchar(64) not null," +
					"prevPlantType varchar(64) not null," +
					"plantTYArea double ," +
					"plantDYArea double ," +
					"bankCard varchar(64) not null," +
					"bankCardNo varchar(64) not null," +
					"signTime varchar(64) not null," +
					"longtitude varchar(64) not null," +
					"latitude varchar(64) not null," +
					"status integer"+
					")";
			db.execSQL(sql);
			//创建图片表
			String sql1 = "create table "+Constant.IMAGEFILE+" ("+
					"id integer primary key,"+
					"name varchar(64) not null," +
					"uploadTime varchar(64) not null" +
					")";
			db.execSQL(sql1);
			//建前作品种表
			String sql2 = "create table "+Constant.PREVPLANTTYPE+" ("+
					"id integer primary key,"+
					"name varchar(64) not null"+
					")";
			db.execSQL(sql2);
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
	}
	public byte[] getDb(){
		ByteArrayOutputStream baos = null;
		FileInputStream fis = null;
		try {
			baos = new ByteArrayOutputStream();
			fis = new FileInputStream(mContext.getDatabasePath(Constant.DBNAME));
			System.out.println(">>"+mContext.getDatabasePath(Constant.DBNAME).getAbsolutePath());
			byte [] arr = new byte[256];
			int len = 0;
			while((len = fis.read(arr)) != -1){
				baos.write(arr, 0, len);
				baos.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fis != null){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(baos != null){
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return baos.toByteArray();
	}
}
