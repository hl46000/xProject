package com.purehero.fx.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import com.purehero.android.ADB;
import com.purehero.common.io.FileUtils;
import com.purehero.common.io.IRelease;
import com.purehero.common.io.PathUtils;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.view.MainViewController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MainClass extends javafx.application.Application {
	public static MainClass instance = null;
	public static void main(String[] args) { 
		instance = new MainClass();
		launch(args);
	}
	
	private Stage primaryStage = null;
	
	private ADB adb = new ADB();
	//private ExecutorService threadPool = Executors.newFixedThreadPool(5);;
	//private ThreadPoolExecutor threadPool = new ThreadPoolExecutor( 10, 10, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>()  );
	private ExecutorService threadPool = null;
		
	public MainClass() {
		ClassLoader clsLoader = getClass().getClassLoader();
		
		File adbPath = FileUtils.extractFileFromJar( clsLoader, "adb/adb.exe", GetTempPath());
		adb.Initialize( adbPath );
	}

	/**
	 * 임시 사용할 폴더 경로를 반환한다. 
	 * 
	 * @return
	 */
	public File GetTempPath() {
		return new File( "c:\\temp\\atm_v3" );
	}
	
	/**
	 * 현재 실행 파일의 위치(경로)를 반환한다.  
	 * 
	 * @return
	 */
	public String getCurrentPath() {
		return PathUtils.GetCurrentPath( this );
	}

	/* 
	 * 화면 UI을 시작할때 처음 호출되는 함수
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		FXMLLoader mainViewLoader = new FXMLLoader(MainClass.this.getClass().getResource("view/MainView.fxml"));
		Parent mainView = mainViewLoader.load();
		
		MainViewController ctrl = (MainViewController) mainViewLoader.getController();
		ctrl.setADB(adb);
		
		Scene scene = new Scene( mainView, -1, -1, Color.WHITE);		
		primaryStage.setScene( scene );
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	
	private static List<IRelease> i_releases = new ArrayList<IRelease>();	// i_releases 객체가 static 이 아니면 stop 함수에서 정상적으로 처리가 되지 않는다.  
	/**
	 * Application 이 종료될때 리소스 해제등이 필요한 경우을 위해 Release 함수를 호출해 주는 interface 을 등록 한다.  
	 * 
	 * @param release_interface
	 */
	public void addReleaseInterface( IRelease release_interface ) {
		i_releases.add( release_interface );
	}
	
	/** 
	 * Application 이 종료될때 호출되는 함수
	 * @see javafx.application.Application#stop()
	 */
	@Override
	public void stop() throws Exception {
		for( IRelease if_release : i_releases ) {
			if_release.Release();
		}
				
		adb.Release();
		
		if( threadPool != null ) {
			threadPool.shutdownNow();
			
			while( !threadPool.isTerminated()) {
				try {
					Thread.sleep( 300 );
				} catch( Exception e ) {}
			}
		}
		threadPool = null;
	}
		
	/**
	 * Device 정보를 획득할 수 있는 ADB 객체를 반환한다. 
	 * 
	 * @return
	 */
	public ADB getADB() {
		return adb;
	}
	
	public Window getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Application 에서 사용하는 설정값 정보 파일을 불러 온다.
	 * 
	 * @param path
	 * @return 성공 시에 Property 객체를 실패 시에는 null 이 반환한다.  
	 */
	public PropertyEx getProperty() {
		String path = PathUtils.GetCurrentPath( MainClass.instance );
		File prop_file = new File( path, "MyApkTestTool.prop" );
		
		PropertyEx prop = new PropertyEx("Purehero APK test tool");
		try {
			prop.load( prop_file.getAbsolutePath() );
		} catch (IOException e) {
		}
		
		return prop;
	}
	
	/**
	 * Thread pool 을 이용하여 Runnable 객체를 실행한다. 
	 * Thread poll 을 사용하면 종료시에 Process 가 종료되지 않고 hang 이 걸리게 된다. 때문에 매번 새로운 Thread 에서 실행되도록 한다. 
	 * 
	 * @param command
	 */
	synchronized public void runThreadPool( Runnable command ) {
		if( threadPool != null ) {
			threadPool.execute(command);
			
		} else {
			new Thread( command ).start();
		}
		
	}
}
