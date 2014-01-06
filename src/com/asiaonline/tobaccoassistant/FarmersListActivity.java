package com.asiaonline.tobaccoassistant;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.adapter.AutoCompleteTextViewAdapter;
import com.asiaonline.tobaccoassistant.adapter.FarmerListViewPagerAdapter;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.frag.GtasksFragment;
import com.asiaonline.tobaccoassistant.frag.NoFragment;
import com.asiaonline.tobaccoassistant.frag.YesFragment;
/**烟农列表页面
 * */
public class FarmersListActivity extends FragmentActivity implements OnClickListener{
	private Button back_btn,add_btn,search_clear;
	private TextView title_tv;
	private ListView dropListView;
	private EditText editText;
	private ViewPager viewPager;
	private RadioGroup radioGroup;
	private RadioButton yesbtn,unBtn,noBtn;
	private ContractDAO contractDAO;
	private Toast toast;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private AutoCompleteTextViewAdapter dropListViewAdapter;
	private FarmerListViewPagerAdapter viewPagerAdapter;
	private YesFragment yesFragment;
	private GtasksFragment gtasksFragment;
	private NoFragment noFragment;
	private List<Contract> contracts = new ArrayList<Contract>();
	private List<Contract> data = new ArrayList<Contract>();
	public static String name;
	public static final int YES = 0;
	public static final int GTASKS = 1;
	public static final int NO = 2;
	public static boolean NOREFRESH = false;
	public static boolean YESREFRESH = false;
	public static boolean GTASKSREFRESH = false;
	private boolean is;
	private boolean onCreate = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.farmersactiviyt);
		initData();
		initView();
		setListener();
		onCreate = true;
	}
	
	/**初始化数据
	 * */
	private void initData(){
		contractDAO = new ContractDAO(this);
		Intent intent = getIntent();
		name = intent.getStringExtra("name");
		yesFragment = new YesFragment();
		gtasksFragment = new GtasksFragment();
		noFragment = new NoFragment();
		fragments.add(yesFragment);
		fragments.add(gtasksFragment);
		fragments.add(noFragment);
		dropListViewAdapter = new AutoCompleteTextViewAdapter(contracts);
	}
	/**初始化控件
	 * */
	private void initView(){
		title_tv = (TextView) findViewById(R.id.farmlist_title_tv);
		back_btn = (Button) findViewById(R.id.back_to_villagelist);
		add_btn = (Button) findViewById(R.id.farmer_add);
		search_clear = (Button) findViewById(R.id.search_clear);
		editText = (EditText) findViewById(R.id.farmlist_edittext);
		viewPager = (ViewPager) findViewById(R.id.farmerlist_viewpager);
		viewPager.setOffscreenPageLimit(2);
		viewPagerAdapter = new FarmerListViewPagerAdapter(getSupportFragmentManager(), fragments);
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(GTASKS);
		title_tv.setText("待确定农户列表");
		dropListView = (ListView) findViewById(R.id.droplistview);
		dropListView.setAdapter(dropListViewAdapter);
		radioGroup = (RadioGroup) findViewById(R.id.farmlist_radiogroup);
		yesbtn = (RadioButton) findViewById(R.id.radiobutton_yes);
		unBtn = (RadioButton) findViewById(R.id.radiobutton_un);
		noBtn = (RadioButton) findViewById(R.id.radiobutton_no);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case YES:
					yesbtn.setChecked(true);
					title_tv.setText("意向签约农户列表");
					if(YESREFRESH){
						yesFragment.showAll();
						YESREFRESH = false;
					}
					break;
				case GTASKS:
					unBtn.setChecked(true);
					title_tv.setText("待确定农户列表");
					break;
				case NO:
					noBtn.setChecked(true);
					title_tv.setText("无意向签约农户列表");
					if(NOREFRESH){
						noFragment.showAll();
						NOREFRESH = false;
					}
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
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.radiobutton_yes:
					viewPager.setCurrentItem(YES, false);
					break;
				case R.id.radiobutton_un:
					viewPager.setCurrentItem(GTASKS, false);
					break;
				case R.id.radiobutton_no:
					viewPager.setCurrentItem(NO, false);
					break;
				}
			}
		});
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				if(editText.getText().toString().trim().length()==0){
					yesFragment.showAll();
					gtasksFragment.showAll();
					noFragment.showAll();
					search_clear.setVisibility(View.GONE);
					dropListView.setVisibility(View.GONE);
				}else{
					search_clear.setVisibility(View.VISIBLE);
					contracts.removeAll(contracts);
					contracts.addAll(contractDAO.findByName(editText.getText().toString().trim(),name));
					if(contracts.size()>0&&is==false){
						dropListViewAdapter.notifyDataSetChanged();
						dropListView.setVisibility(View.VISIBLE);
					}else{
						dropListView.setVisibility(View.GONE);
						is = false;
					}
				}
				
			}
		});
		toast = Toast.makeText(this, "没有找到您要找的烟农…", 2000);
		toast.setGravity(Gravity.CENTER, 0, 0);
	}
	/**设置事件监听
	 * */
	private void setListener(){
		back_btn.setOnClickListener(this);
		add_btn.setOnClickListener(this);
		search_clear.setOnClickListener(this);
		dropListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				dropListView.setVisibility(View.GONE);
				is = true;
				data = contractDAO.getFarmers(contracts.get(position).getName(),contracts.get(position).getVillageName());
				editText.setText(contracts.get(position).getName());
				switch (data.get(0).getStatus()) {
				case Contract.YES:
					yesFragment.setData(data);
					yesFragment.refresh();
					viewPager.setCurrentItem(YES,false);
					break;
				case Contract.GTASKS:
					gtasksFragment.setData(data);
					gtasksFragment.refresh();
					viewPager.setCurrentItem(GTASKS,false);
					break;
				case Contract.NO:
					noFragment.setData(data);
					noFragment.refresh();
					viewPager.setCurrentItem(NO,false);
					break;
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!onCreate){
			if(YESREFRESH){
				yesFragment.showAll();
				YESREFRESH = false;
			}
			if(NOREFRESH){
				noFragment.showAll();
				NOREFRESH = false;
			}
			if(GTASKSREFRESH){
				gtasksFragment.showAll();
				GTASKSREFRESH = false;
			}
		}
		onCreate = false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.farmer_add:
			Intent intent = new Intent(FarmersListActivity.this,DeviceActivity.class);
			this.startActivity(intent);
			break;
		case R.id.back_to_villagelist:
			FarmersListActivity.this.finish();
			break;
		case R.id.search_clear:
			editText.setText("");
			break;
		}
	
	}
	
}
