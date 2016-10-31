package requests;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import model.Issue;
import model.Volume;
import scenes.VolumePreview;

/**
 * DO not use this class, this is for testing only
 * @author James
 *
 */
@SuppressWarnings("unused")
public class CVrequestAsync {
	private static String api_key = "ebae3bcc02357fb42c9408727710be74f12576ce";
	private static String baseURL = "http://api.comicvine.com";
	private static JsonNode response = null;
	private static String limit = "100";
	private static int cntr = -1;


	/**
	 * Super volume search, returns ArrayList of the volumes that published by publisher param
	 * @param qString - what volume to search for
	 * @param publisher
	 * @return ArrayList<Volume> of the search results,fields:
	 * name<br>start_year<br>publisher array<br>id<br>count_of_issues<br>image array
	 * 
	 */
	//	public static ArrayList<Volume> searchVolume(String qString, String publisher){
	//
	//		ArrayList<Volume> vols = new ArrayList<>();
	//		try {
	//			response = Unirest.get(baseURL + "/search")
	//					.header("Accept", "application/json")
	//					.queryString("api_key", api_key)
	//					.queryString("client","cvscrapper")
	//					.queryString("query", qString)
	//					.queryString("resources", "volume")
	//					.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
	//					.queryString("format", "json")
	//					.queryString("limit", "100")
	//					.asJson().getBody();
	//
	//			int resNum = response.getObject().getInt("number_of_total_results");
	//			System.out.println("found " + resNum);
	//			int pages = (resNum/100)+1;		
	//			JSONObject jo = response.getObject();
	//			Vector<JSONArray> allResults = new Vector<>(); 
	//			allResults.addElement(jo.getJSONArray("results"));
	//
	//			AtomicInteger gets = new AtomicInteger(1);
	//			for(int i = 1; i < pages; i++){
	//				Future <HttpResponse<JsonNode>> future1 = Unirest.get(baseURL + "/search")
	//						.header("Accept", "application/json")
	//						.queryString("api_key", api_key)
	//						.queryString("client","cvscrapper")
	//						.queryString("query", qString)
	//						.queryString("resources", "volume")
	//						.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
	//						.queryString("offset", String.valueOf(100*cntr))
	//						.queryString("format", "json")
	//						.queryString("limit", "100")
	//						.asJsonAsync(new Callback<JsonNode>(){
	//							public void completed(HttpResponse<JsonNode> response) {
	//								System.out.println("Thread " + Thread.currentThread().getId() + " writing");
	//								allResults.addElement(response.getBody().getObject().getJSONArray("results"));
	//								System.out.println(gets.incrementAndGet());
	//							}
	//
	//							@Override
	//							public void failed(UnirestException e) {
	//								// TODO Auto-generated method stub
	//								e.printStackTrace();
	//							}
	//
	//							@Override
	//							public void cancelled() {
	//								// TODO Auto-generated method stub
	//								System.out.println("cancelling request");
	//							}
	//						});
	//			}
	//			
	//			//wait until they are all fetched
	//			System.out.println("Thread " + Thread.currentThread().getId() + " waitings");
	//			
	//			while(gets.get() < pages){}
	////			try {
	////				Unirest.shutdown();
	////			} catch (IOException e) {
	////				// TODO Auto-generated catch block
	////				e.printStackTrace();
	////			}
	//			//Volume temp = null;
	//			JSONObject tempJO;
	//			String pubName = "";
	//			for(JSONArray j: allResults){				
	//				for(int k = 0; k < j.length() && vols.size() < 30; k++){
	//					tempJO = j.getJSONObject(k);
	//					if (!tempJO.isNull("publisher") && !tempJO.isNull("name")){
	//						pubName = tempJO.getJSONObject("publisher").get("name").toString();
	//						if(pubName.contains(publisher)){
	//							vols.add(new Volume(tempJO));
	//						}
	//					}
	//				}
	//			}						
	//
	//			System.out.println("returning " + vols.size());
	//			return vols;
	//		} catch (UnirestException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		return null;
	//	}


	public static ArrayList<Volume> searchVolume(String qString){

		ArrayList<Volume> vols = new ArrayList<>();
		try {
			response = Unirest.get(baseURL + "/search")
					.header("Accept", "application/json")
					.queryString("api_key", api_key)
					.queryString("client","cvscrapper")
					.queryString("query", qString)
					.queryString("resources", "volume")
					.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
					.queryString("format", "json")
					.queryString("limit", "100")
					.asJson().getBody();

			int resNum = response.getObject().getInt("number_of_total_results");
			System.out.println("found " + resNum);
			JSONObject jo = response.getObject();
			JSONArray ja = jo.getJSONArray("results");
			JSONObject tempJO;
			String pubName = "";
			
			for(int i = 0; i < 50 && i < resNum; i++){				
				tempJO = ja.getJSONObject(i);
				vols.add(new Volume(tempJO));
			}
			
			System.out.println("returning " + vols.size());
			return vols;
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

};