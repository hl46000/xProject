package android.touch.macro.v2.view;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.touch.macro.v2.PropertyV2;
import android.touch.macro.v2.TouchMacroV2;
import android.touch.macro.v2.UtilV2;
import android.touch.macro.v2.adb.AdbDevice;
import android.touch.macro.v2.adb.AdbV2;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class mainController {
	
	@FXML
	private TextField txAdbPath;
	
	@FXML
	private TableView<AdbDevice> tvDeviceInfo;
	
	@FXML
	private Canvas cvDisplay;
	
	@FXML
	private Label lbCaptureImageSize;
	
	@FXML
	private Label lbClickPosition;
	
	@FXML
	private ContextMenu cmDisplayRotate;
	
	int	display_screen_width 		= -1;		// 이미지를 표시할 영역의 넓이
	int display_screen_height 		= -1;		// 이미지를 표시할 영역의 높이
	double display_ratio			= 1.0f;		// 이미지를 화면에 표시할때 확대/축소 비율
	int display_angle				= 0;		// 화면의 획전 각도
	boolean drawArrawImage			= false;	// 화면에 좌표 지정화살표를 표시 할지에 대한 Flag
	Point ptArrayImageDevicePoint	= new Point();
	BufferedImage 	captured_image	= null;
	Image 		  	display_image	= null;
	Image 			img_arrow 		= null;
	
	@FXML
    public void initialize() {
		if( !initializeAdbPath()) return;
        if( !initializeDeviceInfo()) return;
     
        display_screen_width 	= (int)cvDisplay.getWidth();
        display_screen_height 	= (int)cvDisplay.getHeight();
        
        System.out.println( String.format( "display_screen_width = %d, display_screen_height = %d", display_screen_width, display_screen_height ));
        
        InputStream is = getClass().getClassLoader().getResourceAsStream("arrow.png" );
		img_arrow = new Image(is);
		try {
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// 이미지가 로딩되기 전까지 이미지 영역을 표시해 줌
		GraphicsContext gc = cvDisplay.getGraphicsContext2D();
		gc.setLineWidth(3);
		gc.setFill(Color.GREEN);
		gc.setStroke(Color.BLUEVIOLET);
        
        gc.strokeRect( 0, 0, display_screen_width, display_screen_height);
        gc.strokeLine( 0, 0, display_screen_width, display_screen_height);
        gc.strokeLine( 0, display_screen_height, display_screen_width, 0);
	}
	
	/**
	 * 디바이스 정보를 초기화 시켜주는 함수
	 * 
	 * @return
	 */
	private boolean initializeDeviceInfo() {
		handler_refreshDeviceInfo();
		tvDeviceInfo.getSelectionModel().selectedItemProperty().addListener( DeviceInfoSelectChangeListener );
		return true;
	}
	
	@FXML
	private void eventHandleAction( ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj instanceof CheckBox) {
			CheckBox cb = (CheckBox) obj;
			
			switch( cb.getId() ) {
			case "ID_CHECKBOX_SELECT_ALL_DEVICE" : handler_selectAllDevice( cb.isSelected() ); break;
			}
			
		} else if( obj instanceof Button) {
			Button btn = ( Button ) obj;
			
			switch( btn.getId() ) {
			case "ID_BUTTON_REFRESH_DEVICE_INFO" : handler_refreshDeviceInfo(); break;
			case "ID_BUTTON_CAPTURE_SCREEN" 	 : handler_captureScreen(); break;
			}
			
		} else if( obj instanceof MenuItem ) {
			MenuItem mi = ( MenuItem ) obj;
			
			switch( mi.getId() ) {
			case "ID_MENU_ROTATE_P90" : handler_menu_rotate_p90(); break;
			case "ID_MENU_ROTATE_N90" : handler_menu_rotate_n90(); break;
			}
		}
	}
	
	
	/**
	 * Capture image rotate -90
	 */
	private void handler_menu_rotate_n90() {
		display_angle = display_angle == 0 ? 360 - 90 : display_angle - 90;
		
		BufferedImage bi = UtilV2.rotate( captured_image, -90 );
		displayCaptureImage( bi );		
	}

	/**
	 * Capture image rotate +90
	 */
	private void handler_menu_rotate_p90() {
		display_angle = display_angle == 270 ? 0 : display_angle + 90;
		
		BufferedImage bi = UtilV2.rotate( captured_image, 90 );
		displayCaptureImage( bi );
	}

	/**
	 * 마우스 이벤트를 전달받을 함수
	 * 
	 * @param e
	 */
	@FXML
	private void eventHandleMouse(MouseEvent e) {
		Object obj = e.getSource();
		
		if( obj instanceof Canvas ) {
			Canvas cv = ( Canvas ) obj;
			switch( cv.getId()) {
			case "cvDisplay" : handler_screen_position_mouse(e); break;
			}
		}
    }
	
	
	/**
	 * Screen position 이미지가 클릭되면 호출되는 함수
	 * 
	 * @param e
	 */
	private void handler_screen_position_mouse(MouseEvent e) {
		if( captured_image == null ) {
			return;
		}
		
		Point screenPoint = new Point( (int) e.getX(), (int) e.getY() );  
		Point devicePoint = screenPointToDevicePoint( screenPoint );
		
		lbClickPosition.setText( String.format( "X:%d, Y:%d", devicePoint.x, devicePoint.y ));
		
		if( 1 == e.getClickCount()) {
						
			cmDisplayRotate.hide();
			if( e.getButton() == MouseButton.SECONDARY ) {
				// Context ment 을 보여 줍시다.
				cmDisplayRotate.show( cvDisplay, e.getScreenX(), e.getScreenY());
				
			} else {
				drawArrawImage = true;
				
				ptArrayImageDevicePoint = devicePoint;
				//System.out.printf( "화면좌표:%s, 디바이스좌표:%s\n", screenPoint.toString(), devicePoint.toString());
				
				displayCaptureImage( captured_image );
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
		Point pt = getRatioedPoint( screenPoint );
		return getAngledPoint( pt, captured_image.getWidth(), captured_image.getHeight(), display_angle );
	}

	/**
	 * 단말기의 Screen 좌표값을 화면의 Screen 좌표값으로 변환하여 반환합니다. 
	 * 
	 * @param devicePoint
	 * @return
	 */
	private Point devicePointToScreenPoint(Point devicePoint) {
		Point pt = getUnratioedPoint( devicePoint );
		return getUnangledPoint( pt, (int)display_image.getWidth(), (int)display_image.getHeight(), display_angle );
	}
	
	

	/**
	 * 디바이스 좌표를 입력받아 화면 좌표값의 비율료 조정된 값을 반환합니다. 
	 * 
	 * @param pt
	 * @return
	 */
	private Point getUnratioedPoint(Point pt) {
		return new Point((int)(pt.x*display_ratio), (int)(pt.y*display_ratio));		
	}

	/**
	 * 이미지의 angle 이 적용된 좌표로 변환하여 반환 합니다. 
	 * 
	 * @param pt
	 * @return
	 */
	protected Point getAngledPoint( Point pt, int width, int height, int angle) {
		Point ret = new Point( pt );
		
		if( angle == 90 ) {		
			ret.x = pt.y; ret.y = width - pt.x;
		} else if( angle == 180 ) {
			ret.x = width - pt.x; ret.y = height - pt.y;
		} else if( angle == 270 ) {
			ret.x = height - pt.y; ret.y = pt.x;
		}
		
		return ret;
	}
	
	private Point getUnangledPoint(Point pt, int width, int height, int angle) {
		Point ret = new Point( pt );
		
		if( angle == 90 ) {		
			ret.y = pt.x; ret.x = width - pt.y; 
		} else if( angle == 180 ) {
			ret.x = width - pt.x; ret.y = height - pt.y;
		} else if( angle == 270 ) {
			ret.x = pt.y; ret.y = height - pt.x;
		}
		
		return ret;
	}

	/**
	 * 입력된 좌표값을 화면좌표값을 화면에 적용된 확대/축소값을 적용하여 원래의 X,Y 값을 반환 합니다. <br>즉, 화면좌표을 입력 받아 디바이스의 좌표로 변경시켜반환합니다. 
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private Point getRatioedPoint(Point pt) {
		return new Point((int)(pt.x/display_ratio), (int)(pt.y/display_ratio));
	}

	/**
	 * 디바이스 화면 캡쳐 
	 */
	private void handler_captureScreen() {
		AdbDevice device = getSelectedDeviceInfo();
		if( device == null ) {
			System.err.println("디바이스를 선택 후 시도해 주세요");
			return;
		}
		
		BufferedImage bufferedImage = AdbV2.screenCapture(device);
		lbCaptureImageSize.setText( String.format( "W:%d, H:%d", bufferedImage.getWidth(), bufferedImage.getHeight()));
		
		BufferedImage bi = UtilV2.rotate( bufferedImage, display_angle );
		displayCaptureImage( bi );
	}
	
	
	/**
	 * 입력된 bufferedImage 을 화면에 표시할 영역에 맞게 확대/축소 하여 표시해 준다. 
	 * 
	 * @param bufferedImage
	 */
	private void displayCaptureImage(BufferedImage bufferedImage) {
		try {
			BufferedImage resizedBufferedImage = loadResizedImage( bufferedImage );
			if( resizedBufferedImage != null ) {
				display_image = SwingFXUtils.toFXImage( resizedBufferedImage, null);
				
				GraphicsContext gc = cvDisplay.getGraphicsContext2D();
				gc.clearRect(0, 0, cvDisplay.getWidth(), cvDisplay.getHeight());
				gc.drawImage( display_image, 0, 0 );
				
				if( drawArrawImage ) {
					Point displayPoint = devicePointToScreenPoint( ptArrayImageDevicePoint );
					//System.out.printf( "디바이스좌표:%s, 화면좌표:%s\n", ptArrayImageDevicePoint.toString(), displayPoint.toString());
					gc.drawImage( img_arrow, displayPoint.x - 13, displayPoint.y );
				}
				
				captured_image = bufferedImage;
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
	 * 디바이스 정보창에 현재 선택된 디바이스의 객체를 반환 합니다. 선택된 디바이스가 없으면 null 반환
	 * 
	 * @return
	 */
	private AdbDevice getSelectedDeviceInfo() {
		return tvDeviceInfo.getSelectionModel().getSelectedItem();		
	}


	/**
	 * DeviceInfo 정보를 선택했을 때 호출되는 리스터
	 */
	ChangeListener<AdbDevice> DeviceInfoSelectChangeListener = new ChangeListener<AdbDevice>() {
		@Override
		public void changed(ObservableValue<? extends AdbDevice> observableValue, AdbDevice old_value, AdbDevice new_value ) {
			new_value.print();
		}
	};
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void handler_refreshDeviceInfo() {
		AdbV2.debugLog = true;
		ArrayList<AdbDevice> devices = AdbV2.getDevices();
		
		TableColumn<AdbDevice, Boolean> tcCheckBox	= (TableColumn<AdbDevice, Boolean>) tvDeviceInfo.getColumns().get(0);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<AdbDevice, Boolean>("selected"));
		tcCheckBox.setCellFactory( new Callback<TableColumn<AdbDevice, Boolean>, TableCell<AdbDevice, Boolean>>() {
            public TableCell<AdbDevice, Boolean> call(TableColumn<AdbDevice, Boolean> p) {
                return new CheckBoxTableCell<AdbDevice, Boolean>();
            }
        });
		
		TableColumn<AdbDevice, String> tcModelName 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(1);
		tcModelName.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("model"));
		
		TableColumn<AdbDevice, String> tcSerial 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(2);
		tcSerial.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("serialNumber"));
		
		TableColumn<AdbDevice, String> tcOsVersion 	= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(3);
		tcOsVersion.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("os_ver"));
		
		TableColumn<AdbDevice, String> tcStatus		= (TableColumn<AdbDevice, String>) tvDeviceInfo.getColumns().get(4);
		tcStatus.setCellValueFactory( new PropertyValueFactory<AdbDevice, String>("status"));
		
		ObservableList<AdbDevice> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
	}


	/**
	 * @param b
	 */
	private void handler_selectAllDevice(boolean b) {
		ObservableList<AdbDevice> deviceInfoData = tvDeviceInfo.getItems();
		for( AdbDevice deviceInfo : deviceInfoData ) {
			deviceInfo.setSelected( b );
		}
		tvDeviceInfo.refresh();
	}


	/**
	 * 
	 */
	private boolean initializeAdbPath() {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		File adb_file = new File( prop.getValue("ADB_PATH"));
		
		txAdbPath.setText( adb_file.getAbsolutePath() );
		
		if( !AdbV2.setAdbPath( adb_file.getAbsolutePath() )) {
			System.err.println( String.format( "\t'%s' 파일을 확인 후 다시 시도해 주세요.", prop.getPropertyFilePath()));
			
			return false;
		}
		
		return true;
	}

	@FXML
	private void onChangeAdbPath() {
		PropertyV2 prop = TouchMacroV2.instance.load_app_property();
		File adb_file = new File( prop.getValue("ADB_PATH"));
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("ADB 파일을 선택해 주세요");
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("ADB", "adb.exe") );
		fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("ALL Files", "*.*") );
		fileChooser.setInitialDirectory( adb_file.getParentFile() );
		fileChooser.setInitialFileName( adb_file.getName());
		
		File result = fileChooser.showOpenDialog( TouchMacroV2.instance.getPrimaryStage());
		if( result.exists()) {
			String name = result.getName().toLowerCase(); 
			if( name.compareTo("adb.exe") == 0 || name.startsWith("adb")) { 
				
				prop.setValue( "ADB_PATH", result.getAbsolutePath() );
				try {
					prop.save("TouchMacro v2");
					txAdbPath.setText( result.getAbsolutePath() );
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
