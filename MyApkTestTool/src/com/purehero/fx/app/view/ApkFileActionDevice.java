package com.purehero.fx.app.view;

import java.io.File;

import com.purehero.android.DeviceInfo;
import com.purehero.fx.common.MenuUtils;

import javafx.scene.control.Menu;
import net.dongliu.apk.parser.ApkParser;

/**
 * Menu 에서 check 되어 있는 옵션에 따라 APK 파일을 실행 시키는 CLASS
 * 
 * @author purehero
 *
 */
public class ApkFileActionDevice extends Thread implements Runnable {
	final MainViewController parent;
	final DeviceInfo deviceInfo;
	final File apkFile;
	final ApkParser apkParser;
	final Menu optionMenu;
	
	/**
	 * @param parent
	 * @param deviceInfo	
	 * @param apkFile		실행할 APK 파일
	 * @param apkParser		APK 파일의 정보를 추출한 Parser
	 * @param optionMenu	APK 실행 옵션을 가지고 있는 Menu 객체
	 */
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
		
		// 실행 옵션 Menu 항목 중 APK_UNINSTALL 항목이 check 되어 있으면 APK 파일을 해당 Device 에서 Uninstall 시킨다. 
		if( MenuUtils.isCheckMenu( optionMenu, "APK_UNINSTALL" )) {
			
			// 단말기 리스트에서 해당 단말기의 Commant 항목을 "APK Uninstalling" 로 갱신 시킨다.
			parent.updateDeviceCommant( deviceInfo, "APK Uninstalling", true );
			try {
				
				// ADB 명령어를 Thread 로 실행 시킬 수 있는데, 본 Class 자체가 Thread 이기 때문에 따로 Thread 에서 실행 시키지는 않는다. 
				deviceInfo.getInterface().uninstallPackage( packageName ); 
				parent.updateDeviceCommant( deviceInfo, "APK Uninstalled", true );
			} catch (Exception e) {
				e.printStackTrace();
				parent.updateDeviceCommant( deviceInfo, "Failed: APK Uninstall", true );
				return;
			}					
		}
		
		// 실행 옵션 Menu 항목 중 APK_INSTALL 항목이 check 되어 있으면 APK 파일을 해당 Device 에서 Install 시킨다.
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
		
		// 실행 옵션 Menu 항목 중 APK_RUNNING 항목이 check 되어 있으면 APK 파일을 해당 Device 에서 Running 시킨다.
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
