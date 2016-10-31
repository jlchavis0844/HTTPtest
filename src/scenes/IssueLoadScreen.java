package scenes;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Slider;
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
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setTitle("Progress Controls");
		Label label = new Label("loading new comics...");

		HBox hb = new HBox();
		hb.setSpacing(5);
		hb.setAlignment(Pos.CENTER);
		hb.getChildren().addAll(label);
		scene.setRoot(hb);
		stage.show();
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
