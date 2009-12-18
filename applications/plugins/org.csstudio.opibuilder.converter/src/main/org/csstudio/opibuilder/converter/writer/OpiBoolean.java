package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmBoolean;

/**
 * XML output class for EdmBoolean type.
 * @author Matevz
 */
public class OpiBoolean extends OpiAttribute {

	/**
	 * Creates an element <name>booleanValue</name> with the given EdmBoolean value.
	 */
	public OpiBoolean(Context con, String name, EdmBoolean b) {
		this(con, name, b.is());
	}
	
	/**
	 * Creates an element <name>booleanValue</name> with the given boolean value.
	 */
	public OpiBoolean(Context con, String name, boolean b) {
		super(con, name);
		context.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(b)));
	}
}
