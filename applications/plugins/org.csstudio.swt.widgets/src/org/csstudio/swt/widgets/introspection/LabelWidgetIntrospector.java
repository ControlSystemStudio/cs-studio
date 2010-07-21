package org.csstudio.swt.widgets.introspection;

import org.eclipse.draw2d.Label;

/**The introspector for widget inherited from {@link Label}.
 * @author Xihui Chen
 *
 */
public class LabelWidgetIntrospector extends DefaultWidgetIntrospector {
	public static String[] LABEL_WIDGET_NON_PROPERTIES = new String[]{
		"icon",
		"iconAlignment",
		"iconTextGap",
		"labelAlignment",
		"textAlignment",
		"textPlacement"
	};
	@Override
	public String[] getNonProperties() {
	
		return concatenateStringArrays(super.getNonProperties(), LABEL_WIDGET_NON_PROPERTIES);
	}
	
}
