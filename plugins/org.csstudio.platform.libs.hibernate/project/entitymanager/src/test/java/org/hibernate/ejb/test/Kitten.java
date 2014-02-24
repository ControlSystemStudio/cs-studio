// $Id: Kitten.java 14833 2008-07-01 10:05:16Z hardy.ferentschik $
package org.hibernate.ejb.test;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * @author Hardy Ferentschik
 */
@SuppressWarnings("serial")
@Entity
public class Kitten {
	
	private Integer id;
	private String name;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Constructs a <code>String</code> with all attributes
	 * in name = value format.
	 *
	 * @return a <code>String</code> representation 
	 * of this object.
	 */
	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "Kitten ( "
	        + super.toString() + TAB
	        + "id = " + this.id + TAB
	        + "name = " + this.name + TAB
	        + " )";
	
	    return retValue;
	}
}
