package com.purehero.atm.v3.view;

import java.util.ArrayList;
import java.util.List;

import com.purehero.atm.v3.model.AdbV3;
import com.purehero.atm.v3.model.DeviceInfo;
import com.purehero.atm.v3.model.UtilV3;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

public class deviceListViewController {

	@FXML
	private TableView<DeviceInfo> tvDeviceInfo;
	
	@FXML
    public void initialize() {
		instance = this;
		
		refresh_device_infos();
	}
	
	public static deviceListViewController instance = null;
	
	@SuppressWarnings("unchecked")
	private void refresh_device_infos() {
		AdbV3.debugLog = true;
		ArrayList<DeviceInfo> devices = AdbV3.getDevices();

		int column_index = 0;
		
		TableColumn<DeviceInfo, Boolean> tcCheckBox	= (TableColumn<DeviceInfo, Boolean>) tvDeviceInfo.getColumns().get(column_index++);
		tcCheckBox.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Boolean>("selected"));
		tcCheckBox.setCellFactory( new Callback<TableColumn<DeviceInfo, Boolean>, TableCell<DeviceInfo, Boolean>>() {
            public TableCell<DeviceInfo, Boolean> call(TableColumn<DeviceInfo, Boolean> p) {
            	return new CheckBoxTableCell<DeviceInfo, Boolean>();            
            }
        });
		
		
		TableColumn<DeviceInfo, String> tcModelName 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcModelName.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("model"));
		tcModelName.setStyle("-fx-alignment: CENTER;");
				
		TableColumn<DeviceInfo, String> tcSerial 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcSerial.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("serialNumber"));
		tcSerial.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcOsVersion 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOsVersion.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("os_ver"));
		tcOsVersion.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcDisplayOn 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcDisplayOn.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("displayOn"));
		tcDisplayOn.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcBatteryLvl 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcBatteryLvl.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("batteryLevel"));
		tcBatteryLvl.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcStatus		= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcStatus.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("status"));
		tcStatus.setStyle("-fx-alignment: CENTER;");
		
		ObservableList<DeviceInfo> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
	}

	@FXML
	private void mouse_event_handler( MouseEvent e) {
	}
	
	@FXML
	private void action_event_handler( ActionEvent e) {
		Object obj = e.getSource();
		
		String ctrl_id = null;
		if( obj instanceof Button) 			ctrl_id = (( Button ) obj ).getId();
		else if( obj instanceof MenuItem ) 	ctrl_id = (( MenuItem ) obj).getId();
				
		if( ctrl_id == null ) return;
		
		switch( ctrl_id ) {
		case "ID_MENU_OPEN_SHELL" 		: open_adb_shell(); break; 
		case "ID_MENU_LIST_REPLACE" 	: refresh_device_infos(); break;
		}
	}
	
	private final String MSG_NOT_SELECTED_DEVICE = "선택된 단말기가 없습니다. \n단말기는 선택하신 후 다시 시도해 주세요.";
	private void open_adb_shell() {
		DeviceInfo deviceInfo = getSelectedDeviceItem();
		if( deviceInfo == null ) {
			UtilV3.alertWindow( "Information", MSG_NOT_SELECTED_DEVICE, AlertType.WARNING );
			return;
		}
		
		AdbV3.OpenShell( deviceInfo );
	}

	/**
	 * 디바이스 정보창에 현재 선택된 디바이스의 객체를 반환 합니다. 선택된 디바이스가 없으면 null 반환
	 * 
	 * @return
	 */
	public DeviceInfo getSelectedDeviceItem() {
		return tvDeviceInfo.getSelectionModel().getSelectedItem();		
	}

	/**
	 * 
	 */
	public void updateDeviceInfoList() {
		tvDeviceInfo.refresh();
	}
	
	/**
	 * 디바이스 정보창에 check box 가 체크된 객체들을 반환 합니다. 
	 * @return
	 */
	public List<DeviceInfo> getCheckedDeviceInfo() {
		List<DeviceInfo> ret = new ArrayList<DeviceInfo>();
		
		ObservableList<DeviceInfo> deviceInfoData = tvDeviceInfo.getItems();
		for( DeviceInfo deviceInfo : deviceInfoData ) {
			if( deviceInfo.getSelected()) ret.add( deviceInfo );
		}
		return ret;
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
			DeviceInfo device = ( DeviceInfo ) getTableColumn().getTableView().getItems().get(getIndex());
			if( device != null ) {
				device.setSelected( !device.getSelected());
			}
		}
	}
}
