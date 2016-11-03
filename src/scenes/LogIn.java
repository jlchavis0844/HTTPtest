package scenes;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class LogIn {

	private Button loginButton;
	private Button signUpButton;
	private TextField usernameTextField;
	private PasswordField passwordTextField;
	private Label incorrect;

	public LogIn() {
		Stage stage = new Stage();
		stage.setTitle("Welcome | Login or Sign Up");
		BorderPane bp = new BorderPane();
		GridPane grid = new GridPane();

		incorrect = new Label("blah");
		usernameTextField = new TextField();
		passwordTextField = new PasswordField();
		loginButton = new Button("Log In");
		signUpButton = new Button("Sign Up");


		usernameTextField.setPromptText("Username");
		passwordTextField.setPromptText("Password");

		// grid.add(loginLabel, 1, 0, 1, 1);
		grid.add(usernameTextField, 1, 0, 2, 1);
		grid.add(passwordTextField, 1, 1, 2, 1);
		grid.add(loginButton, 1, 2, 1, 1);
		grid.add(signUpButton, 2, 2, 1, 1);
		grid.setVgap(5);
		grid.setHgap(5);
		grid.setAlignment(Pos.CENTER);

		// bp.setCenter(grid);

		Scene scene = new Scene(grid, 400, 200);
		String style= LogIn.class.getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		stage.setResizable(false);
		scene.getStylesheets().add(getClass().getResource("../application/application.css").toExternalForm());
		stage.setScene(scene);
		stage.showAndWait();

	}
	

}