package edu.bistu.xgxykjcx.godshandandthief.actor;

import edu.bistu.xgxykjcx.godshandandthief.GHTMainActivity;
import edu.bistu.xgxykjcx.godshandandthief.GHTSurfaceView;
import edu.bistu.xgxykjcx.godshandandthief.actor.obstacle.Obstacle;
import edu.bistu.xgxykjcx.godshandandthief.statesystem.StateSystem.PlayerType;
import edu.bistu.xgxykjcx.godshandandthief.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class Businessman extends GameActor implements OnGestureListener {
	public static float SPEED = (GHTSurfaceView.SCREEN_W / 2f) / 1000f;
	
	public static final String UP_FLING_STRING = "UP_FLING";
	public static final String DOWN_FLING_STRING = "DOWN_FLING";
	public static final String IS_INJURED_STRING = "IS_INJURED";
	
	// 操作的标志
	public static final int UP_FLING = 0;
	public static final int DOWN_FLING = 1;
	// 身体状态的标志
	private final int IS_RUN = 0;
	private final int IS_UP = 1;
	private final int IS_DOWN = 2;
	private final int IS_INJURED = 3;
	
	private Context context;
	private GHTMainActivity mainActivity;
	
	private int health, frameW, frameH, incrementWHalf, incrementHHalf, currentFrame, bodyMotion;
	private int heartStartX, heartY, heartInterval;
	//private int upHight;		// 跳跃高度 默认是一个身高
	private int [] frameTotal;
	private long brushTime, upTime, downTime, injuredTime;
	private float scrollX, scrollY;
	private boolean [] fling;
	private PlayerType playerType;
	
	private Bitmap [][] frame;
	private Bitmap heart;
	private GestureDetector mGestureDetector;
	
	
	
	public Businessman() {
		this.context = GHTMainActivity.CONTEXT;
		mainActivity = (GHTMainActivity) GHTMainActivity.CONTEXT;
		playerType = null;
		
		frameTotal = new int[4];
		frame = new Bitmap[4][];
		
		bodyMotion = IS_RUN;
		frameTotal[bodyMotion] = 5;
		actorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.businessman_run);
		frameW = actorBitmap.getWidth();			// 100
		frameH = actorBitmap.getHeight() / 5;		// 120
		frame[bodyMotion] = new Bitmap[frameTotal[bodyMotion]];
		for(int i = 0; i < frameTotal[bodyMotion]; i++) {
			frame[bodyMotion][i] = Bitmap.createBitmap(actorBitmap, 0, frameH * i, frameW, frameH);
		}
		
		bodyMotion = IS_UP;
		frameTotal[bodyMotion] = 1;
		actorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.businessman_up);
		frame[bodyMotion] = new Bitmap[frameTotal[bodyMotion]];
		for(int i = 0; i < frameTotal[bodyMotion]; i++) {
			frame[bodyMotion][i] = actorBitmap;
		}
		
		bodyMotion = IS_DOWN;
		frameTotal[bodyMotion] = 1;
		actorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.businessman_down);
		frame[bodyMotion] = new Bitmap[frameTotal[bodyMotion]];
		for(int i = 0; i < frameTotal[bodyMotion]; i++) {
			frame[bodyMotion][i] = actorBitmap;
		}
		
		bodyMotion = IS_INJURED;
		frameTotal[bodyMotion] = 4;
		actorBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.businessman_injured);
		frame[bodyMotion] = new Bitmap[frameTotal[bodyMotion]];
		for(int i = 0; i < frameTotal[bodyMotion]; i++) {
			frame[bodyMotion][i] = Bitmap.createBitmap(actorBitmap, 0, frameH * i, frameW, frameH);
		}
		
		heart = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
		heartStartX = GHTSurfaceView.SCREEN_W / 17;
		heartY = GHTSurfaceView.SCREEN_H / 17;
		heartInterval = heart.getWidth();
		
		brushTime = 0;
		
		hight = GHTSurfaceView.SCREEN_H / 4;		// 高度是屏幕高度1/4
		shrink = hight / (float) frameH;
		width = (int) (frameW * shrink);
		incrementWHalf = (int) (frameW * (shrink - 1) / 2);
		incrementHHalf = (int) (frameH * (shrink - 1) / 2);
		
		actorX = GHTSurfaceView.SCREEN_W / 5 + incrementWHalf;		// 定位
		actorY = Background.FLOOR - frameH - incrementHHalf;
		Log.d(this.getClass().getSimpleName(), "actorX = " + actorX + ", frameW = " + frameW + ", frameH = " + frameH);
		Log.d(this.getClass().getSimpleName(), "incrementWHalf = " + incrementWHalf + ", incrementHHalf = " + incrementHHalf);
		
		fling = new boolean[2];
		
		health = 3;
		
		//初始化GestureDetector
		mGestureDetector = new GestureDetector(context, this);
		mGestureDetector.setIsLongpressEnabled(true);
		
		paint = new Paint();
	}
	
	public Businessman(PlayerType playerType) {
		this();
		this.playerType = playerType;
	}
	
	@Override
	public void update(long elapsedTime) {
		brushTime += elapsedTime;
		
		//处理输入操作
		if(fling[DOWN_FLING]) {
			if(bodyMotion == IS_RUN)
				bodyMotion = IS_DOWN;
		}
		if(fling[UP_FLING]) {
			if(bodyMotion == IS_RUN || bodyMotion == IS_DOWN)
				bodyMotion = IS_UP;
		}
		fling[DOWN_FLING] = false;
		fling[UP_FLING] = false;
		
		// 处理身体姿势
		if(bodyMotion == IS_UP) {
			actorY = Background.FLOOR - frameH - incrementHHalf - hight;
			if(upTime < 800 || fling[UP_FLING])
				upTime += elapsedTime;
			else {
				upTime = 0;
				bodyMotion = IS_RUN;
				actorY = Background.FLOOR - frameH - incrementHHalf;
			}
		}
		if(bodyMotion == IS_DOWN)
			if(downTime < 800 || fling[DOWN_FLING])
				downTime += elapsedTime;
			else {
				downTime = 0;
				bodyMotion = IS_RUN;
				actorY = Background.FLOOR - frameH - incrementHHalf;
			}
		if(bodyMotion == IS_INJURED)
			if(injuredTime < 700)
				injuredTime += elapsedTime;
			else {
				injuredTime = 0;
				bodyMotion = IS_RUN;
				actorY = Background.FLOOR - frameH - incrementHHalf;		//防止不在地面跑
			}
		
		if(brushTime > 80) {
			currentFrame = ++currentFrame % frameTotal[bodyMotion];
			brushTime = 0;
		}
	}
	
	@Override
	public void render(Canvas canvas) {
		for(int i = 0; i < health; i++)
			canvas.drawBitmap(heart, heartStartX + i * heartInterval, heartY, paint);
		
		canvas.save();
		if(Background.FACE_TO == Background.TO_RIGHT)
			canvas.scale(-shrink, shrink, actorX + frameW / 2, actorY + frameH / 2);
		else
			canvas.scale(shrink, shrink, actorX + frameW / 2, actorY + frameH / 2);
		currentFrame = currentFrame % frameTotal[bodyMotion];		//防止不同动作bodyMotion的总数不一样 没有经过update导致数组越界
		canvas.drawBitmap(frame[bodyMotion][currentFrame], actorX, actorY, paint);
		canvas.restore();
	}
	
	public boolean isCollisionWith(Obstacle obstacle) {
		int allowMistake = obstacle.getWidth() / 4;
		if(obstacle.getLeft() < getRight() - allowMistake && getLeft() + allowMistake < obstacle.getRight()) {
			
			//Log.d(this.getClass().getSimpleName(), "businessman left = " + getLeft() + ", right = " + getRight());
			//Log.d(this.getClass().getSimpleName(), "obstacle left = " + obstacle.getLeft() + ", right = " + obstacle.getRight());
			switch(obstacle.getType()) {
			case Obstacle.HOLE :
				if(bodyMotion == IS_DOWN || bodyMotion == IS_INJURED)
					return false;
				else
					return true;
			case Obstacle.STONE :
			case Obstacle.PIT :
				if(bodyMotion == IS_UP || bodyMotion == IS_INJURED)
					return false;
				else
					return true;
			default:
				return false;
			}
		}
		return false;
	}
	
	public int setFling(int direction) {
		if(direction == UP_FLING) {
			fling[UP_FLING] = true;
			return UP_FLING;
		}
		if(direction == DOWN_FLING) {
			fling[DOWN_FLING] = true;
			return DOWN_FLING;
		}
		return -1;
	}
	
	public void beInjured() {
		if(bodyMotion != IS_INJURED) {
			health--;
			bodyMotion = IS_INJURED;
			Log.i(this.getClass().getSimpleName(), "businessman is injured.");
		}
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		//Log.v(this.getClass().getSimpleName(), "onTouchEvent");
		if(event.getAction() ==  MotionEvent.ACTION_UP) {
	    	Log.d(this.getClass().toString(), "scrollX = " + scrollX + ", scrollY = " + scrollY);
	    	scrollX = 0;
			scrollY = 0;
	    }
		
		return mGestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent arg0) {
		// 一次点击只唤醒一次
		//Log.v(this.getClass().getSimpleName(), "onDonw");
		return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		
		return false;
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
		// 一旦点击稍有移动就进入scroll再进入filing 不走长按这条线了 只有按住不动才能唤醒这个方法（只唤醒一次）
		//Log.v(this.getClass().getSimpleName(), "onLongPress");
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		scrollX -= distanceX;
		scrollY -= distanceY;
		int scrollLength = GHTSurfaceView.SCREEN_W / 10;
		Log.d(this.getClass().getSimpleName(), "onScroll ——> distanceX = " + distanceX + ", distanceY = " + distanceY);
		
		if(scrollLength < -scrollY) {
			if(GHTMainActivity.CAN_SENDMESSAGE)
				mainActivity.sendMessage(UP_FLING_STRING);
			fling[UP_FLING] = true;
			Log.d(this.getClass().toString(), "onScroll to up.");
		}
		if((scrollX > scrollLength && scrollY < scrollLength / 2) || scrollY > scrollLength) {
			if(GHTMainActivity.CAN_SENDMESSAGE)
				mainActivity.sendMessage(DOWN_FLING_STRING);
			fling[DOWN_FLING] = true;
			Log.d(this.getClass().toString(), "onScroll to right.");
		}
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent e) {
		//Log.v(this.getClass().getSimpleName(), "onShowPress");
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		//Log.v(this.getClass().getSimpleName(), "onSingleTapUp");
		// TODO Auto-generated method stub
		return true;
	}
	
	public int getHreat() {
		return health;
	}
	
	public int setHreat(int health) {
		this.health = health;
		return health;
	}
	
	@Override
	public float getLeft() {
		return actorX - incrementWHalf;
	}
	
	@Override
	public float getRight() {
		return actorX + frameW + incrementWHalf;
	}
	
}
