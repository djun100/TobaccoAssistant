package com.asiaonline.tobaccoassistant;

import android.os.Environment;

/**常量类
 * */
public class Constant {
	public static String yanzhandefault = "烟站";
	//烟站名
	public static String yanzhan = "烟站";
	//烟站单位代码
	public static String yanzhanCode = "";
	//数据库名称
	public static final String DBNAME = "asiadb.db";
	//数据库烟农表名称
	public static final String TABLENAME = "farmers";
	//村委表名
	public static final String VILLAGE = "villages";
	//前作品种表名
	public static final String PREVPLANTTYPE = "prevPlantType";
	//图片表名
	public static final String IMAGEFILE = "images";
	
	//网络交互相关message
	public static final int CANUPDATE = 1; //有新版本消息
	public static final int NOUPDATE = 2;  //无新版本消息
	public static final int CHECKUPDATEFAILED = 22;//版本检查更新失败
	public static final int GETFARMERSSUCESSED = 3; //获取数据成功消息
	public static final int GETFARMERSFAILED = 4; //获取数据失败消息
	public static final int UPLOADSUCESSED = 5; //上传成功消息
	public static final int UPLOADFAILED = 6;	//上传失败消息
	public static final int VERSIONPROGRESS = 7;  //版本下载进度消息
	public static final int DOWNPROGRESS = 8;	//数据获取进度消息
	public static final int VERSIONDOWNSUCESSED = 9; //版本下载成功消息
	public static final int VERSIONDOWNFAILED = 10; //版本下载失败消息
	public static final int CANCAL = 11;   //取消更新消息
	public static final int NETERROR = 30;//网络异常消息
	//图片存放路径
	public static final String IMG_PATH = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/IdentityCard/image/";
	//APK文件路径
	public static final String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/IdentityCard/apk/";
	//APK文件名称
	public static final String apkName = "IdentityCard.apk";
	
	/******************************************************************/
	//检查版本更新URL
	public static final String CHECKUPDATE_URL = "http://rss.sina.com.cn/news/china/focus15.xml"; 
	//版本下载URL
//	public static final String DOWNLOADVERSION_URL = "http://192.168.0.119:8080/IdentityCard_1.2.apk";
	public static final String DOWNLOADVERSION_URL = "http://zhaof.u.qiniudn.com/IdentityCard_1.2.apk";
	//获取烟农URL
	public static final String GETFARMERS_URL = "";
	//文件上传URL
	public static final String UPLOAD_URL = "";
}
