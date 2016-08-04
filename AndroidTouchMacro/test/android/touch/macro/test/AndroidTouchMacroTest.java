package android.touch.macro.test;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Test;

import android.touch.macro.AndroidTouchMacro;

public class AndroidTouchMacroTest extends AndroidTouchMacro {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1622488612165535747L;

	@Test
	public void reverseDisplayRotate_0_test() {
		int x = 10;
		int y = 20;
		int w = 1080;
		int h = 1920;
		
		int r = 0;
		
		for( int i = 0; i < 10; i++ ) {
			x++; y++;
			
			Point p1 = displayRotate( x, y, w, h, r );
			Point p2 = reverseDisplayRotate( p1.x, p1.y, w, h, r );
			
			Assert.assertTrue( p2.x == x );
			Assert.assertTrue( p2.y == y );
		}
	}

	
	@Test
	public void reverseDisplayRotate_p90_test() {
		int x = 10;
		int y = 20;
		int w = 1080;
		int h = 1920;
		
		int r = 90;
		
		for( int i = 0; i < 10; i++ ) {
			x++; y++;
		
			Point p1 = displayRotate( x, y, w, h, r );
			Point p2 = reverseDisplayRotate( p1.x, p1.y, w, h, r );
			
			System.out.println( p2.toString() );
			
			Assert.assertTrue( p2.x == x );
			Assert.assertTrue( p2.y == y );
		}
	}
	
	@Test
	public void reverseDisplayRotate_n90_test() {
		int x = 10;
		int y = 20;
		int w = 1080;
		int h = 1920;
		
		int r = -90;
		
		for( int i = 0; i < 10; i++ ) {
			x++; y++;
			
			Point p1 = displayRotate( x, y, w, h, r );
			Point p2 = reverseDisplayRotate( p1.x, p1.y, w, h, r );
			
			System.out.println( p2.toString() );
			
			Assert.assertTrue( p2.x == x );
			Assert.assertTrue( p2.y == y );
		}
	}
}
