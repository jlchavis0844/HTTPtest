package requests;

import org.apache.commons.codec.digest.DigestUtils;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * 
 * @author James
 *
 */
public class MarvelRequest {

	public MarvelRequest() {
		// TODO Auto-generated constructor stub
	}
	public static void test(){
		String publicKey = "bd9c5a73d1c0f51391ebb0c1d64568bc";
		String privateKey = "189658364a2f41db73b39337b78c15106ffa4a0b";
		long timeStamp = System.currentTimeMillis();

		String stringToHash = timeStamp + privateKey + publicKey;
		String hash = DigestUtils.md5Hex(stringToHash);

		String url = String.format("http://gateway.marvel.com/v1/public/characters?ts=%d&apikey=%s&hash=%s&limit=%d", timeStamp, publicKey, hash, 10);
		String query = "http://gateway.marvel.com/v1/public/series";
		
		try {
			JsonNode response = Unirest.get(query)
					.header("Accept", "application/json")
					.queryString("title", "Moon Knight")
					.queryString("startYear", 2016)
					.queryString("apikey", publicKey)
					.queryString("ts",timeStamp)
					.queryString("hash", hash) 
					.asJson().getBody();
			System.out.println("apikey,"+ publicKey);
			System.out.println("ts, " + timeStamp);
			System.out.println("hash, "+ hash);
			System.out.println(url);
			System.out.println(response.toString());
		} catch (UnirestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
}
