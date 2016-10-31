package com.purehero.fx.app.view.work;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TitledPane;

public class TestViewController implements EventHandler<ActionEvent>{
	
	/**
	 *  
	 * 
	 * @throws Exception
	 */
	@FXML
    public void initialize() throws Exception {
	}
	
	@FXML
	@Override
	public void handle(ActionEvent event ) {
		Object obj = event.getSource();
		if( obj instanceof Button ) {
			OnClickHandler( obj );
		}
	}

	/**
	 * ActionEvent 에서 click event 을 처리하는 함수 입니다. 
	 * 
	 * @param obj
	 */
	private void OnClickHandler(Object obj) {
		Control ctrl = ( Control ) obj;
		switch( ctrl.getId()) {
		case "ID_BUTTON_DEL_TEST_VIEW" 	: deviceTestViewController.removeTestView( parentTitledPane ); break;
		}
	}

	DeviceTestViewController deviceTestViewController = null;
	public void setDeviceTestViewController( DeviceTestViewController deviceTestViewController) {
		this.deviceTestViewController = deviceTestViewController;
	}

	TitledPane parentTitledPane = null;
	public void setParentTitledPane(TitledPane tp) {
		parentTitledPane = tp;
	}
}
