package android.touch.macro;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.io.FileUtils;

import android.touch.macro.adb.AdbCommand;
import android.touch.macro.adb.DeviceInfo;
import android.touch.macro.ex.JButtonEx;
import android.touch.macro.ex.JLabelEx;
import android.touch.macro.ex.JMenuItemEx;
import android.touch.macro.ex.JRadioButtonMenuItemEx;
import android.touch.macro.recoder.TouchRecodeData;
import android.touch.macro.recoder.TouchRecoder;
import android.touch.macro.util.Log;
import android.touch.macro.util.Util;

public class AndroidTouchMacro extends JFrame {
	private static AndroidTouchMacro mainFrame = null;
	private static final long serialVersionUID = -4073797057805115738L;
	private JLabelEx imagePanel = null;
	private float  imageRate = 1.0f;
	
	private ArrayList<DeviceInfo> devices = null;
	private int mSelectedDevice = 0;

	private BufferedImage img_display = null;
	private int display_rotate = 0;
		
	private BufferedImage img_arrow = null;
	private Label lb_project_name = new Label("프로젝트를 선택해 주세요");
	private Label lb_page_info = new Label("0/0");
	private Label lb_battery_level = null;
	
	private TouchRecoder recoder = new TouchRecoder(); 
	private int touch_screen_x = -1;
	private int touch_screen_y = -1;
	private int touch_screen_width = -1;
	private int touch_screen_height = -1;
	
	private JTextField tf_delay = null;
	private JTextField tf_batteryLevel = null;
	private JButtonEx rotate90p_button = null;
	private JButtonEx rotate90n_button = null;
	private JButtonEx playNstop_button = null;
	
	private JCheckBox cb_keepBatteryLevel = null;
	private JPopupMenu pop_keyEvent = new JPopupMenu();
	private Map<String,BufferedImage> map_images = new HashMap<String,BufferedImage>(); 
	
	public boolean isKeepBetteryLevel() {
		return cb_keepBatteryLevel.isSelected();
	}
	
	public int getKeepBetteryLevelValue() {
		try {
			return Integer.valueOf( tf_batteryLevel.getText() );
		} catch( Exception e ) {
			e.printStackTrace();
		}
		
		return cb_keepBatteryLevel.isSelected() ? 10 : 0;
	}
	
	private Panel setPanelRight() {
		JButtonEx rightMove = new JButtonEx( ">>", Constant.BUTTON_ITEM_RIGHT_MOVE );
		rightMove.addActionListener( buttonsActionListener );
		
		Panel panelRight = new Panel();
		panelRight.setLayout(new BorderLayout(0, 0));
		panelRight.add( rightMove );
		
		return panelRight;
	}
	
	private Panel setPanelLeft() {
		JButtonEx leftMove = new JButtonEx( "<<", Constant.BUTTON_ITEM_LEFT_MOVE );
		leftMove.addActionListener( buttonsActionListener );
		
		Panel panelLeft = new Panel();
		panelLeft.setLayout(new BorderLayout(0, 0));
		panelLeft.add( leftMove );
		
		return panelLeft;
	}
	
	private Panel setPanelBottom() {
		Panel panelBottom = new Panel();
		panelBottom.setLayout(new BorderLayout(0, 0));
		
		Panel _top = new Panel();
		Panel _center = new Panel();
		Panel _bottom = new Panel();
		Panel _keepBattery = new Panel();
		
		Panel _centerParent = new Panel();
		_centerParent.setLayout(new BorderLayout(0, 0));
		
		_top.setLayout(new FlowLayout());
		_center.setLayout(new FlowLayout());
		_bottom.setLayout(new FlowLayout());
		_keepBattery.setLayout(new FlowLayout());
		
		_centerParent.add( _center, BorderLayout.CENTER );
		_centerParent.add( _keepBattery, BorderLayout.SOUTH );
		
		cb_keepBatteryLevel = new JCheckBox("Keep Battery level");
		_keepBattery.add(cb_keepBatteryLevel);
				
		tf_batteryLevel = new JTextField();
		tf_batteryLevel.setText("10");
		_keepBattery.add(tf_batteryLevel);
		tf_batteryLevel.setColumns(3);
		
		lb_battery_level = new Label("     Current Battery Level : ");
		_keepBattery.add(lb_battery_level);
		
		panelBottom.add( _top, BorderLayout.NORTH );
		panelBottom.add( _centerParent, BorderLayout.CENTER );
		panelBottom.add( _bottom, BorderLayout.SOUTH );
		
		_top.add(new JLabel("화면회전"));
		rotate90p_button = new JButtonEx("+90회전", Constant.BUTTON_ITEM_ROTATE_p90);
		rotate90p_button.addActionListener( buttonsActionListener );
		_top.add( rotate90p_button );
						
		rotate90n_button = new JButtonEx("-90회전", Constant.BUTTON_ITEM_ROTATE_n90 );
		rotate90n_button.addActionListener( buttonsActionListener );
		_top.add(rotate90n_button);
		
		_top.add(new JLabel("          지연시간"));
				
		tf_delay = new JTextField();
		tf_delay.setText("3");
		tf_delay.setHorizontalAlignment(SwingConstants.RIGHT);
		_top.add(tf_delay);
		tf_delay.setColumns(3);
		
		_top.add(new JLabel("sec   "));
				
		JButtonEx btnInsert1 = new JButtonEx("Insert", Constant.BUTTON_ITEM_PREV_INSERT );
		btnInsert1.setText("앞에추가");
		btnInsert1.addActionListener( buttonsActionListener );
		_center.add(btnInsert1);
		
		JButtonEx btnInsert2 = new JButtonEx("Insert", Constant.BUTTON_ITEM_POST_INSERT );
		btnInsert2.setText("뒤에추가");
		btnInsert2.addActionListener( buttonsActionListener );
		_center.add(btnInsert2);
		
		JButtonEx btnModify = new JButtonEx("Modify", Constant.BUTTON_ITEM_MODIFY);
		btnModify.setText("수정");
		btnModify.addActionListener( buttonsActionListener );
		_center.add(btnModify);
		
		JButtonEx btnDelete = new JButtonEx("Delete", Constant.BUTTON_ITEM_DELETE);
		btnDelete.setText("삭제");
		btnDelete.addActionListener( buttonsActionListener );
		_center.add(btnDelete);
				
		
		_bottom.add(new JLabel("화면이미지"));
		JButtonEx btnLoad = new JButtonEx("불러오기", Constant.BUTTON_SCREEN_IMAGE_LOAD );
		btnLoad.addActionListener( buttonsActionListener );
		_bottom.add(btnLoad);
		
		JButtonEx btnSave = new JButtonEx("저장하기", Constant.BUTTON_SCREEN_IMAGE_SAVE );
		btnSave.addActionListener( buttonsActionListener );
		_bottom.add(btnSave);
		
		_bottom.add(new JLabel("    "));
		
		JButtonEx btnCaption = new JButtonEx("장치 화면캡쳐", Constant.BUTTON_SCREEN_CAPTION );
		btnCaption.addActionListener( buttonsActionListener );
		_bottom.add(btnCaption);
				
		return panelBottom;
	}
	
	public void update_current_battery_level( int level ) {
		lb_battery_level.setText( String.format( "     Current Battery Level : %d", level ) );		
	}
	
	private void update_display_image() {
		if( imagePanel == null ) return;
		if( img_display == null ) return;
				
		BufferedImage draw_img = rotate( img_display, display_rotate );
		touch_screen_width  = draw_img.getWidth();
		touch_screen_height = draw_img.getHeight();
		
		Log.i( "Image size : w=%d, h=%d\n", touch_screen_width, touch_screen_height );
		
		int p_w = imagePanel.getWidth();
		int p_h = imagePanel.getHeight();
		Log.i( "Image Panel size : w=%d, h=%d\n", p_w, p_h );
		
		float w_div = (float)p_w / touch_screen_width;
		float h_div = (float)p_h / touch_screen_height;
		imageRate = Math.min( w_div, h_div );
		
		Log.i( "div %f %f %f\n", w_div, h_div, imageRate );
		
		int new_w = (int) (touch_screen_width  * imageRate);
		int new_h = (int) (touch_screen_height * imageRate);
		Log.i( "Image resize : w=%d, h=%d\n", new_w, new_h );
		
		BufferedImage resizedImage = resizeImage( draw_img, new_w, new_h );
		
		Container container = imagePanel.getParent();
		container.setSize(new_w, new_h);
		container.invalidate();
		
		imagePanel.setIcon( new ImageIcon( resizedImage ));
		imagePanel.repaint();
						
		resizedImage.flush();
		resizedImage = null;
		
		draw_img.flush();
		draw_img = null;
		
		mainFrame.pack();
	}
	
	private void update_display_cursor( int x, int y ) {
		if( img_arrow != null ) {
			imagePanel.setImage( img_arrow, x - 14, y );
			imagePanel.repaint();					
		}
	}
	
	private Panel setPanelTop() {
		Panel panelTop = new Panel();
		panelTop.setLayout(new BorderLayout(0, 0));
		
		Panel _top = new Panel();
		Panel _bottom = new Panel();
		
		_top.setLayout(new BorderLayout());
		_bottom.setLayout(new BorderLayout());
		
		panelTop.add( _top, BorderLayout.NORTH );
		panelTop.add( _bottom, BorderLayout.CENTER );
		
		Label project_label = new Label("프로젝트명 : ");
		project_label.setAlignment(Label.RIGHT);
		lb_project_name.setAlignment(Label.LEFT );
		
		playNstop_button = new JButtonEx("Play", Constant.BUTTON_ITEM_PLAYSTOP );
		playNstop_button.addActionListener( buttonsActionListener );
		
		_top.add(project_label, BorderLayout.WEST );
		_top.add(lb_project_name, BorderLayout.CENTER );
		_top.add( playNstop_button, BorderLayout.EAST );
		
		lb_page_info.setAlignment(Label.CENTER);
		_bottom.add(lb_page_info, BorderLayout.CENTER );
		
		return panelTop;
	}
	
	/**
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param radio
	 * @return
	 */
	protected Point displayRotate( int x, int y, int w, int h, int radio ) {
		Log.i( "radio : %d", radio );
		Point ret = new Point( x, y );
		
		if( radio == 90 ) {		// 순방향
			ret.x = y; ret.y = w - x;
		} else if( radio == -90 ) {			// 역방향
			ret.x = h - y; ret.y = x;
		}
		
		return ret;
	}
	
	
	protected Point reverseDisplayRotate( int x, int y, int w, int h, int radio ) {
		
		Log.i( "x:%d, y:%d, w:%d, h:%d, radio : %d", x, y, w, h, radio );
		Point ret = new Point( x, y );
		
		if( radio == 90 ) {		// 순방향
			ret.x = w - y; ret.y = x;
		} else if( radio == -90 ) {			// 역방향
			ret.x = y; ret.y = h - x;
		}
				
		return ret;
	}
		
	/**
	 * 
	 */
	
	MouseMotionListener imagePanelMouseMotionListener = new MouseMotionListener() {
		long previous_ms = 0;
		
		@Override
		public void mouseDragged(MouseEvent e) {
			long ms = System.currentTimeMillis();
			if( ms - previous_ms < 100 || e.isMetaDown()) return;
			previous_ms = ms;
			
			Point point = e.getPoint();
			
			touch_screen_x = (int)( point.x / imageRate );
			touch_screen_y = (int)( point.y / imageRate );
		
			Point rotated_point = displayRotate( touch_screen_x, touch_screen_y, touch_screen_width, touch_screen_height, display_rotate );
			Log.i( "Mouse Dragged : x=%d, y=%d\n", rotated_point.x, rotated_point.y );
			
			touch_screen_x = rotated_point.x;
			touch_screen_y = rotated_point.y;
			
			recoder.TouchDrag( touch_screen_x, touch_screen_y );
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			

		}
		
	};
	
	MouseAdapter imagePanelMouseAdapter = new MouseAdapter(){
		@Override
		public void mousePressed(MouseEvent e) {
			Point point = e.getPoint();
			if( e.isMetaDown() || pop_keyEvent.isShowing()) {
				return;
			}
			update_display_cursor( point.x, point.y );
			
			touch_screen_x = (int)( point.x / imageRate );
			touch_screen_y = (int)( point.y / imageRate );
		
			Point rotated_point = displayRotate( touch_screen_x, touch_screen_y, touch_screen_width, touch_screen_height, display_rotate );
			Log.i( "Mouse clicked2 : x=%d, y=%d\n", rotated_point.x, rotated_point.y );
			
			touch_screen_x = rotated_point.x;
			touch_screen_y = rotated_point.y;
			
			recoder.TouchDown( touch_screen_x, touch_screen_y );
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if( e.isMetaDown() ) {
				Point point = e.getPoint();
				pop_keyEvent.show( e.getComponent(), point.x, point.y);
				return;
			}
			if( pop_keyEvent.isShowing()) {
				return;
			}
			recoder.TouchUp( touch_screen_x, touch_screen_y );
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			super.mouseMoved(e);
		}
		
		
	};
		
	private Panel setPanelCenter() {
		Panel panelCenter = new Panel();
		panelCenter.setLayout(new BorderLayout(0, 0));
		
		imagePanel = new JLabelEx();
		imagePanel.setVerticalAlignment(SwingConstants.TOP);
		imagePanel.setHorizontalAlignment(SwingConstants.LEFT);
		imagePanel.addMouseListener(imagePanelMouseAdapter );
		imagePanel.addMouseMotionListener( imagePanelMouseMotionListener );
		
		panelCenter.add( imagePanel, BorderLayout.CENTER );
		return panelCenter;
	}
	
	private void setPanel( Container container ) {
		// Right panel
		container.add( setPanelRight(), BorderLayout.EAST);
		
		// Left panel
		container.add( setPanelLeft(), BorderLayout.WEST);
		
		// bottom panel
		container.add( setPanelBottom(), BorderLayout.SOUTH );
		
		// top panel
		container.add( setPanelTop(), BorderLayout.NORTH);
		
		// center panel
		container.add( setPanelCenter(), BorderLayout.CENTER );
		
		JMenuItemEx popupMenu = new JMenuItemEx("BACK KEY", Constant.ID_POPUP_BACK_KEY );
		popupMenu.addActionListener( TouchItemsActionListener );
		pop_keyEvent.add( popupMenu );
		
		popupMenu = new JMenuItemEx("HOME KEY", Constant.ID_POPUP_HOME_KEY );
		popupMenu.addActionListener( TouchItemsActionListener );
		pop_keyEvent.add( popupMenu );
		
		popupMenu = new JMenuItemEx("POWER KEY", Constant.ID_POPUP_POWER_KEY );
		popupMenu.addActionListener( TouchItemsActionListener );
		pop_keyEvent.add( popupMenu );
	}
	
	/**
	 * TopMenu - Touch memu click event
	 */
	ActionListener TouchItemsActionListener = new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			JMenuItemEx item = (JMenuItemEx) arg0.getSource();
			switch( item.objectID ) {
			case Constant.ID_POPUP_BACK_KEY :
				AdbCommand.sendKeyEvent( devices.get( mSelectedDevice ), "KEYCODE_BACK");
				break;
			case Constant.ID_POPUP_HOME_KEY :
				AdbCommand.sendKeyEvent( devices.get( mSelectedDevice ), "KEYCODE_HOME");
				break;
			case Constant.ID_POPUP_POWER_KEY :
				AdbCommand.sendKeyEvent( devices.get( mSelectedDevice ), "KEYCODE_POWER");
				break;
				
			case Constant.MENU_TOUCH_ITEM_NEW	: 
			{
				File selectedFolder = Util.openFolderChooserDialog();
				if( selectedFolder != null ) {
					Log.i( "selected path : %s", selectedFolder.getAbsolutePath());
					
					lb_project_name.setText( selectedFolder.getName() );
					recoder.newProject( AndroidTouchMacro.this, selectedFolder );
					
					update_page_info();
				}				
			}	
				break;
				
			case Constant.MENU_TOUCH_ITEM_LOAD 	:
			{
				File selectedFolder = Util.openFolderChooserDialog();
				if( selectedFolder != null ) {
					Log.i( "selected path : %s", selectedFolder.getAbsolutePath());
					
					lb_project_name.setText( selectedFolder.getName() );
					recoder.loadProject( AndroidTouchMacro.this, selectedFolder);
					
					update_page_info();
				}
			}	
				break;
				
			case Constant.MENU_TOUCH_ITEM_SAVE 	: 
				recoder.setTouchScreenSize( touch_screen_width, touch_screen_height );
				recoder.saveProject(AndroidTouchMacro.this);
				break;
				
			case Constant.MENU_TOUCH_ITEM_RECODE: 
				touch_test_run();
				
				break;
				
			case Constant.MENU_TOUCH_ITEM_PLAY 	: 
				if( recoder.isPlaying()) {
					item.setText("재생하기");
					recoder.stop();
				} else {
					item.setText("정지하기");
					touch_test_run();
					recoder.play();
				}
				break;
			}
		}
	};
	
	private void touch_test_run() {
		if( !recoder.touchScreenTest()) {
			Util.alert( "터치 기능 테스트", "단말기의 터치값을 추출하기위해 \n'확인' 버튼을 클릭 후\n단말기의 터치스크린을 한번 터치해 주세요");
			
			recoder.touchScreenTestRun( AndroidTouchMacro.this );
		}
	}
	
	/**
	 * 재생하기 중에 다음 페이지로 이동할 남은 시간을 표시해 준다. 
	 * 
	 * @param sec
	 */
	public void update_remain_time( int sec ) {
		lb_page_info.setText( String.format( "%d 초후 다음 페이지로 이동합니다.", sec ) );			
	}
	
	/**
	 * 
	 */
	public void update_page_info() {
		int total_page_cnt = recoder.getTotalPage();
		int current_page   = recoder.getCurrentPage();
		
		TouchRecodeData recode_data = recoder.getCurrentRecodeData();
		if( recode_data == null ) return;
		
		if( recoder.isPlaying()) {
			update_remain_time( recode_data.delay );			
		} else{
			lb_page_info.setText( String.format("%d/%d", current_page, total_page_cnt) );
		}
			
				
		touch_screen_x = recode_data.x; 
		touch_screen_y = recode_data.y;
				
		tf_delay.setText( String.valueOf( recode_data.delay ));
				
		release_buffed_images( img_display );
		img_display = loadAndCacheImage( recode_data.screenCapFile );
		
		update_display_image();
		update_reverseDisplay_cursor();
	}
	
	/**
	 * @param file
	 * @return
	 */
	private BufferedImage loadAndCacheImage( File file ) {
		String key = file.getAbsolutePath();
		if( map_images.containsKey( key )) {
			return map_images.get( key );
		}
		
		BufferedImage ret = Util.FileToBufferedImage( file );
		map_images.put( key, ret );
		
		return ret;
	}
	
	
	/**
	 * @return
	 */
	private JMenu setMenuTouchItems() {
		JMenu menu = new JMenu( "프로젝트" );
		menu.setMnemonic( 'P' );
		
		JMenuItemEx newMenu = new JMenuItemEx( "새로만들기", Constant.MENU_TOUCH_ITEM_NEW );
		newMenu.setMnemonic( 'n' );
		newMenu.addActionListener( TouchItemsActionListener );
		menu.add( newMenu );
		
		JMenuItemEx loadMenu = new JMenuItemEx( "불러오기", Constant.MENU_TOUCH_ITEM_LOAD );
		loadMenu.setMnemonic( 'o' );
		loadMenu.addActionListener( TouchItemsActionListener );
		menu.add( loadMenu );
		
		JMenuItemEx saveMenu = new JMenuItemEx( "저장하기", Constant.MENU_TOUCH_ITEM_SAVE );
		saveMenu.setMnemonic( 's' );
		saveMenu.addActionListener( TouchItemsActionListener );
		menu.add( saveMenu );
		
		menu.add( new JSeparator() );
		
		JMenuItemEx recodeMenu = new JMenuItemEx( "터치테스트", Constant.MENU_TOUCH_ITEM_RECODE );
		recodeMenu.setMnemonic( 'r' );
		recodeMenu.addActionListener( TouchItemsActionListener );
		menu.add( recodeMenu );
		
		/*
		JMenuItemEx playMenu = new JMenuItemEx( "재생하기", Constant.MENU_TOUCH_ITEM_PLAY );
		playMenu.setMnemonic( 'p' );
		playMenu.addActionListener( TouchItemsActionListener );
		menu.add( playMenu );
		*/
		
		return menu;
	}
	
	/**
	 * @return
	 */
	private JMenu setMenuDevicesItems() {
		JMenu menu = new JMenu( "단말기" );
		menu.setMnemonic( 'D' );
		
		int idx = 0;
		
		ButtonGroup bg = new ButtonGroup(); 
		devices = AdbCommand.getDevices();
		if( devices != null ) {
			for( DeviceInfo deviceInfo : devices ) {
				String title = String.format( "%s[%s]", deviceInfo.model, deviceInfo.os_ver );
				
				JRadioButtonMenuItemEx mntmDevices = new JRadioButtonMenuItemEx( title, false );
				mntmDevices.objectID = idx++;
				bg.add( mntmDevices );
				
				mntmDevices.addItemListener( new ItemListener(){
					public void itemStateChanged(ItemEvent arg0) {
						if ( arg0.getStateChange() == ItemEvent.SELECTED ) {
							JRadioButtonMenuItemEx item = ( JRadioButtonMenuItemEx ) arg0.getItem(); 
							mSelectedDevice = item.objectID;
							
							recoder.setDeviceInfo( devices.get(mSelectedDevice));
					    }
					}});
				menu.add( mntmDevices );
			}
			
			if( devices.size() > 1 ) {
				recoder.setDeviceInfo( devices.get(mSelectedDevice));
			}
		}
				
		return menu;
	}
	
	private void setMenu() { 
		// Add Memubar
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		// Add File memu
		//menuBar.add( setMenuFileItems());
		
		// Add devices memu
		menuBar.add( setMenuDevicesItems());
				
		// touch memu
		menuBar.add( setMenuTouchItems());
		
		// Add Help memu
		
	}
	
	/**
	 * @param originalImage
	 * @param destWidth
	 * @param destHeight
	 * @return
	 */
	private static BufferedImage resizeImage( BufferedImage originalImage, int destWidth, int destHeight ) {
		BufferedImage resizedImage = new BufferedImage( destWidth, destHeight, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, destWidth, destHeight, null);
		g.dispose();
	 
		//originalImage.flush();
		//originalImage = null;
		
		return resizedImage;
	}
	
	
	/**
	 * Rotates an image. Actually rotates a new copy of the image.
	 * 
	 * @param img The image to be rotated
	 * @param angle The angle in degrees
	 * @return The rotated image
	 */
	public static BufferedImage rotate(BufferedImage img, double angle)
	{
		if( angle == 0.0 ) return img;
		
		double sin = Math.abs(Math.sin(Math.toRadians(angle)));
		double cos = Math.abs(Math.cos(Math.toRadians(angle)));

	    int w = img.getWidth(null), h = img.getHeight(null);

	    int neww = (int) Math.floor(w*cos + h*sin);
	    int newh = (int) Math.floor(h*cos + w*sin);

	    BufferedImage bimg = new BufferedImage( neww, newh, img.getType());
	    Graphics2D g = bimg.createGraphics();

	    g.translate((neww-w)/2, (newh-h)/2);
	    g.rotate(Math.toRadians(angle), w/2, h/2);
	    g.drawRenderedImage(img, null);
	    g.dispose();

	    //img.flush();
	    //img = null;
	    
	    return bimg;
	}
	
	/**
	 * @param img
	 */
	private void release_buffed_images( BufferedImage img ) {
		if( img != null ) {
			img.flush();
		}
		img = null;
	}
	
	
	/**
	 * 스크린 좌료를 화면 좌표로 역 계산하여 커서를 표시한다. 
	 */
	private void update_reverseDisplay_cursor() {
		Point rotated_point = reverseDisplayRotate( touch_screen_x, touch_screen_y, touch_screen_width, touch_screen_height, display_rotate );
		update_display_cursor((int)( rotated_point.x * imageRate ), (int)( rotated_point.y * imageRate));
	}
	
	/**
	 * 
	 */
	ActionListener buttonsActionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JButtonEx item = ( JButtonEx ) e.getSource();
			
			switch( item.objectID ) {
			
			case Constant.BUTTON_ITEM_PLAYSTOP :
				if( recoder.isPlaying()) {
					lb_page_info.setText( String.format("%d/%d", recoder.getCurrentPage(), recoder.getTotalPage() ) );
										
					playNstop_button.setText( "Play" );
					recoder.stop();
				} else {
					playNstop_button.setText( "Stop" );
					touch_test_run();
					recoder.play();
				}
				break;
				
			case Constant.BUTTON_ITEM_ROTATE_p90 :
				display_rotate += 90;
				if( display_rotate == 90 ) {
					rotate90p_button.setEnabled( false );
				}
				rotate90n_button.setEnabled( true );
				
				update_display_image();
				update_reverseDisplay_cursor();
								
				break;
			
			case Constant.BUTTON_ITEM_ROTATE_n90 :
				display_rotate -= 90;
				if( display_rotate == -90 ) {
					rotate90n_button.setEnabled( false );
				}
				rotate90p_button.setEnabled( true );
				
				update_display_image();
				update_reverseDisplay_cursor();
				break;
				
			case Constant.BUTTON_ITEM_RIGHT_MOVE :
				if( recoder.nextPage()) {
					update_page_info();
				}
				break;
				
			case Constant.BUTTON_ITEM_LEFT_MOVE	: 
				if( recoder.prevPage()) {
					update_page_info();
				}
				break;
				
			case Constant.BUTTON_SCREEN_CAPTION :
				release_buffed_images( img_display );
				img_display = AdbCommand.screencap( devices.get( mSelectedDevice ) );
				update_display_image();
				break;
				
				
			case Constant.BUTTON_ITEM_POST_INSERT :
			{
				//File image = AdbCommand.getLastScreencapFile(devices.get( mSelectedDevice ));
				File image;
				try {
					image = Util.BufferedImageToFile( img_display );
				} catch (IOException e1) {
					e1.printStackTrace();
					break;
				}
				
				String strDelay = tf_delay.getText();
				int delay = 1;
				try {
					delay = Integer.valueOf( strDelay );
				} catch( Exception exception ) { }
				
				if( recoder.postInsertPage( touch_screen_x, touch_screen_y, image, delay )) {
					update_page_info();
				}
			}
			break;
			
			case Constant.BUTTON_ITEM_PREV_INSERT :
			{
				//File image = AdbCommand.getLastScreencapFile(devices.get( mSelectedDevice ));
				File image;
				try {
					image = Util.BufferedImageToFile( img_display );
				} catch (IOException e1) {
					e1.printStackTrace();
					break;
				}
				String strDelay = tf_delay.getText();
				int delay = 1;
				try {
					delay = Integer.valueOf( strDelay );
				} catch( Exception exception ) {
					//exception.printStackTrace();
				}
				
				if( recoder.prevInsertPage( touch_screen_x, touch_screen_y, image, delay )) {
					update_page_info();
				}
			}
				break;
				
			case Constant.BUTTON_ITEM_MODIFY :
			{
				String strDelay = tf_delay.getText();
				int delay = 1;
				try {
					delay = Integer.valueOf( strDelay );
				} catch( Exception exception ) {
					//exception.printStackTrace();
				}
				
				// cache image 에서 해당 이미지를 삭제 한다. 
				String key = recoder.getCurrentRecodeData().screenCapFile.getAbsolutePath();
				if( map_images.containsKey( key )) {
					release_buffed_images( map_images.get(key));
					map_images.remove( key );
				}
								
				if( recoder.modifyPage(touch_screen_x, touch_screen_y, img_display, delay)) {
					update_page_info();
				}
			}
				break;
				
			case Constant.BUTTON_ITEM_DELETE :
				if( recoder.deletePage()) {
					update_page_info();
				}
				break;
				
			case Constant.BUTTON_SCREEN_IMAGE_LOAD :
			{
				File open_image = Util.openFileChooserDialog();
				if( open_image != null ) {
					release_buffed_images( img_display );
					img_display = loadAndCacheImage( open_image );
					update_display_image();					
				}
			}
				break;
				
			case Constant.BUTTON_SCREEN_IMAGE_SAVE :
			{
				if( img_display == null ) {
					Util.alert( "오류", "저장할 화면 이미지가 없습니다. ");
					break;
				}
				
				File save_image = Util.saveFileChooserDialog();
				if( save_image != null ) {
					File _src = null;
					try {
						_src = Util.BufferedImageToFile( img_display );
						FileUtils.copyFile( _src, save_image );
						
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						if( _src != null ) _src.delete();
					}
					
				}
			}
			
				break;
			}
		}
	};
			
	public static void main( String args[] ) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int scr_width  = ( int ) screenSize.getWidth();
		int scr_height = ( int ) screenSize.getHeight();
		int win_width  = 800;
		int win_height = 600;
		
		mainFrame = new AndroidTouchMacro();
		mainFrame.setMinimumSize( new Dimension( win_width, win_height ));
		mainFrame.pack();
		mainFrame.setBounds(( scr_width - win_width ) / 2, ( scr_height - win_height ) / 2, win_width, win_height );
		mainFrame.setVisible( true );
	}

	public AndroidTouchMacro() {
		setTitle("ANDROID TOUCH MACRO");
		
		setPanel( getContentPane() );
		setMenu();
		
		this.addWindowListener( new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				destory();				
			}
		});
		
		InputStream is = getClass().getClassLoader().getResourceAsStream("arrow.png" );
		img_arrow = Util.InputStreamToBufferedImage( is );
							
		this.addComponentListener( new ComponentListener() {
			
			public void componentShown(ComponentEvent e) {
			}
			
			public void componentResized(ComponentEvent e) {
				update_display_image();
			}
			
			public void componentMoved(ComponentEvent e) {
			}
			
			public void componentHidden(ComponentEvent e) {
			}
		});
		
		Util.setFrame( this );
		recoder.init( this );
	}
	
	/**
	 * Application 을 종료 시킵니다. 
	 */
	public void destory() {
		recoder.release();
		try {
			Thread.sleep( 1000 );
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Set<String> keys = map_images.keySet();
		for( String key : keys ) {
			BufferedImage val = map_images.get( key );
			release_buffed_images( val );
		}
		map_images.clear();
		
		G.saveDefaultProperties();
				
		try {
			FileUtils.deleteDirectory( G.getTempPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		AndroidTouchMacro.this.dispose();
		
		for( Process adb : G.AdbProcess ) {
			adb.destroy();
		}
		G.AdbProcess.clear();
		
		Log.i( "windowClosing" );
				
		Runtime.getRuntime().halt(0);
		System.exit(0);
	}
}
