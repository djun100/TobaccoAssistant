package com.asiaonline.tobaccoassistant.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.entity.Contract;

public class AutoCompleteTextViewAdapter extends BaseAdapter{
	private List<Contract> list;
	public AutoCompleteTextViewAdapter(List<Contract> list) {
		super();
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.autocompletetextview_item,null);
			holder.name = (TextView) convertView.findViewById(R.id.auto_name_tv);
			holder.villageName = (TextView) convertView.findViewById(R.id.auto_villagename_tv);
			holder.status = (TextView) convertView.findViewById(R.id.auto_status);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(list.get(position).getName());
		holder.villageName.setText(list.get(position).getVillageName());
		String str = "";
		switch (list.get(position).getStatus()) {
			case Contract.YES:
				str = "意向签约";
				break;
			case Contract.NO:
				str = "无意向签约";
				break;
			case Contract.GTASKS:
				str = "待确定";
				break;
		}
		holder.status.setText(str);
		return convertView;
	}
	class ViewHolder{
		TextView name ,villageName,status;
	}
}
