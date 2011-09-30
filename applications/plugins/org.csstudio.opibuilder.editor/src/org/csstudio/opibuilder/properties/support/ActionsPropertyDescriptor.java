/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties.support;


import org.csstudio.opibuilder.visualparts.ActionsCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**The property descriptor for actions property.
 * @author Xihui Chen
 *
 */
public class ActionsPropertyDescriptor extends TextPropertyDescriptor {
	
	private boolean showHookOption;
	
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public ActionsPropertyDescriptor(final Object id, 
			final String displayName, final boolean showHookOption) {
		super(id, displayName);
		this.showHookOption = showHookOption;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ActionsCellEditor(parent, "Set Actions", showHookOption);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
}
