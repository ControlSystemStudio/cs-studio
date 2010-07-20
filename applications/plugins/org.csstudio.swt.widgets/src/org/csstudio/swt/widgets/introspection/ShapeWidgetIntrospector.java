package org.csstudio.swt.widgets.introspection;

import org.csstudio.swt.widgets.figures.AbstractScaledWidgetFigure;

/**The introspector for widget inherited from {@link AbstractScaledWidgetFigure}.
 * @author Xihui Chen
 *
 */
public class ShapeWidgetIntrospector extends DefaultWidgetIntrospector {
	public static String[] SHAPE_WIDGET_NON_PROPERTIES = new String[]{
		"antialias",
		"lineAttributes",
		"lineDash",
		"lineDashOffset",
		"lineJoin",
		"lineMiterLimit",
		"lineWidthFloat"
	};
	@Override
	public String[] getNonProperties() {
	
		return concatenateStringArrays(super.getNonProperties(), SHAPE_WIDGET_NON_PROPERTIES);
	}
	
}
