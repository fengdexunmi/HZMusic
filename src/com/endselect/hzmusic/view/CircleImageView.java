package com.endselect.hzmusic.view;


import com.endselect.hzmusic.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author frankfang
 * @version 2015年4月13日 下午2:43:11
 * @Description 自定义圆形ImageView
 */
public class CircleImageView extends ImageView {
	
	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 2;
	
	private static final int DEFAULT_BORDER_WIDTH = 0;
	private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
	
	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();
	
	private final Matrix mShaderMatrix = new Matrix();
	private final Paint mBitmapPaint = new Paint();
	private final Paint mBorderPaint = new Paint();
	
	private int mBorderColor = DEFAULT_BORDER_COLOR;
	private int mBorderWidth = DEFAULT_BORDER_WIDTH;
	
	private Bitmap mBitmap;
	private BitmapShader mBitmapShader;
	private int mBitmapWidth;
	private int mBitmapHeight;
	
	private float mDrawableRadius;
	private float mBorderRadius;
	
	private boolean mReady;
	private boolean mSetupPending;

	public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setScaleType(SCALE_TYPE);
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);
		mBorderWidth = array.getDimensionPixelSize(R.styleable.CircleImageView_border_witdh, DEFAULT_BORDER_WIDTH);
		mBorderColor = array.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);
		array.recycle();
		
		mReady = true;
		if(mSetupPending) {
			setup();
			mSetupPending = false;
		}
	}

	public CircleImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public CircleImageView(Context context) {
		super(context);
		super.setScaleType(SCALE_TYPE);
		
		mReady = true;
		if(mSetupPending) {
			setup();
			mSetupPending = false;
		}
		
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(getDrawable() == null) {
			return;
		}
		
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);;
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		setup();
	}
	
	public int getmBorderColor() {
		return mBorderColor;
	}

	public void setmBorderColor(int mBorderColor) {
		if(this.mBorderColor == mBorderColor) {
			return;
		}
		
		this.mBorderColor = mBorderColor;
		mBorderPaint.setColor(mBorderColor);
		invalidate();
	}

	public int getmBorderWidth() {
		return mBorderWidth;
	}

	public void setmBorderWidth(int mBorderWidth) {
		if(this.mBorderWidth == mBorderWidth) {
			return;
		}
		this.mBorderWidth = mBorderWidth;
		setup();
	}
	
	@Override
	public ScaleType getScaleType() {
		// TODO Auto-generated method stub
		return SCALE_TYPE;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		mBitmap = bm;
		setup();
	}
	
	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		mBitmap = getBitmapFromDrawable(drawable);
		setup();
	}
	
	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		mBitmap = getBitmapFromDrawable(getDrawable());
		setup();
	}
	
	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if(drawable == null) {
			return null;
		}
		
		if(drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}
		
		try {
			Bitmap bitmap = null;
			if(drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}
			
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			
			return bitmap;
		} catch (Exception e) {
			return null;
		}
	}
	

	private void setup() {
		
		if(!mReady) {
			mSetupPending = true;
			return;
		}
		
		if(mBitmap == null) {
			return;
		}
		
		mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		
		mBitmapPaint.setAntiAlias(true);
		mBitmapPaint.setShader(mBitmapShader);
		
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setColor(mBorderColor);
		mBorderPaint.setStrokeWidth(mBorderWidth);
		
		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
		
		mBorderRect.set(0, 0, getWidth(), getHeight());
		mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);
		
		mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
		mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
		
		updateShaderMatrix();
		invalidate();
		
	}
	
	private void updateShaderMatrix() {
		float scale;
		float dx = 0;
		float dy = 0;
		
		mShaderMatrix.set(null);
		if(mBitmapWidth * mDrawableRect.height() > mBitmapHeight * mDrawableRect.width()) {
			scale = mDrawableRect.height() / (float) mBitmapHeight;
			dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
		} else {
			scale = mDrawableRect.width() / (float)mBitmapWidth;
			dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
		}
		
		mShaderMatrix.setScale(scale, scale);
		mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
		
		mBitmapShader.setLocalMatrix(mShaderMatrix);
		
	}

}
