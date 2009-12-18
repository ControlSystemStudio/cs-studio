package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmString;

/**
 * XML output class for EdmString type.
 * @author Matevz
 */
public class OpiString extends OpiAttribute {

	/**
	 * Creates an element <name>stringValue</name> with the given EdmString value.
	 */
	public OpiString(Context con, String name, EdmString s) {
		this(con, name, s.get());
	}
	
	/**
	 * Creates an element <name>stringValue</name> with the given String value.
	 */
	public OpiString(Context con, String name, String s) {
		super(con, name);
		context.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(s)));
	}
}
