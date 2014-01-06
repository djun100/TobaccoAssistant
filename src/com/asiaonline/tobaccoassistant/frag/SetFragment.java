package com.asiaonline.tobaccoassistant.frag;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.PrimaryActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.db.ImageFileDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.entity.ImageFile;
import com.asiaonline.tobaccoassistant.tool.FileTool;
import com.asiaonline.tobaccoassistant.tool.HttpUtil;
import com.asiaonline.tobaccoassistant.tool.HttpUtil.UploadCallback;
import com.asiaonline.tobaccoassistant.tool.SpeechUtil;
import com.asiaonline.tobaccoassistant.widget.MenuSelectButton;
import com.asiaonline.tobaccoassistant.widget.TextSurfaceView;
import com.asiaonline.tobaccoassistant.zxing.CaptureActivity;

public class SetFragment extends Fragment implements OnClickListener{
	private View view;
	private MenuSelectButton server_btn,update_btn,down_btn,up_btn,voic_btn,choice_btn,zxing_btn;
	private ProgressBar versionProgressBar,downProgressBar;
	private SharedPreferences preferences;
	private int which;
	private final int ON = 0 ;
	private final int OFF = 1;
	private int speek = 0;
	private String str[] = new String[]{"神思SS628(100)W","华视CVR-100B"};
	private String result[] = new String[4];
	private Dialog dialog;
	private ProgressDialog progressDialog;
	private SharedPreferences preferences2;
	private String address;
	private MyHandler handler;
	private boolean loading = false;
	public static boolean cancal = false;
	private int successNum,failedNum;
	private ImageFileDAO imageFileDAO;
	private List<ImageFile> imageFiles = new ArrayList<ImageFile>();
	private String uploadTime;
	private SpeechUtil speechUtil;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_set,null);
		initView();
		return view;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListener();
	}
	/**初始化控件
	 * */
	private void initView(){
		server_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_server);
		server_btn.setTitleText(R.string.btn_server_title);
		server_btn.setSummaryText(address);
		update_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_version);
		update_btn.setTitleText(R.string.btn_version_title);
		update_btn.setSummaryText("当前版本："+getAppVersionName());
		down_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_down);
		down_btn.setTitleText(R.string.btn_down_title);
		up_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_up);
		up_btn .setTitleText(R.string.btn_up_title);
		if("".equalsIgnoreCase(uploadTime)){
			up_btn.setSummaryText("无成功上传记录");
		}else{
			up_btn.setSummaryText("最近上传："+uploadTime);
		}
		voic_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_voic);
		voic_btn.setTitleText(R.string.btn_voic_title);
		zxing_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_zxing);
		if(speek==ON){
			voic_btn.setSummaryText("已开启");
		}else{
			voic_btn.setSummaryText("已关闭");
		}
		choice_btn = (MenuSelectButton) view.findViewById(R.id.btn_setting_choice);
		choice_btn.setTitleText(R.string.btn_choice_title);
		preferences = getActivity().getSharedPreferences("defualtDevice", getActivity().MODE_PRIVATE);
		which = preferences.getInt("deviceNo", 0);
		choice_btn.setSummaryText(str[which]);
		zxing_btn.setTitleText(R.string.btn_zxing);
		versionProgressBar = (ProgressBar) view.findViewById(R.id.version_progressbar);
		downProgressBar = (ProgressBar) view.findViewById(R.id.down_progressbar);
	}
	/**初始化数据
	 * */
	private void initData(){
		speechUtil = new SpeechUtil(getActivity());
		speechUtil.initSpeechSynthesizer();
		handler = new MyHandler();
		imageFileDAO = new ImageFileDAO(getActivity());
		preferences2 = getActivity().getSharedPreferences("ini", getActivity().MODE_PRIVATE);
		speek = preferences2.getInt("speek",ON);
		address = preferences2.getString("address",getActivity().getResources().getString(R.string.btn_server_summary));
		uploadTime = preferences2.getString("uploadTime", "");
	}
	/**设置监听
	 * */
	private void setListener(){
		server_btn.setOnClickListener(this);
		down_btn.setOnClickListener(this);
		update_btn.setOnClickListener(this);
		up_btn.setOnClickListener(this);
		voic_btn.setOnClickListener(this);
		choice_btn.setOnClickListener(this);
		zxing_btn.setOnClickListener(this);
	}
	public void refresh(){
		preferences = getActivity().getSharedPreferences("defualtDevice", getActivity().MODE_PRIVATE);
		which = preferences.getInt("deviceNo", 0);
		choice_btn.setSummaryText(str[which]);
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_setting_server://服务器设置
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_dialog_layout,null);
			Button cancel = (Button) view.findViewById(R.id.cancel_btn);
			Button reset = (Button) view.findViewById(R.id.reset);
			Button save = (Button) view.findViewById(R.id.save_btn);
			final EditText editText = (EditText) view.findViewById(R.id.set_et);
			editText.setText(server_btn.getSummaryText());
			cancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
			reset.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					editText.setText(address);
				}
			});
			save.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					address = editText.getText().toString().trim();
					SharedPreferences.Editor editor = preferences2.edit();
					editor.putString("address", address);
					editor.commit();
					dialog.dismiss();
					server_btn.setSummaryText(address);
				}
			});
			showDialog();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setContentView(view);
			break;
		case R.id.btn_setting_zxing: //二维码扫描
			startActivityForResult(new Intent(getActivity(), CaptureActivity.class), 101);
			break;
		case R.id.btn_setting_version: //版本更新
			if(cancal){
				handler.sendEmptyMessage(Constant.CANUPDATE);
				cancal = false;
			}else{
				if(!loading){
					if(HttpUtil.isNetOpen(getActivity())){
						showProgressDialog("正在检查是否有新的版本...");
						new Thread(new Runnable() {
							@Override
							public void run() {
								String result = HttpUtil.checkUpdate(Constant.CHECKUPDATE_URL,handler);
								if(!"".equalsIgnoreCase(result)){
									if(result.equalsIgnoreCase(getAppVersionName())){
										handler.sendMessage(handler.obtainMessage(Constant.NOUPDATE));
									}else{
										handler.sendMessage(handler.obtainMessage(Constant.CANUPDATE, result));
									}
								}else{
									handler.sendMessage(handler.obtainMessage(Constant.CHECKUPDATEFAILED,"获取版本信息失败"));
								}
							}
						}).start();
					}else{
						handler.sendEmptyMessage(Constant.NETERROR);
					}
					
				}else{
					Builder builder = new Builder(getActivity());
					builder.setTitle("取消更新");
					builder.setMessage("正在下载,是否取消更新？");
					builder.setPositiveButton("是",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							cancal = true;
							handler.sendEmptyMessage(Constant.CANCAL);
						}
					});
					builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					Dialog dialog = builder.create();
					dialog.setCanceledOnTouchOutside(true);
					dialog.show();
				}
			}
			break;
		case R.id.btn_setting_down: //数据下载
			if(Constant.yanzhanCode.equalsIgnoreCase("")){
				Toast.makeText(getActivity(), "您还未绑定烟站，请先扫描二维码",Toast.LENGTH_LONG).show();
			}else{
				showProgressDialog("正在获取数据...");
				if(HttpUtil.isNetOpen(getActivity())){
					new Thread(new GetFarmersRunable()).start();
				}
			}
			break;
		case R.id.btn_setting_up: //数据上传
			if(HttpUtil.isNetOpen(getActivity())){
				List<ImageFile> unUploads = imageFileDAO.unUploads();
				if("".equalsIgnoreCase(uploadTime)){
					showProgressDialog("正在上传，请稍候...");
					new Thread(new Runnable() {
						@Override
						public void run() {
							if(HttpUtil.uploadDB(Constant.UPLOAD_URL, getActivity().getDatabasePath(Constant.DBNAME).getAbsolutePath(),Constant.DBNAME)){
								handler.sendMessage(handler.obtainMessage(Constant.UPLOADSUCESSED,0, 0));
							}else{
								handler.sendMessage(handler.obtainMessage(Constant.UPLOADFAILED, 0,0));
							}
						}
					}).start();
					up_btn.setClickable(false);
					up_btn.setSummaryText("正在上传数据库文件");
				}else{
					if(unUploads.size()>0){
						Builder builder = new Builder(getActivity());
						builder.setTitle("上传确认");
						builder.setMessage("重新上传所有文件还是继续上一次未完成的上传？");
						builder.setPositiveButton("上传所有",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showProgressDialog("正在上传，请稍候...");
								new Thread(new Runnable() {
									@Override
									public void run() {
										if(HttpUtil.uploadDB(Constant.UPLOAD_URL, getActivity().getDatabasePath(Constant.DBNAME).getAbsolutePath(),Constant.DBNAME)){
											handler.sendMessage(handler.obtainMessage(Constant.UPLOADSUCESSED,0, 0));
										}else{
											handler.sendMessage(handler.obtainMessage(Constant.UPLOADFAILED, 0,0));
										}
									}
								});
								up_btn.setClickable(false);
								up_btn.setSummaryText("正在上传数据库文件");
							}
						});
						builder.setPositiveButton("继续上传",new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								showProgressDialog("正在上传，请稍候...");
								ExecutorService pool = Executors.newFixedThreadPool(3);
								imageFiles = imageFileDAO.unUploads();
								successNum=0;
								failedNum=0;
								for(int i=0;i<imageFiles.size();i++){
									pool.execute(new UploadRunable(imageFiles.get(i)));
								}
								up_btn.setClickable(false);
								up_btn.setSummaryText("正在上传图片");
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
					}else{
						
					}
				}
			}else{
				handler.sendEmptyMessage(Constant.NETERROR);
			}
			break;
		case R.id.btn_setting_voic: //语音提示
			if(speek==ON){
				speek=OFF;
				preferences2.edit().putInt("speek",OFF).commit();
				voic_btn.setSummaryText("已关闭");
				PrimaryActivity.speek=OFF;
			}else{
				speek=ON;
				preferences2.edit().putInt("speek",ON).commit();
				voic_btn.setSummaryText("已开启");
				PrimaryActivity.speek=ON;
			}
			break;
		case R.id.btn_setting_choice: //设备选择
			View dialog_layout = LayoutInflater.from(getActivity()).inflate(R.layout.set_dialog_layout,null);
			final RadioButton shensi = (RadioButton) dialog_layout.findViewById(R.id.shensi_radio);
			final RadioButton huashi = (RadioButton) dialog_layout.findViewById(R.id.huashi_radio);
			LinearLayout shensilayout = (LinearLayout) dialog_layout.findViewById(R.id.shensi_layout);
			LinearLayout huashilayout = (LinearLayout) dialog_layout.findViewById(R.id.huashi_layout);
			if(which==0){
				shensi.setChecked(true);
				huashi.setChecked(false);
			}else if(which==1){
				shensi.setChecked(false);
				huashi.setChecked(true);
			}
			shensi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(which!=0){
						shensi.setChecked(true);
						huashi.setChecked(false);
						which = 0;
						preferences.edit().putInt("deviceNo", which).commit();
						choice_btn.setSummaryText(str[which]);
					}
					dialog.dismiss();
				}
			});
			huashi.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(which!=1){
						shensi.setChecked(false);
						huashi.setChecked(true);
						which = 1;
						preferences.edit().putInt("deviceNo", which).commit();
						choice_btn.setSummaryText(str[which]);
					}
					dialog.dismiss();
				}
			});
			shensilayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(which!=0){
						shensi.setChecked(true);
						huashi.setChecked(false);
						which = 0;
						preferences.edit().putInt("deviceNo", which).commit();
						choice_btn.setSummaryText(str[which]);
					}
					dialog.dismiss();
				}
			});
			huashilayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if(which!=1){
						shensi.setChecked(false);
						huashi.setChecked(true);
						which = 1;
						preferences.edit().putInt("deviceNo", which).commit();
						choice_btn.setSummaryText(str[which]);
					}
					dialog.dismiss();
				}
			});
			showDialog();
			dialog.setCanceledOnTouchOutside(true);
			dialog.setContentView(dialog_layout);
			break;
		}
	} 
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if(requestCode == 101) {
				if(resultCode == Activity.RESULT_OK) {
					Bundle bundle = data.getExtras();
					String str = bundle.getString("result", "Unkown");
					if(str!=null&&str.trim().length()>0){
						result = str.split(",");
						final String yanzhan =  result[1].trim();
						if(Constant.yanzhandefault.equalsIgnoreCase(Constant.yanzhan)){
							Constant.yanzhan = yanzhan;
							Constant.yanzhanCode = result[0].trim();
							preferences2.edit().putString("yanzhan",Constant.yanzhan).commit();
							preferences2.edit().putString("yanzhanCode",Constant.yanzhanCode).commit();
						}else{
							if(!Constant.yanzhan.equalsIgnoreCase(yanzhan)){
								Builder builder = new Builder(getActivity());
								builder.setTitle("烟叶站更新确认");
								builder.setMessage("确定将绑定烟叶站 "+Constant.yanzhan+" 更新为 "+yanzhan+"?");
								builder.setPositiveButton("是",new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Constant.yanzhan = yanzhan;
										Constant.yanzhanCode = result[0].trim();
										preferences2.edit().putString("yanzhan",Constant.yanzhan).commit();
										preferences2.edit().putString("yanzhanCode",Constant.yanzhanCode).commit();
									}
								});
								builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
							}
						}
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**获取本应用版本号
	 * */
	private String getAppVersionName(){
		String versionName = "";   
	    try {   
	        PackageManager pm = getActivity().getPackageManager();   
	        PackageInfo pi = pm.getPackageInfo(getActivity().getPackageName(), 0);   
	        versionName = pi.versionName;   
	        if (versionName == null || versionName.length() <= 0) {   
	            return "";   
	        }   
	    } catch (Exception e) {   
	        Log.e("VersionInfo", "Exception", e);   
	    }   
	    return versionName; 
	}
	
	private void showDialog(){
		dialog = new Dialog(getActivity(),R.style.single_choice_dialog);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	/**事务类
	 * */
	class MyHandler extends Handler{
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
			case Constant.CANUPDATE: //可以更新
				if(progressDialog!=null&&progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				speechUtil.speak("发现新版本");
				Builder builder = new Builder(getActivity());
				builder.setTitle("版本更新");
				builder.setMessage("发现新版本："+msg.obj+",是否更新？");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(HttpUtil.isNetOpen(getActivity())){
							new Thread(new UpdateRunable()).start();
							update_btn.setSummaryText("正在下载新版本");
							loading = true;
						}else{
							handler.sendEmptyMessage(Constant.NETERROR);
						}
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
				break;
			case Constant.NOUPDATE: //无需更新
				progressDialog.dismiss();
				speechUtil.speak("已是最新版本");
				Toast.makeText(getActivity(), "已是最新版本", Toast.LENGTH_LONG).show();
				break;
			case Constant.CHECKUPDATEFAILED: //检查更新失败
				progressDialog.dismiss();
				Toast.makeText(getActivity(), msg.obj.toString(), Toast.LENGTH_LONG).show();
				break;
			case Constant.GETFARMERSSUCESSED: //获取烟农成功
				progressDialog.dismiss();
				speechUtil.speak("获取数据成功");
				Toast.makeText(getActivity(), "获取数据成功", Toast.LENGTH_LONG).show();
				break;
			case Constant.GETFARMERSFAILED: //获取烟农失败
				progressDialog.dismiss();
				speechUtil.speak("获取数据失败");
				break;
			case Constant.UPLOADSUCESSED: //上传数据成功
				progressDialog.dismiss();
				if(HttpUtil.isNetOpen(getActivity())){
					if(msg.arg1==0){
						//上传数据库文件成功
						imageFiles = imageFileDAO.getAllImageFile();
						successNum=0;
						failedNum=0;
						showProgressDialog("正在上传，请稍候...");
						ExecutorService pool = Executors.newFixedThreadPool(3);
						for(int i=0;i<imageFiles.size();i++){
							pool.execute(new UploadRunable(imageFiles.get(i)));
						}
						up_btn.setSummaryText("正在上传图片");
					}else{
						if(failedNum>0){
							Builder builder2 = new Builder(getActivity());
							builder2.setTitle("未上传图片");
							builder2.setMessage("还有"+failedNum+"个图片未成功上传，是否继续上传？");
							builder2.setPositiveButton("是",new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									showProgressDialog("正在上传，请稍候...");
									ExecutorService pool = Executors.newFixedThreadPool(3);
									imageFiles = imageFileDAO.unUploads();
									successNum=0;
									failedNum=0;
									for(int i=0;i<imageFiles.size();i++){
										pool.execute(new UploadRunable(imageFiles.get(i)));
									}
									up_btn.setSummaryText("正在上传图片");
								}
							});
							builder2.setNegativeButton("否", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
									up_btn.setClickable(true);
									up_btn.setSummaryText("最近上传："+uploadTime);
								}
							});
							Dialog dialog1 = builder2.create();
							dialog1.setCanceledOnTouchOutside(true);
							dialog1.show();
						}else{
							up_btn.setClickable(true);
							up_btn.setSummaryText("最近上传："+uploadTime);
						}
					}
				}else{
					handler.sendEmptyMessage(Constant.NETERROR);
				}
				
				break;
			case Constant.UPLOADFAILED:
				progressDialog.dismiss();
				if(msg.arg1==0){
					//上传数据库文件失败
					speechUtil.speak("上传失败");
					Toast.makeText(getActivity(), "上传失败", Toast.LENGTH_LONG).show();
					up_btn.setClickable(true);
					up_btn.setSummaryText("无成功上传记录");
				}else{
					//上传图片失败
					
				}
				break;
			case Constant.VERSIONPROGRESS:
				//给进度条设置进度
				versionProgressBar.setProgress(msg.arg1>100?100:msg.arg1);
				update_btn.setSummaryText("下载进度："+(msg.arg1>100?100:msg.arg1)+"%");
				break;
			case Constant.DOWNPROGRESS:
				
				break;
			case Constant.VERSIONDOWNSUCESSED:
				//新版本下载完成
				speechUtil.speak("下载完成");
				loading = false;
				versionProgressBar.setProgress(0);
				update_btn.setSummaryText("下载完成");
				FileTool.Instanll(new File(Constant.path+Constant.apkName),getActivity());
				break;
			case Constant.VERSIONDOWNFAILED:
				loading = false;
				speechUtil.speak("下载失败");
				Toast.makeText(getActivity(), "下载失败", Toast.LENGTH_LONG).show();
				update_btn.setSummaryText("下载失败");
				break;
			case Constant.CANCAL:
				loading = false;
				speechUtil.speak("已取消下载");
				Toast.makeText(getActivity(), "已取消下载", Toast.LENGTH_LONG).show();
				update_btn.setSummaryText("已取消下载");
				versionProgressBar.setProgress(0);
				break;
			case Constant.NETERROR:
				if(progressDialog!=null&&progressDialog.isShowing()){
					progressDialog.dismiss();
				}
				AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
				builder2.setTitle("无网络");
				builder2.setMessage("没有可用的网络,是否设置网络？");
				builder2.setPositiveButton("设置", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
						getActivity().startActivity(intent);
						return;
					}
				});
				builder2.show();
				break;
			}
		}
	}
	/**版本更新线程类
	 * */
	class UpdateRunable implements Runnable{
		@Override
		public void run() {
			HttpUtil.DownLoadFile(Constant.DOWNLOADVERSION_URL,Constant.path,Constant.apkName, handler);
		}
		
	}
	/**烟农数据获取线程类
	 * */
	class GetFarmersRunable implements Runnable{
		@Override
		public void run() {
			String str = HttpUtil.getJsonStr(getActivity());
			List<Contract> farmers = new ArrayList<Contract>();
			farmers = HttpUtil.getContractsFromJson(str);
			//获取上一年份
			String year = (new GregorianCalendar().get(Calendar.YEAR)-1)+"";
//			farmers = HttpUtil.getContractsFromJson(HttpUtil.doGet(Constant.GETFARMERS_URL, year, Constant.yanzhanCode, handler));
			if(farmers.size()>0){
				ContractDAO contractDAO = new ContractDAO(getActivity());
				for(int i=0;i<farmers.size();i++){
					contractDAO.doInsert(farmers.get(i));
				}
				try {
					Thread.sleep(3000);
					handler.sendEmptyMessage(Constant.GETFARMERSSUCESSED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	/**图片上传线程类
	 * */
	class UploadRunable implements Runnable{
		private ImageFile imageFile;
		public UploadRunable(ImageFile imageFile) {
			this.imageFile = imageFile;
		}
		@Override
		public void run() {
			HttpUtil.uploadImage(Constant.UPLOAD_URL,Constant.IMG_PATH,imageFile.getName(),new UploadCallback() {
				@Override
				public void onSuccess() {
					successNum++;
					imageFile.setUploadTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
					imageFileDAO.updateTime(imageFile);
				}
				@Override
				public void onFailed() {
					failedNum++;
				}
			});
			if(successNum+failedNum>=imageFiles.size()){
				handler.sendMessage(handler.obtainMessage(Constant.UPLOADSUCESSED,1,0));
				if(successNum>0){
					uploadTime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
					preferences2.edit().putString("uploadTime",uploadTime).commit();
				}
			}
		}
		
	}
	private void showProgressDialog(String msg) {
		progressDialog = new ProgressDialog(getActivity());
		progressDialog.setCanceledOnTouchOutside(false);
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.progress, null);	
		TextSurfaceView tv = (TextSurfaceView) view.findViewById(R.id.pb_tv);
		tv.setText(msg); 
		tv.setTextSize(18);
		tv.setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.market_wait));
		progressDialog.show();
		progressDialog.setContentView(view);
	} 
}
