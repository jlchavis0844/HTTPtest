package scenes;
import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.Issue;

public class IssueHbox extends HBox{
	private Issue issue;
	private ImageView thumb;
	private BufferedImage bi;
	private Label infoLbl;
	private Label a = new Label("ADDED");
	private Label na = new Label("NOT ADDED");;
	
	public IssueHbox(Issue rhIssue, boolean add){
		super();
		setSpacing(10);

		issue = rhIssue;
		long start = System.currentTimeMillis();
		bi = issue.getImage("thumb");
		Image image = SwingFXUtils.toFXImage(bi, null);
		thumb = new ImageView(image);
		System.out.println("Image fetch took :" + (System.currentTimeMillis() - start));
		thumb.setFitHeight(50);
		thumb.setFitWidth(33);

		
		String info = issue.getVolumeName() + " #" + issue.getIssueNum() + "\nDate: " + issue.getCoverDate(); 
		infoLbl = new Label(info);
		
		if(add == true){
			getChildren().addAll(thumb, infoLbl, a);
		}
		else{
			getChildren().addAll(thumb, infoLbl, na);
		}	
	}
	
	public Issue getIssue(){
		return issue;
	}
	
	public BufferedImage getImage(){
		return bi;
	}
}