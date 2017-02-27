package requests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JFrame;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.Game;
import scenes.CrapPopUp;

public class GBrequest {
	private static String api_key = "db50a857ef685a24f0e3f58e4412273b01d8a426";
	private static String baseURL = "http://www.giantbomb.com/api";
	private static JsonNode response = null;
	
	public static ArrayList<Game> searchGame(String gName){
		ArrayList<Game> gameResults = new ArrayList<>();
		try {
			response = Unirest.get(baseURL + "/search")
					.header("Accept", "application/json")
					.queryString("api_key", api_key)
					.queryString("query", gName)
					.queryString("resources", "game")
					.queryString("format", "json")
					.queryString("limit", "100")
//					.queryString("field_list", "name,id,original_release_date,"
//							+ "expected_release_day,platforms,image,deck, "
//							+ "api_detail_url, site_detail_url")
					.asJson().getBody();
			
			int resultNum = response.getObject().getInt("number_of_total_results");
			int pages = (resultNum/100)+1;
			
			System.out.println("found " + resultNum + " in " + pages);
			
			JSONObject jo = response.getObject();
			Vector<JSONArray> allResults = new Vector<>();
			allResults.addElement(jo.getJSONArray("results"));

			AtomicInteger gets = new AtomicInteger(1);
			for (int i = 1; i < pages; i++) {
				Future<HttpResponse<JsonNode>> future1 = Unirest.get(baseURL + "/search")
						.header("Accept", "application/json")
						.queryString("api_key", api_key)
						.queryString("query", gName)
						.queryString("resources", "game")
						.queryString("format", "json")
						.queryString("limit", "100")
//						.queryString("field_list", "name,id,original_release_date,"
//								+ "expected_release_day,platforms,image,deck,"
//								+ "expected_release_month, expected_release_year"
//								+ "api_detail_url, site_detail_url")
						.asJsonAsync(new Callback<JsonNode>() {
							public void completed(HttpResponse<JsonNode> response) {
								System.out.println("Thread " + Thread.currentThread().getId() + " writing");
								allResults.addElement(response.getBody().getObject().getJSONArray("results"));
								System.out.println(gets.incrementAndGet());
							}

							@Override
							public void failed(UnirestException e) {
								// TODO Auto-generated method stub
								e.printStackTrace();
							}

							@Override
							public void cancelled() {
								// TODO Auto-generated method stub
								System.out.println("cancelling request");
							}
						});
			}

			// wait until they are all fetched
			System.out.println("Thread " + Thread.currentThread().getId() + " waitings");

			while (gets.get() < pages) {
			}
			
			if(allResults.get(0).isNull(0))
				return null;
			int counter = 0;
			System.out.println(allResults.get(0).getJSONObject(0));
			JSONArray tempJA = allResults.get(0);
			int size = tempJA.length();
			if(size > 30){
				size = 30;
			}
			
			for(int i = 0; i < size; i++){
				JSONObject tempJO = tempJA.getJSONObject(i);
				gameResults.add(new Game(tempJO));
				System.out.println(tempJA.getJSONObject(i).get("name"));
			}
//			allResults.forEach(currJA ->{
//				int size = currJA.length();
//				for(int i = 0; i < size; i++){
//					gameResults.add(new Game(currJA.getJSONObject(i)));
//					System.out.println(currJA.getJSONObject(i).get("name"));
//				}
//			});
			//Image img = gameResults.get(0).getRemoteScreen();
			//BufferedImage bimg = SwingFXUtils.fromFXImage(img, null);
			//new CrapPopUp(new JFrame(), "Testing", bimg);
			
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return gameResults;
	}
	
	public static JSONObject getFullGame(String api_url){
		try {
			response = Unirest.get(api_url)
					.header("Accept", "application/json")
					.queryString("api_key", api_key)
					.queryString("format", "json")
					.asJson().getBody();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response.getObject().getJSONObject("results");
	}
	
	public static String getHTTPVideo(String api_url){
		String fileLink = "";
		try {
			response = Unirest.get(api_url)
					.header("Accept", "application/json")
					.queryString("api_key", api_key)
					.queryString("format", "json")
					.asJson().getBody();
			fileLink = response.getObject().getJSONObject("results")
					.getString("embed_player");
					//.getString("low_url");
			System.out.println("---------------------------------------------"
					+ ":" + fileLink);
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileLink;
	}

}
