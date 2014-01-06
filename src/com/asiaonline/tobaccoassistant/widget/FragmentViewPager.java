package com.asiaonline.tobaccoassistant.widget;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

/**
 * @description 自定义ViewPager,处理数据加载
 * @author zhaofeng
 * @date 2013-09-15
 * */
public class FragmentViewPager extends ViewPager{
	private Handler handler;
	public FragmentViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	int mX = 0;
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
    	try {
    		int position = getCurrentItem();
			int count = getAdapter().getCount();
			int x = (int) ev.getX();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mX = x;
				break;
			case MotionEvent.ACTION_MOVE:
				if(position==0&&x-mX>30){
					Toast.makeText(getContext(),"已是第一条数据！", Toast.LENGTH_SHORT).show();
				}else if(position==count-1&&mX-x>30){
					Toast.makeText(getContext(),"已是最后一条数据！", Toast.LENGTH_SHORT).show();
				}
				break;
			case MotionEvent.ACTION_UP:
				
				break;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
        return super.onInterceptTouchEvent(ev);  
    }  
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//			int position = getCurrentItem();
//			int count = getAdapter().getCount();
//			int x = (int) ev.getX();
//			switch (ev.getAction()) {
//			case MotionEvent.ACTION_DOWN:
//				mX = x;
//				break;
//			case MotionEvent.ACTION_MOVE:
//				if(position==0&&x-mX>50){
//					Message msg = new Message();
//					msg.what = FarmerShowActivity.FIRST;
//					handler.sendMessage(msg);
//				}else if(position==count-1&&-x>50){
//					Message msg = new Message();
//					msg.what = FarmerShowActivity.LAST;
//					handler.sendMessage(msg);
//				}
//				break;
//			case MotionEvent.ACTION_UP:
//				
//				break;
//			}
//    	return super.onTouchEvent(ev);
//    }
}
