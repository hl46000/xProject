package com.purehero.fx.app.view.work;

import java.util.ArrayList;
import java.util.List;

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

public class TestViewController implements EventHandler<ActionEvent>{
	
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
	 *  
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

	public void clear() {
		// TODO Auto-generated method stub
		
	}

	public void runTesting(MainViewController mainViewController) throws Exception {
		List<DeviceInfo> deviceInfos = mainViewController.getCheckedDeviceInfo();
		if( deviceInfos == null ) return;
		
		List<DeviceTestRunner> testRunners = new ArrayList<DeviceTestRunner>(); 
		for( DeviceInfo deviceInfo : deviceInfos ) {
			DeviceTestRunner testRunner = new DeviceTestRunner( mainViewController, deviceInfo );
			testRunners.add( testRunner );
			testRunner.start();
		}
		
		// thread 가 모두 종료 될때까지 대기 한다. 
		for( DeviceTestRunner testRunner : testRunners ) {
			testRunner.join();
		}
	}
	
	private Runnable TestCountIncrementRunnable = new Runnable() {
		@Override
		public void run() {
			int value = Integer.valueOf( tfTestCount.getText());
			tfTestCount.setText( String.format( "%d", value + 1 ));						
		}
	};
	
	class DeviceTestRunner extends Thread implements Runnable {
		final MainViewController mainViewController;
		final DeviceInfo deviceInfo;
		
		int nRepeatCount 		= Integer.valueOf( tfTestRepeatCount.getText());
		int runningDelayTime 	= Integer.valueOf( tfRunningDelayTime.getText() );
		int exitDelayTime 		= Integer.valueOf( tfExitDelayTime.getText() );
		boolean bInstall		= cbApkInstall.isSelected();
		boolean bUninstall		= cbApkUninstall.isSelected();
		boolean bRunningDelay	= cbRunningDelayTime.isSelected();
		boolean bExitDelay		= cbExitDelayTime.isSelected();
		
		public DeviceTestRunner( MainViewController mainViewController, DeviceInfo deviceInfo ) {
			this.mainViewController = mainViewController;
			this.deviceInfo = deviceInfo;
		}
		
		@Override
		public void run() {
			for( int count = 0; count < nRepeatCount; count++ ) {
				if( bInstall ) {
					mainViewController.updateDeviceCommant(deviceInfo, "APK Installing", true );
					
					mainViewController.updateDeviceCommant(deviceInfo, "APK Installed", true );
				}
				
				if( bRunningDelay ) {
					for( int i = 0; i < runningDelayTime; i++ ) {
						mainViewController.updateDeviceCommant(deviceInfo, String.format( "Remaining waiting time %d(s)", runningDelayTime - i ), true );
						try {
							Thread.sleep( 1000 );
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				
				if( bExitDelay ) {
					for( int i = 0; i < exitDelayTime; i++ ) {
						mainViewController.updateDeviceCommant(deviceInfo, String.format( "Remaining waiting time %d(s)", exitDelayTime - i ), true );
						try {
							Thread.sleep( 1000 );
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				if( bUninstall ) {
					mainViewController.updateDeviceCommant(deviceInfo, "APK Uninstalling", true );
					
					mainViewController.updateDeviceCommant(deviceInfo, "APK Uninstalled", true );
				}
				
				deviceInfo.setCount( deviceInfo.getCount() + 1 );
				mainViewController.updateDeviceCommant(deviceInfo, "Test done", true );
				
				Platform.runLater( TestCountIncrementRunnable );
			}
		
		}
	}
}
