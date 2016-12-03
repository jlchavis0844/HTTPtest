package scenes;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import localDB.LocalDB;
import model.Issue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;

public class IssuePreview extends HBox {
	private Issue issue;
	private ImageView thumb;
	private BufferedImage bi;
	private Label infoLbl;
	private Label addLabel;

	public IssuePreview(Issue rhIssue) {
		super();

		issue = rhIssue;
		long start = System.currentTimeMillis();
		bi = issue.getImage("thumb");
		Image image = SwingFXUtils.toFXImage(bi, null);
		thumb = new ImageView(image);
		addLabel = new Label();
		addLabel.setTextFill(Color.RED);
		addLabel.setVisible(false);
		/*
		 * System.out.println("Image fetch for " + issue.getVolumeName() + " #"
		 * + issue.getIssueNum() + " took :" + (System.currentTimeMillis() -
		 * start));
		 */
		thumb.setFitHeight(50);
		thumb.setFitWidth(33);

		String info = issue.getVolumeName() + " #" + issue.getIssueNum() + "\nDate: " + issue.getCoverDate();
		infoLbl = new Label(info);
		getChildren().addAll(thumb, infoLbl, addLabel);

		/*
		 * context menu code
		 */
		ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("delete");
        item1.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                // localDB.remove
            	// we need to add remove into localdb
            	// how to updateleft in this class???
            	// updateleft in the main use added list
            	// we got problem here so for now don't update
            	infoLbl.setText("ISSUE DELETED RESET THE PROGRAM FOR UPDATE");
            	LocalDB.deleteIssueByID(rhIssue.getID());
            }
        });
        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(item1);
        // When user right-click issue preview
        setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
 
            @Override
            public void handle(ContextMenuEvent event) {
 
                contextMenu.show(infoLbl, event.getScreenX(), event.getScreenY());
            }
        });
        /*
         * end of context menu code
         */
	}

	public Issue getIssue() {
		return issue;
	}

	public void setAddInfo(String s) {
		addLabel.setText(s);
		addLabel.setVisible(true);
	}
	
	public void setVolumeName(){
		addLabel.setText(issue.getVolumeName());
		addLabel.setVisible(true);
		addLabel.setTextFill(Color.BLACK);
	}

	public BufferedImage getImage() {
		return bi;
	}
}
