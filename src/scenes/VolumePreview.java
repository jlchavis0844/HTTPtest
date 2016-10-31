package scenes;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import model.Issue;
import model.Volume;

public class VolumePreview extends HBox{
	private Volume vol;
	private ImageView thumb;
	private Label infoLbl;

	/**
	 * Makes a preview of a volume, to load the image, us setImage()
	 * @param rhVol
	 * @param issues
	 */
	public VolumePreview(Volume rhVol, ArrayList<Issue> issues) {
		super();

		vol = rhVol;
		long start = System.currentTimeMillis();
		//		BufferedImage bi = vol.getImage("thumb");
		//		Image image = SwingFXUtils.toFXImage(bi, null);
		//		thumb = new ImageView(image);
		thumb = new ImageView();
		//System.out.println("Image fetch took :" + (System.currentTimeMillis() - start));
		thumb.setFitHeight(50);
		thumb.setFitWidth(33);

		int counter = 0;
		for(Issue i : issues){
			if(i.getVolumeID().equals(vol.getID())){
				counter++;
			}
		}

		String info = vol.getName() + "\n" + vol.getPublisher()  + "     " + vol.getStartYear() +
				"\n" + counter + " out of " + vol.getCountofIssue() + " in collection";

		infoLbl = new Label(info);
		getChildren().addAll(thumb, infoLbl);
	}

	public String getVolName(){
		return vol.getName();
	}

	public void update(ArrayList<Issue> issues){
		int counter = 0;
		for(Issue i : issues){
			if(i.getVolumeID().equals(vol.getID())){
				counter++;
			}

			String info = vol.getName() + "\n" + vol.getPublisher()  + "     " + vol.getStartYear() +
					"\n" + counter + " out of " + vol.getCountofIssue() + " in collection";
			infoLbl.setText(info);
			getChildren().clear();
			getChildren().addAll(thumb, infoLbl);
		}

	}

	/**
	 * @return the vol
	 */
	public Volume getVolume() {
		return vol;
	}

	/**
	 * @param vol the vol to set
	 */
	public void setVolume(Volume vol) {
		this.vol = vol;
	}

	/**
	 * loads the image
	 */
	public void setImage(){
		if(thumb.getImage() == null){
			BufferedImage bi = vol.getImage("thumb");
			Image image = SwingFXUtils.toFXImage(bi, null);
			thumb.setImage(image);
		}
	}
}