package scenes;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import requests.CVImage;
import requests.SQLQuery;

public class LogIn {

	private CheckBox cb;
	private Button loginButton;
	private Button signUpButton;
	private TextField usernameTextField;
	private PasswordField passwordTextField;
	private Label incorrect;
	private boolean newUser = false;
	private String[] info;

	public LogIn() throws IOException {
		//CVImage.cleanAllLocalImgs();// clean up images on start up
		Stage stage = new Stage();

		stage.setTitle("Welcome | Login or Sign Up");
		GridPane grid = new GridPane();

		incorrect = new Label("Logging in failed");//will hold error message
		incorrect.setVisible(false);//default to not visible
		incorrect.setTextFill(Color.RED);//make it red
		usernameTextField = new TextField();
		passwordTextField = new PasswordField();
		loginButton = new Button("Log In");
		signUpButton = new Button("Sign Up");
		cb = new CheckBox("Auto Login");

		//set focus to allow prompt text to display on user and password fields
		Platform.runLater(new Runnable(){
			public void run(){
				cb.requestFocus();
			}
		});
		signUpButton.setOnAction(e -> {
			boolean temp = sendRegister();//try to register
			if(temp)//if registration is good
				stage.close();//close stage and move on
		});

		loginButton.setOnAction(e ->{
			info[0] = usernameTextField.getText();
			info[1] = passwordTextField.getText();
			//info[2] = getCurrentTimeStamp();
			info[3] = String.valueOf(cb.isSelected());
			System.out.println("Sending login info... " + arrString(info));
			boolean temp = SQLQuery.login(info[0], info[1]);
			if(temp){
				SQLQuery.setLoginInfo(info);
				SQLQuery.fullSync();
				stage.close();
			} else {
				incorrect.setText("Login failed");
				incorrect.setVisible(true);
			}
		});

		usernameTextField.setPromptText("Username");
		passwordTextField.setPromptText("Password");

		info = SQLQuery.getLoginInfo();//get username, password, timestamp, auto
		if (info[0] != null) {//if login info is found
			usernameTextField.setText(info[0]);//set username
			passwordTextField.setText(info[1]);//set password
			cb.setSelected(Boolean.valueOf(info[3]));//auto login?
			signUpButton.setVisible(false);//hide register button
			loginButton.setVisible(true);//show login button
		} else {//there is no login info found
			signUpButton.setVisible(true);//make signup button visible
			newUser = true;
			loginButton.setVisible(false);//hide login until registration is done
		}

		// grid.add(loginLabel, 1, 0, 1, 1);
		grid.add(usernameTextField, 1, 0, 2, 1);
		grid.add(passwordTextField, 1, 1, 2, 1);
		grid.add(loginButton, 1, 2, 1, 1);
		grid.add(signUpButton, 2, 2, 1, 1);
		grid.add(cb, 3, 2, 1, 1);
		grid.add(incorrect, 1, 4,4,2);

		grid.setVgap(5);
		grid.setHgap(5);
		grid.setAlignment(Pos.CENTER);

		// bp.setCenter(grid);

		Scene scene = new Scene(grid, 400, 200);
		//String style = LogIn.class.getResource("../application.css").toExternalForm();
		//scene.getStylesheets().add(style);
		stage.setResizable(false);
		stage.setScene(scene);

		boolean loggedIn = false;
		if(!cb.isSelected()){
			stage.showAndWait();
			SQLQuery.fullSync();
		} else if(!newUser && cb.isSelected()){
			loggedIn = SQLQuery.login(info[0], info[1]);
			SQLQuery.fullSync();
			if(!loggedIn) {
				AlertBox.display("Unable to log in", 
						"Logging in has failed, check information and try again");
				cb.setSelected(false);
				signUpButton.setVisible(true);
				incorrect.setText("Stored user info is incorrect");
				incorrect.setVisible(true);
				stage.showAndWait();
			} else {
//				stage.show();
//				signUpButton.setVisible(false);
//				loginButton.setVisible(false);
			}
		} else {
			SQLQuery.fullSync();
		}
	}

	public boolean sendRegister(){
		boolean reged = false;
		String user = usernameTextField.getText();
		String pass = passwordTextField.getText();

		if(user == null || user.equals("")){
			incorrect.setVisible(true);
			incorrect.setText("Missing User Name");
			//AlertBox.display("Missing username", "Please enter your username");
			return false;
		}

		if(pass == null || pass.equals("")){
			//AlertBox.display("Missing password", "Please enter your password");
			incorrect.setVisible(true);
			incorrect.setText("Missing password");
			return false;
		}

		String response = SQLQuery.register(user, pass);
		if(response.contains("ERROR:")){
			incorrect.setText(response);
			incorrect.setVisible(true);
			return false;
		} else reged = true;

		String[] info = {user, pass, getCurrentTimeStamp(), String.valueOf(cb.isSelected())};
		reged = SQLQuery.setLoginInfo(info);


		if(!reged){
			incorrect.setText("Write to DB failed");
			incorrect.setVisible(true);
		} else {
			AlertBox.display("Registration Success", (user + " has been registered"));
		}
		return reged;
	}

	public String getCurrentTimeStamp() {
		java.util.Date date = new java.util.Date();
		Timestamp ts = new Timestamp(date.getTime());
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
		return timeStamp;
	}
	
	public static String arrString(String[] arr){
		String line = "[";
		for(String s: arr)
			line += (s + ", ");
		line = line.substring(0, line.length()-2);
		line += "]";
		return line;
	}
	

}