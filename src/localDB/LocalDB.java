package localDB;

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
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import model.Issue;
import model.Volume;
import requests.CVImage;

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
	public final static int ISSUE = 0;
	public final static int VOLUME = 1;

	/**
	 * Inserts issue along with corresponding volume, if needed and fetches aand stores images
	 * @param issue
	 * @return
	 */
	public static boolean addIssue(Issue issue) {
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

			for (int i = 0; i < nameNum; i++) {
				currName = names[i];
				if (!jo.isNull(currName) && !currName.equals("image")) {
					value = jo.get(names[i]).toString();
					if (!value.equals("[]")) {
						goodNames.add(names[i]);
						goodValues.add(jo.get(names[i]).toString());
					}
				}
			}

			nameNum = goodNames.size();
			for (int i = 0; i < nameNum; i++) {
				if (i != nameNum - 1) {
					qNames += (goodNames.get(i) + ", ");
					qVals += (" ? ,");
				} else {
					qNames += (goodNames.get(i) + ") ");
					qVals += (" ? );");
				}
			}

			String sql = qNames + qVals;
			System.out.println(sql);
			PreparedStatement pre = conn.prepareStatement(sql);

			for (int i = 0; i < nameNum; i++) {
				pre.setString((i + 1), goodValues.get(i));
			}

			pre.executeUpdate();

			CVImage.addIssueImg(issue, "medium");
			CVImage.addIssueImg(issue, "thumb");

			addVolume(new Volume(jo.getJSONObject("volume")));
			// printTable("issue");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (conn.isClosed() == false) {
					conn.close();
				}
				// if (stat.isClosed() == false) {
				// stat.close();
				// }
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return true;
	}

	/**
	 * AAdds volume and corresponding data such aas pictures to the database
	 * @param vol Volume object to be added to the database
	 * @return true if the volume was added, false otherwise
	 */
	public static boolean addVolume(Volume vol) {
		if (exists(vol.getID(), VOLUME))
			return false;

		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			JSONObject jo = vol.getJSONObject();

			String id = vol.getID();
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
			String formattedDate = sdf.format(date);

			jo.put("JSON", jo.toString());
			jo.put("timeStamp", formattedDate);

			String[] names = JSONObject.getNames(jo);
			String qNames = "INSERT INTO volume (";
			String qVals = "VALUES (";

			int nameNum = names.length;
			ArrayList<String> goodNames = new ArrayList<>();
			ArrayList<String> goodValues = new ArrayList<>();
			String value = "";
			String currName = "";

			for (int i = 0; i < nameNum; i++) {
				currName = names[i];
				if (!jo.isNull(currName) && !currName.equals("image")) {
					value = jo.get(names[i]).toString();
					if (!value.equals("[]")) {
						goodNames.add(names[i]);
						goodValues.add(jo.get(names[i]).toString());
					}
				}
			}

			nameNum = goodNames.size();
			for (int i = 0; i < nameNum; i++) {
				if (i != nameNum - 1) {
					qNames += (goodNames.get(i) + ", ");
					qVals += (" ? ,");
				} else {
					qNames += (goodNames.get(i) + ") ");
					qVals += (" ? );");
				}
			}

			String sql = qNames + qVals;
			System.out.println(sql);
			PreparedStatement pre = conn.prepareStatement(sql);

			for (int i = 0; i < nameNum; i++) {
				pre.setString((i + 1), goodValues.get(i));
			}

			pre.executeUpdate();

			CVImage.addVolumeImg(vol, "medium");
			CVImage.addVolumeImg(vol, "thumb");

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if (!conn.isClosed())
				conn.close();
			return false;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * Checks to see if the given ID is in the data base for given object
	 * @param id volume/issue id
	 * @param type LocalDB.ISSUE or LocalDB.VOLUME
	 * @return true or false
	 */
	public static boolean exists(String id, int type) {
		int count = 0;
		try {
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();

			String table = "";// choose the table
			if (type == 0) {
				table = "issue";
			} else
				table = "volume";

			// Check to see if the issue/volume has been added
			String sql = "SELECT 1 FROM " + table + " WHERE id = '" + id + "';";
			ResultSet rs = stat.executeQuery(sql);
			rs.next();
			// System.out.println(rs.getString(1));

			if (rs.isClosed()) {
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
		// if issue/volume hasn't been added, quit and return false
		if (count == 1)
			return true;
		else
			return false;
	}

	/**
	 * updates the field with the value given.
	 * 
	 * @param id
	 *            - unique id of the object
	 * @param field
	 *            - the datafield to be updated
	 * @param value
	 *            - the value of the updated field
	 * @param type
	 *            - either localDB.ISSUE or localDB.VOLUME
	 * @return boolean if the object does not exists or the update fails,
	 *         returns null
	 */
	public static boolean update(String id, String field, String value, int type) {
		int count = 0;
		try {
			if (!exists(id, type))
				return false;
			conn = DriverManager.getConnection(url);
			Statement stat = conn.createStatement();

			String sql = "UPDATE issue SET " + field + " = ? WHERE id = ?";
			PreparedStatement pre = conn.prepareStatement(sql);
			pre.setString(1, value);
			pre.setString(2, id);
			count = pre.executeUpdate();

			if (count == 0) {// return true if 1, false if zero
				return false;
			} else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean executeUpdate(String str) {
		Connection newconn = null;
		Statement newstat = null;
		try {
			newconn = DriverManager.getConnection(url);
			newstat = newconn.createStatement();
			int updated = newstat.executeUpdate(str);
			// newconn.close();
			if (updated != 1) {
				System.out.println("SQL insert failed");
			}
			// System.out.println(stat.executeUpdate(str));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public static ResultSet executeQuery(String str) {
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

	/**
	 * Returns the value of the requested field from the reqested table for the
	 * ID passed
	 * 
	 * @param key
	 *            - the field name
	 * @param id
	 *            - the id of the issue/ volume
	 * @param type
	 *            - LocalDB.ISSUE or LocalDB.VOLUME
	 * @return A string of the value corresponding to the key
	 */
	public static String getIssueField(String key, String id, int type) {
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();

			String query = "SELECT " + key + " FROM ? WHERE id = ?;";
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
	
	/**
	 * Searches all json fields of either volume or issue to see if value exists
	 * @param key Term to search for
	 * @param type VOLUME or ISSUE
	 * @return true or false on whether it was found
	 */
	public static boolean searchAllFields(String key, int type) {
		boolean retVal = false;
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			String table = (type == 0) ? "issue" : "volume";
			String query = "SELECT COUNT(*) FROM " + table + " WHERE JSON LIKE ?;";
			
			PreparedStatement pre = conn.prepareStatement(query);
			pre.setString(1,"%" + key + "%");
			ResultSet rs = pre.executeQuery();
			rs.next();
			retVal = (rs.getInt(1) > 0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}

	/**
	 * Builds and returns an unsorted list of issues
	 * @return ArrayList<Issue>
	 */
	public static ArrayList<Issue> getAllIssues() {
		ArrayList<Issue> iList = new ArrayList<Issue>();
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();

			String sql = "SELECT JSON FROM issue;";
			PreparedStatement pre = conn.prepareStatement(sql);

			ResultSet rs = pre.executeQuery();

			String val = "";
			JSONObject tObj = null;
			Issue tIssue = null;

			while (rs.next()) {
				val = rs.getString(1);
				tObj = new JSONObject(val);
				tIssue = new Issue(tObj);
				// System.out.println("fetching " + tIssue.getVolumeName() + " #
				// " + tIssue.getIssueNum());
				iList.add(tIssue);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (iList.size() == 0)
			return null;
		return iList;
	}

	/**
	 * Returns an ArrayList<Volume> of all volumes in the local database
	 * @return ArrayList<Volume>
	 */
	public static ArrayList<Volume> getAllVolumes() {
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

			while (rs.next()) {
				val = rs.getString(1);
				tObj = new JSONObject(val);
				vol = new Volume(tObj);
				// System.out.println("fetching " + vol.getName());
				iList.add(vol);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (iList.size() == 0)
			return null;
		return iList;
	}

	/**
	 * return an ArrayList<Issue> by authors based on order. if order is true,
	 * ascendence if order is false, descendence
	 * 
	 * @param order
	 * @return ArrayList<Issue>
	 * @throws JSONException
	 */
	public static void sortIssuesByName(ArrayList<Issue> input, boolean order) throws JSONException {

		final Comparator<Issue> comparatorIssue = new Comparator<Issue>() {
			public int compare(Issue e1, Issue e2) {

				int result = e1.getName().toLowerCase().compareTo(e2.getName().toLowerCase());

				if (result == 0) {
					int e1IssueNumber = Integer.valueOf(e1.getIssueNum());
					int e2IssueNumber = Integer.valueOf(e2.getIssueNum());

					result = compareInteger(e1IssueNumber, e2IssueNumber);
				}

				return result * ((order) ? 1 : -1);
			}
		};

		Collections.sort(input, comparatorIssue);

	}

	public static void sortIssuesByVolumeName(ArrayList<Issue> input, boolean order) throws JSONException {
		final Comparator<Issue> comparatorIssue = new Comparator<Issue>() {
			public int compare(Issue e1, Issue e2) {
				return e1.getVolumeName().compareTo(e2.getVolumeName()) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortIssuesByIssueNum(ArrayList<Issue> input, boolean order) throws JSONException {
		final Comparator<Issue> comparatorIssue = new Comparator<Issue>() {
			public int compare(Issue e1, Issue e2) {
				double e1ID = checkDouble(e1.getIssueNum());
				double e2ID = checkDouble(e2.getIssueNum());

				return Double.compare(e1ID, e2ID) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortIssuesByCoverDate(ArrayList<Issue> input, boolean order) throws JSONException {
		final Comparator<Issue> comparatorIssue = new Comparator<Issue>() {
			public int compare(Issue e1, Issue e2) {
				return e1.getCoverDate().compareTo(e2.getCoverDate()) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortVolumesByName(ArrayList<Volume> input, boolean order) throws JSONException {
		final Comparator<Volume> comparatorIssue = new Comparator<Volume>() {
			public int compare(Volume e1, Volume e2) {
				return e1.getName().toLowerCase().compareTo(e2.getName().toLowerCase()) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortVolumesByID(ArrayList<Volume> input, boolean order) throws JSONException {
		final Comparator<Volume> comparatorIssue = new Comparator<Volume>() {
			public int compare(Volume e1, Volume e2) {
				return e1.getID().toLowerCase().compareTo(e2.getID().toLowerCase()) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortVolumesByStartYear(ArrayList<Volume> input, boolean order) throws JSONException {
		final Comparator<Volume> comparatorIssue = new Comparator<Volume>() {
			public int compare(Volume e1, Volume e2) {
				return e1.getStartYear().toLowerCase().compareTo(e2.getStartYear().toLowerCase()) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortVolumesByPublisher(ArrayList<Volume> input, boolean order) throws JSONException {

		final Comparator<Volume> comparatorIssue = new Comparator<Volume>() {
			public int compare(Volume e1, Volume e2) {
				return e1.getPublisher().toLowerCase().compareTo(e2.getPublisher().toLowerCase()) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static void sortVolumesByStartCountOfIssues(ArrayList<Volume> input, boolean order) throws JSONException {
		final Comparator<Volume> comparatorIssue = new Comparator<Volume>() {
			public int compare(Volume e1, Volume e2) {
				int e1CountOfIssue = Integer.valueOf(e1.getCountofIssue());
				int e2CountOfIssue = Integer.valueOf(e2.getCountofIssue());

				return compareInteger(e1CountOfIssue, e2CountOfIssue) * ((order) ? 1 : -1);
			}
		};
		Collections.sort(input, comparatorIssue);
	}

	public static ArrayList<Volume> searchVolumeByName(String inputValue) throws JSONException, SQLException {
		return searchVolume("name", "%" + inputValue + "%", Operator.WORD_LIKE);
	}

	public static ArrayList<Volume> searchVolumeByPublisher(String inputValue) throws JSONException, SQLException {
		return searchVolume("publisher", "%" + inputValue + "%", Operator.WORD_LIKE);
	}

	public static ArrayList<Volume> searchVolumeByCountOfIssues(String inputValue, String operator)
			throws JSONException, SQLException {
		return searchVolume("count_of_issues", inputValue, operator);
	}

	public static ArrayList<Volume> searchVolumeByYear(String inputValue) throws JSONException, SQLException {
		return searchVolume("start_year", inputValue, Operator.EQUAL);
	}

	public static ArrayList<Volume> searchVolumeByYear(String inputValue, String operator)
			throws JSONException, SQLException {
		return searchVolume("start_year", inputValue, operator);
	}

	public static ArrayList<Volume> searchVolume(String inputColumn, String inputValue, String operator)
			throws JSONException, SQLException {
		ArrayList<Volume> resultVolume = new ArrayList<>();

		String columnName = inputColumn;// what field we are searching

		// make connection
		conn = DriverManager.getConnection(url);
		stat = conn.createStatement();

		// what we are searching for
		String searchValue = inputValue;

		// build query
		String sql = "SELECT JSON FROM volume WHERE ";
		if (inputValue.chars().allMatch(Character::isDigit)) {

			// SELECT * FROM volume WHERE count_of_issues > 5;
			sql += columnName + " " + operator + inputValue;
		} else {
			// sql += columnName + " LIKE " + "'" + "%" + inputValue + "%" + "'"
			// + ";";
			sql += columnName + " " + operator + " " + "'" + inputValue + "'" + ";";
		}

		PreparedStatement pre = conn.prepareStatement(sql);

		ResultSet rs = pre.executeQuery();
		ResultSetMetaData meta = rs.getMetaData();

		String value = "";
		JSONObject tObj = null;
		Volume vol = null;

		// make volumes from the json string returned from database and stores
		// in ArrayList
		while (rs.next()) {
			value = rs.getString(1);
			tObj = new JSONObject(value);
			vol = new Volume(tObj);
			resultVolume.add(vol);
		}

		return resultVolume;
	}

	// ----------------------------------------------issues--------------------------------
	public static ArrayList<Issue> searchIssueByIssueNumber(String inputValue, String operator)
			throws JSONException, SQLException {
		return searchIssue("issue_number", inputValue, operator);
	}

	public static ArrayList<Issue> searchIssueByIssueNumber(String inputValue) throws JSONException, SQLException {
		return searchIssue("issue_number", inputValue, Operator.EQUAL);
	}

	public static ArrayList<Issue> searchIssueByName(String inputValue) throws JSONException, SQLException {
		return searchIssue("name", "%" + inputValue + "%", Operator.WORD_LIKE);
	}

	public static ArrayList<Issue> searchIssueByVolumeName(String inputValue) throws JSONException, SQLException {
		return searchIssue("volName", "%" + inputValue + "%", Operator.WORD_LIKE);
	}

	public static ArrayList<Issue> searchIssue(String inputColumn, String inputValue, String operator)
			throws JSONException, SQLException {
		ArrayList<Issue> resultIssue = new ArrayList<>();

		String columnName = inputColumn;

		conn = DriverManager.getConnection(url);
		stat = conn.createStatement();

		String searchValue = inputValue;
		String sql = "SELECT JSON FROM issue WHERE ";

		if (inputValue.chars().allMatch(Character::isDigit)) {

			// SELECT * FROM volume WHERE count_of_issues > 5;
			sql += columnName + " " + operator + inputValue;
		} else {
			// sql += columnName + " LIKE " + "'" + "%" + inputValue + "%" + "'"
			// + ";";
			sql += columnName + " " + operator + " " + "'" + inputValue + "'" + ";";

		}

		PreparedStatement pre = conn.prepareStatement(sql);

		ResultSet rs = pre.executeQuery();

		String value = "";
		JSONObject tObj = null;
		Issue issue = null;

		while (rs.next()) {
			value = rs.getString(1);
			tObj = new JSONObject(value);
			issue = new Issue(tObj);
			resultIssue.add(issue);
		}

		return resultIssue;
	}

	
	//TODO: remove this function, call Integer.compare() instead
	public static int compareInteger(int inputA, int inputB) {
		int result;
		if (inputA == inputB) {
			result = 0;
		} else if (inputA < inputB) {
			result = -1;
		} else {
			result = 1;
		}
		return result;
	}
	
	public static void sortVolumes(List<Volume> volList) {

		final Comparator<Volume> comparatorVolume = new Comparator<Volume>() {

			public int compare(Volume v1, Volume v2) {
				String name1 = v1.getName().replace("The ", "");
				String name2 = v2.getName().replace("The ", "");
				int result = name1.compareTo(name2);

				if (result == 0) {
					return v1.getStartYear().compareTo(v2.getStartYear());
				} else
					return result;
			}
		};

		Collections.sort(volList, comparatorVolume);
	}

	/**
	 * Function to delete the volume from the volume database and all related issues from the issue table
	 * @param inputID - the id of the volume to be deleted 
	 * @return boolean of whether all the actions succeded
	 */
	public static boolean deleteVolumeByID(String inputID) {

		if (inputID.chars().allMatch(Character::isDigit)) {

			try {
				conn = DriverManager.getConnection(url);
				
				String sql = "DELETE FROM VOLUME WHERE id = ?;";
				PreparedStatement pre = conn.prepareStatement(sql);
				pre.setString(1, inputID);
				int count = pre.executeUpdate();
				
				System.out.println("Trying to delete: " + ((count == 1) ? "Worked" : "Failed"));

				//if the delete from the volume table fails, exit function with false value
				if(count == 0){
					return false;
				}
				
				/**
				 * lets try to delete all the issues for this volume from the issues table
				 */
				sql = "DELETE FROM issue WHERE VOLUME LIKE ?;";
				pre=conn.prepareStatement(sql);
				pre.setString(1, "%" + inputID + "%");
				
				count = pre.executeUpdate();
				System.out.println("number of issues deleted: " + count);

			} catch (SQLException e) {
				System.err.println("failed on volume delete attempt: " + inputID);
				try {
					System.err.println(stat.getWarnings());
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Removes this issue from the local database, will not remove from any gui items or update runtime lists
	 * @param inputID - the id of the issue to delete
	 * @return boolean of whether the sql call worked
	 */
	public static boolean deleteIssueByID(String inputID) {

		if (inputID.chars().allMatch(Character::isDigit)) {

			try {
				conn = DriverManager.getConnection(url);
				stat = conn.createStatement();

				String sql = "DELETE FROM issue WHERE id = ?;";
				PreparedStatement pre = conn.prepareStatement(sql);
				pre.setString(1, inputID);
				if(pre.executeUpdate() == 0){
					System.err.println("Failed to delete " + inputID);
					return false;
				} else {
					System.out.println("Deleted " + inputID);
					return true;
				}
			} catch (SQLException e) {
				System.err.println("SQLException trying to delete volume by id: " + inputID);
				e.printStackTrace();
			}
			
		}
		return false;
	}

	public static boolean deleteVolumeByName(String inputName) {
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();

			String sql = "DELETE FROM volume WHERE name = " + "'" + inputName + "'";

			PreparedStatement pre = conn.prepareStatement(sql);

			pre.executeUpdate(sql);

			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static boolean deleteIssueByName(String inputName) {
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();

			String sql = "DELETE FROM issue WHERE name = " + "'" + inputName + "'";

			PreparedStatement pre = conn.prepareStatement(sql);

			pre.executeUpdate(sql);

			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Fetches and builds all issues	
	 * @return ArrayList<Issue> of every issue
	 */
	public static String[] getAllIDs() {
		ArrayList<String> iList = new ArrayList<String>();
		try {
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();

			String sql = "SELECT id FROM issue;";
			PreparedStatement pre = conn.prepareStatement(sql);

			ResultSet rs = pre.executeQuery();
			ResultSetMetaData meta = rs.getMetaData();

			String val = "";
			JSONObject tObj = null;
			Issue tIssue = null;

			while (rs.next()) {
				iList.add(rs.getString(1));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (iList.size() == 0)
			return null;
		return iList.toArray(new String[iList.size()]);
	}
	
	/**
	 * Checks if string is an integer and handles exception by returning MIN_VALUE
	 * @param s the string to check 
	 * @return Integer.MIN_VALUE on invalid, number on valid
	 */
	public static int checkInt(String s){
		int val = Integer.MIN_VALUE;
		try{
			val = Integer.parseInt(s);
		} catch( NumberFormatException e){
			System.out.println("Failed to convert " + s);
		}
		return val;
	}
	
	/**
	 * Checks if string is an double and handles exception by returning MIN_VALUE
	 * @param s the string to check 
	 * @return Double.MIN_VALUE on invalid, number on valid
	 */
	public static double checkDouble(String s){
		double val = Double.MIN_NORMAL;
		try{
			val = Double.parseDouble(s);
		} catch( NumberFormatException e){
			if(s.contains(".")){
				String temp = s.split(".")[0];
				 val = checkDouble(temp);
				System.out.println(s + " is not a Double, checking " + temp);
			}
			System.out.println("could not convert " + s);
		}
		return val;
	}
	
}