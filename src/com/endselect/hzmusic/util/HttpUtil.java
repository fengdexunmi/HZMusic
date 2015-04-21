package com.endselect.hzmusic.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * @author frankfang
 * @version 2015年4月14日 下午2:37:31
 * @Description 网络工具类
 */
public class HttpUtil {

	public static final String TAG = HttpUtil.class.getName();
	
	/*
	 * 单例模式
	 */
	private static HttpUtil httpUtil = new HttpUtil();
	private HttpUtil(){}
	public static HttpUtil getHttpInstance() {
		return httpUtil;
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月14日 下午5:25:43
	 * @Description GET请求
	 * @param httpUrl
	 * @return
	 */
	public String doGetRequest(String httpUrl) {
		Log.i(TAG, "doGetRequest " + httpUrl);
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type","application/json; charset=utf-8");
			InputStream in = null;
			StringBuffer sb = new StringBuffer();
			Log.i(TAG, "doGetRequest " + connection.getResponseCode());
			if(connection.getResponseCode() == 200){
				byte[] buf = new byte[1024];  
                in = connection.getInputStream();  
                for (int n; (n = in.read(buf)) != -1;) {  
                    sb.append(new String(buf, 0, n, "UTF-8"));  
                } 
	         }
			in.close();  
            connection.disconnect();
	             
			Log.i(TAG, "doGetRequest " + sb.toString());
			return sb.toString();
			
		} catch (Exception e) {
			Log.i(TAG, "doGetRequest " + "GET请求出现异常");
			e.printStackTrace();
			return null;
		}
		
	}
}
