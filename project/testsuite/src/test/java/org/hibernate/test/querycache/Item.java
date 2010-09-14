//$Id: Item.java 9587 2006-03-09 21:38:26Z steve.ebersole@jboss.com $
package org.hibernate.test.querycache;

import java.util.Set;
import java.util.HashSet;


/**
 * @author Gavin King
 */
public class Item {
	private long id;
	private String name;
	private String description;

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
