package org.csstudio.opibuilder.converter.writer;

import org.csstudio.opibuilder.converter.model.EdmPointsList;
import org.w3c.dom.Element;


/**
 * XML output class for OpiPointsList type.
 * @author Xihui Chen
 */
public class OpiPointsList extends OpiAttribute {

	/**
	 * Creates an element <name>intValue</name> with the given EdmInt value.
	 */
	public OpiPointsList(Context con, String name, EdmPointsList xPoints, EdmPointsList yPoints) {
		this(con, name, xPoints.get(), yPoints.get());
	}
	
	/**
	 * Creates an element <name>intValue</name> with the given int value.
	 */
	public OpiPointsList(Context con, String name, int[] x, int[] y) {
		super(con, name);
		
		for(int i=0; i<Math.min(x.length, y.length); i++){
			Element pointElement = context.getDocument().createElement("point");
			pointElement.setAttribute("x", ""+(x[i]-context.getX()));
			pointElement.setAttribute("y", ""+(y[i]-context.getY()));
			context.getElement().appendChild(pointElement);			
		}
	}
}
