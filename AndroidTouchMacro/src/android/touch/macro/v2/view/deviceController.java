package android.touch.macro.v2.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.touch.macro.v2.DataManager;
import android.touch.macro.v2.PropertyV2;
import android.touch.macro.v2.TouchMacroV2;
import android.touch.macro.v2.adb.AdbDevice;
import android.touch.macro.v2.adb.AdbV2;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
		
		if( !initializeAdbPath()) return;
        if( !initializeDeviceInfo()) return;
        
        dataManager.setDeviceController( this );
	}
	
	/**
	 * 
	 */
	private boolean initializeAdbPath() {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		File adb_file = new File( prop.getValue("ADB_PATH"));
		
		txAdbPath.setText( adb_file.getAbsolutePath() );
		
		if( !AdbV2.setAdbPath( adb_file.getAbsolutePath() )) {
			System.err.println( String.format( "\t'%s' 파일을 확인 후 다시 시도해 주세요.", prop.getPropertyFilePath()));
			
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
                System.out.println("call");
            	return new CheckBoxTableCell<AdbDevice, Boolean>();
            }
            
        });
		
		
		TableColumn<AdbDevice, String> tcModelName 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcModelName.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("model"));
		
		TableColumn<AdbDevice, String> tcSerial 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcSerial.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("serialNumber"));
		
		TableColumn<AdbDevice, String> tcOsVersion 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOsVersion.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("os_ver"));
		
		/*
		TableColumn<AdbDevice, String> tcOrientation 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOrientation.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("orientation"));
		*/
		TableColumn<AdbDevice, String> tcDisplayOn 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcDisplayOn.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("displayOn"));
				
		TableColumn<AdbDevice, String> tcStatus		= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcStatus.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("status"));
		
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
			case "ID_BTN_CHANGE_ADB_PATH"			: onClick_changeAdbPath(); break;
			}
		} else if( obj instanceof MenuItem ) {
			MenuItem mi = ( MenuItem ) obj;
			
			switch( mi.getId() ) {
			case "ID_MENU_APK_INSTALL"				: onClickMenu_ApkInstall(); break;
			case "ID_MENU_APK_UNINSTALL"			: onClickMenu_ApkUninstall(); break;
			case "ID_MENU_APK_UPDATE"				: onClickMenu_ApkUpdate(); break;
			}
		}
	}
	
	
	private void onClickMenu_ApkUpdate() {
		// TODO Auto-generated method stub
		
	}

	private void onClickMenu_ApkUninstall() {
		// TODO Auto-generated method stub
		
	}

	private void onClickMenu_ApkInstall() {
		List<AdbDevice> devices = getCheckedDeviceInfo();
		if( devices.size() < 1 ) {
			System.err.println("체크된 단말기가 없습니다. ");
			return;
		}
		
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		File apk_path = new File( prop.getValue("APK_PATH"));
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("APK 파일을 선택해 주세요");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("APK", "*.apk") );
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("ALL Files", "*.*") );
		if( apk_path.exists()) {
			fileChooser.setInitialDirectory( apk_path );
		}
				
		File result = fileChooser.showOpenDialog( TouchMacroV2.instance.getPrimaryStage());
		if( result == null ) return;
		
		if( result.exists()) {
			String name = result.getName().toLowerCase(); 
			if( name.endsWith(".apk")) { 
				prop.setValue( "APK_PATH", result.getAbsolutePath() );
				try {
					prop.save("TouchMacro v2");
					
					String cmd = String.format( "install \"%s\"", result.getAbsoluteFile());
					
					
					for( AdbDevice device : devices ) {
						AdbV2.Command( cmd, device);
					}					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
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
			
		} else {
			AdbDevice device = tvDeviceInfo.getSelectionModel().getSelectedItem();
			if( device != null ) {
				device.setSelected( !device.getSelected() );
				/*
				try {
					tvDeviceInfo.refresh();
				} catch( Exception ex ) {
				}
				*/
			}
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
		/*
		try {
			tvDeviceInfo.refresh();
		} catch( Exception e ) {
		}
		*/
	}

	private void onClick_changeAdbPath() {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		File adb_file = new File( prop.getValue("ADB_PATH"));
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("ADB 파일을 선택해 주세요");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("ADB", "adb.exe") );
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("ALL Files", "*.*") );
		fileChooser.setInitialDirectory( adb_file.getParentFile() );
		fileChooser.setInitialFileName( adb_file.getName());
		
		File result = fileChooser.showOpenDialog( TouchMacroV2.instance.getPrimaryStage());
		if( result == null ) return;
		
		if( result.exists()) {
			String name = result.getName().toLowerCase(); 
			if( name.compareTo("adb.exe") == 0 || name.startsWith("adb")) { 
				
				AdbV2.setAdbPath( result.getAbsolutePath() );
				prop.setValue( "ADB_PATH", result.getAbsolutePath() );
				try {
					prop.save("TouchMacro v2");
					txAdbPath.setText( result.getAbsolutePath() );
					
				} catch (IOException e) {
					e.printStackTrace();
				}
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
