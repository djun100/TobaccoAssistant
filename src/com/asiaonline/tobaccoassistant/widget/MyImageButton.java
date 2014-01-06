package com.asiaonline.tobaccoassistant.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.R;
/**自定义控件，实现有图标的按钮
 * */
public class MyImageButton extends LinearLayout{
	private ImageView imageView;
	private TextView textView;
	
	public MyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		imageView = (ImageView) findViewById(R.id.btn_icon);
		textView = (TextView) findViewById(R.id.btn_name);
	}
	public void setImage(Drawable drawable){
		imageView.setImageDrawable(drawable);
	}
	public void setImage(int resId){
		imageView.setImageResource(resId);
	}
	public void setText(String str){
		textView.setText(str);
	}
	public void setText(int resId){
		textView.setText(resId);
	}
	
}
