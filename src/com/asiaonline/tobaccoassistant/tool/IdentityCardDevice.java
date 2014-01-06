package com.asiaonline.tobaccoassistant.tool;

import android.bluetooth.BluetoothDevice;

public abstract class IdentityCardDevice {
	protected BluetoothDevice device;
	protected MonitorThread thread;
	//代表读取到身份证信息
	public static final int IDCARD_INFO = 11;
	//代表连接成功
	public static final int CONNECT_SUCCESS = 13;
	//代表连接失败
	public static final int CONNECT_FAILED = 14;
	protected abstract boolean connectWithDevice();
	public abstract void setDevice(BluetoothDevice device);
	
	public abstract void workMethod();
	
	//开始工作
	public final void startWork(){
		if(thread != null){
			thread.shutDown();
		}
		if(connectWithDevice()){
			thread = new MonitorThread();
			thread.start();
		}
	}
	//停止工作
	public void stopWork(){
		if(thread != null){
			thread.shutDown();
		}
	}
	//暂停对身份证读卡器的监控
	public final void pause(){
		if(thread != null){
			thread.pause = true;
		}
	}
	//继续监控……
	public void resume(){
		if(thread != null){
			thread.pause = false;
		}
	}
	class MonitorThread extends Thread{
		private boolean running = true;
		private boolean pause = false;
		@Override
		public void run() {
			while(running){
				if(pause){
					continue;
				}
				workMethod();
			}
		}
		public void shutDown(){
			this.running = false;
		}
	}
}
