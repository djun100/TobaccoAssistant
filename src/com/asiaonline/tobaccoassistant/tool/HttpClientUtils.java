package com.asiaonline.tobaccoassistant.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

/**
 * HttpClient请求
 * @author zhudf
 */
public class HttpClientUtils {

	/* HttpClient的基本使用（以POST请求为例）：
	 *  
	 * 1）创建HttpClient实例（类似于浏览器客户端）； 
	 * 	  HttpClient client = new DefaultHttpClient();
	 * 
	 * 2）创建HttpPost请求，需要向HttpPost的构造方法传入所请求的URL；
	 * 	  HttpPost post = new HttpPost(requestUrl);
	 * 
	 * 3）发出POST请求（调用HttpClient的execute()方法，execute()的参数为HttpPost实例）；
	 * 	  HttpResponse response = client.execute(post); 
	 * 
	 * 4）读取返回结果； 
	 * 5）释放连接；
	 * 6）对返回的结果进行处理。
	 */
	/**
	 * 登录 HttpGet
	 * @param String url
	 * @param String data
	 */
	public static String login(String url, String data) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url + "?" + data);
		
		String str = null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if(entity != null) {
					str = EntityUtils.toString(entity);
				}
			} else {
				return str;
			}
			return str;
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			httpget.abort();
			httpclient.getConnectionManager().shutdown();
		}
		return str;
	}
	/**
	 * 登录 HttpPost
	 * 解决中文的问题 key-value
	 * Map<String, String> params = new HashMap<String, String>
	 * params.put("name", "admin");
	 * params.put("password", "111111");
	 */
	public static String loginByPost(String url, Map<String,String> params) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		Iterator it = params.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			nvps.add(new BasicNameValuePair(key, params.get(key)));
		}
		String str = null;
		try {
			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response;
			response = client.execute(post);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (entity != null) {
					str = EntityUtils.toString(entity);
				}
			} else {
				return str;
			}
			return str;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 上传图片等文件
	 */
	public static String upload(Context context, String url, Map<String,String> params, String name,String path) {
		File file = new File(path);
		if (!file.exists()) {
			Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
			return null;
		}
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		
		String str = null;
		try {
			MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			Iterator it = params.keySet().iterator();
			while(it.hasNext()) {
				String key = (String) it.next();
				mulentity.addPart(key, new StringBody(params.get(key)));
			}
			// 添加图片表单数据
			FileBody filebody = new FileBody(file);
			mulentity.addPart(name, filebody);
			httppost.setEntity(mulentity);
			
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if(entity != null) {
					str = EntityUtils.toString(entity);
				}
			} else {
				return str;
			}
			return str;
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			httppost.abort();
			httpclient.getConnectionManager().shutdown();
		}
		return str;
	}
	/**
	 * 下载
	 */
	public static boolean download(Context context, String url, String path, String filename) {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (entity != null) {
					InputStream in = entity.getContent();
					
					path = path + "/" + filename;
					File file = new File(path);
					if(!file.exists())
						file.createNewFile();
					FileOutputStream fos = new FileOutputStream(new File(path));
					int len;
					byte[] buffer = new byte[1024];
					while ((len = in.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
					}
					fos.flush();
					fos.close();
					in.close();
				}
			} else {
				return false;
			}
			entity.consumeContent();
			// 安装apk
//			installApk(context, path);
			return true;
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			httpget.abort();
			httpclient.getConnectionManager().shutdown();
		}
		return false;
	}
	/**
	 * 上传错误日志
	 */
	public static int uploadErrorLog(Context context, File file, String url, String deviceID, String version, String userName) {
		if (!file.exists()) {
//			Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
			return -404;
		}
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		
		String str = null;
		try {
			MultipartEntity mulentity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			mulentity.addPart("did", new StringBody(deviceID));
			mulentity.addPart("appver", new StringBody(version));
			mulentity.addPart("uid", new StringBody(userName));
			
			// 添加图片表单数据
			FileBody filebody = new FileBody(file);
			mulentity.addPart("file", filebody);
			httppost.setEntity(mulentity);
			
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return 100;
			} else {
				return -100;
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} finally {
			httppost.abort();
			httpclient.getConnectionManager().shutdown();
		}
		return -100;
	
	}
	
	// ************************************************* //
	/**
	 * 安装apk
	 */
	private static void installApk(Context context, String filePath) {
		Intent intent = new Intent(Intent.ACTION_VIEW); 
	    intent.setDataAndType(Uri.parse("file://" + filePath),"application/vnd.android.package-archive"); 
	    context.startActivity(intent);
	}
}
