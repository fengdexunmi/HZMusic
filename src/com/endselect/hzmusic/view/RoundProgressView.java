/**
 * 
 */
package com.endselect.hzmusic.view;


import com.endselect.hzmusic.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author frankfang
 * @E-mail frankfang@hyx.com	
 * @version 2015年4月14日 下午5:45:51
 * @Description TODO
 */
public class RoundProgressView extends View {
	
	private Paint mPaint; //画笔
	private int mRoundColor; //圆环的颜色
	private int mRoundProgressColor; //圆环进度的颜色
	private int mProgressTextColor; //中间进度百分比字体颜色
	private float mProgressTextSize; //中间进度百分比字体大小
	private boolean mProgressTextDisplayable; //中间进度百分比是否显示
	private float mRoundWidth; //圆环的宽度
	private int mMaxProgress; //最大进度
	private int mCurrentProgress; //当前进度
	private int style; //进度风格
	private static final int STROKE = 0; //空心
	private static final int FILL = 1; //实心
	
	public RoundProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RoundProgressView(Context context) {
		this(context, null);
	}
	
	public RoundProgressView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mPaint = new Paint();
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
		
		//获取自定义属性和默认值
		mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.GRAY);
		mRoundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.YELLOW);
		mProgressTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.BLUE);
		mProgressTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
		mRoundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 15);
		mMaxProgress = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		mProgressTextDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
		
		mTypedArray.recycle();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		/**
		 * 画圆环
		 */
		int center = getWidth() / 2;
		int radius = (int) (center - mRoundWidth / 2) - 2;
		mPaint.setColor(mRoundColor);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(mRoundWidth - 2);
		mPaint.setAntiAlias(true);
		canvas.drawCircle(center, center, radius, mPaint);
		
		/**
		 * 画进度百分比
		 */
		mPaint.setStrokeWidth(0);
		mPaint.setColor(mProgressTextColor);
		mPaint.setTextSize(mProgressTextSize);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		int percent = (int) ((float) mCurrentProgress / (float) mMaxProgress * 100);
		float textWidth = mPaint.measureText(percent + "%");
		if(mProgressTextDisplayable && style == STROKE) {
			canvas.drawText(percent + "%", center - textWidth / 2, center + mProgressTextSize / 2, mPaint);
		}
		
		/**
		 * 画圆弧（进度）	
		 */
		mPaint.setStrokeWidth(mRoundWidth);
		mPaint.setColor(mRoundProgressColor);
		mPaint.setAntiAlias(true);
		RectF oval = new RectF(center - radius - 1, center - radius - 1, center + radius + 1, center + radius + 1);
		switch (style) {
		case STROKE:
			mPaint.setStyle(Style.STROKE);
			canvas.drawArc(oval, 270.0F, 360.0F * mCurrentProgress / mMaxProgress, false, mPaint);
			break;

		case FILL:
			mPaint.setStyle(Style.FILL_AND_STROKE);
			if(mCurrentProgress != 0) {
				canvas.drawArc(oval, 270.0F, 360.0F * mCurrentProgress / mMaxProgress, true, mPaint);
			}
			break;
		}
		
		
		
	}

	public int getmRoundColor() {
		return mRoundColor;
	}

	public void setmRoundColor(int mRoundColor) {
		this.mRoundColor = mRoundColor;
	}

	public int getmRoundProgressColor() {
		return mRoundProgressColor;
	}

	public void setmRoundProgressColor(int mRoundProgressColor) {
		this.mRoundProgressColor = mRoundProgressColor;
	}

	public int getmProgressTextColor() {
		return mProgressTextColor;
	}

	public void setmProgressTextColor(int mProgressTextColor) {
		this.mProgressTextColor = mProgressTextColor;
	}

	public float getmProgressTextSize() {
		return mProgressTextSize;
	}

	public void setmProgressTextSize(float mProgressTextSize) {
		this.mProgressTextSize = mProgressTextSize;
	}

	public float getmRoundWidth() {
		return mRoundWidth;
	}

	public void setmRoundWidth(float mRoundWidth) {
		this.mRoundWidth = mRoundWidth;
	}

	public synchronized int getmMaxProgress() {
		return mMaxProgress;
	}

	public synchronized void setmMaxProgress(int mMaxProgress) {
		if(mMaxProgress < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.mMaxProgress = mMaxProgress;
	}

	public synchronized int getmCurrentProgress() {
		return mCurrentProgress;
	}

	/**
	 * 
	 * @author frankfang
	 * @E-mail frankfang@hyx.com	
	 * @version 2015年3月17日 下午4:09:31
	 * @Description 设置进度需要考虑多线程，同步刷新界面调用postInvalidate()能在非UI线程刷新
	 * @param mCurrentProgress
	 */
	public synchronized void setmCurrentProgress(int mCurrentProgress) {
		if(mCurrentProgress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if(mCurrentProgress > mMaxProgress) {
			mCurrentProgress = mMaxProgress;
		}
		if(mCurrentProgress <= mMaxProgress) {
			this.mCurrentProgress = mCurrentProgress;
			postInvalidate();
		}
		
	}
	
}
