package com.purehero.fx.app;

import java.io.File;

public class AAPT {
	private File aapt = null;
	
	public boolean Initialize( File aapt ) {
		this.aapt = aapt;
		
		return true;
	}
	
	public String getPackageName( File apkFile ) {
		return null;
	}
}
