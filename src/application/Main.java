package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.JSONObject;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import localDB.LocalDB;
import model.Issue;
import model.Volume;
import requests.CVImage;
import requests.CVrequest;
import requests.CVrequestAsync;
import requests.MarvelRequest;
import requests.SQLQuery;
import scenes.AddComic;
import scenes.DetailView;
import scenes.IssueLoadScreen;
import scenes.IssuePreview;
import scenes.IssueResult;
import scenes.VolResult;
import scenes.VolumePreview;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;


public class Main extends Application {
	@SuppressWarnings("rawtypes")
	private Stage window;
	private BorderPane layout;
	private static ArrayList<Issue> added;
	private static ArrayList<Issue> allIssues;
	private static ArrayList<Volume> allVols;
	private static List<VolumePreview> volPreviews;
	private static ScrollPane leftScroll;
	private HBox hbox;
	private Button addButton;
	private static TreeView treeView;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("Digital Long Box");
		layout = new BorderPane();

		added = new ArrayList<Issue>();

		System.out.println("getting all issues");
		Long start;
		start = System.currentTimeMillis();
		allIssues = LocalDB.getAllIssues();
		System.out.println("Done loading after " + (System.currentTimeMillis() - start));

		if(allIssues == null){
			System.out.println("no issues found ");
			allIssues = new ArrayList<Issue>();
		}

		start = System.currentTimeMillis();
		allVols = LocalDB.getAllVolumes();
		System.out.println("volume loading took " + (System.currentTimeMillis()-start));

		if(allVols == null){
			allVols = new ArrayList<>();
		}
		volPreviews = new ArrayList<VolumePreview>();

		start = System.currentTimeMillis();
		for(Volume v: allVols){
			volPreviews.add(new VolumePreview(v, allIssues));
		}
		System.out.println("time to load all volumes " + (System.currentTimeMillis() - start));

		treeView = new TreeView<VolumePreview>(buildRoot());
		treeView.setPrefWidth(500);
		treeView.setPrefHeight(950);

		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {
				if(newValue instanceof VolumeCell){
					if(!((VolumeCell) newValue).isFilled())
						((VolumeCell) newValue).setIssues(allIssues);
				} else {
					TreeItem<IssuePreview> ti = (TreeItem<IssuePreview>) treeView.getSelectionModel().getSelectedItem();
					if(ti != null){	
						if(ti.getValue() != null){
							Issue issue = ti.getValue().getIssue();
							layout.setRight(new DetailView(issue));
						} else System.out.println("something went wrong loading issue");
					} else System.out.println("something went wrong loading issue");
				}
				boolean expanded = ((TreeItem) treeView.getSelectionModel().getSelectedItem()).isExpanded();
				newValue.setExpanded(!expanded);
			}

		});

		leftScroll = new ScrollPane();
		leftScroll.setPrefHeight(1000);
		leftScroll.setMaxHeight(1080);
		//leftScroll.setContent(volListView);
		leftScroll.setContent(treeView);
		leftScroll.setPadding(new Insets(10));

		layout.setLeft(leftScroll);
		hbox = new HBox();
		hbox.setPadding(new Insets(10));
		addButton = new Button("Click here to add");
		hbox.getChildren().add(addButton);
		layout.setTop(hbox);

		addButton.setOnAction(e -> {
			new AddComic(added);
			updateLeft();
		});

		Scene scene = new Scene(layout, 1900, 1050);
		System.out.println("applying " + getClass().getResource("../application.css").toExternalForm());
		String style= getClass().getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		window.setScene(scene);
		window.show();
		System.out.println("Done loading after " + (System.currentTimeMillis() - start));
		backgroundLoadVols();
	}

	public static void main(String[] args) {
		//		org.json.JSONObject jo = CVrequest.getIssue("488852");
		//		Volume test = new Volume(jo.getJSONObject("volume"));
		//		LocalDB.addVolume(test);
		//		new VolumePreview(test);
		//MarvelRequest.test();
		//		ArrayList<Volume> vols = CVrequestAsync.searchVolume("Batman", "DC");
		//		for(Volume v: vols)
		//			System.out.println(v.toString());
		launch(args);
		//System.out.println("to adjust");
		//SQLQuery.getLoginInfo();

		System.exit(0);
	}

	public static void updateLeft(){
		new IssueLoadScreen(added, allIssues, volPreviews);
		treeView.setRoot(buildRoot());
		added.clear();
		backgroundLoadIssues();
	}

	@SuppressWarnings("rawtypes")
	public static TreeItem buildRoot(){
		TreeItem root = new TreeItem<VolumePreview>();

		root.setValue("Volumes");
		root.setExpanded(true);
		for(VolumePreview vp: volPreviews){
			//vp.update(allIssues);
			TreeItem temp = new VolumeCell(vp);
			root.getChildren().add(temp);
		}
		return root;
	}

	/**
	 * Loads all issues for all volumes in the background
	 */
	public static void backgroundLoadIssues(){
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for(Object obj : treeView.getRoot().getChildren()){
			executorService.execute(new Runnable() {
				public void run(){
					((VolumeCell) obj).setIssues(allIssues);
				}
			});
		}
		executorService.shutdown();
	}

	/**
	 * loads all the volume images in the back ground
	 */
	public static void backgroundLoadVols(){
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		int volNum = allVols.size();
		AtomicInteger counter = new AtomicInteger(0);

		for(Object obj : treeView.getRoot().getChildren()){
			executorService.execute(new Runnable() {
				public void run(){
					System.out.println(counter.incrementAndGet() + " ?= " + volNum);
					((VolumePreview)((VolumeCell) obj).getValue()).setImage();
					System.out.println("done loading " + ((VolumePreview)((VolumeCell) obj).getValue()).getVolName());
				}
			});
		}
		executorService.shutdown();

		while(counter.get() != volNum){};//wait here to load volumes
		backgroundLoadIssues();//start loading issues
	}
}
