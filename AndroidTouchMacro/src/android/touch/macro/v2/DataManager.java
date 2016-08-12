package android.touch.macro.v2;

import java.awt.Point;
import java.awt.image.BufferedImage;

import android.touch.macro.v2.adb.AdbDevice;
import android.touch.macro.v2.view.deviceController;
import javafx.scene.image.Image;

public class DataManager {
	public int	display_screen_width 		= -1;		// 이미지를 표시할 영역의 넓이
	public int display_screen_height 		= -1;		// 이미지를 표시할 영역의 높이
	
	public double display_ratio			= 1.0f;		// 이미지를 화면에 표시할때 확대/축소 비율
	public int display_angle				= 0;		// 화면의 획전 각도
	
	public boolean drawArrawImage			= false;	// 화면에 좌표 지정화살표를 표시 할지에 대한 Flag
	
	public Point ptArrayImageDevicePoint	= new Point();
	
	public BufferedImage 	captured_image	= null;
	
	public Image 		  	display_image	= null;
	public Image 			img_arrow 		= null;
	
	
	private deviceController	device_controller = null;
	public void setDeviceController( deviceController controller ) { device_controller = controller; }
	
	/**
	 * 디바이스 정보창에 현재 선택된 디바이스의 객체를 반환 합니다. 선택된 디바이스가 없으면 null 반환
	 * 
	 * @return
	 */
	public AdbDevice getSelectedDeviceInfo() {
		return device_controller.getSelectedDeviceItem();		
	} 
}
