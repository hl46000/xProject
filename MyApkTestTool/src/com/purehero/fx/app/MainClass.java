package com.purehero.fx.app;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.purehero.android.ADB;
import com.purehero.common.io.FileUtils;
import com.purehero.common.io.IRelease;
import com.purehero.common.io.PathUtils;
import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.view.MainViewController;

public class MainClass extends javafx.application.Application {
	public static MainClass instance = null;
	public static void main(String[] args) { 
		//instance = new MainClass();
		//launch(args);
		
		LuaValue luaG 				= JsePlatform.standardGlobals();
		LuaValue CheckLogCatFunc	= null;
		String scriptPath 			= "d:\\testFunc.lua";
		try {
			luaG.get("dofile").call( LuaValue.valueOf( scriptPath ));
			CheckLogCatFunc = luaG.get("testFunc");
			
			List<String> p1 = new ArrayList<String>();
			p1.add( "1111111111" );
			p1.add( "2222222222" );
			p1.add( "2222222222" );
			p1.add( "2222222222" );
			p1.add( 0, String.valueOf( p1.size() ));
			
			LuaValue[] LuaParams = new LuaValue[] {
				    CoerceJavaToLua.coerce(p1.toArray())
				};
			LuaValue retvals = (LuaValue) CheckLogCatFunc.invoke( LuaValue.varargsOf( LuaParams ));
			System.out.println( "java result : " + retvals.toString());
		} catch( Exception e ) {
			e.printStackTrace();
		}
		System.exit(0);
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
	
	/**
	 * 현재 실행 파일의 위치(경로)를 반환합니다. 
	 * 
	 * @return
	 */
	public String getCurrentPath() {
		return PathUtils.GetCurrentPath( this );
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
	
	private static List<IRelease> i_releases = new ArrayList<IRelease>();	// i_releases 객체가 static 이 아니면 stop 함수에서 정상적으로 처리가 되지 않는다.  
	/**
	 * Application 이 종료될때 리소스 해제등이 필요한 경우을 위해 Release 함수를 호출해 주는 interface 을 등록 합니다. 
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
		System.out.println( path );
		
		File prop_file = new File( path, "MyApkTestTool.prop" );
		
		PropertyEx prop = new PropertyEx("Purehero APK test tool");
		try {
			prop.load( prop_file.getAbsolutePath() );
		} catch (IOException e) {
		}
		
		return prop;
	}
}
