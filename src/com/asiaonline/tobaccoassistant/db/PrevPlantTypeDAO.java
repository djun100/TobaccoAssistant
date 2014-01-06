package com.asiaonline.tobaccoassistant.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.asiaonline.tobaccoassistant.Constant;

public class PrevPlantTypeDAO extends SQLiteHelper{

	public PrevPlantTypeDAO(Context context) {
		super(context);
	}
	public void doInsert(String pre){
		if(!isExist(pre)){
			String sql = "insert into "+Constant.PREVPLANTTYPE+ " (name)values('"+
					pre
					+"')";
			execSql(sql);
		}
	}
	public List<String> getAll(){
		List<String> list = new ArrayList<String>();
		String sql = "select * from "+Constant.PREVPLANTTYPE;
		Cursor cursor = Query(sql);
		while (cursor!=null&&cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex("name")));
		}
		return list;
	}
	public boolean isExist(String prevPlantType){
		boolean exist = false;
		String sql = "select * from "+Constant.PREVPLANTTYPE+ " where name='"+prevPlantType+"'";
		Cursor cursor = Query(sql);
		if(cursor!=null&&cursor.moveToNext()){
			exist = true;
			cursor.close();
		}
		super.closeConnection();
		return exist;
	}
}
