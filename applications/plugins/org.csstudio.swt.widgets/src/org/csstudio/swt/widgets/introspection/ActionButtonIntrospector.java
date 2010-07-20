package org.csstudio.swt.widgets.introspection;

import org.csstudio.swt.widgets.figures.AbstractScaledWidgetFigure;

/**The introspector for widget inherited from {@link AbstractScaledWidgetFigure}.
 * @author Xihui Chen
 *
 */
public class ActionButtonIntrospector extends DefaultWidgetIntrospector {
	public static String[] NON_PROPERTIES = new String[]{
		"armed",
		"toggled",
		"mousePressed",
		"opaque",
		"selected"
	};
	@Override
	public String[] getNonProperties() {
	
		return concatenateStringArrays(super.getNonProperties(), NON_PROPERTIES);
	}
	
}
