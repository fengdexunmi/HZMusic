package com.endselect.hzmusic.view;

import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.util.AttributeSet;
import android.view.View;

import com.endselect.hzmusic.render.FFTData;
import com.endselect.hzmusic.render.Renderer;

/**
 * @author frankfang
 * @E-mail frankfang@hyx.com	
 * @Description ��Ƶ���ӻ�
 */
public class VisualizerView extends View {
	
	private static final String TAG = "VisualizerView";
	private byte[] mFFTBytes;
	private Rect mRect = new Rect();
	private Visualizer mVisualizer;
	
	private Set<Renderer> mRenderers;
	private Paint mFlashPaint = new Paint();
	private Paint mFadePaint = new Paint();

	public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		init();
	}

	public VisualizerView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VisualizerView(Context context) {
		this(context, null);
	}
	
	private void init() {
		mFFTBytes = null;
		
		mFlashPaint.setColor(Color.argb(122, 255, 255, 255));
		mFadePaint.setColor(Color.argb(238, 255, 255, 255));
		mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));
		mRenderers = new HashSet<Renderer>();
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��21�� ����6:02:36
	 * @Description ������Ƶ������
	 * @param player
	 */
	@SuppressLint("NewApi")
	public void link(MediaPlayer player) {
		if(player == null) {
			return;
		}
		
		mVisualizer = new Visualizer(player.getAudioSessionId());
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
		
		Visualizer.OnDataCaptureListener captureListener = new OnDataCaptureListener() {
			
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform,
					int samplingRate) {
			}
			
			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] fft,
					int samplingRate) {
				updateVisualizerFFT(fft);
			}
		};
		
		mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 2, true, true);
		mVisualizer.setEnabled(true);
		//��Ƶ���Ž���ʱֹͣ���ӻ�
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				mVisualizer.setEnabled(false);
				clearRenderers();
			}
		});
	}
	
	private void updateVisualizerFFT(byte[] bytes) {
		mFFTBytes = bytes;
		invalidate();
	}
	
	boolean mFlash = false;
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��21�� ����5:35:33
	 * @Description ��Ƶ��ʼʱ���п��ӻ�
	 */
	public void flash() {
		mFlash = true;
		invalidate();
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��21�� ����5:38:49
	 * @Description �����Ⱦ��
	 * @param renderer
	 */
	public void addRenderer(Renderer renderer) {
		if(renderer != null) {
			mRenderers.add(renderer);
		}
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��21�� ����5:39:38
	 * @Description ���������Ⱦ��
	 */
	public void clearRenderers() {
		mRenderers.clear();
	}
	
	/**
	 * 
	 * @author frankfang
	 * @version 2015��4��21�� ����5:40:33
	 * @Description 
	 */
	public void release() {
		mVisualizer.release();
	}
	
	Bitmap mCanvasBitmap;
	Canvas mCanvas;
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		mRect.set(0, 0, getWidth(), getHeight());
		if(mCanvasBitmap == null) {
			mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
		}
		if(mCanvas == null) {
			mCanvas = new Canvas(mCanvasBitmap);
		}
		
		//���Ƹ���Ҷ�任Ч��
		if(mFFTBytes != null) {
			FFTData fftData = new FFTData(mFFTBytes);
			for(Renderer renderer : mRenderers) {
				renderer.render(mCanvas, fftData, mRect);
			}
		}
		
		mCanvas.drawPaint(mFadePaint);
		if(mFlash) {
			mFlash = false;
			mCanvas.drawPaint(mFlashPaint);
		}
		
		canvas.drawBitmap(mCanvasBitmap, new Matrix(), null);
	}
	
	

}
