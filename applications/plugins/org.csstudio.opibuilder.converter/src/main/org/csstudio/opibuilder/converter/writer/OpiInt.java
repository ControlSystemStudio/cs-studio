package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmInt;

/**
 * XML output class for EdmInt type.
 * @author Matevz
 */
public class OpiInt extends OpiAttribute {

	/**
	 * Creates an element <name>intValue</name> with the given EdmInt value.
	 */
	public OpiInt(Context con, String name, EdmInt i) {
		this(con, name, i.get());
	}
	
	/**
	 * Creates an element <name>intValue</name> with the given int value.
	 */
	public OpiInt(Context con, String name, int i) {
		super(con, name);
		context.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(i)));
	}
}
