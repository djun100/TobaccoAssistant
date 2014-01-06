package com.asiaonline.tobaccoassistant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuSelectButton extends LinearLayout{
	
	TextView title,summary;

	public MenuSelectButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		title = (TextView) findViewById(android.R.id.title);
		summary = (TextView)findViewById(android.R.id.summary);
	}
	
	public void setTitleText(String s){
		if(title != null){
			title.setText(s);
		}
	}
	
	public void setTitleText(int resId){
		if(title != null){
			title.setText(resId);
		}
	}
	
	public void setSummaryText(String s){
		if(summary != null){
			summary.setText(s);
		}
	}
	
	public void setSummaryText(int resId){
		if(summary != null){
			summary.setText(resId);
		}
	}
	public String getTitleText(){
		return title.getText().toString();
	}
	public String getSummaryText(){
		return summary.getText().toString();
	}
}
