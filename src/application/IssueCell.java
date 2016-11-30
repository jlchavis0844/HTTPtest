package application;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import scenes.IssuePreview;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class IssueCell extends TreeItem {
	private IssuePreview ip;
	private ContextMenu editMenu;
	boolean filled = false;

	public IssueCell(IssuePreview iPre) {
		super(iPre);
		ip = iPre;
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
