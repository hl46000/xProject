package com.purehero.fx.app.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.purehero.android.ADB;
import com.purehero.android.DeviceChangeListener;
import com.purehero.android.DeviceInfo;
import com.purehero.android.SignApk;
import com.purehero.common.io.IRelease;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.app.view.apk_list.DeviceApkListViewController;
import com.purehero.fx.app.view.macro.DeviceMacroViewController;
import com.purehero.fx.app.view.test.DeviceTestViewController;
import com.purehero.fx.common.DialogUtils;
import com.purehero.fx.common.MenuUtils;
import com.purehero.fx.common.TableViewUtils;
import com.purehero.fx.control.ex.CheckBoxTableCellEx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import net.dongliu.apk.parser.ApkParser;

public class MainViewController implements DeviceChangeListener, EventHandler<ActionEvent>, IRelease {
	@FXML
	private TableView<DeviceInfo> tvDeviceInfo;
	
	@FXML
	private Menu SingleFileOption;		// 단독 장비 메뉴의 옵션
	
	@FXML
	private Menu MultiFileOption;		// 멀티 장비 메뉴의 옵션
	
	@FXML
	private Menu menuDeviceTestPath;	// Device 테스트 경로 메뉴
	
	@FXML
	private Label statusMessage;		// 상태 정보를 출력해주는 Label
	
	@FXML
	private TabPane workTabPane;		
	
	private ADB adb = null;
	
	/**
	 * UI 화면이 시작될때 처음 호출되는 함수
	 * 
	 * @throws Exception
	 */
	@FXML
    public void initialize() throws Exception {
		initTableView();
		
		MenuUtils.loadCheckMenuStatus( SingleFileOption );
		MenuUtils.loadCheckMenuStatus( MultiFileOption );
		MenuUtils.loadPathMenuText( menuDeviceTestPath );
		
		workTabPane.getTabs().add( loadDeviceTestView());
		workTabPane.getTabs().add( loadDeviceMacroView());
		workTabPane.getTabs().add( loadDeviceApkListView());
		
		MainClass.instance.addReleaseInterface( this );
	}
	
	/**
	 * @return
	 * @throws IOException 
	 */
	private Tab loadDeviceApkListView() throws IOException {
		FXMLLoader deviceApkListViewLoader = new FXMLLoader( getClass().getResource("apk_list/DeviceApkListView.fxml")); 
		Parent deviceApkListView = deviceApkListViewLoader.load();
		Tab tab = new Tab();
		tab.setText("Device APK LIST");
		tab.setContent( deviceApkListView );
				
		DeviceApkListViewController deviceApkListViewController = ( DeviceApkListViewController ) deviceApkListViewLoader.getController();
		deviceApkListViewController.setMainViewController( this );
		
		return tab;
	}

	/**
	 * Macro view 에 해당하는 fxml(DeviceMacroView.fxml) 파일을 로딩하여 Tab 에 Add 시켜서 Tab 객체를 반환한다. 
	 * 
	 * @return
	 * @throws IOException
	 */
	private Tab loadDeviceMacroView() throws IOException {
		FXMLLoader deviceMacroViewLoader = new FXMLLoader( getClass().getResource("macro/DeviceMacroView.fxml")); 
		Parent deviceMacroView = deviceMacroViewLoader.load();
		Tab tab = new Tab();
		tab.setText("Device Macro");
		tab.setContent( deviceMacroView );
				
		DeviceMacroViewController deviceMacroViewController = ( DeviceMacroViewController ) deviceMacroViewLoader.getController();
		deviceMacroViewController.setMainViewController( this );
		
		return tab;
	}

	/**
	 * Device Test view 에 해당하는 fxml(DeviceTestView.fxml) 파일을 로딩하여 Tab 에 Add 시켜서 Tab 객체를 반환한다.
	 * 
	 * @return
	 * @throws IOException
	 */
	private Tab loadDeviceTestView() throws IOException {
		FXMLLoader deviceTestViewLoader = new FXMLLoader( getClass().getResource("test/DeviceTestView.fxml")); 
		Parent deviceTestView = deviceTestViewLoader.load();
		Tab tab = new Tab();
		tab.setText("Device Test");
		tab.setContent( deviceTestView );
				
		DeviceTestViewController deviceTestViewController = ( DeviceTestViewController ) deviceTestViewLoader.getController();
		deviceTestViewController.setMainViewController( this );
		deviceTestViewController.startService();
		
		return tab;
	}

	private boolean bReleased = false;
	/**
	 * Controll 객체가 Release 되었는지를 반환 한다. 
	 * 테스트 진행 Thread 에서 Application 이 종료되었는지를 확인할 때 사용된다. 
	 * 
	 * @return
	 */
	public boolean isReleased() { return bReleased; } 
	
	/* 
	 * 앱이 종료될때 해당 UI의 리소스를 반환하기 위해 호출된다. 
	 */
	@Override
	public void Release() {
		bReleased = true;
		
		// 각 Device의 Logcat 서비스가 실행 중이면 이를 중지 시킨다. 
		List<DeviceInfo> devices = getDevices();
		for( DeviceInfo device : devices ) {
			if( device.isLogcatStarted()) {
				device.logCatStop();
			}
		}
	}
	
	/**
	 * ADB 객체를 사용할 수 있도록 받아오고, 장비의 변경을 감지하는 Listener 을 설정 한다.  
	 * 
	 * @param adb
	 */
	public void setADB( ADB adb ) {
		this.adb = adb;
		adb.setDeviceChangeListener( this );
		
		OnDeviceChangedEvent();
	}
	
	/**
	 * 현재 PC에 연결된 Android 장치들의 정보를 반환합니다. ADB로 연결이 되어 있어야 한다. 
	 * 
	 * @return
	 */
	public List<DeviceInfo> getDevices() {
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		for( IDevice device : adb.getDevices()) {
			devices.add( new DeviceInfo( device ));
		}
		return devices;
	}
	
	/**
	 * 장치 목록 TableView 을 초기화 한다.
	 */
	private void initTableView() {
		int column_index = 0;
		
		TableViewUtils.CheckBoxTableColumn	( tvDeviceInfo, "selected", 	"CENTER", column_index++, MainViewController.this );		// check box
		TableViewUtils.StringTableColumn	( tvDeviceInfo, "deviceName", 	"CENTER", column_index++ );		// 장치명
		//TableViewUtils.StringTableColumn	( tvDeviceInfo, "modelName", 	"CENTER", column_index++ );		// 모델명
		TableViewUtils.StringTableColumn	( tvDeviceInfo, "serialNumber", "CENTER", column_index++ );		// 시리얼 번호
		TableViewUtils.StringTableColumn	( tvDeviceInfo, "osVersion", 	"CENTER", column_index++ );		// OS 버전
		TableViewUtils.IntegerTableColumn	( tvDeviceInfo, "batteryLevel", "CENTER", column_index++ );		// 베터리 레벨
		TableViewUtils.StringTableColumn	( tvDeviceInfo, "state", 		"CENTER", column_index++ );		// 연결 상태
		TableViewUtils.IntegerTableColumn	( tvDeviceInfo, "count", 		"CENTER", column_index++ );		// 실행 횟수
		TableViewUtils.IntegerTableColumn	( tvDeviceInfo, "errorCount", 	"CENTER", column_index++ );		// 오류발생 횟수
		TableViewUtils.StringTableColumn	( tvDeviceInfo, "commant", 		"CENTER", column_index++ );		// 비고
	}
	
	/**
	 * ADB에 연결된 장치 정보를 얻어와 tableView 의 내용을 갱신 시킨다.
	 */
	private void refresh_device_infos() {
		String selectedDeviceSerialNumber = null;
		DeviceInfo deviceInfo = tvDeviceInfo.getSelectionModel().getSelectedItem();
		if( deviceInfo != null ) {
			selectedDeviceSerialNumber = deviceInfo.getSerialNumber();
		}
			
		List<DeviceInfo> devices = getDevices();
		
		ObservableList<DeviceInfo> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
		
		if( selectedDeviceSerialNumber != null ) {
			for( int i = 0; i < devices.size(); i++ ) {
				DeviceInfo info = devices.get(i);
				if( info.getSerialNumber().compareTo( selectedDeviceSerialNumber ) == 0 ) {
					tvDeviceInfo.getSelectionModel().select( i );
					
					break;
				}
			}
		}
	}
	
	/* 
	 * UI에서 발생하는 ActionEvent 처리를 담당한다. 
	 */
	@FXML
	@Override
	public void handle(ActionEvent event) {
		Object obj = event.getSource();
		if( obj instanceof CheckBoxTableCellEx ) {
			handleCheckBoxTableCellEx( (com.purehero.fx.control.ex.CheckBoxTableCellEx<?, ?> ) obj );
			
		} else if( obj instanceof CheckBox ) {
			handleCheckBox(( CheckBox ) obj);
			
		} else if( obj instanceof CheckMenuItem ) {
			handleCheckMenuItem(( CheckMenuItem ) obj);
		
		} else if( obj instanceof MenuItem ) {
			handleMenuItem(( MenuItem ) obj);
		}
	}
		
	/**
	 * UI에서 발생하는 ActionEvent 중 MenuItem 에서 발생한 Event 처리를 담당한다. 
	 * 
	 * MenuItem handle event 
	 * @param mi
	 */
	private void handleMenuItem(MenuItem mi) {
		switch( mi.getId()) {
		case "ID_MENU_LOGCAT"				: OnButtonClickLogcat(); break;
		case "ID_MENU_OPEN_SHELL" 			: OnButtonClickOpenShell(); break;
		case "ID_MENU_SELECT_APK_FILE"		: OnButtonClickSelectApkFile(); break;
		case "ID_MENU_MULTI_APK_FILE"		: OnButtonClickMultiSelectApkFile(); break;
		case "ID_MENU_DEVICE_TEST_APK_PATH"	: OnButtonClickPathMenuItem("APK 모니터링 경로", mi ); break;
		case "ID_MENU_DEVICE_TEST_LOG_PATH"	: OnButtonClickPathMenuItem("테스트 로그파일 경로", mi ); break;
		case "ID_MENU_DEVICE_TEST_OUT_PATH"	: OnButtonClickPathMenuItem("테스트 결과파일 경로", mi ); break;
		case "ID_MENU_DEVICE_TEST_OPTIONS"	: OnButtonClickDeviceTestOptions(); break;
		}
	}

	

	/**
	 * 테스트 옵션 설정 버튼 처리 함수
	 */
	private void OnButtonClickDeviceTestOptions() {
		try {
			DialogUtils.showResDialog("view/testView.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 경로를 설정하는 메뉴을 클릭 했을때 처리 함수
	 * 
	 * @param title
	 */
	private void OnButtonClickPathMenuItem( String title, MenuItem menuItem ) {
		File result = DialogUtils.openDirectoryDialog( title );
		if( result == null ) return;
		
		ButtonType confirmation = DialogUtils.alert( "경로 변경 확인", String.format( "'%s' 를\n\n'%s' 로 변경 하시겠습니까?\n\n", title, result.getAbsolutePath()), AlertType.CONFIRMATION );
		if( confirmation != ButtonType.OK && confirmation != ButtonType.YES ) return;

		PropertyEx prop = MainClass.instance.getProperty();
		prop.setValue( menuItem.getId(), result.getAbsolutePath());
		prop.save();
		
		MenuUtils.addMenuTitle( menuDeviceTestPath, menuItem.getId(), result.getAbsolutePath() );
		// Do somethings ~
		
		DialogUtils.alert( "경로 변경", String.format( "'%s' 가 \n\n'%s' 로 변경 되었습니다.\n\n", title, result.getAbsolutePath()), AlertType.INFORMATION );
	}

	/**
	 * UI에서 발생하는 ActionEvent 중 CheckMenuItem 에서 발생한 Event 처리를 담당한다. 
	 * @param ckMenu
	 */
	private void handleCheckMenuItem(CheckMenuItem ckMenu) {
		PropertyEx prop = MainClass.instance.getProperty();
		prop.setValue( "CHECK_MENU_STATUS_" + ckMenu.getId(), ckMenu.isSelected() ? "CHECKED" : "" );
		prop.save();
	}

	/**
	 * UI에서 발생하는 ActionEvent 중 CheckBox 에서 발생한 Event 처리를 담당한다. 
	 *  
	 * @param cb
	 */
	private void handleCheckBox(CheckBox cb) {
		switch( cb.getId()) {
		case "ID_CHECKBOX_SELECT_ALL_DEVICE" :
			boolean isSelected = cb.isSelected();
			for( DeviceInfo deviceInfo : tvDeviceInfo.getItems() ) {
				deviceInfo.setSelected( isSelected );
			}
			tvDeviceInfo.refresh();					
			break;
		}
	}

	/**
	 * UI에서 발생하는 ActionEvent 중 CheckBoxTableCellEx 에서 발생한 Event 처리를 담당한다. 
	 * 
	 * @param ckCell
	 */
	private void handleCheckBoxTableCellEx(CheckBoxTableCellEx<?, ?> ckCell) {
		DeviceInfo deviceInfo = tvDeviceInfo.getItems().get( ckCell.getIndex() );
		deviceInfo.setSelected( !deviceInfo.getSelected());
		tvDeviceInfo.refresh();
	}

	/**
	 * 디바이스 정보창에 check box 가 체크된 DeviceInfo 객체들을 반환한다.
	 * @return
	 */
	public List<DeviceInfo> getCheckedDeviceInfo() {
		List<DeviceInfo> ret = new ArrayList<DeviceInfo>();
		
		ObservableList<DeviceInfo> deviceInfoData = tvDeviceInfo.getItems();
		for( DeviceInfo deviceInfo : deviceInfoData ) {
			if( deviceInfo.getSelected()) ret.add( deviceInfo );
		}
		
		if( ret.size() < 1 ) {
			DialogUtils.alertThread( "INFORMATION", "디바이스가 선택되지 않았습니다. \n디바이스의 체크 버튼을 체크 후에 다시 시도해 주세요.", AlertType.INFORMATION );
			return null;
		}

		return ret;
	}
	
	/**
	 * APK 선택 Dialog 을 띄워 APK 파일을 선택하고, 실행 옵션에 따라서 실행한다.
	 * 단말기 리스트에서 항목이 체크된 선택된 단말기들에서 실행이 된다.  
	 */
	private void OnButtonClickMultiSelectApkFile() {
		List<DeviceInfo> deviceInfos = getCheckedDeviceInfo();
		if( deviceInfos == null ) return;
		
		final File apkFile = DialogUtils.openFileDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			final ApkParser apkParser = new ApkParser( apkFile );
			
			final String packageName 		 	= apkParser.getApkMeta().getPackageName();
			final String launcherActivityName 	= apkParser.getApkMeta().getLauncherActivityName();
			
			apkParser.close();
			new Thread( new Runnable(){
				@Override
				public void run() {
					File tmpFile = apkFileSign( MultiFileOption, apkFile );
					for( DeviceInfo deviceInfo : deviceInfos ) {
						// 단말기별로 옵션메뉴에 설정한 명령 대로 실행해 줍니다. 
						new ApkFileActionDevice( MainViewController.this, deviceInfo, tmpFile, packageName, launcherActivityName, MultiFileOption ).start();
					}
				}}).start();
			
		} catch( Exception e ) {
			e.printStackTrace();
			return;
		}
	}

	/**
	 * APK 파일을 서명하여 돌려 준다. 
	 * 메뉴항목에서 서명메뉴에 체크가 되어 있지 않으면 원본 APK 파일을 반환한다. 
	 * 
	 * @param OptionMenu
	 * @param apkFile
	 * @return
	 */
	protected File apkFileSign(Menu OptionMenu, File apkFile) {
		if( MenuUtils.isCheckMenu( OptionMenu, "APK_SIGN" )) {
			updateStatusMessage( "APK Signning : " + apkFile.getName() );
				
			File signedApkFile = new File( apkFile.getParentFile(), apkFile.getName().replace( ".apk", "_signed.apk"));
			SignApk sign = new SignApk();
			sign.sign( apkFile, signedApkFile );
			
			updateStatusMessage( "APK Signed : " + signedApkFile.getName());
			return signedApkFile;
		}
		return apkFile;
	}
	
	/**
	 * UI 하단의 status bar의 메시지를 갱신한다. 
	 * 
	 * @param message
	 */
	public void updateStatusMessage( final String message ) {
		Platform.runLater( new Runnable() {
			@Override
			public void run() {
				statusMessage.setText( message );			
			}
		});
	}
		
	private Runnable ListUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			tvDeviceInfo.refresh();			
		}
	};
	
	/**
	 * 입력된 deviceInfo 항목의 commant 내용을 갱신한다. 
	 * commant 값이 null 이면 단말기 항목만 갱신한다.  
	 * Thread 에서도 호출 가능하다. 
	 * 
	 * @param deviceInfo
	 * @param commant
	 * @param listUpdate
	 */
	public void updateDeviceCommant( DeviceInfo deviceInfo, String commant, boolean listUpdate ) {
		if( commant != null ){
			deviceInfo.setCommant( commant );
		}
		if( listUpdate ) {
			Platform.runLater( ListUpdateRunnable );
		}
	}
	
	/**
	 * 단말기 리스트에서 선택된( 체크가 아님 ) 단말기의 DeviceInfo 객체를 반환한다. 
	 * 
	 * @return
	 */
	public DeviceInfo getSelectedDeviceInfo() {
		DeviceInfo deviceInfo = tvDeviceInfo.getSelectionModel().getSelectedItem();
		if( deviceInfo == null ) {
			DialogUtils.alert( "INFORMATION", "디바이스가 선택되지 않았습니다. \n디바이스를 선택 후 다시 시도해 주세요.", AlertType.INFORMATION );
			return null;
		}
		return deviceInfo;
	}
	
	/**
	 * APK 선택 Dialog 을 띄워 APK 파일을 선택하고, 실행 옵션에 따라서 실행한다.
	 * 단말기 리스트에서 선택된 단말기에서만 실행이 된다.  
	 * @throws IOException 
	 */
	private void OnButtonClickSelectApkFile() {
		DeviceInfo deviceInfo = getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		final File apkFile = DialogUtils.openFileDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			final ApkParser apkParser = new ApkParser( apkFile );
			
			final String packageName 		 	= apkParser.getApkMeta().getPackageName();
			final String launcherActivityName 	= apkParser.getApkMeta().getLauncherActivityName();
			
			apkParser.close();
			new Thread( new Runnable(){
				@Override
				public void run() {
					File tmpFile = apkFileSign( SingleFileOption, apkFile );
					
					// 단말기별로 옵션메뉴에 설정한 명령 대로 실행해 줍니다. 
					new ApkFileActionDevice( MainViewController.this, deviceInfo, tmpFile, packageName, launcherActivityName, SingleFileOption ).start();					
				}}).start();	
			
		}  catch (Exception e1) {
			e1.printStackTrace();			
		}		
	}

	/**
	 * ADB Shell 명령어의 결과값을 받을 공통 리시버, 아무 동작도 하지 않는다. 
	 */
	public IShellOutputReceiver shellOutputReceiver = new IShellOutputReceiver() {

		@Override
		public void addOutput(byte[] data, int offset, int length) {
			//System.out.println( "1" + new String( data, offset, length ));
		}

		@Override
		public void flush() {}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	/**
	 * LogCat 수신을 위한 리시버, 아무 동작도 하지 않는다. 
	 */
	LogCatListener logcatListener = new LogCatListener() {
		@Override
		public void log(List<LogCatMessage> msgList) {
		}
	};

	/**
	 * 선택된 단말기의 Logcat 정보를 받아 와서 consol 로 출력한다.   
	 */
	private void OnButtonClickLogcat() {
		final DeviceInfo deviceInfo = getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		if( deviceInfo.isLogcatStarted()) {
			deviceInfo.logCatStop();
			List<String> logcatVal = deviceInfo.getLogCatMessages();
			int i = 0;
			for( String s : logcatVal ) {
				System.out.printf ( "[%5d] %s\n", i++, s );
			}
		} else {
			deviceInfo.logCatStart( logcatListener );
		}
	}
	
	/**
	 * 선택된 단말기의 Adb shell 창을 띄운다. 
	 */
	private void OnButtonClickOpenShell() {
		final DeviceInfo deviceInfo = getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
				
		new Thread( new Runnable(){
			@Override
			public void run() {
				String cmd = "cmd.exe /c start";
				String prog = String.format( "%s %s -s %s shell", cmd, adb.getAdbPath(), deviceInfo.getSerialNumber() );
				
				try {
					Runtime.getRuntime().exec( prog );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}}).start();
	}

	/* 
	 * 디바이스 정보에 변화가 생기면 호출되는 함수
	 */
	@Override
	public void OnDeviceChangedEvent() {
		refresh_device_infos();
	}
	
	/**
	 * 단말기에서 APK 파일 제거
	 * 
	 * @throws Exception
	 */
	public void apkFileUnistall( DeviceInfo deviceInfo, String packageName, String appName, long delay ) throws Exception {
		updateDeviceCommant(deviceInfo, String.format( "'%s' 단말기에서 삭제", appName ), true );
		try {
			deviceInfo.getInterface().uninstallPackage( packageName );
		} catch (Exception e) {
			updateDeviceCommant( deviceInfo, e.getMessage(), true );
			throw e;
		}
		updateDeviceCommant(deviceInfo, String.format( "'%s' 단말기에서 삭제 완료", appName ), true );
		Thread.sleep( delay );
	}
}
