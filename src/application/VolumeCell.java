package application;

import java.util.ArrayList;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import model.Issue;
import scenes.IssuePreview;
import scenes.VolumePreview;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class VolumeCell extends TreeItem {
	private VolumePreview vp;
	private ContextMenu editMenu;
	boolean filled = false;

	/**
	 * 
	 * @param vPre
	 */
	
	public VolumeCell(VolumePreview vPre) {
		super(vPre);
		vp = vPre;
		Label label = new Label();
		MenuItem item1 = new MenuItem("Menu Item 1");
		item1.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				label.setText("Select Menu Item 1");
			}
		});
	}

	/**
	 * Loads all the issues associated with this volume
	 * 
	 * @param allIssues
	 *            - ArrayList of all the issues
	 */
	public void setIssues(ArrayList<Issue> allIssues) {
		for (Issue i : allIssues) {
			if (i.getVolumeID().equals(vp.getVolume().getID())) {
				// System.out.println("Adding " + i);
				// Had to insert this inorder to prevent multiple threads
				// writing to children
				Platform.runLater(new Runnable() {
					public void run() {
						getChildren().add(new TreeItem<IssuePreview>(new IssuePreview(i)));
					}
				});
			}
		}
		filled = true;
	}

	/**
	 * determines whether this volume cell has had the setIssues method called
	 * @return boolean of wether the setIssues method has been called
	 */
	public boolean isFilled() {
		return filled;
	}
}
