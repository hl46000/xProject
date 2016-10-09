package com.purehero.atm.v3.ex;

import javafx.scene.canvas.Canvas;

public class ResizableCanvas extends Canvas {
	private DrawInterface draw_interface = null;
	public void setDrawInterface( DrawInterface _if ) {
		draw_interface = _if;
	}
	
	public ResizableCanvas() {
		widthProperty().addListener(evt -> draw());
		heightProperty().addListener(evt -> draw());
    }
 
    private void draw() {
    	if( draw_interface != null ) {
    		draw_interface.draw( getGraphicsContext2D(), getWidth(), getHeight() );
    	}
    }
 
    @Override
    public boolean isResizable() {
    	return true;
    }
 
    @Override
    public double prefWidth(double height) {
    	return getWidth();
    }
 
    @Override
    public double prefHeight(double width) {
    	return getHeight();
    }
}
