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

/*
 * the screen the show up after user close the added scene
 * right now the code will run through the list of user selected issues
 * any issue that is already in the collection will be ignore
 * issue that is not in collection will be display and put in 
 * an arraylist of issue for adding uppon closing the scene
 * or clicking on continue button.
 * 
 *  cancel work, back and newadd doesn't and i don't know why.
 */
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
		ArrayList<Issue> willAdd = new ArrayList<Issue>();
		IssuePreview ip = null;
		
		for(Issue i: addedCopy){
			ip = new IssuePreview(i);
			if(LocalDB.exists(i.getID(), LocalDB.ISSUE)){
				ip.setAddInfo("Already in collection");
				added.remove(i);
			}
			// the list of issue preview for display to the user
			addList.add(ip);
			
			//the list of issue that we will actually add after operation is over
			willAdd.add(i);
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
		
		// user cancel what they selected
		cancel.setOnAction(e -> {
			stage.close();
			added.clear();
		});
		
		go.setOnAction(e -> {
			// add the issue to DB
			willAdd.forEach(i -> {
				System.out.println(i);
			});
			for(Issue i: willAdd){

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
			//then close the stage
			stage.close();
		});

		// if user close the stage and ignore the continue button we still add what need to be add
		stage.setOnCloseRequest(e ->{
			for(Issue i: willAdd){

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
		});
		
		HBox bottom = new HBox(5);
		bottom.getChildren().addAll(back, newAdd, cancel, go);
		layout.setBottom(bottom);
		Scene scene = new Scene(layout);
		stage.setScene(scene);
		stage.showAndWait();
	}
	
}