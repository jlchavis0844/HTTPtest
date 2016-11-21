package requests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.imageio.ImageIO;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;

import localDB.*;
import model.Issue;
import model.Volume;

public class CVImage {
	private static BufferedImage img = null;

	public static void addAllImages(Issue i){
		
		JSONObject joImg = i.getFullObject().getJSONObject("image");
		String names[] = JSONObject.getNames(joImg);
		String id = i.getID();

		String urlStr = "";
		int nameLen = names.length;

		for(int j = 0; j < nameLen; j++){
			urlStr = joImg.get(names[j]).toString();
			CVImage.addIssueImg(i, names[j].replace("_url", ""));
		}
	}

	public static BufferedImage getRemoteImage(String urlStr){
		URL url = null;

		try {
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31"
							+ "(KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			//img = ImageIO.read(conn.getInputStream()); Faster, but easily corrupted
			img = ImageIO.read(ImageIO.createImageInputStream(conn.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}

	public static boolean addIssueImg(Issue i, String size){
		String urlType = i.getImgURL(size);
		URL url = null;
		HttpURLConnection conn = null;
		String id = i.getID();
		
		try {
			url = new URL(urlType);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31"
							+ "(KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			//img = ImageIO.read(conn.getInputStream());
			img = ImageIO.read(ImageIO.createImageInputStream(conn.getInputStream()));
			String ext = "png";
			File dir = new File("./images/issue/");
			String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(12), ext);
			File file = new File(dir, name);
			ImageIO.write(img, "png", file);
			String col = size.replace("_url", "");
			String db = "jdbc:sqlite:./DigLongBox.db";
			
			Connection SQLconn;
			String str = "UPDATE issue SET "+ col + " = ? WHERE id = ? ";
			try {
				
				SQLconn = DriverManager.getConnection(db);
				PreparedStatement pre = SQLconn.prepareStatement(str);
				pre.setString(1, "./images/issue/" + name);
				pre.setString(2, id);
				System.out.println(pre.executeUpdate());
				
				JSONObject tJO = i.getFullObject();
				tJO.put(size, "./Images/issue/" + name);
				str = "UPDATE issue SET JSON = ? WHERE id = ? ";
				pre = SQLconn.prepareStatement(str);
				pre.setString(1, tJO.toString());
				pre.setString(2, id);
				System.out.println(pre.executeUpdate());
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean addVolumeImg(Volume vol, String size){
		//		addVolumeImg(vol.getImgURL(size), vol.getID(), size);
		//		return true;
		//	}
		//	
		//	public static boolean addVolumeImg(String urlStr, String id, String size){
		String urlStr = vol.getImgURL(size);
		URL url = null;
		HttpURLConnection conn = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31"
							+ "(KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			//img = ImageIO.read(conn.getInputStream());
			img = ImageIO.read(ImageIO.createImageInputStream(conn.getInputStream()));
			String ext = "png";
			File dir = new File("./images/volume/");
			String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(12), ext);
			File file = new File(dir, name);
			boolean writeRes = ImageIO.write(img, "png", file);
			System.out.println("trying to write " + writeRes);
			String col = size.replace("_url", "");

			String str = "UPDATE volume SET "+ col + " ='./Images/volume/" + name +"' WHERE id='" + vol.getID() + "';";

			System.out.println(str);
			LocalDB.executeUpdate(str);

			JSONObject jo = vol.getJSONObject();
			jo.append(size, file);
			jo.remove("JSON");
			jo.append("JSON", jo.toString());
			str = "UPDATE volume SET JSON = '" + jo.toString() + "' WHERE id = '" + vol.getID() + "';";
			LocalDB.executeUpdate(str);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static void printResultSet(ResultSet rs){
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print(",  ");
					String columnValue = rs.getString(i);
					System.out.print(columnValue + " " + rsmd.getColumnName(i));
				}
				System.out.println("");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static BufferedImage getLocalVolumeImg(String id, String size){
		try {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:./DigLongBox.db");
		Statement stat = conn.createStatement();
		String colName = size.replace("_url", "");
		String query = "SELECT " + colName + " FROM volume WHERE id = '" + id + "';";
		ResultSet rs = stat.executeQuery(query);
		rs.next();
		String path = rs.getString(1);
		img =  ImageIO.read(new File(path));
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
		
	}
	
	public static BufferedImage getLocalIssueImg(String id, String size){
		try {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:./DigLongBox.db");
		Statement stat = conn.createStatement();
		String colName = size.replace("_url", "");
		String query = "SELECT " + colName + " FROM issue WHERE id = '" + id + "';";
		ResultSet rs = stat.executeQuery(query);
		rs.next();
		String path = rs.getString(1);
		img =  ImageIO.read(new File(path));
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return img;
		
	}

//	public static BufferedImage getLocalImage(String id, String size, int type){
//		String col = size;
//		String table = (type == 0) ? "issue" : "volume";
//		if(size.contains("_url"))
//			col = size.replace("_url", "");
//
//		String str = "SELECT " + col + " FROM " + table + " WHERE id = '" + id + "';";
//		System.out.println(str);
//		ResultSet rs = LocalDB.executeQuery(str);
//
//		try {
//			rs.next();
//			String path = rs.getString(col);
//			//String path = "./Images/volume/YxhTZW1UcU8p.png";
//			img =  ImageIO.read(new File(path));
//		} catch (IOException | SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return img;
//	}
}
