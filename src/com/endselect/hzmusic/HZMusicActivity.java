package com.endselect.hzmusic;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.endselect.hzmusic.common.Constant;
import com.endselect.hzmusic.common.Player;
import com.endselect.hzmusic.model.SongInfo;
import com.endselect.hzmusic.render.CircleBarRenderer;
import com.endselect.hzmusic.server.Server;
import com.endselect.hzmusic.view.CircleImageView;
import com.endselect.hzmusic.view.RoundProgressView;
import com.endselect.hzmusic.view.SlidingUpPanelLayout;
import com.endselect.hzmusic.view.SlidingUpPanelLayout.PanelSlideListener;
import com.endselect.hzmusic.view.VisualizerView;
import com.squareup.picasso.Picasso;

/**
 * 
 * @author frankfang
 * @version 2015年4月21日 下午4:00:43
 * @Description 主Activity
 */
public class HZMusicActivity extends Activity {
	
	private static final String TAG = HZMusicActivity.class.getSimpleName();

	private Context context;
	private CircleImageView coverView; //圆形封面视图
	private RoundProgressView progressView; //圆形进度
	private VisualizerView visualizerView; //音频可视化
	private SlidingUpPanelLayout slidingPanel; //滑动面板
	private LinearLayout btnBarLayout; //音频控制按钮布局块
	private FrameLayout musicViewLayout;
	private ImageView pauseView; //暂停
	private TextView tvSongName; //歌曲名
	private TextView tvSinger; //歌手名
	private Player player; //播放器
	
	/*面板滑动时需要计算的偏移*/
	private float coverXSlop;
	private float coverXDelta;
	private float coverYSlop;
	private float coverYDelta;
	private float coverScaleSlop;
	private float coverScaleDelta;
	private float btnBarXSlop;
	private float btnBarXDelta;
	private float btnBarYSlop;
	private float btnBarYDelta;
	private float btnBarScaleSlop;
	private float btnBarScaleDelta;
	private RectF rectF1 = new RectF();
	private RectF rectF2 = new RectF();
	private float musicLayoutLeft;
	private float musicLayoutRight;
	private float musicLayoutTop;
	private float musicLayoutBottom;
	
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
		slidingPanel = (SlidingUpPanelLayout) findViewById(R.id.sliding_panel_layout);
		musicViewLayout = (FrameLayout) findViewById(R.id.fl_music_view);
		btnBarLayout = (LinearLayout) findViewById(R.id.ll_btn_bar);
		pauseView =(ImageView) findViewById(R.id.iv_music_pause);
		tvSongName = (TextView) findViewById(R.id.tv_song_name);
		tvSinger = (TextView) findViewById(R.id.tv_singer);
		
		measureComponentSize();
		
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
		
		//监听滑动面板
		slidingPanel.setPanelSlideListener(new PanelSlideListener() {
			
			@Override
			public void onPanelSlide(View panel, float slideOffset) {
				updatePositionOnSlide(slideOffset);
				updateAlphaOnSlide(slideOffset);
			}
			
			@Override
			public void onPanelHidden(View panel) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPanelExpanded(View panel) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPanelCollapsed(View panel) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPanelAnchored(View panel) {
				// TODO Auto-generated method stub
				
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
				Picasso.with(context).load(songInfo.getPicture()).into(coverView);
				player = new Player(musicUrl, progressView);
				player.play();
				visualizerView.link(player.mediaPlayer);
				addCircleBarRenderer();
			}
		}
		
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月23日 下午12:00:20
	 * @Description 测量组件大小（musicLayout + btnBar）
	 */
	private void measureComponentSize() {
		final float density = getResources().getDisplayMetrics().density;
		final int screenWidth = getResources().getDisplayMetrics().widthPixels;
		Log.i(TAG, "screenWidth " + screenWidth);
		// Calculate ActionBar height
		TypedValue tv = new TypedValue();
		int actionBarHeight = 0;
		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
		{
		    actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
		}
		final int screenHeight = getResources().getDisplayMetrics().heightPixels - actionBarHeight;
		Log.i(TAG, "screenHeight " + actionBarHeight + " " + screenHeight);
		
		musicViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
					musicViewLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					musicViewLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
				
				musicLayoutLeft = musicViewLayout.getLeft();
				musicLayoutRight = musicViewLayout.getRight();
				musicLayoutTop = musicViewLayout.getTop();
				musicLayoutBottom = musicViewLayout.getBottom();
				Log.i(TAG, "musicLayout " + musicLayoutLeft + " " + musicLayoutRight + " " + musicLayoutTop + " " + musicLayoutBottom);
				rectF1.set(musicLayoutLeft, musicLayoutTop, musicLayoutRight, musicLayoutBottom);
				
				float musicLayoutRightTo = getResources().getDimension(R.dimen.panel_height) * density;
				float musicLayoutTopTo = screenHeight - getResources().getDimension(R.dimen.panel_height) * density;
				rectF2.set(0, musicLayoutTopTo, musicLayoutRightTo, screenHeight);
			}
		});
		
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月22日 下午5:44:03
	 * @Description 滑动面板时更新位置
	 * @param slideOffset
	 */
	private void updatePositionOnSlide(float slideOffset) {
		Log.i(TAG, "slideOffset " + slideOffset);
		float scaleX = 1.0f - (1.0f - slideOffset) * (rectF2.width() / rectF1.width());
        float scaleY = 1.0f - (1.0f - slideOffset) * (rectF2.height() / rectF1.height());
        float translationX = 0.5f * (1.0f - slideOffset) * (rectF2.right + rectF2.left - rectF1.right - rectF1.left);
        Log.i(TAG, "rectF1 " + rectF1.left + " " + rectF1.top + " " + rectF1.right + " " + rectF1.bottom);
        float translationY = 0.5f * (1.0f - slideOffset) * (rectF2.top + rectF2.bottom - rectF1.top - rectF1.bottom);
        Log.i(TAG, "rectF2 " + rectF2.left + " " + rectF2.top + " " + rectF2.right + " " + rectF2.bottom);
        musicViewLayout.setTranslationX(translationX);
        musicViewLayout.setTranslationY(translationY);
		musicViewLayout.setScaleX(scaleX);
		musicViewLayout.setScaleY(scaleY);
		
		btnBarLayout.setTranslationX(slideOffset * btnBarXSlop + btnBarXDelta);
		btnBarLayout.setTranslationY(slideOffset * btnBarYSlop + btnBarYDelta);
		float scaleF2 = slideOffset * btnBarScaleSlop + btnBarScaleDelta;
		btnBarLayout.setScaleX(scaleF2);
		btnBarLayout.setScaleY(scaleF2);
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015年4月23日 上午10:17:46
	 * @Description 滑动面板时更新透明度
	 * @param slideOffset
	 */
	private void updateAlphaOnSlide(float slideOffset) {
		findViewById(R.id.tv_channel).setAlpha(slideOffset);
		tvSinger.setAlpha(slideOffset);
		tvSongName.setAlpha(slideOffset);
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
//		moveTaskToBack(true);
		super.onBackPressed();
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
