package com.purehero.atm.v3.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.SplitPane;

public class mainViewController {
	
	@FXML
	SplitPane fxSplitPane;
	
	@FXML
    public void initialize() throws Exception {
		Parent deviceListView 	= FXMLLoader.load( mainViewController.this.getClass().getResource("deviceListView.fxml"));
		Parent actionTabView 	= FXMLLoader.load( mainViewController.this.getClass().getResource("actionTabView.fxml"));
		
		fxSplitPane.getItems().addAll( deviceListView, actionTabView );
		fxSplitPane.setDividerPosition( 0, 0.3f);
	}
}
