package com.purehero.atm.v3;

import java.io.File;
import java.io.InputStream;

import com.android.ddmlib.AndroidDebugBridge;
import com.purehero.atm.v3.model.AdbV3;
import com.purehero.atm.v3.model.UtilV3;

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

	public MainClass() {
		super();
		
		try {
			AndroidDebugBridge.init(true);
		} catch( Exception e ){}
		
		ClassLoader clsLoader = getClass().getClassLoader();
		AdbV3.setAdbPath ( checkPath( clsLoader, "adb/adb.exe" ));
		AdbV3.setAaptPath( checkPath( clsLoader, "aapt/aapt.exe" ));
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent mainView = FXMLLoader.load( MainClass.this.getClass().getResource("view/mainView.fxml"));
		
		Scene scene = new Scene( mainView, 800, 900, Color.WHITE);
		primaryStage.setTitle( "Android Touch Macro v3.0" );
		primaryStage.setScene( scene );
		primaryStage.setResizable(true);
		primaryStage.show();
	}	
	
	@Override
	public void stop() throws Exception {
		super.stop();
		
		AndroidDebugBridge.terminate();
	}

	/**
	 * 리소스 내의 실행 파일을 실행할 수 있는 위치로 이동 시키고 파일의 경로를 반환한다. 
	 * 
	 * @param clsLoader
	 * @param resName
	 * @return
	 */
	private String checkPath( ClassLoader clsLoader, String resName ) {
		File inFile		= new File( resName );
		File outFile 	= new File( UtilV3.GetTempPath(), inFile.getName());
		if( outFile.exists() ) return outFile.getAbsolutePath();
		
		outFile.getParentFile().mkdirs();
		
		InputStream is = clsLoader.getResourceAsStream( resName );
		if( is == null ) return "";
		
		UtilV3.fileWrite( is, outFile );
		
		return outFile.getAbsolutePath();
	}
}
