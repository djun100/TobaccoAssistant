package com.asiaonline.tobaccoassistant.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**不能滑动的ViewPager
 * */
public class NoScrollViewpager extends ViewPager{

	public NoScrollViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		getParent().requestDisallowInterceptTouchEvent(false);
		return false;
	}
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		getParent().requestDisallowInterceptTouchEvent(false);
		return false;
	}
}
