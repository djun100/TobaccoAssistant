package com.asiaonline.tobaccoassistant.frag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.FarmerShowActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.adapter.FarmersListViewAdapter;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
/** 查看页面
 * */
@SuppressLint("ValidFragment")
public class SearchFragment extends Fragment implements OnItemClickListener,OnClickListener{
	private Context context;
	private View fragmentLayout;
	private Button search_btn;
	private ListView listView;
	private EditText editText;
	private ContractDAO contractDAO;
	private FarmersListViewAdapter adapter;
	private Toast toast;
	private List<Contract> list = new ArrayList<Contract>();
	private List<Contract> data = new ArrayList<Contract>();
	public SearchFragment(){
		
	}
	public SearchFragment(Context context) {
		super();
		this.context = context;
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentLayout = inflater.inflate(R.layout.searchfragment,container,false);
		return fragmentLayout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		setListener();
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		initData();
		list.removeAll(list);
		list.addAll(data);
		adapter.notifyDataSetChanged();
	}
	/**初始化数据
	 * */
	private void initData(){
		contractDAO = new ContractDAO(context);
		data = contractDAO.findTop(0,10);
	}
	/**初始化控件
	 * */
	private void initView(){
		listView = (ListView) fragmentLayout.findViewById(R.id.search_list);
//		adapter = new FarmersListViewAdapter(context, list);
		listView.setAdapter(adapter);
		search_btn = (Button) fragmentLayout.findViewById(R.id.search_btn);
		editText = (EditText) fragmentLayout.findViewById(R.id.search_edittext);
		toast = Toast.makeText(context, "没有找到您要找的烟农…", 2000);
		toast.setGravity(Gravity.CENTER, 0, 0);
	}
	/**设置事件监听
	 * */
	private void setListener(){
		listView.setOnItemClickListener(this);
		search_btn.setOnClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		Intent intent = new Intent(context,FarmerShowActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("list", (Serializable) list);
		intent.putExtras(bundle);
		intent.putExtra("position", position);
		context.startActivity(intent);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_btn:
			String str = editText.getText().toString();
			if(str.equalsIgnoreCase("")){
				data = contractDAO.findTop(0,10);
			}else{
//				data = contractDAO.findByName(str);
			}
			if(data.size()==0){
				toast.show();
			}else{
				list.removeAll(list);
				list.addAll(data);
				adapter.notifyDataSetChanged();
			}
			break;
		}
	}
}
