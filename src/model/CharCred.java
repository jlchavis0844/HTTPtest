package model;

/**
 * Simple class to hold character_credit info
 * @author James
 *
 */
public class CharCred {
	private String id;
	private String name;
	private String link;
	public CharCred(String idNum, String n, String lnk) {
		id = idNum;
		name = n;
		link = lnk;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}
	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

}
