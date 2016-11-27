package scenes;

import java.awt.image.BufferedImage;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import model.Issue;

public class IssuePreview extends HBox{
	private Issue issue;
	private ImageView thumb;
	private BufferedImage bi;
	private Label infoLbl;
	private Label addLabel;
	
	public IssuePreview(Issue rhIssue) {
		super();
		
		issue = rhIssue;
		long start = System.currentTimeMillis();
		bi = issue.getImage("thumb");
		Image image = SwingFXUtils.toFXImage(bi, null);
		thumb = new ImageView(image);
		addLabel = new Label();
		addLabel.setTextFill(Color.RED);
		addLabel.setVisible(false);
		/*System.out.println("Image fetch for " + issue.getVolumeName() + " #" + issue.getIssueNum()
							+ " took :" + (System.currentTimeMillis() - start));*/
		thumb.setFitHeight(50);
		thumb.setFitWidth(33);
		
		String info = issue.getVolumeName() + " #" + issue.getIssueNum() + "\nDate: " + issue.getCoverDate(); 
		infoLbl = new Label(info);
		getChildren().addAll(thumb, infoLbl, addLabel);	
	}
	
	public Issue getIssue(){
		return issue;
	}
	
	public void setAddInfo(String s){
		addLabel.setText(s);
		addLabel.setVisible(true);
	}
	
	public BufferedImage getImage(){
		return bi;
	}
}
