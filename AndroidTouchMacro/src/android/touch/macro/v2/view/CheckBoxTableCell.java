package android.touch.macro.v2.view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;

public class CheckBoxTableCell<S, T> extends TableCell<S, T> implements EventHandler<ActionEvent> {
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
		//this.getTableView().get
		System.out.println( getIndex());
		ov = getTableColumn().getCellObservableValue(getIndex());
		
		System.out.println( ov.toString());
		checkBox.selectedProperty().bindBidirectional(new SimpleBooleanProperty(!checkBox.isSelected()));
	}
}