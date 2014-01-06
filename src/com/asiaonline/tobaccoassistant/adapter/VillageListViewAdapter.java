package com.asiaonline.tobaccoassistant.adapter;

import java.math.BigDecimal;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.entity.VillageListItem;

public class VillageListViewAdapter extends BaseAdapter{
	private Context context;
	private List<VillageListItem> list;
	public VillageListViewAdapter(Context context, List<VillageListItem> list) {
		super();
		this.context = context;
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
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.villagelist_item, null);
			holder.village_name = (TextView) convertView.findViewById(R.id.villagename);
			holder.count_tv = (TextView) convertView.findViewById(R.id.village_count);
			holder.Area_tv = (TextView) convertView.findViewById(R.id.area_tv);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		holder.village_name.setText(list.get(position).getVillageName());
		holder.count_tv.setText("已签农户数量(户): "+list.get(position).getCount());
		holder.Area_tv.setText("总面积(亩): "+round(list.get(position).getTotalPlantArea(),2, BigDecimal.ROUND_HALF_UP));
		return convertView;
	}
	private class ViewHolder{
		TextView village_name,count_tv,Area_tv;
	}
	  public static double round(double value, int scale,  
	             int roundingMode) {    
	        BigDecimal bd = new BigDecimal(value);    
	        bd = bd.setScale(scale, roundingMode);    
	        double d = bd.doubleValue();    
	        bd = null;    
	        return d;    
	    } 
}
