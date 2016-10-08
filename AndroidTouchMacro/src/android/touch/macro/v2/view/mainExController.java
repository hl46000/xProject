package android.touch.macro.v2.view;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class mainExController {
	
	@FXML
	private TextArea taConsole;
	
	@FXML
    public void initialize() {
		taConsole.insertText( 0, "insert" );
	}
}
