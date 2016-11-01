package com.purehero.fx.control.ex;

import javafx.scene.control.TitledPane;

public class TitledPaneEx extends TitledPane {
	
	private Object controller = null;
	public void setController( Object controller ) {
		this.controller = controller; 
	}
	
	public Object getController() {
		return controller;
	}

}
