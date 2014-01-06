package com.asiaonline.tobaccoassistant;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.adapter.MyFragmentPagerAdapter;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.tool.SpeechUtil;
import com.asiaonline.tobaccoassistant.widget.AlwaysMarqueeTextView;
import com.asiaonline.tobaccoassistant.widget.FragmentViewPager;
import com.asiaonline.tobaccoassistant.widget.TextSurfaceView;
public class FarmerShowActivity extends FragmentActivity implements OnClickListener,OnPageChangeListener{
	private Button back;
	private Button delete;
	private TextSurfaceView tv;
	private AlwaysMarqueeTextView textView;
	private FragmentViewPager viewPager;
	private List<Contract> list = new ArrayList<Contract>();
	private ContractDAO contractDAO;
	private MyFragmentPagerAdapter adapter;
	private int position;
	private ProgressDialog progressDialog;
	private SpeechUtil speechUtil;
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.farmershowactivity);
		initData();
		initView();
		setListener();
	}
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		speechUtil.destroy();
	}
	/**初始化数据
	 * */
	private void initData(){
		preferences = getSharedPreferences("ini",MODE_PRIVATE);
		int times = preferences.getInt("times",0);
		times++;
		if(times == 1){
			Toast.makeText(this,"左右滑动可以查看更多烟农",Toast.LENGTH_LONG).show();
			preferences.edit().putInt("times", times).commit();
		}
		contractDAO = new ContractDAO(this);
		speechUtil = new SpeechUtil(this);
		speechUtil.initSpeechSynthesizer();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		list = (List<Contract>) bundle.getSerializable("list");
		position = intent.getIntExtra("position",position);
		Message msg = handler.obtainMessage();
		handler.sendMessageDelayed(msg, 400);
	}
	/**初始化控件
	 * */
	private void initView(){
		back = (Button) findViewById(R.id.back_to_list);
		viewPager = (FragmentViewPager) findViewById(R.id.show_pager);
		delete = (Button) findViewById(R.id.farmer_delete);
		textView = (AlwaysMarqueeTextView) findViewById(R.id.show_title_tv);
		textView.setText(list.get(position).getName()+"个人信息");
		if(list.get(position).getStatus()==Contract.NO){
			delete.setVisibility(View.GONE);
		}
		tv = (TextSurfaceView) findViewById(R.id.surface_tv);
		tv.setText("正在加载数据..."); 
		tv.setTextSize(18);
		tv.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.market_wait));
		tv.setGradient(false);
	}
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list);
			viewPager.setAdapter(adapter);
			viewPager.setCurrentItem(position,false);
			tv.setVisibility(View.GONE);
//			progressDialog.dismiss();
		}
	};
	/**设置事件监听
	 * */
	private void setListener(){
		back.setOnClickListener(this);
		viewPager.setOnPageChangeListener(this);
		delete.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_to_list:
			this.finish();
			break;
		case R.id.farmer_delete:
			Builder builder = new Builder(this);
			builder.setTitle("无意向签约");
			builder.setMessage(list.get(position).getName()+" 确定无意向签约？");
			builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					
				@Override
				public void onClick(DialogInterface dialog, int which) {
					list.get(position).setStatus(Contract.NO);
					contractDAO.setStatus(list.get(position));
					Toast.makeText(FarmerShowActivity.this,"操作成功",Toast.LENGTH_LONG).show();
					speechUtil.speak("操作成功");
					Intent intent = new Intent(FarmerShowActivity.this, FarmersListActivity.class);
					FarmerShowActivity.this.startActivity(intent);
					FarmerShowActivity.this.finish();
					FarmersListActivity.GTASKSREFRESH = true;
					FarmersListActivity.YESREFRESH = true;
					FarmersListActivity.NOREFRESH = true;
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			Dialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
			break;
		}
	}
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}
	@Override
	public void onPageSelected(int position) {
		FarmerShowActivity.this.position = position;
		textView.setText(list.get(position).getName()+"个人信息");
	}
	
	private void showDialog(String msg) {
		progressDialog = new ProgressDialog(FarmerShowActivity.this);
		progressDialog.setCanceledOnTouchOutside(false);
		View view = LayoutInflater.from(FarmerShowActivity.this).inflate(
				R.layout.progress, null);
		TextSurfaceView tv = (TextSurfaceView) view.findViewById(R.id.pb_tv);
		tv.setText(msg); 
		tv.setTextSize(18);
		tv.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.market_wait));
		tv.setGradient(false);
		progressDialog.show();
		progressDialog.setContentView(view);
	}
}
