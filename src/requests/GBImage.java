package requests;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import localDB.*;
import model.Game;

public class GBImage {
	private static BufferedImage bImg = null;
	private static Image img = null;
	private static int cntr;
	public static String IconSize = "icon";
	public static String MediumSize = "medium";
	public static String ScreenSize = "screen";
	public static String SmallSize = "small";
	public static String SuperSize = "super";
	public static String ThumbSize = "thumb";
	public static String TinySize = "tiny";
	public static int ISSUE = 0;
	public static int VOLUME = 1;
	public static int GAME = 2;
	

	public static void addAllImages(Game game) {

		JSONObject joImg = game.getGame().getJSONObject("image");
		String names[] = JSONObject.getNames(joImg);
		String id = game.getID();

		String urlStr = "";
		int nameLen = names.length;

		for (int j = 0; j < nameLen; j++) {
			urlStr = joImg.get(names[j]).toString();
			GBImage.addGameImg(game, names[j].replace("_url", ""));
		}
	}

	public static Image getRemoteImage(String urlStr) {
		URL url = null;

		try {
			url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31"
					+ "(KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			// img = ImageIO.read(conn.getInputStream()); Faster, but easily
			// corrupted
			bImg = ImageIO.read(ImageIO.createImageInputStream(conn.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return SwingFXUtils.toFXImage(bImg, null);
	}

	public static boolean addGameImg(Game game, String size) {
		String urlType = game.getImgURL(size);
		URL url = null;
		HttpURLConnection conn = null;
		String id = game.getID();

		try {
			url = new URL(urlType);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) AppleWebKit/537.31"
					+ "(KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
			// img = ImageIO.read(conn.getInputStream());
			bImg = ImageIO.read(ImageIO.createImageInputStream(conn.getInputStream()));
			String ext = "png";
			File dir = new File("./images/game/");
			String name = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(12), ext);
			File file = new File(dir, name);
			ImageIO.write(bImg, "png", file);
			String col = size.replace("_url", "");
			String db = "jdbc:sqlite:./DigLongBox.db";

			Connection SQLconn;
			String str = "UPDATE game SET " + col + " = ? WHERE id = ? ";
			try {

				SQLconn = DriverManager.getConnection(db);
				PreparedStatement pre = SQLconn.prepareStatement(str);
				pre.setString(1, "./images/game/" + name);
				pre.setString(2, id);
				System.out.println(pre.executeUpdate());

				JSONObject tJO = game.getGame();
				tJO.remove(size);
				tJO.put(size, "./images/game/" + name);
				str = "UPDATE game SET JSON = ? WHERE id = ? ";
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


	public static void printResultSet(ResultSet rs) {
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1)
						System.out.print(",  ");
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
	

	public static Image getLocalGameImg(String id, String size) {
		Connection myConn = null;
		Statement myStat = null;
		ResultSet rs = null;
		try {
			myConn = DriverManager.getConnection("jdbc:sqlite:./DigLongBox.db");
			myStat = myConn.createStatement();
			String colName = size.replace("_url", "");
			String query = "SELECT " + colName + " FROM game WHERE id = '" + id + "';";
			rs = myStat.executeQuery(query);
			rs.next();
			String path = rs.getString(1);
			bImg = ImageIO.read(new File(path));
			img = SwingFXUtils.toFXImage(bImg, null);
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				myStat.close();
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return img;
	}
	
	public static int cleanAllLocalImgs(){
		int retVal = 0;
		try {
			return (cleanLocalImgs(CVImage.ISSUE) + cleanLocalImgs(CVImage.VOLUME)
			+ cleanLocalImgs(GBImage.GAME));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retVal;
	}
	
	public static int cleanLocalImgs(int type) throws IOException{
		String path = "";
		if(type == GBImage.ISSUE) {
			path = "./images/game";
		} else if(type == GBImage.VOLUME) {
			path = "./images/volume";
		} else if(type == GBImage.GAME){
			path = "./images/game";
		}
		System.out.println("Starting with " + path);
		try(Stream<Path> paths = Files.walk(Paths.get(path))){
			paths.forEach(filePath -> {
				if(Files.isRegularFile(filePath)){
					//split should produce [ | |images|volume|hashName|png]
					String hashName = filePath.toString().split("[\\.|\\\\]")[4];
					if(!LocalDB.searchAllFields(hashName, type)){
						System.out.println("Deleting " + filePath.toString());
						try {
							Files.delete(filePath);
							System.out.println("finished");
							cntr++;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
		}
		return cntr;
	}
}
