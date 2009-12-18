package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmDouble;

/**
 * XML output class for EdmDouble type.
 * @author Matevz
 */
public class OpiDouble extends OpiAttribute {

	/**
	 * Creates an element <name>doubleValue</name> with the given EdmDouble value.
	 */
	public OpiDouble(Context con, String name, EdmDouble d) {
		this(con, name, d.get());
	}

	/**
	 * Creates an element <name>doubleValue</name> with the given double value.
	 */
	public OpiDouble(Context con, String name, double d) {
		super(con, name);
		context.getElement().appendChild(con.getDocument().createTextNode(String.valueOf(d)));
	}

}
