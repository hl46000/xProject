package com.purehero.android;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.IShellOutputReceiver;
import com.android.ddmlib.RawImage;
import com.purehero.common.io.ImageUtils;

public class DeviceInfo extends LogCat {
	public DeviceInfo( IDevice _device ) {
		super( _device );

		getDeviceDisplaySize();
		getDeviceOrientation();
	}
	
	public IDevice getInterface() { return device; }
		
	public String getModelName() 	{ 
		String modelName = device.getProperty( IDevice.PROP_DEVICE_MODEL ); 
		if( modelName != null ) return modelName.toUpperCase();
		return modelName; 
	}
	public String getSerialNumber() { return device.getSerialNumber(); }
	public String getOsVersion() 	{ return device.getProperty( IDevice.PROP_BUILD_VERSION ); }
	public String getState() 		{ return device.getState().name(); }
	public Integer getBatteryLevel() {
		try { 
			return device.getBattery().get(); 
		} catch (Exception e) {}
		return 0;
	}

	private boolean selected = false;
	public Boolean getSelected() { return selected; }
	public void setSelected(Boolean selected) { this.selected = selected; }
	
	private String commant = "idle";
	public String getCommant() { return commant; }
	public void setCommant( String _commant ) { this.commant = _commant; }
	
	// 카운트
	private int count = 0;
	public int getCount() { return count; }
	public void setCount( int _count ) { this.count = _count; }
	
	// 오류 카운트
	private int errorCount = 0;
	public int getErrorCount() { return errorCount; }
	public void setErrorCount( int _errorCount ) { this.errorCount = _errorCount; }
	
	public BufferedImage getScreenshot(){
		getDeviceOrientation();
		
		RawImage img = null;
		try {
			img = device.getScreenshot();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		int w=img.width;
		int h=img.height;
		int size=w * h;
		int argb[]=new int[size];
		int bytesPerPixel=img.bpp / 8;
		long sums[]=new long[4];
		
		for (int i=0; i < size; i++) {
			int value=img.getARGB(i * bytesPerPixel);
		    sums[0]+=(value >> 24) & 0xff;
		    sums[1]+=(value >> 16) & 0xff;
		    sums[2]+=(value >> 8) & 0xff;
		    sums[3]+=(value >> 0) & 0xff;
		}
		
		boolean isArgb=(sums[0] > sums[1]) && (sums[0] > sums[2]) && (sums[0] > sums[3]);
		for (int i=0; i < size; i++) {
			int value=img.getARGB(i * bytesPerPixel);
		    int r, g, b;
		    
		    if (isArgb) {
		    	r=(value >> 16) & 0xff;
		    	g=(value >> 8) & 0xff;
		    	b=(value >> 0) & 0xff;
		    	
		    } else {
		    	r=(value >> 24) & 0xff;
		    	g=(value >> 0) & 0xff;
		    	b=(value >> 8) & 0xff;
		    }
		    argb[i]=(r << 16) | (g << 8) | (b << 0)| 0xff000000;
		}
		
		BufferedImage bimg=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
		bimg.setRGB(0,0,w,h,argb,0,w);
		 
		return ImageUtils.rotate( bimg, 360.0 - orientation * 90 );
	}
	
	private int display_width = 0;
	private int display_height = 0;
	public int getDisplayWidth(){ return orientation % 2 == 0 ? display_width : display_height; }
	public void setDisplayWidth( int width ) { display_width = width; }
	
	public int getDisplayHeight(){ return orientation % 2 == 0 ? display_height : display_width; }
	public void setDisplayHeight( int height ) { display_height = height; }
	
	/**
	 *  
	 */
	private IShellOutputReceiver DeviceDisplaySize_ShellOutputReceiver = new IShellOutputReceiver() {
		@Override
		public void addOutput(byte[] data, int offset, int length) {
			//System.out.println( "1" + new String( data, offset, length ));
			String line = new String( data, offset, length ).trim();
			
			if( line.isEmpty()) return;
			if( !line.startsWith("Physical size: ")) return;
			
			String size = line.substring( "Physical size: ".length() ).trim();
			String token[] = size.split("x");
			if( token.length < 2 ) return;
			
			setDisplayWidth( Integer.valueOf( token[0]));
			setDisplayHeight( Integer.valueOf( token[1]));
		}

		@Override
		public void flush() {}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	/**
	 * @param device
	 */
	private void getDeviceDisplaySize() {
		try {
			device.executeShellCommand("wm size", DeviceDisplaySize_ShellOutputReceiver );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 */
	public void getDeviceOrientation() {
		try {
			device.executeShellCommand("dumpsys SurfaceFlinger", DeviceOrientation_ShellOutputReceiver );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean displayOn = false;
	public boolean getDisplayOn() { return displayOn; }
	public void setDisplayOn( boolean displayOn ) { this.displayOn = displayOn; }
	
	private int orientation;
	public int getOrientation() { return orientation; }
	public void setOrientation( int orientation ) { 
		this.orientation = orientation;
		System.out.println("setOrientation : " + orientation );
	}
	/**
	 *  
	 */
	private IShellOutputReceiver DeviceOrientation_ShellOutputReceiver = new IShellOutputReceiver() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		@Override
		public void addOutput(byte[] data, int offset, int length) {
			baos.write( data, offset, length );
		}

		@Override
		public void flush() { 
			String lines [] = baos.toString().split( System.lineSeparator());
			
			for( String line : lines ) {
				if( !line.contains("orientation")) continue;
				
				String items [] = line.trim().split(",");
				for( String item : items ) {
					String token [] = item.trim().split("=");
					if( token.length > 1 ) {
						String key = token[0].trim().toLowerCase();
						String value = token[1].trim().toLowerCase();
						
						try {
							if( key.compareTo("candraw") == 0 ) {
								setDisplayOn( Integer.valueOf( value ) != 0 );
							} else if( key.compareTo("isdisplayon") == 0 ) {
								setDisplayOn( Integer.valueOf( value ) != 0 );
							} else if( key.compareTo("orientation") == 0 ) {
								setOrientation( Integer.valueOf( value ));
							}
						} catch( Exception e ) {}
					}
				}
			}
			try {
				baos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean isCancelled() { 
			return false;
		}
	};
	
	public void touchScreen(int x, int y ) {
		Command( String.format( "input tap %d %d", x, y ));
	}

	public void swipeScreen(int x, int y, int x2, int y2, long swipeTime) {
		Command( String.format( "input swipe %d %d %d %d %d", x, y, x2, y2, swipeTime ));
	}
	
	private IShellOutputReceiver ShellOutputReceiver = new IShellOutputReceiver() {
		@Override
		public void addOutput(byte[] data, int offset, int length) {}

		@Override
		public void flush() {}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	public void Command( String cmd ) {
		new Thread( new Runnable(){
			@Override
			public void run() {
				try {
					device.executeShellCommand( cmd, ShellOutputReceiver );
				} catch (Exception e) {
					e.printStackTrace();
				}
			}}).start();			
	}
}
