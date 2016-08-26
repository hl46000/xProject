package android.touch.macro.v2.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.touch.macro.v2.DataManager;
import android.touch.macro.v2.PropertyV2;
import android.touch.macro.v2.TouchMacroV2;
import android.touch.macro.v2.UtilV2;
import android.touch.macro.v2.adb.AdbDevice;
import android.touch.macro.v2.adb.AdbV2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class deviceController {
	@FXML
	private TextField txAdbPath;
	
	@FXML
	private TableView<AdbDevice> tvDeviceInfo;
	
	@FXML
	private ContextMenu cmDeviceMenu;
	
	DataManager dataManager = null;
	
	@FXML
    public void initialize() {
		dataManager = TouchMacroV2.instance.getDataManager();
		
		if( !initializeAndroidSDKPath()) return;
        if( !initializeDeviceInfo()) return;
        
        dataManager.setDeviceController( this );
	}
	
	/**
	 * 
	 */
	private boolean initializeAndroidSDKPath() {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		try {
			File sdk_path 	= new File( prop.getValue("SDK_PATH"));
			File adb_path 	= new File( prop.getValue("ADB_PATH"));
			File aapt_path 	= new File( prop.getValue("AAPT_PATH"));
			
			txAdbPath.setText( sdk_path.getAbsolutePath() );
			
			if( !AdbV2.setAdbPath( adb_path.getAbsolutePath() )) {
				System.err.println( String.format( "\t'%s' 파일을 확인 후 다시 시도해 주세요.", prop.getPropertyFilePath()));
				return false;
			}
			
			if( !AdbV2.setAaptPath( aapt_path.getAbsolutePath() )) {
				System.err.println( String.format( "\t'%s' 파일을 확인 후 다시 시도해 주세요.", prop.getPropertyFilePath()));
				return false;
			}
			
		} catch( Exception e ) {
			txAdbPath.setText( "ANDROID SDK 경로를 설정하여 주십시요." );
			return false;
		}
		
		return true;
	}
	
	/**
	 * 디바이스 정보를 초기화 시켜주는 함수
	 * 
	 * @return
	 */
	private boolean initializeDeviceInfo() {
		onClick_refreshDeviceInfo();
		return true;
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void onClick_refreshDeviceInfo() {
		AdbV2.debugLog = true;
		ArrayList<AdbDevice> devices = AdbV2.getDevices();
		
		int column_index = 0;
		
		TableColumn<AdbDevice, Boolean> tcCheckBox	= (TableColumn<AdbDevice, Boolean>) tvDeviceInfo.getColumns().get(column_index++);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<AdbDevice, Boolean>("selected"));
		tcCheckBox.setCellFactory( new Callback<TableColumn<AdbDevice, Boolean>, TableCell<AdbDevice, Boolean>>() {
            public TableCell<AdbDevice, Boolean> call(TableColumn<AdbDevice, Boolean> p) {
            	return new CheckBoxTableCell<AdbDevice, Boolean>();            
            }
        });
		
		
		TableColumn<AdbDevice, String> tcModelName 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcModelName.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("model"));
		tcModelName.setStyle("-fx-alignment: CENTER;");
				
		TableColumn<AdbDevice, String> tcSerial 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcSerial.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("serialNumber"));
		tcSerial.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<AdbDevice, String> tcOsVersion 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOsVersion.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("os_ver"));
		tcOsVersion.setStyle("-fx-alignment: CENTER;");
		
		/*
		TableColumn<AdbDevice, String> tcOrientation 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOrientation.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("orientation"));
		*/
		TableColumn<AdbDevice, String> tcDisplayOn 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcDisplayOn.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("displayOn"));
		tcDisplayOn.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<AdbDevice, String> tcStatus		= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcStatus.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("status"));
		tcStatus.setStyle("-fx-alignment: CENTER;");
		
		ObservableList<AdbDevice> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
	}
	
	
	@FXML
	private void event_handle_action( ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj instanceof CheckBox) {
			CheckBox cb = (CheckBox) obj;
			
			switch( cb.getId() ) {
			case "ID_CHECKBOX_SELECT_ALL_DEVICE" 	: onClickCheckBox_selectAllDevice( cb.isSelected() ); break;
			}
			
		} else if( obj instanceof Button) {
			Button btn = ( Button ) obj;
			
			switch( btn.getId() ) {
			case "ID_BUTTON_REFRESH_DEVICE_INFO" 	: onClick_refreshDeviceInfo(); break;
			case "ID_BTN_CHANGE_SDK_PATH"			: onClick_changeSdkPath(); break;
			}
		} else if( obj instanceof MenuItem ) {
			MenuItem mi = ( MenuItem ) obj;
			
			switch( mi.getId() ) {
			case "ID_MENU_APK_INSTALL"				: onClickMenu_ApkInstall(); break;
			case "ID_MENU_APK_UNINSTALL"			: onClickMenu_ApkUninstall(); break;
			case "ID_MENU_APK_UPDATE"				: onClickMenu_ApkUpdate(); break;
			case "ID_MENU_APK_REINSTALL"			: onClickMenu_ApkReinstall(); break;
			case "ID_MENU_APK_EXCUTE"				: onClickMenu_ApkExcute(); break;
			case "ID_MENU_DISPLAY_ON"				: onClickMenu_DisplayOn(); break;
			case "ID_MENU_DISPLAY_OFF"				: onClickMenu_DisplayOff(); break;
			}
		}
	}
	
	
	private void onClickMenu_ApkExcute() {
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			UtilV2.alertWindow( "Information", "체크된 단말기가 없습니다.", AlertType.WARNING );
		}
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		
		File apk_file = APKFileChooser("단말기에서 실행 할 APK 파일을 선택해 주세요");
		
		if( apk_file == null ) 	return ;
		if( !apk_file.exists()) return ;
		
		List<String> keys = new ArrayList<String>();
		keys.add( "package" );
		keys.add( "launchable-activity" );
		
		Map<String, String> result = AdbV2.getInformationFromApk( apk_file, keys );
		
		String package_name = result.get("package");
		String launch_activity = result.get("launchable-activity");
		
		prop.setValue( "APK_PATH", apk_file.getParentFile().getAbsolutePath() );
		try {
			prop.save("TouchMacro v2");
			
			String cmd = String.format( "shell am start -n '%s/%s'", package_name, launch_activity );
			for( AdbDevice device : devices ) {
				for( String log : AdbV2.Command( cmd, device )) {
					System.out.println( log );
				}				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	private void onClickMenu_ApkReinstall() {
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			UtilV2.alertWindow( "Information", "체크된 단말기가 없습니다.", AlertType.WARNING );
		}
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		
		File apk_file = APKFileChooser("단말기에서 삭제할 APK 파일을 선택해 주세요");
		
		if( apk_file == null ) 	return ;
		if( !apk_file.exists()) return ;
		
		List<String> keys = new ArrayList<String>();
		keys.add( "package" );
		
		Map<String, String> result = AdbV2.getInformationFromApk( apk_file, keys );
		
		String package_name = result.get("package");
		
		prop.setValue( "APK_PATH", apk_file.getParentFile().getAbsolutePath() );
		try {
			prop.save("TouchMacro v2");
			
			String cmd1 = String.format( "uninstall \"%s\"", package_name );
			String cmd2 = String.format( "install \"%s\"", apk_file.getAbsolutePath() );
								
			for( AdbDevice device : devices ) {
				for( String log : AdbV2.Command( cmd1, device )) {
					System.out.println( log );
				}
				
				for( String log : AdbV2.Command( cmd2, device )) {
					System.out.println( log );
				}				
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 선택된 Device들의 화면을 OFF 시킴니다.
	 */
	private void onClickMenu_DisplayOff() {
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			return;
		}
		
		for( AdbDevice device : devices ) {
			if( device.getDisplayOn().compareToIgnoreCase("OFF") != 0 ) {
				AdbV2.Command( "shell input keyevent KEYCODE_POWER", device );
				device.setDisplayOn( "OFF" );
			}
		}
		
		try {
			tvDeviceInfo.refresh();
		} catch( Exception ex ) {
		}	
	}

	/**
	 * 선택된 Device들의 화면을 ON 시킴니다.
	 */
	private void onClickMenu_DisplayOn() {
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			return;
		}
		
		for( AdbDevice device : devices ) {
			if( device.getDisplayOn().compareToIgnoreCase("ON") != 0 ) {
				AdbV2.Command( "shell input keyevent KEYCODE_POWER", device );
				device.setDisplayOn( "ON" );
			}
		}
		
		try {
			tvDeviceInfo.refresh();
		} catch( Exception ex ) {
		}	
	}

	private void onClickMenu_ApkUpdate() {
		ApkFileCommand( "Update 할 APK 파일을 선택해 주세요", "install -r \"%s\"" );
		
	}

	private void onClickMenu_ApkUninstall() {
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			UtilV2.alertWindow( "Information", "체크된 단말기가 없습니다.", AlertType.WARNING );
			return;
		}
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		
		File apk_file = APKFileChooser("단말기에서 삭제할 APK 파일을 선택해 주세요");
		
		if( apk_file == null ) 	return;
		if( !apk_file.exists()) return;
		
		List<String> keys = new ArrayList<String>();
		keys.add( "package" );
		
		Map<String, String> result = AdbV2.getInformationFromApk( apk_file, keys );
		String package_name = result.get("package");
		
		prop.setValue( "APK_PATH", apk_file.getParentFile().getAbsolutePath() );
		try {
			prop.save("TouchMacro v2");
			
			String cmd = String.format( "uninstall \"%s\"", package_name );
								
			for( AdbDevice device : devices ) {
				for( String log : AdbV2.Command( cmd, device )) {
					System.out.println( log );
				}
			}					
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 파일 Dialog 을 띄워서 APK 파일의 경로를 입력 받습니다. 
	 * 
	 * @param title 파일 Dialog 에 보여질 Title 문구
	 * @return
	 */
	private File APKFileChooser( String title ) {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		
		String pre_apk_path = prop.getValue("APK_PATH");
		File apk_path = new File( pre_apk_path == null ? "" : pre_apk_path );
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle( title );
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("APK", "*.apk") );
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("ALL Files", "*.*") );
		if( apk_path.exists()) {
			if( apk_path.isFile() ) apk_path = apk_path.getParentFile();
			fileChooser.setInitialDirectory( apk_path );
		}
				
		return fileChooser.showOpenDialog( TouchMacroV2.instance.getPrimaryStage());
	}

	private void ApkFileCommand( String title, String cmd_format ) {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			UtilV2.alertWindow( "Information", "체크된 단말기가 없습니다.", AlertType.WARNING );
			return;
		}
		
		File result = APKFileChooser( title );
		if( result == null ) return;
		
		if( result.exists()) {
			prop.setValue( "APK_PATH", result.getParentFile().getAbsolutePath() );
			try {
				prop.save("TouchMacro v2");
				
				String cmd = String.format( cmd_format, result.getAbsoluteFile());
				for( AdbDevice device : devices ) {
					for( String log : AdbV2.Command( cmd, device )) {
						System.out.println( log );
					}
				}					
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	private void onClickMenu_ApkInstall() {
		ApkFileCommand( "설치할 APK 파일을 선택해 주세요", "install \"%s\"" );
	}

	/** 
	 * 마우스 이벤트를 전달받을 함수
	 * 
	 * @param e
	 */
	@FXML
	private void event_handle_mouse(MouseEvent e) {
		Control ctrl = ( Control ) e.getSource();
		
		switch( ctrl.getId()) {
		case "tvDeviceInfo" 		: mouse_handler_table_view(e, ctrl); break;
		}		
    }
	
	/**
	 * @param e
	 * @param ctrl 
	 */
	private void mouse_handler_table_view(MouseEvent e, Control ctrl) {
		if( e.getButton() == MouseButton.SECONDARY ) {
			tvDeviceInfo.getSelectionModel().clearSelection();
			
			cmDeviceMenu.hide();
			cmDeviceMenu.show( tvDeviceInfo, e.getScreenX(), e.getScreenY());
		}
	}

	/**
	 * @param b
	 */
	private void onClickCheckBox_selectAllDevice(boolean b) {
		ObservableList<AdbDevice> deviceInfoData = tvDeviceInfo.getItems();
		for( AdbDevice deviceInfo : deviceInfoData ) {
			deviceInfo.setSelected( b );
		}
		
		try {
			tvDeviceInfo.refresh();
		} catch( Exception e ) {
		}		
	}

	private void onClick_changeSdkPath() {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		String sdk_path = prop.getValue("SDK_PATH");
				
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setTitle("Android SDK 폴더를 선택해 주세요");
		if( sdk_path != null ) {
			dirChooser.setInitialDirectory( new File( sdk_path ));
		}
				
		File result = dirChooser.showDialog( TouchMacroV2.instance.getPrimaryStage());
		if( result == null ) return;
		
		if( result.exists()) {
			
			// adb.exe 파일 찾기
			File platform_tools = new File( result, "platform-tools" );
			if( !platform_tools.exists() ) {
				UtilV2.alertWindow( "Information", String.format( "platform-tools 폴더를 찾을 수 없습니다.\n('%s' 폴더를 확인해 주세요.)", result.getAbsolutePath()),  AlertType.WARNING );
				return ;
			}
			
			File adb = new File( platform_tools, "adb.exe" );
			if( !adb.exists() ) {
				UtilV2.alertWindow( "Information", String.format( "adb.exe 파일을 찾을 수 없습니다.\n('%s')", adb.getAbsolutePath()),  AlertType.WARNING );
				return ;
			}
			
			AdbV2.setAdbPath( adb.getAbsolutePath() );
			prop.setValue( "ADB_PATH", adb.getAbsolutePath() );
			
			// aapt.exe 파일 찾기
			File build_tools = new File( result, "build-tools" );
			if( !build_tools.exists()) {
				UtilV2.alertWindow( "Information", String.format( "build-tools 폴더를 찾을 수 없습니다.\n('%s' 폴더를 확인해 주세요)", result.getAbsolutePath()),  AlertType.WARNING );
				return ;
			}
			
			for( File subDir : build_tools.listFiles()) {
				File aapt = new File( subDir, "aapt.exe" );
				if( aapt.exists()) {
					AdbV2.setAaptPath( aapt.getAbsolutePath() );
					prop.setValue( "AAPT_PATH", aapt.getAbsolutePath() );
				}
			}
			
			prop.setValue( "SDK_PATH", result.getAbsolutePath() );
			try {
				prop.save("TouchMacro v2");
				txAdbPath.setText( result.getAbsolutePath() );
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 디바이스 정보창에 현재 선택된 디바이스의 객체를 반환 합니다. 선택된 디바이스가 없으면 null 반환
	 * 
	 * @return
	 */
	public AdbDevice getSelectedDeviceItem() {
		return tvDeviceInfo.getSelectionModel().getSelectedItem();		
	}

	/**
	 * 디바이스 정보창에 check box 가 체크된 객체들을 반환 합니다. 
	 * @return
	 */
	public List<AdbDevice> getCheckedDeviceInfo() {
		List<AdbDevice> ret = new ArrayList<AdbDevice>();
		
		ObservableList<AdbDevice> deviceInfoData = tvDeviceInfo.getItems();
		for( AdbDevice deviceInfo : deviceInfoData ) {
			if( deviceInfo.getSelected()) ret.add( deviceInfo );
		}
		return ret;
	}
}
