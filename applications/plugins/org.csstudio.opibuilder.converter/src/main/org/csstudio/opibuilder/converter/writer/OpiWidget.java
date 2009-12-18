package org.csstudio.opibuilder.converter.writer;

import org.w3c.dom.Element;

/**
 * General class for outputting widgets.
 * @author Matevz
 */
public class OpiWidget {

	protected Context context;

	/**
	 * Creates element:
	 * 		<widget typeId="org.csstudio.opibuilder.widgets.type">
	 * 		</widget>
	 */
	public OpiWidget(Context con) {

		Element element = con.getDocument().createElement("widget");
		con.getElement().appendChild(element);

		// Move context to this object. 
		this.context = new Context(con.getDocument(), element, con.getX(), con.getY());
	}

	/**
	 * Sets the attribute typeId of the OPI widget with 'org.csstudio.opibuilder.widgets.' prefix.  
	 * @param typeId
	 */
	protected void setTypeId(String typeId) {
		context.getElement().setAttribute("typeId", "org.csstudio.opibuilder.widgets." + typeId);
	}
}
