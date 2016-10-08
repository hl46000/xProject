package com.purehero.atm.v3.view.actionTab;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import com.purehero.atm.v3.ex.ResizableCanvas;

public class macroTabViewController {
	@FXML
	SplitPane fxSplitPane;
	
	@FXML
	AnchorPane CanvasPane;
	
	ResizableCanvas cvDisplay = new ResizableCanvas();
	
	@FXML
    public void initialize() {
		CanvasPane.getChildren().add( cvDisplay );
		
		cvDisplay.widthProperty().bind( CanvasPane.widthProperty() );
        cvDisplay.heightProperty().bind( CanvasPane.heightProperty() );
        
        fxSplitPane.setDividerPosition( 0, 0.7f);
	}
	
	
	@FXML
	private void mouse_event_handler( MouseEvent e) {
	} 
	
	@FXML
	private void action_event_handler( ActionEvent e) {
	}
	
	private double get_display_screen_width()  { return cvDisplay.getWidth();  }
	private double get_display_screen_height() { return cvDisplay.getHeight(); }
}
