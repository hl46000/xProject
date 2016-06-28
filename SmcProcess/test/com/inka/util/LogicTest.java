package com.inka.util;

import java.util.Random;

import org.junit.Test;

public class LogicTest {

	@Test
	public void test() {
		
		Random r = new Random( System.currentTimeMillis()) ;
		
		int cnt[] = new int[]{ 0, 0 };
		
		for( int i = 0; i < 100; i++ ) {
			cnt[(int)( Math.abs( r.nextInt()) % 2 )]++;
		}
		
		System.out.printf( "0=%d, 1=%d\n", cnt[0], cnt[1] );
		
		
		cnt = new int[]{ 0, 0 };
		for( int i = 0; i < 100; i++ ) {
			cnt[(int)( System.nanoTime() % 2 )]++;
		}
		
		System.out.printf( "0=%d, 1=%d\n", cnt[0], cnt[1] );
	}

}
