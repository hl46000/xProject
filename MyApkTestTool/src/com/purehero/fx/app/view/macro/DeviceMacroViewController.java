package com.purehero.fx.app.view.macro;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import com.purehero.android.DeviceInfo;
import com.purehero.common.io.ImageUtils;
import com.purehero.common.io.PropertyEx;
import com.purehero.common.io.ZipUtils;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.app.view.MainViewController;
import com.purehero.fx.common.DialogUtils;
import com.purehero.fx.control.ex.DrawInterface;
import com.purehero.fx.control.ex.ResizableCanvas;

public class DeviceMacroViewController {
final 	double DIV_UNIT = 5000.0f;					// 실제 화면을 분활할 값 ( 100분률을 사용하면 좌표가 깨진다. 100 보다 큰값을 사용한다. )
	
	@FXML
	SplitPane fxSplitPane;
	
	@FXML
	AnchorPane CanvasPane;

	@FXML
	private Label lbScreenPageInfo;						// 화면 Page 정보 표시 Lable
	
	@FXML
	private Label lbScriptSubject;						// script 제목 표시 label
	
	@FXML
	private TextField tfDelayTime;						// Delay time textField

	@FXML
	private RadioButton rbClickTypeTap;					// click type을 결정하는 RadioButton 중 tap
	
	@FXML
	private Button btnScriptControl;					// script play / stop 버튼

	@FXML
	private Label lbDelayTimeTitle;
	
	ResizableCanvas cvDisplay = new ResizableCanvas();
	
	
	
	Image 	display_image = null;
	Image	arrow_image = null;
	double 	display_ratio = 1.0f;
	double 	display_rotate = 0.0f;
	double	display_screen_width = 0.0f;				// 화면에 표시되는 이미지의 넓이
	double 	display_screen_height = 0.0f;				// 화면에 표시되는 이미지의 높이
	
	private List<ScreenData> screenDatas = new ArrayList<ScreenData>();
	private int nCurrentScreenIdx = 0;
	private File loaded_image_file = null;
	
	MainViewController mainViewController = null;
	public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}
	
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
        
        try {
        	InputStream is = getClass().getClassLoader().getResourceAsStream("images/arrow.png" );
        	arrow_image = new Image(is);
        	is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				
				display_screen_width = img_w * display_ratio;
				display_screen_height = img_h * display_ratio;
				
				gc.drawImage( display_image, 0, 0, img_w, img_h, 0, 0, display_screen_width, display_screen_height );
				
				if( rbClickTypeTap.isSelected()) {
					if( arrow_image != null ) {
						gc.drawImage( arrow_image, 
							(int)(( display_screen_width / DIV_UNIT ) * mouse_clicked_pos.x ) - 13, 
							(int)(( display_screen_height / DIV_UNIT ) * mouse_clicked_pos.y ) 
						);
					}
				} else {
					gc.setStroke(Color.RED);
					gc.setLineWidth(5);
					drawArrowLine( gc, 
						(int)(( display_screen_width / DIV_UNIT ) * mouse_pressed_pos.x ), 
						(int)(( display_screen_height / DIV_UNIT ) * mouse_pressed_pos.y ), 
						(int)(( display_screen_width / DIV_UNIT ) * mouse_released_pos.x ), 
						(int)(( display_screen_height / DIV_UNIT ) * mouse_released_pos.y ), 5, 5 );
				}
			} else {			
		    	gc.setStroke(Color.RED);
		    	gc.strokeLine(0, 0, width, height);
		    	gc.strokeLine(0, height, width, 0);
			}
		}		
	};
	
	/**
     * Draw an arrow line betwwen two point 
     * @param g the graphic component
     * @param x1 x-position of first point
     * @param y1 y-position of first point
     * @param x2 x-position of second point
     * @param y2 y-position of second point
     * @param d  the width of the arrow
     * @param h  the height of the arrow
     */
    private void drawArrowLine(GraphicsContext g, int x1, int y1, int x2, int y2, int d, int h){
       int dx = x2 - x1, dy = y2 - y1;
       double D = Math.sqrt(dx*dx + dy*dy);
       double xm = D - d, xn = xm, ym = h, yn = -h, x;
       double sin = dy/D, cos = dx/D;

       x = xm*cos - ym*sin + x1;
       ym = xm*sin + ym*cos + y1;
       xm = x;

       x = xn*cos - yn*sin + x1;
       yn = xn*sin + yn*cos + y1;
       xn = x;

       double[] xpoints = {x2, xm, xn};
       double[] ypoints = {y2, ym, yn};

       g.strokeLine(x1, y1, x2, y2);
       g.strokePolygon(xpoints, ypoints, 3);
    }
	
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
		DeviceInfo device_info = mainViewController.getSelectedDeviceInfo();
		if( device_info == null ) return;
		
		if( e.getSource() == cvDisplay ) {
			if( display_image == null ) return;
			
			int percent_x = (int) ((e.getX() * DIV_UNIT ) / display_screen_width ); 
			if( percent_x > DIV_UNIT ) return;
			
			int percent_y = (int) ((e.getY() * DIV_UNIT) / display_screen_height );
			if( percent_y > DIV_UNIT ) return;
			
			Point mouse_device_pos = new Point( percent_x, percent_y );
			
			if( e.getEventType().equals( MouseEvent.MOUSE_CLICKED)) {
				if( System.currentTimeMillis() - mouse_dragged_time > 1500 ) {
					if( rbClickTypeTap.isSelected()) {
						mouse_clicked_pos = mouse_device_pos;
						
						device_info.touchScreen( 
							(int)(( device_info.getDisplayWidth()  / DIV_UNIT ) * mouse_clicked_pos.x ), 
							(int)(( device_info.getDisplayHeight() / DIV_UNIT ) * mouse_clicked_pos.y ) 
						);
						redraw_display_image();
					}
				}
				
			} else if( e.getEventType().equals( MouseEvent.MOUSE_PRESSED )) {
				mouse_pressed_time 	= System.currentTimeMillis();
				mouse_pressed_pos 	= mouse_device_pos;
				
				bMousePressed 	= true;
				bMouseDragged	= false;
								
			} else if( e.getEventType().equals( MouseEvent.MOUSE_RELEASED )) {
				mouse_released_time = System.currentTimeMillis();
				mouse_released_pos	= mouse_device_pos;
					
				if( !rbClickTypeTap.isSelected()) {
					if( bMousePressed && bMouseDragged ) {
						device_info.swipeScreen( 
							(int)(( device_info.getDisplayWidth()  / DIV_UNIT ) * mouse_pressed_pos.x ),  
							(int)(( device_info.getDisplayHeight() / DIV_UNIT ) * mouse_pressed_pos.y ), 
							(int)(( device_info.getDisplayWidth()  / DIV_UNIT ) * mouse_released_pos.x ), 
							(int)(( device_info.getDisplayHeight() / DIV_UNIT ) * mouse_released_pos.y ), 
							mouse_released_time - mouse_pressed_time  );
					}
				}
				bMousePressed = false;
				bMouseDragged = false;
				
			} else if( e.getEventType().equals( MouseEvent.MOUSE_DRAGGED )) {
				mouse_dragged_time = System.currentTimeMillis();
				if( !rbClickTypeTap.isSelected()) {
					mouse_released_pos	= mouse_device_pos;
					redraw_display_image();
				}
				bMouseDragged = true;
			}
		}
	} 
	
	@FXML
	private void handle( ActionEvent e) {
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
		
		case "ID_BTN_MODIFY_SCREEN_DATA"	: OnClickButtonModifyScreenData(); break;
		case "ID_BTN_MOVE_SCREEN_PREV"		: OnClickButtonMoveScreenDataPrev(); break;
		case "ID_BTN_MOVE_SCREEN_NEXT"		: OnClickButtonMoveScreenDataNext(); break;
		
		case "ID_BTN_SCREEN_STEP_ACTION"		: OnClickButtonStepActionScreenData(); break;
		case "ID_BTN_MOVE_FIRST_SCREEN_DATA"	: OnClickButtonMoveFirstScreenData(); break;
		case "btnScriptControl"					: OnClickButtonPlayScriptDatas(); break;
			
		case "ID_BTN_EXPORT_SCRIPT"				: OnClickButtonExportScriptDatas(); break;
		case "ID_BTN_IMPORT_SCRIPT"				: OnClickButtonImportScriptDatas(); break;
		
		default :
			System.out.println( ctrl_id + " : 아직 구현되지 않은 ID 입니다. " );
			break;
		}
	}
	
	/**
	 * 
	 */
	private void OnClickButtonImportScriptDatas() {
		File exported_file = DialogUtils.openFileDialog( "Export 된 파일을 선택하여 주세요", "EXPORT", "*.atm");
		if( exported_file == null ) return;
		
		File script_file = DialogUtils.openFileDialog( "저장할 스크립트파일을 선택하여 주세요.", "SCRIPT", "*.script");
		if( script_file == null ) return;
		
		try {
			ZipUtils.unzip( exported_file, script_file.getParentFile(), false );
			script_file.delete();
			FileUtils.moveFile( new File( script_file.getParentFile(), "Export.stript"), script_file);
			
			File newImageFolder = new File( script_file.getParentFile(), "Images");
			
			PropertyEx scriptInfo = new PropertyEx("");
			scriptInfo.load( script_file.getAbsolutePath());
			
			int count = Integer.valueOf( scriptInfo.getValue( "COUNT" ));
			for( int i = 0; i < count; i++ ) {
				File imageFile = new File( scriptInfo.getValue( String.format( "%03d_IMAGE", i )));
				File new_imagePath = new File( newImageFolder, imageFile.getName());
				scriptInfo.setValue( String.format( "%03d_IMAGE", i ), new_imagePath.getAbsolutePath());
			}
			scriptInfo.save( script_file.getAbsolutePath());
			
			load_script_datas( script_file );
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 현 스크립트 데이터들을 다른 툴에서도 사용 가능하도록 Export 합니다. 
	 */
	private void OnClickButtonExportScriptDatas() {
		// Export 할 파일을 선택 합니다. 
		File result = DialogUtils.saveFileDialog("내보낼 Export 파일을 선택하여 주세요.", "EXPORT", "*.atm");
		if( result == null ) return;
		
		File tmpFolder = new File( result.getParentFile(), ".out" );
		tmpFolder.mkdirs();
		
		File ImgFolder = new File( tmpFolder, "Images");
		ImgFolder.mkdirs();
		
		File scriptFile = new File( tmpFolder, "Export.stript");
		
		PropertyEx scriptInfo = new PropertyEx("");
		scriptInfo.setValue( "COUNT", String.valueOf( screenDatas.size()));
		scriptInfo.setValue( "NAME", lbScriptSubject.getText());
		scriptInfo.setValue( "IMAGE_PATH", ImgFolder.getAbsolutePath());
		
		int index = 0;
		for( ScreenData sData : screenDatas ) {
			File image = new File( ImgFolder, sData.image.getName());
			try {
				FileUtils.copyFile( sData.image, image );
			} catch (IOException e) {
				e.printStackTrace();
				
				DialogUtils.alert( "Error", "파일 복사에 실패 하였습니다. \n" + sData.image.getAbsolutePath(), AlertType.ERROR );
				try {
					FileUtils.deleteDirectory( tmpFolder );
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return;
			}
			scriptInfo.setValue( String.format( "%03d_IMAGE", index ), image.getAbsolutePath());			
			scriptInfo.setValue( String.format( "%03d_TYPE", index ), String.valueOf( sData.type ));
			
			// 좌표값은 이미지 크기의 % 값으로 저장합니다.
			scriptInfo.setValue( String.format( "%03d_X", index ), String.valueOf( sData.point.x ));
			scriptInfo.setValue( String.format( "%03d_Y", index ), String.valueOf( sData.point.y ));
			scriptInfo.setValue( String.format( "%03d_X2", index ), String.valueOf( sData.point2.x ));
			scriptInfo.setValue( String.format( "%03d_Y2", index ), String.valueOf( sData.point2.y ));
			scriptInfo.setValue( String.format( "%03d_DELAYTIME", index ), String.valueOf( sData.delayTime ));
			scriptInfo.setValue( String.format( "%03d_SWIPETIME", index ), String.valueOf( sData.swipeTime ));
			index++;
		}
		
		scriptInfo.save( scriptFile.getAbsolutePath());
		
		try {
			ZipUtils.zip( tmpFolder, result );
			FileUtils.deleteDirectory( tmpFolder );
			
			DialogUtils.alert( "Information", "스크립트가 Export 되었습니다.\n" + result.getAbsolutePath() + " 을 확인하세요.", AlertType.INFORMATION );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	

	/**
	 * 
	 */
	private void OnClickButtonPlayScriptDatas() {
		String name = btnScriptControl.getText();
		if( name.compareTo("Play") == 0 ) {
			List<DeviceInfo> device_infos = mainViewController.getCheckedDeviceInfo();
			if( device_infos == null ) {
				DialogUtils.alert( "Information", "디바이스가 선택되지 않았습니다. \n디바이스를 항목에서 장치를 체크해 주세요.", AlertType.WARNING );
				return;
			}
			
			btnScriptControl.setText( "Stop" );
			lbDelayTimeTitle.setText("남은시간");
			
			script_play_flag = true;
			new ScriptPlayThread( device_infos ).start();
						
		} else {
			btnScriptControl.setDisable( true );
			script_play_flag = false;
		}
		
	}

	private boolean script_play_flag = false;
	class ScriptPlayThread extends Thread implements Runnable {
		List<DeviceInfo> device_infos = null;
		
		public ScriptPlayThread( List<DeviceInfo> device_infos) {
			super();
			this.device_infos = device_infos;
		}

		@Override
		public void run() {
			nCurrentScreenIdx = 0;
			while( script_play_flag ) {
				try { Thread.sleep( 100 ); } catch (InterruptedException e) {}
				if( !script_play_flag ) break;
				
				ScreenData data = screenDatas.get( nCurrentScreenIdx );
				
				int delayTime = data.delayTime;
				while( delayTime > 0 ) {
					try {
						Thread.sleep( 100 );
						delayTime -= 100;
						
					} catch (InterruptedException e) {}
					if( !script_play_flag ) break;
					
					Platform.runLater( updateDelayTime );
				}
				
				try { Thread.sleep( 10 ); } catch (InterruptedException e) {}
				if( !script_play_flag ) break;
				
				// 장치가 선택되어 있다면 화면을 클릭 합니다. 
				if( data.type == ScreenData.TYPE_TAP ) {
					for( DeviceInfo device_info : device_infos ) {
						device_info.touchScreen( 
							(int)(( device_info.getDisplayWidth()  / DIV_UNIT ) * mouse_clicked_pos.x ), 
							(int)(( device_info.getDisplayHeight() / DIV_UNIT ) * mouse_clicked_pos.y ) 
						);
					}
					
				} else if( data.type == ScreenData.TYPE_SWIPE ) {
					for( DeviceInfo device_info : device_infos ) {
						device_info.swipeScreen( 
							(int)(( device_info.getDisplayWidth()  / DIV_UNIT ) * mouse_pressed_pos.x ),  
							(int)(( device_info.getDisplayHeight() / DIV_UNIT ) * mouse_pressed_pos.y ), 
							(int)(( device_info.getDisplayWidth()  / DIV_UNIT ) * mouse_released_pos.x ), 
							(int)(( device_info.getDisplayHeight() / DIV_UNIT ) * mouse_released_pos.y ), 
							mouse_released_time - mouse_pressed_time
						);
					}
				}
				
				try { Thread.sleep( 10 ); } catch (InterruptedException e) {}
				if( !script_play_flag ) break;
				
				for( DeviceInfo device_info : device_infos ) {
					device_info.getDeviceOrientation();	// display on /off 여부 확인
					
					// 베터리 유지 기능
					if( device_info.getBatteryLevel() < 15 && device_info.getDisplayOn()) {						
						device_info.Command("input keyevent KEYCODE_POWER" );	// display off
					} else if( device_info.getBatteryLevel() > 30 && !device_info.getDisplayOn()) {
						device_info.Command("input keyevent KEYCODE_POWER" );	// display on
					}
				}
				
				Platform.runLater( updateScreenInfo );				
				
				try { Thread.sleep( 10 ); } catch (InterruptedException e) {}
				if( !script_play_flag ) break;
			}
			
			Platform.runLater( new Runnable(){
				@Override
				public void run() {
					btnScriptControl.setText( "Play" );
					btnScriptControl.setDisable( false );
					lbDelayTimeTitle.setText("지연시간");
					OnClickButtonMoveFirstScreenData();
				}});
		}
		
		Runnable updateScreenInfo = new Runnable() {
			@Override
			public void run() {
				if( nCurrentScreenIdx + 1 >= screenDatas.size()) {
					nCurrentScreenIdx = 0;
					
					//deviceListViewController.instance.updateDeviceInfoList();
				} else {
					nCurrentScreenIdx++;
				}

				reload_screen_data();
			}
		};
		
		Runnable updateDelayTime = new Runnable() {
			@Override
			public void run() {
				int delayTime = Integer.valueOf( tfDelayTime.getText() );
				delayTime -= 100;
				tfDelayTime.setText( String.valueOf( delayTime ));
			}
		};
	}
	
	/**
	 * 현재 화면 데이터를 ScreenData의 가장 처음으로 이동 시킵니다. 
	 */
	private void OnClickButtonMoveFirstScreenData() {
		nCurrentScreenIdx = 0;
		reload_screen_data();
	}

	/**
	 * 현재 화면 데이터의 Touch Action 을 수행하고 다음 ScreenData로 이동합니다.
	 */
	private void OnClickButtonStepActionScreenData() {
		DeviceInfo device_info = mainViewController.getSelectedDeviceInfo();
		if( device_info == null ) {
			DialogUtils.alert( "Information", "디바이스가 선택되지 않았습니다. \n디바이스를 선택 후 다시 시도해 주세요.", AlertType.WARNING );
			return;
		}

		ScreenData data = screenDatas.get( nCurrentScreenIdx );
		
		// 장치가 선택되어 있다면 화면을 클릭 합니다. 
		if( data.type == ScreenData.TYPE_TAP ) {
			device_info.touchScreen( data.point.x, data.point.y );
		} else if( data.type == ScreenData.TYPE_SWIPE ) {
			device_info.swipeScreen( data.point.x, data.point.y, data.point2.x, data.point2.y, data.swipeTime );
		}
		
		OnClickButtonMoveScreenDataNext();
	}

	private void OnClickButtonMoveScreenDataPrev() { moveScreenData( false ); }
	private void OnClickButtonMoveScreenDataNext() { moveScreenData( true ); }
	private boolean moveScreenData(boolean bNext ) {
		if( bNext ) {
			if( nCurrentScreenIdx + 1 >= screenDatas.size()) return false;
			nCurrentScreenIdx++;
			
		} else {
			if( nCurrentScreenIdx < 1 ) return false;
			nCurrentScreenIdx--;
		}
		
		reload_screen_data();		
		return true;
	}
	
	private void reload_screen_data() {
		ScreenData data = screenDatas.get( nCurrentScreenIdx );
		System.out.println( "nCurrentScreenIdx : " + nCurrentScreenIdx );
		data.print();
		
		switch( data.type ) {
		case ScreenData.TYPE_TAP 	: mouse_clicked_pos	= data.point; break;
		case ScreenData.TYPE_SWIPE 	: mouse_pressed_pos = data.point;
			mouse_released_pos = data.point2;
			break;
		}
		
		loaded_image_file	= data.image;
		
		tfDelayTime.setText( String.valueOf( data.delayTime ) );
		rbClickTypeTap.setSelected( data.type == ScreenData.TYPE_TAP );
		try {
			BufferedImage img = ImageIO.read( loaded_image_file );
			if( img != null ) {
				display_image = SwingFXUtils.toFXImage( img, null );
				redraw_display_image();
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		update_screen_page_info();
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
		update_screen_page_info();
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
		update_screen_page_info();
	}

	/**
	 * 현재 화면에 보여지는 데이터를 ScreenData 객체로 반환 합니다.
	 * 
	 * @return
	 */
	private ScreenData getCurrentScreenData() {
		ScreenData data = new ScreenData();
		data.point		= rbClickTypeTap.isSelected() ? mouse_clicked_pos : mouse_pressed_pos;
		data.point2		= mouse_released_pos;
		data.swipeTime	= mouse_released_time - mouse_pressed_time;
		data.type		= rbClickTypeTap.isSelected() ? ScreenData.TYPE_TAP : ScreenData.TYPE_SWIPE;		
		data.image		= loaded_image_file;
		data.delayTime = Integer.valueOf( tfDelayTime.getText());
		return data;
	}

	/**
	 * 현재 화면의 ScreenData을 화면의 내용으로 갱신합니다. 
	 */
	private void OnClickButtonModifyScreenData() {
		ScreenData data = screenDatas.get( nCurrentScreenIdx );
		data.point		= rbClickTypeTap.isSelected() ? mouse_clicked_pos : mouse_pressed_pos;
		data.point2		= mouse_released_pos;
		data.swipeTime	= mouse_released_time - mouse_pressed_time;
		data.type		= rbClickTypeTap.isSelected() ? ScreenData.TYPE_TAP : ScreenData.TYPE_SWIPE;		
		data.image		= loaded_image_file;
		data.delayTime = Integer.valueOf( tfDelayTime.getText());
		
		screenDatas.set( nCurrentScreenIdx, data );
	}
	
	/**
	 * 현재 화면의 데이터를 Script 에서 제거 합니다. 
	 */
	private void OnClickButtonDeleteScreenData() {
		screenDatas.remove( nCurrentScreenIdx );
		if( nCurrentScreenIdx > 0 ) {
			if( nCurrentScreenIdx >= screenDatas.size()) nCurrentScreenIdx -= 1;
		}
		reload_screen_data();
	}

	/**
	 * 이미 작성된 스크립트 파일을 로딩합니다. 
	 */
	private void OnClickButtonScriptLoad() {
		File result = DialogUtils.openFileDialog( "불러올 스크립트파일을 선택하여 주세요.", "SCRIPT", "*.script" );
		if( result == null ) return;
	
		load_script_datas( result );
	}

	private void load_script_datas(File result) {
		PropertyEx scriptInfo = new PropertyEx("");
		try {
			scriptInfo.load( result.getAbsolutePath() );
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		screenDatas.clear();
		
		lbScriptSubject.setText( scriptInfo.getValue( "NAME" ) );
		
		int count = Integer.valueOf( scriptInfo.getValue( "COUNT" ));
		for( int i = 0; i < count; i++ ) {
			ScreenData sData = new ScreenData();
			sData.image 		= new File(scriptInfo.getValue( String.format( "%03d_IMAGE", i )));
			try {
				sData.type		= Integer.valueOf( scriptInfo.getValue( String.format( "%03d_TYPE", i ) ));
			} catch( Exception e ) { sData.type = ScreenData.TYPE_TAP; }
			
			// 좌표값은 이미지 크기의 % 값으로 저장하기 때문에 읽어 들일때 환산하여 실제 위치 값으로 변환 합니다.  
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
		
		nCurrentScreenIdx = 0;
		reload_screen_data();
		
	}

	/**
	 * 스크립트를 저장합니다. 
	 */
	private void OnClickButtonScriptSave() {
		File result = DialogUtils.saveFileDialog( "저장할 스크립트파일을 선택하여 주세요.", "SCRIPT", "*.script" );
		if( result == null ) return;
		
		PropertyEx scriptInfo = new PropertyEx("");
		scriptInfo.setValue( "COUNT", String.valueOf( screenDatas.size()));
		scriptInfo.setValue( "NAME", lbScriptSubject.getText());
		
		int index = 0;
		for( ScreenData sData : screenDatas ) {
			scriptInfo.setValue( String.format( "%03d_IMAGE", index ), sData.image.getAbsolutePath());			
			scriptInfo.setValue( String.format( "%03d_TYPE", index ), String.valueOf( sData.type ));
			
			// 좌표값은 이미지 크기의 % 값으로 저장합니다.
			scriptInfo.setValue( String.format( "%03d_X", index ), String.valueOf( sData.point.x ));
			scriptInfo.setValue( String.format( "%03d_Y", index ), String.valueOf( sData.point.y ));
			scriptInfo.setValue( String.format( "%03d_X2", index ), String.valueOf( sData.point2.x ));
			scriptInfo.setValue( String.format( "%03d_Y2", index ), String.valueOf( sData.point2.y ));
			scriptInfo.setValue( String.format( "%03d_DELAYTIME", index ), String.valueOf( sData.delayTime ));
			scriptInfo.setValue( String.format( "%03d_SWIPETIME", index ), String.valueOf( sData.swipeTime ));
			index++;
		}
		
		scriptInfo.save( result.getAbsolutePath());
	}


	/**
	 * 새로운 스크립트를 시작합니다. 
	 */
	private void OnClickButtonScriptNew() {
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("스크립트 생성하기");
		dialog.setHeaderText("생성할 스크립명을 입력해 주세요");
		dialog.setContentText("스크립트 명  : ");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			lbScriptSubject.setText( result.get());
			
			display_image = null;
			screenDatas.clear();
			
			redraw_display_image();
			update_screen_page_info();
		}
	}

	private void OnClickButtonImageLoad() {
		loaded_image_file = DialogUtils.openFileDialog("이미지 파일을 선택하여 주세요.","PNG", "*.png");
		if( loaded_image_file == null ) return;
		try {
			BufferedImage img = ImageIO.read( loaded_image_file );
			if( img != null ) {
				display_image = SwingFXUtils.toFXImage( img, null);
				
				redraw_display_image();
			}		
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}

	private void OnClickButtonImageSave() {
		File result = DialogUtils.saveFileDialog("이미지를 저장할 파일을 선택하여 주세요.","PNG", "*.png");
		if( result == null ) return;
		try {
			result.delete();
			ImageIO.write( SwingFXUtils.fromFXImage( display_image, null), "PNG", result );
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	/**
	 * 현재의 이미지를 오른쪽으로 90도 회전 시킴니다. 
	 */
	private void OnClickButtonImageRotateP90() {
		if( display_image == null ) return;
		
		display_rotate = display_rotate == 270.0f ? 0.0f : display_rotate + 90.0f;
		display_image = ImageUtils.rotate( display_image, 90.0f ); 
		redraw_display_image(); 		
	}

	/**
	 * 현재의 이미지를 왼쪽으로 90도 회전 시킴니다. 
	 */
	private void OnClickButtonImageRotateN90() {
		if( display_image == null ) return;
		
		display_rotate = display_rotate == 0.0f ? 270.0f : display_rotate - 90.0f;
		display_image = ImageUtils.rotate( display_image, -90.0f ); 
		redraw_display_image(); 
	}

	/**
	 * 장치 리스트에서 선택된 단말기의 화면을 캡쳐하여 보여 줍니다.  
	 */
	private void OnClickButtonCaptureScreen() {
		new Thread( new Runnable() {

			@Override
			public void run() {
				DeviceInfo device_info = mainViewController.getSelectedDeviceInfo();
				if( device_info == null ) return;
				
				File capture_folder = new File( MainClass.instance.GetTempPath(), "scree_capture" );
				capture_folder.mkdirs();
				
				File capture_file = new File( capture_folder, device_info.getSerialNumber() + ".png" );
				capture_file.delete();
				
				BufferedImage img = device_info.getScreenshot();
				
				if( img != null ) {
					display_image = SwingFXUtils.toFXImage( img, null);
					display_image = ImageUtils.rotate( display_image, display_rotate );
					
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
	private void update_screen_page_info() {
		int max_page = screenDatas.size();
		if( max_page == 0 ) {
			lbScreenPageInfo.setText( "0/0");
		} else {
			lbScreenPageInfo.setText( String.format( "%d/%d", nCurrentScreenIdx + 1, screenDatas.size()));
		}
	}
	
	/**
	 * 
	 */
	private void redraw_display_image() {
		cvDisplay.redraw();		
	}
}
