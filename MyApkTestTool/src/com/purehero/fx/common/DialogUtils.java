package com.purehero.fx.common;

import java.io.File;

import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.MainClass;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;

public class DialogUtils {
	
	/**
	 * Alert 창의 띄운다. 
	 * 
	 * @param title
	 * @param message
	 * @param type
	 */
	public static void alert( String title, String message, AlertType type ) {
		Alert alert = new Alert( type );
		alert.setTitle( title );
		alert.setHeaderText(null);
		alert.setContentText( message );
		
		alert.showAndWait();
	}
		
	/**
	 * 파일 선택 Dialog 을 띄움니다. 
	 * 
	 * @param title
	 * @param extensions 확장자 정보 <br>ex) "APK FILE","*.apk"<br>ex) "APK FILE","*.apk","ALL FILE","*.*"
	 * @return
	 */
	public static File openDialog( String title, String...extensions ) {
		return fileDialog( title, false, extensions );
	}
	
	/**
	 * 저장하기 위한 파일 선택 Dialog 을 띄움니다. 
	 * 
	 * @param title
	 * @param extensions 확장자 정보 <br>ex) "APK FILE","*.apk"<br>ex) "APK FILE","*.apk","ALL FILE","*.*"
	 * @return
	 */
	public static File saveDialog( String title, String...extensions ) {
		return fileDialog( title, true, extensions );
	}
	
	private static File fileDialog( String title, boolean isSave, String...extensions ) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle( title );
		
		for( int i = 0; i + 1 < extensions.length; i+= 2 ) {
			fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( extensions[i], extensions[i+1]) );
		}
		
		String prop_key = ( isSave ? "SAVE_DIALOG_" : "OPEN_DIALOG_" ) + title;
		
		PropertyEx prop = MainClass.instance.getProperty();
		String oldPath = prop.getValue( prop_key );
		if( oldPath != null ) {
			fileChooser.setInitialDirectory( new File( oldPath ));
		}
		
		File ret = isSave ? fileChooser.showSaveDialog( MainClass.instance.getPrimaryStage()) : 
			fileChooser.showOpenDialog( MainClass.instance.getPrimaryStage());
		if( ret != null ) {
			if( ret.exists() ) {
				prop.setValue( prop_key, ret.getParent());
				prop.save();
			}
		}
		return ret;
	}
}
