package com.purehero.fx.app.view;

import java.io.File;

import com.purehero.android.DeviceInfo;
import com.purehero.fx.common.MenuUtils;

import javafx.scene.control.Menu;
import net.dongliu.apk.parser.ApkParser;

public class ApkFileActionDevice extends Thread implements Runnable {
	final MainViewController parent;
	final DeviceInfo deviceInfo;
	final File apkFile;
	final ApkParser apkParser;
	final Menu optionMenu;
	
	public ApkFileActionDevice( MainViewController parent, DeviceInfo deviceInfo, File apkFile, ApkParser apkParser, Menu optionMenu ) {
		this.parent = parent;
		this.deviceInfo = deviceInfo;
		this.apkFile = apkFile;
		this.apkParser = apkParser;
		this.optionMenu = optionMenu;
	}
	
	@Override
	public void run() {
		String packageName = "";
		String launcherActivityName = "";
		
		try {
			packageName 		 = apkParser.getApkMeta().getPackageName();;
			launcherActivityName = apkParser.getApkMeta().getLauncherActivityName();;
		} catch( Exception e ) {
			e.printStackTrace();
			return;
		}
		
		if( MenuUtils.isCheckMenu( optionMenu, "APK_UNINSTALL" )) {
			parent.updateDeviceCommant( deviceInfo, "APK Uninstalling", true );
			try {
				deviceInfo.getInterface().uninstallPackage( packageName );
				parent.updateDeviceCommant( deviceInfo, "APK Uninstalled", true );
			} catch (Exception e) {
				e.printStackTrace();
				parent.updateDeviceCommant( deviceInfo, "Failed: APK Uninstall", true );
				return;
			}					
		}
		if( MenuUtils.isCheckMenu( optionMenu, "APK_INSTALL" )) {
			parent.updateDeviceCommant( deviceInfo, "APK Installing", true );
			try {
				deviceInfo.getInterface().installPackage( apkFile.getAbsolutePath(), false );
				parent.updateDeviceCommant( deviceInfo, "APK Installed", true );
			} catch (Exception e) {
				e.printStackTrace();
				parent.updateDeviceCommant( deviceInfo, "Failed: APK Installed", true );
				return;
			}
			
		}
		if( MenuUtils.isCheckMenu( optionMenu, "APK_RUNNING" )) {
			parent.updateDeviceCommant( deviceInfo, "APK Running", true );
			try {
				deviceInfo.getInterface().executeShellCommand( String.format( "am start -n '%s/%s'", packageName, launcherActivityName), parent.shellOutputReceiver );						
			} catch (Exception e) {
				e.printStackTrace();
				parent.updateDeviceCommant( deviceInfo, "Failed: APK Running", true );
				return;
			}
		}
	}

}
