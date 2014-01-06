package com.asiaonline.tobaccoassistant.service;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.asiaonline.tobaccoassistant.tool.BluetoothTool;
import com.asiaonline.tobaccoassistant.tool.IdentityCardDevice;
import com.asiaonline.tobaccoassistant.tool.IdentityCardDeviceFactory;
import com.asiaonline.tobaccoassistant.tool.SpeechUtil;

public class ConnectionService extends Service{
	private Messenger mMessenger;
	private ArrayList<Messenger> cliMessengers = new ArrayList<Messenger>();
	private boolean isConnectSuccess,isBluetoothOpen;
	private IdentityCardDevice mDevice;
	private Timer timer;
	private MyHandler mHandler;
	private BluetoothTool bluetoothTool;
	private BluetoothDevice bluetoothDevice;
	
	public static final int DEVICE_CHANGED = 20;
	public static final int MSG_REGISTER_CLIENT = 21;
	public static final int MSG_UNREGISTER_CLIENT = 22;
	public static final int DEVICE_PAUSE = 23;
	public static final int DEVICE_RESUME = 24;
	public static final int DISCONNECT = 25;
	public static final int BLUETOOTH_OPEN = 26;
	public static final int BLUETOOTH_CLOSE = 27;
	public static final int GET_CONNECTION_STATE = 28;
	public static final int NOT_SUPPORT_TTS = 29;
	public static final int SPEAK = 30;
	
	private SpeechUtil speechUtil;
	private LocationManager lm;
	@Override
	public void onCreate() {
		super.onCreate();
		mHandler = new MyHandler(this);
		mMessenger = new Messenger(mHandler);
		bluetoothTool = new BluetoothTool();
		isBluetoothOpen = BluetoothAdapter.getDefaultAdapter().isEnabled();
		speechUtil = new SpeechUtil(this);
		if(!speechUtil.isTTSSupport()){
			sendTTSBroadcast();
		}else{
			speechUtil.initSpeechSynthesizer();
		}
		lm = (LocationManager) getSystemService(
				Context.LOCATION_SERVICE);
		registerReceiver();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mMessenger.getBinder();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			if("ttsMark".equals(intent.getAction())){
				speechUtil.initSpeechSynthesizer();
			}else{
				int deviceNo = intent.getIntExtra("deviceNo", 0);
				mDevice = IdentityCardDeviceFactory.getIdentityCardDevice(deviceNo, this, mHandler);
				if(timer != null){
					timer.cancel();
					timer.purge();
				}
				timer = new Timer();
				timer.schedule(new MyTimerTask(), 0, 2000);
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.d("ConnectionService", "退出Service");
		super.onDestroy();
		timer.cancel();
		timer.purge();
		isConnectSuccess = true;
		disConnect();
		if(mDevice != null){
			mDevice.stopWork();
		}
		speechUtil.destroy();
		unregisterReceiver(mBroadCaseReceiver);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	static class MyHandler extends Handler{
		private WeakReference<ConnectionService> ref;
		public MyHandler(ConnectionService con){
			ref = new WeakReference<ConnectionService>(con);
		}
		@Override
		public void handleMessage(Message msg) {
			ConnectionService ser = ref.get();
			if(ser != null){
				switch(msg.what){
				case MSG_REGISTER_CLIENT:
					ser.cliMessengers.add(msg.replyTo);
					ser.sendMessageToClient(GET_CONNECTION_STATE, ser.isConnectSuccess);
					break;
				case MSG_UNREGISTER_CLIENT:
					ser.cliMessengers.remove(msg.replyTo);
					break;
				case IdentityCardDevice.CONNECT_FAILED:
					ser.isConnectSuccess = false;
					ser.sendMessageToClient(IdentityCardDevice.CONNECT_FAILED,null);
					break;
				case IdentityCardDevice.CONNECT_SUCCESS:
					ser.speechUtil.speak("设备连接成功");
					ser.isConnectSuccess = true;
					ser.sendMessageToClient(IdentityCardDevice.CONNECT_SUCCESS,null);
					break;
				case IdentityCardDevice.IDCARD_INFO:
					ser.speechUtil.speak("正在读取信息");
					ser.sendMessageToClient(IdentityCardDevice.IDCARD_INFO,msg.obj);
					break;
				case DEVICE_CHANGED:
					int deviceNo = msg.arg1;
					ser.disConnect();
					ser.mDevice = IdentityCardDeviceFactory.getIdentityCardDevice(deviceNo, ser, this);
					ser.isConnectSuccess = false;
					break;
				case DEVICE_PAUSE:
					if(ser.mDevice != null){
						ser.mDevice.pause();
					}
					break;
				case DEVICE_RESUME:
					if(ser.mDevice != null){
						ser.mDevice.resume();
					}
					break;
				case SPEAK:
					ser.speechUtil.speak(msg.obj.toString());
					break;
				}
			}
		}
	}
	
	/**
	 * 给绑定的客户端发送消息
	 * @param what
	 * @param obj
	 */
	private void sendMessageToClient(int what,Object obj){
		for(int i = 0 ; i < cliMessengers.size() ; i++){
			Message message = Message.obtain(); //不能放在循环外，一条消息不能重复发送
			message.obj = obj;
			message.what = what;
			Messenger messenger = cliMessengers.get(i);
			try {
				messenger.send(message);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**-------------------------------
	 * 定时器，每隔一段时间执行一次，检测是否连接设备，如果未连接则尝试连接
	 * 
	 * @author asiacom104
	 * 
	 */
	public class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			if(mDevice != null){
				synchronized (mDevice) {
					if (!isConnectSuccess &&isBluetoothOpen) { // 没有连接成功，蓝牙是开启的，并且初始化设备则尝试连接
						Log.d("ConnectionService", "尝试连接设备");
						disConnect();
						connect();
					}
				}
			}
		}
	}
	/**
	 * 连接设备
	 */
	private void connect() {
		if (bluetoothDevice == null) {
			bluetoothDevice = bluetoothTool.getIdentityCardDivece();
		}
		mDevice.setDevice(bluetoothDevice);
		mDevice.startWork();
	}

	/**
	 * 断开连接（将线程终止）等操作
	 */
	private void disConnect() {
		if (mDevice != null) {
			mDevice.stopWork();
		}
	}
	
	/**
	 * 动态注册广播，主要关于蓝牙开启、连接状态，定位信息
	 */
	public void registerReceiver() {
		IntentFilter stateChangeFilter = new IntentFilter(
				BluetoothAdapter.ACTION_STATE_CHANGED);
		IntentFilter connectedFilter = new IntentFilter(
				BluetoothDevice.ACTION_ACL_CONNECTED);
		IntentFilter disConnectedFilter = new IntentFilter(
				BluetoothDevice.ACTION_ACL_DISCONNECTED);
		IntentFilter gpsFilter = new IntentFilter("android.location.PROVIDERS_CHANGED");

		registerReceiver(mBroadCaseReceiver, stateChangeFilter);
		registerReceiver(mBroadCaseReceiver, connectedFilter);
		registerReceiver(mBroadCaseReceiver, disConnectedFilter);
		registerReceiver(mBroadCaseReceiver, gpsFilter);
	}

	private BroadcastReceiver mBroadCaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
				isConnectSuccess = false;
				speechUtil.speak("蓝牙连接已断开");
			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {	//蓝牙状态改变
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				if (adapter.isEnabled()) {
					// 蓝牙已开启
					isBluetoothOpen = true;
					speechUtil.speak("蓝牙已开启");
				} else {
					// 蓝牙已关闭
					isBluetoothOpen = false;
					isConnectSuccess = false;
					speechUtil.speak("蓝牙已关闭");
				}
			}
			if(intent.getAction().equals(LocationManager.PROVIDERS_CHANGED_ACTION)){	//gps状态改变
				if(lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					speechUtil.speak("定位服务已开启");
				}else{
					speechUtil.speak("定位服务已关闭");
				}
			}
		}
	};
	
	/**
	 * 如果不支持语音，则发送广播，让用户安装
	 */
	private void sendTTSBroadcast(){
		Intent intent = new Intent();
		intent.setAction("TTS_not_support");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		sendBroadcast(intent);
	}
}
