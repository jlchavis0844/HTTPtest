package scenes;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Game;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.ContextMenuEvent;

public class GamePreview extends HBox {
	private Game game;
	private ImageView thumb;
	private Image image;
	private Label infoLbl;
	private Label addLabel;

	public GamePreview(Game rhGame) {
		super();

		game = rhGame;
		long start = System.currentTimeMillis();
		setMaxWidth(320);
		image = rhGame.getImage("thumb");
		thumb = new ImageView(image);
		addLabel = new Label();
		addLabel.setTextFill(Color.RED);
		addLabel.setVisible(false);
		
		/*
		 * System.out.println("Image fetch for " + game.getVolumeName() + " #"
		 * + game.getGameNum() + " took :" + (System.currentTimeMillis() -
		 * start));
		 */
		double x = image.getWidth();
		double y = image.getHeight();
		double newHeight = (50*y)/x;
		
		thumb.setFitHeight(newHeight);
		thumb.setFitWidth(50);

		String info = game.getName() + "\n" + game.getRelDate() + "\n" + game.getPlatsShort() +
				"\n" + game.getDeck();
		infoLbl = new Label(info);
		infoLbl.setWrapText(true);
		getChildren().addAll(thumb, infoLbl, addLabel);

		/*
		 * context menu code
		 */
		ContextMenu contextMenu = new ContextMenu();
        MenuItem item1 = new MenuItem("delete");
        item1.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {

            }
        });
        // Add MenuItem to ContextMenu
        contextMenu.getItems().addAll(item1);
        // When user right-click game preview
        setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
 
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(infoLbl, event.getScreenX(), event.getScreenY());
            }
        });
        /*
         * end of context menu code
         */
	}

	public Game getGame() {
		return game;
	}

	public void setAddInfo(String s) {
		addLabel.setText(s);
		addLabel.setVisible(true);
	}
	
	public void setGameName(){
		addLabel.setText(game.getName());
		addLabel.setVisible(true);
		addLabel.setTextFill(Color.BLACK);
	}

	public Image getImage() {
		return image;
	}
}
