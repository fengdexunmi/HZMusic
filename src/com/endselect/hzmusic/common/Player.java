/**
 * 
 */
package com.endselect.hzmusic.common;

import java.util.Timer;
import java.util.TimerTask;

import com.endselect.hzmusic.view.RoundProgressView;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.widget.SeekBar;

/**
 * @author frankfang
 * @version 2015年4月14日 上午10:47:16
 * @Description 播放器
 */
public class Player implements OnPreparedListener, OnBufferingUpdateListener, OnCompletionListener {
	
	public MediaPlayer mediaPlayer;
	private RoundProgressView seekBar;
	private String mediaUrl;
	private boolean pause;
	private int playPosition;
	private Timer mTimer = new Timer(); //定时器
	
	public Player(String mediaUrl, RoundProgressView seekBar) {
		this.mediaUrl = mediaUrl;
		this.seekBar = seekBar;
		
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnBufferingUpdateListener(this);
		
		mTimer.schedule(mTimerTask, 0, 1000); //每一秒更新一次进度
	}
	
	/**
	 * 通过定时器和Handler来更新进度条
	 */
	TimerTask mTimerTask = new TimerTask() {
		
		@Override
		public void run() {
			if(mediaPlayer == null) {
				return;
			}
			if(mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
				handler.sendEmptyMessage(0);
			}
		}
	};
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int position = mediaPlayer.getCurrentPosition(); //当前播放进度(毫秒)
			int duration = mediaPlayer.getDuration(); //文件总长度(毫秒)
			if(duration > 0) {
				long pos = seekBar.getmMaxProgress() * position / duration;
				seekBar.setmCurrentProgress((int) pos);
			}
		};
	};
	
	/**
	 * 播放
	 */
	public void play() {
		playMedia(0);
	}
	
	/**
	 * 暂停
	 */
	public boolean pause() {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			pause = true;
		} else {
			if(pause) {
				mediaPlayer.start();
				pause = false;
			}
		}
		
		return pause;
	}
	
	/**
	 * 重播
	 */
	public void replay() {
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.seekTo(0);
		} else {
			playMedia(0);
		}
	}
	
	/**
	 * 停止
	 */
	public void stop() {
		if(mediaPlayer != null && mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		
	}
	
	private void playMedia(int playPosition) {
		mediaPlayer.reset();
		
		try {
			mediaPlayer.setDataSource(mediaUrl);
			mediaPlayer.prepare();
		} catch (Exception e) {
			return;
		}
		
		mediaPlayer.setOnPreparedListener(new CustomPreparedListener(playPosition));
	}
	
	private class CustomPreparedListener implements OnPreparedListener {
		
		private int pausePos; //暂停时的播放进度(毫秒)
		
		public CustomPreparedListener(int pausePos) {
			this.pausePos = pausePos;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			if(mediaPlayer != null) {
				mediaPlayer.start();
			}
			
			if(pausePos > 0) {
				mediaPlayer.seekTo(pausePos);
			}
		}
		
	}
	
	/**
	 * @author frankfang
	 * @version 2015年4月20日 下午5:45:09
	 * @Description TODO
	 */
	public boolean getPause() {
		return pause;
	}

}
