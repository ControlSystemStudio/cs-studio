/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.StringTableProperty.TitlesProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Implementation of PropertySSHelper
 * @author Xihui Chen
 *
 */
public class PropertySSHelperImpl extends PropertySSHelper {

	@Override
	public PropertyDescriptor getActionsPropertyDescriptor(String prop_id,
			String description, boolean showHookOption) {
		return new ActionsPropertyDescriptor(prop_id, description, showHookOption);
	}

	@Override
	public PropertyDescriptor getBooleanPropertyDescriptor(String prop_id,
			String description) {
		return new BooleanPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getOPIColorPropertyDescriptor(String prop_id,
			String description) {
		return new OPIColorPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getDoublePropertyDescriptor(String prop_id,
			String description) {
		return new DoublePropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getFilePathPropertyDescriptor(String prop_id,
			String description, AbstractWidgetModel widgetModel,
			String[] fileExtensions) {
		return new FilePathPropertyDescriptor(
				prop_id, description, widgetModel, fileExtensions);
	}

	@Override
	public PropertyDescriptor getOPIFontPropertyDescriptor(String prop_id,
			String description) {
		return new OPIFontPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getIntegerPropertyDescriptor(String prop_id,
			String description) {
		return new IntegerPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getMacrosPropertyDescriptor(String prop_id,
			String description) {
		return new MacrosPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getPointlistPropertyDescriptor(String prop_id,
			String description) {
		return new PointlistPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getRulesPropertyDescriptor(String prop_id,
			AbstractWidgetModel widgetModel, String description) {
		return new RulesPropertyDescriptor(prop_id, widgetModel, description);
	}

	@Override
	public PropertyDescriptor getScriptPropertyDescriptor(String prop_id,
			AbstractWidgetModel widgetModel, String description) {
		return new ScriptPropertyDescriptor(prop_id, widgetModel, description);
	}

	@Override
	public PropertyDescriptor getStringListPropertyDescriptor(String prop_id,
			String description) {
		return new StringListPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getMultiLineTextPropertyDescriptor(
			String prop_id, String description) {
		return new MultiLineTextPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getTextPropertyDescriptor(String prop_id,
			String description) {
		return new TextPropertyDescriptor(prop_id, description);
	}

	@Override
	public PropertyDescriptor getStringTablePropertyDescriptor(String prop_id,
			String description, TitlesProvider tilesProvider) {
		return new StringTablePropertyDescriptor(prop_id, description, tilesProvider);
	}

	@Override
	public PropertyDescriptor getComplexDataPropertyDescriptor(String prop_id,
			String description, String dialogTitle) {
		return new ComplexDataPropertyDescriptor(prop_id, description, dialogTitle);
	}

	@Override
	public PropertyDescriptor FilePathPropertyDescriptorWithFilter(String prop_id,
			String description, AbstractWidgetModel widgetModel, String[] filters) {
		return new FilePathPropertyDescriptorWithFilter(prop_id, description, widgetModel, filters);
	}

	@Override
	public PropertyDescriptor getMatrixPropertyDescriptor(String prop_id,
			String description) {
		// TODO Auto-generated method stub
		return null;
	}

	

}
