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
import model.Game;
import javafx.scene.input.MouseEvent;

public class SearchGameDetail extends BorderPane {
	private Button editButton;
	private ArrayList<CharCred> chars;
	private String link;
	private WebView webView;
	private WebEngine webEngine;
	private String webLink;
	private StackPane browser;
	private WebView webplay;
	private ImageView imageView;
	private Long start = System.currentTimeMillis();
	public SearchGameDetail(Game game, boolean loadWeb) {
		super();

		start = System.currentTimeMillis();// mark start
		game.populate();
//		if (!game.isFull())
//			game.populate();
		imageView = new ImageView();
		Label nameLbl = new Label("Name"); //1
		Label pubLbl = new Label("Publisher"); //2
		Label rDateLbl = new Label("Release Date");//3
		Label platformLbl = new Label("Platforms");//4
		Label franchiseLbl = new Label("Franchise");//5
		// left.getChildren().addAll(volNameLbl,gameNumLbl,nameLbl,cDateLbl,
		// writerLbl);
		System.out.println("Label setup took : " + (System.currentTimeMillis()-start));
		
		String varName = game.getName();
		String varPub = game.getPublishers();
		String varDate = game.getRelDate();
		String varPlat = game.getPlatsLong();
		String varFran = game.getFranchise();
		System.out.println("fetching game info setup took : " + (System.currentTimeMillis()-start));
		// VBox center = new VBox();
		TextField name = new TextField(varName);//1
		name.setPrefWidth(400);
		TextField publisher = new TextField(varPub);//2
		publisher.setPrefWidth(400);
		TextField rDate = new TextField(varDate);//3
		rDate.setPrefWidth(400);
		TextField platformTxt = new TextField(varPlat);//4
		platformTxt.setPrefWidth(400);
		TextField franchiseTxt = new TextField(varFran);//5
		franchiseTxt.setPrefWidth(400);
		System.out.println("textfield setup took : " + (System.currentTimeMillis()-start));
		// center.getChildren().addAll(volName,gameNum,name,cDate, writer);

		this.setPadding(new Insets(10));

		GridPane grid = new GridPane();
		grid.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		grid.setPadding(new Insets(5, 5, 10, 5));
		grid.setScaleShape(true);
		grid.setVgap(10);
		grid.setHgap(5);

		grid.add(nameLbl, 0, 1);
		grid.add(name, 1, 1);

		grid.add(pubLbl, 0, 2);
		grid.add(publisher, 1, 2);

		grid.add(rDateLbl, 0, 3);
		grid.add(rDate, 1, 3);

		grid.add(franchiseLbl, 0, 4);
		grid.add(franchiseTxt, 1, 4);
		
		grid.setPrefWidth(500);
		System.out.println("text setup took : " + (System.currentTimeMillis()-start));
//		editButton = new Button("Save Changes");
//		editButton.setVisible(true);
//		editButton.setOnAction(e -> {
//			LocalDB.update(game.getID(), "name", name.getSelectedText().toString(), 0);
//			LocalDB.update(game.getID(), "game_number", gameNum.getSelectedText().toString(), 0);
//			LocalDB.update(game.getID(), "cover_date", cDate.getSelectedText().toString(), 0);
//			LocalDB.update(game.getID(), "volume", volName.getSelectedText().toString(), 0);
//		});
//
//		grid.add(editButton, 0, 8);

		WebView descBox = new WebView();
		StackPane webBox = new StackPane();
		webBox.setPadding(new Insets(10));
		webView = new WebView();
		webBox.getChildren().add(webView);
		descBox.autosize();
		String desc = game.getDescription();
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
		descBox.getStyleClass().add("browser");
		System.out.println("Done loading description: " + (System.currentTimeMillis()-start));
		//TODO: change the method to check for local, then get web 
		
		webplay = new WebView();
		webplay.setPrefSize(560,315);
		String embedLink = game.getVideo();
		if(embedLink == null || embedLink.equals("")){
			webplay.setVisible(false);
		} else {
			Platform.runLater(new Runnable(){
				@Override
				public void run() {
					webplay.getEngine().load(embedLink);	
				}
			});
		}
		
		System.out.println("Done loading webview: " + (System.currentTimeMillis()-start));
		VBox tempHack = new VBox(10);
		tempHack.setMinHeight(620);
		tempHack.setPrefWidth(560);
		tempHack.setAlignment(Pos.CENTER);
		tempHack.getChildren().add(imageView);
		tempHack.getChildren().add(webplay);
		tempHack.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		// setMargin(descBox, new Insets(10));
		setLeft(grid);
		// setCenter(center);
		setCenter(tempHack);
		String titleTxt = "";
		titleTxt += game.getName();
		titleTxt += "\t(" + game.getRelDate() + ")";
		Label title = new Label(titleTxt);
		title.setStyle("-fx-font: 40px \"Comic Sans\";");
		setTop(title);

		ScrollPane scrollPane = new ScrollPane();
		ObservableList<String> data = FXCollections.observableArrayList(game.getPlatsLongArr());
		ListView<String> listView = new ListView<>(data);

		listView.setStyle("-fx-border-color: #2B2B2B");
		
		browser = new StackPane();
		browser.setPadding(new Insets(10));
		browser.setMaxWidth(400);

		browser.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");
		browser.getChildren().add(descBox);
		setBottom(browser);


		
		webBox.setStyle("-fx-border-radius: 20 20 20 20; " + "-fx-background-radius: 20 20 20 20; "
				+ "-fx-background-color: #2B2B2B; " + "-fx-border-color: #BBBBBB;");

		scrollPane.setStyle("-fx-border-color: #2B2B2B");
		scrollPane.setContent(listView);
		grid.add(scrollPane, 0, 10, 2, 5);
		scrollPane.setFitToHeight(true);
		scrollPane.setFitToWidth(true);
		
		System.out.println("**************" + tempHack.getWidth() + "(w) " + tempHack.getHeight() + "h");
		
		Platform.runLater(new Runnable(){

			@Override
			public void run() {
				Image tImg = game.getRemoteMedium();
				imageView.setImage(game.getRemoteMedium());
				System.out.println("Image fetch time: " + (System.currentTimeMillis()-start));
				double height = java.lang.Math.min(tImg.getHeight(), 500);
				imageView.setFitHeight(height);
				double x = tImg.getWidth();
				double y = tImg.getHeight();
				double newWidth = (height*x)/y;
				imageView.setFitWidth(newWidth);
				System.out.println("Done loading image: " + (System.currentTimeMillis()-start));
			}
			
		});
	}
	
	public StackPane getWebDesc(){
		return browser;
	}
	
	public void killVideo(){
		webplay= new WebView();
	}
}
