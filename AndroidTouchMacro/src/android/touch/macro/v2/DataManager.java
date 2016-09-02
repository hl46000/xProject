package android.touch.macro.v2;

import java.util.List;

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
	
	public void updateDeviceInfoList() {
		device_controller.updateDeviceInfoList();
	}
	
	/**
	 * @return
	 */
	public List<AdbDevice> getCheckedDeviceInfo() {
		return device_controller.getCheckedDeviceInfo();
	}
}
