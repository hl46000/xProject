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
import javafx.scene.control.Label;
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
	
	@FXML
	private Label statusMessage;		// 상태 정보를 출력해주는 Label
	
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
			tvDeviceInfo.refresh();
			
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
			case "ID_MENU_MULTI_APK_FILE"		: OnButtonClickMultiSelectApkFile(); break;
			}
		}
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
		
		final File apkFile = DialogUtils.openDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			final ApkParser apkParser = new ApkParser( apkFile );
			new Thread( new Runnable(){
				@Override
				public void run() {
					File tmpFile = apkFile;
					
					// APK 파일 서명은 한번만 하면 되기 때문에 따로 처리 한다. 
					if( isCheckMenu( MultiFileOption, "APK_SIGN" )) {
						updateStatusMessage( "APK Signning : " + apkFile.getName() );
							
						File signedApkFile = new File( apkFile.getParentFile(), apkFile.getName().replace( ".apk", "_signed.apk"));
						SignApk sign = new SignApk();
						sign.sign( apkFile, signedApkFile );
						
						updateStatusMessage( "APK Signed : " + signedApkFile.getName());
						tmpFile = signedApkFile;
					}
					
					for( DeviceInfo deviceInfo : deviceInfos ) {
						// 단말기별로 옵션메뉴에 설정한 명령 대로 실행해 줍니다. 
						new ApkFileActionDevice( deviceInfo, tmpFile, apkParser, MultiFileOption ).start();
					}
				}}).start();	
			
		}  catch (Exception e1) {
			e1.printStackTrace();			
		}		
		
	}

	Runnable ListUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			tvDeviceInfo.refresh();			
		}
	};
	
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
	
	class ApkFileActionDevice extends Thread implements Runnable {
		final DeviceInfo deviceInfo;
		final File apkFile;
		final ApkParser apkParser;
		final Menu optionMenu;
		
		public ApkFileActionDevice( DeviceInfo deviceInfo, File apkFile, ApkParser apkParser, Menu optionMenu ) {
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
			
			if( isCheckMenu( optionMenu, "APK_UNINSTALL" )) {
				updateDeviceCommant( deviceInfo, "APK Uninstalling", true );
				try {
					deviceInfo.getInterface().uninstallPackage( packageName );
					updateDeviceCommant( deviceInfo, "APK Uninstalled", true );
				} catch (Exception e) {
					e.printStackTrace();
					updateDeviceCommant( deviceInfo, "Failed: APK Uninstall", true );
					return;
				}					
			}
			if( isCheckMenu( optionMenu, "APK_INSTALL" )) {
				updateDeviceCommant( deviceInfo, "APK Installing", true );
				try {
					deviceInfo.getInterface().installPackage( apkFile.getAbsolutePath(), false );
					updateDeviceCommant( deviceInfo, "APK Installed", true );
				} catch (Exception e) {
					e.printStackTrace();
					updateDeviceCommant( deviceInfo, "Failed: APK Installed", true );
					return;
				}
				
			}
			if( isCheckMenu( optionMenu, "APK_RUNNING" )) {
				updateDeviceCommant( deviceInfo, "APK Running", true );
				try {
					deviceInfo.getInterface().executeShellCommand( String.format( "am start -n '%s/%s'", packageName, launcherActivityName), shellOutputReceiver );						
				} catch (Exception e) {
					e.printStackTrace();
					updateDeviceCommant( deviceInfo, "Failed: APK Running", true );
					return;
				}
			}
		}
	};
	
	/**
	 * APK 선택 Dialog 을 띄워 APK 파일을 선택하고, 실행 옵션에 따라서 실행합니다. 
	 * @throws IOException 
	 */
	private void OnButtonClickSelectApkFile() {
		DeviceInfo deviceInfo = getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		final File apkFile = DialogUtils.openDialog( "APK File 선택", "APK File","*.apk");
		if( apkFile == null ) return;
		
		try {
			final ApkParser apkParser = new ApkParser( apkFile );
			new Thread( new Runnable(){
				@Override
				public void run() {
					File tmpFile = apkFile;
					// APK 파일 서명은 한번만 하면 되기 때문에 따로 처리 한다. 
					if( isCheckMenu( SingleFileOption, "APK_SIGN" )) {
						updateStatusMessage( "APK Signning : " + apkFile.getName() );
						
						File signedApkFile = new File( apkFile.getParentFile(), apkFile.getName().replace( ".apk", "_signed.apk"));
						SignApk sign = new SignApk();
						sign.sign( apkFile, signedApkFile );
						
						updateStatusMessage( "APK Signed : " + signedApkFile.getName());
						tmpFile = signedApkFile;
					}
					
					// 단말기별로 옵션메뉴에 설정한 명령 대로 실행해 줍니다. 
					new ApkFileActionDevice( deviceInfo, tmpFile, apkParser, SingleFileOption ).start();					
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
	 * menu의 하위 메뉴 중 id 값에 을로 끝나는 ID을 가지는 메뉴가 check 되어 있는지를 확인 합니다. 
	 * @param Menu
	 * @param string
	 * @return
	 */
	protected boolean isCheckMenu( Menu menu, String id ) {
		ObservableList<MenuItem> items = menu.getItems();
		for( MenuItem item : items ) {
			if( !item.getId().endsWith( id )) continue;
			
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
