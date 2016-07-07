package android.touch.macro.recoder;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import android.touch.macro.G;

public class TouchRecodeData {
	public File screenCapFile;
	public int x;
	public int y;
	public int delay;
	
	public TouchRecodeData( File img, int x, int y, int delay ) {
		this.x = x;
		this.y = y;
		this.delay = delay;
		
		try {
			screenCapFile = File.createTempFile( "tmp", ".png", G.getTempPath());
			FileUtils.copyFile( img, screenCapFile );
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void release() {
		screenCapFile.delete();
	}
}
