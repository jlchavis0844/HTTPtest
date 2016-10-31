package requests;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import localDB.LocalDB;
/**
 * This class if meant to be a static class used to make calls to PHP scripts that make the SQL calls<br>
 * There will be 4 main types of commands:<br>
 * Register and new user - SQLQuery.register()<br>
 * check login credentials - to be written<br>
 * upload new ids to database - SQLQuery.sendIDs()<br>
 * get id's from database - SQLQuery.getIDs()<br>
 * 
 * for now, PHP files are located at : http://76.94.123.147:49180
 * @author James
 *
 */
public class SQLQuery {

	public static void test(){
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("user", "testUser");
		jo.put("password","password");
		ja.put(new JSONObject("{\"id\":\"5855\"}"));
		ja.put(new JSONObject("{\"id\":\"5856\"}"));
		ja.put(new JSONObject("{\"id\":\"5857\"}"));
		ja.put(new JSONObject("{\"id\":\"5858\"}"));
		jo.put("id_list", ja);
		//OUT:{"password":"password","id_list":[{"id":"5855"},{"id":"5856"},{"id":"5857"},{"id":"5858"}],"user":"testUser"}
		try {
			HttpResponse<JsonNode> response = Unirest.post("http://76.94.123.147:49180/JSONtest.php")
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
					.body(jo)
					.asJson();
			System.out.println(response.toString());
			System.out.println(response.getBody().toString());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Used to add a user to the login table in the longbox database<br>
	 * 
	 * @param user String of the user name
	 * @param pass String of the password
	 * @return String of the registration responses:
	 * failed on id query, + error<br>
	 * 'registration failed, ' + error<br>
	 * registration worked'<br>
	 * "registration failed, user name $lbUser exists"<br>
	 */
	public static String register(String user, String pass){
		JSONObject jo = new JSONObject();
		jo.put("user", user);//write user
		jo.put("password", pass);//write password

		try {//make the HTTP call
			HttpResponse<String> response = Unirest.post("http://76.94.123.147:49180/LBregister.php")
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
					.body(jo)
					.asString();
			return response.getBody().toString();//string of the response
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "poop";//TODO: change to error message
	}

	/**
	 * gets the ID's added after a certain date, returns in a JSONObect
	 * @param user String of the User
	 * @param pass String of the password
	 * @param timeStamp String of the after date "YYYY-MM-DD hr:mn:sc"
	 * @return JSONObject with the following form:
	 * {"id_list":["1234","5678","91011"]}
	 */
	public static JSONObject getIDs(String user, String pass, String timeStamp){
		JSONObject jo = new JSONObject();
		jo.put("user", user);
		jo.put("password", pass);
		jo.put("timeStamp", timeStamp);

		try {
			HttpResponse<JsonNode> response = Unirest.post("http://76.94.123.147:49180/LBgetID.php")
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
					.body(jo)
					.asJson();
			return response.getBody().getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes newly owned IDs to issues table with the given info
	 * @param user String of the user name
	 * @param pass String of the password
	 * @param idArr String array of the id's to send
	 * @return JASONObject of the insert results, ie
	 * on insert error: {"91011":"insert error, Duplicate entry 'user6-91011' for key 'PRIMARY'"}<br>
	 * on insert success {"91011" : "2016-10-02 12:13:14"}
	 */
	public static JSONObject sendIDs(String user, String pass, String[] idArr){
		JSONObject retVal = null;
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("user", user);
		jo.put("password",pass);
		//ja.put(new JSONObject("{\"id\":\"5855\"}"));

		for(String s: idArr){
			ja.put(new JSONObject("{\"id\" : \"" + s +"\"}"));
		}

		jo.put("id_list", ja);

		try {
			HttpResponse<JsonNode> response = Unirest.post("http://76.94.123.147:49180/LBsendID.php")
					.header("accept", "application/json")
					.header("Content-Type", "application/json")
					.body(jo)
					.asJson();
			retVal = response.getBody().getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	public static String[] getLoginInfo(){
		String info[] = new String[3];

		String SQLinfo = "SELECT * FROM login";
		ResultSet rs = LocalDB.executeQuery(SQLinfo);

		try {
			rs.next();
			info[0] = rs.getString("userName");
			info[1] = rs.getString("password");
			info[2] = rs.getString("profile");

			for(String s: info){
				System.out.print(s + "\t");
			}
			System.out.println();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}
}
