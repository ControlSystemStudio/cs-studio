/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.util.OPIColor;
import org.csstudio.opibuilder.visualparts.OPIColorCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**The property descriptor for OPI Color.
 * @author Xihui Chen
 *
 */
public class OPIColorPropertyDescriptor extends PropertyDescriptor {

	public OPIColorPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		setLabelProvider(new OPIColorLabelProvider());
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		OPIColorCellEditor editor = new OPIColorCellEditor(parent, "Choose Color");
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	
	private final static class OPIColorLabelProvider extends LabelProvider{
		
		@Override
		public Image getImage(Object element) {
			if(element !=null && element instanceof OPIColor){
				return ((OPIColor)element).getImage();
			}
			return null;
		}		
	}
	
	
}
