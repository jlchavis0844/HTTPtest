package scenes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import localDB.LocalDB;
import model.Issue;

public class IssueLoadScreen {

	public IssueLoadScreen(ArrayList<Issue> added, ArrayList<Issue> allIssues, List<VolumePreview> volPreviews) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		Group root = new Group();
		
		//Scene scene = new Scene(root);
		BorderPane layout = new BorderPane();
		//Scene scene = new Scene(layout, 1900, 1050);
		
		//stage.setScene(scene);
		stage.setTitle("Progress Controls");
		Label label = new Label("loading new comics...");

		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(label);
		//scene.setRoot(hb);
		//stage.show();
		
		//HBox myHBox = new HBox(10);
		// 1 verticle box for the whole thing
		// for each issue add an Horizontal box
		// the box have the issue icon on the left
		// text in central and add or notadd label on right.
		VBox myVBox = new VBox(10);
		
		//myVBox.getChildren().add(myHBox);
		ArrayList<HBox> myHBoxArr = new ArrayList<HBox>();
		
		// check for the issue in DB and figure out whether 
		// we need to add them or not then create IssueHbox 
		// for each issue and put them in an arraylist of HBox
		// so that we can add all of the HBox in the VBox later on
		ArrayList<Issue> temp = new ArrayList<Issue>();
		ArrayList<Issue> notadd = new ArrayList<Issue>();
		for(Issue i: added){
			if(!(LocalDB.exists(i.getID(), LocalDB.ISSUE))){
				// not in db ok to add
				temp.add(i);
				IssueHbox myhbox = new IssueHbox(i,true);
				myHBoxArr.add(myhbox);
			}
			else{
				notadd.add(i);
				IssueHbox myhbox = new IssueHbox(i,false);
				myHBoxArr.add(myhbox);
			}
		}
		
		// now add the arraylist of HBox into the VBox
		myVBox.getChildren().addAll(myHBoxArr);
		//layout.getChildren().add(myVBox);
		// show the VBox on layout
		
		layout.setCenter(myVBox);
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.show();
		// sleep 1000 msec
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Issue i: temp){

			LocalDB.addIssue(i);
			allIssues.add(i);
			int foundIndex = -1;
			for(int j = 0; j < volPreviews.size(); j++){
				if(volPreviews.get(j).getVolName().equals(i.getVolumeName())){
					foundIndex = j;
					volPreviews.get(j).update(allIssues);
				}
			}

			if(foundIndex == -1){
				volPreviews.add(new VolumePreview(i.getVolume(), allIssues));
			}
			for(VolumePreview pv: volPreviews){
				pv.setImage();
			}

		}
		stage.close();
	}
	
}