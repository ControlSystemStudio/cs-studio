/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.bnl.channelfinder.api;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author rlange
 */
@XmlRootElement(name = "tag")
class XmlTag {
	private String name = null;
	private String owner = null;

	/**
	 * Creates a new instance of XmlTag.
	 * 
	 */
	public XmlTag() {
	}

	/**
	 * Creates a new instance of XmlTag.
	 * 
	 * @param name
	 * @param owner
	 */
	public XmlTag(String name, String owner) {
		this.owner = owner;
		this.name = name;
	}

	/**
	 * Getter for tag name.
	 * 
	 * @return tag name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * Setter for tag name.
	 * 
	 * @param name
	 *            tag name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for tag owner.
	 * 
	 * @return tag owner
	 */
	@XmlAttribute
	public String getOwner() {
		return owner;
	}

	/**
	 * Setter for tag owner.
	 * 
	 * @param owner
	 *            tag owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

}
