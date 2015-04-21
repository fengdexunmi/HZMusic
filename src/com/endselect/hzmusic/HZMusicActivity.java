package com.endselect.hzmusic;

import java.io.IOException;
import java.util.List;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.endselect.hzmusic.R;
import com.endselect.hzmusic.common.Constant;
import com.endselect.hzmusic.common.Player;
import com.endselect.hzmusic.model.SongInfo;
import com.endselect.hzmusic.render.CircleBarRenderer;
import com.endselect.hzmusic.server.Server;
import com.endselect.hzmusic.view.CircleImageView;
import com.endselect.hzmusic.view.RoundProgressView;
import com.endselect.hzmusic.view.VisualizerView;
import com.squareup.picasso.Picasso;

/**
 * 
 * @author frankfang
 * @version 2015年4月21日 下午4:00:43
 * @Description 主Activity
 */
public class HZMusicActivity extends ActionBarActivity {

	private Context context;
	private CircleImageView coverView; //圆形封面视图
	private RoundProgressView progressView; //圆形进度
	private VisualizerView visualizerView; //音频可视化
	private ImageView pauseView; //暂停
	private TextView tvSongName; //歌曲名
	private TextView tvSinger; //歌手名
	private Player player; //播放器
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		context = HZMusicActivity.this;
		initView();
		initAction();
		
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月14日 下午12:21:08
	 * @Description 初始化视图
	 */
	private void initView() {
		coverView = (CircleImageView) findViewById(R.id.civ_music_cover);
		progressView = (RoundProgressView) findViewById(R.id.rgp_music_progress);
		visualizerView = (VisualizerView) findViewById(R.id.visual_view);
		pauseView =(ImageView) findViewById(R.id.iv_music_pause);
		tvSongName = (TextView) findViewById(R.id.tv_song_name);
		tvSinger = (TextView) findViewById(R.id.tv_singer);
		
		//开启获取歌曲的线程
		new GetSongFromUrl().execute();
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月20日 下午5:11:04
	 * @Description 
	 */
	private void initAction() {
		
		//点击封面（暂停或者继续播放音乐）
		coverView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(player != null) {
					//如果正在播放，点击封面暂停音乐
					if(!player.getPause() && player.mediaPlayer.isPlaying()) {
						player.pause();
						pauseView.setVisibility(View.VISIBLE);
					}
				}
			}
		});
		
		
		pauseView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//如果暂停，点击封面播放
				if(player.getPause()) {
					player.pause();
					pauseView.setVisibility(View.GONE);
				}
			}
		});
		
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月15日 上午10:18:59
	 * @Description 异步线程获取歌曲
	 */
	private class GetSongFromUrl extends AsyncTask<Void, Void, List<SongInfo>> {

		@Override
		protected List<SongInfo> doInBackground(Void... params) {
			List<SongInfo> songInfos = Server.getServerInstance().getSongInfos(Constant.MEDIA_URL);
			
			return songInfos;
		}
		
		@Override
		protected void onPostExecute(List<SongInfo> result) {
			super.onPostExecute(result);
			if(result != null && result.size() > 0) {
				SongInfo songInfo = result.get(0);
				String musicUrl = songInfo.getUrl();
				tvSongName.setText(songInfo.getTitle());
				tvSongName.setVisibility(View.VISIBLE);
				tvSinger.setText(songInfo.getArtist());
				tvSinger.setVisibility(View.VISIBLE);
				player = new Player(musicUrl, progressView);
				player.play();
				Picasso.with(context).load(songInfo.getPicture()).into(coverView);
				visualizerView.link(player.mediaPlayer);
				addCircleBarRenderer();
			}
		}
		
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月21日 下午6:02:59
	 * @Description 开启渲染器
	 */
	private void addCircleBarRenderer() {
		Paint paint = new Paint();
		paint.setStrokeWidth(8f);
		paint.setAntiAlias(true);
		paint.setXfermode(new PorterDuffXfermode(Mode.LIGHTEN));
		paint.setColor(Color.argb(255, 222, 92, 143));
		CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
		visualizerView.addRenderer(circleBarRenderer);
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月21日 下午6:03:24
	 * @Description 清除渲染器
	 */
	private void clearRenderer() {
		visualizerView.clearRenderers();
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月20日 下午6:02:17
	 * @Description 按下返回键
	 */
	@Override
	public void onBackPressed() {
		//返回不退出
		moveTaskToBack(true);
	}
	
	@Override
	protected void onDestroy() {
		cleanUp();
		super.onDestroy();
	}
	
	
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月21日 下午5:56:19
	 * @Description 释放资源
	 */
	private void cleanUp() {
		if(player != null) {
			visualizerView.release();
			player.mediaPlayer.release();
			player.mediaPlayer = null;
			player = null;
		}
	}
	
	
	/*
	 * 设置壁纸
	 */
	private void setWallPaper() {
		
		Bitmap bmap2 =BitmapFactory.decodeStream(getResources().openRawResource(R.drawable.wall4));
	    DisplayMetrics metrics = new DisplayMetrics(); 
	    getWindowManager().getDefaultDisplay().getMetrics(metrics);
	    int height = metrics.heightPixels; 
	    int width = metrics.widthPixels;
	    System.out.println("bmap2 width " + bmap2.getWidth() + " height " + bmap2.getHeight());
	    Bitmap bitmap = Bitmap.createScaledBitmap(bmap2, width, height, true); 
	    System.out.println("bitmap width " + bitmap.getWidth() + " height " + bitmap.getHeight());
	    WallpaperManager wallpaperManager = WallpaperManager.getInstance(this); 
	    wallpaperManager.setWallpaperOffsetSteps(1, 1);
	    wallpaperManager.suggestDesiredDimensions(width, height);
	    try {
	      wallpaperManager.setBitmap(bitmap);
	      } catch (IOException e) {
	      e.printStackTrace();
	    }
	}

}
