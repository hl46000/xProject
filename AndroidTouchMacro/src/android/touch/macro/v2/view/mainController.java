package android.touch.macro.v2.view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert.AlertType;
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
import android.touch.macro.v2.adb.DeviceClickData;

public class mainController {
	
	@FXML
	private Canvas cvDisplay;
	
	@FXML
	private Label lbCaptureImageSize;					// 이미지 크기 표시 label
	
	@FXML
	private Label lbClickPosition;						// 클릭 좌표 표시 lable
	
	@FXML
	private Label lbScriptSubject;						// script 제목 표시 label
	
	@FXML
	private Label lbScreenPageInfo;						// 화면 Page 정보 표시 Lable
	
	@FXML
	private Button btnAddScreenDataPrev;				// 현재 화면 이전에 화면 추가 버튼
	@FXML
	private Button btnAddScreenDataNext;				// 현재 화면 다음에 화면 추가 버늩
	@FXML
	private Button btnDelScreenData;					// 현재 화면을 삭제하는 버튼
	@FXML
	private Button btnMovePrevScreenData;				// 이전 화면으로 이동 버튼 '<'
	@FXML
	private Button btnMoveNextScreenData;				// 다음 화면으로 이동 버튼 '>'
	@FXML
	private Button btnScriptControl;					// script 재생 제어 버튼
	
	private int display_screen_width 		= -1;		// 이미지를 표시할 영역의 넓이
	private int display_screen_height 		= -1;		// 이미지를 표시할 영역의 높이
	
	private double display_ratio			= 1.0f;		// 이미지를 화면에 표시할때 확대/축소 비율
	private int display_angle				= 0;		// 화면의 획전 각도
	
	private boolean drawArrawImage			= false;	// 화면에 좌표 지정화살표를 표시 할지에 대한 Flag
	
	private Point ptArrayImageDevicePoint	= new Point();
	
	private BufferedImage 	captured_image	= null;
	private File			captured_image_file = null;
	
	private Image 		  	display_image	= null;
	private Image 			img_arrow 		= null;
	
	private DataManager dataManager = null;
	private List<DeviceClickData> screenDatas = new ArrayList<DeviceClickData>(); 
	private int nCurrentMacroIdx = 0; 
	
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
			case "ID_BTN_CAPTURE_SCREEN" 		: onClick_captureScreen(); break;
			case "ID_BTN_SAVE_IMAGE"	 		: onClick_saveCurrentImage(); break;
			case "ID_BTN_LOAD_IMAGE"	 		: onClick_loadCurrentImage(); break;
			case "btnAddScreenDataPrev"			: onClick_addScreenPrev(); break;
			case "btnAddScreenDataNext"			: onClick_addScreenNext(); break;
			case "btnDelScreenData"				: onClick_delCurrentScreen(); break;
			case "btnMoveNextScreenData"		: onClick_moveScreen(true); break;
			case "btnMovePrevScreenData"		: onClick_moveScreen(false); break;
			case "btnScriptControl"				: break;
			}
			
		} else if( obj instanceof MenuItem ) {
			MenuItem mi = ( MenuItem ) obj;
			
			switch( mi.getId() ) {
			case "ID_MENU_ROTATE_P90" : onClickMenu_rotate_p90(); break;
			case "ID_MENU_ROTATE_N90" : onClickMenu_rotate_n90(); break;
			}
		}
	}
	
	
	private void onClick_moveScreen(boolean b) {
		if( b ) {
			btnMovePrevScreenData.setDisable( false );
			if( nCurrentMacroIdx + 1 >= screenDatas.size()) {
				btnMoveNextScreenData.setDisable( true );
				return;
			}
			nCurrentMacroIdx++;
		} else {
			btnMoveNextScreenData.setDisable( false );
			if( nCurrentMacroIdx < 1 ) {
				btnMovePrevScreenData.setDisable( true );
				return;
			}
			
			nCurrentMacroIdx--;
		}
		
		
		DeviceClickData data = screenDatas.get( nCurrentMacroIdx );
		
		display_angle = data.angle;
		ptArrayImageDevicePoint = data.point;
		captured_image_file = data.image;
		
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read( captured_image_file );
			captured_image = bufferedImage;
		} catch (IOException e) {
			e.printStackTrace();
		}
		displayCaptureImage( captured_image );
		
		updateCaptureImageSizeInfo();
		updateScreenPageInfo();
	}

	/**
	 * 현재 화면을 script 데이터에서 삭제 합니다. 
	 */
	private void onClick_delCurrentScreen() {
		screenDatas.remove( nCurrentMacroIdx );
		if( screenDatas.isEmpty()) {
			btnMovePrevScreenData.setDisable( true );
			btnMoveNextScreenData.setDisable( true );
			btnScriptControl.setDisable( true );
			
			return;
		} else if( nCurrentMacroIdx >= screenDatas.size() - 1 ) {
			nCurrentMacroIdx = screenDatas.size() - 1;
		}
		
		DeviceClickData data = screenDatas.get( nCurrentMacroIdx );
		
		display_angle = data.angle;
		ptArrayImageDevicePoint = data.point;
		captured_image_file = data.image;
		
		BufferedImage bufferedImage;
		try {
			bufferedImage = ImageIO.read( captured_image_file );
			captured_image = bufferedImage;
		} catch (IOException e) {
			e.printStackTrace();
		}
		displayCaptureImage( captured_image );
		
		updateCaptureImageSizeInfo();
		updateScreenPageInfo();
	}

	/**
	 * 
	 */
	private void updateScreenPageInfo() {
		lbScreenPageInfo.setText( String.format( "%d/%d", nCurrentMacroIdx + 1, screenDatas.size()));
	}

	/**
	 * 현재 표시되는 화면 인덱스 다음에 화면 데이터를 추가 합니다.  
	 */
	private void onClick_addScreenNext() {
		DeviceClickData data = new DeviceClickData();
		data.angle 	= this.display_angle;
		data.point	= ptArrayImageDevicePoint;
		data.image	= captured_image_file;
		if( screenDatas.isEmpty()) {
			screenDatas.add( data );
			nCurrentMacroIdx = 0;
		} else {
			screenDatas.add( nCurrentMacroIdx++, data );
		}
		
		updateScreenPageInfo();
	}

	/**
	 * 현재 표시되는 화면 인덱스 이전에 화면 데이터를 추가 합니다.
	 */
	private void onClick_addScreenPrev() {
		DeviceClickData data = new DeviceClickData();
		data.angle 	= this.display_angle;
		data.point	= ptArrayImageDevicePoint;
		data.image	= captured_image_file;
		if( nCurrentMacroIdx == 0 ) {
			screenDatas.add( 0, data );
			nCurrentMacroIdx = 0;
		} else {
			screenDatas.add( --nCurrentMacroIdx, data );
		}
		
		updateScreenPageInfo();
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
		
		ptArrayImageDevicePoint = new Point( (int) e.getX(), (int) e.getY() );  
		Point devicePoint = screenPointToDevicePoint( ptArrayImageDevicePoint );
		
		lbClickPosition.setText( String.format( "X:%4d, Y:%4d", devicePoint.x, devicePoint.y ));
		
		if( 1 == e.getClickCount()) {
						
			//cmDisplayRotate.hide();
			if( e.getButton() == MouseButton.SECONDARY ) {
				// Context ment 을 보여 줍시다.
				//cmDisplayRotate.show( cvDisplay, e.getScreenX(), e.getScreenY());
				
			} else {
				drawArrawImage = true;
				
				//System.out.printf( "화면좌표:%s, 디바이스좌표:%s\n", screenPoint.toString(), devicePoint.toString());
				
				displayCaptureImage( captured_image );
				
				// 장치가 선택되어 있다면 화면을 클릭 합니다. 
				AdbDevice device = dataManager.getSelectedDeviceInfo();
				if( device != null ) {
					
					AdbV2.touchScreen( devicePoint.x, devicePoint.y, device);
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
		Point pt = UtilV2.getRatioedPoint( screenPoint, display_ratio );
		//Point pt = screenPoint;
		//return UtilV2.getAngledPoint( pt, captured_image.getWidth(), captured_image.getHeight(), display_angle );
		return pt;
	}

	/**
	 * 디바이스 화면 캡쳐 
	 */
	private void onClick_captureScreen() {
		AdbDevice device = dataManager.getSelectedDeviceInfo();
		if( device == null ) {
			UtilV2.alertWindow( "Information", "디바이스가 선택되지 않았습니다. \n디바이스를 선택 후 다시 시도해 주세요.", AlertType.WARNING );
			return;
		}
		
		if( captured_image_file == null ) {
			String path = TouchMacroV2.instance.getCurrentPath();
			captured_image_file = new File( path, "screencap.png" );
		}
		captured_image_file.delete();
		
		BufferedImage bufferedImage = AdbV2.screenCapture(device, captured_image_file);
		
		AdbV2.getDeviceOrientation(device);
		System.out.println("ORIENTATION : " + device.getOrientation() );
		
		lbCaptureImageSize.setText( String.format( "W:%4d, H:%4d( %s ) ", bufferedImage.getWidth(), bufferedImage.getHeight(), device.getOrientationText()));
		
		display_angle = 360-device.getOrientation()*90;
		captured_image = bufferedImage;
		
		updateCaptureImageSizeInfo();
		displayCaptureImage( captured_image );
		
		btnAddScreenDataPrev.setDisable( true );
		btnAddScreenDataNext.setDisable( true );
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
					gc.drawImage( img_arrow, ptArrayImageDevicePoint.x - 13, ptArrayImageDevicePoint.y );
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
	private void onClick_saveCurrentImage() {
		final String LAST_SAVE_FILE_PATH_KEY = "LAST_FILE_PATH";
		
		PropertyV2 app_prop = TouchMacroV2.instance.load_app_property();
		String last_save_path = app_prop.getValue(LAST_SAVE_FILE_PATH_KEY);
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지를 저장할 파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("PNG", "*.png") );
		if( last_save_path != null ) {
			File file_last_save_path = new File( last_save_path );
			if( file_last_save_path.exists()) {
				fileChooser.setInitialDirectory( file_last_save_path );
			}
		}
		File result = fileChooser.showSaveDialog(TouchMacroV2.instance.getPrimaryStage());
		if( result == null ) return;
		
		try {
			result.delete();
			ImageIO.write( captured_image, "PNG", result );
									
			PropertyV2 info = new PropertyV2();
			info.setValue( "ANGLE", String.valueOf( display_angle ));
			
			String info_path = result.getAbsolutePath().replace(".png", ".inf");
			info.save( info_path, "");
			
			String last_load_path = result.getParentFile().getAbsolutePath();
			System.out.println( "LAST_FILE_PATH (save): " + last_load_path );
			
			app_prop.setValue(LAST_SAVE_FILE_PATH_KEY, last_load_path );
			app_prop.save("TouchMacro v2.0");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 저장했던 이미지와 좌표 정보를 불러 옵니다. 
	 */
	private void onClick_loadCurrentImage() {
		final String LAST_LOAD_FILE_PATH_KEY = "LAST_FILE_PATH";
		
		PropertyV2 app_prop = TouchMacroV2.instance.load_app_property();
		String last_load_path = app_prop.getValue(LAST_LOAD_FILE_PATH_KEY);
		System.out.println( "LAST_FILE_PATH (load): " + last_load_path );
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("이미지 파일을 선택하여 주세요.");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("PNG", "*.png") );
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
			captured_image_file = result;
			captured_image = ImageIO.read( result );
			
		} catch( IOException e ) {
			e.printStackTrace();
			return;
		}
		
		updateCaptureImageSizeInfo();		
		lbClickPosition.setText( String.format( "X:%4d, Y:%4d", 0, 0 ));
		
		String info_path = result.getAbsolutePath().replace(".png", ".inf");
		
		try {
			PropertyV2 info = new PropertyV2();
			info.load(info_path);
			String strAngle = info.getValue( "ANGLE" );
			if( strAngle != null ) {
				display_angle = Integer.valueOf( strAngle );
			}
		} catch( IOException e ) {
			e.printStackTrace();			
		}
			
		drawArrawImage = false;
		displayCaptureImage( captured_image );
		
		btnAddScreenDataPrev.setDisable( false );
		btnAddScreenDataNext.setDisable( false );
	}
	
	private void updateCaptureImageSizeInfo() {
		lbCaptureImageSize.setText( String.format( "W:%4d, H:%4d", captured_image.getWidth(), captured_image.getHeight()));
	}
}
