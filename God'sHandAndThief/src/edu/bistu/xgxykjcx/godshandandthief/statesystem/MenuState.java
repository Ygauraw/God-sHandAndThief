package edu.bistu.xgxykjcx.godshandandthief.statesystem;

import edu.bistu.xgxykjcx.godshandandthief.BitmapStorage;
import edu.bistu.xgxykjcx.godshandandthief.GHTMainActivity;
import edu.bistu.xgxykjcx.godshandandthief.GHTSurfaceView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class MenuState implements IGameObject {
	
	private Context context;
	private StateSystem stateSystem;
	
	private final int X = 0, Y = 1;
	private long exitTime = 0;
	private float [][] menuLocation;
	private Bitmap [] menuButton;
	private Paint paint;
	
	
	
	public MenuState(StateSystem stateSystem) {
		this.context = GHTMainActivity.CONTEXT;
		this.stateSystem = stateSystem;
		
		menuButton = new Bitmap[3];
		menuButton[0] = BitmapStorage.getGodHand();
		menuButton[1] = BitmapStorage.getThief();
		menuButton[2] = BitmapStorage.getBeat();
		
		menuLocation = new float[3][];
		menuLocation[0] = new float[2];
		menuLocation[0][X] = GHTSurfaceView.SCREEN_W / 4;
		menuLocation[0][Y] = GHTSurfaceView.SCREEN_H / 3 - menuButton[0].getHeight();
		menuLocation[1] = new float[2];
		menuLocation[1][X] = GHTSurfaceView.SCREEN_W / 2;
		menuLocation[1][Y] = GHTSurfaceView.SCREEN_H / 2;
		menuLocation[2] = new float[2];
		menuLocation[2][X] = 0;
		menuLocation[2][Y] = GHTSurfaceView.SCREEN_H - menuButton[2].getHeight();
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(25);
	}
	
	public void update(long elapsedTime) {
		// TODO Auto-generated method stub
		
	}
	
	public void render(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		
		for(int i = 0; i < menuButton.length; i++) {
			canvas.drawBitmap(menuButton[i], menuLocation[i][X], menuLocation[i][Y], paint);
		}
		
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_UP) {		//防止到另一个State中产生Fling的崩溃
			for(int i = 0; i < menuLocation.length; i++) {
				if(menuLocation[i][X] < event.getX() 
						&& event.getX() <  menuLocation[i][X] + menuButton[i].getWidth() 
						&& menuLocation[i][Y] < event.getY() 
						&& event.getY() < menuLocation[i][Y] + menuButton[i].getHeight()) {
					if(i == 0) {
						IGameObject godChooseState = new GodChooseState(stateSystem);
						stateSystem.addState("GodChooseState", godChooseState);
						stateSystem.changeState("GodChooseState");
						Toast.makeText(context, "You are God...", Toast.LENGTH_SHORT).show();
					}
					if(i == 1) {
						IGameObject thiefChooseState = new ThiefChooseState(stateSystem);
						stateSystem.addState("ThiefChooseState", thiefChooseState);
						stateSystem.changeState("ThiefChooseState");
						Toast.makeText(context, "You are thief...", Toast.LENGTH_SHORT).show();
					}
					if(i == 2) {
						IGameObject pleaseWaitState = new PleaseWaitState(stateSystem);
						stateSystem.addState("PleaseWaitState", pleaseWaitState);
						stateSystem.changeState("PleaseWaitState");
						Toast.makeText(context, "Just kiding ^_^!", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
		return true;
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if((System.currentTimeMillis() - exitTime) > 2000) {
		    	Toast.makeText(context, "再按一次退出程序", Toast.LENGTH_SHORT).show();
		        exitTime = System.currentTimeMillis();
		    }
		    else
		        System.exit(0);
			return true; //返回true表示执行结束不需继续执行父类按键响应  
		}
		return false;
	}
	
}
