/*
 * Copyright (c) 2010 Brookhaven National Laboratory
 * Copyright (c) 2010-2011 Helmholtz-Zentrum Berlin für Materialien und Energie GmbH
 * All rights reserved. Use is subject to license terms and conditions.
 */

package edu.msu.nscl.olog.api;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Property object that can be represented as XML/JSON in payload data.
 * 
 * @author Eric Berryman taken from Ralph Lange
 *         <Ralph.Lange@helmholtz-berlin.de>
 */
@XmlType(propOrder = { "name", "attributes", "xmlLogs" })
@XmlRootElement(name = "property")
public class XmlProperty {
	private String name = null;
	private Map<String, String> attributes;
	private XmlLogs logs = null;

	/**
	 * Creates a new instance of XmlProperty.
	 * 
	 */
	public XmlProperty() {
	}

	/**
	 * Creates a new instance of XmlProperty.
	 * 
	 * @param name
	 * @param value
	 */
	public XmlProperty(String name) {
		this.name = name;
	}

	/**
	 * @param name
	 * @param attributes
	 */
	public XmlProperty(String name, Map<String, String> attributes) {
		this.name = name;
		this.attributes = attributes;
	}

	/**
	 * Getter for property name.
	 * 
	 * @return property name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * Setter for property name.
	 * 
	 * @param name
	 *            property name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Getter for property's XmlLogs.
	 * 
	 * @return XmlChannels object
	 */
	@XmlElement(name = "logs")
	public XmlLogs getXmlLogs() {
		return logs;
	}

	/**
	 * Setter for property's XmlLogs.
	 * 
	 * @param logs
	 *            XmlLogs object
	 */
	public void setXmlLogs(XmlLogs logs) {
		this.logs = logs;
	}

	/**
	 * Creates a compact string representation for the log.
	 * 
	 * @param data
	 *            the XmlProperty to log
	 * @return string representation for log
	 */
	public static String toLog(XmlProperty data) {
		if (data.logs == null) {
			return data.getName() + "(" + data.getAttributes().toString() + ")";
		} else {
			return data.getName() + "(" + data.getAttributes().toString() + ")"
					+ XmlLogs.toLog(data.logs);
		}
	}
}
