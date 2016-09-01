package android.touch.macro.v2.adb;

import java.awt.Point;
import java.io.File;

public class ScreenData {
	public static final int TYPE_TAP = 0;
	public static final int TYPE_SWIPE = 1;
	
	public File image = null;
	public int angle = 0;
	public Point point = new Point();
	public int delayTime = 1000;			// 1000ms
	public int type = TYPE_TAP;
	
	public Point point2 = new Point();
	public long swipeTime = 1000;			//
	
	public void print() {
		System.out.println("===== ScreenData ==== ");
		System.out.println("image : " + image );
		System.out.println("angle : " + angle );
		System.out.println("type : " + type2String( type ));
		if( type == TYPE_SWIPE ) {
			System.out.println("start point : " + point );
			System.out.println("end point : " + point2 );
			System.out.println("swipeTime : " + swipeTime );
		} else if( type == TYPE_TAP ) {
			System.out.println("point : " + point );
		}
		System.out.println("delayTime : " + delayTime );
		System.out.println("===================== ");
	}
	
	private String type2String( int type ) {
		if( type == TYPE_SWIPE ) return "Swipe";
		if( type == TYPE_TAP   ) return "Tap";
		return "NONE_TYPE";
	}
}
