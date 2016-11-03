package com.purehero.fx.app.view.work;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.purehero.common.io.IRelease;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.app.view.MainViewController;
import com.purehero.fx.common.DialogUtils;
import com.purehero.fx.common.TableViewUtils;
import com.purehero.fx.control.ex.TitledPaneEx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;

public class DeviceTestViewController implements EventHandler<ActionEvent>, IRelease{
	
	@FXML
	private TextField tfDeviceTestApkPath;
	
	@FXML
	private TextField tfDeviceTestOutputPath;

	@FXML
	private Accordion testContainer;
	
	@FXML
	private TableView<ApkFileInfo> tbApkFileList;
	
	private ObservableList<ApkFileInfo> apkFileInfos = FXCollections.observableList( new ArrayList<ApkFileInfo>()); 
	
	FileAlterationObserver observer = null;
    FileAlterationMonitor monitor = null;
	
	/**
	 * DeviceTestViewController 을 초기화 하는 함수 
	 * 
	 * @throws Exception
	 */
	@FXML
    public void initialize() throws Exception {
		initTableView();
		
		TextField textFields[] = { tfDeviceTestApkPath, tfDeviceTestOutputPath };
		PropertyEx prop = MainClass.instance.getProperty();
		for( TextField item : textFields ) {
			String path = prop.getValue( item.getId());
			if( path != null ) {
				item.setText( path );				
			}
		}
		
		// 앱이 시작하기 전에 이미 존재하던 파일들을 추가 시킨다. 
		File ApkFolder = new File( tfDeviceTestApkPath.getText());
		if( ApkFolder.exists() ) {
			File apkFiles [] = ApkFolder.listFiles();
			for( File file : apkFiles ) {
				if( !file.isFile()) continue;
				if( !file.getName().toLowerCase().endsWith(".apk")) continue;
				apkFileInfos.add( new ApkFileInfo( file ));
			}
		}
		
		MainClass.instance.addReleaseInterface( this );
	}
	
	private void initTableView() {
		TableViewUtils.StringTableColumn( tbApkFileList, "name", 	"CENTER", 0 );
		TableViewUtils.StringTableColumn( tbApkFileList, "status", 	"CENTER", 1 );
				
		tbApkFileList.setItems( apkFileInfos );	
	}

	/* (non-Javadoc)
	 * @see com.purehero.fx.app.IRelease#Release()
	 */
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

	/**
	 * ActionEvent 에서 click event 을 처리하는 함수 입니다. 
	 * 
	 * @param obj
	 */
	private void OnClickHandler(Object obj) {
		Control ctrl = ( Control ) obj;
		switch( ctrl.getId()) {
		case "ID_BUTTON_SELECT_DEVICE_TEST_APK_PATH" 	: OnClickSelectDeviceTestPath( "APK 모니터링 경로", tfDeviceTestApkPath ); startService(); break;
		case "ID_BUTTON_SELECT_DEVICE_TEST_OUTPUT_PATH" : OnClickSelectDeviceTestPath( "테스트 결과파일 경로", tfDeviceTestOutputPath ); break;
		case "ID_BUTTON_ADD_TEST_VIEW"					: OnClickAddTestView(); break;
		}
		
	}

	/**
	 * testContainer 에 testView 을 추가 합니다. 
	 */
	private void OnClickAddTestView() {
		FXMLLoader testViewLoader = new FXMLLoader( getClass().getResource("RepeatTestView.fxml")); 
		try {
			Parent testView = testViewLoader.load();
			
			TitledPaneEx tp = new TitledPaneEx();
			tp.setText( String.format( "%d 번째 테스트", testContainer.getPanes().size() + 1 ));
			tp.setContent( testView );
			testContainer.getPanes().add( tp );
			
			RepeatTestViewController testViewController = ( RepeatTestViewController ) testViewLoader.getController();
			testViewController.setParentTitledPane( tp );
			testViewController.setDeviceTestViewController( this );
			
			tp.setController( testViewController );
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	/**
	 * @param tp
	 */
	public void removeTestView( TitledPane tp ) {
		testContainer.getPanes().remove( tp );
	}

	/**
	 * Device Test 관련 경로를 변경할 때 호출되는 함수 입니다. 
	 * 
	 * @param title
	 * @param textField
	 */
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

	/**
	 * 서비스를 중단 합니다. 
	 */
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
	
	/**
	 * APK Path 에 APK 파일이 생성되면 테스트를 진행하는 Service 을 시작 합니다. 
	 */
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
	
	MainViewController mainViewController = null;
	public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}
	
	/**
	 * APK 파일 목록 리스트를 갱신 시킨다.
	 */
	private Runnable ApkFileListUpdate = new Runnable () {
		@Override
		public void run() { tbApkFileList.refresh(); }
	};
	
	/**
	 * APK Path에 APK 파일의 생성을 감지하는 Listener 입니다. 
	 */
	FileAlterationListener fileMonitorListener = new FileAlterationListenerAdaptor() {
		@Override
		public void onFileDelete(File file) {
			for( ApkFileInfo apkfileInfo : apkFileInfos ) {
				if( apkfileInfo.getName().compareTo( file.getName() ) == 0 ) {
					apkFileInfos.remove( apkfileInfo );
					break;
				}
			}
			Platform.runLater( ApkFileListUpdate );			
		}

		@Override
		public void onFileCreate(File file) {
			apkFileInfos.add( new ApkFileInfo(file));
			Platform.runLater( ApkFileListUpdate );
			/*
			System.out.println( "fileMonitorListener::onFileCreate = " + file.getAbsolutePath());
			
			ApkParser apkParser = null;
			try {
				apkParser = new ApkParser( file );
			} catch (IOException e1) {
				e1.printStackTrace();
				return;
			}
			
			File output_folder = new File( tfDeviceTestOutputPath.getText());
			if( !output_folder.exists()) output_folder.mkdirs();
			
			ObservableList<TitledPane> panes = testContainer.getPanes();
			for( TitledPane pane : panes ) {
				if( mainViewController.isReleased()) return;
				
				TitledPaneEx paneEx = ( TitledPaneEx ) pane;
				testContainer.setExpandedPane( paneEx );
				
				RepeatTestViewController testViewController = ( RepeatTestViewController ) paneEx.getController();
				testViewController.clear();
								
				try {
					testViewController.runTesting( mainViewController, apkParser, file, output_folder );
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			testContainer.setExpandedPane( null );
			*/
		}
    };
}
