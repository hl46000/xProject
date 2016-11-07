package com.purehero.fx.common;

import com.purehero.common.io.PropertyEx;
import com.purehero.fx.app.MainClass;

import javafx.collections.ObservableList;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuUtils {
	/**
	 * menu의 하위 메뉴 중 id 값에 을로 끝나는 ID을 가지는 메뉴가 check 되어 있는지를 확인 합니다. 
	 * @param Menu
	 * @param string
	 * @return
	 */
	public static boolean isCheckMenu( Menu menu, String id ) {
		ObservableList<MenuItem> items = menu.getItems();
		for( MenuItem item : items ) {
			if( !item.getId().endsWith( id )) continue;
			
			if( item instanceof CheckMenuItem ) {
				CheckMenuItem ck = ( CheckMenuItem ) item;
				return ck.isSelected();
			}
		}
		return false;
	}

	/**
	 * 메뉴 항목 중 Check 항목 들은 이미 저장된 값을 읽어 와서 설정해 준다.
	 * 
	 * @param optionMenu
	 */
	public static void loadCheckMenuStatus(Menu optionMenu ) {
		if( optionMenu == null ) return;
		
		PropertyEx prop = MainClass.instance.getProperty();
		ObservableList<MenuItem> items = optionMenu.getItems();
		for( MenuItem item : items ) {
			if( item instanceof CheckMenuItem ) {
				CheckMenuItem ck = ( CheckMenuItem ) item;
				String val = prop.getValue( "CHECK_MENU_STATUS_" + item.getId());
				ck.setSelected( val != null && val.compareTo("CHECKED") == 0 ); 
			}
		}
	}
	
	/**
	 * @param menu
	 * @param id
	 * @param msg
	 */
	public static void addMenuTitle( Menu menu, String id, String msg ) {
		ObservableList<MenuItem> items = menu.getItems();
		for( MenuItem item : items ) {
			if( !item.getId().endsWith( id )) continue;
			
			String text =  item.getText();
			String token [] = text.split(":");
			
			item.setText( token[0] +": " + msg );
		}
	}

	/**
	 * 메뉴 항목 중 설정된 경로를 표시해야 할 경우 이미 저장된 값을 읽어 와서 설정해 준다.
	 * 
	 * @param menu
	 */
	public static void loadPathMenuText(Menu menu) {
		PropertyEx prop = MainClass.instance.getProperty();
		
		ObservableList<MenuItem> items = menu.getItems();
		for( MenuItem item : items ) {
			String path = prop.getValue( item.getId());
			if( path != null ) {
				addMenuTitle( menu, item.getId(), path );
			}
		}
	}
}
