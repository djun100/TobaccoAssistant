package com.asiaonline.tobaccoassistant.tool;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.asiaonline.tobaccoassistant.entity.Contract;
import com.ivsign.android.IDCReader.IDCReaderSDK;
public class CVRIdentityCardDevice extends IdentityCardDevice{
	private IDCReaderSDK mChatService;
	private Handler mHandler;
	private Context mContext;
	private int result;
	private String readMessage = "";
	private String mConnectedDeviceName = "CVR-100B";
	
	public CVRIdentityCardDevice(Context context,Handler handler){
		mHandler = handler;
		mContext = context;
		if(mChatService == null){
			mChatService = new IDCReaderSDK(mContext,mHandler,"/sdcard/wltlib");
		}
	}
	@Override
	public void setDevice(BluetoothDevice device) {
		
	}
	@Override
	protected boolean connectWithDevice() {
		boolean flag = false;
		Message msg = mHandler.obtainMessage();
		int ret = -1;
		try {
			ret = mChatService.CVR_InitComm(mConnectedDeviceName, 2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (IDCReaderSDK.CVR_RETCODE_SUCCESS == ret) {
			flag = true;
			msg.what = CONNECT_SUCCESS;
		}else{
			msg.what = CONNECT_FAILED;
		}
		mHandler.sendMessage(msg);
		return flag;
	}

	@Override
	public void workMethod() {
		result = mChatService.CVR_Authenticate(3000); 
		
		if (IDCReaderSDK.CVR_RETCODE_SUCCESS == result) {
			result = mChatService.CVR_Read_Content(1, 3000);
			
			if (IDCReaderSDK.CVR_RETCODE_SUCCESS == result) {
				
				byte[] dataBuf = mChatService.CVR_GetInfo();
				try {
					String TestStr = new String(dataBuf, "UTF16-LE");
					readMessage = new String(TestStr.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				if (readMessage.length() > 100) {
					Contract contract = new Contract();
					contract.setName(mChatService.GetPeopleName().trim());
					contract.setIndentityCard(mChatService.GetPeopleIDCode().trim());
					contract.setGender(mChatService.GetPeopleSex().trim());
					contract.setAddress(mChatService.GetPeopleAddress().trim());
					contract.setNational(mChatService.GetPeopleNation().trim());
					contract.setBirthday(dateFormat(mChatService.GetPeopleBirthday().trim()));
					
					Bitmap bitmap = BitmapFactory
							.decodeFile("/sdcard/wltlib/zp.bmp");
					if(bitmap != null){
						FileTool.saveImage(bitmap, mChatService.GetPeopleName().trim() + ".bmp");
						contract.setImage(mChatService.GetPeopleName().trim() + ".bmp");
					}
					Message msg = mHandler.obtainMessage(IDCARD_INFO,contract);
					mHandler.sendMessage(msg);
				}
			}
		}
	}
	@Override
	public void stopWork() {
		super.stopWork();
		try {
			mChatService.CVR_CloseComm();
			mChatService.stop();
		}catch(Exception e){
			
		}
	}
	private String dateFormat(String str){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String s = "";
		try {
			Date date = format.parse(str);
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");
			s = format2.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return s;
	}
}
