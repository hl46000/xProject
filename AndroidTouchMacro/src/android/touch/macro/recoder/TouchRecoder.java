package android.touch.macro.recoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import android.touch.macro.AndroidTouchMacro;
import android.touch.macro.G;
import android.touch.macro.adb.AdbCommand;
import android.touch.macro.adb.DeviceInfo;
import android.touch.macro.util.Log;
import android.touch.macro.util.Util;

public class TouchRecoder {
	private TouchEvent touchEvent = new TouchEvent();
	private ArrayList<TouchRecodeData> recode_data = new ArrayList<TouchRecodeData>();
	private int current_page = 0;
	private File project_folder = null;
	private int orientation = 1;	// 기본 90도 가로 모드
	
	private int touch_screen_width = -1;
	private int touch_screen_height = -1;
	
	private DeviceInfo deviceInfo = null;
	private AndroidTouchMacro androidTouchMacro = null;
	
	
	public void init( AndroidTouchMacro androidTouchMacro ) {
		this.androidTouchMacro = androidTouchMacro;
	}
	
	public void setTouchScreenSize( int w, int h ) {
		touch_screen_width  = w;
		touch_screen_height = h;
	}
	
	
	/**
	 * Device 정보객체를 설정한다. 
	 * 
	 * @param deviceInfo
	 */
	public void setDeviceInfo( DeviceInfo deviceInfo ) {
		this.deviceInfo = deviceInfo;
	}
	
	/**
	 * 
	 */
	public void release() {
		Log.i( "TouchRecoder::release ==> IN" );
		touchEvent.release();
		stop();
		
		for( TouchRecodeData data : recode_data ) {
			data.release();
		}
		recode_data.clear();
		Log.i( "TouchRecoder::release ==> OUT" );
	}
	

	/**
	 * 선택된 폴더에 새롭게 Touch Data 을 기록 합니다. 
	 * 
	 * @param frame
	 * @param selectedFolder
	 */
	public void newProject( JFrame frame, File selectedFolder ) {
		File list [] = selectedFolder.listFiles();
		if( list.length > 0 ) {
			int result = JOptionPane.showConfirmDialog( frame, String.format("'%s' 폴더에 파일 존재 합니다. \n기존 파일들은 모두 삭제 됩니다. \n 계속 진행할까요?", selectedFolder.getName() ), "새로 만들기", JOptionPane.YES_NO_OPTION );
			if( result == JOptionPane.NO_OPTION ) {
				return;
			} 
		}
		
		for( TouchRecodeData data : recode_data ) {
			data.release();
		}
		recode_data.clear();
		current_page = 0;
		
		project_folder = selectedFolder;
		
		try {
			orientation = Integer.valueOf( AdbCommand.getOrientation( deviceInfo ));
		} catch( Exception e ) { e.printStackTrace(); }
		
		try {
			FileUtils.deleteDirectory( project_folder );
		} catch (IOException e) { e.printStackTrace();}
		project_folder.mkdirs();
	}

	/**
	 * 선택된 폴더에 존재하는 Touch Data 내용을 읽어옵니다. 
	 * 
	 * @param selectedFolder
	 */
	public void loadProject(JFrame frame, File selectedFolder ) {
		if( project_folder != null ) {
			int result = JOptionPane.showConfirmDialog( frame, "열려진 프로젝트가 있습니다. \n이전 프로젝트를 저장 하시겠습니까?", "TouchTest", JOptionPane.YES_NO_CANCEL_OPTION );
			if( result == JOptionPane.YES_OPTION ) {
				saveProject( frame );
			} else if( result == JOptionPane.CANCEL_OPTION ) {
				return;
			} 
		}
		
		for( TouchRecodeData data : recode_data ) {
			data.release();
		}
		recode_data.clear();
		
		Properties prop = new Properties();
		File propFile = new File( selectedFolder.getAbsolutePath() + "/TouchHistory.properties" );
		FileInputStream fis = null;
		try {
			fis = new FileInputStream( propFile );
			prop.load( fis );
			
			int cnt = Integer.valueOf(( String ) prop.get("COUNT"));
			for( int i = 0; i < cnt; i++ ) {
				String key = String.format( "HISTORY_%03d", i );
				String val = prop.getProperty(key);
				
				if( val != null ) {
					String token [] = val.split(" ");
					
					int x = Integer.valueOf( token[0] );
					int y = Integer.valueOf( token[1] );
					int d = Integer.valueOf( token[2] );
					File img = new File( selectedFolder.getAbsolutePath() + "/" + token[3] );
					File tmp = File.createTempFile( "tmp", ".png", G.getTempPath() );
					
					FileUtils.copyFile( img, tmp );
					
					recode_data.add( new TouchRecodeData( tmp, x, y, d ));
					tmp.delete();
				}
			}
			
			current_page = 1;
			project_folder = selectedFolder;
			
			String key = String.format( "ORIENTATION" );
			String val = prop.getProperty(key);
			try {
				orientation = Integer.valueOf( val );
			} catch( Exception e ) { e.printStackTrace(); }
			
			key = String.format( "SCREEN_WIDTH" );
			val = prop.getProperty(key);
			try {
				touch_screen_width = Integer.valueOf( val );
			} catch( Exception e ) { e.printStackTrace(); }	
			
			key = String.format( "SCREEN_HEIGHT" );
			val = prop.getProperty(key);
			try {
				touch_screen_height = Integer.valueOf( val );
			} catch( Exception e ) { e.printStackTrace(); }
						
		} catch ( Exception e ) {
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
	}
	
	/**
	 * 현재 기록된 Touch Data 내용을 저장합니다. 
	 */
	public void saveProject( JFrame frame ) {
		if( project_folder == null ) {
			project_folder = Util.saveFolderChooserDialog();
			
			if( project_folder == null ) {
				return;
			}
		}
		
		File propFile = new File( project_folder.getAbsolutePath() + "/TouchHistory.properties" );
		Properties prop = Util.readProperties( propFile );
		
		// 기존 파일들은 모두 삭제한다. 
		if( propFile.exists()) {
			int index = 0;
			
			while( true ) {
				String key = String.format( "HISTORY_%03d", index++ );
				String val = prop.getProperty(key);
				
				if( val == null ) break;
				String token[] = val.split(" ");
				
				File file = new File( project_folder.getAbsolutePath() + "/" + token[ token.length - 1 ] );
				if( file.exists()) file.delete();
			}
			
			prop.clear();
		}
				
		prop.put( "COUNT", String.valueOf( recode_data.size()));
		prop.put( "ORIENTATION", String.valueOf(orientation));
		prop.put( "SCREEN_WIDTH", String.valueOf(touch_screen_width));
		prop.put( "SCREEN_HEIGHT", String.valueOf(touch_screen_height));
		  
		int index = 0;
		for( TouchRecodeData data : recode_data ) {
			try {
				File img = File.createTempFile( "IMG_", String.format("_%02d.png", index), project_folder );
				FileUtils.copyFile( data.screenCapFile, img );
				
				String key = String.format( "HISTORY_%03d", index );
				String val = String.format( "%d %d %d %s", data.x, data.y, data.delay, img.getName() );
				
				prop.put( key, val );
			} catch (IOException e) {
				e.printStackTrace();
			}
			index++;
		}
		
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream( propFile );
			prop.store( fos, "123" );
			
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
	 * 현재 작업 중인 화면을 Current page 앞에 새로운 화면을 추가한다.   
	 * 
	 * @param x
	 * @param y
	 * @param img
	 * @param delay
	 * @return
	 */
	public boolean prevInsertPage( int x, int y, File img, int delay ) {
		try {
			if( recode_data.isEmpty()) {
				recode_data.add( new TouchRecodeData( img, x, y, delay));
				current_page++;
			} else {
				recode_data.add( current_page == 0 ? 0 : current_page - 1, new TouchRecodeData( img, x, y, delay));
			}
			//current_page++;
			
		} catch( IndexOutOfBoundsException e ) {
			return false;
		}
		return true;
	}
	
	
	/**
	 * 현재 작업 중인 화면을 Current page 뒤에 새로운 화면을 추가한다.   
	 * 
	 * @param x
	 * @param y
	 * @param img
	 * @param delay
	 * @return
	 */
	public boolean postInsertPage( int x, int y, File img, int delay ) {
		try {
			if( recode_data.isEmpty()) {
				recode_data.add( new TouchRecodeData( img, x, y, delay));
				current_page++;
			} else {
				recode_data.add( current_page, new TouchRecodeData( img, x, y, delay));
				current_page++;
			}
			
		} catch( IndexOutOfBoundsException e ) {
			return false;
		}
		return true;
	}
		
	
	/**
	 * current page 정보를 갱신합니다. 
	 * 
	 * @return
	 */
	public boolean modifyPage( int x, int y, BufferedImage img, int delay ) {
		TouchRecodeData data = recode_data.get( current_page - 1 );
		data.x = x;
		data.y = y;
		data.delay = delay;
		try {
			Util.BufferedImageToFile( img, data.screenCapFile );
		} catch (IOException e) {
			e.printStackTrace();
		}
		 
		return true;
	}
	
	/**
	 * 현재 작업 중인 Touch history data의 current page 정보를 삭제 합니다. 
	 * 
	 * @return
	 */
	public boolean deletePage() {
		try {
			TouchRecodeData data = recode_data.remove( current_page - 1 );
			data.release();
			
			current_page--;
		} catch( IndexOutOfBoundsException e ) {
			return false;
		}
		return true;
	}
	
	/**
	 * @return
	 */
	public TouchRecodeData getCurrentRecodeData() {
		if( recode_data.isEmpty()) return null;
		
		return recode_data.get( current_page - 1 );
	}
	
	public int getTotalPage() {
		return recode_data.size();
	}

	public int getCurrentPage() {
		return current_page;
	}

	public boolean nextPage() {
		if( current_page + 1 > recode_data.size()) {
			return false;
		}
		current_page++;
		return true;
	}
	
	public boolean prevPage() {
		if( current_page - 1 > 0 ) {
			current_page--;
			return true;
		}
		return false;
	}

	public void TouchDown(int screen_x, int screen_y) { touchEvent.TouchDown(screen_x, screen_y); }
	public void TouchUp(int screen_x, int screen_y) { touchEvent.TouchUp(screen_x, screen_y); }
	public void TouchDrag(int screen_x, int screen_y) { touchEvent.TouchDrag(screen_x, screen_y); }
	public boolean touchScreenTest() { return touchEvent.touchScreenTest( deviceInfo ); }
	public void touchScreenTestRun(JFrame frame) { touchEvent.touchScreenTestRun(frame); }


	/**
	 * 등록된 Touch data history 를 재생합니다. 
	 */
	boolean run_thread = false;
	public boolean isPlaying() { return run_thread; } 
	public void play() {
		Log.i( "Play thread start ==> IN" );
		stop();
		
		if( recode_data.size() < 1 ) return;
		/*
		if( orientation != Integer.valueOf( AdbCommand.getOrientation(deviceInfo)) ) {
			Util.alert( "Touch Macro", "장치의 방향이 데이터 작성 시의 방향과 일치하지 않습니다. ");
			return;
		}
		*/
		run_thread = true;
		new Thread( new Runnable(){
			
			/**
			 * 
			 */
			public void play() {
				
				touchEvent.reconnection();
				Log.i( "\f" );
				
				for( int i = 0; i < recode_data.size(); i++ ) {
					if( !run_thread ) break;
															
					current_page = i + 1;
					androidTouchMacro.update_page_info();
					
					TouchRecodeData data = recode_data.get(i);
					
					for( int sec = 0; sec < data.delay && run_thread; sec ++ ) {
						androidTouchMacro.update_remain_time( data.delay - sec );
						
						try {
							Thread.sleep( 1000 );
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					touchEvent.TouchDown( data.x, data.y );
					try {
						Thread.sleep( 200 );
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					touchEvent.TouchUp( data.x, data.y );
				}
			}
			
			public void run() {
				Log.i( "Play thread run ==> IN" );
				
				int count = 0;
				while( run_thread ) {
					
					for( Process process : G.tempAdbProcess ) {
						try {
							process.destroy();
						} catch( Exception e ) {}
					}
					G.tempAdbProcess.clear();
					
					if( !AdbCommand.checkConnectionDevice( deviceInfo )) {
						Util.alert( "Disconnection", String.format( "'%s' 장치와 연결이 끊어졌습니다.", deviceInfo.model ));
						run_thread = false;
						break;
					}
					
					if( !run_thread ) break;
					play();
					keepBatteryLevel();
					Log.i( "Played count : %d, - %s", ++count, new Date().toString() );
				}
				
				Log.i( "Play thread run ==> OUT" );
			}

			} ).start();
		
		androidTouchMacro.update_remain_time( -1 );
		
		Log.i( "Play thread start ==> OUT" );
	}
	
	public void stop() {
		Log.i( "Play thread stop ==> IN" );
		run_thread = false;		
		Log.i( "Play thread stop ==> OUT" );
	}
	
	/**
	 * 
	 */
	private void keepBatteryLevel() {
		if( !androidTouchMacro.isKeepBetteryLevel()) return;
		
		int keepBatteryLevelValue = androidTouchMacro.getKeepBetteryLevelValue(); 
		int batteryLevelValue = AdbCommand.getBatteryLevel( deviceInfo );
		
		androidTouchMacro.update_current_battery_level( batteryLevelValue );
		
		if( keepBatteryLevelValue > batteryLevelValue ) {
			AdbCommand.sendKeyEvent( deviceInfo, "KEYCODE_POWER");	// 화면을 끔니다. 
			while( keepBatteryLevelValue > batteryLevelValue ) {
				
				// 10분 대기
				try {
					Thread.sleep( 10 * 60 * 1000 );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				batteryLevelValue = AdbCommand.getBatteryLevel( deviceInfo );
			}
			AdbCommand.sendKeyEvent( deviceInfo, "KEYCODE_POWER");	// 화면을 켬니다.
		}
	}
}
