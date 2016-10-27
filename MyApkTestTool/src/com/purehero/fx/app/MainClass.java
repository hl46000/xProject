package com.purehero.fx.app;

import java.io.File;
import java.io.IOException;

import com.purehero.common.io.FileUtils;
import com.purehero.common.io.PathUtils;
import com.purehero.common.io.PropertyEx;

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
		
		Parent mainView = FXMLLoader.load( MainClass.this.getClass().getResource("view/mainView.fxml"));
		Scene scene = new Scene( mainView, -1, -1, Color.WHITE);		
		primaryStage.setScene( scene );
		primaryStage.setResizable(true);
		primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		System.out.println("STOP");
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
