package com.asiaonline.tobaccoassistant;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.adapter.ViewPagerAdapter;
import com.asiaonline.tobaccoassistant.frag.ContractFrag;
import com.asiaonline.tobaccoassistant.frag.SetFragment;
import com.asiaonline.tobaccoassistant.frag.VillageListFragment;
import com.asiaonline.tobaccoassistant.service.ConnectionService;
import com.asiaonline.tobaccoassistant.tool.ApkInstaller;
import com.asiaonline.tobaccoassistant.tool.FileTool;
import com.asiaonline.tobaccoassistant.tool.SpeechUtil;
import com.asiaonline.tobaccoassistant.widget.NoScrollViewpager;

public class PrimaryActivity extends FragmentActivity {
	private RadioGroup navigationRG;
	private RadioButton lookOver,contract_rb,setting;
	private NoScrollViewpager viewPager;
	public ContractFrag contractFrag;
	private SetFragment setFragment;
	private VillageListFragment villageListFragment;
	private List<Fragment> list = new ArrayList<Fragment>();
	private LocationManager lm;
	public static int result = 0;
	public static int speek;
	public static boolean is = false;
	private Intent service;
	private String ttsNotSupportAction = "TTS_not_support";
	private SpeechUtil speechUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.primary_layout);
		FileTool.writeAuthorizeFile(this);
		register();
		speechUtil = new SpeechUtil(this);
		speek = getSharedPreferences("ini", MODE_PRIVATE).getInt("speek",0);
		Constant.yanzhan = getSharedPreferences("ini", MODE_PRIVATE).getString("yanzhan",Constant.yanzhandefault);
		Constant.yanzhanCode = getSharedPreferences("ini", MODE_PRIVATE).getString("yanzhanCode","");
		navigationRG = (RadioGroup) this.findViewById(R.id.navigation);
		lookOver = (RadioButton) findViewById(R.id.lookOver);
		contract_rb = (RadioButton) findViewById(R.id.contract_rb);
		setting = (RadioButton) findViewById(R.id.setting);
		navigationRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {  
				case R.id.contract_rb:
					viewPager.setCurrentItem(1, false);
					break;
				case R.id.lookOver:
					viewPager.setCurrentItem(0,false);
					break;
				case R.id.setting:
					viewPager.setCurrentItem(2,false);
					break;
				}
			}
		});
		contractFrag = new ContractFrag();
		villageListFragment = new VillageListFragment();
		setFragment = new SetFragment();
		list.add(villageListFragment);
		list.add(contractFrag);
		list.add(setFragment);
		viewPager = (NoScrollViewpager) findViewById(R.id.content_viewpager);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),list));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					lookOver.setChecked(true);
					villageListFragment.refresh();
					ContractFrag.collectInfoActHasShown = true;
					contractFrag.flag = 1;
					break;
				case 1:
					contract_rb.setChecked(true);
					ContractFrag.collectInfoActHasShown = false;
					contractFrag.refresh();
					contractFrag.flag = 0;
					break;
				case 2:
					setting.setChecked(true);
					setFragment.refresh();
					ContractFrag.collectInfoActHasShown = true;
					contractFrag.flag = 1;
					break;
				}
				
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
				
			}
		});
		service = new Intent(this,ConnectionService.class);
		int deviceNo = getSharedPreferences("defualtDevice",
				MODE_PRIVATE).getInt("deviceNo", -1);
		service.putExtra("deviceNo", deviceNo);
		startService(service);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(service);
		unregisterReceiver(receiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(is&&speechUtil.isTTSSupport()){
			Intent intent = new Intent(PrimaryActivity.this,ConnectionService.class);
			intent.setAction("ttsMark");
			startService(intent);
			is = false;
		}
		switch (result) {
		case 2:
			Toast.makeText(this, "保存失败", 1000).show();
			break;

		case 1:
			Toast.makeText(this, "保存成功", 1000).show();
			break;
		}
		result = 0;
	}
	
	/**
	 * 注册接收不支持TTS的广播
	 */
	private void register(){
		IntentFilter ttsFilter = new IntentFilter(ttsNotSupportAction);
		registerReceiver(receiver, ttsFilter);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals(ttsNotSupportAction)){
				showTTSnotSupportDialog();
			}
		}
	};
	
	/**
	 * 调整到安装语音提示支持包界面
	 *//*
	private void startInstallTTSActivity(){
		Intent installIntent = new Intent();
    	installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
    	startActivity(installIntent);
	}*/
	
	/**
	 * 显示不支持语音提示对话框
	 */
	private void showTTSnotSupportDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示!");
		builder.setMessage("系统不支持语音提示功能，是否安装支持包？");
		builder.setPositiveButton("安装", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				processInstall(PrimaryActivity.this,null,"SpeechService.mp3");
				is = true;
			}
		});
		builder.setNegativeButton("不安装", null);
		CheckBox box = new CheckBox(this);
		box.setText("不再提示");
		box.setTextColor(Color.BLACK);
		builder.setView(box);
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	private boolean processInstall(Context context ,String url,String assetsApk){
		if(!ApkInstaller.installFromAssets(context, assetsApk)){
		    Toast.makeText(this, "安装失败", Toast.LENGTH_SHORT).show();
		    return false;
		}
		return true;		
	}
	
	// ******************************************** //
	private long exitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				// System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
