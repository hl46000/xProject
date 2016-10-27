package com.purehero.fx.common;

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
}
