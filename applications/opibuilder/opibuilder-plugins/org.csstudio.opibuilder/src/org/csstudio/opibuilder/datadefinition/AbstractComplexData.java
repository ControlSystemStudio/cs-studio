/*******************************************************************************
 * Copyright (c) 2012 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.datadefinition;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.AbstractWidgetProperty;
import org.eclipse.core.runtime.Assert;

/**The abstract data that holds multiple properties.
 * @author Xihui
 *
 */
public abstract class AbstractComplexData {
	private Map<String, AbstractWidgetProperty> propertyMap;	
	
	private AbstractWidgetModel widgetModel;
	
	public AbstractComplexData(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		propertyMap = new LinkedHashMap<String, AbstractWidgetProperty>();
		configureProperties();
		

	}
	
	

	/**Add a property to the widget.
	 * @param property the property to be added.
	 */
	public void addProperty(final AbstractWidgetProperty property){
		Assert.isNotNull(property);
		property.setWidgetModel(getWidgetModel());
		propertyMap.put(property.getPropertyID(), property);				
	}
	
	/**
	 * The place to add properties.
	 */
	protected abstract void configureProperties();
	
	public AbstractWidgetProperty[] getAllProperties(){
		AbstractWidgetProperty[] propArray = new AbstractWidgetProperty[propertyMap.size()];
		int i=0;
		for(AbstractWidgetProperty p : propertyMap.values())
			propArray[i++] = p;
		return propArray;
	}
	
	public Set<String> getAllPropertyIDs() {
		return propertyMap.keySet();
	}
	
	public AbstractComplexData getCopy() {
		AbstractComplexData copy = createInstance();
		for(String id : propertyMap.keySet()){
			copy.setPropertyValue(id, getPropertyValue(id));
		}
		copy.setWidgetModel(getWidgetModel());
		return copy;
	}

	public AbstractWidgetProperty getProperty(String propId) {
		return propertyMap.get(propId);
	}

	public Object getPropertyValue(Object id) {
		Assert.isTrue(propertyMap.containsKey(id));
		return propertyMap.get(id).getPropertyValue();
	}

	/**
	 * @return the widgetModel
	 */
	public AbstractWidgetModel getWidgetModel() {
		return widgetModel;
	}

	public void setPropertyValue(Object id, Object value) {
		Assert.isTrue(propertyMap.containsKey(id));
		propertyMap.get(id).setPropertyValue(value);
	}

	/**
	 * @param widgetModel the widgetModel to set
	 */
	public void setWidgetModel(AbstractWidgetModel widgetModel) {
		this.widgetModel = widgetModel;
		for(AbstractWidgetProperty property : getAllProperties())
			property.setWidgetModel(widgetModel);
	}

	/**
	 * @return a new instance of this data.
	 */
	public abstract AbstractComplexData createInstance();

	@Override
	public boolean equals(Object obj) {
		if(obj == null || !(obj instanceof AbstractComplexData))
			return false;
		AbstractComplexData objData = ((AbstractComplexData)obj);
		for(AbstractWidgetProperty property : getAllProperties()){
			if(objData.getProperty(property.getPropertyID()) == null)
				return false;
			if(!(property.getPropertyValue().equals(
					objData.getPropertyValue(property.getPropertyID()))))
				return false;
		}
		return true;
	}
}
