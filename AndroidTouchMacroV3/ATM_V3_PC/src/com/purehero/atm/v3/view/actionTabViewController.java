package com.purehero.atm.v3.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

public class actionTabViewController {
	@FXML
	TabPane fxTabPane;
	
	
    public void initialize() throws Exception {
    	String [] TabViewNames = { 
    		"actionTab/macroTabView.fxml"	,"Touch Macro", 
    		"actionTab/apkTestTabView.fxml"	,"APK Test",
    		"actionTab/consoleTabView.fxml"	,"Console"
    	};
    	
    	for( int i = 0; i < TabViewNames.length;  ) {
    		try {
    			Parent view 	= FXMLLoader.load( actionTabViewController.this.getClass().getResource( TabViewNames[i++] ));
    			if( view == null ) { i++; continue; }
    			
    			Tab tab = new Tab( TabViewNames[i++] );
    			tab.setContent( view );
    			fxTabPane.getTabs().add( tab );
    		} catch( Exception e ) {}
    	}  
	}
	
	@FXML
	private void mouse_event_handler( MouseEvent e) {
	}
	
	@FXML
	private void action_event_handler( ActionEvent e) {
	}
}
