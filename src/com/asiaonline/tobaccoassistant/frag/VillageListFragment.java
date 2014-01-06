package com.asiaonline.tobaccoassistant.frag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.FarmersListActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.adapter.VillageListViewAdapter;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.entity.VillageListItem;
import com.asiaonline.tobaccoassistant.tool.HttpUtil;
import com.asiaonline.tobaccoassistant.widget.TextSurfaceView;
import com.asiaonline.tobaccoassistant.zxing.CaptureActivity;

public class VillageListFragment extends Fragment{
	private View fragment_layout;
	private ListView listView;
	private TextView title_tv;
	private Button button;
	private ProgressDialog progressDialog;
	private RelativeLayout relativeLayout;
	private List<VillageListItem> list = new ArrayList<VillageListItem>();
	private List<VillageListItem> data = new ArrayList<VillageListItem>();
	private VillageListViewAdapter adapter;
	private ContractDAO contractDAO ;
	private String result[] = new String[4];
	private String yanzhan;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment_layout = inflater.inflate(R.layout.villagelistfragmen, null);
		initView();
		return fragment_layout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	private void initView(){
		relativeLayout = (RelativeLayout) fragment_layout.findViewById(R.id.aler_layout);
		button = (Button) fragment_layout.findViewById(R.id.village_sure_btn);
		if(Constant.yanzhandefault.equalsIgnoreCase(yanzhan)){
			relativeLayout.setVisibility(View.VISIBLE);
		}else{
			relativeLayout.setVisibility(View.GONE);
		}
		listView = (ListView) fragment_layout.findViewById(R.id.village_list);
		title_tv = (TextView) fragment_layout.findViewById(R.id.village_title);
		title_tv.setText(yanzhan+"下属村委会");
		adapter = new VillageListViewAdapter(getActivity(), list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(getActivity(), FarmersListActivity.class);
				intent.putExtra("name", list.get(arg2).getVillageName());
				getActivity().startActivity(intent);
			}
		});
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(getActivity(), CaptureActivity.class), 101);
			}
		});
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if(requestCode == 101) {
				if(resultCode == Activity.RESULT_OK) {
					Bundle bundle = data.getExtras();
					String str = bundle.getString("result", "Unkown");
					if(str!=null&&!str.trim().equalsIgnoreCase("Unkown")){
						result = str.split(",");
						Constant.yanzhan = result[1].trim();
						Constant.yanzhanCode = result[0].trim();
						getActivity().getSharedPreferences("ini", getActivity().MODE_PRIVATE).edit().putString("yanzhan",Constant.yanzhan).commit();
						getActivity().getSharedPreferences("ini", getActivity().MODE_PRIVATE).edit().putString("yanzhanCode",Constant.yanzhanCode).commit();
						title_tv.setText(Constant.yanzhan+"下属村委会");
						if(relativeLayout.getVisibility()==View.VISIBLE){
							relativeLayout.setVisibility(View.GONE);
							showDialog("正在获取数据...");
							new Thread(new getFarmersRunnable()).start();
						}
					}
				}
			}
		} catch (Exception e) {
			Toast.makeText(getActivity(), "二维码信息获取异常", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	private void initData(){
		contractDAO = new ContractDAO(getActivity());
		yanzhan = getActivity().getSharedPreferences("ini", getActivity().MODE_PRIVATE).getString("yanzhan",Constant.yanzhandefault);
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
	public void refresh(){
		data.removeAll(data);
		data = contractDAO.getVillage();
		list.removeAll(list);
		list.addAll(data);
		adapter.notifyDataSetChanged();
		title_tv.setText(Constant.yanzhan+"下属村委会");
		if(relativeLayout.getVisibility()==View.VISIBLE&&!Constant.yanzhandefault.equalsIgnoreCase(Constant.yanzhan)){
			relativeLayout.setVisibility(View.GONE);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		data.removeAll(data);
		data = contractDAO.getVillage();
		list.removeAll(list);
		list.addAll(data);
		adapter.notifyDataSetChanged();
	}
	private Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg){
			switch (msg.what) {
			case Constant.GETFARMERSSUCESSED:
				adapter.notifyDataSetChanged();
				progressDialog.dismiss();
				break;
			}
		};
	};
	private class getFarmersRunnable implements Runnable{
		@Override
		public void run() {
			String str = HttpUtil.getJsonStr(getActivity());
			List<Contract> farmers = new ArrayList<Contract>();
			farmers = HttpUtil.getContractsFromJson(str);
			//获取上一年份
			String year = (new GregorianCalendar().get(Calendar.YEAR)-1)+"";
//			farmers = HttpUtil.getContractsFromJson(HttpUtil.doGet(Constant.GETFARMERS_URL, year, Constant.yanzhanCode, handler));
			if(farmers.size()>0){
				for(int i=0;i<farmers.size();i++){
					contractDAO.doInsert(farmers.get(i));
				}
				data.removeAll(data);
				data = contractDAO.getVillage();
				list.removeAll(list);
				list.addAll(data);
				try {
					Thread.sleep(3000);
					handler.sendEmptyMessage(Constant.GETFARMERSSUCESSED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
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
	
}
