package com.asiaonline.tobaccoassistant.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.ShowBankCardActivity;

public class SingleChoiceListViewAdapter extends BaseAdapter{
	private Context context;
	private String str[] ;;
	private int pos = -1;
	public SingleChoiceListViewAdapter(Context context,String str[]) {
		super();
		this.context = context;
		this.str = str;
	}
	@Override
	public int getCount() {
		return str.length;
	}
	@Override
	public String getItem(int position) {
		return str[position];
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.dialog_listview_item, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.icon_iv);
			holder.textView = (TextView) convertView.findViewById(R.id.name_tv);
			holder.radioButton = (RadioButton) convertView.findViewById(R.id.radiobtn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		if(position==0){
			holder.imageView.setBackgroundResource(R.drawable.connection);
		}else if(position==1){
			holder.imageView.setBackgroundResource(R.drawable.huashi);
		}
		holder.imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,ShowBankCardActivity.class);
				switch (position) {
				case 0:
					intent.putExtra("resId", R.drawable.connection);
					break;
				case 1:
					intent.putExtra("resId", R.drawable.huashi);
					break;
				
				}
				context.startActivity(intent);
			}
		});
		holder.textView.setText(str[position]);
		if(pos == position){
			holder.radioButton.setChecked(true);
		}else{
			holder.radioButton.setChecked(false);
		}
		return convertView;
	}
	public void setPosition(int position){
		this.pos = position;
	}
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	private class ViewHolder{
		ImageView imageView;
		TextView textView;
		RadioButton radioButton;
	}
}
