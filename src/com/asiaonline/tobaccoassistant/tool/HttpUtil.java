package com.asiaonline.tobaccoassistant.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.asiaonline.tobaccoassistant.Constant;
import com.asiaonline.tobaccoassistant.entity.Contract;
import com.asiaonline.tobaccoassistant.frag.SetFragment;

public class HttpUtil {
	/**
	 * @description 检查版本，得到版本号
	 * */
	public static String checkUpdate(String u,Handler handler){
		String result = "";
		URL url;
		try {
			url = new URL(u);
			HttpURLConnection connection = null;
			connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(6*1000);
			connection.connect();
			if(connection.getResponseCode()==200){
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));  
	            String line = null;  
	            StringBuilder builder = new StringBuilder();  
	            while (null != (line = bufferedReader.readLine())) {  
	                builder.append(line);  
	            }  
				result = builder.toString();
			}else{
				handler.sendMessage(handler.obtainMessage(Constant.CHECKUPDATEFAILED,"网络请求失败"));
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch (ConnectTimeoutException e) {
			handler.sendMessage(handler.obtainMessage(Constant.CHECKUPDATEFAILED,"网络连接超时"));
		}catch (IOException e) {
			e.printStackTrace();
			handler.sendMessage(handler.obtainMessage(Constant.CHECKUPDATEFAILED,"连接失败"));
		}catch (Exception e) {
		}
		return result;
	}
	
	/**@description 下载新版本安装包
	 * */
	public static void DownLoadFile(String url,String path,String name,Handler handler){
		try {
			URL url2 = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
			connection.setConnectTimeout(6*1000);
			connection.setReadTimeout(10*1000);
			connection.connect();
			long length = connection.getContentLength();
			InputStream is = connection.getInputStream();
			if (is != null) {
				File file = new File(path);
				if(!file.exists()){
					file.mkdirs();
				}
				File apk = new File(path+name);
				if(apk.exists()){
					apk.delete();
				}
				FileOutputStream fos = new FileOutputStream(path+name);
				int read;
				long count = 0;
				int precent = 0;
				byte[] buffer = new byte[1024];
				while ((read = is.read(buffer)) != -1&&!SetFragment.cancal) {
					fos.write(buffer, 0, read);
					count += read;
					precent = (int) (((double) count / length) * 100);
					handler.sendMessage(handler.obtainMessage(Constant.VERSIONPROGRESS, precent,0));
				}
				fos.flush();
				fos.close();
				is.close();
				if(!SetFragment.cancal){
					handler.sendEmptyMessage(Constant.VERSIONDOWNSUCESSED);
				}else{
					handler.sendEmptyMessage(Constant.CANCAL);
				}
				
			}else{
				handler.sendEmptyMessage(Constant.VERSIONDOWNFAILED);
			}

		} catch (ClientProtocolException e) {
			handler.sendEmptyMessage(Constant.VERSIONDOWNFAILED);
			e.printStackTrace();
		} catch (IOException e) {
			handler.sendEmptyMessage(Constant.VERSIONDOWNFAILED);
			e.printStackTrace();
		}catch (Exception e) {
			handler.sendEmptyMessage(Constant.VERSIONDOWNFAILED);
		}
		
	}
	/**根据年份和烟站获取烟农
	 * */
	public static String doGet(String path,String year,String yanzhan,Handler handler){
		String result = "";
		String url;
		try {
			url = path+"?year="+URLEncoder.encode(year,HTTP.UTF_8)+"&yanzhan="+URLEncoder.encode(yanzhan, HTTP.UTF_8);
			HttpGet httpGet = new HttpGet(url);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpGet);
			if(response.getStatusLine().getStatusCode()==200){
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity, HTTP.UTF_8);
			}else{
				handler.sendMessage(handler.obtainMessage(Constant.GETFARMERSFAILED,"网络请求失败"));
			}
			
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			handler.sendMessage(handler.obtainMessage(Constant.GETFARMERSFAILED,"网络请求失败"));
			e.printStackTrace();
		} catch (IOException e) {
			handler.sendMessage(handler.obtainMessage(Constant.GETFARMERSFAILED,"获取数据失败"));
			e.printStackTrace();
		}catch (Exception e) {
			handler.sendMessage(handler.obtainMessage(Constant.GETFARMERSFAILED,"获取数据失败"));
		}
		return result;
		
	}
	/**上传数据库文件
	 * */
	public static boolean uploadDB(String url,String path,String name){
		boolean is = false;
		File file = new File(path+name);
		if(file.exists()){
			HttpPost httpPost = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			FileBody body = new FileBody(file);
			entity.addPart(name,body);
			httpPost.setEntity(entity);
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(httpPost);
				if(response.getStatusLine().getStatusCode()==200){
					is = true;
				}else{
					is = false;
				}
			} catch (ClientProtocolException e) {
				is = false;
				e.printStackTrace();
			} catch (IOException e) {
				is = false;
				e.printStackTrace();
			}catch (Exception e) {
				is = false;
			}
		}
		return is;
	}
	/**上传图片
	 * */
	public static void uploadImage(String url,String path,String name,UploadCallback callback){
		File file = new File(path+name);
		if(file.exists()){
			HttpPost httpPost = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			FileBody body = new FileBody(file);
			entity.addPart(name,body);
			httpPost.setEntity(entity);
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(httpPost);
				if(response.getStatusLine().getStatusCode()==200){
					callback.onSuccess();
				}else{
					callback.onFailed();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
				callback.onFailed();
			} catch (IOException e) {
				e.printStackTrace();
				callback.onFailed();
			}catch (Exception e) {
				callback.onFailed(); 
			}
		}
	}
	/**json解析，得到烟农集合
	 * */
	public static List<Contract> getContractsFromJson(String jsonStr){
		List<Contract> list = new ArrayList<Contract>();
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONArray array = jsonObject.getJSONArray("farmers");
			for(int i=0;i<array.length();i++){
				JSONObject jsonObject2 = array.getJSONObject(i);
				String name = jsonObject2.getString("name");
				String gender = jsonObject2.getString("gender");
				String image = jsonObject2.getString("image");
				String indentityCard = jsonObject2.getString("indentityCard");
				String birthday = jsonObject2.getString("birthday");
				String national = jsonObject2.getString("national");
				String address = jsonObject2.getString("address");
				String villageName = jsonObject2.getString("villageName");
				int familyMembers = jsonObject2.getInt("familyMembers");
				int workers = jsonObject2.getInt("workers");
				String education = jsonObject2.getString("education");
				String prevPlantType = jsonObject2.getString("prevPlantType");
				double plantTYArea = jsonObject2.getDouble("plantTYArea");
				double plantDYArea = jsonObject2.getDouble("plantDYArea");
				String bankCard = jsonObject2.getString("bankCard");
				String bankCardNo = jsonObject2.getString("bankCardNo");
				String signTime = jsonObject2.getString("signTime");
				String longtitude = jsonObject2.getString("longtitude");
				String latitude = jsonObject2.getString("latitude");
				int status = jsonObject2.getInt("status");
				list.add(new Contract(name, gender, image, indentityCard, birthday, national, address, villageName, familyMembers, workers, education, prevPlantType, plantTYArea, plantDYArea, bankCard, bankCardNo, signTime, longtitude, latitude, status));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**从txt文件中读取数据，供测试用
	 * */
	public static String getJsonStr(Context context){
		BufferedReader bufferedReader = null;  
		String str = "";
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open("farmers.txt"),"GBK"));  
            String line = null;  
            StringBuilder builder = new StringBuilder();  
            while (null != (line = bufferedReader.readLine())) {  
                builder.append(line);  
            }  
            str = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * 判断当前有无可用的网络
	 * @param context
	 * @return
	 */
	public static Boolean isNetOpen(Context context)
	{
		ConnectivityManager connectivity = 
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo netInfo=connectivity.getActiveNetworkInfo();
			if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
			{
				return true;
			}
		}
		return false;
	}
	
	public interface UploadCallback{
		void onSuccess();
		void onFailed();
	}
}
