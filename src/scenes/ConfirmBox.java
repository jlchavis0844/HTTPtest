package scenes;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConfirmBox {
	private static boolean answer;
	
	public static boolean display(String title, String message){
		Button yesBtn = new Button("Yes");
		Button noBtn = new Button("No");
		answer = false;
		
	
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(400);
		
		Label label = new Label(message);
		Button closeBtn = new Button("Close the Windows");
		closeBtn.setOnAction(e -> window.close());
		
		HBox layout = new HBox(10);
		layout.getChildren().addAll(label, yesBtn, noBtn);
		layout.setAlignment(Pos.CENTER);
		
		yesBtn.setOnAction(e -> {
			window.close();
			answer = true;
		});
		
		noBtn.setOnAction(e -> window.close());
				
		Scene scene = new Scene(layout, 200, 100);
		String style= ConfirmBox.class.getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		window.setScene(scene);
		window.showAndWait();
		
		return answer;
	}

}
