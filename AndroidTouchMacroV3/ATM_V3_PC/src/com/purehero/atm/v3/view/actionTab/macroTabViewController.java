package com.purehero.atm.v3.view.actionTab;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.android.chimpchat.adb.AdbChimpDevice;
import com.purehero.atm.v3.ex.DrawInterface;
import com.purehero.atm.v3.ex.ResizableCanvas;
import com.purehero.atm.v3.model.AdbV3;
import com.purehero.atm.v3.model.DeviceInfo;
import com.purehero.atm.v3.model.UtilV3;
import com.purehero.atm.v3.view.deviceListViewController;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class macroTabViewController {
	@FXML
	SplitPane fxSplitPane;
	
	@FXML
	AnchorPane CanvasPane;
	
	ResizableCanvas cvDisplay = new ResizableCanvas();
	
	Image display_image = null;
	double display_ratio = 1.0;
	
	@FXML
    public void initialize() {
		CanvasPane.getChildren().add( cvDisplay );
		
		cvDisplay.widthProperty().bind( CanvasPane.widthProperty() );
        cvDisplay.heightProperty().bind( CanvasPane.heightProperty() );
        cvDisplay.setDrawInterface( canvasDrawInterface );
        cvDisplay.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
            	mouse_event_handler( event );
            }
        });
        
        fxSplitPane.setDividerPosition( 0, 0.7f);
        
        cvDisplay.setOnMouseDragged( mouseHandler );
        cvDisplay.setOnMousePressed( mouseHandler );
        cvDisplay.setOnMouseReleased( mouseHandler );
	}
	
	EventHandler<MouseEvent> mouseHandler = new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent mouseEvent) {
			DeviceInfo device_info = deviceListViewController.instance.getSelectedDeviceItem();
			if( device_info == null ) return;
			
			Point p = new Point((int) mouseEvent.getX(), (int)mouseEvent.getY());
			
			if( mouseEvent.getEventType().equals( MouseEvent.MOUSE_PRESSED )) {
				try {
					Point real = getRealPoint(p);
					device_info.chimpDevice.getManager().touchDown(real.x, real.y);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
			 } else if( mouseEvent.getEventType().equals( MouseEvent.MOUSE_RELEASED )) {
				 try {
					 Point real = getRealPoint(p);
					 device_info.chimpDevice.getManager().touchUp(real.x, real.y);
				 } catch (IOException ex) {
					 ex.printStackTrace();
				 }
			 } else if( mouseEvent.getEventType().equals( MouseEvent.MOUSE_DRAGGED )) {
				 try {
					 Point real = getRealPoint(p);
					 device_info.chimpDevice.getManager().touchMove(real.x, real.y);
				 } catch (IOException ex) {
					 ex.printStackTrace();
				 }
			 }
		}

		private Point getRealPoint(Point p) {
			return new Point((int)((double) p.x / display_ratio ), (int)((double) p.y / display_ratio ));
		}
	};
	
			
	/**
	 * @param sc_w
	 * @param sc_h
	 * @param img_w
	 * @param img_h
	 * @return
	 */
	private double get_display_ratio( double sc_w, double sc_h, double img_w, double img_h ) {
		double w = sc_w / img_w;
		double h = sc_h / img_h;
		
		return Math.min( w, h );
	}
	
	DrawInterface canvasDrawInterface = new DrawInterface() {
		@Override
		public void draw(GraphicsContext gc, double width, double height ) {
			gc.clearRect(0, 0, width, height);
	 
			if( display_image != null ) {
				double img_w = display_image.getWidth();
				double img_h = display_image.getHeight();
				
				display_ratio = get_display_ratio( width, height, img_w, img_h );
				
				gc.drawImage( display_image, 0, 0, img_w, img_h, 0, 0, img_w * display_ratio, img_h * display_ratio );
			} else {			
		    	gc.setStroke(Color.RED);
		    	gc.strokeLine(0, 0, width, height);
		    	gc.strokeLine(0, height, width, 0);
			}
		}		
	};
	
	@FXML
	private void mouse_event_handler( MouseEvent e) {
	} 
	
	@FXML
	private void action_event_handler( ActionEvent e) throws Exception {
		Object obj = e.getSource();
		
		String ctrl_id = null;
		if( obj instanceof Button) 			ctrl_id = (( Button ) obj ).getId();
		else if( obj instanceof MenuItem ) 	ctrl_id = (( MenuItem ) obj).getId();
				
		if( ctrl_id == null ) return;
		
		switch( ctrl_id ) {
		case "ID_BTN_CAPTURE_SCREEN" 	: OnClickButtonCaptureScreen(); break;
		case "ID_BTN_IMAGE_ROTATE_N90"	: 
			display_image = UtilV3.rotate( display_image, -90.0f ); 
			redraw_display_image(); 
			break;
			
		case "ID_BTN_IMAGE_ROTATE_P90"	: 
			display_image = UtilV3.rotate( display_image, 90.0f ); 
			redraw_display_image(); 
			break;
		}
	}
	
	private void OnClickButtonCaptureScreen() throws Exception {
		DeviceInfo device_info = deviceListViewController.instance.getSelectedDeviceItem();
		if( device_info == null ) return;
		
		BufferedImage img = AdbV3.screenCaptureEx( device_info );
		if( img != null ) {
			display_image = SwingFXUtils.toFXImage( img, null);
			redraw_display_image();
		}
	}

	/**
	 * 
	 */
	private void redraw_display_image() {
		canvasDrawInterface.draw( cvDisplay.getGraphicsContext2D(), cvDisplay.getWidth(), cvDisplay.getHeight());
	}

	private double get_display_screen_width()  { return cvDisplay.getWidth();  }
	private double get_display_screen_height() { return cvDisplay.getHeight(); }
}
