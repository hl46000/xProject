package com.purehero.fx.app.view.work;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.purehero.android.DeviceInfo;
import com.purehero.fx.app.view.MainViewController;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import net.dongliu.apk.parser.ApkParser;
import net.dongliu.apk.parser.bean.ApkMeta;

public class RepeatTestViewController implements EventHandler<ActionEvent>{
	
	@FXML
	private TextField tfTestTitle;			// test title text field
	
	@FXML
	private TextField tfTestRepeatCount;	// test 반복 횟수 
	
	@FXML
	private TextField tfRunningDelayTime;	// 실행 후 Delay 시간 (ms)
	
	@FXML
	private TextField tfExitDelayTime;		// 종료 후 Delay 시간(ms)
	
	@FXML
	private TextField tfTestCount;			// 진행 된 테스트 Count
	
	@FXML
	private TextField tfErrorCount;			// 오류 발생 Count
	
	@FXML
	private CheckBox cbApkInstall;			// APK 파일을 설치할 것인가
	
	@FXML
	private CheckBox cbAppRunning;			// 앱 실행
	
	@FXML
	private CheckBox cbRunningDelayTime;	// 실행 후 Delay 시간을 줄것인가?
	
	@FXML
	private CheckBox cbLogSave;				// 로그를 저장 할 것인가?
	
	@FXML
	private CheckBox cbAppExit;				// 앱을 종료 할 것인가?
	
	@FXML
	private CheckBox cbExitDelayTime;		// 앱 종료 후 Delay 시간을 줄것인가?
	
	@FXML
	private CheckBox cbApkUninstall;		// APK 파일을 Uninstall 할 것인가?
	
	
	/**
	 * Controller 초기화 함수
	 * 
	 * @throws Exception
	 */
	@FXML
    public void initialize() throws Exception {
	}
	
	@FXML
	@Override
	public void handle(ActionEvent event ) {
		Object obj = event.getSource();
		if( obj instanceof Button ) {
			OnClickHandler( obj );
		}
	}

	/**
	 * ActionEvent 에서 click event 을 처리하는 함수 입니다. 
	 * 
	 * @param obj
	 */
	private void OnClickHandler(Object obj) {
		Control ctrl = ( Control ) obj;
		switch( ctrl.getId()) {
		case "ID_BUTTON_DEL_TEST_VIEW" 		: deviceTestViewController.removeTestView( parentTitledPane ); break;
		case "ID_BUTTON_TEST_TITLE_APPLY" 	: onClickTestTitleApply(); break;
		}
	}

	private void onClickTestTitleApply() {
		parentTitledPane.setText( tfTestTitle.getText());
	}

	DeviceTestViewController deviceTestViewController = null;
	public void setDeviceTestViewController( DeviceTestViewController deviceTestViewController) {
		this.deviceTestViewController = deviceTestViewController;
	}

	TitledPane parentTitledPane = null;
	public void setParentTitledPane(TitledPane tp) {
		parentTitledPane = tp;
		tfTestTitle.setText( parentTitledPane.getText() );
	}

	/**
	 * 테스트를 위해 필요한 값들을 초기화 시킴니다. 
	 */
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public void runTesting(MainViewController mainViewController, ApkParser apkParser, File apkFile, File output_folder) throws Exception {
		List<DeviceInfo> deviceInfos = mainViewController.getCheckedDeviceInfo();
		if( deviceInfos == null ) return;
		
		List<DeviceTestRunner> testRunners = new ArrayList<DeviceTestRunner>(); 
		for( DeviceInfo deviceInfo : deviceInfos ) {
			DeviceTestRunner testRunner = new DeviceTestRunner( mainViewController, deviceInfo, apkParser, apkFile, output_folder );
			testRunners.add( testRunner );
			testRunner.start();
		}
		
		// thread 가 모두 종료 될때까지 대기 한다. 
		for( DeviceTestRunner testRunner : testRunners ) {
			testRunner.join();
		}
	}
	
	/**
	 * 테스트의 진행 횟수 카운트를 증가 시킴
	 */
	private Runnable TestCountIncrementRunnable = new Runnable() {
		@Override
		public void run() {
			int value = Integer.valueOf( tfTestCount.getText());
			tfTestCount.setText( String.format( "%d", value + 1 ));						
		}
	};
	
	/**
	 * 테스트의 오류 카운트를 증가 시킴
	 */
	private Runnable TestErrorCountIncrementRunnable = new Runnable() {
		@Override
		public void run() {
			int value = Integer.valueOf( tfErrorCount.getText());
			tfErrorCount.setText( String.format( "%d", value + 1 ));						
		}
	};
	
	/**
	 * 테스트를 담당하는 Thread class
	 * 
	 * @author purehero
	 *
	 */
	class DeviceTestRunner extends Thread implements Runnable {
		final int nRepeatCount 		= Integer.valueOf( tfTestRepeatCount.getText());
		final int runningDelayTime 	= Integer.valueOf( tfRunningDelayTime.getText() );
		final int exitDelayTime 	= Integer.valueOf( tfExitDelayTime.getText() );
		final boolean bInstall		= cbApkInstall.isSelected();
		final boolean bRunning		= cbAppRunning.isSelected();
		final boolean bRunningDelay	= cbRunningDelayTime.isSelected();
		final boolean bTerminate	= cbAppExit.isSelected();
		final boolean bExitDelay	= cbExitDelayTime.isSelected();
		final boolean bUninstall	= cbApkUninstall.isSelected();
		final boolean bLogSave		= cbLogSave.isSelected();
		
		String packageName 			;			// APK 파일의 package name
		String appLabel 			;				// APK 파일의 App name
		String launcherActivityName ;// APK 파일의 Launcher activity name
		String testStartDate		;
		
		final MainViewController mainViewController;
		final DeviceInfo deviceInfo;
		final ApkParser apkParser;
		final File apkFile;
		final File outputFolder;
		public DeviceTestRunner( MainViewController mainViewController, DeviceInfo deviceInfo, ApkParser apkParser, File apkFile, File outputFolder ) {
			this.mainViewController = mainViewController;
			this.deviceInfo 	= deviceInfo;
			this.apkParser		= apkParser;
			this.apkFile		= apkFile;
			this.outputFolder 	= outputFolder;
		}
		
		/**
		 * 단말기에 APK 파일 설치 
		 * 
		 * @throws Exception
		 */
		private void apkFileInstall( long delay ) throws Exception {
			mainViewController.updateDeviceCommant(deviceInfo, String.format( "'%s' 단말기에 설치 합니다.", appLabel ), true );
			try {
				deviceInfo.getInterface().installPackage( apkFile.getAbsolutePath(), false );						
			} catch (Exception e) {
				mainViewController.updateDeviceCommant( deviceInfo, e.getMessage(), true );
				throw e;
			}
			mainViewController.updateDeviceCommant(deviceInfo, String.format( "'%s' 단말기에 설치완료", appLabel ), true );
			Thread.sleep( delay );
		}
		
		/**
		 * 단말기에서 APK 파일 제거
		 * 
		 * @throws Exception
		 */
		private void apkFileUnistall( long delay ) throws Exception {
			mainViewController.updateDeviceCommant(deviceInfo, String.format( "'%s' 단말기에서 삭제", appLabel ), true );
			try {
				deviceInfo.getInterface().uninstallPackage( packageName );
			} catch (Exception e) {
				mainViewController.updateDeviceCommant( deviceInfo, e.getMessage(), true );
				throw e;
			}
			mainViewController.updateDeviceCommant(deviceInfo, String.format( "'%s' 단말기에서 삭제 완료", appLabel ), true );
			Thread.sleep( delay );
		}
		
		@Override
		public void run() {
			ApkMeta apkMeta = null;
			try {
				apkMeta = apkParser.getApkMeta();
			} catch (IOException e2) {
				e2.printStackTrace();
				return;
			}
			
			packageName 			= apkMeta.getPackageName();			// APK 파일의 package name
			appLabel 				= apkMeta.getLabel();				// APK 파일의 App name
			launcherActivityName 	= apkMeta.getLauncherActivityName();// APK 파일의 Launcher activity name
			testStartDate			= new SimpleDateFormat("yyyyMMdd_HHmmss").format( new Date() );

			// APK 재설치
			try {
				apkFileUnistall( 1000 );
				apkFileInstall( 1000 );
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}			
			
			boolean bTestFailed = false;
			for( int countIdx = 0; countIdx < nRepeatCount && !mainViewController.isReleased(); countIdx++ ) {
				bTestFailed = false;	// 기본값 성공
				
				////////////////////////////////////////////// 테스트 앱 설치 ////////////////////////////////////////////
				if( bInstall ) {
					try {
						apkFileInstall( 1000 );
					} catch (Exception e1) {
						e1.printStackTrace();
						return;
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
				
				////////////////////////////////////////////// 테스트 앱 실행 ////////////////////////////////////////////
				if( bRunning ) {
					mainViewController.updateDeviceCommant( deviceInfo, String.format( "'%s' 앱 실행", appLabel ), true );
					if( bLogSave ) {	// LogCat 저장 옵션이 켜져있으면 앱 시작 전에 LogCat 수집을 활성화 시킨다. 
						deviceInfo.logCatStart( null );	// LogCat 이 시작되면 Clear 을 먼저 수행한 이후 부터의 데이터만 수집한다. 
					}
					try {
						deviceInfo.getInterface().executeShellCommand( String.format( "am start -n '%s/%s'", packageName, launcherActivityName), mainViewController.shellOutputReceiver );						
					} catch (Exception e) {
						mainViewController.updateDeviceCommant( deviceInfo, e.getMessage(), true );
						return;
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
				
				////////////////////////////////////////////// 테스트 앱 실행 후 대기 시간 ////////////////////////////////////////////
				if( bRunningDelay ) {
					for( int i = 0; i < runningDelayTime; i++ ) {
						if( mainViewController.isReleased()) break;		// 앱 종료 확인
						mainViewController.updateDeviceCommant(deviceInfo, String.format( "앱 실행 후 대기 중...남은 시간 %d(s)", runningDelayTime - i ), true );
						try {
							Thread.sleep( 1000 );
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
				
				
				////////////////////////////////////////////// 로그 저장 ////////////////////////////////////////////
				if( bLogSave ) { 
					mainViewController.updateDeviceCommant(deviceInfo, String.format( "LogCat 내용을 확인 합니다" ), true );
					deviceInfo.logCatStop();
					List<String> logCatLines = deviceInfo.getLogCatMessages();
					
					// LogCat 메세지 내용을 확인한다. 오류가 있으면
					for( String line : logCatLines ) {
						if( line.indexOf( "Fatal" ) != -1 ) {
							bTestFailed = true;	// 테스트 실패
							break;
						}
					}
										
					// 저장 경로를 설정한다. 
					mainViewController.updateDeviceCommant(deviceInfo, String.format( "LogCat 내용을 저장 합니다" ), true );
					File logFile = new File( outputFolder, 
						String.format( "%s_%s/%s/%s/%s/%03d_log.txt", 
							testStartDate, 					// 현재 날짜와 시간
							packageName,					// TEST 하는 APK 파일의 Package name
							parentTitledPane.getText(),		// TEST Title
							deviceInfo.getModelName(),		// TEST 을 실행한 단말기 모델명
							bTestFailed ? "error" : "log", 	// TEST 성공/실패 여부
							countIdx						// 몇 번째 TEST 인지 Index 번호  
						)
					);
					if( !logFile.getParentFile().exists()) logFile.getParentFile().mkdirs();
					
					try {
						FileUtils.writeLines( logFile, logCatLines );
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					// APK File을 결과 폴더에 복사해 놓는다.
					File dstApkFile = new File( outputFolder, String.format( "%s_%s/%s", testStartDate, packageName, apkFile.getName() ));
					if( !dstApkFile.exists()) {
						try {
							FileUtils.copyFile( apkFile, dstApkFile );
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
				
				////////////////////////////////////////////// 테스트 앱 실행 종료 ////////////////////////////////////////////
				if( bTerminate ) {
					mainViewController.updateDeviceCommant(deviceInfo, String.format( "'%s' 앱을 종료 합니다.", appLabel ), true );
					try {
						deviceInfo.getInterface().executeShellCommand( String.format( "am force-stop %s", packageName ), mainViewController.shellOutputReceiver );						
						deviceInfo.getInterface().executeShellCommand( String.format( "am kill %s", packageName ), mainViewController.shellOutputReceiver );
					} catch (Exception e) {
						mainViewController.updateDeviceCommant( deviceInfo, e.getMessage(), true );
						return;
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
								
				//////////////////////////////////////////// 앱 종료 후 대기 시간 ////////////////////////////////////////////
				if( bExitDelay ) {
					for( int i = 0; i < exitDelayTime; i++ ) {
						if( mainViewController.isReleased()) break;		// 앱 종료 확인
						mainViewController.updateDeviceCommant(deviceInfo, String.format( "앱 종료 후 대기 중...남은 시간 %d(s)", exitDelayTime - i ), true );
						try {
							Thread.sleep( 1000 );
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
				
				//////////////////////////////////////////// 테스트 앱 삭제 ////////////////////////////////////////////
				if( bUninstall ) {
					try {
						apkFileUnistall( 1000 );
					} catch (Exception e1) {
						e1.printStackTrace();
						return;
					}
				}
				if( mainViewController.isReleased()) break;				// 앱 종료 확인
				
				// TEST COUNT INCREMENT
				deviceInfo.setCount( deviceInfo.getCount() + 1 );				// 단말 목록의 테스트 카운트 증가
				if( bTestFailed ) {
					deviceInfo.setErrorCount( deviceInfo.getErrorCount() + 1 );	// 단말 목록의 오류 카운트 증가
					Platform.runLater( TestErrorCountIncrementRunnable );		// 오류 카운트 증가
				}
				
				mainViewController.updateDeviceCommant(deviceInfo, null, true ); // 단말 목록 UI 갱신
				Platform.runLater( TestCountIncrementRunnable );				// 테스트 카운트 증가
			}
		}
	}
}
