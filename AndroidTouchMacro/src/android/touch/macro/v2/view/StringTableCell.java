package android.touch.macro.v2.view;

import javafx.geometry.Pos;
import javafx.scene.control.TableCell;

public class StringTableCell<S, T> extends TableCell<S, T> {
    public StringTableCell() {
        setAlignment(Pos.CENTER);
        setGraphic(null);
        
        setStyle("-fx-alignment: CENTER-LEFT;");
    } 

    @Override 
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        
        setText( empty ? null : getString());
        setGraphic( null );
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}