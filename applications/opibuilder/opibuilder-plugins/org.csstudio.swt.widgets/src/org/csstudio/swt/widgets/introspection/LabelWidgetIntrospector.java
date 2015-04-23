/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
