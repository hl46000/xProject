package com.purehero.fx.app.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import net.dongliu.apk.parser.ApkParser;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.purehero.android.ADB;
import com.purehero.android.DeviceChangeListener;
import com.purehero.android.DeviceInfo;
import com.purehero.android.SignApk;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.IRelease;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.app.view.work.DeviceTestViewController;
import com.purehero.fx.common.CheckBoxTableCellEx;
import com.purehero.fx.common.DialogUtils;
import com.purehero.fx.common.MenuUtils;

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
	
	@FXML
    public void initialize() throws Exception {
		initTableView();
		
		MenuUtils.loadCheckMenuStatus( SingleFileOption );
		MenuUtils.loadCheckMenuStatus( MultiFileOption );
		MenuUtils.loadPathMenuText( menuDeviceTestPath );
		
		FXMLLoader deviceTestViewLoader = new FXMLLoader( getClass().getResource("work/DeviceTestView.fxml")); 
		Parent deviceTestView = deviceTestViewLoader.load();
		Tab tab = new Tab();
		tab.setText("Device Test");
		tab.setContent( deviceTestView );
		workTabPane.getTabs().add(tab);
		
		DeviceTestViewController deviceTestViewController = ( DeviceTestViewController ) deviceTestViewLoader.getController();
		deviceTestViewController.setMainViewController( this );
		deviceTestViewController.startService();
		
		MainClass.instance.addReleaseInterface( this );
	}
	
	@Override
	public void Release() {
		List<DeviceInfo> devices = getDevices();
		for( DeviceInfo device : devices ) {
			if( device.isLogcatStarted()) {
				device.logCatStop();
			}
		}
	}
	
	/**
	 * ADB 객체를 사용할 수 있도록 받아오고, 장비의 변경을 감지하는 Listener 을 설정 합니다. 
	 * 
	 * @param adb
	 */
	public void setADB( ADB adb ) {
		this.adb = adb;
		adb.setDeviceChangeListener( this );
		
		OnDeviceChangedEvent();
	}
	
	/**
	 * 현재 PC에 연결된 Android 장치들의 정보를 반환합니다. ADB로 연결이 되어 있어야 합니다. 
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
	
	@SuppressWarnings("unchecked")
	private void initTableView() {
		int column_index = 0;
		
		TableColumn<DeviceInfo, Boolean> tcCheckBox	= (TableColumn<DeviceInfo, Boolean>) tvDeviceInfo.getColumns().get(column_index++);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Boolean>("selected"));
		tcCheckBox.setCellFactory( new Callback<TableColumn<DeviceInfo, Boolean>, TableCell<DeviceInfo, Boolean>>() {
            public TableCell<DeviceInfo, Boolean> call(TableColumn<DeviceInfo, Boolean> p) {
            	CheckBoxTableCellEx<DeviceInfo, Boolean> ckCell = new CheckBoxTableCellEx<DeviceInfo, Boolean>(); 
            	ckCell.setOnAction( MainViewController.this );
            	return ckCell;
            }
        });
				
		StringTableColumn	( tvDeviceInfo, "modelName", 	"CENTER", column_index++ );		// 모델명
		StringTableColumn	( tvDeviceInfo, "serialNumber", "CENTER", column_index++ );		// 시리얼 번호
		StringTableColumn	( tvDeviceInfo, "osVersion", 	"CENTER", column_index++ );		// OS 버전
		IntegerTableColumn	( tvDeviceInfo, "batteryLevel", "CENTER", column_index++ );		// 베터리 레벨
		StringTableColumn	( tvDeviceInfo, "state", 		"CENTER", column_index++ );		// 연결 상태
		IntegerTableColumn	( tvDeviceInfo, "count", 		"CENTER", column_index++ );		// 실행 횟수
		IntegerTableColumn	( tvDeviceInfo, "errorCount", 	"CENTER", column_index++ );		// 오류발생 횟수
		StringTableColumn	( tvDeviceInfo, "commant", 		"CENTER", column_index++ );		// 비고
	}
	
	/**
	 * ADB에 연결된 장치 정보를 얻어와 tableView 의 내용을 갱신 시킴니다.  
	 */
	private void refresh_device_infos() {
		List<DeviceInfo> devices = getDevices();
		
		ObservableList<DeviceInfo> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
	}
	
	/**
	 * Integer column 을 설정합니다. 
	 * 
	 * @param tvDeviceInfo
	 * @param property
	 * @param align
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void IntegerTableColumn(TableView<DeviceInfo> tvDeviceInfo, String property, String align, int index ) {
		TableColumn<DeviceInfo, Integer> tableColumn 	= (TableColumn<DeviceInfo, Integer>) tvDeviceInfo.getColumns().get(index);
		tableColumn.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Integer>(property));
		tableColumn.setStyle( String.format( "-fx-alignment: %s;", align ));
	}

	/**
	 * 문자열 column 을 설정합니다. 
	 * 
	 * @param tvDeviceInfo
	 * @param property
	 * @param align
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	private void StringTableColumn( TableView<DeviceInfo> tvDeviceInfo, String property, String align, int index ) {
		TableColumn<DeviceInfo, String> tableColumn 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get( index );
		tableColumn.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>(property));
		tableColumn.setStyle( String.format( "-fx-alignment: %s;", align ));
	}

	@FXML
	@Override
	public void handle(ActionEvent event) {
		Object obj = event.getSource();
		if( obj instanceof CheckBoxTableCellEx ) {
			handleCheckBoxTableCellEx( ( CheckBoxTableCellEx<?, ?> ) obj );
			
		} else if( obj instanceof CheckBox ) {
			handleCheckBox(( CheckBox ) obj);
			
		} else if( obj instanceof CheckMenuItem ) {
			handleCheckMenuItem(( CheckMenuItem ) obj);
		
		} else if( obj instanceof MenuItem ) {
			handleMenuItem(( MenuItem ) obj);
		}
	}
		
	/**
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
		case "ID_MENU_DEVICE_TEST_OPTIONS"	: ONButtonClickDeviceTestOptions(); break;
		}
	}

	

	private void ONButtonClickDeviceTestOptions() {
		try {
			DialogUtils.showResDialog("view/testView.fxml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
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
	 * CheckMenuItem handle event 
	 * @param ckMenu
	 */
	private void handleCheckMenuItem(CheckMenuItem ckMenu) {
		PropertyEx prop = MainClass.instance.getProperty();
		prop.setValue( "CHECK_MENU_STATUS_" + ckMenu.getId(), ckMenu.isSelected() ? "CHECKED" : "" );
		prop.save();
	}

	/**
	 * CheckBox handle event 
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
	 * CheckBoxTableCellEx handle event 
	 * @param ckCell
	 */
	private void handleCheckBoxTableCellEx(CheckBoxTableCellEx<?, ?> ckCell) {
		DeviceInfo deviceInfo = tvDeviceInfo.getItems().get( ckCell.getIndex() );
		deviceInfo.setSelected( !deviceInfo.getSelected());
		tvDeviceInfo.refresh();
	}

	/**
	 * 디바이스 정보창에 check box 가 체크된 객체들을 반환 합니다. 
	 * @return
	 */
	private List<DeviceInfo> getCheckedDeviceInfo() {
		List<DeviceInfo> ret = new ArrayList<DeviceInfo>();
		
		ObservableList<DeviceInfo> deviceInfoData = tvDeviceInfo.getItems();
		for( DeviceInfo deviceInfo : deviceInfoData ) {
			if( deviceInfo.getSelected()) ret.add( deviceInfo );
		}
		
		if( ret.size() < 1 ) {
			DialogUtils.alert( "INFORMATION", "디바이스가 선택되지 않았습니다. \n디바이스의 체크 버튼을 체크 후에 다시 시도해 주세요.", AlertType.INFORMATION );
			return null;
		}

		return ret;
	}
	
	private void OnButtonClickMultiSelectApkFile() {
		List<DeviceInfo> deviceInfos = getCheckedDeviceInfo();
		if( deviceInfos == null ) return;
		
		final File apkFile = DialogUtils.openFileDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			final ApkParser apkParser = new ApkParser( apkFile );
			new Thread( new Runnable(){
				@Override
				public void run() {
					File tmpFile = apkFileSign( MultiFileOption, apkFile );
					for( DeviceInfo deviceInfo : deviceInfos ) {
						// 단말기별로 옵션메뉴에 설정한 명령 대로 실행해 줍니다. 
						new ApkFileActionDevice( MainViewController.this, deviceInfo, tmpFile, apkParser, MultiFileOption ).start();
					}
				}}).start();	
			
		}  catch (Exception e1) {
			e1.printStackTrace();			
		}		
		
	}

	/**
	 * APK 파일을 서명하여 돌려 줍니다. 메뉴항목에서 서명메뉴에 체크가 되어 있지 않으면 원본 APK 파일을 반환합니다. 
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
	 * 입력된 deviceInfo 항목의 commant 내용을 갱신합니다. Thread 에서도 호출 가능하다. 
	 * 
	 * @param deviceInfo
	 * @param commant
	 * @param listUpdate
	 */
	public void updateDeviceCommant( DeviceInfo deviceInfo, String commant, boolean listUpdate ) {
		deviceInfo.setCommant( commant );
		if( listUpdate ) {
			Platform.runLater( ListUpdateRunnable );	
		}
	}
	
	/**
	 * 
	 * 
	 * @return
	 */
	private DeviceInfo getSelectedDeviceInfo() {
		DeviceInfo deviceInfo = tvDeviceInfo.getSelectionModel().getSelectedItem();
		if( deviceInfo == null ) {
			DialogUtils.alert( "INFORMATION", "디바이스가 선택되지 않았습니다. \n디바이스를 선택 후 다시 시도해 주세요.", AlertType.INFORMATION );
			return null;
		}
		return deviceInfo;
	}
	
	/**
	 * APK 선택 Dialog 을 띄워 APK 파일을 선택하고, 실행 옵션에 따라서 실행합니다. 
	 * @throws IOException 
	 */
	private void OnButtonClickSelectApkFile() {
		DeviceInfo deviceInfo = getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		final File apkFile = DialogUtils.openFileDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			final ApkParser apkParser = new ApkParser( apkFile );
			new Thread( new Runnable(){
				@Override
				public void run() {
					File tmpFile = apkFileSign( SingleFileOption, apkFile );
					
					// 단말기별로 옵션메뉴에 설정한 명령 대로 실행해 줍니다. 
					new ApkFileActionDevice( MainViewController.this, deviceInfo, tmpFile, apkParser, SingleFileOption ).start();					
				}}).start();	
			
		}  catch (Exception e1) {
			e1.printStackTrace();			
		}		
	}

	/**
	 * ADB Shell 명령어의 결과값을 받을 공통 리시버 입니다. 
	 */
	public IShellOutputReceiver shellOutputReceiver = new IShellOutputReceiver() {

		@Override
		public void addOutput(byte[] data, int offset, int length) {
			System.out.println( new String( data, offset, length ));
		}

		@Override
		public void flush() {}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	LogCatListener logcatListener = new LogCatListener() {
		@Override
		public void log(List<LogCatMessage> msgList) {
			for( LogCatMessage msg : msgList) {
				System.out.println ( msg.toString() );
			}
		}
	};

	/**
	 * 선택된 단말기의 Logcat 정보를 consol 로 출력합니다.  
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
	 * 선택된 단말기의 Adb shell 창을 띄움니다. 
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
	 * 디바이스 정보에 변화가 생기면 호출되는 함수 입니다. 
	 */
	@Override
	public void OnDeviceChangedEvent() {
		refresh_device_infos();
	}
}
