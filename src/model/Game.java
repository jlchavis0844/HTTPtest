package model;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import requests.GBrequest;

public class Game {
	private String name;
	private String relDate;
	private String id;
	private String icon_url;
	private String deck;
	private String api_detail_url;
	private String description;
	private String original_game_rating;
	private String site_detail_url;
	private String videoLink;
	private ArrayList<String> characters;
	private String franchise;
	private ArrayList<String> genres;
	private ArrayList<String> publishers;
	private ArrayList<String> platsShort;
	private ArrayList<String> platsLong;
	private JSONObject game;
	private int cycles = 0;
	private static boolean full = false;
	
	public Game(JSONObject g) {
		game = g;
		platsShort = new ArrayList<String>();
		platsLong = new ArrayList<String>();
		characters = new ArrayList<String>();
		genres = new ArrayList<String>();
		publishers = new ArrayList<String>(); 
		
		if (check(game, "id")) {
			this.id = game.get("id").toString();
		} else {
			System.out.println("id not found");
		}
		init();
		
	}
	
	private void init() {
		cycles++;
		if (cycles < 10) {//ensure no infinite loops
			if (check(game, "name")) {//check and add name
				name = game.get("name").toString();
			} else
				name = null;

			if (check(game, "deck")) {//check and add deck
				deck = game.get("deck").toString();
			} else
				deck = null;
			
			System.out.println(game.get("original_release_date").equals(null));

			if(game.has("original_release_date") &&//check and add release date
					game.isNull("original_release_date")){
				if(game.has("expected_release_day") &&
				   game.has("expected_release_month") &&
				   game.has("expected_release_year") &&
				   !game.isNull("expected_release_day") &&
				   !game.isNull("expected_release_month") &&
				   !game.isNull("expected_release_year")){
					relDate = game.getInt("expected_release_year") + "-" + 
							  game.getInt("expected_release_month") + "-" + 
				   			  game.getInt("expected_release_day");
				} else relDate = null;
			} else if(game.has("original_release_date") && 
					!game.isNull("original_release_date")){
				String[] parts = game.get("original_release_date").toString().split("[,\\s\\-:\\?]");
				relDate = parts[0] + "-" + parts[1] + "-" + parts[2]; 
			} else relDate = null;
			
			if(check(game, "image")){//check and get iconURL
				if(!game.getJSONObject("image").getString("icon_url").equals(null)){
					icon_url = game.getJSONObject("image").getString("icon_url");
				} else icon_url = null;
			}			
		}
	}

	public Image getRemoteImage(String urlStr) {
		URL url = null;
		BufferedImage img = null;
		if(urlStr == null || urlStr.equals("")){
			return null;
		}
		try {
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31"
					+ "(KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			// img = ImageIO.read(conn.getInputStream()); Faster, but easily
			// corrupted
			img = ImageIO.read(ImageIO.createImageInputStream(conn.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SwingFXUtils.toFXImage(img, null);
	}
	
	public Image getRemoteIcon(){
		if(icon_url != "" || icon_url != null){
			return getRemoteImage(icon_url);
		}
		return null;
	}
	
	public Image getRemoteMedium(){
		if(check(game, "image") && game.getJSONObject("image")
				.has("medium_url")){
			return getRemoteImage(game.getJSONObject("image")
					.getString("medium_url"));
		} else return null;
	}
	
	public Image getRemoteThumb(){
		if(check(game, "image") && game.getJSONObject("image")
				.has("thumb_url")){
			return getRemoteImage(game.getJSONObject("image")
					.getString("thumb_url"));
		} else return null;
	}	
	
	public Image getRemoteScreen(){
		if(check(game, "image") && game.getJSONObject("image")
				.has("screen_url")){
			return getRemoteImage(game.getJSONObject("image")
					.getString("screen_url"));
		} else return null;
	}
	
	public String getName() {
		if(name == null){
			return "";
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelDate() {
		if(relDate == null){
			return "";
		}
		return relDate;
	}

	public void setRelDate(String relDate) {
		this.relDate = relDate;
	}

	public String getID() {
		return id;
	}
	
	public String getIcon_url() {
		if(icon_url == null){
			return "";
		}
		return icon_url;
	}

	public void setIcon_url(String icon_url) {
		this.icon_url = icon_url;
	}

	public String getDeck() {
		if(deck == null){
			return "";
		}
		return deck;
	}

	public void setDeck(String deck) {
		this.deck = deck;
	}

	public String getPlatsShort() {
		if(platsShort == null){
			return "";
		}
		String platStr = platsShort.toString();
		platStr = platStr.replaceAll("[\\[\\](){}]","");
		return platStr;
	}
	
	public ArrayList<String> getPlatsShortArr(){
		if(platsShort == null){
			return new ArrayList<String>();
		} else return platsShort;
		
	}
	
	public String getPlatsLong() {
		if(platsLong == null){
			return "";
		}
		String platStr = platsLong.toString();
		platStr = platStr.replaceAll("[\\[\\](){}]","");
		return platStr;
	}
	
	public ArrayList<String> getPlatsLongArr(){
		if(platsLong == null){
			return new ArrayList<String>();
		} else return platsLong;
		
	}

	public JSONObject getGame() {
		return game;
	}

	public void setGame(JSONObject game) {
		this.game = game;
	}

	public static boolean check(JSONObject jo, String target) {
		if (jo.has(target)) {
			String val = jo.get(target).toString();
			return (val != null && val.length() != 0 && !val.equals("null"));
		} else
			return false;
	}
	
	public void populate(){
		full = true;
		if(check(game, "api_detail_url")){
			api_detail_url = game.getString("api_detail_url");
		} else api_detail_url = null;
		
		game = GBrequest.getFullGame(api_detail_url);
		init();
		
		if(check(game, "description")){
			description = game.getString("description");
		} else description = "";
		
		if(check(game, "original_game_rating")){
			JSONArray ja = game.getJSONArray("original_game_rating");
			for(int i = 0; i < ja.length(); i++){
				if(ja.getJSONObject(i).getString("name").contains("ESRB")){
					original_game_rating = ja.getJSONObject(i).getString("name");
					break;
				}
			}
			if(original_game_rating == null){
				original_game_rating = "";
			}
		} else original_game_rating = "";
		
		if(check(game, "platforms")){ // load and array of all the playform's abbreviations
			JSONArray ja = game.getJSONArray("platforms");
			for(int i = 0; i < ja.length(); i++){
				platsShort.add(ja.getJSONObject(i).getString("abbreviation"));
				platsLong.add(ja.getJSONObject(i).getString("name"));
			}
		}
		
		if(check(game, "publishers")){ // load and array of all the publishers
			JSONArray ja = game.getJSONArray("publishers");
			for(int i = 0; i < ja.length(); i++){
				publishers.add(ja.getJSONObject(i).getString("name"));
			}
		}
		
		if(check(game, "franchises") && !game.getJSONArray("franchises").isNull(0) 
				&& !game.getJSONArray("franchises").getJSONObject(0).isNull("name") ){
			franchise = game.getJSONArray("franchises").getJSONObject(0).getString("name");
		} else franchise = null;
		
		if(check(game, "description")){
			description = game.getString("description");
		} else description = null;
		
		if(check(game, "videos") && !game.getJSONArray("videos").isNull(0)){
			String tempLink = game.getJSONArray("videos").getJSONObject(0)
					.getString("api_detail_url");
			videoLink = GBrequest.getHTTPVideo(tempLink);
		} else videoLink = null;
		
		if(check(game, "site_detail_url")){
			site_detail_url = game.getString("site_detail_url");
		} else site_detail_url = null;
		
		if(check(game, "characters")){
			if(characters == null){
				characters = new ArrayList<String>();
			}
			
			JSONArray ja = game.getJSONArray("characters");
			
			for(int i = 0; i < ja.length(); i++){
				characters.add(ja.getJSONObject(i).getString("name"));
			}
		}
	}
	
	public String getPublishers(){
		if(publishers != null){
			return publishers.toString().replaceAll("[\\[\\](){}]","");
		} else return "";
	}
	
	public String getFranchise(){
		if(franchise != null){
			return franchise;
		} else return "";
	}
	
	public String getDescription(){
		if(description != null){
			return description;
		} else return "";
	}
	
	public String getVideo(){
		if(videoLink != null){
			return videoLink;
		} else return "";
	}
	
	public boolean equals(Game rhs){
		return id.equals(rhs.getID());
	}
	
	public Image getImage(String type){
		return getRemoteThumb();
	}
	
	public boolean isFull(){
		return full;
	}

	public String getSiteURL(){
		if(site_detail_url != null){
			return site_detail_url;
		} else return "";
	}
	
	public void setOwned(String value){
		game.put("OwnedOn", value);
	}
	
	public String getImgURL(String size){
		String URL = "";
		
		if(check(game, "image")){
			JSONObject jo = game.getJSONObject("image");
			
			switch(size){

			case "icon":
				URL = jo.getString("icon_url");
				break;
				
			case "medium":
				URL = jo.getString("medium_url");
				break;
				
			case "small":
				URL = jo.getString("small_url");
				break;
				
			case "super":
				URL = jo.getString("super_url");
				break;
				
			case "thumb":
				URL = jo.getString("thumb_url");
				break;
				
			case "tiny":
				URL = jo.getString("tiny_url");
				break;
				
			case "screen":
				URL = jo.getString("screen_url");
				break;
			
			default:
				URL = "";
				break;
			}
		}
		return URL;
	}
	
};
