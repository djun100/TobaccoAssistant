package com.asiaonline.tobaccoassistant.tool;

import java.io.IOException;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import com.asiaonline.tobaccoassistant.entity.Contract;
import com.identity.Shell;
import com.identity.globalEnum;

public class SYNIdentityCardDevice extends IdentityCardDevice{
	private Context mContext;
	private Shell shell;
	private Handler mHandler;
	private boolean isSuccess;
	private byte [] data = new byte[256];
	private String wltPath;
	private String termBPath = "/mnt/sdcard/";
	
	public SYNIdentityCardDevice(Context context,Handler handler){
		mContext = context;
		mHandler = handler;
		wltPath = mContext.getFilesDir().getAbsolutePath()+"/";
	}
	@Override
	public void setDevice(BluetoothDevice device) {
		try {
			shell = new Shell(mContext,device);
		} catch (IOException e) {
			
		}
	}
	@Override
	protected boolean connectWithDevice() {
		boolean flag = false;
		globalEnum ge = globalEnum.NONE;
		Message msg = mHandler.obtainMessage();
		msg.what = CONNECT_FAILED;
		try {
			if (shell != null && shell.Register()){ 
				ge = shell.Init(); 
				if (ge == globalEnum.INITIAL_SUCCESS) {
					flag = true;
					msg.what = CONNECT_SUCCESS;
				} else {
					shell.EndCommunication();
					msg.what = CONNECT_FAILED;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mHandler.sendMessage(msg);
		return flag;
	}

	@Override
	public void workMethod() {
		globalEnum ge = globalEnum.GetIndentiyCardData_GetData_Failed;
		try {
			isSuccess = shell.SearchCard();
			if(isSuccess){
				isSuccess = shell.SelectCard();
				if(isSuccess){
					ge = shell.ReadCard();
					if(ge == globalEnum.GetDataSuccess){
						data = shell.GetCardInfoBytes();
						Contract contract = new Contract();
						
						contract.setName(shell.GetName(data).trim());
						contract.setIndentityCard(shell.GetIndentityCard(data).trim());
						contract.setAddress(shell.GetAddress(data).trim());
						contract.setNational(shell.GetNational(data).trim());
						contract.setGender(shell.GetGender(data).trim());
						contract.setBirthday(shell.GetBirthday(data).trim());
						
						int nret = shell.GetPic(wltPath,termBPath); 
						if(nret > 0){
							Bitmap bm = BitmapFactory.decodeFile(wltPath + "zp.bmp");
							FileTool.saveImage(bm, shell.GetName(data).trim() + ".bmp");
							contract.setImage(shell.GetName(data).trim() + ".bmp");
						}
						Message msg = mHandler.obtainMessage(IDCARD_INFO,contract);
						mHandler.sendMessage(msg);
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	@Override
	public void stopWork() {
		super.stopWork();
		try {
			shell.EndCommunication();
			shell.Destroy();
		}catch(Exception e){
		}
	}
	
}
