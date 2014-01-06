package com.asiaonline.tobaccoassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.frag.FarmerShowFragment;

/**
 * @author asiacom104
 *
 */
public class CollectInfoActivity extends FragmentActivity implements OnClickListener{
	private FragmentManager manager;
	private Button exitButton;
	private FarmerShowFragment fragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_collection_info);
		initView();
		manager = getSupportFragmentManager();
		
		Intent intent = getIntent();
		int from = intent.getIntExtra("from", 1);
		Contract contract = new Contract();
		if(from != FarmerShowFragment.FROM_MANUAL){
			contract = (Contract) intent.getSerializableExtra("contract");
		}
		
		fragment = new FarmerShowFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("contract", contract);
		bundle.putInt("from", from);
		bundle.putString("villageName", intent.getStringExtra("villageName"));
		fragment.setArguments(bundle);
		manager.beginTransaction().add(R.id.content,fragment).commit();
		
	}
	
	private void initView() {
		exitButton = (Button) this.findViewById(R.id.cancel);
		exitButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.cancel:
			this.finish();
			break;
		}
	}
	
}
