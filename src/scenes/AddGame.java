package scenes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.*;
import requests.GBrequest;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AddGame {
	private Button removeButton;
	private Button srchButton;
	private Button addButton;
	private Button doneButton;
	private BorderPane layout;
	private HBox topBox;
	private TextField input;
	private TextField platform;
	private ScrollPane scPane;
	private ListView<GameResult> list;
	private Map<String,SearchGameDetail> details;
	private List<Game> addList;
	private Stage window;
	private final ProgressBar pb;
	private double added = 0.0;
	private SearchGameDetail currGameDetail;
	private Map<String, Game> addMap;
	private Game selectedGame;

	public AddGame(List<Game> addList) {
		addMap = new HashMap<String, Game>();
		this.addList = addList;
		details = new HashMap<String,SearchGameDetail>();
		pb = new ProgressBar(0.0);
		pb.setPrefWidth(200);
		pb.setVisible(false);
		
		window = new Stage();
		//window.initStyle(StageStyle.UNDECORATED);
		window.setTitle("Add Games!");
		window.initModality(Modality.APPLICATION_MODAL);

		window.setOnCloseRequest(e -> {
			e.consume();
			closeThis();
		});
		
		layout = new BorderPane();
		
		topBox = new HBox(10);
		list = new ListView<GameResult>();
		list.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<GameResult>() {
			@Override
			public void changed(ObservableValue<? extends GameResult> observable, GameResult oldValue,
					GameResult newValue) {
				Long start; // timing purposes
				start = System.currentTimeMillis();// mark start
				String gID = null;
				GameResult temp = list.getSelectionModel().getSelectedItem();
				selectedGame = temp.getGame();
				if (temp != null) {
					gID = temp.getGameID();
				}
				
				if(details.containsKey(gID)){
					currGameDetail = details.get(gID);
				} else {
					currGameDetail = new SearchGameDetail(temp.getGame(), false);
					details.put(gID, currGameDetail);				
				}
				
				System.out.println("Fetching " + gID);
				//layout.setRight(new ImageView(temp.getGame().getRemoteMedium()));
				
				layout.setCenter(currGameDetail);
				layout.setRight(currGameDetail.getWebDesc());
				
				if(addMap.containsKey(gID)){
					addButton.setDisable(true);
					removeButton.setDisable(false);
				} else {
					addButton.setDisable(false);
					removeButton.setDisable(true);
				}
				System.out.println("total time to load detail: " + (System.currentTimeMillis() - start));
			}
		});

		// leftBox = new VBox();
		// scPane = new ScrollPane(leftBox);

		scPane = new ScrollPane();
		scPane.setMaxWidth(1200);
		scPane.setMinWidth(500);
		scPane.setMinHeight(500);
		scPane.setFitToWidth(true);
		scPane.setFitToHeight(true);
		scPane.setStyle("-fx-background: rgb(80,80,80);");

		input = new TextField();
		input.setPromptText("Enter Search Terms");
		input.setMinWidth(200);
		input.setOnKeyPressed(e -> {
			System.out.println(e.getCode());
			if (e.getCode() == KeyCode.ENTER) {
				e.consume();
				System.out.println("Enter was pressed");
				srchButton.fire();
			}
		});

		platform = new TextField();
		platform.setPromptText("Platform Name");
		platform.setMinWidth(200);
		platform.setOnKeyPressed(e -> {
			System.out.println(e.getCode());
			if (e.getCode() == KeyCode.ENTER) {
				e.consume();
				System.out.println("Enter was pressed");
				srchButton.fire();
			}
		});

		srchButton = new Button("Search");
		addButton = new Button("Add Game");
		removeButton = new Button("Remove");
		//new Button("Delete");
		doneButton = new Button("Done Adding");
		addButton.setDisable(true);
		removeButton.setDisable(true);

		addButton.setOnAction(e -> {
			if (selectedGame != null) {
				System.out.println("Adding " + selectedGame.getName());
			} else
				System.out.println("game is null");

			addMap.put(selectedGame.getID(), selectedGame);
			addButton.setDisable(true);
			removeButton.setDisable(false);
		});

		srchButton.setOnAction(e -> {
			if (!input.getText().equals("")) {
				pb.setVisible(true);
				addButton.setDisable(true);
				scPane.setContent(list);
				list.setMaxWidth(scPane.getWidth()-5);
				list.setMaxHeight(scPane.getHeight()-5);
				System.out.println(input.getText());
				gameSearch(input.getText(), platform.getText());
				Platform.runLater(() -> {
					list.requestFocus();
				});
			}

		});

		removeButton.setOnAction(e -> {
			addMap.remove(selectedGame.getID());
			removeButton.setDisable(true);
			addButton.setDisable(false);
			Platform.runLater(() -> {
				list.requestFocus();
			});
		});

		doneButton.setOnAction(e -> {
			closeThis();
		});

		topBox.getChildren().addAll(input, platform, srchButton, addButton, removeButton, doneButton, pb);

		// layout.setPadding(new javafx.geometry.Insets(10));
		layout.setTop(topBox);
		layout.setLeft(scPane);
		BorderPane.setMargin(scPane, new javafx.geometry.Insets(10));
		BorderPane.setMargin(topBox, new javafx.geometry.Insets(10));

		Scene scene = new Scene(layout, 1900, 1050);
		String style = getClass().getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		window.setScene(scene);
		window.setMaximized(true);
		window.showAndWait();
	}

	private void gameSearch(String term, String plat) {
		ArrayList<Game> gameArr;

//		if (plat.equals("") || plat == null) {
//			gameArr = GBrequest.searchGame(term, plat);
//		} else {
//			gameArr = GBrequest.searchGame(term);
//		}

		gameArr = GBrequest.searchGame(term);
		
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				List<GameResult> results = new ArrayList<GameResult>();
				int resSize = gameArr.size();
				added = 0;
				for (Game g : gameArr) {
					// leftBox.getChildren().add(new VolumeButton(v, this));
					System.out.println("Adding " + g.getID());
					results.add(new GameResult(g));
					added++;
					Platform.runLater(() -> {
						double prog = added / resSize;
						System.out.println("progress = " + added + " / " + (double) resSize + " = " + prog);
						pb.setProgress(prog);
						if (prog == 1) {
							pb.setVisible(false);
						}
					});
				}
				
				ObservableList<GameResult> obvRes = FXCollections.observableList(results);
				list.setItems(obvRes);
				return null;
			}
		};
		
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}

	public void closeThis() {
		boolean temp = ConfirmBox.display("Close Add Game?", "Are you done adding games?");
		if (temp) {
			window.close();
			if(currGameDetail != null){
			currGameDetail.killVideo();
			addList.clear();
			addList.addAll(addMap.values());
			}
		}
	}
}