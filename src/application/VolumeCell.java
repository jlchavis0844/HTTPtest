package application;

import java.util.ArrayList;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import localDB.LocalDB;
import model.Issue;
import model.Volume;
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
	 * Holds volume preview in the collections treeview
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
	public boolean setIssues(ArrayList<Issue> allIssues) {

		ArrayList<Issue> volumeIssues = new ArrayList<>();
		for(Issue i : allIssues){
			if (i.getVolumeID().equals(vp.getVolume().getID())) {//check by volume ID
				volumeIssues.add(i);
			}
		}
		if(volumeIssues.isEmpty())
			return false;
		LocalDB.sortIssuesByIssueNum(volumeIssues, false);//sort the issues
		
		for(Issue i: volumeIssues){//add issues to the volume preview in the sorted order
			//System.out.println("trying: " + i.getVolumeName() + " #" + i.getIssueNum());

			getChildren().add(new TreeItem<IssuePreview>(new IssuePreview(i)));
		}
		filled = true;
		return true;
	}

	/**
	 * determines whether this volume cell has had the setIssues method called
	 * @return boolean of wether the setIssues method has been called
	 */
	public boolean isFilled() {
		return filled;
	}
	
	public String getVolumeName(){
		return vp.getVolName();
	}
	
	public String getVolumeID(){
		return vp.getVolume().getID();
	}
	
	public Volume getVolume(){
		return vp.getVolume();
	}
	
	public VolumePreview getVolPreview(){
		return vp;
	}

}
