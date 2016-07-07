package my.java_excute.apps;

import javax.usb.UsbDevice;
import javax.usb.UsbHostManager;
import javax.usb.UsbServices;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

public class USBListernerTest implements UsbServicesListener {

	public static void main(String[] args) throws Exception {
		USBListernerTest test = new USBListernerTest();
		
		UsbServices services = UsbHostManager.getUsbServices( );
		services.addUsbServicesListener(test);
		
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void usbDeviceAttached(UsbServicesEvent event) {
		UsbDevice device = event.getUsbDevice();
	    System.out.println(getDeviceInfo(device) + " was added to the bus.");
	}

	@Override
	public void usbDeviceDetached(UsbServicesEvent event) {
		UsbDevice device = event.getUsbDevice();
	    System.out.println(getDeviceInfo(device) + " was removed from the bus.");
	}

	private String getDeviceInfo(UsbDevice device) {
	    try {
	      String product = device.getProductString();
	      String serial  = device.getSerialNumberString();
	      if (product == null) return "Unknown USB device";
	      if (serial != null) return product + " " + serial;
	      else return product;
	    }
	    catch (Exception ex) {
	    }
	    return "Unknown USB device";
	  }
}
