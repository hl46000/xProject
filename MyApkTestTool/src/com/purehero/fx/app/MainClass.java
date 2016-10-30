package com.purehero.fx.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.purehero.android.ADB;
import com.purehero.common.io.FileUtils;
import com.purehero.common.io.PathUtils;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.view.MainViewController;

public class MainClass extends javafx.application.Application {
	public static MainClass instance = null;
	public static void main(String[] args) { 
		instance = new MainClass();
		launch(args);
	}
	
	private Stage primaryStage = null;
	
	private ADB adb = new ADB();
	public MainClass() {
		ClassLoader clsLoader = getClass().getClassLoader();
		
		File adbPath = FileUtils.extractFileFromJar( clsLoader, "adb/adb.exe", GetTempPath());
		adb.Initialize( adbPath );
	}

	private File GetTempPath() {
		return new File( "c:\\temp\\atm_v3" );
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		
		FXMLLoader mainViewLoader = new FXMLLoader(MainClass.this.getClass().getResource("view/mainView.fxml"));
		Parent mainView = mainViewLoader.load();
		
		MainViewController ctrl = (MainViewController) mainViewLoader.getController();
		ctrl.setADB(adb);
		
		Scene scene = new Scene( mainView, -1, -1, Color.WHITE);		
		primaryStage.setScene( scene );
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	
	ArrayList<IRelease> i_releases = new ArrayList<IRelease>(); 
	public void addReleaseInterface( IRelease release_interface ) {
		i_releases.add( release_interface );
	}
	
	@Override
	public void stop() throws Exception {
		for( IRelease if_release : i_releases ) {
			if_release.Release();
		}
		adb.Release();
	}
		
	public ADB getADB() {
		return adb;
	}
	
	public Window getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Application 에서 사용하는 설정값 정보 파일을 불러 옵니다.
	 * 
	 * @param path
	 * @return 성공 시에 Property 객체를 실패 시에는 null 이 반환됩니다. 
	 */
	public PropertyEx getProperty() {
		String path = PathUtils.GetCurrentPath( this );
		File prop_file = new File( path, "MyApkTestTool.prop" );
		
		PropertyEx prop = new PropertyEx("Purehero APK test tool");
		try {
			prop.load( prop_file.getAbsolutePath() );
		} catch (IOException e) {
		}
		
		return prop;
	}
}
