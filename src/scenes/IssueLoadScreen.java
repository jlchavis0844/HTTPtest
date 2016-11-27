package scenes;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import localDB.LocalDB;
import model.Issue;

public class IssueLoadScreen {

	public IssueLoadScreen(List<Issue> added, List<Issue> allIssues, List<VolumePreview> volPreviews) {
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);

		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(20));
		stage.setTitle("Progress Controls");
		Label label = new Label("loading new comics...");
		VBox myVBox = new VBox(10);

		ArrayList<IssuePreview> addList = new ArrayList<>();
		ArrayList<Issue> addedCopy = new ArrayList<>(added);
		IssuePreview ip = null;
		
		for(Issue i: addedCopy){
			ip = new IssuePreview(i);
			if(LocalDB.exists(i.getID(), LocalDB.ISSUE)){
				ip.setAddInfo("Already in collection");
				added.remove(i);
			}
			addList.add(ip);
		}
		
		// now add the arraylist of HBox into the VBox
		myVBox.getChildren().addAll(addList);
		//layout.getChildren().add(myVBox);
		// show the VBox on layout
		
		layout.setCenter(myVBox);
		layout.setTop(label);
		
		Button back = new Button("Back to adding comics");
		Button newAdd = new Button("Start add over");
		Button cancel = new Button("Stop and go back to collection");
		Button go = new Button("Continue");
		
		back.setOnAction(e -> {
			stage.close();
			new AddComic(added);
		});
		
		newAdd.setOnAction(e -> {
			stage.close();
			added.clear();
			new AddComic(added);
		});
		
		cancel.setOnAction(e -> {
			stage.close();
			added.clear();
		});
		
		go.setOnAction(e -> {
			stage.close();
		});
		
		HBox bottom = new HBox(5);
		bottom.getChildren().addAll(back, newAdd, cancel, go);
		layout.setBottom(bottom);
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.showAndWait();
		// sleep 1000 msec
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Issue i: added){

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