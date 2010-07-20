package org.csstudio.swt.widgets.introspection;

import org.eclipse.draw2d.AbstractPointListShape;

/**The introspector for widget inherited from {@link AbstractPointListShape}.
 * @author Xihui Chen
 *
 */
public class PolyWidgetIntrospector extends ShapeWidgetIntrospector {
	public static String[] POLY_WIDGET_NON_PROPERTIES = new String[]{
		"start",
		"end"		
	};
	@Override
	public String[] getNonProperties() {
	
		return concatenateStringArrays(super.getNonProperties(), POLY_WIDGET_NON_PROPERTIES);
	}
	
}
