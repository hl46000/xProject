package com.purehero.atm.v3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.purehero.atm.v3.model.AdbV3;
import com.purehero.atm.v3.model.PropertyEx;
import com.purehero.atm.v3.model.UtilV3;

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

	public MainClass() {
		super();
				
		ClassLoader clsLoader = getClass().getClassLoader();
		
		AdbV3.setAdbPath ( checkPath( clsLoader, "adb/adb.exe" ));
		AdbV3.setAaptPath( checkPath( clsLoader, "aapt/aapt.exe" ));
	}
	
	private Stage primaryStage = null;
	@Override
	public void start(Stage _primaryStage) throws Exception {
		primaryStage = _primaryStage;
		Parent mainView = FXMLLoader.load( MainClass.this.getClass().getResource("view/mainView.fxml"));
		
		Scene scene = new Scene( mainView, 800, 900, Color.WHITE);
		primaryStage.setTitle( "Android Touch Macro v3.0" );
		primaryStage.setScene( scene );
		primaryStage.setResizable(true);
		primaryStage.show();
	}	
	
	ArrayList<IFRelease> if_releases = new ArrayList<IFRelease>(); 
	public void addReleaseInterface( IFRelease release_interface ) {
		if_releases.add( release_interface );
	}
	
	@Override
	public void stop() throws Exception {
		for( IFRelease if_release : if_releases ) {
			if_release.Release();
		}
		
		super.stop();
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

	/**
	 * 현재 앱이 실행 중인 경로를 반환 한다. 
	 * 
	 * @return
	 */
	public String getCurrentPath() {
		String jarDir = getClass().getClassLoader().getResource("").getPath();
		if( System.getProperty( "os.name" ).contains( "Window" )) {
			if( jarDir.startsWith("/")) jarDir = jarDir.substring(1);
		}
		
		return jarDir;
	}
	
	/**
	 * Application 에서 사용하는 설정값 정보 파일을 불러 옵니다.
	 * 
	 * @param path
	 * @return 성공 시에 Property 객체를 실패 시에는 null 이 반환됩니다. 
	 */
	public PropertyEx load_app_property() {
		String path = getCurrentPath();
		File prop_file = new File( path, "TouchMacroV3.prop" );
		
		PropertyEx prop = new PropertyEx();
		try {
			prop.load( prop_file.getAbsolutePath() );
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
		return prop;
	}
	
	public Window getPrimaryStage() {
		return primaryStage;
	}
}
