package com.asiaonline.tobaccoassistant;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.Window;

import com.asiaonline.tobaccoassistant.frag.ContractFrag;
import com.asiaonline.tobaccoassistant.frag.ContractFrag.OnCollectionInfoListener;

public class DeviceActivity extends FragmentActivity implements OnCollectionInfoListener{
	private FragmentManager manager;
	private ContractFrag frag;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.device_layout);
		manager = getSupportFragmentManager();
		frag = new ContractFrag();
		manager.beginTransaction().add(R.id.content, frag).commit();
	}
	public void onClick(View view){
		this.finish();
	}
	
	@Override
	public void onCollectionInfo() {
		this.finish();
	}
}
