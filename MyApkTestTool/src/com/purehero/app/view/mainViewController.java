package com.purehero.app.view;

import java.util.List;

import com.purehero.app.DeviceChangeListener;
import com.purehero.app.DeviceInfo;
import com.purehero.app.MainClass;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class mainViewController implements DeviceChangeListener, EventHandler<ActionEvent> {
	@FXML
	private TableView<DeviceInfo> tvDeviceInfo;
	
	@FXML
    public void initialize() throws Exception {
		refresh_device_infos();
		
		MainClass.instance.getADB().setDeviceChangeListener( this );
	}
	
	@SuppressWarnings("unchecked")
	private void refresh_device_infos() {
		List<DeviceInfo> devices = MainClass.instance.getDevices();

		int column_index = 0;
		
		TableColumn<DeviceInfo, Boolean> tcCheckBox	= (TableColumn<DeviceInfo, Boolean>) tvDeviceInfo.getColumns().get(column_index++);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Boolean>("selected"));
		tcCheckBox.setCellFactory( new Callback<TableColumn<DeviceInfo, Boolean>, TableCell<DeviceInfo, Boolean>>() {
            public TableCell<DeviceInfo, Boolean> call(TableColumn<DeviceInfo, Boolean> p) {
            	CheckBoxTableCell<DeviceInfo, Boolean> ckCell = new CheckBoxTableCell<DeviceInfo, Boolean>(); 
            	ckCell.setOnAction( mainViewController.this );
            	return ckCell;
            }
        });
				
		// 모델명
		TableColumn<DeviceInfo, String> tcModelName 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcModelName.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("modelName"));
		tcModelName.setStyle("-fx-alignment: CENTER;");
		
		// 시리얼 번호
		TableColumn<DeviceInfo, String> tcSerialNumber 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcSerialNumber.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("serialNumber"));
		tcSerialNumber.setStyle("-fx-alignment: CENTER;");
		
		// OS 버전
		TableColumn<DeviceInfo, String> tcOsVersion 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOsVersion.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("osVersion"));
		tcOsVersion.setStyle("-fx-alignment: CENTER;");
				
		// 베터리 레벨
		TableColumn<DeviceInfo, Integer> tcBatteryLevel 	= (TableColumn<DeviceInfo, Integer>) tvDeviceInfo.getColumns().get(column_index++);
		tcBatteryLevel.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Integer>("batteryLevel"));
		tcBatteryLevel.setStyle("-fx-alignment: CENTER;");
		
		// 연결 상태
		TableColumn<DeviceInfo, String> tcState 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcState.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("state"));
		tcState.setStyle("-fx-alignment: CENTER;");
		
		// 실행 횟수
		TableColumn<DeviceInfo, Integer> tcCount 	= (TableColumn<DeviceInfo, Integer>) tvDeviceInfo.getColumns().get(column_index++);
		tcCount.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Integer>("count"));
		tcCount.setStyle("-fx-alignment: CENTER;");
		
		// 오류발생 횟수
		TableColumn<DeviceInfo, Integer> tcErrorCount 	= (TableColumn<DeviceInfo, Integer>) tvDeviceInfo.getColumns().get(column_index++);
		tcErrorCount.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Integer>("errorCount"));
		tcErrorCount.setStyle("-fx-alignment: CENTER;");
		
		// 비고
		TableColumn<DeviceInfo, String> tcCommant 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcCommant.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("commant"));
		tcCommant.setStyle("-fx-alignment: CENTER;");
		
		ObservableList<DeviceInfo> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
	}
	
	@FXML
	@Override
	public void handle(ActionEvent event) {
		Object obj = event.getSource();
		if( obj instanceof CheckBoxTableCell ) {
			CheckBoxTableCell<?, ?> ckCell = ( CheckBoxTableCell<?, ?> ) obj;
			
			DeviceInfo deviceInfo = tvDeviceInfo.getItems().get( ckCell.getIndex() );
			deviceInfo.setSelected( !deviceInfo.getSelected());
			
		} else if( obj instanceof CheckBox ) {
			CheckBox cb = ( CheckBox ) obj;
			
			switch( cb.getId()) {
			case "ID_CHECKBOX_SELECT_ALL_DEVICE" :
				boolean isSelected = cb.isSelected();
				for( DeviceInfo deviceInfo : tvDeviceInfo.getItems() ) {
					deviceInfo.setSelected( isSelected );
				}
				tvDeviceInfo.refresh();					
				break;
			}
		}
		
	}
	
	@Override
	public void OnDeviceChangedEvent() {
		refresh_device_infos();
	}
	
	
	class CheckBoxTableCell<S, T> extends TableCell<S, T> implements EventHandler<ActionEvent> {
		private final CheckBox checkBox;
	    private ObservableValue<T> ov;

	    public CheckBoxTableCell() {
	        this.checkBox = new CheckBox();
	        this.checkBox.setAlignment(Pos.CENTER);

	        setAlignment(Pos.CENTER);
	        setGraphic(checkBox);
	        
	        checkBox.setOnAction(this);
	        chClickEvent = new ActionEvent( this, null );
	    } 

	    ActionEvent chClickEvent = null;
	    EventHandler<ActionEvent> actionEvent = null;
	    public void setOnAction( EventHandler<ActionEvent> arg0) {
	    	actionEvent = arg0;	  
	    }
	    
	    @Override 
	    public void updateItem(T item, boolean empty) {
	        super.updateItem(item, empty);
	        
	        if (empty) {
	            setText(null);
	            setGraphic(null);
	            
	        } else {
	            setGraphic(checkBox);
	            if (ov instanceof BooleanProperty) {
	                checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
	            }
	            
	            ov = getTableColumn().getCellObservableValue(getIndex());
	            if (ov instanceof BooleanProperty) {
	                checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
	            }
	            
	            checkBox.setSelected((Boolean)item );
	        }        
	    }

		@Override
		public void handle(ActionEvent arg0) {
			if( actionEvent != null ) {
				actionEvent.handle(chClickEvent);
			}
		}
	}


	
}
