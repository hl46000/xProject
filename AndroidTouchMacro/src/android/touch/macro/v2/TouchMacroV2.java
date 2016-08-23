package android.touch.macro.v2;

import java.io.File;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TouchMacroV2 extends javafx.application.Application {
	public static void main(String[] args) { 
		instance = new TouchMacroV2();
		launch(args);
	}
		
	public static TouchMacroV2 instance = null;
	
	private Stage primaryStage;
	private DataManager dataManager = null;
	
	public TouchMacroV2() {
		dataManager = new DataManager();
	}
	
	public void setPrimaryStage( Stage primaryStage ) {
		this.primaryStage = primaryStage;
	}

	public Stage getPrimaryStage() { return primaryStage; }
	public DataManager getDataManager() { return dataManager; }
	
	/**
	 * Application 에서 사용하는 설정값 정보 파일을 불러 옵니다.
	 * 
	 * @param path
	 * @return 성공 시에 Property 객체를 실패 시에는 null 이 반환됩니다. 
	 */
	public PropertyV2 load_app_property() {
		String path = getCurrentPath();
		File prop_file = new File( path, "TouchMacroV2.prop" );
		
		PropertyV2 prop = new PropertyV2();
		try {
			prop.load( prop_file.getAbsolutePath() );
		} catch (IOException e) {
						
			prop.setValue( "ADB_PATH", "adb or adb.exe 파일 경로를 여기에 설정해 주세요!!");
			try {
				prop.save( prop_file.getAbsolutePath(), "TouchMacro v2" );
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return prop;
	}

	/**
	 * 현재 앱이 실행 중인 경로를 반환 한다. 
	 * 
	 * @return
	 */
	public String getCurrentPath() {
		return UtilV2.GetCurrentPath( this );
	}
	
	
	@Override
	public void start(Stage primaryStage) throws Exception {		
		setPrimaryStage(primaryStage);
		
		primaryStage.setTitle( "Android Touch Macro v2.0" );
		
		BorderPane root = new BorderPane();
		Parent deviceView = FXMLLoader.load(TouchMacroV2.this.getClass().getResource("view/device.fxml"));
		Parent main = FXMLLoader.load(TouchMacroV2.this.getClass().getResource("view/main.fxml"));
		
		root.setTop( deviceView );		
		root.setCenter( main );
								
		Scene scene = new Scene( root );
		primaryStage.setScene( scene );
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}
