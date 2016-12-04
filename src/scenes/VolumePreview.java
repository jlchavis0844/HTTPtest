package scenes;


import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import application.Main;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.HBox;
import localDB.LocalDB;
import model.Issue;
import model.Volume;

public class VolumePreview extends HBox{
	private Volume vol;
	private ImageView thumb;
	private Label infoLbl;

	/**
	 * Makes a preview of a volume, to load the image, us setImage()
	 * @param rhVol
	 * @param allIssues
	 */
	public VolumePreview(Volume rhVol, List<Issue> allIssues) {
		super();

		vol = rhVol;
		long start = System.currentTimeMillis();
		thumb = new ImageView();
		//System.out.println("Image fetch took :" + (System.currentTimeMillis() - start));
		thumb.setFitHeight(50);
		thumb.setFitWidth(33);

		int counter = 0;
		for(Issue i : allIssues){
			if(i.getVolumeID().equals(vol.getID())){
				counter++;
			}
		}

		String info = vol.getName() + "\n" + vol.getPublisher()  + "     " + vol.getStartYear() +
				"\n" + counter + " out of " + vol.getCountofIssue() + " in collection";

		infoLbl = new Label(info);
		getChildren().addAll(thumb, infoLbl);
		
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
            	infoLbl.setText("VOLUME DELETED RESET THE PROGRAM FOR UPDATE");
            	LocalDB.deleteVolumeByID(rhVol.getID());
            	Main.afterVolumeUpdate(vol);
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

	public String getVolName(){
		return vol.getName();
	}

	public void update(List<Issue> allIssues){
		int counter = 0;
		for(Issue i : allIssues){
			if(i.getVolumeID().equals(vol.getID())){
				counter++;
			}

			String info = vol.getName() + "\n" + vol.getPublisher()  + "     " + vol.getStartYear() +
					"\n" + counter + " out of " + vol.getCountofIssue() + " in collection";
			infoLbl.setText(info);
			getChildren().clear();
			getChildren().addAll(thumb, infoLbl);
		}
	}

	/**
	 * @return the vol
	 */
	public Volume getVolume() {
		return vol;
	}

	/**
	 * @param vol the vol to set
	 */
	public void setVolume(Volume vol) {
		this.vol = vol;
	}

	/**
	 * loads the image
	 */
	public void setImage(){
		if(thumb.getImage() == null){
			BufferedImage bi = vol.getImage("thumb");
			Image image = SwingFXUtils.toFXImage(bi, null);
			thumb.setImage(image);
		}
	}
}
