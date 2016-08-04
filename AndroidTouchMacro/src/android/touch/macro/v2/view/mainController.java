package android.touch.macro.v2.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.sun.javafx.css.Size;

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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class mainController {
	
	@FXML
	private TextField txAdbPath;
	
	@FXML
	private TableView<AdbDevice> tvDeviceInfo;
	
	@FXML
	private ImageView ivDisplay;
	
	@FXML
	private Label lbCaptureImageSize;
	
	@FXML
	private Label lbClickPosition;
	
	@FXML
	private ContextMenu cmDisplayRotate;
	
	int	display_screen_width 		= -1;		// 이미지를 표시할 영역의 넓이
	int display_screen_height 		= -1;		// 이미지를 표시할 영역의 높이
	BufferedImage captured_image	= null;
		
	@FXML
    public void initialize() {
        if( !initializeAdbPath()) return;
        if( !initializeDeviceInfo()) return;
     
        display_screen_width 	= (int)ivDisplay.getFitWidth();
        display_screen_height 	= (int)ivDisplay.getFitHeight();
        
        System.out.println( String.format( "display_screen_width = %d, display_screen_height = %d", display_screen_width, display_screen_height ));
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
		BufferedImage bi = UtilV2.rotate( captured_image, -90 );
		displayCaptureImage( bi );		
	}

	/**
	 * Capture image rotate +90
	 */
	private void handler_menu_rotate_p90() {
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
		
		if( obj instanceof ImageView ) {
			ImageView iv = ( ImageView ) obj;
			switch( iv.getId()) {
			case "ivDisplay" : handler_screen_position_mouse(e); break;
			}
		}
    }
	
	
	/**
	 * Screen position 이미지가 클릭되면 호출되는 함수
	 * 
	 * @param e
	 */
	private void handler_screen_position_mouse(MouseEvent e) {
		if( 1 == e.getClickCount()) {
			lbClickPosition.setText( String.format( "X:%d, Y:%d", (int)e.getX(), (int)e.getY()));
			
			cmDisplayRotate.hide();
			if( e.getButton() == MouseButton.SECONDARY ) {
				// Context ment 을 보여 줍시다.
				cmDisplayRotate.show( ivDisplay, e.getScreenX(), e.getScreenY());
			}
		}
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
		displayCaptureImage( bufferedImage );
	}
	
	
	private void displayCaptureImage(BufferedImage bufferedImage) {
		try {
			Image image = loadResizedImage( bufferedImage );
			if( image != null ) {
				ivDisplay.setImage(image);
				lbCaptureImageSize.setText( String.format( "X:%d, Y:%d", (int)image.getWidth(), (int)image.getHeight()));
				
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
	private Image loadResizedImage(BufferedImage bufferedImage) throws FileNotFoundException {
		double x_ratio = display_screen_width / (double)bufferedImage.getWidth();
		double y_ratio = display_screen_height / (double)bufferedImage.getHeight();
		double ratio = Math.min( x_ratio, y_ratio ); 
		
		bufferedImage = UtilV2.resizeImage( bufferedImage, (int)( bufferedImage.getWidth()*ratio), (int)(bufferedImage.getHeight()*ratio));
		Image image = SwingFXUtils.toFXImage( bufferedImage, null);
		return image;
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
