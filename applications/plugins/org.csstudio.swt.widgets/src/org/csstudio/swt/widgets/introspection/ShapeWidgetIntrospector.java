/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
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
