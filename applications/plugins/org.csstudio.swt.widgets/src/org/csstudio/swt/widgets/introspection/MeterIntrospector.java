package org.csstudio.swt.widgets.introspection;

import org.csstudio.swt.widgets.figures.AbstractScaledWidgetFigure;

/**The introspector for widget inherited from {@link AbstractScaledWidgetFigure}.
 * @author Xihui Chen
 *
 */
public class MeterIntrospector extends ScaleWidgetIntrospector {
	public static String[] METER_WIDGET_NON_PROPERTIES = new String[]{
		"transparent"
	};
	@Override
	public String[] getNonProperties() {
	
		return concatenateStringArrays(super.getNonProperties(), METER_WIDGET_NON_PROPERTIES);
	}
	
}
