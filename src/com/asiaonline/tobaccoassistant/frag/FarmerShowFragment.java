package com.asiaonline.tobaccoassistant.frag;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asiaonline.tobaccoassistant.CameraActivity;
import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.FarmersListActivity;
import com.asiaonline.tobaccoassistant.R;
import com.asiaonline.tobaccoassistant.ShowBankCardActivity;
import com.asiaonline.tobaccoassistant.db.ContractDAO;
import com.asiaonline.tobaccoassistant.db.ImageFileDAO;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.entity.VillageListItem;
import com.asiaonline.tobaccoassistant.locationservice.MyLocationManager;
import com.asiaonline.tobaccoassistant.service.ConnectionService;
import com.asiaonline.tobaccoassistant.tool.BitmapTool;
import com.asiaonline.tobaccoassistant.tool.FileTool;
import com.asiaonline.tobaccoassistant.tool.ValidateTool;

public class FarmerShowFragment extends Fragment implements OnClickListener,OnItemSelectedListener{
	private Button takePicBtn,saveBtn,birthdayBtn;
	private ImageView bankCardIv,portrait,delBankcardIv;
	private TextView nameTv,idTv,villageNameTv;
	private Spinner villageNameSpinner,natinalSpinner,
					familyMemSpinner,workerNumSpinner,educationSpinner
					,historyPlantSpinner;
	private EditText addressEt,plantTyAreaEt,plantDyAreaEt,bankCardIdEt;
	private RadioGroup genderRg;
	
	//用于校验存放内容非空的EditText
	private List<TextView> cannotBeEmptyList = new ArrayList<TextView>();
	//存放内容应该为大于1且最多保留两位小数的EditText
	private List<EditText> patternList = new ArrayList<EditText>();
	//存放整数的EditText
	private List<TextView> positiveIntegerList = new ArrayList<TextView>();
				
	public static final int TACK_PICTRUE = 1001;
	public static final int TACK_POTRAIT_PIC = 1002;
	private Contract contract;
	private ContractDAO contractDao;
	private ImageFileDAO imageFileDAO;
	private Dialog dialog;
	private String villageName;
	private Calendar calendar = Calendar.getInstance();
	private int currentYear;
	
	private String orignalPortraitName;
	private String orignalBankcardName;
	private String currentPortraitName;
	private String currentBankcardName;
	private boolean save;
	
	private int from; //信息来自扫描、手动录入、还是点击农户列表后的跳转
	/**
	 * 手动录入
	 */
	public static final int FROM_MANUAL = 1;
	/**
	 * 扫描录入
	 */
	public static final int FROM_SCAN = 2;
	/**
	 * 农户列表
	 */
	public static final int FROM_LIST = 3;

	private Messenger serMessenger;
	private Messenger cMessenger;
	private MyHandler mHandler;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		contractDao = new ContractDAO(getActivity());
		imageFileDAO = new ImageFileDAO(getActivity());
		mHandler = new MyHandler(this);
		cMessenger = new Messenger(mHandler);
		Intent intentService = new Intent(getActivity(),ConnectionService.class);
		getActivity().bindService(intentService, con, Service.BIND_AUTO_CREATE);
		
		currentYear = calendar.get(Calendar.YEAR);
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.identitycard_info_layout, container,false);
		contract = new Contract();
		Bundle bundle = getArguments();
		from = bundle.getInt("from", FROM_MANUAL);
		villageName = bundle.getString("villageName");
		if(bundle.containsKey("contract")){
			contract = (Contract) bundle.getSerializable("contract");
		}
		
		if(from == FROM_MANUAL){	//手动输入
			initManualView(view);
		}else if(from == FROM_SCAN){	//扫描
			initScanView(view);
		}else if(from == FROM_LIST){	//列表
			initFromlistView(view);
			
		}
		return view;
	}
	
	/**
	 * 初始化手动输入的视图
	 * @param view
	 */
	private void initManualView(View view) {
		inflatViewStub(view,R.id.can_input_stub);
		inflateOtherInfoLayout(view);
		if(villageName == null || villageName.equals("")){
			initVillageNameCanInput(view);
		}else{
			initVillageNameCannotInput(view);
		}
		contract.setGender("男");
		contract.setNational("汉族");
	}
	
	/**
	 * 初始化机器扫描视图
	 * @param view
	 * @param bundle
	 */
	private void initScanView(View view) {
		inflatViewStub(view,R.id.cannot_input_stub);
		if(isContractExist(contract)){
			initVillageNameCannotInput(view);
			contract = contractDao.findById(contract.getIndentityCard());
			showAlertDialog();
			setContractExistView();
		}else{
			if(villageName == null || villageName.equals("")){
				initVillageNameCanInput(view);
			}else{
				initVillageNameCannotInput(view);
			}
			setBaseInfo();
		}
	}
	
	/**
	 * 初始化点击农户列表显示的视图
	 * @param view
	 * @param bundle
	 */
	private void initFromlistView(View view){
		inflatViewStub(view,R.id.cannot_input_stub);
		initVillageNameCannotInput(view);
		setContractExistView();
	}
	
	/**
	 * 根据数据来源，初始化界面，如果是手动录入，则加载姓名、身份证号为EditText，否则为TextView
	 * @param view
	 * @param id
	 */
	private void inflatViewStub(View view,int id){
		ViewStub viewStub = (ViewStub) view.findViewById(id);
		viewStub.inflate();
		initView(view);
	}
	
	/**
	 * 其他信息中与身份正相关信息
	 * @param view
	 */
	private void inflateOtherInfoLayout(View view) {
		ViewStub moreInfo = (ViewStub) view.findViewById(R.id.other_identitycard_info_section);
		moreInfo.inflate();
		birthdayBtn = (Button) view.findViewById(R.id.birthday_btn);
		birthdayBtn.setOnClickListener(this);
		
		String [] nationalArr = getResources().getStringArray(R.array.national_list);
		natinalSpinner = (Spinner) view.findViewById(R.id.national_spinner);
		natinalSpinner.setAdapter(getArrayAdapter(nationalArr));
		natinalSpinner.setOnItemSelectedListener(this);
		genderRg = (RadioGroup) view.findViewById(R.id.gender);
		genderRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				contract.setGender(checkedId == R.id.male ? "男" : "女");
			}
		});
		addressEt = (EditText) view.findViewById(R.id.address_et);
	}
	
	/**
	 * 可更改村委名称，用Spinner选择
	 * @param view
	 */
	private void initVillageNameCanInput(View view){
		ViewStub viewStub = (ViewStub) view.findViewById(R.id.villageName_can_input);
		viewStub.inflate();
		villageNameSpinner = (Spinner) view.findViewById(R.id.village_name_spinner);
		String [] arr = getVillageNameArr();
		villageNameSpinner.setAdapter(getArrayAdapter(arr));
		villageNameSpinner.setOnItemSelectedListener(this);
	}
	private ArrayAdapter<String> getArrayAdapter(String [] arr) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item,arr);
		adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
		return adapter;
	}
	/**
	 * 不可更改村委名称，用TextView显示
	 * @param view
	 */
	private void initVillageNameCannotInput(View view){
		ViewStub viewStub = (ViewStub) view.findViewById(R.id.villageName_cannot_input);
		viewStub.inflate();
		villageNameTv = (TextView) view.findViewById(R.id.village_name_tv);
		villageNameTv.setText(villageName);
	}
	
	/**
	 * 村委名称列表
	 * @return
	 */
	private String[] getVillageNameArr(){
		List<VillageListItem> villages = new ContractDAO(getActivity()).getVillage();
		String [] arr = new String[villages.size()+1];
		arr[0] = "点击选择";
		for(int i = 0 ; i < villages.size() ; i++){
			arr[i+1] = villages.get(i).getVillageName();
		}
		return arr;
	}
	
	/**
	 * 是否录入过此人信息
	 * @param contract
	 * @return
	 */
	private boolean isContractExist(Contract contract){
		if(contractDao.isExist(contract)){
			return true;
		}
		return false;
	}
	
	/**
	 * 填充基本信息
	 */
	private void setBaseInfo() {
		nameTv.setText(contract.getName());
		idTv.setText(contract.getIndentityCard());
	}
	
	/**
	 * 如果已录入，将原有数据填充到视图中
	 */
	public void setContractExistView(){
		setBaseInfo();
		if(villageNameTv != null){
			villageNameTv.setText(contract.getVillageName());
		}
		bankCardIdEt.setText(contract.getBankCardNo());
		
		currentPortraitName = orignalPortraitName = contract.getImage();
		Bitmap bitmap = BitmapTool.decodeSampledBitmapFromResource(Constant.IMG_PATH + orignalPortraitName, 100, 100);
		if(bitmap != null){
			portrait.setImageBitmap(bitmap);
		}
		
		currentBankcardName = orignalBankcardName = contract.getBankCard();
		Bitmap bankCardImg = BitmapTool.decodeSampledBitmapFromResource(Constant.IMG_PATH + orignalBankcardName, 200, 200);
		if(bankCardImg != null){
			bankCardIv.setVisibility(View.VISIBLE);
			bankCardIv.setImageBitmap(bankCardImg);
			bankCardIv.setClickable(true);
			delBankcardIv.setVisibility(View.VISIBLE);
		}
		if(contract.getPlantTYArea() != 0){
			plantTyAreaEt.setText(contract.getPlantTYArea()+"");
		}
		if(contract.getPlantDYArea() != 0){
			plantDyAreaEt.setText(contract.getPlantDYArea()+"");
		}
		
		familyMemSpinner.setSelection(getPostionAtArr(R.array.number, contract.getFamilyMembers()+""));
		workerNumSpinner.setSelection(getPostionAtArr(R.array.number, contract.getWorkers()+""));
		educationSpinner.setSelection(getPostionAtArr(R.array.education, contract.getEducation()));
		historyPlantSpinner.setSelection(getPostionAtArr(R.array.history_plant, contract.getPrevPlantType()));
	}
	
	/**
	 * 查找字符串在字符串数组中的位置
	 * @param arrId	字符串数组资源ID
	 * @param content 查找的字符串
	 * @return
	 */
	private int getPostionAtArr(int arrId,String content) {
		String [] arr = getResources().getStringArray(arrId);
		for(int i = 0 ; i < arr.length ; i++){
			if(arr[i].equals(content)){
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * 如果已经录入信息，提示时候要修改的对话框
	 */
	public void showAlertDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示！");
		builder.setMessage("此人信息已经录入，是否要修改？");
		builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				getActivity().finish();
			}
		});
		builder.setPositiveButton("修改", null);
		builder.setCancelable(false).create().show();
	}
	
	public static class MyHandler extends Handler{
		private WeakReference<FarmerShowFragment> ref;
		public MyHandler(FarmerShowFragment act){
			ref = new WeakReference<FarmerShowFragment>(act);
		}
		@Override
		public void handleMessage(Message msg) {
			FarmerShowFragment act = ref.get();
			if(act != null){
				switch(msg.what){
				case MyLocationManager.LOCATION_INFO:
					Map<String,Object> map = (Map<String, Object>) msg.obj;
					act.contract.setLongtitude(map.get("longtitude").toString());
					act.contract.setLatitude(map.get("latitude").toString());
					break;
				}
			}
		}
	}
	
	private ServiceConnection con = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			serMessenger = new Messenger(service);
			Message msg = Message.obtain();
			msg.what = ConnectionService.MSG_REGISTER_CLIENT;
			msg.replyTo = cMessenger;
			try {
				serMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	
	private void sendMessageToService(int what,int arg1,Object obj) {
		Message msg = Message.obtain();
		msg.what = what;
		msg.arg1 = arg1;
		msg.obj = obj;
		try {
			if(serMessenger != null){
				serMessenger.send(msg);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 初始界面
	 */
	public void initView(View view){
		
		nameTv = (TextView) view.findViewById(R.id.name);
		idTv = (TextView) view.findViewById(R.id.id);
		
		portrait = (ImageView) view.findViewById(R.id.portrait_iv);
		LinearLayout.LayoutParams params1 = (LayoutParams) portrait.getLayoutParams();
		params1.width = getResources().getDisplayMetrics().widthPixels / 4;
		params1.height = (int)(getResources().getDisplayMetrics().widthPixels / 4 * 1.2);
		portrait.setLayoutParams(params1);
		
		bankCardIdEt = (EditText) view.findViewById(R.id.bank_card_id);
		bankCardIv = (ImageView) view.findViewById(R.id.bankCard);
		
		RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) bankCardIv.getLayoutParams();
		int width = getResources().getDisplayMetrics().widthPixels/2;
		params.width = width;
		params.height = (int)(width/1.5);
		bankCardIv.setLayoutParams(params);
		bankCardIv.setOnClickListener(this);
		bankCardIv.setClickable(false);
		delBankcardIv = (ImageView) view.findViewById(R.id.del_view);
		delBankcardIv.setOnClickListener(this);
		
		plantTyAreaEt = (EditText) view.findViewById(R.id.plant_ty_area_et);
		plantDyAreaEt = (EditText) view.findViewById(R.id.plant_dy_area_et);
		
		setValidateList();
		
		portrait.setOnClickListener(this);
		
		takePicBtn = (Button) view.findViewById(R.id.take_pic);
		takePicBtn.setOnClickListener(this);
		saveBtn = (Button) view.findViewById(R.id.save_btn);
		saveBtn.setOnClickListener(this);

		familyMemSpinner = (Spinner) view.findViewById(R.id.family_members_spinner);
		workerNumSpinner = (Spinner) view.findViewById(R.id.worker_num_spinner);
		educationSpinner = (Spinner) view.findViewById(R.id.education_spinner);
		historyPlantSpinner = (Spinner) view.findViewById(R.id.history_plant_spinner);
		
		familyMemSpinner.setOnItemSelectedListener(this);
		workerNumSpinner.setOnItemSelectedListener(this);
		educationSpinner.setOnItemSelectedListener(this);
		historyPlantSpinner.setOnItemSelectedListener(this);
		
		familyMemSpinner.setAdapter(getArrayAdapter(getStringArr(R.array.number)));
		workerNumSpinner.setAdapter(getArrayAdapter(getStringArr(R.array.number)));
		educationSpinner.setAdapter(getArrayAdapter(getStringArr(R.array.education)));
		historyPlantSpinner.setAdapter(getArrayAdapter(getStringArr(R.array.history_plant)));
	}
	
	private String [] getStringArr(int id) {
		return getResources().getStringArray(id);
	}
	
	/**
	 * 初始化格式校验列表
	 */
	private void setValidateList(){
		setCannotBeEmptyValidateList();
		setFloatFormatValidateList();
		setPositiveIntegerList();
	}
	
	/**
	 * 非空列表
	 */
	private void setCannotBeEmptyValidateList(){
		cannotBeEmptyList.add(nameTv);
		cannotBeEmptyList.add(idTv);
	}
	
	/**
	 * 正数列表
	 */
	private void setFloatFormatValidateList(){
		patternList.add(plantTyAreaEt);
		patternList.add(plantDyAreaEt);
	}
	/**
	 * 正整数列表
	 */
	private void setPositiveIntegerList(){
	}
	
	
	/**
	 * 启动自定义相机
	 * @param type
	 * @param requestCode
	 */
	public void startMyCamera(String type,int requestCode){
		Intent intent = new Intent(getActivity(),CameraActivity.class);
		intent.putExtra("type",type);
		startActivityForResult(intent, requestCode);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancel:
			getActivity().finish();
			break;
		case R.id.take_pic:
			startMyCamera("bankcard",TACK_PICTRUE);
			break;
		case R.id.bankCard:
			Intent intent1 = new Intent(getActivity(),ShowBankCardActivity.class);
			intent1.putExtra("bankcard",currentBankcardName);
			startActivity(intent1);
			break;
		case R.id.portrait_iv:
			startMyCamera("portrait",TACK_POTRAIT_PIC);
			break;
		case R.id.save_btn:
			onSaveButtonClick();
			break;
		case R.id.birthday_btn:
			showDatePickerDialog();
			break;
		case R.id.del_view:
			delBankcardImage();
			break;
		}
	}
	
	private void delBankcardImage(){
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("提示！");
		builder.setMessage("确定要删除银行卡照片吗？");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				bankCardIv.setVisibility(View.GONE);
				delBankcardIv.setVisibility(View.GONE);
				FileTool.deleteImage(currentBankcardName);
				imageFileDAO.doDelete(currentBankcardName);
				currentBankcardName = "";
			}
		});
		builder.setCancelable(true);
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case FarmerShowFragment.TACK_PICTRUE:
			if(resultCode == Activity.RESULT_OK){
				String fileName = data.getStringExtra("fileName");
				bankCardIv.setImageBitmap(BitmapTool.decodeSampledBitmapFromResource(Constant.IMG_PATH + fileName, 200, 200));
				bankCardIv.setVisibility(View.VISIBLE);
				bankCardIv.setClickable(true);
				delBankcardIv.setVisibility(View.VISIBLE);
				if(currentBankcardName != orignalBankcardName){
					FileTool.deleteImage(currentBankcardName);
				}
				currentBankcardName = fileName;
			}
			break;
		case FarmerShowFragment.TACK_POTRAIT_PIC:
			if(resultCode == Activity.RESULT_OK){
				String fileName = data.getStringExtra("fileName");
				Bitmap bitmap = BitmapTool.decodeSampledBitmapFromResource(Constant.IMG_PATH + fileName,100, 100);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, baos);
				portrait.setImageBitmap(bitmap);
				if(currentPortraitName != orignalPortraitName){
					FileTool.deleteImage(currentPortraitName);
				}
				currentPortraitName = fileName;
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	/**
	 * 将数据插入数据库
	 */
	private void insertIntoDBAndFinish(){
		contract.setStatus(Contract.YES);
		String speakContent = "";
		if(contractDao.doInsert(contract)){
			FarmersListActivity.GTASKSREFRESH = true;
			FarmersListActivity.YESREFRESH = true;
			FarmersListActivity.NOREFRESH = true;
			speakContent = "签约信息保存成功";
			save = true;
		}else{
			speakContent = "签约信息保存失败";
			save = false;
		}
		Toast.makeText(getActivity(), speakContent, Toast.LENGTH_SHORT).show();
		sendMessageToService(ConnectionService.SPEAK, 0,speakContent);
		getActivity().finish();
	}

	
	/**
	 * 信息不全，提示不能保存的对话框
	 * @param msg
	 */
	private void showDiableSaveDialog(String msg) {
		TextView tv = new TextView(getActivity());
		tv.setPadding(10, 10, 10, 10);
		tv.setTextSize(17);
		tv.setTextColor(Color.WHITE);
		tv.setText(msg);
		dialog = new Dialog(getActivity(),R.style.CustomDialog);
		dialog.setContentView(tv);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}
	
	private void showDatePickerDialog(){
		DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				calendar.set(year, monthOfYear, dayOfMonth);
				birthdayBtn.setText(year+ "年" + (monthOfYear+1 >= 10 ? "" : "0")+(monthOfYear + 1) + "月"+ (dayOfMonth >= 10 ? "" : "0") + dayOfMonth + "日");
			}
		}, calendar.get(Calendar.YEAR) == currentYear ? calendar.get(Calendar.YEAR)-30 : calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
		datePickerDialog.getDatePicker().setMaxDate(Calendar.getInstance().getTime().getTime());
		datePickerDialog.getDatePicker().setCalendarViewShown(false);
		datePickerDialog.setTitle("出生日期");
		datePickerDialog.show();
	}
	
	/**
	 * 初始化popupwindow
	 * @param cx
	 * @param contentView
	 * @return
	 */
	private PopupWindow makePopupWindow(Context cx,View contentView) {
	    PopupWindow window;
	    window = new PopupWindow(cx);
	    //设置PopupWindow的内容view
	    window.setContentView(contentView);
	    window.setHeight(LayoutParams.WRAP_CONTENT);
	    window.setWidth(LayoutParams.WRAP_CONTENT);
	    //设置PopupWindow外部区域是否可触摸
	    //window.setOutsideTouchable(true);
	    return window;
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * 保留两个小数
	 * @param f
	 * @return
	 */
	private String formatString(float f){
		return String.format("%.2f",f);
	}

	public void onSaveButtonClick() {
//		if(from==0&&villageNameSpinner.getSelectedItemPosition() == 0){
//			Toast.makeText(getActivity(), "请先选择村委会", Toast.LENGTH_LONG).show();
//			return;
//		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.CHINA);
		String sign_time = sdf.format(new Date());
		
		String bankCardNo = bankCardIdEt.getText().toString().trim();
		String plantTyArea = plantTyAreaEt.getText().toString().trim();
		String plantDyArea = plantDyAreaEt.getText().toString().trim();
		
		//一下为非空字段
		String name = nameTv.getText().toString().trim();
		String id = idTv.getText().toString().trim();
		
		if(villageNameTv != null){
			contract.setVillageName(villageNameTv.getText().toString().trim());
		}
		
		if(contract.getVillageName() == null || contract.getVillageName().equals("点击选择")){
			showDiableSaveDialog("请选择所在村委！");
			return;
		}
		
		if(!ValidateTool.isEmptyValidate(cannotBeEmptyList,getString(R.string.error_message))){
			showDiableSaveDialog("请录入完整信息！");
			return;
		}
		
		//最多保留两位小数
		if(!ValidateTool.isFloateValidate(patternList,getString(R.string.error_message_num_format))){
			showDiableSaveDialog("注意数字格式！");
			return;
		}
		if(!ValidateTool.isPositiveIntegerValidate(positiveIntegerList,getString(R.string.error_message_num_format))){
			showDiableSaveDialog("注意数字格式！");
			return;
		}
		
		if(plantTyArea.equals("") && plantDyArea.equals("")){
			showDiableSaveDialog("请录入种植信息！");
			return;
		}
		
		if(addressEt != null){
			contract.setAddress(addressEt.getText().toString());
		}
		
		if(!plantTyArea.equals("")){
			contract.setPlantTYArea(Double.parseDouble(plantTyArea));
		}else{
			contract.setPlantTYArea(0);
		}
		if(!plantDyArea.equals("")){
			contract.setPlantDYArea(Double.parseDouble(plantDyArea));
		}else{
			contract.setPlantDYArea(0);
		}
		if(orignalBankcardName != null && !orignalBankcardName.equals(currentBankcardName)){
			FileTool.deleteImage(orignalBankcardName);
			imageFileDAO.doDelete(orignalBankcardName);
		}
		if(orignalPortraitName != null && !orignalPortraitName.equals(currentPortraitName)){
			FileTool.deleteImage(orignalPortraitName);
			imageFileDAO.doDelete(orignalPortraitName);
		}
		contract.setBankCard(currentBankcardName);
		contract.setImage(currentPortraitName);
		
		contract.setName(name);
		contract.setIndentityCard(id);
		
		contract.setSignTime(sign_time);
		contract.setBankCardNo(bankCardNo);
		if(from==1&&contractDao.isExist(contract)){
			String orignalName = contractDao.findById(contract.getIndentityCard()).getName();
			final int Id = contractDao.findById(contract.getIndentityCard()).getId();
			Builder builder = new Builder(getActivity());
			builder.setTitle("提示");
			builder.setMessage("身份证号 "+contract.getIndentityCard()+"("+orignalName+")"+"已经存在,是否改为 "+contract.getName()+" ?");
			builder.setPositiveButton("修改",new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					contract.setId(Id);
					insertIntoDBAndFinish();
				}
			});
			builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					getActivity().finish();
				}
			});
			builder.show();
		}else{
			insertIntoDBAndFinish();
		}
	}
	@Override
	public void onDestroy() {
		sendMessageToService(ConnectionService.MSG_UNREGISTER_CLIENT, 0, null);
		getActivity().unbindService(con);
		if(!save){
			if(currentBankcardName != null && !currentBankcardName.equals(orignalBankcardName)){
				FileTool.deleteImage(currentBankcardName);
			}
			if(currentPortraitName != null && !currentPortraitName.equals(orignalPortraitName)){
				FileTool.deleteImage(currentPortraitName);
			}
		}
		super.onDestroy();
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		if(view == null){
			return;
		}
		String content = ((TextView)view).getText().toString().trim();
		switch(parent.getId()){
		case R.id.family_members_spinner:
			if(!content.equals("点击选择")){
				contract.setFamilyMembers(Integer.parseInt(content));
			}
			break;
		case R.id.worker_num_spinner:
			if(!content.equals("点击选择")){
				contract.setWorkers(Integer.parseInt(content));
			}
			break;
		case R.id.education_spinner:
			contract.setEducation(content);
			break;
		case R.id.village_name_spinner:
			contract.setVillageName(content);
			break;
		case R.id.national_spinner:
			contract.setNational(content);
			break;
		case R.id.history_plant_spinner:
			contract.setPrevPlantType(content);
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}
}
