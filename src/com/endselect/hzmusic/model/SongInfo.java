package com.endselect.hzmusic.model;

/**
 * @author frankfang
 * @version 2015年4月14日 下午12:29:31
 * @Description 歌曲信息
 */
public class SongInfo {
	
	/*
	http://douban.fm/j/mine/playlist?channel=0 
	"album": "/subject/26327960/",
	"picture": "http://img3.douban.com/lpic/s28010003.jpg",
	"ssid": "cd86",
	"artist": "Róisín Murphy",
	"url": "http://mr4.douban.com/201504141227/c8a439942e5a183fd9d4211b15d6b3fb/view/song/small/p2431744.mp3",
	"company": "PIAS America",
	"title": "Exile",
	"rating_avg": 3.42863,
	"length": 240,
	"subtype": "",
	"public_time": "2015",
	"songlists_count": 0,
	"sid": "2431744",
	"aid": "26327960",
	"sha256": "4df2a636f2a8a73b4d086e7002066ab211f3f0f7d183180f9d923dd86411e295",
	"kbps": "64",
	"albumtitle": "Hairless Toys",
	"like": "0"*/
	
	private String picture; //封面
	private String url; //音频 url
	private String artist; //歌手名
	private String title; //歌曲名
	
	public String getPicture() {
		return picture;
	}
	public void setPicture(String picture) {
		this.picture = picture;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
		
		return "[picture: " + picture + ", url: " + url 
				+ ", artist: " + artist + ", title: " + title + "]";
	}
	
}
