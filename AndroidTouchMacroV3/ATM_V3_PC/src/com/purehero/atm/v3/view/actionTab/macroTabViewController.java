package com.purehero.atm.v3.view.actionTab;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.purehero.atm.v3.MainClass;
import com.purehero.atm.v3.ex.DrawInterface;
import com.purehero.atm.v3.ex.ResizableCanvas;
import com.purehero.atm.v3.model.AdbV3;
import com.purehero.atm.v3.model.DeviceInfo;
import com.purehero.atm.v3.model.PropertyEx;
import com.purehero.atm.v3.model.ScreenData;
import com.purehero.atm.v3.model.UtilV3;
import com.purehero.atm.v3.view.deviceListViewController;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class macroTabViewController {
	
	@FXML
	SplitPane fxSplitPane;
	
	@FXML
	AnchorPane CanvasPane;

	@FXML
	private Label lbScreenPageInfo;						// 화면 Page 정보 표시 Lable
	
	ResizableCanvas cvDisplay = new ResizableCanvas();
	
	Image 	display_image = null;
	double 	display_ratio = 1.0f;
	double 	display_rotate = 0.0f;
	
	private List<ScreenData> screenDatas = new ArrayList<ScreenData>();
	private int nCurrentScreenIdx = 0;
	private File loaded_image_file = null;
		
	@FXML
    public void initialize() {
		CanvasPane.getChildren().add( cvDisplay );
		
		cvDisplay.widthProperty().bind( CanvasPane.widthProperty() );
        cvDisplay.heightProperty().bind( CanvasPane.heightProperty() );
        cvDisplay.setDrawInterface( canvasDrawInterface );
        cvDisplay.setOnMouseClicked ( mouse_event );
        cvDisplay.setOnMousePressed ( mouse_event );
        cvDisplay.setOnMouseReleased( mouse_event );
        cvDisplay.setOnMouseDragged ( mouse_event );
        
        double divier_pos = 0.78f;
        fxSplitPane.setDividerPosition( 0, divier_pos );
	}
	
	EventHandler<MouseEvent> mouse_event = new EventHandler<MouseEvent>(){
		@Override
        public void handle(MouseEvent event) { mouse_event_handler( event ); }
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
	
	
	// *************************************************************************************
	boolean bMousePressed 		= false;
	boolean bMouseDragged 		= false;
	Point mouse_pressed_pos		= new Point(0,0);		//  
	Point mouse_released_pos	= new Point(0,0);
	Point mouse_clicked_pos 	= new Point(0,0);
	long mouse_pressed_time		= 0;		// 화면 Touch down 시간
	long mouse_released_time 	= 0;		// 화면 Touch up 시간
	long mouse_dragged_time 	= 0;		// 화면 Drag 마지막 시간
	// *************************************************************************************
	
	@FXML
	private void mouse_event_handler( MouseEvent e) {
		DeviceInfo device_info = deviceListViewController.instance.getSelectedDeviceItem();
		if( device_info == null ) return;
		
		if( e.getSource() == cvDisplay ) {
			Point mouse_device_pos = new Point( (int)( e.getX() / display_ratio ), (int)( e.getY() / display_ratio ) );
						
			if( e.getEventType().equals( MouseEvent.MOUSE_CLICKED)) {
				if( System.currentTimeMillis() - mouse_dragged_time > 1000 ) {
					mouse_clicked_pos = mouse_device_pos;
					AdbV3.touchScreen( mouse_clicked_pos.x, mouse_clicked_pos.y, device_info  );
				}
				
			} else if( e.getEventType().equals( MouseEvent.MOUSE_PRESSED )) {
				mouse_pressed_time 	= System.currentTimeMillis();
				mouse_pressed_pos 	= mouse_device_pos;
				
				bMousePressed 	= true;
				bMouseDragged	= false;
								
			} else if( e.getEventType().equals( MouseEvent.MOUSE_RELEASED )) {
				mouse_released_time = System.currentTimeMillis();
				mouse_released_pos	= mouse_device_pos;
						
				if( bMousePressed && bMouseDragged ) {
					AdbV3.swipeScreen( 
						mouse_pressed_pos.x, mouse_pressed_pos.y, 
						mouse_released_pos.x, mouse_released_pos.y, 
						mouse_released_time - mouse_pressed_time, device_info  );
				}
				bMousePressed = false;
				bMouseDragged = false;
				
			} else if( e.getEventType().equals( MouseEvent.MOUSE_DRAGGED )) {
				mouse_dragged_time = System.currentTimeMillis();
				bMouseDragged = true;				
			}
		}
	} 
	
	@FXML
	private void action_event_handler( ActionEvent e) {
		Object obj = e.getSource();
		
		String ctrl_id = null;
		if( obj instanceof Button) 			ctrl_id = (( Button ) obj ).getId();
		else if( obj instanceof MenuItem ) 	ctrl_id = (( MenuItem ) obj).getId();
				
		if( ctrl_id == null ) return;
		
		switch( ctrl_id ) {
		case "ID_BTN_CAPTURE_SCREEN" 	: OnClickButtonCaptureScreen(); break;
		case "ID_BTN_IMAGE_ROTATE_N90"	: OnClickButtonImageRotateN90(); break; 			
		case "ID_BTN_IMAGE_ROTATE_P90"	: OnClickButtonImageRotateP90(); break;
		case "ID_BTN_SAVE_IMAGE"		: OnClickButtonImageSave(); break;
		case "ID_BTN_LOAD_IMAGE"		: OnClickButtonImageLoad(); break;
		case "ID_BTN_NEW_SCRIPT"		: OnClickButtonScriptNew(); break;
		case "ID_BTN_SAVE_SCRIPT"		: OnClickButtonScriptSave(); break;
		case "ID_BTN_LOAD_SCRIPT"		: OnClickButtonScriptLoad(); break;
			
		case "ID_BTN_ADD_SCREEN_DATA_AT_PREV"	: OnClickButtonAddScreenDataAtPrev(); break;
		case "ID_BTN_ADD_SCREEN_DATA_AT_NEXT"	: OnClickButtonAddScreenDataAtNext(); break; 
		case "ID_BTN_DEL_SCREEN_DATA"	: OnClickButtonDeleteScreenData(); break;
		case "ID_BTN_MOD_SCREEN_DATA"	: OnClickButtonModifyScreenData(); break;
		
		case "ID_BTN_MOVE_SCREEN_PREV"		: OnClickButtonMoveScreenDataPrev(); break;
		case "ID_BTN_MOVE_SCREEN_NEXT"		: OnClickButtonMoveScreenDataNext(); break;
		default :
			System.out.println( ctrl_id + " : 아직 구현되지 않은 ID 입니다. " );
			break;
		}
	}
	
	private void OnClickButtonMoveScreenDataPrev() { moveScreenData( false ); }
	private void OnClickButtonMoveScreenDataNext() { moveScreenData( true ); }
	private boolean moveScreenData(boolean bNext ) {
		if( bNext ) {
			if( nCurrentScreenIdx + 1 >= screenDatas.size()) {
				return false;
			}
			nCurrentScreenIdx++;
			if( nCurrentScreenIdx + 1 >= screenDatas.size()) {
			}
		} else {
			if( nCurrentScreenIdx < 1 ) {
				return false;
			}
			
			nCurrentScreenIdx--;
			if( nCurrentScreenIdx == 0 ) {
			}
		}
		
		ScreenData data = screenDatas.get( nCurrentScreenIdx );
		System.out.println( "nCurrentScreenIdx : " + nCurrentScreenIdx );
		data.print();
		
		mouse_clicked_pos	= data.point;
		loaded_image_file	= data.image;
		
		//tfDelayTime.setText( String.valueOf( data.delayTime ) );
		//rbClickTypeTap.setSelected( data.type == ScreenData.TYPE_TAP );
		try {
			BufferedImage img = ImageIO.read( loaded_image_file );
			if( img != null ) {
				display_image = SwingFXUtils.toFXImage( img, null );
				
				Platform.runLater( new Runnable(){
					@Override
					public void run() {
						redraw_display_image();
					}});
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		updateScreenPageInfo();
		return true;
	}
	
	/**
	 * 현재의 Screen 데이터를 Script Index 앞에 추가 합니다.
	 */
	private void OnClickButtonAddScreenDataAtPrev() {
		ScreenData data = getCurrentScreenData();
		
		if( nCurrentScreenIdx == 0 ) {
			screenDatas.add( 0, data );
			nCurrentScreenIdx = 0;
		} else {
			screenDatas.add( --nCurrentScreenIdx, data );
		}
		updateScreenPageInfo();
	}

	/**
	 * 현재의 Screen 데이터를 Script Index 뒤에 추가 합니다.  
	 */
	private void OnClickButtonAddScreenDataAtNext() {
		ScreenData data = getCurrentScreenData();
		
		if( screenDatas.isEmpty()) {
			screenDatas.add( data );
			nCurrentScreenIdx = 0;
		} else {
			screenDatas.add( ++nCurrentScreenIdx, data );
		}
		updateScreenPageInfo();
	}

	/**
	 * 현재 화면에 보여지는 데이터를 ScreenData 객체로 반환 합니다.
	 * 
	 * @return
	 */
	private ScreenData getCurrentScreenData() {
		ScreenData data = new ScreenData();
		data.point		= mouse_pressed_pos;
		data.point2		= mouse_released_pos;
		//data.swipeTime	= swipeTime;
		//data.type		= rbClickTypeTap.isSelected() ? ScreenData.TYPE_TAP : ScreenData.TYPE_SWIPE;
		data.type		= ScreenData.TYPE_TAP;
		data.image		= loaded_image_file;
		//data.delayTime = Integer.valueOf( tfDelayTime.getText());
		data.delayTime = 3000;
		
		return data;
	}

	private void OnClickButtonModifyScreenData() {
		// TODO Auto-generated method stub
		
	}
	
	private void OnClickButtonDeleteScreenData() {
		// TODO Auto-generated method stub
		
	}

	private void OnClickButtonScriptLoad() {
		String last_script_path = getLastScriptPath();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("불러올 스크립트파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("SCRIPT", "*.script") );
		if( last_script_path != null ) {
			File file_last_script_path = new File( last_script_path );
			if( file_last_script_path.exists()) {
				fileChooser.setInitialDirectory( file_last_script_path );
			}
		}
		File result = fileChooser.showOpenDialog( MainClass.instance.getPrimaryStage());
		if( result == null ) return;
	
		PropertyEx scriptInfo = new PropertyEx();
		try {
			scriptInfo.load( result.getAbsolutePath() );
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		screenDatas.clear();
		
		int count = Integer.valueOf( scriptInfo.getValue( "COUNT" ));
		String name = scriptInfo.getValue( "NAME" );
		for( int i = 0; i < count; i++ ) {
			ScreenData sData = new ScreenData();
			sData.image 	= new File(scriptInfo.getValue( String.format( "%03d_IMAGE", i )));
			
			try {
				sData.type		= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_TYPE", i ) ));
			} catch( Exception e ) { sData.type = ScreenData.TYPE_TAP; }
			sData.point.x 	= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_X", i ) ));
			sData.point.y 	= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_Y", i ) ));
			try {
				sData.point2.x 	= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_X2", i ) ));
				sData.point2.y 	= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_Y2", i ) ));
			} catch( Exception e ) { sData.point2 = sData.point; } 
			sData.delayTime = Integer.valueOf( scriptInfo.getValue( String.format( "%03d_DELAYTIME", i ) ));
			try {
				sData.swipeTime	= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_SWIPETIME", i ) ));
			} catch( Exception e ) { sData.swipeTime = 1000; }
		
			screenDatas.add(sData);
		}
		
		nCurrentScreenIdx = 1;
		moveScreenData(false);
		
		updateScreenPageInfo();
	}

	private void OnClickButtonScriptSave() {
		String last_script_path = getLastScriptPath();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("저장할 스크립트파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("SCRIPT", "*.script") );
		if( last_script_path != null ) {
			File file_last_script_path = new File( last_script_path );
			if( file_last_script_path.exists()) {
				fileChooser.setInitialDirectory( file_last_script_path );
			}
		}
		File result = fileChooser.showSaveDialog( MainClass.instance.getPrimaryStage());
		if( result == null ) return;
		
		PropertyEx scriptInfo = new PropertyEx();
		scriptInfo.setValue( "COUNT", String.valueOf( screenDatas.size()));
		scriptInfo.setValue( "NAME", result.getName() );
		
		int index = 0;
		for( ScreenData sData : screenDatas ) {
			scriptInfo.setValue( String.format( "%03d_IMAGE", index ), sData.image.getAbsolutePath());			
			scriptInfo.setValue( String.format( "%03d_TYPE", index ), String.valueOf( sData.type ));
			scriptInfo.setValue( String.format( "%03d_X", index ), String.valueOf( sData.point.x ));
			scriptInfo.setValue( String.format( "%03d_Y", index ), String.valueOf( sData.point.y ));
			scriptInfo.setValue( String.format( "%03d_X2", index ), String.valueOf( sData.point2.x ));
			scriptInfo.setValue( String.format( "%03d_Y2", index ), String.valueOf( sData.point2.y ));
			scriptInfo.setValue( String.format( "%03d_DELAYTIME", index ), String.valueOf( sData.delayTime ));
			scriptInfo.setValue( String.format( "%03d_SWIPETIME", index ), String.valueOf( sData.swipeTime ));
			index++;
		}
		try {
			scriptInfo.save( result.getAbsolutePath(), result.getName() );
			setLastScriptPath( result.getParent());
			
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}

	

	private void OnClickButtonScriptNew() {
		// TODO Auto-generated method stub
		
	}

	private void OnClickButtonImageLoad() {
		String last_image_path = getLastImagePath();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지 파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("PNG", "*.png") );
		if( last_image_path != null ) {
			File file_last_load_path = new File( last_image_path );
			if( file_last_load_path.exists()) {
				fileChooser.setInitialDirectory(file_last_load_path);
			}
		}
		
		loaded_image_file = fileChooser.showOpenDialog( MainClass.instance.getPrimaryStage());
		if( loaded_image_file == null ) return;
		try {
			BufferedImage img = ImageIO.read( loaded_image_file );
			if( img != null ) {
				display_image = SwingFXUtils.toFXImage( img, null);
				
				redraw_display_image();
				setLastImagePath( loaded_image_file.getParent());
			}		
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private void OnClickButtonImageSave() {
		String last_image_path = getLastImagePath();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지를 저장할 파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("PNG", "*.png") );
		if( last_image_path != null ) {
			File file_last_save_path = new File( last_image_path );
			if( file_last_save_path.exists()) {
				fileChooser.setInitialDirectory( file_last_save_path );
			}
		}
		
		File result = fileChooser.showSaveDialog( MainClass.instance.getPrimaryStage());
		if( result == null ) return;
		try {
			result.delete();
			ImageIO.write( SwingFXUtils.fromFXImage( display_image, null), "PNG", result );
			
			setLastImagePath( result.getParent());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void OnClickButtonImageRotateP90() {
		if( display_image == null ) return;
		
		display_rotate = display_rotate == 270.0f ? 0.0f : display_rotate + 90.0f;
		display_image = UtilV3.rotate( display_image, 90.0f ); 
		redraw_display_image(); 		
	}

	private void OnClickButtonImageRotateN90() {
		if( display_image == null ) return;
		
		display_rotate = display_rotate == 0.0f ? 270.0f : display_rotate - 90.0f;
		display_image = UtilV3.rotate( display_image, -90.0f ); 
		redraw_display_image(); 
	}

	private void OnClickButtonCaptureScreen() {
		new Thread( new Runnable() {

			@Override
			public void run() {
				DeviceInfo device_info = deviceListViewController.instance.getSelectedDeviceItem();
				if( device_info == null ) return;
				
				File capture_folder = new File( UtilV3.GetTempPath(), "scree_capture" );
				capture_folder.mkdirs();
				
				File capture_file = new File( capture_folder, device_info.getSerialNumber() + ".png" );
				capture_file.delete();
				
				BufferedImage img = AdbV3.screenCapture( device_info, capture_file);
				
				if( img != null ) {
					display_image = SwingFXUtils.toFXImage( img, null);
					display_image = UtilV3.rotate( display_image, display_rotate );
					
					try { Thread.sleep( 100 ); } catch (InterruptedException e) { e.printStackTrace(); }
					Platform.runLater( new Runnable(){
						@Override
						public void run() {
							redraw_display_image();
						}});					
				}					
			}
		}).start();
	}

	/**
	 * Screen의 Page 정보를 갱신하여 표시 합니다. 
	 */
	private void updateScreenPageInfo() {
		lbScreenPageInfo.setText( String.format( "%d/%d", nCurrentScreenIdx + 1, screenDatas.size()));
	}
	
	/**
	 * 
	 */
	private void redraw_display_image() {
		cvDisplay.redraw();		
	}

	/**
	 * 가장 마지막에 접근한 이미지 경로를 저장합니다. 
	 * 
	 * @param parent
	 */
	private void setLastImagePath(String parent) {
		PropertyEx prop = MainClass.instance.load_app_property();
		prop.setValue( "LAST_IMAGE_PATH", parent);
		try {
			prop.save("TouchMacro v3.0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 가장 마지막에 접근한 이미지 경로를 반환 합니다. 
	 * 
	 * @return
	 */
	private String getLastImagePath() {
		PropertyEx prop = MainClass.instance.load_app_property();
		return prop.getValue("LAST_IMAGE_PATH");
	}
	
	/**
	 * 가장 마지막에 접근한 이미지 경로를 저장합니다. 
	 * 
	 * @param parent
	 */
	private void setLastScriptPath(String parent) {
		PropertyEx prop = MainClass.instance.load_app_property();
		prop.setValue( "LAST_SCRIPT_PATH", parent);
		try {
			prop.save("TouchMacro v3.0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	private String getLastScriptPath() {
		PropertyEx prop = MainClass.instance.load_app_property();
		return prop.getValue("LAST_SCRIPT_PATH");
	}
	
	//private double get_display_screen_width()  { return cvDisplay.getWidth();  }
	//private double get_display_screen_height() { return cvDisplay.getHeight(); }
}
