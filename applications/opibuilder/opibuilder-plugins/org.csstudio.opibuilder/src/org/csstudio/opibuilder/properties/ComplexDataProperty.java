/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.datadefinition.AbstractComplexData;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.support.PropertySSHelper;
import org.csstudio.opibuilder.util.ConsoleService;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;

/**
 * A property that can hold data with multiple properties.
 * 
 * @author Xihui Chen
 * 
 */
public class ComplexDataProperty extends AbstractWidgetProperty {

	public static final String XML_ATTRIBUTE_TYPE = "type"; //$NON-NLS-1$
	
	private String dialogTitle;

	/**
	 * Widget Property Constructor
	 * 
	 * @param prop_id
	 *            the property id which should be unique in a widget model.
	 * @param description
	 *            the description of the property, which will be shown as the
	 *            property name in property sheet.
	 * @param category
	 *            the category of the widget.
	 * @param defaultData
	 *            default value. It cannot be null.
	 * @param dialogTitle 
	 * 			  title of the dialog for editing the complex data.
	 */
	public ComplexDataProperty(String prop_id, String description,
			WidgetPropertyCategory category,
			AbstractComplexData defaultData, String dialogTitle) {
		super(prop_id, description, category, defaultData);
		this.dialogTitle = dialogTitle;
	}

	@Override
	public Object checkValue(Object value) {
		if (value == null)
			return null;
		AbstractComplexData acceptableValue = null;
		if (value instanceof AbstractComplexData) {
			((AbstractComplexData) value).setWidgetModel(widgetModel);
			acceptableValue = (AbstractComplexData) value;
		}

		return acceptableValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		if (PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().getComplexDataPropertyDescriptor(prop_id,
						description, dialogTitle);
	}

	@Override
	public AbstractComplexData readValueFromXML(Element propElement) {
		AbstractComplexData result = ((AbstractComplexData) getDefaultValue())
				.createInstance();

		List<?> children = propElement.getChildren();
		Iterator<?> iterator = children.iterator();
		Set<String> propIdSet = result.getAllPropertyIDs();
		while (iterator.hasNext()) {
			Element subElement = (Element) iterator.next();
			// handle property
			if (propIdSet.contains(subElement.getName())) {
				String propId = subElement.getName();
				try {
					result.setPropertyValue(propId, result.getProperty(propId)
							.readValueFromXML(subElement));
				} catch (Exception e) {
					String errorMessage = "Failed to read the "
							+ propId
							+ " sub property for "
							+ getPropertyID()
							+ ". "
							+ "The default property value will be setted instead. \n"
							+ e;
					OPIBuilderPlugin.getLogger().log(Level.WARNING,
							errorMessage, e);
					ConsoleService.getInstance().writeWarning(errorMessage);
				}
			}
		}

		return result;
	}

	@Override
	public void writeToXML(Element propElement) {
		AbstractComplexData data = (AbstractComplexData) getPropertyValue();
		for (AbstractWidgetProperty property : data.getAllProperties()) {
			Element propEle = new Element(property.getPropertyID());
			property.writeToXML(propEle);
			propElement.addContent(propEle);
		}

	}

	@Override
	public void setWidgetModel(AbstractWidgetModel widgetModel) {
		super.setWidgetModel(widgetModel);
		((AbstractComplexData) getPropertyValue())
				.setWidgetModel(widgetModel);
	}

}
