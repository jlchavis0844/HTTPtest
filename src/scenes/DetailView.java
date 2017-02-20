package scenes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import localDB.LocalDB;
import model.CharCred;
import model.Issue;
import javafx.scene.input.MouseEvent;

public class DetailView extends BorderPane {
	private Button editButton;
	private ArrayList<CharCred> chars;
	private String link;
	private WebView webView;
	private WebEngine webEngine;
	private String webLink;

	public DetailView(Issue issue, boolean loadWeb) {
		super();

		if (!issue.isFull())
			issue.populate();

		// VBox left = new VBox();
		Label nameLbl = new Label("Name");
		Label issueNumLbl = new Label("Issue Number#");
		Label cDateLbl = new Label("Cover Date");
		Label volNameLbl = new Label("Volume");
		Label writerLbl = new Label("Writers");
		// left.getChildren().addAll(volNameLbl,issueNumLbl,nameLbl,cDateLbl,
		// writerLbl);

		// VBox center = new VBox();
		TextField name = new TextField(issue.getName());
		TextField issueNum = new TextField(issue.getIssueNum());
		TextField cDate = new TextField(issue.getCoverDate());
		TextField volName = new TextField(issue.getVolumeName());
		TextField writer = new TextField(issue.getPerson("writer"));
		writer.setEditable(false);
		Label arcLbl = new Label("Story Arc");
		TextField arcName = new TextField(issue.getArcName());
		webLink = issue.getWebLink();

		// center.getChildren().addAll(volName,issueNum,name,cDate, writer);

		this.setPadding(new Insets(10));

		GridPane grid = new GridPane();
		grid.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		grid.setPadding(new Insets(5, 5, 10, 5));
		grid.setScaleShape(true);
		grid.setVgap(10);
		grid.setHgap(5);

		grid.add(volNameLbl, 0, 1);
		grid.add(volName, 1, 1);

		grid.add(issueNumLbl, 0, 2);
		grid.add(issueNum, 1, 2);

		grid.add(cDateLbl, 0, 3);
		grid.add(cDate, 1, 3);

		grid.add(nameLbl, 0, 4);
		grid.add(name, 1, 4);

		grid.add(writerLbl, 0, 5);
		grid.add(writer, 1, 5);

		grid.add(new Label("Artist"), 0, 6);
		grid.add(new TextField(issue.getPerson("art")), 1, 6);

		grid.add(arcLbl, 0, 7);
		grid.add(arcName, 1, 7);

		editButton = new Button("Save Changes");
		editButton.setVisible(true);
		editButton.setOnAction(e -> {
			LocalDB.update(issue.getID(), "name", name.getSelectedText().toString(), 0);
			LocalDB.update(issue.getID(), "issue_number", issueNum.getSelectedText().toString(), 0);
			LocalDB.update(issue.getID(), "cover_date", cDate.getSelectedText().toString(), 0);
			LocalDB.update(issue.getID(), "volume", volName.getSelectedText().toString(), 0);
		});

		grid.add(editButton, 0, 8);

		WebView descBox = new WebView();
		StackPane webBox = new StackPane();
		webBox.setPadding(new Insets(10));
		webView = new WebView();
		webBox.getChildren().add(webView);
		descBox.setMinHeight(50);
		descBox.setPrefHeight(200);
		descBox.autosize();
		String desc = issue.getDescription();
		Document doc = Jsoup.parse(desc);
		doc.select("table").remove();
		doc.append("<link rel=\"stylesheet\" type=\"text/css\" href=\""
				+ getClass().getResource("../application.css").toExternalForm() + "\" />");
		doc.select("h4").remove();
		desc = doc.toString();
		// descBox.getEngine().setUserStyleSheetLocation(getClass().getResource("../application.css").toExternalForm());

		desc = desc.replaceAll("null", "No summary");
		descBox.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");

		descBox.getEngine().loadContent(desc);
		descBox.setMaxHeight(300);
		// descBox.setFontScale(0.75);
		descBox.getStyleClass().add("browser");

		BufferedImage bi = issue.getImage("medium");
		Image image = SwingFXUtils.toFXImage(bi, null);
		ImageView imageView = new ImageView(image);
		imageView.setOnMouseClicked((MouseEvent event) -> {
			if (loadWeb) {
				webEngine.load(webLink);
				setRight(webBox);
			}
		});

		imageView.setFitWidth(390);
		imageView.setFitHeight(600);
		VBox tempHack = new VBox(10);
		tempHack.setMinHeight(620);
		tempHack.setMinWidth(410);
		tempHack.setAlignment(Pos.CENTER);
		tempHack.getChildren().add(imageView);
		tempHack.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		// setMargin(descBox, new Insets(10));
		setLeft(grid);
		// setCenter(center);
		setCenter(tempHack);
		String titleTxt = "";
		titleTxt += issue.getVolumeName() + " #" + issue.getIssueNum();
		titleTxt += "\t(" + issue.getCoverDate() + ")";
		Label title = new Label(titleTxt);
		title.setStyle("-fx-font: 40px \"Comic Sans\";");
		setTop(title);

		ScrollPane scrollPane = new ScrollPane();
		chars = LocalDB.getCharacterList(issue.getID());
		ArrayList<String> charNames = new ArrayList<String>();
		Collections.sort(charNames);
		for (CharCred c : chars) {
			charNames.add(c.getName());
		}
		ObservableList<String> data = FXCollections.observableArrayList(charNames);
		ListView<String> listView = new ListView<>(data);

		listView.setStyle("-fx-border-color: #2B2B2B");
		listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if(loadWeb){
				link = null;
				for (CharCred c : chars) {
					if (c.getName().equals((String) (newValue))) {
						link = c.getLink();
						break;
					}
				}
	
				Platform.runLater(() -> {
					webEngine.load(link);
					setRight(webBox);
				});
			}
			System.out.println("Loading Link..." + link);
		});

		StackPane browser = new StackPane();
		browser.setPadding(new Insets(10));

		browser.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		browser.getChildren().add(descBox);
		setBottom(browser);


		
		webBox.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		webEngine = webView.getEngine();
		webEngine.setJavaScriptEnabled(false);

		if (loadWeb) {
			setRight(webBox);

			Platform.runLater(() -> {
				webEngine.load(webLink);
			});
		}

		scrollPane.setStyle("-fx-border-color: #2B2B2B");
		scrollPane.setContent(listView);
		grid.add(scrollPane, 0, 10, 2, 5);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
	}
}
