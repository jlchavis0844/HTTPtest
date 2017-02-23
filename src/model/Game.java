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

public class Game {
	private String name;
	private String relDate;
	private String id;
	private String icon_url;
	private String deck;
	private ArrayList<String> platforms;
	private JSONObject game;
	private int cycles = 0;
	
	public Game(JSONObject g) {
		game = g;
		platforms = new ArrayList<String>();
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
					game.get("original_release_date").equals(null)){
				if(game.has("expected_release_day") &&
				   game.has("expected_release_month") &&
				   game.has("expected_release_year") &&
				   !game.getJSONObject("expected_release_day").equals(null) &&
				   !game.getJSONObject("expected_release_month").equals(null) &&
				   !game.getJSONObject("expected_release_year").equals(null)){
					relDate = game.getInt("expected_release_year") + "-" + 
							  game.getInt("expected_release_month") + "-" + 
				   			  game.getInt("expected_release_day");
				} else relDate = null;
			} else if(game.has("original_release_date") && 
					!game.get("original_release_date").equals(null)){
				String[] parts = game.get("original_release_date").toString().split("[,\\s\\-:\\?]");
				relDate = parts[0] + "-" + parts[1] + "-" + parts[2]; 
			} else relDate = null;
			
			if(check(game, "image")){//check and get iconURL
				if(!game.getJSONObject("image").getString("icon_url").equals(null)){
					icon_url = game.getJSONObject("image").getString("icon_url");
				} else icon_url = null;
			}
			
			if(check(game, "platforms")){ // load and array of all the platforms
				JSONArray ja = game.getJSONArray("platforms");
				for(int i = 0; i < ja.length(); i++){
					
					platforms.add(ja.get(i).toString());
				}
			}
		}
	}

	public Image getRemoteImage(String urlStr) {
		URL url = null;
		BufferedImage img = null;
		
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

	public String getId() {
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

	public ArrayList<String> getPlatforms() {
		if(platforms == null){
			return new ArrayList<String>();
		}
		return platforms;
	}

	public void setPlatforms(ArrayList<String> platforms) {
		this.platforms = platforms;
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

};
