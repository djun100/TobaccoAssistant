package com.asiaonline.tobaccoassistant.frag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.FarmerShowActivity;
import com.asiaonline.tobaccoassistant.FarmersListActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.adapter.FarmersListViewAdapter;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.tool.SettingsManager;
import com.asiaonline.tobaccoassistant.widget.BaseSwipeListViewListener;
import com.asiaonline.tobaccoassistant.widget.SwipeListView;

public class GtasksFragment extends Fragment{
	private View fragment_layout;
	private SwipeListView listView;
	private TextView textView;
	private List<Contract> list = new ArrayList<Contract>();
	private ContractDAO contractDAO;
	private FarmersListViewAdapter adapter;
	private ArrayAdapter<String> arrayAdapter;
	private String items[] = new String[]{"有意向签约，去填写资料","不种植，加入“不种植”列表"};
	public GtasksFragment(){
		
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contractDAO = new ContractDAO(getActivity());
		list = contractDAO.getFarmers(FarmersListActivity.name,Contract.GTASKS);
		arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, items);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragment_layout = inflater.inflate(R.layout.gtasksfragment_layout, null);
		listView = (SwipeListView) fragment_layout.findViewById(R.id.gtasks_farmerlist);
		adapter = new FarmersListViewAdapter(getActivity(), list, FarmersListActivity.GTASKS,listView);
		listView.setAdapter(adapter);
		textView = (TextView) fragment_layout.findViewById(R.id.gtasks_tishi_tv);
		if(list.size()>0){
			textView.setVisibility(View.GONE);
		}else{
			textView.setVisibility(View.VISIBLE);
		}
		initListView();
		return fragment_layout;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListener();
	}
	private void setListener(){
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				Intent intent = new Intent(getActivity(),FarmerShowActivity.class);
        		Bundle bundle = new Bundle();
        		bundle.putSerializable("list", (Serializable) list);
        		intent.putExtras(bundle);
        		intent.putExtra("position", position);
        		intent.putExtra("villageName", list.get(position).getVillageName());
        		getActivity().startActivity(intent);
			}
		});
//		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
//
//			@Override
//			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
//					final int position, long arg3) {
//				Builder builder = new Builder(getActivity());
//				builder.setTitle(list.get(position).getName());
//				builder.setSingleChoiceItems(arrayAdapter,0, new OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						dialog.cancel();
//						switch (which) {
//							case 0:
//								Intent intent = new Intent(getActivity(),FarmerShowActivity.class);
//				        		Bundle bundle = new Bundle();
//				        		bundle.putSerializable("list", (Serializable) list);
//				        		intent.putExtras(bundle);
//				        		intent.putExtra("position", position);
//				        		intent.putExtra("villageName", list.get(position).getVillageName());
//				        		getActivity().startActivity(intent);
//							break;
//							case 1:
//							list.get(position).setStatus(Contract.NO);
//							contractDAO.setStatus(list.get(position));
//							list.remove(position);
//							adapter.notifyDataSetChanged();
//							FarmersListActivity.NOREFRESH = true;
//							break;
//						}
//					}
//				});
//				Dialog dialog = builder.create();
//				dialog.setCanceledOnTouchOutside(true);
//				dialog.show();
//				return false;
//			}
//		});
		listView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }
            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
            }

            @Override
            public void onStartClose(int position, boolean right) {
            }

            @Override
            public void onClickFrontView(int position) {
            	Intent intent = new Intent(getActivity(),FarmerShowActivity.class);
        		Bundle bundle = new Bundle();
        		bundle.putSerializable("list", (Serializable) list);
        		intent.putExtras(bundle);
        		intent.putExtra("position", position);
        		getActivity().startActivity(intent);
            }
            @Override
            public void onClickBackView(int position) {
            	
            }
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
//            	 for (int position : reverseSortedPositions) {
//                     list.remove(position);
//                 }
//                 adapter.notifyDataSetChanged();
            }

        });
		
	}
	private void initListView() {
		SettingsManager settings = SettingsManager.getInstance();
        listView.setSwipeMode(settings.getSwipeMode());
        listView.setSwipeActionLeft(settings.getSwipeActionLeft());
        listView.setSwipeActionRight(settings.getSwipeActionRight());
        listView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
        listView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        listView.setAnimationTime(settings.getSwipeAnimationTime());
        listView.setSwipeOpenOnLongPress(false);
    }
	public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }
	public void refresh(){
		adapter.notifyDataSetChanged();
		listView.closeOpenedItems();
		if(list.size()>0){
			textView.setVisibility(View.GONE);
		}else{
			textView.setVisibility(View.VISIBLE);
		}
	}
	public void setData(List<Contract> data){
		list.removeAll(list);
		list.addAll(data);
	}
	public void showAll(){
		list.removeAll(list);
		list.addAll(contractDAO.getFarmers(FarmersListActivity.name,Contract.GTASKS));
		refresh();
	}
}
