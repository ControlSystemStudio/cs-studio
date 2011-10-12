/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.jdom.Element;

/**Version property.
 * @author Xihui Chen
 *
 */
public class VersionProperty extends UnchangableStringProperty{

	public VersionProperty(String prop_id, String description,
			WidgetPropertyCategory category, String defaultValue) {
		super(prop_id, description, category, defaultValue);
	}
	
	@Override
	public void writeToXML(Element propElement) {			
		setPropertyValue(OPIBuilderPlugin.getDefault().getBundle().getVersion().toString());
		super.writeToXML(propElement);
	}
	
	@Override
	public boolean configurableByRule() {
		return false;
	}	
		

}
