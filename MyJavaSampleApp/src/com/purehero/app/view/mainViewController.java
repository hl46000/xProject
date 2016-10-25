package com.purehero.app.view;

import java.util.List;

import com.android.ddmlib.IDevice;
import com.purehero.app.DeviceChangeListener;
import com.purehero.app.DeviceInfo;
import com.purehero.app.MainClass;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class mainViewController implements DeviceChangeListener {
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
		column_index++;
				
		TableColumn<DeviceInfo, String> tcModelName 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcModelName.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("modelName"));
		tcModelName.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcSerialNumber 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcSerialNumber.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("serialNumber"));
		tcSerialNumber.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcOsVersion 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcOsVersion.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("osVersion"));
		tcOsVersion.setStyle("-fx-alignment: CENTER;");
				
		column_index++;
		
		TableColumn<DeviceInfo, Integer> tcBatteryLevel 	= (TableColumn<DeviceInfo, Integer>) tvDeviceInfo.getColumns().get(column_index++);
		tcBatteryLevel.setCellValueFactory( new PropertyValueFactory<DeviceInfo, Integer>("batteryLevel"));
		tcBatteryLevel.setStyle("-fx-alignment: CENTER;");
		
		TableColumn<DeviceInfo, String> tcState 	= (TableColumn<DeviceInfo, String>) tvDeviceInfo.getColumns().get(column_index++);
		tcState.setCellValueFactory( new PropertyValueFactory<DeviceInfo, String>("state"));
		tcState.setStyle("-fx-alignment: CENTER;");
		
		ObservableList<DeviceInfo> deviceInfoData = FXCollections.observableArrayList( devices );		
		tvDeviceInfo.setItems( deviceInfoData );
	}
	
	@FXML
	private void action_event_handler( ActionEvent e) {
	}

	@Override
	public void OnDeviceChangedEvent() {
		refresh_device_infos();
	}
}
