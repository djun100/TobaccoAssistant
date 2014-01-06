package com.asiaonline.tobaccoassistant.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.asiaonline.tobaccoassistant.Constant;

public class FileTool {
	public static boolean saveImage(Bitmap bm,String name){
		if(!isFileExist(Constant.IMG_PATH)){
			File file = new File(Constant.IMG_PATH);
			file.mkdirs();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte [] data2 = baos.toByteArray();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(Constant.IMG_PATH + name.trim());
			out.write(data2);
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(out != null){
					out.close();
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean saveImage(Bitmap bm,String path,String name){
		if(!isFileExist(path)){
			File file = new File(path);
			file.mkdirs();
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte [] data2 = baos.toByteArray();
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(path + name.trim());
			out.write(data2);
			out.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(out != null){
					out.close();
					return true;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static Bitmap getBitmap(String path,String name){
		Bitmap bm = BitmapFactory.decodeFile(path + name);
		return bm;
	}
	
	private static void writeSYNAuthorizeFile(Context context){
		String savePath = "/mnt/sdcard/Termb.Lic";
		if(isFileExist(savePath)){
			return;
		}
		write(context, savePath, "Termb.Lic");
	}
	private static void writeCVRAuthorizedFile(Context context){
		String path = "mnt/sdcard/wltlib";
		if(isFileExist(path)){
			return;
		}else{
			File file = new File(path);
			file.mkdirs();
		}
		write(context, path + "/base.dat", "wltlib/base.dat");
		write(context, path + "/license.lic", "wltlib/license.lic");
	}
	private static void write(Context context,String savePath,String fileName){
		FileOutputStream fos = null;
		InputStream in = null;
		try {
			in = context.getResources().getAssets().open(fileName);
			fos = new FileOutputStream(savePath);
			byte [] data = new byte[256];
			int len = 0;
			while((len = in.read(data)) != -1){
				fos.write(data,0,len);
			}
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {

			}
		}
	}
	
	public static void writeAuthorizeFile(Context context){
		writeSYNAuthorizeFile(context);
		//writeCVRAuthorizedFile(context);
	}
	public static boolean isFileExist(String path){
		File file = new File(path);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	public static boolean saveFile(byte [] data,String path,String fileName){
		boolean flag = false;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path + File.separator + fileName);
			fos.write(data);
			fos.flush();
			flag = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	public static void deleteImage(String fileName){
		File file = new File(Constant.IMG_PATH + fileName);
		if(file.exists()){
			file.delete();
		}
	}
	// 安装下载后的apk文件
	public static void Instanll(File file, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}	
}
