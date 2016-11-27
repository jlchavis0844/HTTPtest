package model;

import java.awt.image.BufferedImage;

import org.apache.commons.lang.ArrayUtils;
import org.json.JSONObject;

import requests.CVImage;
import requests.CVrequest;


public class Volume {

	private JSONObject vol = new JSONObject();
	private String id;
	private String name;
	private String startYear;
	private String publisher;
	private String numIssues;
	private int cycles = 0;

	private boolean full = false;

	// constructor that take in a volume json object
	// the json object belong to an array of 
	// volume json object that alex get from calling
	// CVcrequest
	public Volume (JSONObject jo){
		this.vol = jo;
		if(check(jo, "id")){
			this.id = jo.get("id").toString();
		}
		else{
			System.out.println("id not found");
		}
		init();

		if(publisher == null && startYear == null && cycles < 10)
			populate();
	}

	/**
	 * initializes member variables. Counter add to ensure there is never an infinite loop
	 */
	private void init(){
		cycles++;
		if(cycles < 10){	
			if(check(vol, "name")){
				name = vol.get("name").toString();
			} else name = null;

			if(check(vol, "start_year")){
				startYear = vol.get("start_year").toString();
			} else startYear = null;

			if(check(vol, "publisher")){
				JSONObject publisherobjects = vol.getJSONObject("publisher"); 
				if(check(publisherobjects,"name")){
					publisher = publisherobjects.get("name").toString();
				}
			} else publisher = null;

			if(check(vol, "count_of_issues")){
				numIssues = vol.get("count_of_issues").toString();
			} else numIssues = null;
		}
	}

	public void populate(){
		vol = CVrequest.getVolumeInfo(id);
		init();
	}

	public String toString(){		
		return ("name: " + name + "\t\t\tstart_year: " + startYear + "\tpublisher: " + publisher 
				+ "\t\tid: " + id + "\tcount_of_issues: " + numIssues);		
	}

	// return the json object id field
	// return null if the field contain null or empty
	public String getID (){
		if(check(vol, "id")){
			return vol.get("id").toString();
		}
		return null;
	}
	
	public String getImgURL(String size){
		String sizes[] = {"icon","thumb","tiny","small","super","screen","medium"};
		if(ArrayUtils.contains(sizes, size) && check(vol, "image")){
			return vol.getJSONObject("image").getString(size + "_url");
		} else return null;
	}

	public BufferedImage getLocalImg(String size){
		String sizes[] = {"icon","thumb","tiny","small","super","screen","medium"};
		if(vol.has(size) && ArrayUtils.contains(sizes, size)){
			return CVImage.getLocalVolumeImg(id, size);
		} else return null;
	}
	
	// return the json object name field
	// return null if the field contain null or empty
	public String getName (){
		return name;
	}

	// return the json object count_of_issues field
	// return null if the field contain null or empty
	public String getCountofIssue (){
		return numIssues;
	}


	//return buffered image of the size given, locally if possible, remotely if needed
	public BufferedImage getImage(String size){
		if(vol.has(size)){
			return CVImage.getLocalVolumeImg(id, size); 
		} else if(check(vol, "image")){
			String url = vol.getJSONObject("image").getString(size + "_url");
			System.out.println("WARNING: Fetching online image for volume " + name);
			return CVImage.getRemoteImage(url);
		}
		return null;
	}

	// return the json object resource_type field
	// return null if the field contain null or empty
	public String getResource_type (){
		if(check(vol, "resource_type")){
			return vol.get("resource_type").toString();
		}
		return null;
	}

	// return the json object publisher field
	// return null if the field contain null or empty
	public String getPublisher (){
		return publisher;
	}

	// return the json object start_year field
	// return null if the field contain null or empty
	public String getStartYear (){
		return startYear;
	}
	/*
	public void populate (){
		full = true;
	}

	public boolean isFull (){
		return full;
	}*/

	// return the private member vol
	// which is volume json object
	public JSONObject getJSONObject(){
		return vol;
	}

	public	static boolean check (JSONObject jo, String target){
		if(jo.has(target)){
			String val = jo.get(target).toString();
			return (val != null && val.length() != 0);
		} else return false;

	}
}