package com.purehero.fx.app.view.work;

import java.io.File;

public class ApkFileInfo {
	private final File apkfile;
	private String status = "대기";
	public ApkFileInfo( File file ) {
		apkfile = file;
	}
	
	public File getApkFile() {
		return apkfile;
	}
	
	public String getName() {
		return apkfile.getName();
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus( String _status ) {
		status = _status;
	}
}
