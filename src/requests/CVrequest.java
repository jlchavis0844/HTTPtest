package requests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import localDB.LocalDB;
import model.Issue;
import model.Volume;

/**
 * A class to simplify the sending of API requests to the ComicVine API.<br>
 * This class should be called in the following manner:<br>
 * JSONObject = CVrequest.method("params");<br>
 * 
 * @author James
 *
 */
public class CVrequest {
	private static String api_key = "ebae3bcc02357fb42c9408727710be74f12576ce";
	private static String baseURL = "http://api.comicvine.com";
	private static JsonNode response = null;
	private static String limit = "100";

	/**
	 * Default search, limit is 10, no given resource
	 * 
	 * @param qString
	 *            = the term to search for
	 * @return JSONObject of the response body, values are:<br>
	 *         image JSON array<br>
	 * 		Aliases<br>
	 * 		Gender<br>
	 * 		site_detail_url<br>
	 * 		deck<br>
	 * 		origin array<br>
	 *         resource_type<br>
	 * 		birth<br>
	 * 		description<br>
	 * 		real_name<br>
	 * 		api_detail_url<br>
	 * 		date_added<br>
	 * 		name<br>
	 *         "publisher" array<br>
	 * 		id<br>
	 * 		first_appeared_in_issue<br>
	 * 		count_of_issue_appearances<br>
	 * 		date_last_updated<br>
	 */
	public static JSONObject search(String qString) {
		try {
			response = Unirest.get(baseURL + "/search").header("Accept", "application/json")
					.queryString("api_key", api_key).queryString("client", "cvscrapper").queryString("query", qString)
					.queryString("format", "json").queryString("limit", limit).asJson().getBody();
			return response.getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This search gives even more search options including query. resource,
	 * limit, and field_list
	 * 
	 * @param qString
	 *            - String to search for
	 * @param rString
	 *            - String of the resource being searched
	 * @param limit
	 *            - int of how many results to return, max of 100
	 * @param fieldList
	 *            - Array of Strings of field names to return in addition to
	 *            header: character<br>
	 * 			concept<br>
	 * 			origin<br>
	 * 			object<br>
	 * 			location<br>
	 * 			issue<br>
	 * 			story_arc<br>
	 * 			volume<br>
	 * 			publisher<br>
	 *            person<br>
	 * 			team<br>
	 * 			video
	 * @return JsonNode of the response body, depends on resource
	 */
	public static JSONObject search(String qString, String rString, int lim, String[] fieldList) {
		String fList = "";

		for (int i = 0; i < fieldList.length; i++) {
			fList += fieldList[i];
			if (i != fieldList.length - 1)
				fList += ",";
		}

		System.out.println(fList);

		try {
			response = Unirest.get(baseURL + "/search").header("Accept", "application/json")
					.queryString("api_key", api_key).queryString("client", "cvscrapper").queryString("query", qString)
					.queryString("resources", rString).queryString("field_list", fList).queryString("format", "json")
					.queryString("limit", String.valueOf(lim)).asJson().getBody();
			return response.getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Search with option of limit and resource string.
	 * 
	 * @param qString
	 *            - String to search for
	 * @param limit
	 *            - int of how many results to return, max of 100
	 * @param rString
	 *            - what resource to search for
	 * @return JsonNode of the response body, depends on resource
	 */
	public static JSONObject search(String qString, int limit, String rString) {
		try {
			response = Unirest.get(baseURL + "/search").header("Accept", "application/json")
					.queryString("api_key", api_key).queryString("client", "cvscrapper").queryString("query", qString)
					.queryString("resources", rString).queryString("format", "json")
					.queryString("limit", String.valueOf(limit)).asJson().getBody();
			return response.getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private static String loadApiKey(){
		return "test";
	}

	/**
	 * Easy volume search, returns ArrayList of the volumes
	 * 
	 * @param qString
	 *            - what volume to search for
	 * @return ArrayList<Volume> of the search results,fields: name<br>
	 * 		start_year<br>
	 * 		publisher array<br>
	 * 		id<br>
	 * 		count_of_issues<br>
	 * 		image array
	 * 
	 */
	public static ArrayList<Volume> searchVolume(String qString) {

		ArrayList<Volume> vols = new ArrayList<>();
		try {
			response = Unirest.get(baseURL + "/search").header("Accept", "application/json")
					.queryString("api_key", api_key).queryString("client", "cvscrapper").queryString("query", qString)
					.queryString("resources", "volume")
					.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
					.queryString("format", "json").queryString("limit", "100").asJson().getBody();

			int resNum = response.getObject().getInt("number_of_total_results");
			int pages = (resNum / 100) + 1;
			System.out.println("found " + resNum + " in " + pages);
			
			JSONObject jo = response.getObject();
			Vector<JSONArray> allResults = new Vector<>();
			allResults.addElement(jo.getJSONArray("results"));

			AtomicInteger gets = new AtomicInteger(1);
			for (int i = 1; i < pages; i++) {
				Future<HttpResponse<JsonNode>> future1 = Unirest.get(baseURL + "/search")
						.header("Accept", "application/json").queryString("api_key", api_key)
						.queryString("client", "cvscrapper").queryString("query", qString)
						.queryString("resources", "volume")
						.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
						.queryString("offset", String.valueOf(100 * i)).queryString("format", "json")
						.queryString("limit", "100").asJsonAsync(new Callback<JsonNode>() {
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

			JSONObject tempJO;
			String pubName = "";
			for (JSONArray j : allResults) {
				for (int k = 0; k < j.length() && vols.size() < 30; k++) {
					tempJO = j.getJSONObject(k);
					vols.add(new Volume(tempJO));
				}
			}

			System.out.println("returning " + vols.size());
			LocalDB.sortVolumesByStartCountOfIssues(vols, false);
			return vols;
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Super volume search, returns ArrayList of the volumes that published by
	 * publisher param
	 * 
	 * @param qString
	 *            - what volume to search for
	 * @param publisher
	 * @return ArrayList<Volume> of the search results,fields: name<br>
	 * 		start_year<br>
	 * 		publisher array<br>
	 * 		id<br>
	 * 		count_of_issues<br>
	 * 		image array
	 * 
	 */
	public static ArrayList<Volume> searchVolume(String qString, String publisher) {

		ArrayList<Volume> vols = new ArrayList<>();
		try {
			response = Unirest.get(baseURL + "/search").header("Accept", "application/json")
					.queryString("api_key", api_key).queryString("client", "cvscrapper").queryString("query", qString)
					.queryString("resources", "volume")
					.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
					.queryString("format", "json").queryString("limit", "100").asJson().getBody();

			int resNum = response.getObject().getInt("number_of_total_results");
			int pages = (resNum / 100) + 1;
			System.out.println("found " + resNum + " in " + pages + " pages");
			JSONObject jo = response.getObject();
			Vector<JSONArray> allResults = new Vector<>();
			allResults.addElement(jo.getJSONArray("results"));

			AtomicInteger gets = new AtomicInteger(1);
			for (int i = 1; i < pages; i++) {
				Future<HttpResponse<JsonNode>> future1 = Unirest.get(baseURL + "/search")
						.header("Accept", "application/json").queryString("api_key", api_key)
						.queryString("client", "cvscrapper").queryString("query", qString)
						.queryString("resources", "volume")
						.queryString("field_list", "name,start_year,publisher,id,count_of_issues,image")
						.queryString("offset", String.valueOf(100 * i)).queryString("format", "json")
						.queryString("limit", "100").asJsonAsync(new Callback<JsonNode>() {
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

			HashMap<String, Volume> retVols = new HashMap<String, Volume>();
			
			JSONObject tempJO;
			String pubName = "";
			for (JSONArray j : allResults) {
				for (int k = 0; k < j.length() && retVols.size() < 30; k++) {
					tempJO = j.getJSONObject(k);
					System.out.println("looking at " + tempJO.getInt("id"));
					if (!tempJO.isNull("publisher") && !tempJO.isNull("name")) {
						pubName = tempJO.getJSONObject("publisher").get("name").toString();
						if (pubName.contains(publisher)) {
							retVols.put(tempJO.get("id").toString(), new Volume(tempJO));
						}
					}
				}
			}
			vols.addAll(retVols.values());
			System.out.println("returning " + vols.size());
			LocalDB.sortVolumesByStartCountOfIssues(vols, false);
			return vols;
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns information for the given Volume ID
	 * 
	 * @param volID
	 *            String of the volume ID
	 * @return JSONObject with the following fields:<br>
	 *         name<br>
	 * 		start_year<br>
	 * 		publisher<br>
	 * 		image<br>
	 * 		count_of_issues<br>
	 * 		id
	 */
	public static JSONObject getVolumeInfo(String volID) {
		try {
			String query = "http://comicvine.gamespot.com/api/volume/4050-" + volID;
			response = Unirest.get(query).header("Accept", "application/json").queryString("api_key", api_key)
					.queryString("client", "cvscrapper")
					.queryString("field_list", "name,start_year,publisher,image,count_of_issues,id")
					.queryString("format", "json").queryString("limit", "100").asJson().getBody();
			return response.getObject().getJSONObject("results");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns a JSONArray of <b>ALL</b> the issues for a volume
	 * 
	 * @param volID
	 *            - String of the unique identifier for the volume you want
	 *            results for
	 * @return JSONArray sorted by cover_date (oldest first) with the following
	 *         fields:<br>
	 *         image json object<br>
	 * 		issue_number<br>
	 * 		id<br>
	 * 		name<br>
	 * 		cover_date
	 */
	public static ArrayList<Issue> getVolumeIDs(String volID) {
		ArrayList<Issue> list = new ArrayList<>();
		;

		try {
			JsonNode jn = Unirest.get(baseURL + "/issues").header("Accept", "application/json")
					.queryString("api_key", api_key).queryString("client", "cvscrapper").queryString("format", "json")
					.queryString("limit", "100")
					.queryString("field_list", "name,issue_number,id,image,cover_date,deck,description")
					.queryString("filter", "volume:" + volID).queryString("sort", "cover_date")
					// .queryString("page", String.valueOf(pageNum))
					.asJson().getBody();

			int resNum = jn.getObject().getInt("number_of_total_results");
			int pages = (resNum / 100) + 1;
			JSONObject jo = jn.getObject();
			JSONArray allResults[] = new JSONArray[pages];
			allResults[0] = jo.getJSONArray("results");

			for (int i = 1; i < pages; i++) {
				allResults[i] = Unirest.get(baseURL + "/issues").header("Accept", "application/json")
						.queryString("api_key", api_key).queryString("client", "cvscrapper")
						.queryString("format", "json").queryString("limit", "100")
						.queryString("offset", String.valueOf(100 * i)).queryString("sort", "cover_date")
						.queryString("field_list", "name,issue_number,id,image,cover_date,deck,description")
						.queryString("filter", "volume:" + volID).asJson().getBody().getObject()
						.getJSONArray("results");
			}

			Issue temp = null;
			for (JSONArray j : allResults) {
				for (int k = 0; k < j.length(); k++) {
					// allIssues.put(j.get(k));
					temp = new Issue(j.getJSONObject(k));
					list.add(temp);
				}
			}
			return list;
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns all data for the given issue id
	 * 
	 * @param issId
	 *            - String of the comic's unqiue ID
	 * @return JSON object with the following keys person_credits<br>
	 *         concept_credits<br>
	 *         first_appearance_storyarcs<br>
	 *         aliases<br>
	 *         deck<br>
	 *         description<br>
	 *         api_detail_url<br>
	 *         issue_number<br>
	 *         location_credits<br>
	 *         cover_date<br>
	 *         id<br>
	 *         date_last_updated<br>
	 *         store_date<br>
	 *         character_credits<br>
	 *         first_appearance_locations<br>
	 *         image<br>
	 *         site_detail_url<br>
	 *         first_appearance_objects<br>
	 *         first_appearance_concepts<br>
	 *         first_appearance_characters<br>
	 *         volume<br>
	 * 		date_added<br>
	 *         first_appearance_teams<br>
	 *         team_credits<br>
	 * 		name<br>
	 * 		story_arc_credits<br>
	 * 		character_died_in<br>
	 * 		object_credits<br>
	 * 		has_staff_review<br>
	 *         team_disbanded_in
	 */
	public static JSONObject getIssue(String issId) {
		try {
			String query = "http://comicvine.gamespot.com/api/issue/4000-" + issId;
			response = Unirest.get(query).queryString("api_key", api_key).queryString("format", "json").asJson()
					.getBody();
			return response.getObject().getJSONObject("results");
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
};