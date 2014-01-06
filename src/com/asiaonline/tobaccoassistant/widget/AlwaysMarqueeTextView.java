package com.asiaonline.tobaccoassistant.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
/**
 * @description 自定义TextView，实现总是滚动效果
 * @CreateDate 2012-9-16
 * */
public class AlwaysMarqueeTextView extends TextView{

	 public AlwaysMarqueeTextView(Context context) {  
	        super(context);  
	    }  
	  
	    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	    }  
	  
	    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle);  
	    }  
	      //始终处于获得焦点状态
	    @Override  
	    public boolean isFocused() {  
	        return true;  
	    }  

}
