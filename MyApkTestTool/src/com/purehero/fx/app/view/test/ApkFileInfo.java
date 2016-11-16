package com.purehero.fx.app.view.test;

import java.io.File;

public class ApkFileInfo {
	private File apkfile;
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
	
	/////////////////////////////////////////////////
	private boolean selected = false;
	public Boolean getSelected() { return selected; }
	public void setSelected(Boolean selected) { this.selected = selected; }
	
	String appName;
	public String getAppName() { return appName; }
	public void setAppName( String _appName ) {
		appName = _appName;
	}
	
	String packageName;
	public String getPackageName() { return packageName; }
	public void setPackageName( String _packageName ) {
		packageName = _packageName;
	}
	
	String deviceApkFilePath;
	public String getApkFilePath() { return deviceApkFilePath; }
	public void setApkFilePath( String path ) {
		deviceApkFilePath = path;
	}
}
