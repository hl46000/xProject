package android.touch.macro.v2.view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

import android.touch.macro.v2.DataManager;
import android.touch.macro.v2.PropertyV2;
import android.touch.macro.v2.TouchMacroV2;
import android.touch.macro.v2.UtilV2;
import android.touch.macro.v2.adb.AdbDevice;
import android.touch.macro.v2.adb.AdbV2;

public class mainController {
	
	@FXML
	private Canvas cvDisplay;
	
	@FXML
	private Label lbCaptureImageSize;
	
	@FXML
	private Label lbClickPosition;
	
	public int display_screen_width 		= -1;		// 이미지를 표시할 영역의 넓이
	public int display_screen_height 		= -1;		// 이미지를 표시할 영역의 높이
	
	public double display_ratio			= 1.0f;		// 이미지를 화면에 표시할때 확대/축소 비율
	public int display_angle				= 0;		// 화면의 획전 각도
	
	public boolean drawArrawImage			= false;	// 화면에 좌표 지정화살표를 표시 할지에 대한 Flag
	
	public Point ptArrayImageDevicePoint	= new Point();
	
	public BufferedImage 	captured_image	= null;
	
	public Image 		  	display_image	= null;
	public Image 			img_arrow 		= null;
	
	DataManager dataManager = null;
	
	@FXML
    public void initialize() {
		dataManager = TouchMacroV2.instance.getDataManager();
     
        display_screen_width 	= (int)cvDisplay.getWidth();
        display_screen_height 	= (int)cvDisplay.getHeight();
        
        System.out.println( String.format( "display_screen_width = %d, display_screen_height = %d", 
        		display_screen_width, display_screen_height ));
        
        InputStream is = getClass().getClassLoader().getResourceAsStream("arrow.png" );
        img_arrow = new Image(is);
        
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 이미지가 로딩되기 전까지 이미지 영역을 표시해 줌
		/*
		GraphicsContext gc = cvDisplay.getGraphicsContext2D();
		gc.setLineWidth(3);
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.BLUEVIOLET);
        
        gc.strokeRect( 0, 0, display_screen_width, display_screen_height);
        gc.strokeLine( 0, 0, display_screen_width, display_screen_height);
        gc.strokeLine( 0, display_screen_height, display_screen_width, 0);
        */
		
		dataManager.setMainController( this );
	}
		
	@FXML
	private void event_handle_action( ActionEvent e) {
		Object obj = e.getSource();
		
		if( obj instanceof Button) {
			Button btn = ( Button ) obj;
			
			switch( btn.getId() ) {
			case "ID_BUTTON_CAPTURE_SCREEN" 	 	: onClick_captureScreen(); break;
			case "ID_BTN_SAVE_SCREEN_POSITION"	 	: onClick_saveScreenPosition(); break;
			case "ID_BTN_LOAD_SCREEN_POSITION"	 	: onClick_loadScreenPosition(); break;
			}
			
		} else if( obj instanceof MenuItem ) {
			MenuItem mi = ( MenuItem ) obj;
			
			switch( mi.getId() ) {
			case "ID_MENU_ROTATE_P90" : onClickMenu_rotate_p90(); break;
			case "ID_MENU_ROTATE_N90" : onClickMenu_rotate_n90(); break;
			}
		}
	}
	
	
	

	

	/**
	 * Capture image rotate -90
	 */
	private void onClickMenu_rotate_n90() {
		display_angle = display_angle == 0 ? 360 - 90 : display_angle - 90;
		
		displayCaptureImage( captured_image );		
	}

	/**
	 * Capture image rotate +90
	 */
	private void onClickMenu_rotate_p90() {
		display_angle = display_angle == 270 ? 0 : display_angle + 90;
		
		displayCaptureImage( captured_image );
	}

	/**
	 * 마우스 이벤트를 전달받을 함수
	 * 
	 * @param e
	 */
	@FXML
	private void event_handle_mouse(MouseEvent e) {
		Object obj = e.getSource();
		
		if( obj instanceof Canvas ) {
			Canvas cv = ( Canvas ) obj;
			switch( cv.getId()) {
			case "cvDisplay" 		: mouse_handler_screen_position(e); break;
			}
		}
    }
	
	/**
	 * Screen position 이미지가 클릭되면 호출되는 함수
	 * 
	 * @param e
	 */
	private void mouse_handler_screen_position(MouseEvent e) {
		if( captured_image == null ) {
			return;
		}
		
		Point screenPoint = new Point( (int) e.getX(), (int) e.getY() );  
		Point devicePoint = screenPointToDevicePoint( screenPoint );
		
		lbClickPosition.setText( String.format( "X:%4d, Y:%4d", devicePoint.x, devicePoint.y ));
		
		if( 1 == e.getClickCount()) {
						
			//cmDisplayRotate.hide();
			if( e.getButton() == MouseButton.SECONDARY ) {
				// Context ment 을 보여 줍시다.
				//cmDisplayRotate.show( cvDisplay, e.getScreenX(), e.getScreenY());
				
			} else {
				drawArrawImage = true;
				
				ptArrayImageDevicePoint = devicePoint;
				//System.out.printf( "화면좌표:%s, 디바이스좌표:%s\n", screenPoint.toString(), devicePoint.toString());
				
				displayCaptureImage( captured_image );
				
				// 장치가 선택되어 있다면 화면을 클릭 합니다. 
				AdbDevice device = dataManager.getSelectedDeviceInfo();
				if( device != null ) {
					
					AdbV2.touchScreen( ptArrayImageDevicePoint.x, ptArrayImageDevicePoint.y, device);
				}
			}
		}
	}

	/**
	 * 앱의 Screen 에서 클릭한 좌표를 단말기의 Screen 좌표로 변경 시켜 반환 합니다. 
	 * 
	 * @param screenPoint
	 * @return
	 */
	private Point screenPointToDevicePoint(Point screenPoint) {
		//Point pt = UtilV2.getRatioedPoint( screenPoint, dataManager.display_ratio );
		Point pt = screenPoint;
		return UtilV2.getAngledPoint( pt, captured_image.getWidth(), captured_image.getHeight(), display_angle );
	}

	/**
	 * 단말기의 Screen 좌표값을 화면의 Screen 좌표값으로 변환하여 반환합니다. 
	 * 
	 * @param devicePoint
	 * @return
	 */
	private Point devicePointToScreenPoint(Point devicePoint) {
		Point pt = UtilV2.getUnratioedPoint( devicePoint, display_ratio );
		return UtilV2.getUnangledPoint( pt, (int)display_image.getWidth(), (int)display_image.getHeight(), display_angle );
	}
		
	

	

	/**
	 * 디바이스 화면 캡쳐 
	 */
	private void onClick_captureScreen() {
		AdbDevice device = dataManager.getSelectedDeviceInfo();
		if( device == null ) {
			System.err.println("디바이스를 선택 후 시도해 주세요");
			return;
		}
		
		BufferedImage bufferedImage = AdbV2.screenCapture(device);
		
		AdbV2.getDeviceOrientation(device);
		System.out.println("ORIENTATION : " + device.getOrientation() );
		
		lbCaptureImageSize.setText( String.format( "W:%4d, H:%4d( %s ) ", bufferedImage.getWidth(), bufferedImage.getHeight(), device.getOrientationText()));
		
		display_angle = device.getOrientation()*90;
		bufferedImage = UtilV2.rotate( bufferedImage, 360-device.getOrientation()*90 );
		captured_image = bufferedImage;
		
		displayCaptureImage( captured_image );
	}
	
	
	/**
	 * 입력된 bufferedImage 을 화면에 표시할 영역에 맞게 확대/축소 하여 표시해 준다. 
	 * 
	 * @param bufferedImage
	 */
	private void displayCaptureImage(BufferedImage bufferedImage) {
		try {
			BufferedImage rotatedBufferedImage = UtilV2.rotate( bufferedImage, display_angle );
			BufferedImage resizedBufferedImage = loadResizedImage( rotatedBufferedImage );
			if( resizedBufferedImage != null ) {
				display_image = SwingFXUtils.toFXImage( resizedBufferedImage, null);
				
				GraphicsContext gc = cvDisplay.getGraphicsContext2D();
				gc.clearRect(0, 0, cvDisplay.getWidth(), cvDisplay.getHeight());
				gc.drawImage( display_image, 0, 0 );
				
				if( drawArrawImage ) {
					Point displayPoint = devicePointToScreenPoint( ptArrayImageDevicePoint );
					gc.drawImage( img_arrow, displayPoint.x - 13, displayPoint.y );
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Image 를 표시할 영역에 마추어 확대/축소된 이미지 객체를 반환한다. 
	 * 
	 * @param bufferedImage
	 * @return
	 * @throws FileNotFoundException 
	 */
	private BufferedImage loadResizedImage(BufferedImage bufferedImage) throws FileNotFoundException {
		double x_ratio = display_screen_width / (double)bufferedImage.getWidth();
		double y_ratio = display_screen_height / (double)bufferedImage.getHeight();
		display_ratio = Math.min( x_ratio, y_ratio ); 
		
		bufferedImage = UtilV2.resizeImage( bufferedImage, (int)( bufferedImage.getWidth()*display_ratio), (int)(bufferedImage.getHeight()*display_ratio));
		return bufferedImage;
	}
	//Image image = SwingFXUtils.toFXImage(capture, null);
	
	/**
	 * 현재 작업중인 이미지와 좌표 정보를 저장합니다. 
	 */
	private void onClick_saveScreenPosition() {
		final String LAST_SAVE_FILE_PATH_KEY = "LAST_SAVE_FILE_PATH";
		
		PropertyV2 app_prop = TouchMacroV2.instance.load_app_property();
		String last_save_path = app_prop.getValue(LAST_SAVE_FILE_PATH_KEY);
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지와 좌표를 저장할 파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Position", "*.pos") );
		if( last_save_path != null ) {
			File file_last_save_path = new File( last_save_path );
			if( file_last_save_path.exists()) {
				fileChooser.setInitialDirectory( file_last_save_path );
			}
		}
		File result = fileChooser.showSaveDialog(TouchMacroV2.instance.getPrimaryStage());
		if( result == null ) return;
		
		try {
			ImageIO.write( captured_image, "PNG", new File( result.getParentFile(), result.getName().replaceAll( ".pos", ".png") ));
			
			PropertyV2 prop = new PropertyV2();
			prop.setValue( "POSITION_X", String.valueOf( ptArrayImageDevicePoint.x ));
			prop.setValue( "POSITION_Y", String.valueOf( ptArrayImageDevicePoint.y ));
			prop.setValue( "SCREEN_WIDTH", String.valueOf( captured_image.getWidth() ));
			prop.setValue( "SCREEN_HIGHT", String.valueOf( captured_image.getHeight() ));
			
			AdbDevice device = dataManager.getSelectedDeviceInfo();
			if( device != null ) {
				prop.setValue( "SCREEN_ORIENTATION", String.valueOf( device.getOrientation() ));
			}
			prop.setValue( "DISPLAY_ANGLE", String.valueOf( display_angle ));
			
			prop.save( result.getAbsolutePath(), "");
		
			app_prop.setValue(LAST_SAVE_FILE_PATH_KEY, result.getParentFile().getAbsolutePath());
			app_prop.save("TouchMacro v2.0");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 저장했던 이미지와 좌표 정보를 불러 옵니다. 
	 */
	private void onClick_loadScreenPosition() {
		final String LAST_LOAD_FILE_PATH_KEY = "LAST_LOAD_FILE_PATH";
		
		PropertyV2 app_prop = TouchMacroV2.instance.load_app_property();
		String last_load_path = app_prop.getValue(LAST_LOAD_FILE_PATH_KEY);
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("저장한 이미지와 좌표 파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("Position", "*.pos") );
		if( last_load_path != null ) {
			File file_last_load_path = new File( last_load_path );
			if( file_last_load_path.exists()) {
				fileChooser.setInitialDirectory(file_last_load_path);
			}
		}
		
		File result = fileChooser.showOpenDialog(TouchMacroV2.instance.getPrimaryStage());
		
		if( result == null ) return;
		if( !result.exists()) return;
		
		try {
			BufferedImage bufferedImage = ImageIO.read( new File( result.getParentFile(), result.getName().replaceAll( ".pos", ".png") ) );
			
			lbCaptureImageSize.setText( String.format( "W:%4d, H:%4d", bufferedImage.getWidth(), bufferedImage.getHeight()));
			
			PropertyV2 prop = new PropertyV2();
			prop.load( result.getAbsolutePath() );
			
			ptArrayImageDevicePoint.x 	= Integer.valueOf( prop.getValue( "POSITION_X"));
			ptArrayImageDevicePoint.y 	= Integer.valueOf( prop.getValue( "POSITION_Y"));
			display_angle			  	= Integer.valueOf( prop.getValue( "DISPLAY_ANGLE"));
			
			AdbDevice device = dataManager.getSelectedDeviceInfo();
			if( device != null ) {
				device.setOrientation( Integer.valueOf( prop.getValue( "SCREEN_ORIENTATION")));
			}
			lbClickPosition.setText( String.format( "X:%4d, Y:%4d", ptArrayImageDevicePoint.x, ptArrayImageDevicePoint.y ));
			
			drawArrawImage = true;
			displayCaptureImage( bufferedImage );
			
			app_prop.setValue( LAST_LOAD_FILE_PATH_KEY, result.getParentFile().getAbsolutePath());
			app_prop.save("TouchMacro v2.0");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
