package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import model.*;
import requests.CVrequest;
import requests.CVrequestAsync;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddComic {
	private Button srchButton;
	private Button addButton;
	private BorderPane layout;
	private HBox topBox;
	private TextField input;
	private TextField pubName;
	private ScrollPane scPane;
	ListView<VolResult> list;
	ListView<IssueResult> issueList;
	ArrayList<Issue> addList;


	public AddComic(ArrayList<Issue> added) {
		addList = added;
		Stage window = new Stage();
		window.setTitle("Add Comics!");
		window.initModality(Modality.APPLICATION_MODAL);
		window.setOnCloseRequest(e ->{
			e.consume();
			boolean temp = ConfirmBox.display("Close Add Comic?", "Are you done adding comics?");
			if(temp){
				window.close();
			}
		});

		layout = new BorderPane();
		topBox = new HBox(10);
		list = new ListView<VolResult>();
		list.setMinWidth(1000);
		list.setMaxWidth(1200);
		list.setMinHeight(800);

		issueList = new ListView<IssueResult>();
		issueList.setMinWidth(1000);
		issueList.setMaxWidth(1200);

		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<VolResult>() {
			@Override
			public void changed(ObservableValue<? extends VolResult> observable, VolResult oldValue, VolResult newValue) {
				String vID = null;
				VolResult temp = list.getSelectionModel().getSelectedItem();

				if(temp != null){
					vID = temp.getVolID();
				}

				System.out.println("Fetching " + vID);
				getIssues(vID);		
			}
		});

		//leftBox = new VBox();
		//scPane = new ScrollPane(leftBox);

		scPane = new ScrollPane();
		scPane.setMaxWidth(1200);
		scPane.setMinWidth(1000);
		scPane.setMinHeight(800);
		scPane.setFitToWidth(true);

		input = new TextField();
		input.setPromptText("Enter Search Terms");
		input.setMinWidth(600);

		pubName = new TextField();
		pubName.setPromptText("Publisher Name");
		pubName.setMinWidth(200);

		srchButton = new Button("Search");
		addButton = new Button("Add Issue");
		addButton.setVisible(false);
		addButton.setOnAction(e -> {
			Issue iSel = issueList.getSelectionModel().getSelectedItem().getIssue();
			if(iSel != null){
				System.out.println("Adding "+ iSel.getVolumeName() + " #" + iSel.getIssueNum());
			} else System.out.println("issue is null");

			addList.add(iSel);
			addButton.setVisible(false);
		});


		srchButton.setOnAction(e -> {
			addButton.setVisible(false);
			scPane.setContent(list);
			System.out.println(input.getText());
			volSearch(input.getText(), pubName.getText());
		});

		topBox.getChildren().addAll(input, pubName, srchButton, addButton);

		//layout.setPadding(new javafx.geometry.Insets(10));
		layout.setTop(topBox);
		layout.setLeft(scPane);
		BorderPane.setMargin(scPane, new javafx.geometry.Insets(10));
		BorderPane.setMargin(topBox, new javafx.geometry.Insets(10));

		Scene scene = new Scene(layout, 1900, 1050);
		String style= getClass().getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		window.setScene(scene);
		window.showAndWait();
	}

	private void volSearch(String term, String pub){
		ArrayList<Volume> vols;

		if(pub.equals("") || pub == null){
			vols = CVrequest.searchVolume(term);
		} else {
			vols = CVrequest.searchVolume(term, pub);
		}

		List<VolResult> results = new ArrayList<VolResult>();
		AtomicInteger adds = new AtomicInteger(0);
		for(Volume v: vols){
			//leftBox.getChildren().add(new VolumeButton(v, this));
			new Thread(){ public void run(){
				results.add(new VolResult(v));
				System.out.println("tread #" + this.getId()+ " added # " + adds.incrementAndGet());
			}}.start();
		}
		
		int volSize = vols.size();
		while(volSize != adds.get()){};//wait for threads to catchup
		
		ObservableList<VolResult> obvRes = FXCollections.observableList(results);
		list.setItems(obvRes);
	}

	public void  getIssues(String volID){
		ArrayList<Issue> issues = CVrequest.getVolumeIDs(volID);
		List<IssueResult> results = new ArrayList<IssueResult>();

		for(Issue i: issues){
			results.add(new IssueResult(i));
		}

		ObservableList<IssueResult> obvRes = FXCollections.observableList(results);

		issueList = new ListView<IssueResult>();
		issueList.setItems(obvRes);
		issueList.setMinWidth(scPane.getWidth());
		issueList.setMinHeight(700);
		issueList.setPrefSize(600, 900);
		issueList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<IssueResult>() {

			@Override
			public void changed(ObservableValue<? extends IssueResult> observable, IssueResult oldValue,
					IssueResult newValue) {
				layout.setCenter(new DetailView(newValue.getIssue()));
				addButton.setVisible(true);
			}
		});
		scPane.setContent(issueList);
		System.out.println("scPane: " + scPane.getHeight() + "\tissueList " + issueList.getHeight());
	}

}
