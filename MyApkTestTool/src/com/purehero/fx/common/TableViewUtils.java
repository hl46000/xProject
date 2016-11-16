package com.purehero.fx.common;

import com.purehero.fx.control.ex.CheckBoxTableCellEx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableViewUtils {
	/**
	 * Integer column 을 설정합니다. 
	 * @param <E>
	 * 
	 * @param tvDeviceInfo
	 * @param property
	 * @param align [ top-left | top-center | top-right | center-left | center | center-right bottom-left | bottom-center | bottom-right | baseline-left | baseline-center | baseline-right ]
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public static <E> void IntegerTableColumn(TableView<E> tableView, String property, String align, int index ) {
		TableColumn<E, Integer> tableColumn 	= (TableColumn<E, Integer>) tableView.getColumns().get(index);
		tableColumn.setCellValueFactory( new PropertyValueFactory<E, Integer>(property));
		tableColumn.setStyle( String.format( "-fx-alignment: %s;", align ));
	}

	/**
	 * 문자열 column 을 설정합니다. 
	 * @param <E>
	 * 
	 * @param tvDeviceInfo
	 * @param property
	 * @param align [ top-left | top-center | top-right | center-left | center | center-right bottom-left | bottom-center | bottom-right | baseline-left | baseline-center | baseline-right ]
	 * @param index
	 */
	@SuppressWarnings("unchecked")
	public static <E> void StringTableColumn( TableView<E> tableView, String property, String align, int index ) {
		TableColumn<E, String> tableColumn 	= (TableColumn<E, String>) tableView.getColumns().get( index );
		tableColumn.setCellValueFactory( new PropertyValueFactory<E, String>(property));
		tableColumn.setStyle( String.format( "-fx-alignment: %s;", align ));
	}

	/**
	 * @param <E>
	 * @param tvDeviceInfo
	 * @param string
	 * @param string2
	 * @param i
	 */
	@SuppressWarnings("unchecked")
	public static <E> void CheckBoxTableColumn(TableView<E> tableView, String property, String align, int index, EventHandler<ActionEvent> event ) {
		TableColumn<E, Boolean> tcCheckBox	= (TableColumn<E, Boolean>) tableView.getColumns().get(index);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<E, Boolean>(property));
		tcCheckBox.setCellFactory( new Callback<TableColumn<E, Boolean>, TableCell<E, Boolean>>() {
            public TableCell<E, Boolean> call(TableColumn<E, Boolean> p) {
            	CheckBoxTableCellEx<E, Boolean> ckCell = new CheckBoxTableCellEx<E, Boolean>(); 
            	if( event != null ) {
            		ckCell.setOnAction( event );
            	}
            	return ckCell;
            }
        });
	}
}
