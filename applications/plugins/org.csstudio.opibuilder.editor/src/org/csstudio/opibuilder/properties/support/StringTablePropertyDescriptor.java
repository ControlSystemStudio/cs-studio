/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties.support;

import java.util.Arrays;

import org.csstudio.opibuilder.properties.StringTableProperty.TitlesProvider;
import org.csstudio.opibuilder.visualparts.StringTableCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Descriptor for a property that has a value which should be edited with a String Table
 * cell editor.
 * 
 * @author Xihui Chen
 * 
 */
public final class StringTablePropertyDescriptor extends TextPropertyDescriptor {
	
	private String displayName;
	private TitlesProvider columnTitles;
	/**
	 * Standard constructor.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public StringTablePropertyDescriptor(final Object id, final String displayName, final TitlesProvider tilesProvider) {
		super(id, displayName);
		this.displayName = displayName;
		this.columnTitles = tilesProvider;
		setLabelProvider(new LabelProvider(){
			@Override
			public String getText(Object element) {
				if(element == null)
					return ""; //$NON-NLS-1$
				else if(!(element instanceof String[][]))
					return element.toString();
				String[][] stringTable = (String[][])element;
				if(stringTable.length >0)
					return Arrays.toString(stringTable[0]) +
							(stringTable.length > 1? "..." : ""); //$NON-NLS-1$ //$NON-NLS-2$
				return ""; //$NON-NLS-1$
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new StringTableCellEditor(parent, "Edit " + displayName, columnTitles);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}	

}
