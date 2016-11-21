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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

import model.Issue;
import model.Volume;
import requests.CVImage;
import requests.CVrequest;

/**
 * Operator Description Example = Checks if the values of two operands are equal
 * or not, if yes then condition becomes true. (a = b) is not true. != Checks if
 * the values of two operands are equal or not, if values are not equal then
 * condition becomes true. (a != b) is true. <> Checks if the values of two
 * operands are equal or not, if values are not equal then condition becomes
 * true. (a <> b) is true. > Checks if the value of left operand is greater than
 * the value of right operand, if yes then condition becomes true. (a > b) is
 * not true. < Checks if the value of left operand is less than the value of
 * right operand, if yes then condition becomes true. (a < b) is true. >= Checks
 * if the value of left operand is greater than or equal to the value of right
 * operand, if yes then condition becomes true. (a >= b) is not true. <= Checks
 * if the value of left operand is less than or equal to the value of right
 * operand, if yes then condition becomes true. (a <= b) is true. !< Checks if
 * the value of left operand is not less than the value of right operand, if yes
 * then condition becomes true. (a !< b) is false. !> Checks if the value of
 * left operand is not greater than the value of right operand, if yes then
 * condition becomes true. (a !> b) is true.
 * 
 * Operator Description ALL The ALL operator is used to compare a value to all
 * values in another value set. AND The AND operator allows the existence of
 * multiple conditions in an SQL statement's WHERE clause. ANY The ANY operator
 * is used to compare a value to any applicable value in the list according to
 * the condition. BETWEEN The BETWEEN operator is used to search for values that
 * are within a set of values, given the minimum value and the maximum value.
 * EXISTS The EXISTS operator is used to search for the presence of a row in a
 * specified table that meets certain criteria. IN The IN operator is used to
 * compare a value to a list of literal values that have been specified. LIKE
 * The LIKE operator is used to compare a value to similar values using wildcard
 * operators. NOT The NOT operator reverses the meaning of the logical operator
 * with which it is used. Eg: NOT EXISTS, NOT BETWEEN, NOT IN, etc. This is a
 * negate operator. OR The OR operator is used to combine multiple conditions in
 * an SQL statement's WHERE clause. IS NULL The NULL operator is used to compare
 * a value with a NULL value. UNIQUE The UNIQUE operator searches every row of a
 * specified table for uniqueness (no duplicates).
 */

interface Operator {

	String EQUAL = "=";
	String NOT_EQUAL = "!=";
	String GREATER_THAN = ">";
	String LESS_THAN = "<";
	String LESS_THAN_OR_EQUAL = "<=";
	String GREATER_THAN_OR_EQUAL = ">=";
	String NOT_LESS_THAN = "!<";
	String NOT_GREATER_THAN = "!>";
	String BETWEEN = "<>";

	String WORD_ALL = "ALL";
	String WORD_AND = "AND";
	String WORD_BETWEEN = "BETWEEN";
	String WORD_EXIT = "EXIST";
	String WORD_IN = "IN";
	String WORD_LIKE = "LIKE";
	String WORD_NOT = "NOT";
	String WORD_OR = "OR";
	String WORD_IS_NULL = "IS NULL";
	String WORD_UNIQUE = "UNIQUE";

}

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

			String id = issue.getID();
			JSONObject jo = issue.getFullObject();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			String formattedDate = sdf.format(date);

			jo.put("timeStamp", formattedDate);
			jo.put("volName", jo.getJSONObject("volume").getString("name"));
			jo.put("JSON", jo.toString());

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
		Connection newconn = null;
		Statement newstat = null;
		try {
			newconn = DriverManager.getConnection(url);
			newstat = newconn.createStatement();
			int updated = newstat.executeUpdate(str);
			//newconn.close();
			if(updated != 1){
				System.out.println("SQL insert failed");
			}
			//System.out.println(stat.executeUpdate(str));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		}

		return rs;
	}

	public static boolean test(){
		try {
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();
			String value = CVrequest.getIssue("552139").get("location_credits").toString();
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
				//System.out.println("fetching " + tIssue.getVolumeName() + " # " + tIssue.getIssueNum());
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
				//System.out.println("fetching " + vol.getName());
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
	
	public static void sortVolumes(List<Volume> volList){

		final Comparator<Volume> comparatorVolume = new Comparator<Volume>() {
			
			public int compare(Volume v1, Volume v2) {
				String name1 = v1.getName().replace("The", "");
				String name2 = v2.getName().replace("The", "");
				int result = name1.compareTo(name2);
				
				if(result == 0){
					return v1.getStartYear().compareTo(v2.getStartYear());
				}else return result;
			}
		};

		Collections.sort(volList, comparatorVolume);
	}
	
	
}


