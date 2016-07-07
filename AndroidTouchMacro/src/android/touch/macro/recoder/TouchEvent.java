package android.touch.macro.recoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import android.touch.macro.G;
import android.touch.macro.adb.AdbCommand;
import android.touch.macro.adb.AdbShell;
import android.touch.macro.adb.AdbShellCallback;
import android.touch.macro.adb.DeviceInfo;
import android.touch.macro.util.Log;

public class TouchEvent {
	private ArrayList<TouchEventValue> touchDownValues = new ArrayList<TouchEventValue>();
	private ArrayList<TouchEventValue> touchUpValues = new ArrayList<TouchEventValue>();
	
	private String properties_filename = null;
	private String touch_device_name = null;
	private boolean bLoadTouchValue = false;
	
	private  DeviceInfo deviceInfo = null;
	private AdbShell adb_shell = null;
	//private boolean isTouchDown = false;
	
	
	/**
	 * 
	 */
	public void release() {
		if( adb_shell != null ) {
			adb_shell.close();
			
			adb_shell = null;
		}		
	}
	
	/**
	 * 기존 연결된 ADB을 종료하고, 새로운 ADB을 연결한다. 
	 */
	public void reconnection() {
		release();
		
		for( Process adb : G.AdbProcess ) {
			adb.destroy();
		}
		G.AdbProcess.clear();
		
		if( !bLoadTouchValue ) {
			Log.d( "not loaded touch event values!" );
			return;
		}
		if( touch_device_name == null ) {
			Log.d( "touch device name is null" );
			return;
		}
		
		if( adb_shell == null ) {
			adb_shell = new AdbShell();
			if( !adb_shell.connect( deviceInfo )) {
				Log.i( "ERROR: failed 'adb shell -s %s'", deviceInfo.serialNumber );
				return;
			}
		}
	}
	
	/**
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean TouchDown( int x, int y ) {
		if( !bLoadTouchValue ) {
			Log.d( "not loaded touch event values!" );
			return false;
		}
		if( touch_device_name == null ) {
			Log.d( "touch device name is null" );
			return false;
		}
		
		if( adb_shell == null ) {
			adb_shell = new AdbShell();
			if( !adb_shell.connect( deviceInfo )) {
				Log.i( "ERROR: failed 'adb shell -s %s'", deviceInfo.serialNumber );
				return false;
			}
		}
		
		for( TouchEventValue eventValues : touchDownValues ) {
			if( 0x0001/*EV_ABS*/ == eventValues.type && 0x0110/*ABS_MT_POSITION_Y*/ == eventValues.id ) {
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0000, x * 70 ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0001, y * 107 ), null );
				
			} else if( 0x0001/*EV_SYN*/ == eventValues.type && 0x014a/*SYN_REPORT*/ == eventValues.id ) {
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0001, 0x014a, 0x00000001 ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x003a, 0x00000001 ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0035, x ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0036, y ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0000, 0x0002, 0x00000000 ), null );				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0000, 0x0000, 0x00000000 ), null );
				//break;
				
			} else if(0x0003/*EV_SYN*/ == eventValues.type && 0x0039/*SYN_REPORT*/ == eventValues.id ) {	// 겔럭시 팝			
				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value ), null );				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0032, 0x0000000d ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0035, x ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0036, y ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0030, 0x0000000e ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0031, 0x0000000b ), null );				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x003c, 0x0000000a ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0000, 0x0000, 0x00000000 ), null );				
				break;
				
			} else {
				eventValues.value = y;
			}
			String cmd = String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value );
			//Log.i( "adb shell command : %s", cmd );
			adb_shell.command( cmd, null );
		}
			
		//isTouchDown = true;
				
		return true;
	}
	
	public void TouchDrag(int x, int y) {
		for( TouchEventValue eventValues : touchDownValues ) {
			if( 0x0003/*EV_ABS*/ == eventValues.type && 0x0035/*ABS_MT_POSITION_X*/ == eventValues.id ) {
				eventValues.value = x;
				String cmd = String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value );
				adb_shell.command( cmd, null );
			} else if( 0x0003/*EV_ABS*/ == eventValues.type && 0x0036/*ABS_MT_POSITION_Y*/ == eventValues.id ) {
				eventValues.value = y;
				String cmd = String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value );
				adb_shell.command( cmd, null );
			} else if( 0x0000/*EV_SYN*/ == eventValues.type && 0x0000/*SYN_REPORT*/ == eventValues.id ) {
				String cmd = String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value );
				adb_shell.command( cmd, null );
				
			} else {
				continue;
			}
		}
	}
	
	
	/**
	 * @return
	 */
	public boolean TouchUp(int x, int y) {
		if( !bLoadTouchValue ) {
			Log.d( "not loaded touch event values!" );
			return false;
		}
		if( touch_device_name == null ) {
			Log.d( "touch device name is null" );
			return false;
		}
		
		if( adb_shell == null ) {
			adb_shell = new AdbShell();
			if( !adb_shell.connect( deviceInfo )) {
				Log.i( "ERROR: failed 'adb shell -s %s'", deviceInfo.serialNumber );
				return false;
			}
		}
		
		for( TouchEventValue eventValues : touchUpValues ) {
			if( 0x0001/*EV_SYN*/ == eventValues.type && 0x014a/*SYN_REPORT*/ == eventValues.id ) {
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0001, 0x014a, 0x00000000 ), null );				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x003a, 0x00000000 ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0035, x ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0003, 0x0036, y ), null );
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0000, 0x0002, 0x00000000 ), null );				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0000, 0x0000, 0x00000000 ), null );
								
			} else if(0x0003/*EV_SYN*/ == eventValues.type && 0x0039/*SYN_REPORT*/ == eventValues.id ) {	// 겔럭시 팝		
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value ), null );				
				adb_shell.command( String.format( "sendevent %s %d %d %d", touch_device_name, 0x0000, 0x0000, 0x00000000 ), null );				
				break;
				
			} else {
				String cmd = String.format( "sendevent %s %d %d %d", touch_device_name, eventValues.type, eventValues.id, eventValues.value );
				//Log.i( "adb shell command : %s", cmd );
				adb_shell.command( cmd, null );
			}
		}
		
		//isTouchDown = false;
		
		return true;
	}
	
	
	/**
	 * 단말기에 대한 Touch Event Test 정보를 가지고 있는지를 확인 합니다. 
	 * 
	 * @param info
	 * @return true: 화면터치 정보를 가지고 있음
	 */
	public boolean touchScreenTest( DeviceInfo info ) {
		Log.i( "TouchEvent::touchScreenTest ==> IN" );
				
		properties_filename = G.getDefaultPath() + "/" + info.model.replace(" ", "_");
		File file = new File( properties_filename );
					
		deviceInfo = info;
		
		release();
			
		touch_device_name = AdbCommand.getTouchDeviceName(deviceInfo);
		
		if( !file.exists()) return false;
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream( file );
			
			Properties prop = new Properties();
			prop.load( fis );
					
			return loadTouchValue( prop );			
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( fis != null ) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
					
		Log.i( "TouchEvent::touchScreenTest ==> OUT" );
		return false;
	}
	
	
	/**
	 * 
	 */
	private void saveTouchValue() {
		int down_cnt = touchDownValues.size();
		int up_cnt = touchUpValues.size();
		
		// 오류 체크
		if( down_cnt < 1 ) {
			Log.i( "ERROR : saveTouchValue => down_cnt value is 0" );
			return;
		}
		
		if( up_cnt < 1 ) {
			Log.i( "ERROR : saveTouchValue => up_cnt value is 0" );
			return;
		}
		
		if( properties_filename == null ) {
			Log.i( "ERROR : saveTouchValue => properties_filename is null" );
			return;
		}
		
		// 배열에 있는 EventValue 값들을 properties 에 기록한다.
		Properties prop = new Properties();
		 
		int i = 0;
		prop.put( "TOUCH_DOWN_CNT", String.valueOf(down_cnt));
		for( TouchEventValue eventValue : touchDownValues ) {
			String key = String.format("TOUCH_DOWN_EVENT_%d", i++);
			String val = String.format("%d %d %d", eventValue.type, eventValue.id, eventValue.value );
			
			prop.put( key, val );
		}
		
		i = 0;
		prop.put( "TOUCH_UP_CNT", String.valueOf(up_cnt));
		for( TouchEventValue eventValue : touchUpValues ) {
			String key = String.format("TOUCH_UP_EVENT_%d", i++);
			String val = String.format("%d %d %d", eventValue.type, eventValue.id, eventValue.value );
			
			prop.put( key, val );
		}
		
		
		// properties 에 기록된 값들을 파일에 기록한다. 
		File file = new File( properties_filename );
		if( file.exists()) file.delete();
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( file );
			prop.store( fos, "" );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} finally {
			if( fos != null ) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * File 에 저장된 Touch Event 값을 읽어 온다. 
	 * 
	 * @param prop
	 * @return
	 */
	private boolean loadTouchValue( Properties prop ) {
		String val = prop.getProperty("TOUCH_DOWN_CNT", "0");
		int down_cnt = Integer.valueOf( val );
		
		val = prop.getProperty("TOUCH_UP_CNT", "0");
		int up_cnt = Integer.valueOf( val );
		
		if( down_cnt == 0 ) {
			Log.i( "ERROR : loadTouchValue => down_cnt value is 0" );
			return false;
		}
		
		if( up_cnt == 0 ) {
			Log.i( "ERROR : loadTouchValue => up_cnt value is 0" );
			return false;
		}
		
		bLoadTouchValue = false;
		
		touchDownValues.clear();
		for( int i = 0; i < down_cnt; i++ ) {
			val = prop.getProperty( String.format("TOUCH_DOWN_EVENT_%d", i));
			if( val == null ) {
				Log.i( "ERROR : '%s' is not found, down_cnt is %d", val, down_cnt );
				return false;
			}
			
			String [] tokens = val.split(" ");
			TouchEventValue newEventValue = new TouchEventValue();
			
			newEventValue.type  = Integer.valueOf( tokens[0] );
			newEventValue.id 	= Integer.valueOf( tokens[1] );
			newEventValue.value = Integer.valueOf( tokens[2] );
			
			touchDownValues.add( newEventValue );
		}
		
		touchUpValues.clear();
		for( int i = 0; i < up_cnt; i++ ) {
			val = prop.getProperty( String.format("TOUCH_UP_EVENT_%d", i));
			if( val == null ) {
				Log.i( "ERROR : '%s' is not found, up_cnt is %d", val, up_cnt );
				return false;
			}
			
			String [] tokens = val.split(" ");
			TouchEventValue newEventValue = new TouchEventValue();
			
			newEventValue.type  = Integer.valueOf( tokens[0] );
			newEventValue.id 	= Integer.valueOf( tokens[1] );
			newEventValue.value = Integer.valueOf( tokens[2] );
			
			touchUpValues.add( newEventValue );
		}
		
		Log.i( "SUCCESS : loadTouchValue from '%s'", properties_filename );
		print_touch_values();
		
		bLoadTouchValue = true;
		
		return true;
	}

	/**
	 * 단말기에 대한 Touch Event Test 정보를 수집하고 저장합니다.<br>  
	 * 본 함수는 touchScreenTest 함수를 호출값이 false 일때 호출해 주면 된다.  
	 * 
	 * @param _frame
	 */
	public void touchScreenTestRun( JFrame _frame ) {
		final AdbShell _shell = new AdbShell();
		final JFrame frame = _frame;
		
		if( _shell.connect( deviceInfo )) {
			
			bLoadTouchValue = false;
			_shell.command( "getevent", new AdbShellCallback(){
				
				ArrayList<TouchEventValue> touchValues = null;				
				
				public void callback(AdbShell shell, String line ) {
					if( !line.startsWith( touch_device_name )) return;
					while( line.contains("  ")) {
						line = line.replace( "  ", " " );
					}
					
					String [] tokens = line.split(" ");
					TouchEventValue newEventValue = new TouchEventValue();
					
					newEventValue.type  = Integer.valueOf( tokens[1], 16 );
					newEventValue.id 	 = Integer.valueOf( tokens[2], 16 );
					try {
						newEventValue.value = tokens[3].startsWith("f") ? -1 : Integer.valueOf( tokens[3], 16 );
					} catch( Exception e ) {
						newEventValue.value = -1;
					}
					
					Log.d( "=> %04x %04x %08x", newEventValue.type, newEventValue.id, newEventValue.value );
					
					if( newEventValue.type == 0x0003 ) {			// touch up
						if( newEventValue.id == 0x0039  ) {
							if( newEventValue.value == -1 ) {
								touchValues = touchUpValues;
							} else {
								touchValues = touchDownValues;
							}
							touchValues.add( newEventValue );
							return;
							
						} else {
							return;
						}
					} else if( newEventValue.type == 0x0001 ) {
						if( newEventValue.id == 0x0110 ) {
							if( newEventValue.value == 1 ) {
								touchValues = touchDownValues;
							} else {
								touchValues = touchUpValues;
							}
						} else if( newEventValue.id == 0x014a ) {
							if( newEventValue.value == 1 ) {
								touchValues = touchDownValues;
							} else {
								touchValues = touchUpValues;
							}
						} else {
							return;
						}
					}
					
					if( touchValues == null ) {
						return;
					}
					
					boolean found = false;
					for( TouchEventValue eventValue : touchValues ) {
						if( eventValue.type == newEventValue.type && eventValue.id == newEventValue.id ) {
							eventValue.value = newEventValue.value;
							found = true;
						}
					}
					
					if( found ) {
						return;
					}
										
					if( newEventValue.type == 0x0000 && newEventValue.id == 0x0000 && newEventValue.value == 0 ) { 	// report
						if( touchValues == touchUpValues ) {
							Log.d( "TouchEvent END" );
							
							shell.close();
							
							JOptionPane.showMessageDialog( frame, "단말기의 터치값 추출이 완료 되었습니다.",
						             "TouchTest", JOptionPane.INFORMATION_MESSAGE );
							
							saveTouchValue();
							print_touch_values();
							
							bLoadTouchValue = true;
						}
					} else {
						touchValues.add( newEventValue );
					}
				}});
		}
	}
	
	/**
	 * 로딩된 TouchEvent 값을 로그로 출력한다. 
	 */
	private void print_touch_values() {
		for( TouchEventValue eventValues : touchDownValues ) {
			Log.d( "TOUCH_DOWN %04x %04x %08x", eventValues.type, eventValues.id, eventValues.value );
		}
		
		for( TouchEventValue eventValues : touchUpValues ) {
			Log.d( "TOUCH_UP %04x %04x %08x", eventValues.type, eventValues.id, eventValues.value );
		}
	}
}
