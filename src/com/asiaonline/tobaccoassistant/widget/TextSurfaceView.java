package com.asiaonline.tobaccoassistant.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class TextSurfaceView extends SurfaceView implements Callback{
	private Canvas canvas;
	private Paint paint;
	private SurfaceHolder holder;
	private boolean flag;
	private int x,y,x1,y1,speed;
	private Bitmap bitmap;
	private String text;
	private int textSize;
	private Matrix matrix;
	private int width,height;
	private int degrees = 0;
	private boolean isGradient = true;
	public TextSurfaceView(Context context){
		super(context);
	}
	public TextSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		holder = getHolder();
		holder.addCallback(this);
		paint = new Paint();
		paint.setColor(Color.WHITE);
		setZOrderOnTop(true);
	}
	public void setBitmap(Bitmap bitmap){
		this.bitmap = bitmap;
	}
	public void setText(String text){
		this.text = text;
	}
	public void setTextSize(int textSize){
		this.textSize = textSize;
	}
	public void setGradient(boolean is){
		this.isGradient = is;
	}
	void myDraw(){
		canvas = holder.lockCanvas();
		try {
			canvas.setDrawFilter(new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
			canvas.drawColor(Color.WHITE);
			matrix = new Matrix();
			matrix.setRotate(degrees, bitmap.getWidth()/2, bitmap.getHeight()/2);
			matrix.postTranslate(10, 20);
			canvas.drawBitmap(bitmap,matrix,paint);
			if(isGradient){
				LinearGradient gradient = new LinearGradient
						(x, 0, x+text.length()*textSize, textSize,new int[]{Color.rgb(4, 207,250),Color.RED,Color.BLUE},null, Shader.TileMode.REPEAT);
				paint.setShader(gradient);
			}else{
				paint.setColor(Color.BLACK);
			}
			paint.setTextSize(textSize);
			paint.setAntiAlias(true);
			canvas.save();
			canvas.drawText(text, bitmap.getWidth()+25,height/2+5, paint);
			canvas.restore();
			holder.unlockCanvasAndPost(canvas);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	void logic(){
		x+=speed;
		if(x>x1+300){
			x=0;
		}
		degrees = degrees+10;
	}
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		x = 0; y = 0; x1 = x; y1 = y;
		flag = true;
		speed = 8;
		width = getWidth();
		height = getHeight();
		new Thread(new MyRunable()).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		flag = false;
	}
	class MyRunable implements Runnable{
		@Override
		public void run() {
			while(flag){
				long start = System.currentTimeMillis();
				myDraw();
				logic();
				long end = System.currentTimeMillis();
				if (end - start < 50) {
					try {
						Thread.sleep(50 - (end - start));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}
		}
		
	}
}
