package scenes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebView;
import model.Issue;

public class IssueResult extends VBox {
	private Label text;
	private Issue issue;
	
	@FXML
	private WebView desc;
	
	public IssueResult(Issue rhIssue) {
		super();
		this.issue = rhIssue;
		
		text = new Label();
		text.setWrapText(true);
		String info = "Issue# " + issue.getIssueNum()+ " \tDate: "  + issue.getCoverDate() + "\n" + issue.getName();
		Document doc = Jsoup.parse(issue.getDescription());
		Elements element = doc.select("table").remove();
		
		String clean = Jsoup.clean(doc.body().html(), Whitelist.basic());
		
		
		String descript = Jsoup.parse(doc.toString()).text();
		info += "\n" + descript;
		text.setText(info);
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setPrefSize(900, 200);
		setMaxHeight(200);
		/*desc = new WebView();
		desc.getEngine().loadContent(issue.getDescription());
		desc.setMaxHeight(200);
		desc.setFontScale(0.75);*/
		setPrefHeight(100);
		getChildren().addAll(text);
		
	}
	
	public Issue getIssue(){
		return issue;
	}

	/**
	 * @return the text
	 */
	public Label getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(Label text) {
		this.text = text;
	}
}
