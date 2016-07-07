package android.touch.macro.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import android.touch.macro.util.Util;

public class UtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
		BufferedImage img1, img2;
		try {
			img1 = ImageIO.read( new File( "c:\\test1.png" ));
			img2 = ImageIO.read( new File( "c:\\test2.png" ));
			
			int[] result1 = Util.imageHistogram( img1 );
			int[] result2 = Util.imageHistogram( img2 );
			
			Assert.assertNotNull( result1 );
			Assert.assertNotNull( result2 );
				
			int sum = 0;
			for( int i = 0; i < result1.length; i++ ) {
				sum += Math.max( result1[i], result2[i] ) - Math.min( result1[i], result2[i] );								
			}
			
			int d_sum = 100 - (int)(( sum * 100.0 ) / result1.length );
			System.out.printf( "%d\n", d_sum );
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
