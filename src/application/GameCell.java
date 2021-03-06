package application;

import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import scenes.GamePreview;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
/**
 * Class that holds the collection search Results in the treeview
 * @author James
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GameCell extends TreeItem {
	boolean filled = false;

	public GameCell(GamePreview gPre) {
		super(gPre);
		Label label = new Label();
		MenuItem item1 = new MenuItem("Menu Item 1");
		item1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				label.setText("Select Menu Item 1");
			}
		});
	}
}
