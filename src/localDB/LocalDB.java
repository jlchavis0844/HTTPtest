package localDB;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import model.Issue;
import model.Volume;
import requests.CVImage;
import requests.CVrequest;

public class LocalDB {

	private static String url = "jdbc:sqlite:./DigLongBox.db";
	private static Connection conn;
	private static Statement stat;
	public static int ISSUE = 0;
	public static int VOLUME = 1;

	public static boolean addIssue(Issue issue){
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			
			
			//stat.executeUpdate("DELETE FROM issue;");
			//stat.executeUpdate("VACUUM");
			String id = issue.getID();
			JSONObject jo = issue.getFullObject();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			String formattedDate = sdf.format(date);
			
			jo.put("timeStamp", formattedDate);
			jo.put("volName", jo.getJSONObject("volume").getString("name"));
			jo.put("JSON", jo.toString());
			
			//stat.executeUpdate("INSERT INTO issue (id) VALUES ('" + id + "');");
			String[] names = JSONObject.getNames(jo);
			String qNames = "INSERT INTO issue (";
			String qVals = "VALUES (";

			int nameNum = names.length;
			ArrayList<String> goodNames = new ArrayList<>();
			ArrayList<String> goodValues = new ArrayList<>();
			String value = "";
			String currName = "";
			
			for(int i = 0; i < nameNum; i++){
				currName = names[i];
				if(!jo.isNull(currName) && !currName.equals("image")){
					value = jo.get(names[i]).toString();
					if(!value.equals("[]")){
						goodNames.add(names[i]);
						goodValues.add(jo.get(names[i]).toString());
					}
				}
			}

			nameNum = goodNames.size();
			for(int i = 0; i < nameNum; i++){
				if(i != nameNum-1){
					qNames += (goodNames.get(i) + ", ");
					qVals += (" ? ,");
				} else {
					qNames += (goodNames.get(i) + ") ");
					qVals += (" ? );");
				}
			}
			
			String sql = qNames+ qVals;
			System.out.println(sql);
			PreparedStatement pre = conn.prepareStatement(sql); 
			
			for(int i = 0; i < nameNum; i++){
				pre.setString((i+1), goodValues.get(i));
			}
			
			pre.executeUpdate();

			CVImage.addIssueImg(issue, "medium");
			CVImage.addIssueImg(issue, "thumb");

			addVolume(new Volume(jo.getJSONObject("volume")));
			//printTable("issue");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn.isClosed() == false){
					conn.close();
				}
				if(stat.isClosed() == false){
					stat.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}
		return true;
	}
	
	public static boolean addVolume(Volume vol){
		if(exists(vol.getID(), VOLUME))
			return false;
		
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			JSONObject jo = vol.getJSONObject();
			//stat.executeUpdate("DELETE FROM issue;");
			//stat.executeUpdate("VACUUM");
			String id = vol.getID();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			String formattedDate = sdf.format(date);
			
			jo.put("JSON", jo.toString());
			jo.put("timeStamp", formattedDate);

			//stat.executeUpdate("INSERT INTO issue (id) VALUES ('" + id + "');");
			String[] names = JSONObject.getNames(jo);
			String qNames = "INSERT INTO volume (";
			String qVals = "VALUES (";

			int nameNum = names.length;
			ArrayList<String> goodNames = new ArrayList<>();
			ArrayList<String> goodValues = new ArrayList<>();
			String value = "";
			String currName = "";
			
			for(int i = 0; i < nameNum; i++){
				currName = names[i];
				if(!jo.isNull(currName) && !currName.equals("image")){
					value = jo.get(names[i]).toString();
					if(!value.equals("[]")){
						goodNames.add(names[i]);
						goodValues.add(jo.get(names[i]).toString());
					}
				}
			}

			nameNum = goodNames.size();
			for(int i = 0; i < nameNum; i++){
				if(i != nameNum-1){
					qNames += (goodNames.get(i) + ", ");
					qVals += (" ? ,");
				} else {
					qNames += (goodNames.get(i) + ") ");
					qVals += (" ? );");
				}
			}
			
			String sql = qNames+ qVals;
			System.out.println(sql);
			PreparedStatement pre = conn.prepareStatement(sql); 
			
			for(int i = 0; i < nameNum; i++){
				pre.setString((i+1), goodValues.get(i));
			}
			
			pre.executeUpdate();
			
			CVImage.addVolumeImg(vol, "medium");
			CVImage.addVolumeImg(vol, "thumb");
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(!conn.isClosed())
				conn.close();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public static boolean exists(String id, int type){
		int count = 0;
		try {
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();

			String table = "";//choose the table
			if(type == 0){
				table = "issue";
			} else table = "volume";

			//Check to see if the issue/volume has been added
			String sql = "SELECT 1 FROM " + table + " WHERE id = '" + id + "';";
			ResultSet rs = stat.executeQuery(sql);
			rs.next();
			//System.out.println(rs.getString(1));
			
			if(rs.isClosed()){
				System.out.println("not found");
				return false;
			}
			count = Integer.valueOf(rs.getString(1));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//if issue/volume hasn't been added, quit and return false
		if(count == 1)
			return true;
		else return false;
	}

	/**
	 * updates the field with the value given.
	 * @param id - unique id of the object
	 * @param field - the datafield to be updated
	 * @param value - the value of the updated field
	 * @param type - either localDB.ISSUE or localDB.VOLUME
	 * @return boolean if the object does not exists or the update fails, returns null
	 */
	public static boolean update(String id, String field, String value, int type){
		int count = 0;
		try {
			if(!exists(id, type))
				return false;
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			
			String sql = "UPDATE issue SET " + field + " = ? WHERE id = ?";
			PreparedStatement pre = conn.prepareStatement(sql);
			pre.setString(1, value);
			pre.setString(2, id);
			count = pre.executeUpdate();


			if(count == 0){//return true if 1, false if zero 
				return false;
			} else return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean executeUpdate(String str){
		try {
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			System.out.println(stat.executeUpdate(str));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(conn.isClosed() == false){
					conn.close();
				}
				if(stat.isClosed() == false){
					stat.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	

		}
		return true;
	}

	public static ResultSet executeQuery(String str){
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(url);
			Statement stmt = conn.createStatement();
			rs = stmt.executeQuery(str);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
//			try {
//				if(conn.isClosed() == false){
//					conn.close();
//				}
////				if(stat.isClosed() == false){
////					stat.close();
////				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	

		}
		return rs;
	}

	public static boolean test(){
		try {
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			/*stat.executeUpdate("DELETE FROM issue;");
			stat.executeUpdate("VACUUM");
			stat.executeUpdate("INSERT INTO issue (id) VALUES ('" + 552139 + "');");*/
			String value = CVrequest.getIssue("552139").get("location_credits").toString();
			//value = org.json.simple.JSONObject.escape(value);
			//String sql = "UPDATE issue SET location_credits ='" + value + "' WHERE id='552139';";
			PreparedStatement pre = conn.prepareStatement("UPDATE issue SET location_credits = ? WHERE id='552139'");
			pre.setString(1, value);
			pre.executeUpdate();

			ResultSet rs = stat.executeQuery("SELECT id, location_credits FROM issue;");
			while (rs.next()) {
				System.out.println(rs.getInt("id") + "\t "+ rs.getString("location_credits"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stat.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return true;
	}

	public static boolean printTable(String tbl){

		ResultSet rs;
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			rs = stat.executeQuery("SELECT * FROM " +  tbl + ";");
			ResultSetMetaData rsMeta = rs.getMetaData();
			int cols = rsMeta.getColumnCount();

			while (rs.next()) {
				for(int i = 1; i < cols; i++){
					String colName = rsMeta.getColumnName(i);
					String colVal = rs.getString(i);
					System.out.println(colName + ": " + colVal);
				}
				System.out.println("***************************************end row***********************************");
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				stat.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public static boolean loadSQL(String path){
		String url = "jdbc:sqlite:./DigLongBox.db";
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			stat.executeUpdate("drop table if exists issue;");
			BufferedReader in = new BufferedReader(new FileReader(path));
			String longAssCommand = "";
			String temp;

			while ((temp = in.readLine()) != null){
				longAssCommand += temp;
			}

			System.out.println(longAssCommand);
			in.close();

			stat.executeUpdate(longAssCommand);
			System.out.println("Printing columns");
			ResultSet rs = stat.executeQuery("PRAGMA table_info('issue');");
			while (rs.next()) {
				System.out.println(rs.getString("name") + "\t "+ rs.getString("type"));
			}
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally {
			try {
				stat.close();
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
	
	
//	public static Issue getIssue(String id){
//		Issue issue = null;
//		try {
//			conn = DriverManager.getConnection(url);
//			stat = conn.createStatement();
//			
//			String query  = "SELECT JSON FROM issue WHERE id = ?;";
//			PreparedStatement pre = conn.prepareStatement(query);
//			pre.setString(1, id);
//			ResultSet rs = pre.executeQuery();
//			rs.next();
//			String jsonStr = rs.getString(1);
//			
//			if(!jsonStr.equals("") || jsonStr != null){
//				issue = new Issue(new JSONObject(jsonStr));
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return issue;	
//		
//	}

	/**
	 * Returns the value of the requested field from the reqested table for the ID passed
	 * @param key - the field name
	 * @param id - the id of the issue/ volume
	 * @param type - LocalDB.ISSUE or LocalDB.VOLUME
	 * @return A string of the value corresponding to the key
	 */
	public static String getIssueField(String key, String id, int type){
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			
			String query  = "SELECT " + key + " FROM ? WHERE id = ?;";
			String table = (type == 0) ? "issue" : "volume";
			PreparedStatement pre = conn.prepareStatement(query);
			pre.setString(1, table);
			pre.setString(2, id);
			ResultSet rs = pre.executeQuery();
			rs.next();
			return rs.getString(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;		
	}
	
//	public static JSONObject getLocalIssue(String id){
//		
//		try {
//			conn = DriverManager.getConnection(url);
//			stat = conn.createStatement();
//			
//			String sql = "SELECT * FROM issue WHERE id = ?";
//			PreparedStatement pre = conn.prepareStatement(sql);
//			pre.setString(1, id);
//			
//			ResultSet rs = pre.executeQuery();
//			ResultSetMetaData meta = rs.getMetaData();
//			List<List<String>> rowList = new LinkedList<List<String>>();
//			Object val = "";
//			
//			while(rs.next()){
//				List<String> colList = new LinkedList<String>();
//				rowList.add(colList);
//				int size = meta.getColumnCount();
//				
//				for(int col = 1; col <=  size; col++){
//					val = rs.getObject(col);
//					colList.add(val.toString());
//				}
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	
	public static ArrayList<Issue> getAllIssues(){
		ArrayList<Issue> iList = new ArrayList<Issue>();
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			
			String sql = "SELECT JSON FROM issue;";
			PreparedStatement pre = conn.prepareStatement(sql);
			
			ResultSet rs = pre.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			
			String val = "";
			JSONObject tObj = null;
			Issue tIssue = null;

			while(rs.next()){
				val = rs.getString(1);	
				tObj = new JSONObject(val);
				tIssue = new Issue(tObj);
				System.out.println("fetching " + tIssue.getVolumeName() + " # " + tIssue.getIssueNum());
				iList.add(tIssue);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(iList.size() == 0)
			return null;
		return iList;
	}
	
	public static ArrayList<Volume> getAllVolumes(){
		ArrayList<Volume> iList = new ArrayList<Volume>();
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			
			String sql = "SELECT JSON FROM volume;";
			PreparedStatement pre = conn.prepareStatement(sql);
			
			ResultSet rs = pre.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();
			
			String val = "";
			JSONObject tObj = null;
			Volume vol = null;

			while(rs.next()){
				val = rs.getString(1);	
				tObj = new JSONObject(val);
				vol = new Volume(tObj);
				System.out.println("fetching " + vol.getName());
				iList.add(vol);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(iList.size() == 0)
			return null;
		return iList;
	}
}


