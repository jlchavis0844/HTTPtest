package application;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import localDB.LocalDB;
import model.Issue;
import model.Volume;
import scenes.AddComic;
import scenes.DetailView;
import scenes.IssueLoadScreen;
import scenes.IssuePreview;
import scenes.LogIn;
import scenes.VolumePreview;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.*;

/**
 * Main class, contains all gui start and scence controls
 * 
 * @author jlchavis
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class Main extends Application {
	private Stage window;// main stage
	private BorderPane layout;// layout for window
	private static ArrayList<Issue> added; // list to hold added issues from
											// addComic scene
	private static ArrayList<Issue> allIssues;// holds all issues from the local
												// DB
	private static ArrayList<Volume> allVols;// holds a list of all volumes,
												// apart from issues
	private static List<VolumePreview> volPreviews;// holds VolumePreviews
													// generated for every
													// volume
	private static ScrollPane leftScroll;// holds the tree that lists volumes
	private HBox hbox;// for the top row of border frame
	private Button addButton; // launches addComic scene
	private static TreeView treeView; // holds VolumeCell-->volume preview
										// -->issues review
	
	private static TreeView srchTree;

	/**
	 * launched from main(), starts main scene/ui
	 */
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		new LogIn();// launch login scene

		window = primaryStage;// set as primary stage
		window.setTitle("Digital Long Box");// set title
		// window.initStyle(StageStyle.TRANSPARENT);

		layout = new BorderPane();// main scene is border pane

		added = new ArrayList<Issue>();// for addComics scene

		System.out.println("getting all issues");
		Long start; // timing purposes
		start = System.currentTimeMillis();// mark start
		allIssues = LocalDB.getAllIssues();// get all issues
		System.out.println("Done loading after " + (System.currentTimeMillis() - start));

		if (allIssues == null) {// if there are no issues
			System.out.println("no issues found ");
			allIssues = new ArrayList<Issue>();// prevent null pointer
		}

		start = System.currentTimeMillis();
		allVols = LocalDB.getAllVolumes();// get all volumes
		System.out.println("volume loading took " + (System.currentTimeMillis() - start));

		if (allVols == null) {// if there are no volumes
			allVols = new ArrayList<>();// instantiate volume
		}
		LocalDB.sortVolumes(allVols);
		volPreviews = new ArrayList<VolumePreview>();// holds volume previews
														// for volumecell

		start = System.currentTimeMillis();
		for (Volume v : allVols) {// make volume preview for every volume
			volPreviews.add(new VolumePreview(v, allIssues));
		}
		System.out.println("time to load all volumes " + (System.currentTimeMillis() - start));

		treeView = new TreeView<VolumePreview>(buildRoot("Volumes", volPreviews));// tree
																					// for
																					// volume
																					// cells
		treeView.setPrefWidth(500);
		treeView.setPrefHeight(950);

		/**
		 * Click listener to expand and show issues checks if clicking on volume
		 * preview or issue nothing is volume preview, detail view if issue
		 * preview
		 */
		treeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {

			@Override
			public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {
				if (newValue instanceof VolumeCell) {
					if (!((VolumeCell) newValue).isFilled())
						((VolumeCell) newValue).setIssues(allIssues);
				} else {
					TreeItem<IssuePreview> ti = (TreeItem<IssuePreview>) treeView.getSelectionModel().getSelectedItem();
					if (ti != null) {
						if (ti.getValue() != null && ti.getValue() instanceof IssuePreview) {
							Issue issue = ti.getValue().getIssue();
							layout.setRight(new DetailView(issue));
						} else
							System.out.println("Issue preview is null");
					} else
						System.out.println("TreeItem is null");
				}
				
				if(treeView.getSelectionModel() != null && treeView.getSelectionModel().getSelectedItem() != null){
					boolean expanded = ((TreeItem) treeView.getSelectionModel().getSelectedItem()).isExpanded();
					newValue.setExpanded(!expanded);
				}
			}
		});

		/**
		 * Adding searching and sorting
		 */
		HBox issHeader = new HBox(10);
		issHeader.setAlignment(Pos.CENTER_LEFT);
		issHeader.setPadding(new Insets(10, 0, 0, 10));
		issHeader.setPrefWidth(450);
		VBox leftSide = new VBox(10);
		leftSide.setPrefWidth(issHeader.getPrefWidth());
		leftSide.setPrefHeight(1000);
		Button leftSearch = new Button("Search");
		TextField srchTxt = new TextField();
		srchTxt.setPrefWidth(200);
		srchTxt.setPromptText("Enter Search Term");
		ObservableList<String> options = FXCollections.observableArrayList("All Fields", "Story Arc", "Characters",
				"People", "Publisher");
		ComboBox comboBox = new ComboBox(options);
		comboBox.setPromptText("Fields to be Searched");
		issHeader.getChildren().add(srchTxt);
		issHeader.getChildren().add(comboBox);
		issHeader.getChildren().add(leftSearch);

		leftScroll = new ScrollPane();
		leftScroll.setContent(treeView);
		leftScroll.setPadding(new Insets(10));

		/**
		 * Lets go with a tabbed view
		 */
		TabPane tabs = new TabPane();
		tabs.setPadding(new Insets(10));
		// tabs.getStyleClass().add("floating");
		tabs.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		Tab allIssTab = new Tab("  Collection  ");
		allIssTab.setContent(leftScroll);
		Tab srchTab = new Tab("  Search Collection  ");
		// Need another scrollPane and vbox to hold the seaarach results.
		VBox srchVBox = new VBox(10);
		srchVBox.setFillWidth(true);
		srchVBox.setPrefHeight(1000);
		srchVBox.getChildren().add(issHeader);
		ScrollPane srchScroll = new ScrollPane();
		srchScroll.setFitToWidth(true);
		srchScroll.setFitToHeight(true);
		// new a new tree view for the new ScrollPane
		srchTree = new TreeView<IssuePreview>();
		srchTree.setPrefHeight(1000);
		srchScroll.setContent(srchTree);
		srchVBox.getChildren().addAll(srchScroll);
		srchVBox.setFillWidth(true);
		srchTab.setContent(srchVBox);

		tabs.getTabs().addAll(allIssTab, srchTab);
		leftSide.getChildren().add(tabs);
		// leftSide.setStyle("-fx-border-color: grey");

		srchTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem>() {
			@Override
			public void changed(ObservableValue<? extends TreeItem> observable, TreeItem oldValue, TreeItem newValue) {

				TreeItem<IssuePreview> ti = (TreeItem<IssuePreview>) srchTree.getSelectionModel().getSelectedItem();
				if (ti != null) {
					if (ti.getValue() != null) {
						Issue issue = ti.getValue().getIssue();
						layout.setRight(new DetailView(issue));
					} else
						System.out.println("something went wrong loading issue");
				} else
					System.out.println("something went wrong loading issue");
			}
		});

		leftSearch.setOnAction(e -> {
			String srchText = srchTxt.getText();
			String selected = "";
			if (!(comboBox.getSelectionModel().getSelectedItem() == null)) {
				selected = comboBox.getSelectionModel().getSelectedItem().toString();
			}

			if (!srchText.equals("") && !selected.equals(comboBox.getPromptText())) {
				switch (comboBox.getSelectionModel().getSelectedIndex()) {
				case 0:
					userSearch("JSON", srchText);
					break;
				case 1:
					userSearch("story_arc_credits", srchText);
					break;
				default:
					System.out.println("Didn't click all");
					break;
				}
			}
		});

		// layout left
		layout.setLeft(leftSide);
		hbox = new HBox();
		hbox.setPadding(new Insets(10));
		addButton = new Button("Click here to add");
		hbox.getChildren().add(addButton);
		layout.setTop(hbox);

		addButton.setOnAction(e -> {
			new AddComic(added);
			if (!added.isEmpty())
				updateLeft();
		});
		window.setMaximized(true);
		Scene scene = new Scene(layout, 1900, 1050);
		System.out.println(getClass().getResource("../application.css"));
		// System.out.println("applying " +
		// getClass().getResource("../application.css").toExternalForm());
		String style = getClass().getResource("../application.css").toExternalForm();
		scene.getStylesheets().add(style);
		window.setScene(scene);
		window.show();
		leftScroll.setFitToHeight(true);
		leftScroll.setFitToWidth(true);
		System.out.println(
				"Done loading after " + (System.currentTimeMillis() - start) + ", starting background loading");
		backgroundLoadVols();
	}

	public static void main(String[] args) {
		// ArrayList<Volume> allVols = LocalDB.getAllVolumes();
		// allVols.forEach(vol -> {
		// CVImage.addVolumeImg(vol, "thumb");
		// CVImage.addVolumeImg(vol, "medium");
		//
		// });
		// System.out.println(CVrequest.getApiKey());
		launch(args);
		System.out.println("run stuff here");
		System.exit(0);

	}

	public static void updateLeft() {
		if(added.size() != 0){
			new IssueLoadScreen(added, allIssues, volPreviews);
		}
		treeView.setRoot(buildRoot("Volumes", volPreviews));
		added.clear();
		backgroundLoadIssues();
	}

	@SuppressWarnings("rawtypes")
	public static TreeItem buildRoot(String title, List<VolumePreview> content) {
		TreeItem root = new TreeItem<VolumePreview>();

		root.setValue(title);
		root.setExpanded(true);
		for (VolumePreview vp : volPreviews) {
			// vp.update(allIssues);

			TreeItem temp = new VolumeCell(vp);
			root.getChildren().add(temp);
		}
		return root;
	}

	/**
	 * Loads all issues for all volumes in the background
	 */
	public static void backgroundLoadIssues() {
		System.out.println("Starting background load of issues");
		ExecutorService executorService = Executors.newFixedThreadPool(10);

		for (Object obj : treeView.getRoot().getChildren()) {
			executorService.execute(new Runnable() {
				public void run() {
					//// Platform.runLater(new Runnable() {
					// public void run() {
					boolean hasIssues = ((VolumeCell) obj).setIssues(allIssues);
					if (!hasIssues) {
						System.out.println("Warning: No issues found for " + ((VolumeCell) obj).getVolumeName());
					}
					// }
					// });
				}
			});
		}
		// executorService.shutdown();

	}

	/**
	 * loads all the volume images in the back ground
	 */
	public static void backgroundLoadVols() {
		System.out.println("Starting background load of volumes");
		int volNum = allVols.size();

		for (Object obj : treeView.getRoot().getChildren()) {
			((VolumePreview) ((VolumeCell) obj).getValue()).setImage();
			System.out.println("done loading " + ((VolumePreview) ((VolumeCell) obj).getValue()).getVolName());
		}
		backgroundLoadIssues();// start loading issues
	}

	/**
	 * Runs search on the user's collection
	 * 
	 * @param field
	 *            which field is being searched
	 * @param srchText
	 *            what we are searching for
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void userSearch(String field, String srchText) {
		try {
			ArrayList<Issue> results = LocalDB.searchIssue(field, "%" + srchText + "%", "LIKE");
			LocalDB.sortIssuesByCoverDate(results, true);
			TreeItem root = new TreeItem<IssuePreview>();

			root.setValue(srchText);
			root.setExpanded(true);
			for (Issue i : results) {
				TreeItem temp = new IssueCell(new IssuePreview(i));
				root.getChildren().add(temp);
			}

			srchTree.setRoot(root);
		} catch (JSONException | SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	/**
	 * This function will rebuild the VolumeCell -> VolumePreview -> Volume -> Issue(s) 
	 * preview chain by removing the given issue and then recreating the chain
	 * @param issue - this isssue that should be removed
	 */
	@SuppressWarnings({"unchecked" , "rawtypes"})
	public static void afterIssueUpdate(Issue issue) {
		allIssues.remove(issue);
		TreeItem tempRoot = treeView.getRoot();
		VolumeCell tempCell = null;
		//VolumePreview tempVP = null;
		ObservableList oldList = tempRoot.getChildren();
		
		oldList.forEach(vc -> {
			if(((VolumeCell)vc).getVolumeID().equals(issue.getVolumeID())){
				VolumePreview tempVP = new VolumePreview(((VolumeCell)vc).getVolume(), allIssues);
				vc = new VolumeCell(tempVP);
				((VolumeCell)vc).setIssues(allIssues);
			}
		});
		
		treeView.setRoot(buildRoot("Volumes", volPreviews));
		backgroundLoadIssues();
	}
	
	/**
	 * This function will rebuild the VolumeCell -> VolumePreview -> Volume -> Issue(s) 
	 * preview chain after removing the given volume and then recreating the chain
	 * @param vol - this volume that should be removed
	 */
	@SuppressWarnings({"unchecked" , "rawtypes"})
	public static void afterVolumeUpdate(Volume vol) {
		allVols.remove(vol);
		VolumePreview deleteMe = null;
		
		TreeItem tempRoot = treeView.getRoot();
		VolumeCell tempCell = null;
		//VolumePreview tempVP = null;
		ObservableList oldList = tempRoot.getChildren();
		for(int i = 0; i< oldList.size(); i++){
			tempCell = (VolumeCell) oldList.get(i);
			if(tempCell.getVolumeID().equals(vol.getID())){
				volPreviews.remove(tempCell.getVolPreview());
				oldList.remove(tempCell);
				break;
			}
		}
		
		treeView.setRoot(buildRoot("Volumes", volPreviews));
		backgroundLoadIssues();
	}
}
