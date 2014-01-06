package com.asiaonline.tobaccoassistant.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.entity.ImageFile;

public class ImageFileDAO extends SQLiteHelper{
	public ImageFileDAO(Context context) {
		super(context);
	}
	
	public boolean doInsert(ImageFile imageFile){
		boolean is = true;
		if(!isExist(imageFile)){
			String sql = "insert into "+Constant.IMAGEFILE+" (name,uploadTime)values('"+
					imageFile.getName()+"','"+
					imageFile.getUploadTime()+
					"')";
			try {
				execInsertSql(sql);
			} catch (Exception e) {
				e.printStackTrace();
				is = false;
			}
		}
		return is;
	}
	
	public List<ImageFile> getAllImageFile(){
		List<ImageFile> list = new ArrayList<ImageFile>();
		String sql = "select * from "+Constant.IMAGEFILE;
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			long id = cursor.getLong(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String uploadTime = cursor.getString(cursor.getColumnIndex("uploadTime"));
			list.add(new ImageFile(id, name, uploadTime));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	
	public ImageFile findByName(String n){
		ImageFile imageFile = null;
		String sql = "select * from "+Constant.IMAGEFILE+" where name='"+n+"'";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			long id = cursor.getLong(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String uploadTime = cursor.getString(cursor.getColumnIndex("uploadTime"));
			imageFile = new ImageFile(id, name, uploadTime);
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return imageFile;
	}
	public List<ImageFile> unUploads(){
		List<ImageFile> list = new ArrayList<ImageFile>();
		String sql = "select * from "+Constant.IMAGEFILE+" where uploadTime='null'";
		Cursor cursor = Query(sql);
		while(cursor!=null&&cursor.moveToNext()){
			long id = cursor.getLong(cursor.getColumnIndex("id"));
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String uploadTime = cursor.getString(cursor.getColumnIndex("uploadTime"));
			list.add(new ImageFile(id, name, uploadTime));
		}
		if(cursor!=null){
			cursor.close();
		}
		super.closeConnection();
		return list;
	}
	public void updateTime(ImageFile file){
		String sql = "update "+Constant.IMAGEFILE+ " set uploadTime='"+file.getUploadTime()+"' where name='"+file.getName()+"'";
		execSql(sql);
	}
	
	public void doDelete(String n){
		String sql = "delete from "+Constant.IMAGEFILE+" where name='"+n+"'";
		execSql(sql);
	}
	public boolean isExist(ImageFile file){
		boolean exist = false;
		String sql = "select * from "+Constant.IMAGEFILE+ " where name='"+file.getName()+"'";
		Cursor cursor = Query(sql);
		if(cursor!=null&&cursor.moveToNext()){
			exist = true;
			cursor.close();
		}
		super.closeConnection();
		return exist;
	}
}
