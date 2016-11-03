package scenes;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.Issue;

public class IssuePreview extends HBox{
	private Issue issue;
	private ImageView thumb;
	private BufferedImage bi;
	private Label infoLbl;
	
	public IssuePreview(Issue rhIssue) {
		super();
		
		issue = rhIssue;
		long start = System.currentTimeMillis();
		bi = issue.getLocalImg("thumb");
		Image image = SwingFXUtils.toFXImage(bi, null);
		thumb = new ImageView(image);
		System.out.println("Image fetch for " + issue.getVolumeName() + " #" + issue.getIssueNum()
							+ " took :" + (System.currentTimeMillis() - start));
		thumb.setFitHeight(50);
		thumb.setFitWidth(33);
		
		String info = issue.getVolumeName() + " #" + issue.getIssueNum() + "\nDate: " + issue.getCoverDate(); 
		infoLbl = new Label(info);
		getChildren().addAll(thumb, infoLbl);	
	}
	
	public Issue getIssue(){
		return issue;
	}
	
	public BufferedImage getImage(){
		return bi;
	}
}
