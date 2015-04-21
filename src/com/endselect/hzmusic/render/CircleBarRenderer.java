/**
 * 
 */
package com.endselect.hzmusic.render;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * @author frankfang
 * @version 2015年4月21日 下午4:43:27
 * @Description 渲染实现
 */
public class CircleBarRenderer extends Renderer {
	
	private int mDivisions;
	private Paint mPaint;
	private boolean mCycleColor;
	
	public CircleBarRenderer(Paint paint, int divisions) {
		this(paint, divisions, false);
	}
	
	public CircleBarRenderer(Paint paint, int divisions, boolean cycleColor) {
		super();
		this.mPaint = paint;
		this.mDivisions = divisions;
		this.mCycleColor = cycleColor;
	}

	@Override
	public void onRender(Canvas canvas, FFTData data, Rect rect) {
		if(mCycleColor) {
			cycleColor();
		}
		
		for(int i = 0; i < data.bytes.length / mDivisions; i++) {
			byte rfk = data.bytes[mDivisions * i];
			byte ifk = data.bytes[mDivisions * i + 1];
			float magnitude = (rfk * rfk + ifk * ifk);
			float abValue = 75 * (float) Math.log10(magnitude);
			
			float[] cartPoint = {
					(float) (i * mDivisions) / (data.bytes.length - 1),
					rect.height() / 2 - abValue / 4
			};
			
			float[] polarPoint = toPolar(cartPoint, rect);
			mFFTPoints[i * 4] = polarPoint[0];
			mFFTPoints[i * 4 + 1] = polarPoint[1];
			
			float[] cartPoint2 = {
					(float) (i * mDivisions) / (data.bytes.length - 1),
					rect.height() / 2 + abValue
			};
			float[] polarPoint2 = toPolar(cartPoint2, rect);
			mFFTPoints[i * 4 + 2] = polarPoint2[0];
			mFFTPoints[i * 4 + 3] = polarPoint2[1];
		}
		
		canvas.drawLines(mFFTPoints, mPaint);
		
		modulation += 0.13;
		angleModulation += 0.28;
	}
	
	private float colorCounter = 0;
	private void cycleColor() {
		int r = (int) Math.floor(128 * (Math.sin(colorCounter) + 1));
		int g = (int) Math.floor(128 * (Math.sin(colorCounter + 2) + 1));
		int b = (int) Math.floor(128 * (Math.sin(colorCounter + 4) + 1));
		mPaint.setColor(Color.argb(128, r, g, b));
		colorCounter += 0.03;
	}
	
	float modulation = 0;
	float modulationStrength = 0.4f;
	float angleModulation = 0;
	float aggressive = 0.4f;
	private float[] toPolar(float[] cartesian, Rect rect) {
		double cX = rect.width() / 2;
		double cY = rect.height() / 2;
		double angle = (cartesian[0]) * 2 * Math.PI;
//		double radius = ((rect.width() / 2) * (1 - aggressive) + aggressive * cartesian[1] / 2)
//				* ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2);
		double radius = ((rect.width() * 3 / 5) * (1 - aggressive) + aggressive * cartesian[1] / 2)
				* ((1 - modulationStrength) + modulationStrength * (1 + Math.sin(modulation)) / 2);
		float[] out = {
				(float) (cX + radius * Math.sin(angle + angleModulation)), 
				(float) (cY + radius * Math.cos(angle + angleModulation))
		};
		
		return out;
	}

}
