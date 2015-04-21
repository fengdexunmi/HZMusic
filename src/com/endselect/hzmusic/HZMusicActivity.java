package com.endselect.hzmusic;

import java.io.IOException;
import java.util.List;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.endselect.hzmusic.server.Server;
import com.endselect.hzmusic.view.CircleImageView;
import com.endselect.hzmusic.view.RoundProgressView;
import com.squareup.picasso.Picasso;

/**
 * 
 * @author frankfang
 * @version 2015��4��21�� ����4:00:43
 * @Description ��Activity
 */
public class HZMusicActivity extends ActionBarActivity {

	private Context context;
	private CircleImageView coverView; //Բ�η�����ͼ
	private RoundProgressView progressView; //Բ�ν���
	private ImageView pauseView; //��ͣ
	private TextView tvSongName; //������
	private TextView tvSinger; //������
	private Player player; //������
	
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
	 * @version 2015��4��14�� ����12:21:08
	 * @Description ��ʼ����ͼ
	 */
	private void initView() {
		coverView = (CircleImageView) findViewById(R.id.civ_music_cover);
		progressView = (RoundProgressView) findViewById(R.id.rgp_music_progress);
		pauseView =(ImageView) findViewById(R.id.iv_music_pause);
		tvSongName = (TextView) findViewById(R.id.tv_song_name);
		tvSinger = (TextView) findViewById(R.id.tv_singer);
		
		//������ȡ�������߳�
		new GetSongFromUrl().execute();
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��20�� ����5:11:04
	 * @Description 
	 */
	private void initAction() {
		
		//������棨��ͣ���߼����������֣�
		coverView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(player != null) {
					//������ڲ��ţ����������ͣ����
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
				//�����ͣ��������沥��
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
	 * @version 2015��4��15�� ����10:18:59
	 * @Description �첽�̻߳�ȡ����
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
			}
		}
		
	}
	
	
	/*
	 * ���ñ�ֽ
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
	
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��20�� ����6:02:17
	 * @Description ���·��ؼ�
	 */
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
	}
	

}
