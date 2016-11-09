package com.purehero.fx.app.view.work;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import net.dongliu.apk.parser.ApkParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import com.purehero.android.SignApk;
import com.purehero.common.io.IRelease;
import com.purehero.common.io.PropertyEx;
import com.purehero.common.io.SMTP;
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
		case "ID_BUTTON_SAVE_TEST_SETTING"				: OnClickSaveTestSetting(); break;
		case "ID_BUTTON_LOAD_TEST_SETTING"				: OnClickLoadTestSetting(); break;
		}
		
	}

	/**
	 * 
	 */
	private void OnClickLoadTestSetting() {
		File result = DialogUtils.openFileDialog( "테스트 설정 로드", "SETTING","*.setting");
		if( result == null ) return;
		
		PropertyEx prop = new PropertyEx("");
		try {
			prop.load( result.getAbsolutePath());
			
			testContainer.getPanes().clear();
			
			int count = Integer.valueOf( prop.getValue("COUNT"));
			for( int i = 0; i < count; i++ ) {
				String jsonString = prop.getValue( String.format("SETTING_DATA%03d", i));
				
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
					testViewController.setTestDatas( jsonString );
					
					tp.setController( testViewController );
				} catch (IOException e) {			
					e.printStackTrace();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private void OnClickSaveTestSetting() {
		File result = DialogUtils.saveFileDialog( "테스트 설정 저장", "SETTING","*.setting");
		if( result == null ) return;
		
		PropertyEx prop = new PropertyEx("테스트 설정 파일"); 
				
		int index = 0;
		ObservableList<TitledPane> panes = testContainer.getPanes();
		prop.setValue( "COUNT", String.format( "%d", panes.size()));
		
		for( TitledPane pane : panes ) {
			TitledPaneEx paneEx = ( TitledPaneEx ) pane;
			testContainer.setExpandedPane( paneEx );
			
			RepeatTestViewController testViewController = ( RepeatTestViewController ) paneEx.getController();
			String key = String.format( "SETTING_DATA%03d", index++ );
			String val = testViewController.getTestDatas();
			prop.setValue(key, val );
		}
		
		prop.save( result.getAbsolutePath());
		DialogUtils.alert( "확인", "테스트 설정파일이 저장되었습니다", AlertType.INFORMATION );
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
        //final long pollingInterval = 5 * 1000;
		observer = new FileAlterationObserver(apkFolder);
		observer.addListener( fileMonitorListener );
		
		//monitor = new FileAlterationMonitor(pollingInterval);
		monitor = new FileAlterationMonitor();
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
	
	Thread deviceTestThread = null;
	
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
			synchronized (apkFileInfos) {
				apkFileInfos.add( new ApkFileInfo(file));
				
				Platform.runLater( ApkFileListUpdate );
				if( deviceTestThread == null ) {
					deviceTestThread = new Thread( deviceTestRunnable );
					deviceTestThread.start();
				}
			}
		}
    };
    
    /**
     * @param message
     */
    private void updateStatusMessage( String message ) {
    	if( mainViewController != null ) {
			mainViewController.updateStatusMessage( message );
		}
    }
    
    /**
     * @param apkFile
     * @param apkStatus
     * @param statusMsg
     */
    private void updateApkFileListStatus( ApkFileInfo apkFile, String apkStatus, String statusMsg ) {
    	apkFile.setStatus(apkStatus);
		Platform.runLater( ApkFileListUpdate );
		if( statusMsg != null ) {
			updateStatusMessage( statusMsg );
		}
    }
    Runnable deviceTestRunnable = new Runnable() {
		@Override
		public void run() {
			boolean testLoop = false;
			synchronized (apkFileInfos) { 
				testLoop = !apkFileInfos.isEmpty();
			}
			
			List<String> recipientList = new ArrayList<String>(); 
			List<String> testResult = new ArrayList<String>(); 
			
			recipientList.add("purehero@inka.co.kr");
			while( testLoop && !mainViewController.isReleased()) {
				ApkFileInfo apkFile = null;
				synchronized (apkFileInfos) { 
					apkFile = apkFileInfos.get(0);
				}
				
				updateStatusMessage( String.format( "'%s' 파일 테스트 시작", apkFile.getName() ));
				try { Thread.sleep( 1000 ); } catch (InterruptedException e2) {}

				updateApkFileListStatus( apkFile, "분석", String.format( "'%s' 파일 분석 중", apkFile.getName() ));
								
				// Test 할 APK 파일에 서명을 합니다. ( 기본으로 서명 작업을 수행합니다. )
				ApkParser apkParser = null;
				try {
					apkParser = new ApkParser( apkFile.getApkFile() );
				} catch (IOException e1) {
					e1.printStackTrace();
					updateStatusMessage( String.format( "'%s' 파일 분석 실패로 테스트 중지", apkFile.getName() ));
					break;
				}
								
				File output_folder = new File( tfDeviceTestOutputPath.getText());
				File imsiFoler = new File( output_folder, "temp" );
				if( !imsiFoler.exists()) imsiFoler.mkdirs();
						
				// APK 파일 서명
				updateApkFileListStatus( apkFile, "서명", String.format( "'%s' 파일 서명 중", apkFile.getName() ));
				
				SignApk signApk = new SignApk();
				File signedApkFile = new File( imsiFoler, apkFile.getName().replace(".apk", "_signed.apk"));
				signApk.sign( apkFile.getApkFile(), signedApkFile );
				
				// TEST 진행
				updateApkFileListStatus( apkFile, "진행", String.format( "'%s' 파일 테스트 진행", apkFile.getName() ));
				
				ObservableList<TitledPane> panes = testContainer.getPanes();
				for( TitledPane pane : panes ) {
					if( mainViewController.isReleased()) return;
					
					TitledPaneEx paneEx = ( TitledPaneEx ) pane;
					testContainer.setExpandedPane( paneEx );
										
					RepeatTestViewController testViewController = ( RepeatTestViewController ) paneEx.getController();
					testViewController.clear();
									
					try {
						testResult.add( "=====================================================================" );
						testResult.add( paneEx.getText());
						testResult.add( "=====================================================================" );
						List<String> result = testViewController.runTesting( mainViewController, apkParser, signedApkFile, output_folder );
						testResult.addAll( result );
						testResult.add( "=====================================================================" );
					} catch (Exception e) {
						e.printStackTrace();
					}				
				}
				testContainer.setExpandedPane( null );
				
				try {
					apkParser.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// TEST 완료
				updateApkFileListStatus( apkFile, "완료", String.format( "'%s' 파일 테스트 완료", apkFile.getName() ));
				
				// TEST 파일 정리
				synchronized (apkFileInfos) { 
					apkFileInfos.remove(0);
					Platform.runLater( ApkFileListUpdate );
					
					// 다음 파일이 존재하면 다음 파일 Test을 진행합니다. 
					testLoop = !apkFileInfos.isEmpty();
				}
				try { FileUtils.forceDelete( apkFile.getApkFile()); } catch (IOException e) {}
				try { FileUtils.forceDelete( signedApkFile ); } catch (IOException e) {}
				
				// 리포트 파일이 있으면 메일로 발송한다. 
				try {
					SMTP.sendInkaNoreplyMail( "DEVICE TEST RESULT", recipientList, testResult, null );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			synchronized (apkFileInfos) {
				deviceTestThread = null;
			}
		}
    };
}
