/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.visualparts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

/**The cellEditor for macros property descriptor.
 * @author Xihui Chen
 *
 */
public class StringListCellEditor extends AbstractDialogCellEditor {
	
	private List<String> data;

	public StringListCellEditor(Composite parent, String title) {
		super(parent, title);
	}

	@Override
	protected void openDialog(Shell parentShell, String dialogTitle) {
			
		StringListEditDialog dialog = 
			new StringListEditDialog(parentShell, data, dialogTitle);
		if(dialog.open() == Window.OK){
			data = dialog.getResult();			
		}
	}

	@Override
	protected boolean shouldFireChanges() {
		return data != null;
	}

	@Override
	protected Object doGetValue() {
		return data;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doSetValue(Object value) {
		if(value == null || !(value instanceof List))
			data = new ArrayList<String>();
		else
			data = (List<String>)value;
			
	}

}
