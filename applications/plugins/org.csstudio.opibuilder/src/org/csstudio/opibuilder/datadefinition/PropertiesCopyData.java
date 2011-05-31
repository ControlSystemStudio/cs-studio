/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.datadefinition;

import java.util.List;

import org.csstudio.opibuilder.model.AbstractWidgetModel;

/**The data for coping properties.
 * @author Xihui Chen
 *
 */
public class PropertiesCopyData {
	
	private AbstractWidgetModel widgetModel;
	
	private List<String> propIDList;

	public PropertiesCopyData(AbstractWidgetModel widgetModel,
			List<String> propIDList) {
		this.widgetModel = widgetModel;
		this.propIDList = propIDList;
	}

	/**
	 * @return the widgetModel
	 */
	public final AbstractWidgetModel getWidgetModel() {
		return widgetModel;
	}

	/**
	 * @param widgetModel the widgetModel to set
	 */
	public final void setWidgetModel(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
	}

	/**
	 * @return the propIDList
	 */
	public final List<String> getPropIDList() {
		return propIDList;
	}

	/**
	 * @param propIDList the propIDList to set
	 */
	public final void setPropIDList(List<String> propIDList) {
		this.propIDList = propIDList;
	}
	
	
	
	
}
