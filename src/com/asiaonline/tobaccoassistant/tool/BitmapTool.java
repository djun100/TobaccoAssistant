package com.asiaonline.tobaccoassistant.tool;

import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

public class BitmapTool {
	/**
	 * 计算采样比例
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static int calculateInSampleSize(
			BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	    if (height > reqHeight || width > reqWidth) {
	
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }
	
	    return inSampleSize;
	}
	/**
	 * 从图片字节数组中生产指定宽高的图片
	 * （保持原图的宽高比，采样率取原图高度/需要高度 、原图宽度/需要宽度 中比例较小的一个）
	 * @param data 图片字节数组
	 * @param reqWidth	需要的宽度
	 * @param reqHeight	需要的高度
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(byte [] data, int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    //只获得原始图片的宽度和高度
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(data, 0, data.length,options);
	    options.inPurgeable = true;
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeByteArray(data,0,data.length, options);
	}
	/**
	 * 从制定的路径下，生产制定宽高的图片
	 * @param filePath //文件路径
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static Bitmap decodeSampledBitmapFromResource(String filePath, int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);
	    options.inPurgeable = true;
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}
	/**
	 * 从指定路径下 ，得到指定宽高的图片，并旋转（逆时针）一定的角度
	 * @param filePath
	 * @param reqWidth
	 * @param reqHeight
	 * @param degree
	 * @return
	 */
	public static Bitmap decodeSampleRotateBitmap(String filePath,int reqWidth,int reqHeight,float degree){
		
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);
	    options.inPurgeable = true;
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    options.inJustDecodeBounds = false;
	    Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
	    Bitmap rotateBitmap = getRotateBitmap(bitmap, 90);
	    bitmap.recycle();
	    return rotateBitmap;
	}
	/**
	 * 将图片旋转一定角度
	 * @param bitmap 原图
	 * @param degree 旋转角度（逆时针）
	 * @return	旋转后的图片
	 */
	public static Bitmap getRotateBitmap(Bitmap bitmap,float degree){
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.setRotate(degree);
		Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),matrix,true);
		return rotateBitmap;
	}
	/**
	 * 从中间部分截取图片
	 * @param picDate
	 * @param screenHeight
	 * @param screenWidth
	 * @param captrueW
	 * @param captrueH
	 * @param insamleSize
	 * @return
	 */
	public static Bitmap getCaptureBimtap(byte [] picDate,int screenHeight,int screenWidth,int captrueW,int captrueH,int insamleSize){
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = insamleSize;
		Bitmap bitmap = BitmapFactory.decodeByteArray(picDate, 0,
				picDate.length,options);
		Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, screenHeight,
				screenWidth, true);
		bitmap.recycle();
		Bitmap captureBitmap = Bitmap.createBitmap(scaleBitmap,
				(screenHeight - captrueH) / 2,
				(screenWidth - captrueW) / 2, captrueH, captrueW);
		scaleBitmap.recycle();
		Bitmap roatateBitmap = BitmapTool.getRotateBitmap(captureBitmap, 90);
		captureBitmap.recycle();
		if(insamleSize > 1){
			Bitmap bitmap2 = Bitmap.createScaledBitmap(roatateBitmap, captrueW/insamleSize, captrueH/insamleSize, true);
			roatateBitmap.recycle();
			return bitmap2;
		}
		return roatateBitmap;
	}
}

