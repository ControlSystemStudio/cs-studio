package org.csstudio.swt.widgets.introspection;

import org.csstudio.swt.widgets.figures.AbstractScaledWidgetFigure;

/**The introspector for widget inherited from {@link AbstractScaledWidgetFigure}.
 * @author Xihui Chen
 *
 */
public class ScaleWidgetIntrospector extends DefaultWidgetIntrospector {
	public static String[] SCALE_WIDGET_NON_PROPERTIES = new String[]{
		"scale",
		"opaque"
	};
	@Override
	public String[] getNonProperties() {
	
		return concatenateStringArrays(super.getNonProperties(), SCALE_WIDGET_NON_PROPERTIES);
	}
	
}
