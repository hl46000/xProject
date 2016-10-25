package com.purehero.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.android.ddmlib.IDevice;
import com.purehero.common.io.FileUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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
		System.out.println("START");
		this.primaryStage = primaryStage;
		
		Parent mainView = FXMLLoader.load( MainClass.this.getClass().getResource("view/mainView.fxml"));
		Scene scene = new Scene( mainView, -1, -1, Color.WHITE);		
		primaryStage.setScene( scene );
		primaryStage.setResizable(true);
		primaryStage.show();
	}

	@Override
	public void init() throws Exception {
		System.out.println("INIT");
	}

	@Override
	public void stop() throws Exception {
		System.out.println("STOP");
		adb.Release();
	}
		
	public ADB getADB() {
		return adb;
	}
	
	public List<DeviceInfo> getDevices() {
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		for( IDevice device : adb.getDevices()) {
			devices.add( new DeviceInfo( device ));
		}
		return devices;
	}
}
