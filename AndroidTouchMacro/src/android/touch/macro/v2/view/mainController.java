package android.touch.macro.v2.view;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import android.touch.macro.v2.DataManager;
import android.touch.macro.v2.TouchMacroV2;

public class mainController {
	
	@FXML
	private TabPane tpMainTabPane;
	
	@FXML
	private ContextMenu cmDisplayRotate;
	
	DataManager dataManager = null;
	
	@FXML
    public void initialize() {
		dataManager = TouchMacroV2.instance.getDataManager();
		dataManager.setMainController( this );
		
		try {
			Parent screen_position = FXMLLoader.load(TouchMacroV2.instance.getClass().getResource("view/screen_position.fxml"));
			addMainTabPane( screen_position, "Screen position" );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param parent
	 * @param Title
	 */
	public void addMainTabPane( Parent parent, String Title ) {
		Tab tab = new Tab( Title );
		tab.setContent( parent );
		tpMainTabPane.getTabs().add(tab);		
	}
	
	@FXML
	private void event_handle_action( ActionEvent e) {
	}
	
	/**
	 * 마우스 이벤트를 전달받을 함수
	 * 
	 * @param e
	 */
	@FXML
	private void event_handle_mouse(MouseEvent e) {
	}
}
