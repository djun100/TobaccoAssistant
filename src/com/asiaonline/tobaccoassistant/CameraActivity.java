package com.asiaonline.tobaccoassistant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.asiaonline.tobaccoassistant.tool.BitmapTool;
import com.asiaonline.tobaccoassistant.tool.FileTool;

public class CameraActivity extends Activity implements OnClickListener,
		SurfaceHolder.Callback, Camera.PictureCallback {
	private Button button;
	private Camera camera;
	private TextView captrueTv;
	private SurfaceView cameraView;
	private SurfaceHolder surfaceHolder;
	private Button saveBt,cancelBt,closeBtn;
	private byte[] picDate;
	private int screenWidth;
	private int screenHeight;
	int captrueH;
	int captrueW;
	public static final Uri IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	
	private String type;
	private int sampleSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camera_layout);
		type = getIntent().getStringExtra("type");
		captrueTv = (TextView) this.findViewById(R.id.capture);

		screenWidth = getResources().getDisplayMetrics().widthPixels;
		screenHeight = getResources().getDisplayMetrics().heightPixels;

		LayoutParams params = (LayoutParams) captrueTv.getLayoutParams();
		
		if (type.equals("portrait")) {
			captrueW = screenWidth;
			captrueH = (int)(captrueW*1.2);
			params.width = screenWidth;
			params.height = screenHeight;
			captrueTv.setLayoutParams(params);
			sampleSize = 4;
		} else {
			captrueW = screenWidth * 4 / 5;
			captrueH = (int) (captrueW / 1.5);
			params.width = captrueW;
			params.height = captrueH;
			captrueTv.setLayoutParams(params);
			sampleSize = 1;
		}
		button = (Button) findViewById(R.id.button1);
		button.setOnClickListener(this);
		cameraView = (SurfaceView) findViewById(R.id.surfaceView);
		saveBt = (Button) this.findViewById(R.id.save);
		cancelBt = (Button) this.findViewById(R.id.cancel);
		saveBt.setOnClickListener(this);
		cancelBt.setOnClickListener(this);
		closeBtn = (Button) this.findViewById(R.id.close_btn);
		closeBtn.setOnClickListener(this);

		surfaceHolder = cameraView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);

		cameraView.setFocusable(true);
		cameraView.setFocusableInTouchMode(true);
		cameraView.setClickable(true);
		cameraView.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		picDate = data;
		cancelBt.setVisibility(View.VISIBLE);
		saveBt.setVisibility(View.VISIBLE);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		camera = Camera.open();
		try {
			camera.setPreviewDisplay(holder);
			Camera.Parameters parameters = camera.getParameters();
			// parameters.setPreviewSize(screenWidth, screenHeight);

			LayoutParams params = (LayoutParams) captrueTv.getLayoutParams();
			params.width = captrueW;
			params.height = captrueH;
			captrueTv.setLayoutParams(params);

			if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
				parameters.set("orientation", "portrait");
				camera.setDisplayOrientation(90);
				//parameters.setRotation(90);
			}else{
				parameters.set("orientation", "landscape");
				//在2.2以上可以使用
				camera.setDisplayOrientation(0);
			}
			camera.setParameters(parameters);
			List<String> colorEffects = parameters.getSupportedColorEffects();
			Iterator<String> cei = colorEffects.iterator();
			while (cei.hasNext()) {
				String currentEffect = cei.next();
				if (currentEffect.equals(Camera.Parameters.EFFECT_SOLARIZE)) {
					parameters
							.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
					break;
				}
			}
			camera.setParameters(parameters);
		} catch (IOException e) {
			camera.release();
		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			button.setClickable(false);
			camera.takePicture(null, null, this);
			break;
		case R.id.save:
			long dateTaken = System.currentTimeMillis();
			// 图像名称
			String filename = DateFormat.format("yyyy-MM-dd kk.mm.ss",
					dateTaken).toString()
					+ ".jpg";
			Bitmap bitmap = BitmapTool.getCaptureBimtap(picDate, screenHeight, screenWidth, captrueW, captrueH, sampleSize);
			//insertImage(getContentResolver(),filename,dateTaken,PATH,filename,bitmap3,picDate);
			FileTool.saveImage(bitmap,Constant.IMG_PATH, filename);
			Intent intent = getIntent();
			intent.putExtra("fileName", filename);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.cancel:
			button.setClickable(true);
			camera.startPreview();
			cancelBt.setVisibility(View.GONE);
			saveBt.setVisibility(View.GONE);
			break;
		case R.id.close_btn:
			finish();
			break;
		}
	}
	private Uri insertImage(ContentResolver cr, String name, long dateTaken,
			String directory, String filename, Bitmap source, byte[] jpegData) {

		OutputStream outputStream = null;
		String filePath = directory + filename;
		try {
			File dir = new File(directory);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File file = new File(directory, filename);
			if (file.createNewFile()) {
				outputStream = new FileOutputStream(file);
				source.compress(CompressFormat.JPEG, 100, outputStream);
				outputStream.flush();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Throwable t) {
				}
			}
		}
		ContentValues values = new ContentValues(7);
		values.put(MediaStore.Images.Media.TITLE, name);
		values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
		values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		values.put(MediaStore.Images.Media.DATA, filePath);
		return cr.insert(IMAGE_URI, values);
	}
}
