package edu.bistu.xgxykjcx.godshandandthief.actor;

import edu.bistu.xgxykjcx.godshandandthief.BitmapStorage;
import edu.bistu.xgxykjcx.godshandandthief.GHTSurfaceView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Cloud extends GameActor {
	
	private int cloudSpeed, frameW, frameH;
	
	
	
	public Cloud(Context context) {
		
		cloudSpeed = 100;
		actorBitmap = BitmapStorage.getCloud();
		
		frameW = actorBitmap.getWidth();
		frameH = actorBitmap.getHeight();

		shrink = (GHTSurfaceView.SCREEN_W / 6) / (float) frameW;
		
		actorX = Background.FACE_TO == Background.TO_LEFT ? GHTSurfaceView.SCREEN_W : -frameW;
		actorY = GHTSurfaceView.SCREEN_H / 9 + frameH * (shrink - 1) / 2;
		
		Log.i(this.getClass().toString(), "cloudShrink = " + shrink);
		paint = new Paint();
	}
	
	public Cloud(Context context, float actorX) {
		this(context);
		setActorX(actorX);
	}
	
	@Override
	public void update(long elapsedTime) {
		
		if(Background.FACE_TO == Background.TO_LEFT)
			actorX -= (cloudSpeed * elapsedTime) / 1000;
		else
			actorX += (cloudSpeed * elapsedTime) / 1000;
		
		//������Ļת��
		if(actorX < -frameW * shrink) {
			actorX = GHTSurfaceView.SCREEN_W + (frameW * (shrink - 1) / 2);
		}
		if(actorX > GHTSurfaceView.SCREEN_W + (frameW * (shrink - 1) / 2)) {
			actorX = -frameW * shrink;
		}
	}
	
	@Override
	public void render(Canvas canvas) {
		canvas.save();
		if(Background.FACE_TO == Background.TO_RIGHT)
			canvas.scale(-shrink, shrink, actorX + frameW /2, actorY + frameH /2);
		else
			canvas.scale(shrink, shrink, actorX + frameW /2, actorY + frameH /2);
		canvas.drawBitmap(actorBitmap, actorX, actorY, paint);
		canvas.restore();
	}
	
	void setActorX(float actorX) {
		this.actorX = actorX;
	}
	
	void setActorY(float actorY) {
		this.actorY = actorY;
	}
	
	@Override
	public float getLeft() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public float getRight() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
