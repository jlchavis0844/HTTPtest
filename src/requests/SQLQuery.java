package requests;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import localDB.LocalDB;
import model.Issue;

/**
 * This class if meant to be a static class used to make calls to PHP scripts
 * that make the SQL calls<br>
 * There will be 4 main types of commands:<br>
 * Register and new user - SQLQuery.register()<br>
 * check login credentials - to be written<br>
 * upload new ids to database - SQLQuery.sendIDs()<br>
 * get id's from database - SQLQuery.getIDs()<br>
 * 
 * for now, PHP files are located at : http://76.94.123.147:49180
 * 
 * @author James
 *
 */
public class SQLQuery {

	/**
	 * Used to add a user to the login table in the longbox database<br>
	 * 
	 * @param user
	 *            String of the user name
	 * @param pass
	 *            String of the password
	 * @return String of the registration responses: failed on id query, +
	 *         error<br>
	 *         'registration failed, ' + error<br>
	 *         registration worked'<br>
	 *         "registration failed, user name $lbUser exists"<br>
	 */
	public static String register(String user, String pass) {
		JSONObject jo = new JSONObject();
		jo.put("user", user);// write user
		jo.put("password", pass);// write password

		try {// make the HTTP call
			HttpResponse<String> response = Unirest.post("http://76.94.123.147:49180/LBregister.php")
					.header("accept", "application/json").header("Content-Type", "application/json").body(jo)
					.asString();
			return response.getBody().toString();// string of the response
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Logic error";// TODO: change to error message
	}

	/**
	 * gets the ID's added after a certain date, returns in a JSONObect
	 * 
	 * @param user
	 *            String of the User
	 * @param pass
	 *            String of the password
	 * @param timeStamp
	 *            String of the AFTER date "YYYY-MM-DD hr:mn:sc"
	 * @return ArrayList<String> of the id's in string format
	 */
	public static ArrayList<String> getIDs(String user, String pass, String timeStamp) {
		JSONObject jo = new JSONObject();
		jo.put("user", user);
		jo.put("password", pass);
		jo.put("timeStamp", timeStamp);

		ArrayList<String> retVals = new ArrayList<>();
		try {
			HttpResponse<JsonNode> response = Unirest.post("http://76.94.123.147:49180/LBgetID.php")
					.header("accept", "application/json").header("Content-Type", "application/json").body(jo).asJson();
			JSONArray ja = response.getBody().getObject().getJSONArray("id_list");

			for (int i = 0; i < ja.length(); i++) {
				retVals.add(ja.getString(i));
			}

		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVals;
	}

	/**
	 * gets the ID's deleted after a certain date, returns ArrayList of id's in
	 * string
	 * 
	 * @param user
	 *            String of the User
	 * @param pass
	 *            String of the password
	 * @param timeStamp
	 *            String of the AFTER date "YYYY-MM-DD hr:mn:sc"
	 * @return ArrayList<String> of the id's in a string format
	 */
	public static ArrayList<String> getDeletedIDs(String user, String pass, String timeStamp) {
		JSONObject jo = new JSONObject();
		jo.put("user", user);
		jo.put("password", pass);
		jo.put("timeStamp", timeStamp);

		ArrayList<String> retVals = new ArrayList<>();
		try {
			HttpResponse<JsonNode> response = Unirest.post("http://76.94.123.147:49180/LBgetDeletedID.php")
					.header("accept", "application/json").header("Content-Type", "application/json").body(jo).asJson();
			JSONArray ja = response.getBody().getObject().getJSONArray("id_list");

			for (int i = 0; i < ja.length(); i++) {
				retVals.add(ja.getString(i));
			}

		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVals;
	}

	/**
	 * Writes newly owned IDs to issues table with the given info
	 * 
	 * @param user
	 *            String of the user name
	 * @param pass
	 *            String of the password
	 * @param idArr
	 *            String array of the id's to send
	 * @return JASONObject of the insert results, ie on insert error:
	 *         {"91011":"insert error, Duplicate entry 'user6-91011' for key
	 *         'PRIMARY'"}<br>
	 *         on insert success {"91011" : "2016-10-02 12:13:14"}
	 */
	public static JSONObject sendIDs(String user, String pass, String[] idArr) {
		JSONObject retVal = null;
		JSONArray ja = new JSONArray();
		JSONObject jo = new JSONObject();
		jo.put("user", user);
		jo.put("password", pass);
		// ja.put(new JSONObject("{\"id\":\"5855\"}"));

		for (String s : idArr) {
			ja.put(new JSONObject("{\"id\" : \"" + s + "\"}"));
		}

		jo.put("id_list", ja);

		try {
			HttpResponse<JsonNode> response = Unirest.post("http://76.94.123.147:49180/LBsendID.php")
					.header("accept", "application/json").header("Content-Type", "application/json").body(jo).asJson();
			retVal = response.getBody().getObject();
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	public static boolean login(String user, String pass) {
		boolean retVal = false;
		JSONObject jo = new JSONObject();
		jo.put("user", user);
		jo.put("password", pass);

		try {
			HttpResponse<String> response = Unirest.post("http://76.94.123.147:49180/LBlogin.php")
					.header("content-type", "application/json").header("cache-control", "no-cache").body(jo).asString();
			System.out.println(response.getBody());
			retVal = Boolean.valueOf(response.getBody());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	public static String[] getLoginInfo() {
		String info[] = new String[4];

		String SQLinfo = "SELECT * FROM login";
		ResultSet rs = LocalDB.executeQuery(SQLinfo);

		try {
			rs.next();
			if (!rs.isClosed()) {
				info[0] = rs.getString("user");
				info[1] = rs.getString("password");
				info[2] = rs.getString("timestamp");
				info[3] = rs.getString("auto");
				rs.close();
			}
			for (String s : info) {
				System.out.print(s + "\t");
			}
			System.out.println();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}

	public static boolean setLoginInfo(String info[]) {
		String query = "SELECT COUNT (*) FROM login WHERE user='" + info[0] + "';";
		ResultSet rs = LocalDB.executeQuery(query);
		int count = -1;

		try {
			rs.next();
			count = rs.getInt(1);
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (count == 0) {
			query = "INSERT INTO login (`user`, `password`, `timestamp`, `auto`) VALUES (" + "'" + info[0] + "', " + "'"
					+ info[1] + "', " + "'" + info[2] + "', " + "'" + info[3] + "'); ";
		} else {
			query = "UPDATE login SET `password` = '" + info[1] + "', `timestamp` = '" + info[2] + "', `auto` = '"
					+ info[3] + "' WHERE `user` = '" + info[0] + "';";
		}
		return LocalDB.executeUpdate(query);
	}

	public static boolean updateLoginInfo(String uName, String ts, String al) {
		String query = "UPDATE login SET `timestamp` = '" + ts + "', `auto` = '" + al + "' WHERE `user` = '" + uName
				+ "';";
		return LocalDB.executeUpdate(query);
	}

	public static boolean removeIssues(ArrayList<String> id) {
		Boolean result = false;

		for (String currID : id) {
			if (!LocalDB.deleteIssueByID(currID)) {
				System.out.println("failed to find local issue with id: " + currID);
			}
		}

		String info[] = getLoginInfo();

		try {
			boolean retVal = false;
			JSONObject jo = new JSONObject();
			jo.put("user", info[0]);
			jo.put("password", info[1]);
			for (String currID : id) {
				jo.put("issues", id);
			}

			HttpResponse<String> response = Unirest.post("http://jchavis.hopto.org:49180/LBremove.php")
					.header("content-type", "application/json").header("cache-control", "no-cache").body(jo).asString();
			System.out.println(response.getBody());
			retVal = Integer.valueOf(response.getBody()) == id.size();
			if (retVal == false) {
				System.out.println("Failed to delete all issues, remote removed: " + response.getBody());
			} else System.out.println("Deleted " + Integer.valueOf(response.getBody()) + " issues");

		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getCurrentTimeStamp() {
		java.util.Date date = new java.util.Date();
		Timestamp ts = new Timestamp(date.getTime());
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
		return timeStamp;
	}

	public static void fullSync() {
		String info[] = getLoginInfo();
		ArrayList<String> newRemote = getIDs(info[0], info[1], info[2]);
		for (String s : newRemote) {
			if (!LocalDB.exists(s, LocalDB.ISSUE)) {
				LocalDB.addIssue(new Issue(CVrequest.getIssue(s)));
			}
		}

		ArrayList<String> deletedIDs = getDeletedIDs(info[0], info[1], info[2]);
		for (String s : deletedIDs) {
			if (LocalDB.exists(s, LocalDB.ISSUE)) {
				LocalDB.deleteIssueByID(s);
			}
		}

		String[] allIDs = LocalDB.getAllIDs();
		if (allIDs != null && allIDs.length != 0) {
			sendIDs(info[0], info[1], allIDs);
			SQLQuery.updateLoginInfo(info[0], getCurrentTimeStamp(), info[3]);
		}
	}
}
