package com.purehero.atm.v3.model;

import java.awt.Point;
import java.io.File;

public class ScreenData {
	public static final int TYPE_TAP = 0;
	public static final int TYPE_SWIPE = 1;
	
	public File image = null;				// image file
	public int image_width;					// image width
	public int image_height;				// image height
	public Point point 	= new Point();		// tab : click pos, swipe : start pos
	public Point point2 = new Point();		// swipe : end pos
	public int delayTime = 1000;			// Touch wait time (ms)
	public int type = TYPE_TAP;				// Touch Type : tab or swipe
	public long swipeTime = 1000;			// swipe during time 
	
	public void print() {
		System.out.println("===== ScreenData ==== ");
		System.out.println("image : " + image );
		System.out.println("image width : " + image_width );
		System.out.println("image height : " + image_height );
		System.out.println("touch type : " + type2String( type ));
		if( type == TYPE_SWIPE ) {
			System.out.println("start point : " + point );
			System.out.println("end point : " + point2 );
			System.out.println("swipeTime : " + swipeTime );
		} else if( type == TYPE_TAP ) {
			System.out.println("tap point : " + point );
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
