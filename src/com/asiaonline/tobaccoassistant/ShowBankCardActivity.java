package com.asiaonline.tobaccoassistant;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;

import com.asiaonline.tobaccoassistant.widget.ScaleImageView;

public class ShowBankCardActivity extends Activity{
	private Bitmap bitmap;
	private ScaleImageView imageView;
	private RelativeLayout relativeLayout;
//	private Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bankcardactivity);
		Intent intent = getIntent();
		String bankcardPath = null;
		int resId = 0;
		try {
			bankcardPath = intent.getStringExtra("bankcard");
			resId = intent.getIntExtra("resId", 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		imageView = (ScaleImageView) findViewById(R.id.scaleimageview);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowBankCardActivity.this.finish();
			}
		});
		if(bankcardPath!=null){
			bitmap = BitmapFactory.decodeFile(Constant.IMG_PATH+bankcardPath);
			if(bitmap!=null)
				imageView.setImageBitmap(bitmap);
		}else{
			try {
				imageView.setImageResource(resId);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		relativeLayout = (RelativeLayout) findViewById(R.id.bankcard_layout);
		relativeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ShowBankCardActivity.this.finish();
			}
		});
//		button = (Button) findViewById(R.id.eixt);
//		button.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ShowBankCardActivity.this.finish();
//			}
//		});
	}
	@Override
	protected void onDestroy() {
		if(bitmap!=null)
		bitmap.recycle();
		super.onDestroy();
	}
}
