/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.scriptUtil;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.util.WidgetDescriptor;
import org.csstudio.opibuilder.util.WidgetsService;
import org.eclipse.osgi.util.NLS;

/**The Utility Class to help managing widgets.
 * @author Xihui Chen
 *
 */
public class WidgetUtil {


	/**Create a new widget model with the give widget type ID.
	 * @param widgetTypeID type ID of the widget. 
	 * You can get the typeID of a widget by opening an OPI with this widget in text editor.
	 * @return the widget model.
	 * @throws Exception if the widget type ID does not exist.
	 */
	public static AbstractWidgetModel createWidgetModel(String widgetTypeID) throws Exception{
		WidgetDescriptor widgetDescriptor = 
			WidgetsService.getInstance().getWidgetDescriptor(widgetTypeID);
		if(widgetDescriptor != null)
			return widgetDescriptor.getWidgetModel();
		else
			throw new  RuntimeException(
					NLS.bind("The widget type ID: {0} does not exist!", widgetTypeID));
	}
	
	
	
	
}
