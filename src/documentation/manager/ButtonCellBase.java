package documentation.manager;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;

public abstract class ButtonCellBase extends TableCell<DocEntry, Button> {

    protected Button bt;
    
    public abstract void process(DocEntry d);
    public ButtonCellBase(String str) {
        bt = new Button(str);
        bt.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                DocEntry d =  (DocEntry) getTableRow().getItem();
                process(d);
//                System.out.println(d.getName());
            }
        });
        setGraphic(bt);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
    }
}
