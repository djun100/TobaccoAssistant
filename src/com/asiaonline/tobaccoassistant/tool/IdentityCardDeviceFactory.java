package com.asiaonline.tobaccoassistant.tool;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import com.asiaonline.tobaccoassistant.R;

public class IdentityCardDeviceFactory {
	public final static int SYN = 0;	//神思设备代号
	public final static int CVR = 1;	//华视设备代号
	private static String deviceNameArr[] = new String[]{"神思SS628(100)W","华视CVR-100B"};
	
	public static IdentityCardDevice getIdentityCardDevice(int deviceNo,Context context,Handler handler) {
		IdentityCardDevice device = null;
		if(deviceNo == SYN){
			device = new SYNIdentityCardDevice(context, handler);
		}else if(deviceNo == CVR){
			device = new CVRIdentityCardDevice(context, handler);
		}
		return device;
	}
	
	/**
	 * 得到设备名称数组
	 * @return
	 */
	public static String [] getDeviceNameArr(){
		return deviceNameArr;
	}
	
	/**
	 * 得到设备图片
	 * @param context
	 * @param deviceNo
	 * @return
	 */
	public static Drawable getDevicePic(Context context,int deviceNo){
		Drawable drawable = null;
		switch(deviceNo){
		case IdentityCardDeviceFactory.CVR:
			drawable = context.getResources().getDrawable(R.drawable.huashi);
			break;
		default:
			drawable = context.getResources().getDrawable(R.drawable.shensijpg);
			break;
		}
		return drawable;
	}
	
	
	/**
	 * 根据设备代号得到设备名
	 * @param deviceNo
	 * @return
	 */
	public static String getDeviceName(int deviceNo){
		String name = "";
		switch(deviceNo){
		case IdentityCardDeviceFactory.CVR:
			name = "华视CVR-100B";
			break;
		default:
			name = "神思SS628(100)W";
			break;
		}
		return name;
	}
}
