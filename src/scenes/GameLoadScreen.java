package scenes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import application.Main;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import localDB.LocalDB;
import model.Game;

/*
 * the screen the show up after user close the added scene
 * right now the code will run through the list of user selected games
 * any game that is already in the collection will be ignore
 * game that is not in collection will be display and put in 
 * an arraylist of game for adding uppon closing the scene
 * or clicking on continue button.
 * 
 *  cancel work, back and newadd doesn't and i don't know why.
 */
public class GameLoadScreen {
	private ArrayList<Game> willAdd;
	private ArrayList<Game> addedCopy;
	private ArrayList<GamePreview> addList;
	private List<Game> added;
	private List<Game> allGames;
	private List<GamePreview> gamePreviews;
	private ProgressBar pb;
	private double progress = 0.0;
	private double cntr = 0.0;
	private HBox bottom;
	private Button back;
	private Button newAdd;
	private Button cancel;
	private Button go;

	public GameLoadScreen(List<Game> addedGames, List<Game> gList,
			List<GamePreview> gpList) {
		pb = new ProgressBar(0.0);
		added = addedGames;
		allGames = gList;
		gamePreviews = gpList;

		Stage stage = new Stage();
		stage.initStyle(StageStyle.TRANSPARENT);
		stage.initModality(Modality.APPLICATION_MODAL);

		BorderPane layout = new BorderPane();
		layout.setPadding(new Insets(20));
		stage.setTitle("Progress Controls");
		Label label = new Label("loading new games...");
		pb.setScaleShape(true);
		VBox myVBox = new VBox();
		myVBox.setPadding(new Insets(20, 10, 20, 10));
		
		addList = new ArrayList<>();
		addedCopy = new ArrayList<>(added);
		willAdd = new ArrayList<Game>();
		GamePreview gp = null;

		for (Game g : addedCopy) {
			gp = new GamePreview(g);
			if (LocalDB.exists(g.getID(), LocalDB.GAME)) {
				gp.setAddInfo("Already in collection");
				added.remove(g);
			}
			// the list of game preview for display to the user
			addList.add(gp);

			// the list of game that we will actually add after operation is
			// over
			willAdd.add(g);
		}

		// now add the arraylist of HBox into the VBox
		myVBox.getChildren().addAll(addList);
		// layout.getChildren().add(myVBox);
		// show the VBox on layout

		layout.setCenter(myVBox);
		layout.setTop(pb);
		pb.setPrefWidth(400);
		pb.setVisible(false);
		BorderPane.setAlignment(pb, Pos.CENTER);

		back = new Button("Back to adding games");
		newAdd = new Button("Start add over");
		cancel = new Button("Stop and go back to collection");
		go = new Button("Continue");

		back.setOnAction(e -> {
			stage.close();
			new AddGame(added);
		});

		newAdd.setOnAction(e -> {
			stage.close();
			added.clear();
			new AddGame(added);
		});

		// user cancel what they selected
		cancel.setOnAction(e -> {
			stage.close();
			added.clear();
		});

		go.setOnAction(e -> {
			// add the game to DB
			willAdd.forEach(i -> {
				System.out.println(i);
			});
			pb.setVisible(true);
			bottom.getChildren().clear();
			addGames(stage);

			// then close the stage
			if (cntr == willAdd.size()) {
				System.out.println("++++++++++++++++++++++++++++++++closing load screen");
				stage.close();
			} else {
				System.out.println("---------------------------------not there yes\t");
			}
		});

		// if user close the stage and ignore the continue button we still add
		// what need to be add
		stage.setOnCloseRequest(e -> {
			addGames(stage);
		});

		bottom = new HBox(5);
		bottom.getChildren().addAll(back, newAdd, cancel, go);
		layout.setBottom(bottom);
		layout.setStyle("-fx-border-radius: 20 20 20 20; " 
				+ "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; "
				+ "-fx-border-color: #BBBBBB;");
		Scene scene = new Scene(layout);
		scene.setFill(Color.TRANSPARENT);
		String style = getClass().getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		stage.setScene(scene);
		stage.showAndWait();
	}

	public void addGames(Stage stage) {
		Task task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				for (Game g : willAdd) {

					LocalDB.addGame(g);
					allGames.add(g);
					int foundIndex = -1;
					for (int j = 0; j < gamePreviews.size(); j++) {
						if (gamePreviews.get(j).getGame().getID().equals(g.getID())) {
							foundIndex = j;
						}
					}

					if (foundIndex == -1) {
						gamePreviews.add(new GamePreview(g));
					}

					cntr++;
					progress = cntr / willAdd.size();
					System.out.println(progress);
					Platform.runLater(() -> {
						pb.setProgress(progress);

					});
				}

				if (cntr == willAdd.size()) {
					System.out.println("Added:" + cntr);
					//TimeUnit.SECONDS.sleep(1);
					Platform.runLater(() -> {
						cancel.fire();
						// cancel.setText("Done loading, click to continue");
						// bottom.getChildren().add(cancel);
						Main.updateGames();
						// stage.close();
					});
				}
				return null;
			}
		};
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();

	}

}