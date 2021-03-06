package edu.bistu.xgxykjcx.godshandandthief.actor;

import edu.bistu.xgxykjcx.godshandandthief.BitmapStorage;
import edu.bistu.xgxykjcx.godshandandthief.GHTSurfaceView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Ground extends GameActor {
	
	private int frameW, frameH;
	
	
	
	public Ground(Context context) {
		
		actorBitmap = BitmapStorage.getGround();
		
		frameW = actorBitmap.getWidth();
		frameH = actorBitmap.getHeight();
		
		shrink = (GHTSurfaceView.SCREEN_H - Background.FLOOR) / (float) frameH;
		
		actorX = frameW * (shrink - 1) / 2;
		actorY = GHTSurfaceView.SCREEN_H * 3 / 4 + (GHTSurfaceView.SCREEN_H / 4 - frameH) / 2;
		
		Log.i(this.getClass().toString(), "groundShrink = " + shrink + "\nactorX = " + actorX + "  actorY = " + actorY);
		paint = new Paint();
		
	}
	
	@Override
	public void update(long elapsedTime) {
		
		if(Background.FACE_TO == Background.TO_LEFT)
			actorX -= Businessman.SPEED * elapsedTime;
		else
			actorX += Businessman.SPEED * elapsedTime;
		
		// 超出屏幕转回
		// 两倍屏幕是为了帮助上帝那边
		if(actorX < GHTSurfaceView.SCREEN_W * 2 - frameW * (shrink + 1) / 2) {
			actorX = frameW * (shrink - 1) / 2;
		}
		if(actorX > frameW * (shrink - 1) / 2) {
			actorX = GHTSurfaceView.SCREEN_W - frameW * (shrink + 1) / 2;
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
