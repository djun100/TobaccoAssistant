package com.asiaonline.tobaccoassistant.adapter;

import java.io.Serializable;
import java.util.List;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.FarmerShowActivity;
import com.asiaonline.tobaccoassistant.FarmersListActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.tool.SpeechUtil;
import com.asiaonline.tobaccoassistant.widget.MyImageButton;
import com.asiaonline.tobaccoassistant.widget.SwipeListView;
public class FarmersListViewAdapter extends BaseAdapter{
	private Context context;
	private List<Contract> list;
	private SwipeListView listView;
	private Bitmap bitmap;
	private ContractDAO contractDAO;
	private int which;
	private SpeechUtil speechUtil;
	public FarmersListViewAdapter(Context context, List<Contract> list,int which,SwipeListView listView) {
		super();
		this.context = context;
		this.list = list;
		this.which = which;
		this.listView = listView;
		speechUtil = new SpeechUtil(context);
		speechUtil.initSpeechSynthesizer();
		contractDAO = new ContractDAO(context);
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.farmerslsit_item,parent,false);
			holder.imageView = (ImageView) convertView.findViewById(R.id.image_iv);
			holder.name_tv = (TextView) convertView.findViewById(R.id.farmer_name_tv);
			holder.sign_time = (TextView) convertView.findViewById(R.id.sign_time);
			holder.plantArea = (TextView) convertView.findViewById(R.id.plantArea_tv);
			holder.yield = (TextView) convertView.findViewById(R.id.yield);
			holder.delete = (MyImageButton) convertView.findViewById(R.id.delete);
			holder.edit = (MyImageButton) convertView.findViewById(R.id.edit);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		bitmap = BitmapFactory.decodeFile(Constant.IMG_PATH+list.get(position).getImage());
//		byte[] data = list.get(position).getImageData();
//		bitmap = BitmapFactory.decodeByteArray(data, 0,data.length);
		if(bitmap!=null){
			holder.imageView.setImageBitmap(bitmap);
		}else{
			holder.imageView.setImageResource(R.drawable.person_default);
		}
		holder.name_tv.setText(list.get(position).getName());
		holder.sign_time.setText(list.get(position).getSignTime());
		holder.yield.setText("地烟面积："+list.get(position).getPlantDYArea()+"亩");
		holder.plantArea.setText("田烟面积："+list.get(position).getPlantTYArea()+"亩");
		holder.delete.setImage(R.drawable.deletebtn); holder.delete.setText("无意向签约");
		holder.edit.setImage(R.drawable.biz_pc_account_modify_name);holder.edit.setText("意向签约");
		switch (which) {
		case FarmersListActivity.YES:
			holder.sign_time.setVisibility(View.VISIBLE);
			holder.edit.setVisibility(View.GONE);
			break;
		case FarmersListActivity.GTASKS:
			holder.sign_time.setVisibility(View.GONE);
			break;
		case FarmersListActivity.NO:
			holder.sign_time.setVisibility(View.GONE);
			holder.delete.setVisibility(View.GONE);
			break;
		
		}
		holder.delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Builder builder = new Builder(context);
				builder.setTitle("无意向签约");
				builder.setMessage(list.get(position).getName()+" 确定无意向签约？");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
						
					@Override
					public void onClick(DialogInterface dialog, int which) {
						list.get(position).setStatus(Contract.NO);
						contractDAO.setStatus(list.get(position));
						Toast.makeText(context,"操作成功！",Toast.LENGTH_LONG).show();
						speechUtil.speak("操作成功");
//						listView.dismiss(position);
						list.remove(position);
						notifyDataSetChanged();
						listView.closeOpenedItems();
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
			}
		});
		holder.edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context,FarmerShowActivity.class);
        		Bundle bundle = new Bundle();
        		bundle.putSerializable("list", (Serializable) list);
        		intent.putExtras(bundle);
        		intent.putExtra("position", position);
        		intent.putExtra("villageName", list.get(position).getVillageName());
        		context.startActivity(intent);
			}
		});
		return convertView;
	}
	private class ViewHolder{
		ImageView imageView;
		TextView name_tv;
		TextView sign_time,yield,plantArea;
		MyImageButton delete,edit;
	}
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
