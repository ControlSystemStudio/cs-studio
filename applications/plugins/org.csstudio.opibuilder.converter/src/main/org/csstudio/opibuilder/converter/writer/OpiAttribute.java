package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Element;

/**
 * General OPI output class for EdmAttribute.
 * @author Matevz
*/
public class OpiAttribute {

	protected Context context;
	
	/**
	 * Appends an element with the given name to current element and
	 * sets the local context to this element.  
	 */
	public OpiAttribute(Context con, String name) {

		Element element = con.getDocument().createElement(name);
	    con.getElement().appendChild(element);
		
	    // Move context to this object. 
		this.context = new Context(con.getDocument(), element, con.getX(), con.getY());
	}
}
