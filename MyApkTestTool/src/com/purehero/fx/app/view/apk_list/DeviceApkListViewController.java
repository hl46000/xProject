package com.purehero.fx.app.view.apk_list;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.android.ddmlib.IShellOutputReceiver;
import com.purehero.android.DeviceInfo;
import com.purehero.common.io.HTTPS;
import com.purehero.common.io.IRelease;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.app.view.MainViewController;
import com.purehero.fx.app.view.WorkThread;
import com.purehero.fx.app.view.test.ApkFileInfo;
import com.purehero.fx.common.DialogUtils;
import com.purehero.fx.common.TableViewUtils;
import com.purehero.fx.control.ex.CheckBoxTableCellEx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import net.dongliu.apk.parser.ApkParser;

public class DeviceApkListViewController implements EventHandler<ActionEvent>, IRelease {
	
	@FXML
	private TableView<ApkFileInfo> tbApkInfoList;
	
	private ObservableList<ApkFileInfo> apkFileInfos = FXCollections.observableList( new ArrayList<ApkFileInfo>());
	private PropertyEx appNameProp = null;
	
	
	@FXML
    public void initialize() {
		initTableView();
		
		appNameProp = loadAppNameProperty();
	}

	private PropertyEx loadAppNameProperty() {
		PropertyEx prop = MainClass.instance.getProperty();
		
		final File prop_file = new File( prop.getPropertyFilePath());
		File folder = prop_file.getParentFile();
		File appNamePropFile = new File( folder, "AppName.prop" );
		if( !appNamePropFile.exists())
			try {
				appNamePropFile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		
		PropertyEx appNameProp = new PropertyEx( "AppName property" );
		try {
			appNameProp.load( appNamePropFile );
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return appNameProp;
		
	}

	private void initTableView() {
		int column_index = 0;
		
		TableViewUtils.CheckBoxTableColumn	( tbApkInfoList, "selected", 	"center-left", column_index++, DeviceApkListViewController.this );		// check box
		TableViewUtils.StringTableColumn	( tbApkInfoList, "appName", 	"center-left", column_index++ );
		TableViewUtils.StringTableColumn	( tbApkInfoList, "packageName", "center-left", column_index++ );
		TableViewUtils.StringTableColumn	( tbApkInfoList, "apkFilePath", "center-left", column_index++ );
		
		tbApkInfoList.setItems( apkFileInfos );	
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(ActionEvent arg0) {
		Object obj = arg0.getSource();
		
		if( obj instanceof CheckBoxTableCellEx ) {
			handleCheckBoxTableCellEx( (com.purehero.fx.control.ex.CheckBoxTableCellEx<?, ?> ) obj );
			
		} else if( obj instanceof Button ) {
			OnButtonClicked( ( Button ) obj );
			
		} else if( obj instanceof CheckBox ) {
			OnCheckBoxClicked( ( CheckBox ) obj );
			
		} else if( obj instanceof MenuItem ) {
			OnMenuItemClicked( ( MenuItem ) obj );
		}
	}
	
	private void OnMenuItemClicked(MenuItem obj) {
		switch( obj.getId()) {
		case "ID_MENU_GET_APP_NAME" 	: OnMenuItemGetAppName(); 		break;
		case "ID_MENU_EXTRACTION_APK"	: OnMenuItemExtractionAPK();	break;
		case "ID_MENU_DELETE_APK"		: OnMenuItemDeleteAPK();		break;
		case "ID_MENU_UPDATE_APK"		: onMenuItemUpdateAPK();		break;
		}
	}

	private void onMenuItemUpdateAPK() {
		DeviceInfo deviceInfo = mainViewController.getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		ApkFileInfo apkFileInfo = tbApkInfoList.getSelectionModel().getSelectedItem();
		if( apkFileInfo == null ) {
			DialogUtils.alert( "INFORMATION", "APK 파일이 선택되지 않았습니다. \nAPK 파일을 선택 후 다시 시도해 주세요.", AlertType.INFORMATION );
			return;
		}
		
		File result = DialogUtils.openFileDialog( "갱신할 APK 파일을 선택하세요.", "APKs", "*.apk");
		if( result == null ) return;
		
		MainClass.instance.runThreadPool( new UpdateApkOnDevice( deviceInfo, apkFileInfo, result ));
	}

	class UpdateApkOnDevice implements Runnable {
		final DeviceInfo deviceInfo;
		final ApkFileInfo apkFileInfo;
		final File apkfile;
		public UpdateApkOnDevice( DeviceInfo deviceInfo, ApkFileInfo apkFileInfo, File apkfile ) {
			this.deviceInfo 	= deviceInfo;
			this.apkFileInfo 	= apkFileInfo;
			this.apkfile		= apkfile;
		}
		@Override
		public void run() {
			try {
				mainViewController.updateDeviceCommant( deviceInfo, String.format( "'%s' APK Updating", apkFileInfo.getAppName()), true );
				deviceInfo.getInterface().pushFile( apkfile.getAbsolutePath(), apkFileInfo.getApkFilePath() );
				mainViewController.updateDeviceCommant( deviceInfo, String.format( "'%s' APK Updated", apkFileInfo.getAppName()), true );
				
				Platform.runLater( new Runnable(){
					@Override
					public void run() {
						DialogUtils.alert( "APK Update 완료", 
								String.format( "'%s' 의 APK 파일이 업데이트 되었습니다. ", apkFileInfo.getAppName()),
								AlertType.INFORMATION );
					}} );
			} catch (Exception e) {
				e.printStackTrace();
				mainViewController.updateDeviceCommant( deviceInfo, e.getMessage(), true );
			} 
		}
	}
	
	private void OnMenuItemDeleteAPK() {
		DeviceInfo deviceInfo = mainViewController.getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		ApkFileInfo apkFileInfo = tbApkInfoList.getSelectionModel().getSelectedItem();
		if( apkFileInfo == null ) {
			DialogUtils.alert( "INFORMATION", "APK 파일이 선택되지 않았습니다. \nAPK 파일을 선택 후 다시 시도해 주세요.", AlertType.INFORMATION );
			return;
		}
		
		if( ButtonType.OK == DialogUtils.alert( "CONFIRMATION", String.format("'%s' 을 삭제합니다.", apkFileInfo.getAppName()), AlertType.CONFIRMATION )) {
			try {
				mainViewController.apkFileUnistall( deviceInfo, apkFileInfo.getPackageName(), apkFileInfo.getAppName(), 1000 );
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void OnMenuItemExtractionAPK() {
		DeviceInfo deviceInfo = mainViewController.getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		ApkFileInfo apkFileInfo = tbApkInfoList.getSelectionModel().getSelectedItem();
		if( apkFileInfo == null ) {
			DialogUtils.alert( "INFORMATION", "APK 파일이 선택되지 않았습니다. \nAPK 파일을 선택 후 다시 시도해 주세요.", AlertType.INFORMATION );
			return;
		}
		
		File result = DialogUtils.saveFileDialog( "저장할 APK 파일을 선택하세요.", "APKs", "*.apk");
		if( result == null ) return;
		
		MainClass.instance.runThreadPool( new ExtractionApkFromDevice( deviceInfo, apkFileInfo, result ));
	}

	class ExtractionApkFromDevice implements Runnable {
		final DeviceInfo deviceInfo;
		final ApkFileInfo apkFileInfo;
		final File outfile;
		public ExtractionApkFromDevice( DeviceInfo deviceInfo, ApkFileInfo apkFileInfo, File outfile ) {
			this.deviceInfo 	= deviceInfo;
			this.apkFileInfo 	= apkFileInfo;
			this.outfile		= outfile;
		}
		@Override
		public void run() {
			try {
				mainViewController.updateDeviceCommant( deviceInfo, String.format( "'%s' APK Extracting", apkFileInfo.getAppName()), true );
				deviceInfo.getInterface().pullFile( apkFileInfo.getApkFilePath(), outfile.getAbsolutePath() );
				mainViewController.updateDeviceCommant( deviceInfo, String.format( "'%s' APK Extracted", apkFileInfo.getAppName()), true );
				
				Platform.runLater( new Runnable(){
					@Override
					public void run() {
						DialogUtils.alert( "APK 추출 완료", 
								String.format( "'%s' 의 APK 파일이 '%s' 로 저장이 되었습니다. ", apkFileInfo.getAppName(), outfile.getAbsolutePath()),
								AlertType.INFORMATION );
					}} );
			} catch (Exception e) {
				e.printStackTrace();
				mainViewController.updateDeviceCommant( deviceInfo, e.getMessage(), true );
			} 
		}
	}
	
	private void OnMenuItemGetAppName() {
		DeviceInfo deviceInfo = mainViewController.getSelectedDeviceInfo();
		if( deviceInfo == null ) return;
		
		ApkFileInfo apkFileInfo = tbApkInfoList.getSelectionModel().getSelectedItem();
		if( apkFileInfo == null ) {
			DialogUtils.alert( "INFORMATION", "APK 파일이 선택되지 않았습니다. \nAPK 파일을 선택 후 다시 시도해 주세요.", AlertType.INFORMATION );
			return;
		}
		
		MainClass.instance.runThreadPool( new GetAppNameFromApkInDevice( deviceInfo, apkFileInfo ));
	}

	class GetAppNameFromApkInDevice implements Runnable {
		final DeviceInfo deviceInfo;
		final ApkFileInfo apkFileInfo;
		public GetAppNameFromApkInDevice( DeviceInfo deviceInfo, ApkFileInfo apkFileInfo ) {
			this.deviceInfo 	= deviceInfo;
			this.apkFileInfo 	= apkFileInfo;
		}
		@Override
		public void run() {
			File tempFolder = MainClass.instance.GetTempPath();
			File tempFile = null;
			try {
				tempFile = File.createTempFile( "tmp", ".tmp", tempFolder );
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			
			try {
				deviceInfo.getInterface().pullFile( apkFileInfo.getApkFilePath(), tempFile.getAbsolutePath() );
				ApkParser apkParser = new ApkParser( tempFile );
				String appName = apkParser.getApkMeta().getLabel();
				apkParser.close();
				
				apkFileInfo.setAppName( appName );
				
				appNameProp.setValue( apkFileInfo.getPackageName(), appName );
				appNameProp.save();
				
				Platform.runLater( ApkInfoListUpdateRunnable );
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				if( tempFile != null ) {
					tempFile.delete();
				}
			}
		}
	}
	
	Runnable ApkInfoListUpdateRunnable = new Runnable() {
		@Override
		public void run() {
			tbApkInfoList.refresh();
		}
		
	};
	
	private void OnCheckBoxClicked(CheckBox obj) {
		switch( obj.getId()) {
		case "ID_CK_ALL_CHECK" : OnCheckBoxAllCheck( obj.isSelected() ); break;
		}
	}

	private void OnCheckBoxAllCheck(boolean bChecked ) {
		ObservableList<ApkFileInfo> items = tbApkInfoList.getItems();
		for( ApkFileInfo item : items ) {
			item.setSelected( bChecked );
		}
		tbApkInfoList.refresh();
	}

	private void handleCheckBoxTableCellEx(CheckBoxTableCellEx<?, ?> ckCell) {
		ApkFileInfo apkInfo = tbApkInfoList.getItems().get( ckCell.getIndex() );
		apkInfo.setSelected( !apkInfo.getSelected());
		tbApkInfoList.refresh();
	}

	private void OnButtonClicked( Button button ) {
		switch( button.getId()) {
		case "ID_BTN_LIST_UPDATE" 			: OnButtonListUpdate(); break;
		case "ID_BUTTON_DELETE_CHECKED_APK" : OnButtonDeleteCheckedAPK(); break;
		}
	}

	Runnable DeleteCheckedApkRunnable = new Runnable() {
		@Override
		public void run() {
			DeviceInfo deviceInfo = mainViewController.getSelectedDeviceInfo();
			if( deviceInfo == null ) return;
			
			List<Integer> delItems = new ArrayList<Integer>();
			
			for( int i = 0; i < apkFileInfos.size(); i++ ) {
				ApkFileInfo apkFileInfo = apkFileInfos.get(i);
				if( !apkFileInfo.getSelected()) continue;
				
				try {
					mainViewController.apkFileUnistall( deviceInfo, apkFileInfo.getPackageName(), apkFileInfo.getAppName(), 1000 );				
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				delItems.add(i);
			}
		
			Collections.reverse(delItems);
			for( Integer index : delItems ) {
				apkFileInfos.remove(index.intValue());
			}
			
			Platform.runLater( ApkInfoListUpdateRunnable );
		}
	};
	
	private void OnButtonDeleteCheckedAPK() {
		MainClass.instance.runThreadPool( DeleteCheckedApkRunnable );
	}

	private void OnButtonListUpdate() {
		DeviceInfo device_info = mainViewController.getSelectedDeviceInfo();
		if( device_info == null ) return;
		
		apkFileInfos.clear();
		MainClass.instance.runThreadPool( new WorkThread( device_info, "pm list package -f -3", DeviceApkList_ShellOutputReceiver ));
	}

	private IShellOutputReceiver DeviceApkList_ShellOutputReceiver = new IShellOutputReceiver() {
		ByteArrayOutputStream baos = null;
				
		@Override
		public void addOutput(byte[] data, int offset, int length) {
			if( baos == null ) baos = new ByteArrayOutputStream();
			baos.write( data, offset, length );
		}

		@Override
		public void flush() { 
			String contents = baos.toString();
			String lines [] = contents.split( "\n" );
			
			for( String line : lines ) {
				if( mainViewController.isReleased()) break;
				try {
					parsingAndAddToApkList( line );
				} catch( Exception e ) {}
			}

			try {
				baos.flush();
				baos.close();
				baos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	MainViewController mainViewController = null;
	public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}

	protected void parsingAndAddToApkList(String line) {
		line = line.substring( "package:".length());		
		String token[] = line.split("=");
		
		ApkFileInfo apkFileinfo = new ApkFileInfo( null );
		apkFileinfo.setApkFilePath( token[0].trim() );
		apkFileinfo.setPackageName( token[1].trim() );
						
		setAppNameWithPackageName( apkFileinfo );
		
		apkFileInfos.add( apkFileinfo );
	}

	private void setAppNameWithPackageName(ApkFileInfo apkFileInfo) {
		final String packageName = apkFileInfo.getPackageName();
		
		String appName = appNameProp.getValue( packageName );
		if( appName == null ) {
			appName = HTTPS.getAppNameFromGooglePlay( packageName );
			
			if( appName != null ) {
				appNameProp.setValue( packageName, appName );
				appNameProp.save();
			}
		}
		
		apkFileInfo.setAppName( appName == null ? "unknown" : appName );
	}
}
