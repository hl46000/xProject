package com.purehero.fx.app.view.apk_list;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import com.android.ddmlib.IShellOutputReceiver;
import com.purehero.android.DeviceInfo;
import com.purehero.common.io.IRelease;
import com.purehero.fx.app.MainClass;
import com.purehero.fx.app.view.MainViewController;
import com.purehero.fx.app.view.WorkThread;
import com.purehero.fx.app.view.test.ApkFileInfo;
import com.purehero.fx.common.TableViewUtils;

public class DeviceApkListViewController implements EventHandler<ActionEvent>, IRelease {
	
	@FXML
	private TableView<ApkFileInfo> tbApkInfoList;
	
	private ObservableList<ApkFileInfo> apkFileInfos = FXCollections.observableList( new ArrayList<ApkFileInfo>());
	
	@FXML
    public void initialize() {
		initTableView();
	}

	private void initTableView() {
		TableViewUtils.StringTableColumn( tbApkInfoList, "appName", 	"CENTER", 0 );
		TableViewUtils.StringTableColumn( tbApkInfoList, "packageName", 	"CENTER", 1 );
		TableViewUtils.StringTableColumn( tbApkInfoList, "apkFilePath", 	"CENTER", 1 );
		
		tbApkInfoList.setItems( apkFileInfos );	
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handle(ActionEvent arg0) {
		Object obj = arg0.getSource();
		
		if( obj instanceof Button ) {
			OnButtonClicked( ( Button ) obj );
		}
	}
	
	private void OnButtonClicked( Button button ) {
		switch( button.getId()) {
		case "ID_BTN_LIST_UPDATE" : OnButtonListUpdate(); break;
		}
	}

	private void OnButtonListUpdate() {
		DeviceInfo device_info = mainViewController.getSelectedDeviceInfo();
		if( device_info == null ) return;
		
		MainClass.instance.runThreadPool( new WorkThread( device_info, "pm list package -f", DeviceApkList_ShellOutputReceiver ));
	}

	private IShellOutputReceiver DeviceApkList_ShellOutputReceiver = new IShellOutputReceiver() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
				
		@Override
		public void addOutput(byte[] data, int offset, int length) {
			baos.write( data, offset, length );
		}

		@Override
		public void flush() { 
			String contents = baos.toString();
			String lines [] = contents.split( System.lineSeparator());
			
			for( String line : lines ) {
				System.out.println( line );
			}

			try {
				baos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public boolean isCancelled() { return false; }
	};
	
	MainViewController mainViewController = null;
	public void setMainViewController(MainViewController mainViewController) {
		this.mainViewController = mainViewController;
	}
}
