package com.asiaonline.tobaccoassistant.frag;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.CollectInfoActivity;
import com.asiaonline.tobaccoassistant.DeviceActivity;
import com.asiaonline.tobaccoassistant.FarmersListActivity;
import com.asiaonline.tobaccoassistant.HelpActivity;
import com.asiaonline.tobaccoassistant.PrimaryActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.service.ConnectionService;
import com.asiaonline.tobaccoassistant.tool.IdentityCardDevice;
import com.asiaonline.tobaccoassistant.tool.IdentityCardDeviceFactory;
import com.asiaonline.tobaccoassistant.widget.TextSurfaceView;

public class ContractFrag extends Fragment implements OnClickListener {
	private TextView operateReminderTv,batteryTv;
	private ImageView currentDeviceIv,connectionIv,idcard;
	private Button manualInputBtn,backBtn;
	private CheckBox bluetoothCb, gpsCb;
	private boolean isConnectSuccess,isGpsEnable,isBluetoothOpen;

	private LocationManager lm;

	private MyHandler mHandler;
	private Messenger serMessenger;
	private Messenger cMessenger;
	// 是否已经跳转到信息收集页面
	public static boolean collectInfoActHasShown;
	private ProgressDialog progressDialog;

	private Dialog singleChoiceDialog;

	private int current; // 当前电量
	private int total; // 总电量
	private int mDeviceNo; // 当前连接的设备编号
	private String deviceNameArr[] = IdentityCardDeviceFactory
			.getDeviceNameArr();

	private SharedPreferences preference;
	private OnCollectionInfoListener collecInfoListener;
	public int flag = 0;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnCollectionInfoListener){
			collecInfoListener = (OnCollectionInfoListener) activity;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		preference = getActivity().getSharedPreferences("defualtDevice",
				Context.MODE_PRIVATE);

		isConnectSuccess = false;
		isBluetoothOpen = false;
		collectInfoActHasShown = false;
		lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		registerReceiver();
		mHandler = new MyHandler(this);
		cMessenger = new Messenger(mHandler);
		Intent intent = new Intent(getActivity(), ConnectionService.class);
		getActivity().bindService(intent,con, Context.BIND_AUTO_CREATE);
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		
		View view = inflater
				.inflate(R.layout.contract_layout, container, false);
		findView(view);
		mDeviceNo = preference.getInt("deviceNo", -1);
		if (mDeviceNo != -1) {
			currentDeviceIv.setImageDrawable(IdentityCardDeviceFactory
					.getDevicePic(getActivity(), mDeviceNo));
		}
		setGpsState();
		setBluetoothState();
		return view;
	}
	
	
	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void findView(View view) {
		operateReminderTv = (TextView) view.findViewById(R.id.reminder);
		connectionIv = (ImageView) view.findViewById(R.id.connection_iv);
		connectionIv.setOnClickListener(this);
		batteryTv = (TextView) view.findViewById(R.id.battery);

		manualInputBtn = (Button) view.findViewById(R.id.manual_input_btn);
		manualInputBtn.setOnClickListener(this);
		backBtn = (Button) view.findViewById(R.id.back_btn);
		backBtn.setOnClickListener(this);
		backBtn.setVisibility(this.getActivity().getClass().equals(PrimaryActivity.class) ? View.GONE : View.VISIBLE);

		idcard = (ImageView) view.findViewById(R.id.idcard);
		idcard.setAnimation(AnimationUtils.loadAnimation(getActivity(),
				R.anim.idcard_anim));
		idcard.setScaleType(ScaleType.CENTER_INSIDE);
		RelativeLayout.LayoutParams layoutParams1 = (LayoutParams) idcard
				.getLayoutParams();
		layoutParams1.height = getResources().getDisplayMetrics().heightPixels / 8;
		idcard.setLayoutParams(layoutParams1);

		currentDeviceIv = (ImageView) view.findViewById(R.id.device_pic);
		RelativeLayout.LayoutParams layoutParams2 = (LayoutParams) currentDeviceIv
				.getLayoutParams();
		layoutParams2.height = getResources().getDisplayMetrics().heightPixels / 4;

		currentDeviceIv.setLayoutParams(layoutParams2);

		bluetoothCb =  (CheckBox) view.findViewById(R.id.bluetooth_cb);
		bluetoothCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					BluetoothAdapter.getDefaultAdapter().enable();
				} else {
					BluetoothAdapter.getDefaultAdapter().disable();
				}
			}
		});
		gpsCb = (CheckBox) view.findViewById(R.id.gps_cb);
		gpsCb.setOnClickListener(this);

	}
	private ServiceConnection con = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serMessenger = new Messenger(service);
			Message msg = Message.obtain();
			msg.what = ConnectionService.MSG_REGISTER_CLIENT;
			msg.replyTo = cMessenger;
			try {
				serMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	
	
	/**
	 * IdentityCardTool发来的消息
	 * 
	 * @author asiacom104
	 * 
	 */
	public static class MyHandler extends Handler {
		private final WeakReference<ContractFrag> ref;

		public MyHandler(ContractFrag frg) {
			ref = new WeakReference<ContractFrag>(frg);
		}

		@Override
		public void handleMessage(Message msg) {
			ContractFrag frg = ref.get();
			if (frg != null) {
				switch (msg.what) {
				case IdentityCardDevice.IDCARD_INFO: // 发来读到的身份证信息
					if (frg.getActivity() != null && frg.isGpsEnable
							&& !frg.collectInfoActHasShown && frg.flag == 0) {
						frg.collectInfoActHasShown = true;
						frg.setIdCardViewState(false);
						frg.showDialog("正在读取信息……");

						Contract contract = (Contract) msg.obj;
						Intent intent = new Intent(frg.getActivity(),
								CollectInfoActivity.class);
						intent.putExtra("from", FarmerShowFragment.FROM_SCAN);
						intent.putExtra("contract", contract);
						intent.putExtra("villageName", frg.getVillageName());
						frg.startActivity(intent);
						frg.collectInfo();
					}
					break;
				case IdentityCardDevice.CONNECT_SUCCESS: // 连接成功的消息
					frg.operateReminderTv.setVisibility(View.VISIBLE);
					frg.setConnectionState(true);
					if (frg.isGpsEnable) {
						frg.setIdCardViewState(true);
						frg.operateReminderTv.setVisibility(View.VISIBLE);
					}
					if (frg.progressDialog != null) {
						frg.progressDialog.dismiss();
					}
					break;
				case IdentityCardDevice.CONNECT_FAILED: // 连接失败的消息
					frg.operateReminderTv.setVisibility(View.INVISIBLE);
					if (frg.progressDialog != null) {
						frg.progressDialog.dismiss();
					}
					frg.setIdCardViewState(false);
					frg.setConnectionState(false);
					break;
				case ConnectionService.GET_CONNECTION_STATE:
					boolean b = (Boolean) msg.obj;
					frg.setConnectionState(b);
					frg.operateReminderTv.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
					break;
				}
			}
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
		IntentFilter locationFilter = new IntentFilter(
				LocationManager.PROVIDERS_CHANGED_ACTION);
		IntentFilter batteryFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);

		getActivity().registerReceiver(mBroadCaseReceiver, stateChangeFilter);
		getActivity().registerReceiver(mBroadCaseReceiver, connectedFilter);
		getActivity().registerReceiver(mBroadCaseReceiver, disConnectedFilter);
		getActivity().registerReceiver(mBroadCaseReceiver, locationFilter);
		getActivity().registerReceiver(mBroadCaseReceiver, batteryFilter);
	}

	private BroadcastReceiver mBroadCaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
				if (idcard.getVisibility() == View.VISIBLE) {
					setIdCardViewState(false);
				}
				setConnectionState(false);
				operateReminderTv.setVisibility(View.INVISIBLE);
			}
			if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
				BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
				if (adapter.isEnabled()) {
					// 蓝牙已开启
					isBluetoothOpen = true;
					showDialog("正在连接设备……");
					bluetoothCb.setChecked(true);
				} else {
					// 蓝牙已关闭
					setIdCardViewState(false);
					isBluetoothOpen = false;
					setConnectionState(false);
					bluetoothCb.setChecked(false);
				}
			}
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				current = intent.getIntExtra("level", 0);
				total = intent.getIntExtra("scale", 100);
				onBatteryInfoReceiver(current, total);
			}
		}
	};

	private void onBatteryInfoReceiver(int level, int scale) {
		int percent = current * 100 / total;

		batteryTv.setText("设备剩余电量：" + percent + "%");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.connection_iv:
			Intent intent = new Intent();
			intent.setClass(getActivity(), HelpActivity.class);
			intent.putExtra("what", HelpActivity.CONTENT_CONNECTION_HELP);
			intent.putExtra("deviceNo", mDeviceNo);
			startActivity(intent);
			break;
		case R.id.manual_input_btn:
			collectInfoActHasShown = true;
			Intent intent2 = new Intent(getActivity(),
					CollectInfoActivity.class);
			intent2.putExtra("from", FarmerShowFragment.FROM_MANUAL);
			intent2.putExtra("villageName", getVillageName());
			startActivity(intent2);
			collectInfo();
			break;
		case R.id.gps_cb:
			Intent intent3 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);   
			startActivityForResult(intent3,0);
			break;
		case R.id.back_btn:
			this.getActivity().finish();
			break;
		}

	}
	
	/**
	 * 获取村委名称
	 * @return
	 */
	public String getVillageName(){
		return this.getActivity().getClass().equals(DeviceActivity.class) ? FarmersListActivity.name : "";
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		//sendMessageToService(ConnectionService.DEVICE_PAUSE, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isConnectSuccess = true;
		isBluetoothOpen = false;
		collectInfoActHasShown = false;

		getActivity().unregisterReceiver(mBroadCaseReceiver);
		sendMessageToService(ConnectionService.MSG_UNREGISTER_CLIENT, 0,null);
		getActivity().unbindService(con);
	}

	@Override
	public void onResume() {
		super.onResume();
		collectInfoActHasShown = false;
		this.setGpsState();
	}
	public void refresh(){
		int deviceNo = preference.getInt("deviceNo", -1);
		if (deviceNo == -1) {	// 第一次开启应用
			View dialog_layout = LayoutInflater.from(getActivity()).inflate(R.layout.set_dialog_layout,null);
			final RadioButton shensi = (RadioButton) dialog_layout.findViewById(R.id.shensi_radio);
			final RadioButton huashi = (RadioButton) dialog_layout.findViewById(R.id.huashi_radio);
				LinearLayout shensilayout = (LinearLayout) dialog_layout.findViewById(R.id.shensi_layout);
				LinearLayout huashilayout = (LinearLayout) dialog_layout.findViewById(R.id.huashi_layout);
				shensi.setChecked(true);
				huashi.setChecked(false);
				shensi.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						sendMessageToService(ConnectionService.DEVICE_CHANGED, 0,null);
						singleChoiceDialog.dismiss();
						currentDeviceIv.setImageDrawable(IdentityCardDeviceFactory
								.getDevicePic(getActivity(), 0));
						mDeviceNo = 0;
						preference.edit().putInt("deviceNo", 0).commit();
					}
				});
				huashi.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
							mDeviceNo = 1;
							shensi.setChecked(false);
							huashi.setChecked(true);
							preference.edit().putInt("deviceNo", 1).commit();
							singleChoiceDialog.dismiss();
							sendMessageToService(ConnectionService.DEVICE_CHANGED, 1,null);
							currentDeviceIv.setImageDrawable(IdentityCardDeviceFactory
								.getDevicePic(getActivity(), 1));
					}
				});
				shensilayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mDeviceNo = 0;
						sendMessageToService(ConnectionService.DEVICE_CHANGED, 0,null);
						singleChoiceDialog.dismiss();
						currentDeviceIv.setImageDrawable(IdentityCardDeviceFactory
								.getDevicePic(getActivity(), 0));
						preference.edit().putInt("deviceNo", 0).commit();
					}
				});
				huashilayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						mDeviceNo = 1;
						shensi.setChecked(false);
						huashi.setChecked(true);
						preference.edit().putInt("deviceNo", 1).commit();
						singleChoiceDialog.dismiss();
						sendMessageToService(ConnectionService.DEVICE_CHANGED, 1,null);
						currentDeviceIv.setImageDrawable(IdentityCardDeviceFactory
								.getDevicePic(getActivity(), 1));
					}
				});
				showSingleChoiceDialog();
				singleChoiceDialog.setContentView(dialog_layout);
			} else {
				if (mDeviceNo != deviceNo) {	//设备改变，断开连接，从新获取设备
					mDeviceNo = deviceNo;
					idcard.setAnimation(null);
					idcard.setVisibility(View.INVISIBLE);
					currentDeviceIv.setImageDrawable(IdentityCardDeviceFactory
							.getDevicePic(getActivity(), deviceNo));
					this.sendMessageToService(ConnectionService.DEVICE_CHANGED, mDeviceNo,null);
					//要放在获取设备之后
					isConnectSuccess = false;
				}
				if (!isConnectSuccess && isBluetoothOpen) {
					showDialog("正在连接设备…");
				}
			}
		
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		
	}
	
	private void sendMessageToService(int what,int arg1,Object obj) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;
		msg.obj = obj;
		try {
			if(serMessenger != null){
				serMessenger.send(msg);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 显示自定义对话框
	 * @param msg
	 */
	private void showDialog(String msg) {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setCanceledOnTouchOutside(false);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.progress, null);
		TextSurfaceView tv = (TextSurfaceView) view.findViewById(R.id.pb_tv);
		tv.setText(msg); 
		tv.setTextSize(18);
		tv.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.market_wait));
		progressDialog.show();
		progressDialog.setContentView(view);
	}

	/**
	 * 第一次启动该应用时跳出选择设备对话框
	 */
	private void showSingleChoiceDialog() {
		singleChoiceDialog = new Dialog(getActivity(),
				R.style.single_choice_dialog);
		singleChoiceDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				if (isBluetoothOpen) {
					showDialog("正在连接设备……");
				}
				if (mDeviceNo == -1) {
					mDeviceNo = IdentityCardDeviceFactory.SYN;
					preference.edit().putInt("deviceNo", mDeviceNo).commit();
				}
				sendMessageToService(ConnectionService.DEVICE_CHANGED, mDeviceNo,null);
			}
		});
		singleChoiceDialog.setCanceledOnTouchOutside(false);
		singleChoiceDialog.show();
	}

	/**
	 * 设置与Gps状态
	 */
	private void setGpsState() {
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			isGpsEnable = false;
			gpsCb.setChecked(false);
		} else {
			isGpsEnable = true;
			gpsCb.setChecked(true);
		}
	}

	/**
	 * 设置与Gps相关的参数及View等
	 * 
	 * @param normal
	 */
	private void setBluetoothState() {
		if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
			isBluetoothOpen = true;
			bluetoothCb.setChecked(true);
		} else {
			isBluetoothOpen = false;
			bluetoothCb.setChecked(false);
		}
	}

	/**
	 * 设置连接相关视图
	 * @param normal
	 */
	private void setConnectionState(boolean normal) {
		if (!normal) {
			isConnectSuccess = false;
			connectionIv.setImageResource(R.drawable.connection_false);
			connectionIv.setClickable(true);
		} else {
			isConnectSuccess = true;
			connectionIv.setImageResource(R.drawable.connection_true);
			connectionIv.setClickable(false);
		}
	}

	private void setIdCardViewState(boolean normal) {
		if (normal) {
			idcard.setVisibility(View.VISIBLE);
			idcard.setAnimation(AnimationUtils.loadAnimation(getActivity(),
					R.anim.idcard_anim));
		} else {
			idcard.setAnimation(null);
			idcard.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * Activity回调，调转到录入信息界面，Activity做出什么反应
	 */
	private void collectInfo(){
		if(collecInfoListener != null){
			collecInfoListener.onCollectionInfo();
		}
	}
	
	public interface OnCollectionInfoListener{
		public void onCollectionInfo();
	}
}
