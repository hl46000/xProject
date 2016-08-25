package android.touch.macro.v2;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.List;

import javafx.scene.image.Image;
import android.touch.macro.v2.adb.AdbDevice;
import android.touch.macro.v2.view.deviceController;
import android.touch.macro.v2.view.mainController;

public class DataManager {	
	private deviceController	device_controller 	= null;
	private mainController 		mainController		= null;
	
	public void setDeviceController( deviceController controller ) { device_controller = controller; }
	public void setMainController( mainController controller ) { mainController = controller; }
	
	/**
	 * 디바이스 정보창에 현재 선택된 디바이스의 객체를 반환 합니다. 선택된 디바이스가 없으면 null 반환
	 * 
	 * @return
	 */
	public AdbDevice getSelectedDeviceInfo() {
		return device_controller.getSelectedDeviceItem();		
	}
	
	/**
	 * @return
	 */
	public List<AdbDevice> getCheckedDeviceInfo() {
		return device_controller.getCheckedDeviceInfo();
	}
}
