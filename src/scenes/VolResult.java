package scenes;

import java.awt.image.BufferedImage;

import org.json.JSONObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.Volume;
import requests.CVImage;

public class VolResult extends HBox {
	Label text;
	ImageView thumb;
	Volume vol;
	
	
	public VolResult(Volume vol) {
		super();
		this.vol = vol;
		
		text = new Label();
		String info = vol.getName() + "\n" + vol.getPublisher() + "\n" + vol.getStartYear() +
				"\n" + vol.getCountofIssue() + " issues";
		text.setText(info);
		JSONObject jo = vol.getJSONObject();
		jo = jo.getJSONObject("image");
		String url = jo.getString("thumb_url");
		
		BufferedImage bi = CVImage.getRemoteImage(url);
		Image image = SwingFXUtils.toFXImage(bi, null);
		thumb = new ImageView(image);
		thumb.setFitHeight(100);
		thumb.setFitWidth(66);
		BorderPane bp = new BorderPane();
		bp.setLeft(thumb);
		bp.setRight(text);
		BorderPane.setMargin(text, new Insets(0,0,0,50));
		BorderPane.setMargin(thumb, new Insets(0, 0, 10, 0));
		getChildren().addAll(bp);
	}
	
	public String getVolID(){
		return vol.getID();
	}

}
