package com.purehero.fx.app.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.purehero.aos.signapk.SignApk;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.ADB;
import com.purehero.fx.app.DeviceChangeListener;
import com.purehero.fx.app.DeviceInfo;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.common.DialogUtils;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import net.dongliu.apk.parser.ApkParser;

public class mainViewController implements DeviceChangeListener, EventHandler<ActionEvent> {
	@FXML
	private TableView<DeviceInfo> tvDeviceInfo;
	
	@FXML
	private Menu SingleFileOption;		// 단독 장비 메뉴의 옵션
	
	@FXML
	private Menu MultiFileOption;		// 멀티 장비 메뉴의 옵션
	
	private ADB adb = null;
	
	@FXML
    public void initialize() throws Exception {
		adb = MainClass.instance.getADB(); 
		adb.setDeviceChangeListener( this );
		
		refresh_device_infos();
		
		// 메뉴 항목 중 Check 항목 들은 이미 저장된 값을 읽어 와서 설정해 준다. 
		PropertyEx prop = MainClass.instance.getProperty();
		ObservableList<MenuItem> items = SingleFileOption.getItems();
		for( MenuItem item : items ) {
			if( item instanceof CheckMenuItem ) {
				CheckMenuItem ck = ( CheckMenuItem ) item;
				
				String val = prop.getValue( "CHECK_MENU_STATUS_" + item.getId());
				ck.setSelected( val != null && val.compareTo("CHECKED") == 0 ); 
			}
		}
		items = MultiFileOption.getItems();
		for( MenuItem item : items ) {
			if( item instanceof CheckMenuItem ) {
				CheckMenuItem ck = ( CheckMenuItem ) item;
				
				String val = prop.getValue( "CHECK_MENU_STATUS_" + item.getId());
				ck.setSelected( val != null && val.compareTo("CHECKED") == 0 ); 
			}
		}
				
		prop.save();
	}
	
	public List<DeviceInfo> getDevices() {
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		for( IDevice device : adb.getDevices()) {
			devices.add( new DeviceInfo( device ));
		}
		return devices;
	}
	
	@SuppressWarnings("unchecked")
	private void refresh_device_infos() {
		List<DeviceInfo> devices = getDevices();

		int column_index = 0;
		
		TableColumn<DeviceInfo, Boolean> tcCheckBox	= (TableColumn<DeviceInfo, Boolean>) tvDeviceInfo.getColumns().get(column_index++);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Boolean>("selected"));
		tcCheckBox.setCellFactory( new Callback<TableColumn<DeviceInfo, Boolean>, TableCell<DeviceInfo, Boolean>>() {
            public TableCell<DeviceInfo, Boolean> call(TableColumn<DeviceInfo, Boolean> p) {
            	CheckBoxTableCell<DeviceInfo, Boolean> ckCell = new CheckBoxTableCell<DeviceInfo, Boolean>(); 
            	ckCell.setOnAction( mainViewController.this );
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
		if( obj instanceof CheckBoxTableCell ) {
			CheckBoxTableCell<?, ?> ckCell = ( CheckBoxTableCell<?, ?> ) obj;
			
			DeviceInfo deviceInfo = tvDeviceInfo.getItems().get( ckCell.getIndex() );
			deviceInfo.setSelected( !deviceInfo.getSelected());
			
		} else if( obj instanceof CheckBox ) {
			CheckBox cb = ( CheckBox ) obj;
			
			switch( cb.getId()) {
			case "ID_CHECKBOX_SELECT_ALL_DEVICE" :
				boolean isSelected = cb.isSelected();
				for( DeviceInfo deviceInfo : tvDeviceInfo.getItems() ) {
					deviceInfo.setSelected( isSelected );
				}
				tvDeviceInfo.refresh();					
				break;
			}
		} else if( obj instanceof CheckMenuItem ) {
			CheckMenuItem ckMenu = ( CheckMenuItem ) obj;
			PropertyEx prop = MainClass.instance.getProperty();
			prop.setValue( "CHECK_MENU_STATUS_" + ckMenu.getId(), ckMenu.isSelected() ? "CHECKED" : "" );
			prop.save();
		
		} else if( obj instanceof MenuItem ) {
			MenuItem mi = ( MenuItem ) obj;
			
			switch( mi.getId()) {
			case "ID_MENU_OPEN_SHELL" 			: OnButtonClickOpenShell(); break;
			case "ID_MENU_SELECT_APK_FILE"		: OnButtonClickSelectApkFile(); break;
			}
		}
	}
	
	Runnable ListUpdateRunnable = new Runnable() {
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
		
		File apkFile = DialogUtils.openDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			@SuppressWarnings("resource")
			ApkParser apkParser = new ApkParser( apkFile );
			File signedApkFile = new File( apkFile.getParentFile(), apkFile.getName().replace( ".apk", "_signed.apk")); 
			
			final String packageName 			= apkParser.getApkMeta().getPackageName();;
			final String launcherActivityName 	= apkParser.getApkMeta().getLauncherActivityName();;
					
			new Thread( new Runnable(){
				@Override
				public void run() {
					if( isCheckMenu( SingleFileOption, "ID_CKMENU_SIGNLE_APK_SIGN" )) {
						updateDeviceCommant( deviceInfo, "APK Signning", true );
										
						SignApk sign = new SignApk();
						sign.sign( apkFile, signedApkFile );
						
						updateDeviceCommant( deviceInfo, "APK Signed", true );
					}
					
					if( isCheckMenu( SingleFileOption, "ID_CKMENU_SIGNLE_APK_UNINSTALL" )) {
						updateDeviceCommant( deviceInfo, "APK Uninstalling", true );
						try {
							deviceInfo.getInterface().uninstallPackage( packageName );
							updateDeviceCommant( deviceInfo, "Failed: APK Uninstalled", true );
						} catch (Exception e) {
							e.printStackTrace();
							updateDeviceCommant( deviceInfo, "Failed: APK Uninstall", true );
							return;
						}					
					}
					if( isCheckMenu( SingleFileOption, "ID_CKMENU_SIGNLE_APK_INSTALL" )) {
						updateDeviceCommant( deviceInfo, "APK Installing", true );
						try {
							deviceInfo.getInterface().installPackage( signedApkFile.getAbsolutePath(), false );
							updateDeviceCommant( deviceInfo, "APK Installed", true );
						} catch (Exception e) {
							e.printStackTrace();
							updateDeviceCommant( deviceInfo, "Failed: APK Installed", true );
							return;
						}
						
					}
					if( isCheckMenu( SingleFileOption, "ID_CKMENU_SIGNLE_APK_RUNNING" )) {
						updateDeviceCommant( deviceInfo, "APK Running", true );
						try {
							deviceInfo.getInterface().executeShellCommand( String.format( "am start -n '%s/%s'", packageName, launcherActivityName), shellOutputReceiver );						
						} catch (Exception e) {
							e.printStackTrace();
							updateDeviceCommant( deviceInfo, "Failed: APK Running", true );
							return;
						}
					}
					
				}}).start();	
			
		}  catch (Exception e1) {
			e1.printStackTrace();			
		}		
	}

	IShellOutputReceiver shellOutputReceiver = new IShellOutputReceiver() {

		@Override
		public void addOutput(byte[] data, int offset, int length) {
			System.out.println( new String( data, offset, length ));
		}

		@Override
		public void flush() {}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	/**
	 * menu의 하위 메뉴 중 id 값에 해당하는 메뉴가 check 되어 있는지를 확인 합니다. 
	 * @param Menu
	 * @param string
	 * @return
	 */
	protected boolean isCheckMenu( Menu menu, String id ) {
		ObservableList<MenuItem> items = menu.getItems();
		for( MenuItem item : items ) {
			if( item.getId().compareTo( id ) != 0 ) continue;
			
			if( item instanceof CheckMenuItem ) {
				CheckMenuItem ck = ( CheckMenuItem ) item;
				return ck.isSelected();
			}
		}
		return false;
	}

	/**
	 * 선택된 단말기의 Adb shell 창을 띄움니다. 
	 */
	private void OnButtonClickOpenShell() {
		DeviceInfo deviceInfo = getSelectedDeviceInfo();
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

	@Override
	public void OnDeviceChangedEvent() {
		refresh_device_infos();
	}
	
	
	class CheckBoxTableCell<S, T> extends TableCell<S, T> implements EventHandler<ActionEvent> {
		private final CheckBox checkBox;
	    private ObservableValue<T> ov;

	    public CheckBoxTableCell() {
	        this.checkBox = new CheckBox();
	        this.checkBox.setAlignment(Pos.CENTER);

	        setAlignment(Pos.CENTER);
	        setGraphic(checkBox);
	        
	        checkBox.setOnAction(this);
	        chClickEvent = new ActionEvent( this, null );
	    } 

	    ActionEvent chClickEvent = null;
	    EventHandler<ActionEvent> actionEvent = null;
	    public void setOnAction( EventHandler<ActionEvent> arg0) {
	    	actionEvent = arg0;	  
	    }
	    
	    @Override 
	    public void updateItem(T item, boolean empty) {
	        super.updateItem(item, empty);
	        
	        if (empty) {
	            setText(null);
	            setGraphic(null);
	            
	        } else {
	            setGraphic(checkBox);
	            if (ov instanceof BooleanProperty) {
	                checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
	            }
	            
	            ov = getTableColumn().getCellObservableValue(getIndex());
	            if (ov instanceof BooleanProperty) {
	                checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
	            }
	            
	            checkBox.setSelected((Boolean)item );
	        }        
	    }

		@Override
		public void handle(ActionEvent arg0) {
			if( actionEvent != null ) {
				actionEvent.handle(chClickEvent);
			}
		}
	}


	
}
