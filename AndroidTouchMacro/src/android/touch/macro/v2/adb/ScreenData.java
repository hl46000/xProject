package android.touch.macro.v2.adb;

import java.awt.Point;
import java.io.File;

public class ScreenData {
	public File image = null;
	public int angle = 0;
	public Point point = new Point();
	public int delayTime = 1000;		// 1000ms
	
	public void print() {
		System.out.println("===== ScreenData ==== ");
		System.out.println("image : " + image );
		System.out.println("angle : " + angle );
		System.out.println("point : " + point );
		System.out.println("delayTime : " + delayTime );
		System.out.println("===================== ");
	}
}
