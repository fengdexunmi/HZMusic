/**
 * 
 */
package com.endselect.hzmusic.server;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.endselect.hzmusic.model.SongInfo;
import com.endselect.hzmusic.util.HttpUtil;

/**
 * @author frankfang
 * @version 2015��4��14�� ����2:21:06
 * @Description �������������
 */
public class Server {
	
	public static final String TAG = Server.class.getName();
	
	/*
	 * ����ģʽ
	 */
	private static Server server = new Server();
	private Server() {}
	public static Server getServerInstance() {
		return server;
	}
	
	/**
	 * ���������ȡ������Ϣ
	 */
	public List<SongInfo> getSongInfos(String songInfoUrl) {
		List<SongInfo> songInfos = null;
		
		String song = HttpUtil.getHttpInstance().doGetRequest(songInfoUrl);
		if(!TextUtils.isEmpty(song)) {
			try {
				JSONObject jsonObject = new JSONObject(song);
				JSONArray jsonArray = jsonObject.getJSONArray("song");
				if(jsonArray != null && jsonArray.length() > 0) {
					songInfos = new ArrayList<SongInfo>();
					for(int i = 0; i < jsonArray.length(); i++) {
						SongInfo songInfo = new SongInfo();
						songInfo.setArtist(jsonArray.getJSONObject(i).getString("artist"));
						songInfo.setPicture(jsonArray.getJSONObject(i).getString("picture"));
						songInfo.setTitle(jsonArray.getJSONObject(i).getString("title"));
						songInfo.setUrl(jsonArray.getJSONObject(i).getString("url"));
						songInfos.add(songInfo);
					}
					
					Log.i(TAG, songInfos.toString());
					return songInfos;
				}
			} catch (Exception e) {
				Log.i(TAG, e.toString());
				return null;
			}
		}
		
		return songInfos;
	}
}
