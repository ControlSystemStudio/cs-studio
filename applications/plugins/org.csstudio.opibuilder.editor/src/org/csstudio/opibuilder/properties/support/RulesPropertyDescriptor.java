/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.RulesProperty;
import org.csstudio.opibuilder.visualparts.RulesInputCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**The property descriptor for {@link RulesProperty}.
 * @author Xihui Chen
 *
 */
public class RulesPropertyDescriptor extends TextPropertyDescriptor {
	
	private AbstractWidgetModel widgetModel;
	
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public RulesPropertyDescriptor(final Object id, final AbstractWidgetModel widgetModel, final String displayName) {
		super(id, displayName);
		this.widgetModel = widgetModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new RulesInputCellEditor(parent, widgetModel, "Attach Rules");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
}
