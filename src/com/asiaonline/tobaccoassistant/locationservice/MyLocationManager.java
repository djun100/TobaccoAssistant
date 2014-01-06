package com.asiaonline.tobaccoassistant.locationservice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.asiaonline.tobaccoassistant.CollectInfoActivity;

public class MyLocationManager implements AMapLocationListener {

	private Activity main;
	private Handler mHandler;
	private LocationManagerProxy mAMapLocManager;
	public static final int LOCATION_INFO = 21;

	public MyLocationManager (CollectInfoActivity main,Handler handler) {
		this.main = main;
		mHandler = handler;
		mAMapLocManager = LocationManagerProxy.getInstance(main);
	}
	/**
	 * 开始定位
	 */
	public void enableMyLocation() {
		if( mAMapLocManager == null ) {
			mAMapLocManager = LocationManagerProxy.getInstance(main);
		}
		// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
		mAMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 5000, 10, this);
	}
	/**
	 * 取消定位
	 */
	public void disableMyLocation() {
		if (mAMapLocManager != null) {
			mAMapLocManager.removeUpdates(this);
		}
	}
	@Override
	public void onLocationChanged(Location location) {
		System.out.println("onLocationChanged");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onLocationChanged(AMapLocation location) {
		Log.d("map", "onLocationChanged");
		if (location != null) {
			Double longtitude = location.getLongitude();	// 经度
			Double latitude = location.getLatitude();		// 纬度
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("longtitude", longtitude);
			map.put("latitude", latitude);
			Message msg = mHandler.obtainMessage(LOCATION_INFO);
			msg.obj = map;
			mHandler.sendMessage(msg);
			
			System.out.println("longtitude, latitude = " + longtitude + ", " + latitude);
		}
	}
	/**
	 * 保存到本地
	 */
	private void save2Sdcard(String str) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/amapdata.txt");
				if (!file.exists())
					file.createNewFile();
				
				Log.i("gis", "save_path : " + file.getAbsolutePath());
				FileOutputStream fos = new FileOutputStream(file, true);
				fos.write(str.getBytes());
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * long类型时间格式化
	 */
	public static String convertToTime(long time) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(time);
		return df.format(date);
	}
}

