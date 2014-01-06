package com.asiaonline.tobaccoassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.tool.IdentityCardDeviceFactory;

public class HelpActivity extends Activity implements OnClickListener{
	public static int CONTENT_GPS_HELP = 1;
	public static int CONTENT_BLUETOOTH_HELP = 2;
	public static int CONTENT_CONNECTION_HELP = 3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		int what = getIntent().getIntExtra("what", 0);
		switch(what){
		case 1:
			setContentView(R.layout.gps_help);
			break;
		case 2:
			setContentView(R.layout.bluetooth_help);
			break;
		case 3:
			setContentView(R.layout.connection_help);
			ImageView imageView = (ImageView) this.findViewById(R.id.imageView1);
			TextView textView = (TextView) this.findViewById(R.id.textView3);
			Button backBtn = (Button) this.findViewById(R.id.back_btn);
			backBtn.setOnClickListener(this);
			int deviceNo = getIntent().getIntExtra("deviceNo", 0);
			LayoutParams params = (LayoutParams) imageView.getLayoutParams();
			params.height = getResources().getDisplayMetrics().heightPixels/2;
			switch(deviceNo){
			case IdentityCardDeviceFactory.SYN:
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.connection));
				break;
			case IdentityCardDeviceFactory.CVR:
				imageView.setImageDrawable(getResources().getDrawable(R.drawable.huashia));
				textView.setText("请开启设备，到如下状态，蓝牙指示灯闪烁，即可刷卡！");
				break;
			}
			imageView.setLayoutParams(params);
			break;
		}
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back_btn:
			this.finish();
			break;
		}
	}
}
