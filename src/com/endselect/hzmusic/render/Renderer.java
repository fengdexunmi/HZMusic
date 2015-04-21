/**
 * 
 */
package com.endselect.hzmusic.render;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * @author frankfang
 * @version 2015��4��21�� ����4:34:38
 * @Description ��Ⱦ��
 */
public abstract class Renderer {

	protected float[] mFFTPoints;
	
	public Renderer() {
		
	}
	
	public abstract void onRender(Canvas canvas, FFTData data, Rect rect);
	
	public final void render(Canvas canvas, FFTData data, Rect rect) {
		if(mFFTPoints == null || mFFTPoints.length < data.bytes.length * 4) {
			mFFTPoints = new float[data.bytes.length * 4];
		}
		
		onRender(canvas, data, rect);
	}
}
