package com.purehero.fx.app.view.work;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.IRelease;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.common.DialogUtils;

public class DeviceTestViewController implements EventHandler<ActionEvent>, IRelease{
	
	@FXML
	private TextField tfDeviceTestApkPath;
	
	@FXML
	private TextField tfDeviceTestOutputPath;
	
	FileAlterationObserver observer = null;
    FileAlterationMonitor monitor = null;
	
	@FXML
    public void initialize() throws Exception {
		TextField textFields[] = { tfDeviceTestApkPath, tfDeviceTestOutputPath };
		PropertyEx prop = MainClass.instance.getProperty();
		for( TextField item : textFields ) {
			String path = prop.getValue( item.getId());
			if( path != null ) {
				item.setText( path );				
			}
		}
		
		MainClass.instance.addReleaseInterface( this );
	}
	
	@Override
	public void Release() {
		stopService();
	}
	
	@FXML
	@Override
	public void handle(ActionEvent event ) {
		Object obj = event.getSource();
		if( obj instanceof Button ) {
			OnClickHandler( obj );
		}
	}

	private void OnClickHandler(Object obj) {
		Control ctrl = ( Control ) obj;
		switch( ctrl.getId()) {
		case "ID_BUTTON_SELECT_DEVICE_TEST_APK_PATH" 	: OnClickSelectDeviceTestPath( "APK 모니터링 경로", tfDeviceTestApkPath ); startService(); break;
		case "ID_BUTTON_SELECT_DEVICE_TEST_OUTPUT_PATH" : OnClickSelectDeviceTestPath( "테스트 결과파일 경로", tfDeviceTestOutputPath ); break;
		}
		
	}

	private void OnClickSelectDeviceTestPath( String title, TextField textField ) {
		File result = DialogUtils.openDirectoryDialog( title );
		if( result == null ) return;
		
		ButtonType confirmation = DialogUtils.alert( "경로 변경 확인", String.format( "'%s' 를\n\n'%s' 로 변경 하시겠습니까?\n\n", title, result.getAbsolutePath()), AlertType.CONFIRMATION );
		if( confirmation != ButtonType.OK && confirmation != ButtonType.YES ) return;

		PropertyEx prop = MainClass.instance.getProperty();
		prop.setValue( textField.getId(), result.getAbsolutePath());
		prop.save();
		
		textField.setText( result.getAbsolutePath() );
		
		DialogUtils.alert( "경로 변경", String.format( "'%s' 가 \n\n'%s' 로 변경 되었습니다.\n\n", title, result.getAbsolutePath()), AlertType.INFORMATION );
	}

	private void stopService() {
		if( monitor != null && observer != null ) {
			monitor.removeObserver( observer );
		}
		
		try {
			if( monitor != null ) {
				monitor.stop();
			}
		} catch( Exception e ) { e.printStackTrace(); 
		} finally {
			monitor = null;
		}
		
		try {
			if( observer != null ) {
				observer.destroy();
			}
		} catch( Exception e ) { e.printStackTrace(); 
		} finally {
			observer = null;
		}
	}
	
	public void startService() {
		File apkFolder = new File( tfDeviceTestApkPath.getText());
		if( !apkFolder.exists()) {
			DialogUtils.alert( "ERROR", "APK PATH 설정이 유효하지 않습니다. \n\n경로를 확인 후 다시 시도해 주시기 바랍니다. \n\n", AlertType.ERROR );
			return;
		}
		stopService();
		
		// The monitor will perform polling on the folder every 5 seconds
        final long pollingInterval = 5 * 1000;
		observer = new FileAlterationObserver(apkFolder);
		observer.addListener( fileMonitorListener );
		
		monitor = new FileAlterationMonitor(pollingInterval);
        monitor.addObserver( observer );
        try { monitor.start(); } catch (Exception e) { e.printStackTrace(); }
	}
	
	FileAlterationListener fileMonitorListener = new FileAlterationListenerAdaptor() {
		@Override
		public void onFileCreate(File file) {
			System.out.println( file.getAbsolutePath());
		}
    };
}
