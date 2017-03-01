package scenes;

import java.io.File;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Game;

public class GameResult extends HBox {
	private Label text;
	private ImageView thumb;
	private Game game;
	public GameResult(Game game) {
		super();
		this.game = game;
		System.out.println(game.getName());
		this.setMaxWidth(500);
		setMinWidth(250);
		text = new Label();
		text.setWrapText(true);
		text.setMaxWidth(300);
		String info = game.getName() + "\n" + game.getRelDate() + "\n" + game.getPlatsLong() +
				"\n" + game.getDeck();
		text.setText(info);
		Image image = game.getRemoteThumb();
		System.out.println();
		
		if(image != null){
			thumb = new ImageView(image);
			thumb.setPreserveRatio(true);
			//thumb.setFitHeight(100);
			thumb.setFitWidth(100);
		} else {
			File file = new File("./images/game/missing_icon.jpg");
			thumb = new ImageView(file.toURI().toString());
		}
		BorderPane bp = new BorderPane();
		bp.setLeft(thumb);
		bp.setRight(text);
		BorderPane.setMargin(text, new Insets(0,0,0,50));
		BorderPane.setMargin(thumb, new Insets(0, 0, 10, 0));
		System.out.println("Finished " + game.getName());
		getChildren().addAll(bp);
	}
	
	public String getGameID(){
		return game.getID();
	}
	
	public Game getGame(){
		return game;
	}

}
