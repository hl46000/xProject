package com.purehero.fx.common;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.MainClass;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DialogUtils {
	
	/**
	 * Alert 창의 띄운다. 
	 * 
	 * @param title
	 * @param message
	 * @param type
	 */
	public static ButtonType alert( String title, String message, AlertType type ) {
		Alert alert = new Alert( type );
		alert.setTitle( title );
		alert.setHeaderText(null);
		alert.setContentText( message );
		
		return alert.showAndWait().get();
	}
		
	/**
	 * 파일 선택 Dialog 을 띄움니다. 
	 * 
	 * @param title
	 * @param extensions 확장자 정보 <br>ex) "APK FILE","*.apk"<br>ex) "APK FILE","*.apk","ALL FILE","*.*"
	 * @return
	 */
	public static File openFileDialog( String title, String...extensions ) {
		return fileDialog( title, false, extensions );
	}
	
	/**
	 * 저장하기 위한 파일 선택 Dialog 을 띄움니다. 
	 * 
	 * @param title
	 * @param extensions 확장자 정보 <br>ex) "APK FILE","*.apk"<br>ex) "APK FILE","*.apk","ALL FILE","*.*"
	 * @return
	 */
	public static File saveFileDialog( String title, String...extensions ) {
		return fileDialog( title, true, extensions );
	}
	
	private static File fileDialog( String title, boolean isSave, String...extensions ) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle( title );
		
		for( int i = 0; i + 1 < extensions.length; i+= 2 ) {
			fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter( extensions[i], extensions[i+1]) );
		}
		
		String prop_key = ( isSave ? "SAVE_FILE_DIALOG_" : "OPEN_FILE_DIALOG_" ) + title;
		
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
	
	/**
	 * @param title
	 * @return
	 */
	public static File openDirectoryDialog( String title ) {
		DirectoryChooser dirChooser = new DirectoryChooser(); 
		dirChooser.setTitle( title );
		
		String prop_key = null;
		try {
			prop_key = "OPEN_DIR_DIALOG_" + URLEncoder.encode( title, "UTF-8" );
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		
		PropertyEx prop = MainClass.instance.getProperty();
		String oldPath = prop.getValue( prop_key );
		if( oldPath != null ) {
			dirChooser.setInitialDirectory( new File( oldPath ));
		}
		
		File ret = dirChooser.showDialog(MainClass.instance.getPrimaryStage()); 
		if( ret != null ) {
			if( ret.exists() ) {
				prop.setValue( prop_key, ret.getAbsolutePath() );
				prop.save();
			}
		}
		return ret;
	}
	
	/**
	 * @param res_name
	 * @throws IOException
	 */
	public static void showResDialog(String res_name) throws IOException {
        final FXMLLoader loader = new FXMLLoader( MainClass.instance.getClass().getResource(res_name));
        final Parent root = loader.load();
        final Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.UNDECORATED);
        //stage.initOwner(emailField.getScene().getWindow());
        stage.setScene(scene);
        stage.show();
    }
}
